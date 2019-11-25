/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        val dependencyNodes = node.flattened().filter(distinctByKey(DependencyNode::getIdentifier)).collect(Collectors.toList());

        final File analysisDir = new File("target/jar-analysis");
        try {
            if(!analysisDir.exists()) {
                analysisDir.mkdir();
            }
            FileUtils.cleanDirectory(analysisDir);
            dependencyNodes.stream().map(DependencyNode::getArtifact).map(Artifact::getFile).forEach(f -> createSymbolicLink(f, analysisDir));
            dependencyNodes.stream().map(DependencyNode::getArtifact).forEach(a -> a.setFile(new File(analysisDir, a.getFile().getName())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final CompletableFuture<List<List<AnalyzerResult>>> collect = StreamSupport.stream(Iterables.partition(dependencyNodes, 25).spliterator(), false)
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
                        } else {
                            mergedResultsByAnalyzerType.get(analyzerResult.getAnalyzerType()).getNodeIssuesMap().put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to analyze", e);
        }
        return new ArrayList<>(mergedResultsByAnalyzerType.values());
    }

    private void createSymbolicLink(File source, File analysisDir) {
        try {
            final Path link = Paths.get(analysisDir.getPath(), source.getName());
            if(!Files.exists(link)) {
                Files.createSymbolicLink(link, Paths.get(source.toURI()));
            }
        } catch (IOException e) {
            log.error("Failed to create symlink", e);
            throw new RuntimeException(e);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
