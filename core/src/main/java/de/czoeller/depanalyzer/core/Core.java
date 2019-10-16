package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.analyzer.AnalyzeExecutor;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.jdepend.JDependAnalyzer;
import de.czoeller.depanalyzer.core.input.resolver.PomResolver;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class Core {

    private PomResolver pomResolver;
    @Getter
    private DependencyNode dependencyNode;

    public Core() {
        this.pomResolver = new PomResolverImpl();
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
        final AnalyzerResult dependenciesAndIssues = analyzeExecutor.analyze(dependencyNode, context);
        log.info("{}", dependenciesAndIssues);

        setIssuesToNodes(dependenciesAndIssues);
    }

    private void setIssuesToNodes(AnalyzerResult analyzerResult) {
        final Map<String, List<Issue>> nodeIssuesMap = analyzerResult.getNodeIssuesMap();
        dependencyNode.flattened().forEach(n -> {
            final String key = n.getIdentifier();
            if(nodeIssuesMap.containsKey(key)) {
                n.addIssues(analyzerResult.getAnalyzerType(), nodeIssuesMap.get(n.getIdentifier()));
            }
        });
    }
}
