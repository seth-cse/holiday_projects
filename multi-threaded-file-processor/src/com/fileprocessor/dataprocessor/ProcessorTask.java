package com.fileprocessor.dataprocessor;

import com.fileprocessor.filereader.Chunk;
import com.fileprocessor.model.ProcessedData;
import com.fileprocessor.model.ProcessingStats;

import java.util.List;
import java.util.concurrent.Future;

public class ProcessorTask {
    private final Chunk chunk;
    private final Future<List<ProcessedData>> future;
    private final long submitTime;
    private Long completionTime;

    public ProcessorTask(Chunk chunk, Future<List<ProcessedData>> future) {
        this.chunk = chunk;
        this.future = future;
        this.submitTime = System.currentTimeMillis();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public List<ProcessedData> getResult() throws Exception {
        List<ProcessedData> result = future.get();
        this.completionTime = System.currentTimeMillis();
        return result;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Future<List<ProcessedData>> getFuture() {
        return future;
    }

    public long getProcessingTime() {
        if (completionTime != null) {
            return completionTime - submitTime;
        }
        return System.currentTimeMillis() - submitTime;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public Long getCompletionTime() {
        return completionTime;
    }
}
