package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.impl.DummyAnalyzerImpl;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.input.resolver.PomResolver;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;

import java.io.File;
import java.util.List;
import java.util.Map;

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
        final Artifact artifact = new Artifact(this.dependencyNode.getArtifact().getFile().getAbsolutePath());
        final Map<Artifact, List<Issue>> issues = dummyAnalyzer.analyze(artifact);
        System.out.println(issues);
    }
}
