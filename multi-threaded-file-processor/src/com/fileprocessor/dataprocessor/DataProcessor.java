// dataprocessor/DataProcessor.java
package com.fileprocessor.dataprocessor;

import com.fileprocessor.filereader.Chunk;
import com.fileprocessor.exception.ChunkProcessingException;
import com.fileprocessor.model.ProcessedData;
import com.fileprocessor.model.ProcessingStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DataProcessor implements Callable<List<ProcessedData>> {
    private final Chunk chunk;
    private final Transformation transformation;
    private final ProcessingStats stats;

    public DataProcessor(Chunk chunk, Transformation transformation, ProcessingStats stats) {
        this.chunk = chunk;
        this.transformation = transformation;
        this.stats = stats;
    }

    @Override
    public List<ProcessedData> call() throws Exception {
        System.out.println(Thread.currentThread().getName() + " processing " + chunk.getChunkId());
        
        try {
            List<ProcessedData> processedResults = processChunk(chunk);
            validateResults(processedResults);
            
            stats.incrementChunksProcessed();
            stats.addRecords(processedResults.size());
            
            return processedResults;
            
        } catch (Exception e) {
            stats.incrementErrorCount();
            throw new ChunkProcessingException(chunk.getChunkId(), 
                "Failed to process chunk", e);
        }
    }

    /**
     * Process a single chunk of data
     */
    public List<ProcessedData> processChunk(Chunk chunk) {
        List<ProcessedData> results = new ArrayList<>();
        int lineNumber = chunk.getStartLine();
        
        for (String line : chunk.getLines()) {
            try {
                if (line != null && !line.trim().isEmpty()) {
                    ProcessedData processed = transformData(line, lineNumber, chunk.getSourceFile());
                    results.add(processed);
                }
                lineNumber++;
            } catch (Exception e) {
                System.err.println("Error processing line " + lineNumber + " in " + 
                                 chunk.getChunkId() + ": " + e.getMessage());
                // Continue processing other lines in the chunk
            }
        }
        
        return results;
    }

    /**
     * Transform individual data line
     */
    private ProcessedData transformData(String line, int lineNumber, String sourceFile) {
        String transformedContent = transformation.apply(line, lineNumber, sourceFile);
        
        // Generate unique ID for the processed data
        String id = generateDataId(sourceFile, lineNumber, line);
        
        return new ProcessedData(id, transformedContent, System.currentTimeMillis());
    }

    /**
     * Generate unique ID for processed data
     */
    private String generateDataId(String sourceFile, int lineNumber, String line) {
        String contentHash = Integer.toHexString(line.hashCode());
        return String.format("%s:%d:%s", sourceFile, lineNumber, contentHash);
    }

    /**
     * Validate processing results
     */
    public void validateResults(List<ProcessedData> results) {
        if (results == null) {
            throw new ChunkProcessingException(chunk.getChunkId(), 
                "Processing results cannot be null");
        }

        for (ProcessedData data : results) {
            if (data.getId() == null || data.getId().trim().isEmpty()) {
                throw new ChunkProcessingException(chunk.getChunkId(),
                    "Processed data must have a valid ID");
            }
            
            if (data.getContent() == null) {
                throw new ChunkProcessingException(chunk.getChunkId(),
                    "Processed data content cannot be null");
            }
        }
    }

    public Chunk getChunk() {
        return chunk;
    }
}
