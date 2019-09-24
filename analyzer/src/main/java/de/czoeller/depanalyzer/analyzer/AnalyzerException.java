package de.czoeller.depanalyzer.analyzer;

public class AnalyzerException extends RuntimeException {

    private static final long serialVersionUID = -5006996230640756174L;

    public AnalyzerException(Exception cause) {
        super(cause);
    }

    public AnalyzerException(String message, Exception cause) {
        super(message, cause);
    }
}

