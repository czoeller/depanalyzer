package de.czoeller.depanalyzer.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import de.czoeller.depanalyzer.analyzer.AnalyzeExecutor;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.dependencychecker.DependencyCheckerAnalyzer;
import de.czoeller.depanalyzer.analyzer.dummy.DummyAnalyzerImpl;
import de.czoeller.depanalyzer.analyzer.jdepend.JDependAnalyzer;
import de.czoeller.depanalyzer.analyzer.spotbugs.SpotBugsAnalyzer;
import de.czoeller.depanalyzer.core.input.resolver.PomResolver;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

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
        analyzeAndPersistResults();
    }

    public void readPOM(File pomFile) {
        this.dependencyNode = this.pomResolver.resolvePom(pomFile).getRootNode();
    }

    public void analyzeAndPersistResults() {
        log.info("Starting analyze ...");
        AnalyzerContext context = () -> dependencyNode.getArtifact().getGroupId();
        final AnalyzeExecutor analyzeExecutor = new AnalyzeExecutor(
                new DummyAnalyzerImpl(context),
                new JDependAnalyzer(context),
                new DependencyCheckerAnalyzer(context),
                new SpotBugsAnalyzer(context)
        );
        final List<AnalyzerResult> analyzerResults = analyzeExecutor.analyze(dependencyNode, context);

        for (AnalyzerResult analyzerResult : analyzerResults) {
            log.info("found issues for analyzer type '{}': {}", analyzerResult.getAnalyzerType(), analyzerResults);
            setIssuesToNodes(analyzerResult);
        }

        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        try (Output output = new Output(new FileOutputStream("results.dar"))) {
            kryo.writeObject(output, dependencyNode);
        } catch (FileNotFoundException e) {
            log.error("Could not write results to file.", e);
        }
    }

    private void setIssuesToNodes(AnalyzerResult analyzerResult) {
        final Map<String, List<Issue>> nodeIssuesMap = analyzerResult.getNodeIssuesMap();
        dependencyNode.flattened()
                      .filter(distinctByKey(DependencyNode::getIdentifier))
                      .forEach(n -> {
            final String key = n.getIdentifier();
            if(nodeIssuesMap.containsKey(key)) {
                final List<Issue> issues = nodeIssuesMap.get(n.getIdentifier());
                n.addIssues(analyzerResult.getAnalyzerType(), issues);
            }
        });
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
