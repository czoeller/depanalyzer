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
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
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

        log.debug("{} starting to analyze with #{} analyzers a chunk of nodes with size #{} namely: {}", Thread.currentThread().getName(), analyzers.size(), chunk.size(), chunk);

        for (Analyzer analyzer : analyzers) {
            log.trace("{} starting to analyze with analyzer '{}'", Thread.currentThread().getName(), analyzer);

            final Map<String, List<Issue>> nodeIssues = Maps.newHashMap();
            final Analyzer analyzerInstance = analyzer.newInstance(context);

            for (DependencyNode node : chunk) {
                if(node.getTypes().contains("pom")) {
                    log.trace("Skipping analyze with {} for dependency of type pom '{}'", analyzerInstance.getClass().getSimpleName(), node.toString());
                } else {
                    final List<Issue> issues = analyzerInstance.analyze(node);
                    if(!issues.isEmpty()) {
                        log.info("{} with analyzer '{}' found #{} issues", Thread.currentThread().getName(), analyzerInstance, issues.size());
                        nodeIssues.putIfAbsent(node.getIdentifier(), Lists.newArrayList());
                        nodeIssues.get(node.getIdentifier()).addAll(issues);
                    } else {
                        log.info("{} with analyzer '{}' found no issues", Thread.currentThread().getName(), analyzerInstance);
                    }
                }
            }
            results.add(new AnalyzerResult(analyzer.getType(), nodeIssues));
        }
        return results;
    }
}

