package com.fileprocessor.model;

import java.util.Objects;

public class ProcessedData implements Comparable<ProcessedData> {
    private final String id;
    private final String content;
    private final long timestamp;
    
    public ProcessedData(String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    // Getters, equals, hashCode, compareTo methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedData that = (ProcessedData) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public int compareTo(ProcessedData other) {
        return this.id.compareTo(other.id);
    }
    
    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
