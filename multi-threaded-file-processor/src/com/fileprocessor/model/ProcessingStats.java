package com.fileprocessor.model;

import java.util.concurrent.atomic.AtomicLong;

public class ProcessingStats {
    private final AtomicLong filesProcessed = new AtomicLong(0);
    private final AtomicLong chunksProcessed = new AtomicLong(0);
    private final AtomicLong totalRecords = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private final long startTime;
    
    public ProcessingStats() {
        this.startTime = System.currentTimeMillis();
    }
    
    // Increment methods
    public void incrementFilesProcessed() { filesProcessed.incrementAndGet(); }
    public void incrementChunksProcessed() { chunksProcessed.incrementAndGet(); }
    public void addRecords(long count) { totalRecords.addAndGet(count); }
    public void incrementErrorCount() { errorCount.incrementAndGet(); }
    
    // Getters
    public long getFilesProcessed() { return filesProcessed.get(); }
    public long getChunksProcessed() { return chunksProcessed.get(); }
    public long getTotalRecords() { return totalRecords.get(); }
    public long getErrorCount() { return errorCount.get(); }
    public long getProcessingTime() { 
        return System.currentTimeMillis() - startTime; 
    }
}
