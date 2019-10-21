package de.czoeller.depanalyzer.metamodel;

public enum Analyzers {
    DUMMY("Dummy"),
    METRICS("Software Metrics"),
    CVE("CVE");

    Analyzers(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}