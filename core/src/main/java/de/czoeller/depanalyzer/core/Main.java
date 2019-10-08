package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.analyzer.AnalyzeExecutor;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.jdepend.JDependAnalyzer;
import de.czoeller.depanalyzer.core.input.resolver.PomResolver;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class Main {

    private PomResolver pomResolver;
    @Getter
    private DependencyNode dependencyNode;

    public Main() {
        this.pomResolver = new PomResolverImpl();
    }

    public static void main(String[] args) {
        new Main().analyzePOM(new File("C:\\Users\\noex_\\IdeaProjects\\MasterthesisAnalyse\\velocity-engine\\pom.xml"));
    }

    public void analyzePOM(File pomFile) {
        readPOM(pomFile);
        analyze();
    }

    public void readPOM(File pomFile) {
        this.dependencyNode = this.pomResolver.resolvePom(pomFile).getRootNode();
    }

    public void analyze() {
        log.info("Starting analyze ...");
        AnalyzerContext context = () -> dependencyNode.getArtifact().getGroupId();
        final AnalyzeExecutor analyzeExecutor = new AnalyzeExecutor(new JDependAnalyzer(context));
        final Map<String, List<Issue>> dependenciesAndIssues = analyzeExecutor.analyze(dependencyNode, context);
        log.info("{}", dependenciesAndIssues);

        mapIssuesToNodes(dependenciesAndIssues);
    }

    private void mapIssuesToNodes(Map<String, List<Issue>> dependenciesAndIssues) {
        dependencyNode.flattened().forEach(n -> {
            final String key = n.getIdentifier();
            if(dependenciesAndIssues.containsKey(key)) {
                n.getIssues().addAll(dependenciesAndIssues.get(n.getIdentifier()));
            }
        });
    }

}
