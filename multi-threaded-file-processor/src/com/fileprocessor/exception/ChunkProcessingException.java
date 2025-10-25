package com.fileprocessor.exception;

public class ChunkProcessingException extends RuntimeException {
    private final String chunkId;
    
    public ChunkProcessingException(String chunkId, String message) {
        super("Chunk " + chunkId + ": " + message);
        this.chunkId = chunkId;
    }
    
    public ChunkProcessingException(String chunkId, String message, Throwable cause) {
        super("Chunk " + chunkId + ": " + message, cause);
        this.chunkId = chunkId;
    }
    
    public String getChunkId() { return chunkId; }
}
