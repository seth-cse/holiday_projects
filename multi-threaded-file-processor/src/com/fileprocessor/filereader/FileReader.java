package com.fileprocessor.filereader;

import com.fileprocessor.config.ProcessorConfig;
import com.fileprocessor.exception.FileProcessingException;
import com.fileprocessor.model.ProcessingStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FileReader {
    private final ProcessorConfig config;
    private final FileValidator fileValidator;
    private final ProcessingStats stats;
    private final AtomicInteger chunkCounter = new AtomicInteger(0);

    public FileReader(ProcessorConfig config, ProcessingStats stats) {
        this.config = config;
        this.stats = stats;
        this.fileValidator = new FileValidator(config.getFilePattern());
    }

    /**
     * Read all files from input directory and split into processing tasks
     */
    public List<Chunk> readFiles() {
        File inputDir = new File(config.getInputDirectory());
        
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new FileProcessingException(
                "Input directory does not exist or is not a directory: " + config.getInputDirectory());
        }

        try {
            List<File> validFiles = Files.list(Paths.get(config.getInputDirectory()))
                    .map(Path::toFile)
                    .filter(fileValidator::isValidFile)
                    .collect(Collectors.toList());

            if (validFiles.isEmpty()) {
                throw new FileProcessingException(
                    "No valid files found in directory: " + config.getInputDirectory());
            }

            List<Chunk> allChunks = new ArrayList<>();
            for (File file : validFiles) {
                try {
                    List<Chunk> fileChunks = processFile(file);
                    allChunks.addAll(fileChunks);
                    stats.incrementFilesProcessed();
                } catch (IOException e) {
                    throw new FileProcessingException(
                        "Failed to process file: " + file.getName(), e);
                }
            }

            return allChunks;
        } catch (IOException e) {
            throw new FileProcessingException(
                "Failed to read input directory: " + config.getInputDirectory(), e);
        }
    }

    /**
     * Process a single file and split into chunks
     */
    private List<Chunk> processFile(File file) throws IOException {
        validateFormat(file);
        
        List<Chunk> chunks = new ArrayList<>();
        List<String> currentChunkLines = new ArrayList<>();
        int lineNumber = 0;
        int chunkStartLine = 1;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                currentChunkLines.add(line);

                if (currentChunkLines.size() >= config.getChunkSize()) {
                    Chunk chunk = createChunk(file, currentChunkLines, chunkStartLine, lineNumber);
                    chunks.add(chunk);
                    currentChunkLines.clear();
                    chunkStartLine = lineNumber + 1;
                }
            }

            // Add remaining lines as the last chunk
            if (!currentChunkLines.isEmpty()) {
                Chunk chunk = createChunk(file, currentChunkLines, chunkStartLine, lineNumber);
                chunks.add(chunk);
            }
        }

        return chunks;
    }

    /**
     * Create a chunk with unique ID and metadata
     */
    private Chunk createChunk(File file, List<String> lines, int startLine, int endLine) {
        String chunkId = String.format("chunk-%d-%d", 
            System.currentTimeMillis(), chunkCounter.incrementAndGet());
        
        return new Chunk(chunkId, file.getName(), lines, startLine, endLine);
    }

    /**
     * Split file into tasks based on chunk configuration
     */
    public List<Chunk> splitIntoTasks(File file) {
        try {
            return processFile(file);
        } catch (IOException e) {
            throw new FileProcessingException(
                "Failed to split file into tasks: " + file.getName(), e);
        }
    }

    /**
     * Validate file format and structure
     */
    public void validateFormat(File file) {
        fileValidator.validateFileFormat(file);
        
        // Additional format-specific validation
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".csv")) {
            validateCsvFormat(file);
        } else if (fileName.endsWith(".json")) {
            validateJsonFormat(file);
        }
        // For .txt files, we don't need specific format validation
    }

    /**
     * Basic CSV format validation
     */
    private void validateCsvFormat(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                throw new FileProcessingException("CSV file is empty: " + file.getName());
            }
            
            // Basic check: ensure there are commas (basic CSV structure)
            if (!firstLine.contains(",")) {
                System.err.println("Warning: CSV file " + file.getName() + 
                                 " may not have proper comma separation");
            }
            
        } catch (IOException e) {
            throw new FileProcessingException(
                "Failed to validate CSV format for file: " + file.getName(), e);
        }
    }

    /**
     * Basic JSON format validation
     */
    private void validateJsonFormat(File file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                throw new FileProcessingException("JSON file is empty: " + file.getName());
            }
            
            // Basic JSON structure check
            String trimmedFirst = firstLine.trim();
            if (!trimmedFirst.startsWith("{") && !trimmedFirst.startsWith("[")) {
                throw new FileProcessingException(
                    "Invalid JSON format: file should start with { or [: " + file.getName());
            }
            
        } catch (IOException e) {
            throw new FileProcessingException(
                "Failed to validate JSON format for file: " + file.getName(), e);
        }
    }

    /**
     * Get file size in human-readable format
     */
    public String getFileSize(File file) {
        long bytes = file.length();
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Estimate processing time based on file size and configuration
     */
    public long estimateProcessingTime(File file) {
        long fileSize = file.length();
        int chunks = (int) Math.ceil((double) fileSize / (config.getChunkSize() * 100)); // rough estimate
        return chunks * 100L / config.getThreadPoolSize(); // milliseconds estimate
    }
}
