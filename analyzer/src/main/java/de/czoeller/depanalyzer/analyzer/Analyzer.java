package de.czoeller.depanalyzer.analyzer;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;

import java.util.List;

public interface Analyzer {
    List<Issue> analyze(DependencyNode node) throws AnalyzerException;
}
