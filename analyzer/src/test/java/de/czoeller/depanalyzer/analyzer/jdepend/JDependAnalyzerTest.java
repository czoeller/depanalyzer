package de.czoeller.depanalyzer.analyzer.jdepend;

import de.czoeller.depanalyzer.analyzer.ExampleArtifacts;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.czoeller.depanalyzer.analyzer.jdepend.JDependAnalyzer.INSTABILITY_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("Instability of all issues is greater or equal than threshold")
    @Test
    void instabilityOfAllIssuesIsGreaterThan() {
        final List<Float> instabilities = issues.stream()
                                          .map(i -> (MetricIssue) i)
                                          .map(MetricIssue::getInstability)
                                          .collect(Collectors.toList());
        assertThat(instabilities).allMatch(i -> i >= INSTABILITY_THRESHOLD);
    }

    @DisplayName("TestToString")
    @Test
    void testToString() {
        assertThat(jDependAnalyzer.toString()).isEqualTo("JDependAnalyzer{type=Software Metrics}");
    }
}