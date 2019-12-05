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
package de.czoeller.depanalyzer.analyzer.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.metamodel.AnalyzerResult;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class AnalyzeTask implements Supplier<AnalyzerResult> {

    private final Analyzer analyzer;
    private final List<DependencyNode> chunk;

    /**
     * Performs different analysis on a given chunk on nodes.
     * @param analyzer
     * @param chunk
     */
    public AnalyzeTask(Analyzer analyzer, List<DependencyNode> chunk) {
        this.analyzer = analyzer;
        this.chunk = chunk;
    }

    @Override
    public AnalyzerResult get() {
        final Map<String, List<Issue>> nodeIssues = Maps.newHashMap();

        log.debug("{} starting to analyze with analyzer {} a list of nodes with size #{} namely: {}", Thread.currentThread().getName(), analyzer.getType().toString(), chunk.size(), chunk);

        log.trace("{} starting to analyze with analyzer '{}'", Thread.currentThread().getName(), analyzer);

        for (DependencyNode node : chunk) {
            if(node.getTypes().contains("pom")) {
                log.trace("Skipping analyze with {} for dependency of type pom '{}'", analyzer.getClass().getSimpleName(), node.toString());
            } else if(null == node.getArtifact().getFile()) {
                log.trace("Skipping analyze with {} for dependency with empty artifact file '{}'", analyzer.getClass().getSimpleName(), node.toString());
            } else if(node.getArtifact().getFile().getName().contains("pom.xml")) {
                log.trace("Skipping analyze with {} for dependency with pom.xml artifact file '{}'", analyzer.getClass().getSimpleName(), node.toString());
            } else {
                final List<Issue> issues = analyzer.analyze(node);
                if(!issues.isEmpty()) {
                    log.info("{} with analyzer '{}' found #{} issues", Thread.currentThread().getName(), analyzer, issues.size());
                    nodeIssues.putIfAbsent(node.getIdentifier(), Lists.newArrayList());
                    nodeIssues.get(node.getIdentifier()).addAll(issues);
                } else {
                    log.info("{} with analyzer '{}' found no issues", Thread.currentThread().getName(), analyzer);
                }
            }
        }

        return new AnalyzerResult(this.analyzer.getType(), nodeIssues);
    }
}

