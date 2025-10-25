package com.fileprocessor.filereader;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private final String chunkId;
    private final String sourceFile;
    private final List<String> lines;
    private final int startLine;
    private final int endLine;
    
    public Chunk(String chunkId, String sourceFile, List<String> lines, 
                 int startLine, int endLine) {
        this.chunkId = chunkId;
        this.sourceFile = sourceFile;
        this.lines = new ArrayList<>(lines);
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    // Getters
    public String getChunkId() { return chunkId; }
    public String getSourceFile() { return sourceFile; }
    public List<String> getLines() { return new ArrayList<>(lines); }
    public int getStartLine() { return startLine; }
    public int getEndLine() { return endLine; }
    public int getSize() { return lines.size(); }
    
    @Override
    public String toString() {
        return String.format("Chunk[%s] from %s [lines %d-%d]", 
                           chunkId, sourceFile, startLine, endLine);
    }
}
