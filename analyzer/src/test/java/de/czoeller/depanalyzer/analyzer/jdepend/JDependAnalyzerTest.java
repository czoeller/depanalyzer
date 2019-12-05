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

import de.czoeller.depanalyzer.analyzer.ExampleArtifacts;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.czoeller.depanalyzer.analyzer.jdepend.JDependAnalyzer.DISTANCE_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled("This is no unit test but rather an IT. Cannot be run currently because it has FS dependency.")
class JDependAnalyzerTest {

    private List<Issue> issues;
    private JDependAnalyzer jDependAnalyzer;

    @BeforeEach
    void setUp() {
        jDependAnalyzer = new JDependAnalyzer(() -> "mysql");
        DependencyNode node = new DependencyNode(ExampleArtifacts.TEST_ARTIFACT_MYSQL);
        issues = jDependAnalyzer.analyze(node);
    }

    @DisplayName("Has expected size of issues")
    @Test
    void hasExpectedSizeOfIssues() {
        assertThat(issues.size()).isEqualTo(10);
    }

    @DisplayName("Distance of all issues is greater or equal than threshold")
    @Test
    void instabilityOfAllIssuesIsGreaterThan() {
        final List<Float> distances = issues.stream()
                                          .map(i -> (MetricIssue) i)
                                          .map(MetricIssue::getInstability)
                                          .collect(Collectors.toList());
        assertThat(distances).allMatch(i -> i >= DISTANCE_THRESHOLD);
    }

    @DisplayName("TestToString")
    @Test
    void testToString() {
        assertThat(jDependAnalyzer.toString()).isEqualTo("JDependAnalyzer{type=Software Metrics}");
    }
}