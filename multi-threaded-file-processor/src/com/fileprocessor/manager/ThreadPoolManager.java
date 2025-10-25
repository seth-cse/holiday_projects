package com.fileprocessor.manager;

import com.fileprocessor.config.ThreadConfig;
import com.fileprocessor.exception.FileProcessingException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolManager {
    private final ThreadConfig config;
    private ExecutorService executorService;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public ThreadPoolManager(ThreadConfig config) {
        this.config = config;
        initializeThreadPool();
    }

    private void initializeThreadPool() {
        this.executorService = new ThreadPoolExecutor(
            config.getCorePoolSize(),
            config.getMaxPoolSize(),
            config.getKeepAliveTime(),
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(config.getQueueCapacity()),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy() // handle queue overflow
        );
    }

    public ExecutorService getExecutorService() {
        if (isShutdown.get()) {
            throw new FileProcessingException("Thread pool has been shutdown");
        }
        return executorService;
    }

    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isShutdown() {
        return isShutdown.get();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return (ThreadPoolExecutor) executorService;
    }

    /**
     * Get current thread pool statistics
     */
    public String getPoolStats() {
        ThreadPoolExecutor executor = getThreadPoolExecutor();
        return String.format(
            "ThreadPool[Active: %d, Pool: %d, Core: %d, Max: %d, Queue: %d/%d]",
            executor.getActiveCount(),
            executor.getPoolSize(),
            executor.getCorePoolSize(),
            executor.getMaximumPoolSize(),
            executor.getQueue().size(),
            config.getQueueCapacity()
        );
    }
}
