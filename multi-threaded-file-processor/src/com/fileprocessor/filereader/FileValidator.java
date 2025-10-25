// filereader/FileValidator.java
package com.fileprocessor.filereader;

import java.io.File;
import java.util.regex.Pattern;

public class FileValidator {
    private final Pattern filePattern;
    
    public FileValidator(String pattern) {
        this.filePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }
    
    public boolean isValidFile(File file) {
        try {
            validateFileExists(file);
            validateFileReadable(file);
            validateFileNotEmpty(file);
            return filePattern.matcher(file.getName()).matches();
        } catch (Exception e) {
            System.err.println("File validation failed for " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    private void validateFileExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getPath());
        }
    }
    
    private void validateFileReadable(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + file.getPath());
        }
        
        if (!file.canRead()) {
            throw new IllegalArgumentException("Cannot read file: " + file.getPath());
        }
    }
    
    private void validateFileNotEmpty(File file) {
        if (file.length() == 0) {
            throw new IllegalArgumentException("File is empty: " + file.getPath());
        }
    }
    
    public void validateFileFormat(File file) {
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".csv") && 
            !fileName.endsWith(".json")) {
            throw new IllegalArgumentException(
                "Unsupported file format. Only .txt, .csv, .json are supported: " + file.getPath());
        }
    }
    
    /**
     * Check if file is locked or in use by another process
     */
    public boolean isFileLocked(File file) {
        try {
            // Try to rename the file to itself - if it works, file is not locked
            File tempFile = new File(file.getParent(), file.getName() + ".tmp");
            boolean renamed = file.renameTo(tempFile);
            if (renamed) {
                tempFile.renameTo(file);
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
