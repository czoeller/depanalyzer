package de.czoeller.depanalyzer.analyzer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.tasks.AnalyzeTask;
import de.czoeller.depanalyzer.analyzer.util.CompletableFutureCollector;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class AnalyzeExecutor {

    private final List<Analyzer> delegates;

    public AnalyzeExecutor(Analyzer... delegates) {
        this.delegates = Lists.newArrayList(Arrays.asList(delegates));
    }

    public List<AnalyzerResult> analyze(DependencyNode node, AnalyzerContext context) throws AnalyzerException {

        val dependencyNodes = node.flattened().filter(distinctByKey(d -> d.getArtifact().toString())).collect(Collectors.toList());
        final CompletableFuture<List<List<AnalyzerResult>>> collect = StreamSupport.stream(Iterables.partition(dependencyNodes, 10).spliterator(), false)
                                                                   .map(chunk -> new AnalyzeTask(delegates, context, chunk))
                                                                   .map(CompletableFuture::supplyAsync)
                                                                   .collect(CompletableFutureCollector.collectResult());

        collect.join();
        final List<List<AnalyzerResult>> results;
        final Map<Analyzers, AnalyzerResult> mergedResultsByAnalyzerType = new HashMap<>();

        try {
            results = collect.get();
            for (List<AnalyzerResult> result : results) {
                for (AnalyzerResult analyzerResult : result) {
                    for (Map.Entry<String, List<Issue>> entry : analyzerResult.getNodeIssuesMap().entrySet() ) {
                        if(!mergedResultsByAnalyzerType.containsKey(analyzerResult.getAnalyzerType())) {
                            mergedResultsByAnalyzerType.put(analyzerResult.getAnalyzerType(), analyzerResult);
                        }
                        mergedResultsByAnalyzerType.get(analyzerResult.getAnalyzerType()).getNodeIssuesMap().put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(mergedResultsByAnalyzerType.values());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
