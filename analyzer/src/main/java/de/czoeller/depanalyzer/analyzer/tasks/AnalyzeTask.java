package de.czoeller.depanalyzer.analyzer.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AnalyzeTask implements Supplier<List<AnalyzerResult>> {

    private final List<Analyzer> analyzers;
    private final AnalyzerContext context;
    private final List<DependencyNode> chunk;

    /**
     * Performs different analysis on a given chunk on nodes.
     * @param analyzers
     * @param context
     * @param chunk
     */
    public AnalyzeTask(List<Analyzer> analyzers, AnalyzerContext context, List<DependencyNode> chunk) {
        this.analyzers = analyzers;
        this.context = context;
        this.chunk = chunk;
    }

    @Override
    public List<AnalyzerResult> get() {
        List<AnalyzerResult> results = Lists.newArrayList();

        for (Analyzer analyzer : analyzers) {
            final Map<String, List<Issue>> nodeIssues = Maps.newHashMap();
            try {
                final BaseAnalyzer analyzerInstance = (BaseAnalyzer) analyzer.getClass().newInstance();
                analyzerInstance.setContext(context);

                for (DependencyNode node : chunk) {
                    if(node.getTypes().contains("pom")) {
                        log.info("Skipping analyze with {} for dependency of type pom '{}'", analyzer.getClass().getSimpleName(), node.toString());
                    } else {
                        final List<Issue> issues = analyzerInstance.analyze(node);
                        if(!issues.isEmpty()) {
                            nodeIssues.putIfAbsent(node.getIdentifier(), Lists.newArrayList());
                            nodeIssues.get(node.getIdentifier()).addAll(issues);
                        }
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            results.add(new AnalyzerResult(analyzer.getType(), nodeIssues));
        }
        return results;
    }
}

