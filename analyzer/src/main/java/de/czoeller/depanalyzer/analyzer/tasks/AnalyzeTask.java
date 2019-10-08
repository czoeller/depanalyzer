package de.czoeller.depanalyzer.analyzer.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AnalyzeTask implements Supplier<Map<String, List<Issue>>> {

    private final List<Analyzer> analyzers;
    private final AnalyzerContext context;
    private final List<DependencyNode> chunk;

    public AnalyzeTask(List<Analyzer> analyzers, AnalyzerContext context, List<DependencyNode> chunk) {
        this.analyzers = analyzers;
        this.context = context;
        this.chunk = chunk;
    }

    @Override
    public Map<String, List<Issue>> get() {
        final Map<String, List<Issue>> map = Maps.newHashMap();
        for (Analyzer analyzer : analyzers) {
            try {
                final BaseAnalyzer analyzerInstance = (BaseAnalyzer) analyzer.getClass().newInstance();
                analyzerInstance.setContext(context);

                for (DependencyNode node : chunk) {
                    if(node.getTypes().contains("pom")) {
                        log.info("Skipping analyze with {} for dependency of type pom '{}'", analyzer.getClass().getSimpleName(), node.toString());
                    } else {
                        map.putIfAbsent(node.getIdentifier(), Lists.newArrayList());
                        final List<Issue> issues = analyzerInstance.analyze(node);
                        map.get(node.getIdentifier()).addAll(issues);
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}

