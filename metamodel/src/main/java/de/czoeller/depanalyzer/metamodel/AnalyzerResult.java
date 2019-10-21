package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalyzerResult {
    private final Analyzers analyzerType;
    private final Map<String, List<Issue>> nodeIssuesMap;
}
