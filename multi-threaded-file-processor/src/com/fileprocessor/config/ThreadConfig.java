package com.fileprocessor.config;

public class ThreadConfig {
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime = 60L; // seconds
    private int queueCapacity = 100;
    
    public ThreadConfig(int corePoolSize, int maxPoolSize) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }
    
    // Getters and Setters
    public int getCorePoolSize() { return corePoolSize; }
    public void setCorePoolSize(int corePoolSize) { 
        this.corePoolSize = corePoolSize; 
    }
    
    public int getMaxPoolSize() { return maxPoolSize; }
    public void setMaxPoolSize(int maxPoolSize) { 
        this.maxPoolSize = maxPoolSize; 
    }
    
    public long getKeepAliveTime() { return keepAliveTime; }
    public void setKeepAliveTime(long keepAliveTime) { 
        this.keepAliveTime = keepAliveTime; 
    }
    
    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { 
        this.queueCapacity = queueCapacity; 
    }
}
