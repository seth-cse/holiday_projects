// Main.java
package com.fileprocessor;

import com.fileprocessor.config.ProcessorConfig;
import com.fileprocessor.manager.ProcessingPipeline;

public class Main {
    public static void main(String[] args) {
        System.out.println("Multi-threaded File Processor Starting...");
        
        try {
            // Create configuration
            ProcessorConfig config = createConfig(args);
            
            // Validate input directory exists
            java.io.File inputDir = new java.io.File(config.getInputDirectory());
            if (!inputDir.exists() || !inputDir.isDirectory()) {
                System.err.println("Error: Input directory does not exist: " + config.getInputDirectory());
                System.exit(1);
            }
            
            // Create output directory if it doesn't exist
            java.io.File outputDir = new java.io.File(config.getOutputDirectory());
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Create and execute processing pipeline
            ProcessingPipeline pipeline = new ProcessingPipeline(config);
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down gracefully...");
                pipeline.shutdown();
            }));
            
            // Execute the pipeline
            pipeline.execute();
            
            // Print summary
            printSummary(pipeline);
            
        } catch (Exception e) {
            System.err.println("Processing failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("Processing completed successfully!");
    }

    private static ProcessorConfig createConfig(String[] args) {
        ProcessorConfig config = new ProcessorConfig();
        
        // Parse command line arguments
        if (args.length > 0) {
            config.setInputDirectory(args[0]);
        } else {
            config.setInputDirectory("input");
        }
        
        if (args.length > 1) {
            config.setOutputDirectory(args[1]);
        } else {
            config.setOutputDirectory("output");
        }
        
        if (args.length > 2) {
            try {
                config.setThreadPoolSize(Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                System.err.println("Invalid thread pool size, using default: " + config.getThreadPoolSize());
            }
        }
        
        if (args.length > 3) {
            try {
                config.setChunkSize(Integer.parseInt(args[3]));
            } catch (NumberFormatException e) {
                System.err.println("Invalid chunk size, using default: " + config.getChunkSize());
            }
        }
        
        // Print configuration
        System.out.println("Configuration:");
        System.out.println("  Input Directory: " + config.getInputDirectory());
        System.out.println("  Output Directory: " + config.getOutputDirectory());
        System.out.println("  Thread Pool Size: " + config.getThreadPoolSize());
        System.out.println("  Chunk Size: " + config.getChunkSize());
        System.out.println("  Remove Duplicates: " + config.isRemoveDuplicates());
        System.out.println("  Sort Output: " + config.isSortOutput());
        
        return config;
    }

    private static void printSummary(ProcessingPipeline pipeline) {
        var stats = pipeline.getStats();
        System.out.println("\n=== PROCESSING SUMMARY ===");
        System.out.println("Files processed: " + stats.getFilesProcessed());
        System.out.println("Chunks processed: " + stats.getChunksProcessed());
        System.out.println("Total records: " + stats.getTotalRecords());
        System.out.println("Errors: " + stats.getErrorCount());
        System.out.println("Processing time: " + stats.getProcessingTime() + "ms");
        
        var aggStats = pipeline.getResultAggregator().getAggregationStats();
        System.out.println("Aggregation Stats: " + aggStats.toString());
    }
}
