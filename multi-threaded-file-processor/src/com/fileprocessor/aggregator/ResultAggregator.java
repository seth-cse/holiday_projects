package com.fileprocessor.aggregator;

import com.fileprocessor.model.ProcessedData;
import com.fileprocessor.model.ProcessingStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultAggregator {
    private final List<ProcessedData> allResults;
    private final ProcessingStats stats;
    private final boolean removeDuplicates;
    private final boolean sortData;
    private final AtomicInteger processedTasks;

    public ResultAggregator(ProcessingStats stats, boolean removeDuplicates, boolean sortData) {
        this.allResults = new CopyOnWriteArrayList<>();
        this.stats = stats;
        this.removeDuplicates = removeDuplicates;
        this.sortData = sortData;
        this.processedTasks = new AtomicInteger(0);
    }

    /**
     * Merge results from multiple processing tasks
     */
    public synchronized void mergeResults(List<ProcessedData> newResults) {
        if (newResults == null || newResults.isEmpty()) {
            return;
        }

        allResults.addAll(newResults);
        processedTasks.incrementAndGet();
        
        System.out.println("Aggregated " + newResults.size() + " records. " +
                         "Total: " + allResults.size() + " records from " + 
                         processedTasks.get() + " tasks");
    }

    /**
     * Remove duplicate entries based on data ID
     */
    public List<ProcessedData> removeDuplicates(List<ProcessedData> data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        ConcurrentHashMap<String, ProcessedData> uniqueMap = new ConcurrentHashMap<>();
        
        for (ProcessedData item : data) {
            uniqueMap.putIfAbsent(item.getId(), item);
        }

        List<ProcessedData> uniqueList = new ArrayList<>(uniqueMap.values());
        
        System.out.println("Removed " + (data.size() - uniqueList.size()) + " duplicates. " +
                         "Original: " + data.size() + ", Unique: " + uniqueList.size());
        
        return uniqueList;
    }

    /**
     * Sort data by natural ordering (based on ID)
     */
    public List<ProcessedData> sortData(List<ProcessedData> data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProcessedData> sortedList = new ArrayList<>(data);
        Collections.sort(sortedList);
        
        System.out.println("Sorted " + sortedList.size() + " records");
        
        return sortedList;
    }

    /**
     * Get final aggregated results after applying all processing
     */
    public List<ProcessedData> getFinalResults() {
        List<ProcessedData> finalResults = new ArrayList<>(allResults);
        
        if (removeDuplicates) {
            finalResults = removeDuplicates(finalResults);
        }
        
        if (sortData) {
            finalResults = sortData(finalResults);
        }
        
        return finalResults;
    }

    /**
     * Get aggregation statistics
     */
    public AggregationStats getAggregationStats() {
        return new AggregationStats(
            allResults.size(),
            processedTasks.get(),
            removeDuplicates ? removeDuplicates(allResults).size() : allResults.size()
        );
    }

    public int getTotalRecords() {
        return allResults.size();
    }

    public int getProcessedTasks() {
        return processedTasks.get();
    }

    public void clear() {
        allResults.clear();
        processedTasks.set(0);
    }

    /**
     * Inner class for aggregation statistics
     */
    public static class AggregationStats {
        private final int totalRecords;
        private final int processedTasks;
        private final int uniqueRecords;

        public AggregationStats(int totalRecords, int processedTasks, int uniqueRecords) {
            this.totalRecords = totalRecords;
            this.processedTasks = processedTasks;
            this.uniqueRecords = uniqueRecords;
        }

        public int getTotalRecords() { return totalRecords; }
        public int getProcessedTasks() { return processedTasks; }
        public int getUniqueRecords() { return uniqueRecords; }
        public int getDuplicateCount() { return totalRecords - uniqueRecords; }

        @Override
        public String toString() {
            return String.format(
                "AggregationStats{totalRecords=%d, processedTasks=%d, uniqueRecords=%d, duplicates=%d}",
                totalRecords, processedTasks, uniqueRecords, getDuplicateCount());
        }
    }
}
