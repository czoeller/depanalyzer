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

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.tasks.AnalyzeTask;
import de.czoeller.depanalyzer.analyzer.util.CompletableFutureCollector;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class AnalyzeExecutor {

    private final List<Analyzer> delegates;

    public AnalyzeExecutor(Analyzer... delegates) {
        this.delegates = Lists.newArrayList(Arrays.asList(delegates));
    }

    public List<AnalyzerResult> analyze(DependencyNode node) throws AnalyzerException {

        final List<DependencyNode> dependencyNodes = node.flattened()
                                                         .filter(distinctByKey(DependencyNode::getIdentifier))
                                                         .collect(Collectors.toList());

        final File analysisDir = new File("target/jar-analysis");
        try {
            if(!analysisDir.exists()) {
                analysisDir.mkdir();
            }
            FileUtils.cleanDirectory(analysisDir);
            dependencyNodes.stream()
                           .map(DependencyNode::getArtifact)
                           .filter(a -> null != a.getFile())
                           .filter(a -> !a.getFile().getName().contains("pom.xml"))
                           .map(Artifact::getFile)
                           .forEach(f -> createSymbolicLink(f, analysisDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final LinkedList<DependencyNode> nodesList = new LinkedList<>(dependencyNodes);

        final CompletableFuture<List<AnalyzerResult>> analyzerTasks = delegates.stream()
                                                                               .map(analyzer -> new AnalyzeTask(analyzer, nodesList))
                                                                               .map(CompletableFuture::supplyAsync)
                                                                               .collect(CompletableFutureCollector.collectResult());
        try {
            analyzerTasks.join();
            return analyzerTasks.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to analyze", e);
        }
        throw new IllegalStateException("Could not get analyzer results");
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
