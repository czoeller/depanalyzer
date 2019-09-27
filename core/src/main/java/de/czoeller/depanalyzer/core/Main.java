package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.dummy.DummyAnalyzerImpl;
import de.czoeller.depanalyzer.core.input.resolver.PomResolver;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;

import java.io.File;
import java.util.List;

public class Main {

    private PomResolver pomResolver;
    private DependencyNode dependencyNode;

    public Main() {
        this.pomResolver = new PomResolverImpl();
        readPOM();
        analyze();
    }

    public static void main(String[] args) {
        new Main();
    }

    private void readPOM() {
        final File pomFile = new File("core/pom.xml");
        this.dependencyNode = this.pomResolver.resolvePom(pomFile).getRootNode();
    }

    private void analyze() {
        final Analyzer dummyAnalyzer = new DummyAnalyzerImpl();
        final List<Issue> issues = dummyAnalyzer.analyze(this.dependencyNode);
        System.out.println(issues);
    }
}
