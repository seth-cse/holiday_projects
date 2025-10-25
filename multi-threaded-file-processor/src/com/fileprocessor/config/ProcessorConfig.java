package com.fileprocessor.config;

public class ProcessorConfig {
    private int chunkSize = 1000; // lines per chunk
    private int threadPoolSize = 4;
    private String inputDirectory = "input";
    private String outputDirectory = "output";
    private String filePattern = ".*\\.(txt|csv|json)$";
    private boolean removeDuplicates = true;
    private boolean sortOutput = true;
    
    // Constructors
    public ProcessorConfig() {}
    
    public ProcessorConfig(int chunkSize, int threadPoolSize, 
                          String inputDir, String outputDir) {
        this.chunkSize = chunkSize;
        this.threadPoolSize = threadPoolSize;
        this.inputDirectory = inputDir;
        this.outputDirectory = outputDir;
    }
    
    // Getters and Setters
    public int getChunkSize() { return chunkSize; }
    public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }
    
    public int getThreadPoolSize() { return threadPoolSize; }
    public void setThreadPoolSize(int threadPoolSize) { 
        this.threadPoolSize = threadPoolSize; 
    }
    
    public String getInputDirectory() { return inputDirectory; }
    public void setInputDirectory(String inputDirectory) { 
        this.inputDirectory = inputDirectory; 
    }
    
    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { 
        this.outputDirectory = outputDirectory; 
    }
    
    public String getFilePattern() { return filePattern; }
    public void setFilePattern(String filePattern) { 
        this.filePattern = filePattern; 
    }
    
    public boolean isRemoveDuplicates() { return removeDuplicates; }
    public void setRemoveDuplicates(boolean removeDuplicates) { 
        this.removeDuplicates = removeDuplicates; 
    }
    
    public boolean isSortOutput() { return sortOutput; }
    public void setSortOutput(boolean sortOutput) { 
        this.sortOutput = sortOutput; 
    }
}
