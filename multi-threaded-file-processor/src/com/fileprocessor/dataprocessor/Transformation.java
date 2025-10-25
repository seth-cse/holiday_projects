package com.fileprocessor.dataprocessor;

@FunctionalInterface
public interface Transformation {
    String apply(String line, int lineNumber, String sourceFile);
    
    // Common transformation implementations
    Transformation IDENTITY = (line, lineNumber, sourceFile) -> line;
    
    Transformation UPPERCASE = (line, lineNumber, sourceFile) -> line.toUpperCase();
    
    Transformation LOWERCASE = (line, lineNumber, sourceFile) -> line.toLowerCase();
    
    Transformation TRIM = (line, lineNumber, sourceFile) -> line.trim();
    
    Transformation ADD_LINE_NUMBER = (line, lineNumber, sourceFile) -> 
        String.format("[%s:%d] %s", sourceFile, lineNumber, line);
    
    static Transformation createDefaultTransformation() {
        return TRIM.andThen(ADD_LINE_NUMBER);
    }
    
    default Transformation andThen(Transformation after) {
        return (line, lineNumber, sourceFile) -> 
            after.apply(this.apply(line, lineNumber, sourceFile), lineNumber, sourceFile);
    }
    
    default Transformation compose(Transformation before) {
        return (line, lineNumber, sourceFile) -> 
            this.apply(before.apply(line, lineNumber, sourceFile), lineNumber, sourceFile);
    }
}
