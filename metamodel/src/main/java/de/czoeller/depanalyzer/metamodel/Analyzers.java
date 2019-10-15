package de.czoeller.depanalyzer.metamodel;

public enum Analyzers {
    METRICS("Software Metrics");

    Analyzers(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}