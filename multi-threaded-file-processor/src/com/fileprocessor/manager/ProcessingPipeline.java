// manager/ProcessingPipeline.java
package com.fileprocessor.manager;

import com.fileprocessor.aggregator.ResultAggregator;
import com.fileprocessor.config.ProcessorConfig;
import com.fileprocessor.config.ThreadConfig;
import com.fileprocessor.dataprocessor.DataProcessor;
import com.fileprocessor.dataprocessor.Transformation;
import com.fileprocessor.filereader.Chunk;
import com.fileprocessor.filereader.FileReader;
import com.fileprocessor.model.ProcessedData;
import com.fileprocessor.model.ProcessingStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessingPipeline {
    private final ProcessorConfig config;
    private final ThreadPoolManager threadPoolManager;
    private final FileReader fileReader;
    private final ProcessingStats stats;
    private final ResultAggregator resultAggregator;
    private final Transformation transformation;

    public ProcessingPipeline(ProcessorConfig config) {
        this.config = config;
        this.stats = new ProcessingStats();
        this.fileReader = new FileReader(config, stats);
        this.threadPoolManager = new ThreadPoolManager(
            new ThreadConfig(config.getThreadPoolSize(), config.getThreadPoolSize() * 2)
        );
        this.resultAggregator = new ResultAggregator(
            stats, config.isRemoveDuplicates(), config.isSortOutput()
        );
        this.transformation = Transformation.createDefaultTransformation();
    }

    /**
     * Execute the complete processing pipeline
     */
    public void execute() {
        try {
            System.out.println("Starting file processing pipeline...");
            
            // Step 1: Read files and split into chunks
            System.out.println("Step 1: Reading files from " + config.getInputDirectory());
            List<Chunk> chunks = fileReader.readFiles();
            System.out.println("Created " + chunks.size() + " chunks for processing");
            
            // Step 2: Process chunks in parallel
            System.out.println("Step 2: Processing chunks with " + 
                             config.getThreadPoolSize() + " threads");
            List<ProcessedData> processedData = processChunks(chunks);
            
            // Step 3: Aggregate results
            System.out.println("Step 3: Aggregating results");
            List<ProcessedData> finalResults = resultAggregator.getFinalResults();
            
            // Step 4: Write output (to be implemented in OutputWriter)
            System.out.println("Step 4: Writing " + finalResults.size() + " records to output");
            
            // Print processing summary
            printProcessingSummary(chunks.size(), finalResults.size());
            
        } finally {
            threadPoolManager.shutdown();
        }
    }

    /**
     * Process chunks using thread pool
     */
    private List<ProcessedData> processChunks(List<Chunk> chunks) {
        List<Future<List<ProcessedData>>> futures = new ArrayList<>();
        
        // Submit all chunks for processing
        for (Chunk chunk : chunks) {
            DataProcessor processor = new DataProcessor(chunk, transformation, stats);
            Future<List<ProcessedData>> future = threadPoolManager.getExecutorService().submit(processor);
            futures.add(future);
        }
        
        // Collect results as they complete
        List<ProcessedData> allResults = new ArrayList<>();
        int completed = 0;
        
        for (Future<List<ProcessedData>> future : futures) {
            try {
                List<ProcessedData> chunkResults = future.get(5, TimeUnit.MINUTES);
                resultAggregator.mergeResults(chunkResults);
                allResults.addAll(chunkResults);
                completed++;
                
                System.out.printf("Progress: %d/%d chunks completed%n", completed, chunks.size());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Processing interrupted");
                break;
            } catch (ExecutionException e) {
                System.err.println("Chunk processing failed: " + e.getCause().getMessage());
            } catch (TimeoutException e) {
                System.err.println("Chunk processing timed out");
                future.cancel(true);
            }
        }
        
        return allResults;
    }

    private void printProcessingSummary(int totalChunks, int totalRecords) {
        System.out.println("\n=== PROCESSING SUMMARY ===");
        System.out.println("Files processed: " + stats.getFilesProcessed());
        System.out.println("Chunks processed: " + stats.getChunksProcessed() + "/" + totalChunks);
        System.out.println("Total records: " + totalRecords);
        System.out.println("Errors: " + stats.getErrorCount());
        System.out.println("Processing time: " + stats.getProcessingTime() + "ms");
        
        var aggStats = resultAggregator.getAggregationStats();
        System.out.println("Aggregation: " + aggStats.toString());
        System.out.println("Thread pool stats: " + threadPoolManager.getPoolStats());
    }

    public ProcessingStats getStats() {
        return stats;
    }

    public ResultAggregator getResultAggregator() {
        return resultAggregator;
    }

    public void shutdown() {
        threadPoolManager.shutdown();
    }
}
