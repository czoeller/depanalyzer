package de.czoeller.depanalyzer.analyzer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.czoeller.depanalyzer.analyzer.tasks.AnalyzeTask;
import de.czoeller.depanalyzer.analyzer.util.CompletableFutureCollector;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public Map<String, List<Issue>> analyze(DependencyNode node, AnalyzerContext context) throws AnalyzerException {

        val dependencyNodes = node.flattened().filter(distinctByKey(d -> d.getArtifact().toString())).collect(Collectors.toList());
        final CompletableFuture<List<Map<String, List<Issue>>>> collect = StreamSupport.stream(Iterables.partition(dependencyNodes, 10).spliterator(), false)
                                                                   .map(chunk -> new AnalyzeTask(delegates, context, chunk))
                                                                   .map(CompletableFuture::supplyAsync)
                                                                   .collect(CompletableFutureCollector.collectResult());

        collect.join();
        final List<Map<String, List<Issue>>> maps;
        final Map<String, List<Issue>> newMaps = Maps.newHashMap();
        try {
            maps = collect.get();
            for (Map<String, List<Issue>> map : maps) {
                for (Map.Entry<String, List<Issue>> entry : map.entrySet()) {
                    newMaps.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return newMaps;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
