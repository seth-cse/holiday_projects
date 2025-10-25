package com.fileprocessor.aggregator;

import com.fileprocessor.model.ProcessedData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateRemover {
    
    /**
     * Remove duplicates using multiple strategies
     */
    public List<ProcessedData> removeDuplicates(List<ProcessedData> data, RemovalStrategy strategy) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        switch (strategy) {
            case ID_BASED:
                return removeDuplicatesById(data);
            case CONTENT_BASED:
                return removeDuplicatesByContent(data);
            case COMPLETE_MATCH:
                return removeDuplicatesByCompleteMatch(data);
            default:
                return removeDuplicatesById(data);
        }
    }

    /**
     * Remove duplicates based on ID only
     */
    private List<ProcessedData> removeDuplicatesById(List<ProcessedData> data) {
        Set<String> seenIds = new HashSet<>();
        List<ProcessedData> uniqueList = new ArrayList<>();
        
        for (ProcessedData item : data) {
            if (seenIds.add(item.getId())) {
                uniqueList.add(item);
            }
        }
        
        return uniqueList;
    }

    /**
     * Remove duplicates based on content only
     */
    private List<ProcessedData> removeDuplicatesByContent(List<ProcessedData> data) {
        Set<String> seenContent = new HashSet<>();
        List<ProcessedData> uniqueList = new ArrayList<>();
        
        for (ProcessedData item : data) {
            if (seenContent.add(item.getContent())) {
                uniqueList.add(item);
            }
        }
        
        return uniqueList;
    }

    /**
     * Remove duplicates based on both ID and content
     */
    private List<ProcessedData> removeDuplicatesByCompleteMatch(List<ProcessedData> data) {
        Set<String> seenCombinations = new HashSet<>();
        List<ProcessedData> uniqueList = new ArrayList<>();
        
        for (ProcessedData item : data) {
            String combination = item.getId() + "|||" + item.getContent();
            if (seenCombinations.add(combination)) {
                uniqueList.add(item);
            }
        }
        
        return uniqueList;
    }

    public enum RemovalStrategy {
        ID_BASED,       // Remove duplicates based on ID only
        CONTENT_BASED,  // Remove duplicates based on content only  
        COMPLETE_MATCH  // Remove duplicates based on both ID and content
    }
}
