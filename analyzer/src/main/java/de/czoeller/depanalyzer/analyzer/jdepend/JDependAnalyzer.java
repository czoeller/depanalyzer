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
package de.czoeller.depanalyzer.analyzer.jdepend;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JDependAnalyzer extends BaseAnalyzer {

    public static final double INSTABILITY_THRESHOLD = 0.75;

    private JDepend jdepend;

    private Map<String, Boolean> visitedPackages = new HashMap<>();

    /**
     * Required to obtain instance reflective.
     * TODO: remove reflective instantiation
     */
    public JDependAnalyzer() {
        init();
    }

    public JDependAnalyzer(AnalyzerContext context) {
        super(context);
        init();
    }

    private void init() {
        this.jdepend = new JDepend();
    }

    @Override
    public Analyzers getType() {
        return Analyzers.METRICS;
    }

    @Override
    public Analyzer newInstance(AnalyzerContext context) {
        return new JDependAnalyzer(context);
    }

    @Override
    public List<Issue> analyze(DependencyNode node)  {
        List<Issue> issues = Lists.newArrayList();
        try {
            jdepend.addDirectory(node.getArtifact().getFile().getAbsolutePath());
            final Collection<JavaPackage> packageList = jdepend.analyze();

            final List<JavaPackage> filteredPackages = packageList.stream().filter(this::shouldAnalyzePackage).collect(Collectors.toList());

            for (JavaPackage javaPackage : filteredPackages) {
                log.trace("Analyzing package '{}'", javaPackage);
                visitedPackages.put(javaPackage.getName(), true);
                if (javaPackage.getName().contains(getContext().getTargetGroupId())) {
                    log.trace("Analyzing package internals of '{}'", javaPackage);
                    final float instability = javaPackage.instability();
                    if(instability >= INSTABILITY_THRESHOLD) {
                        log.info("Found instability issue in package '{}'", javaPackage);
                        issues.add(new MetricIssue(Issue.Severity.LOW, String.format("instability of pkg '%s': %.2f", javaPackage.getName(), instability), instability));
                    }
                } else {
                    log.trace("Skip analyzing package '{}'", javaPackage);
                }
            }
        } catch (IOException e) {
            throw new AnalyzerException("Failed to analyze artifact", e);
        }

        return issues;
    }

    private boolean shouldAnalyzePackage(JavaPackage javaPackage) {
        return !alreadyVisited(javaPackage) && !javaPackage.getName().startsWith("java.") && javaPackage.getName()
                                                            .contains(getContext().getTargetGroupId());
    }

    private boolean alreadyVisited(JavaPackage javaPackage) {
        final boolean visited = visitedPackages.containsKey(javaPackage.getName());
        if(visited) {
            log.trace("Already visited package '{}'", javaPackage.getName());
        }
        return visited;
    }
}
