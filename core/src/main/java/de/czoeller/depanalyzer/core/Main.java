package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.impl.DummyAnalyzerImpl;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        final Analyzer dummyAnalyzer = new DummyAnalyzerImpl();
        final Map<Artifact, List<Issue>> issues = dummyAnalyzer.analyze(Core.TEST_ARTIFACT_SPRING);
        System.out.println(issues);
    }
}
