// aggregator/DataSorter.java
package com.fileprocessor.aggregator;

import com.fileprocessor.model.ProcessedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataSorter {
    
    /**
     * Sort data using different strategies
     */
    public List<ProcessedData> sortData(List<ProcessedData> data, SortStrategy strategy) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        List<ProcessedData> sortedList = new ArrayList<>(data);
        
        switch (strategy) {
            case NATURAL_ORDER:
                Collections.sort(sortedList);
                break;
            case TIMESTAMP_ASC:
                sortedList.sort(Comparator.comparingLong(ProcessedData::getTimestamp));
                break;
            case TIMESTAMP_DESC:
                sortedList.sort(Comparator.comparingLong(ProcessedData::getTimestamp).reversed());
                break;
            case CONTENT_ASC:
                sortedList.sort(Comparator.comparing(ProcessedData::getContent));
                break;
            case CONTENT_DESC:
                sortedList.sort(Comparator.comparing(ProcessedData::getContent).reversed());
                break;
            default:
                Collections.sort(sortedList);
        }
        
        return sortedList;
    }

    /**
     * Sort with custom comparator
     */
    public List<ProcessedData> sortData(List<ProcessedData> data, Comparator<ProcessedData> comparator) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        List<ProcessedData> sortedList = new ArrayList<>(data);
        sortedList.sort(comparator);
        return sortedList;
    }

    public enum SortStrategy {
        NATURAL_ORDER,   // Use ProcessedData's natural ordering (by ID)
        TIMESTAMP_ASC,   // Sort by timestamp ascending
        TIMESTAMP_DESC,  // Sort by timestamp descending  
        CONTENT_ASC,     // Sort by content ascending
        CONTENT_DESC     // Sort by content descending
    }
}
