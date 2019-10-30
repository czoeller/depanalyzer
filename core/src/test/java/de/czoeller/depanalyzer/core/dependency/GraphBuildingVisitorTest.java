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
package de.czoeller.depanalyzer.core.dependency;

import de.czoeller.depanalyzer.core.ToStringNodeIdRenderer;
import de.czoeller.depanalyzer.core.dependency.dot.DotGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.czoeller.depanalyzer.core.graph.GraphBuilderMatcher.hasNodesAndEdges;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GraphBuildingVisitorTest {

    private GraphBuilder<DependencyNode> graphBuilder;

    @BeforeEach
    void before() {
        this.graphBuilder = GraphBuilder.create(ToStringNodeIdRenderer.INSTANCE);
        new DotGraphStyleConfigurer(new StyleConfiguration()).configure(this.graphBuilder);
    }

    /**
     * .
     * <pre>
     * parent
     *     - child1
     *     - child2 (test)
     * </pre>
     */
    @Test
    void parentAndChildren() {
        org.eclipse.aether.graph.DependencyNode child1 = createMavenDependencyNode("child1");
        org.eclipse.aether.graph.DependencyNode child2 = createMavenDependencyNode("child2", "test");
        org.eclipse.aether.graph.DependencyNode parent = createMavenDependencyNode("parent", child1, child2);

        GraphBuildingVisitor visitor = new GraphBuildingVisitor(this.graphBuilder);
        assertTrue(visitor.visitEnter(parent));
        assertTrue(visitor.visitEnter(child1));
        assertTrue(visitor.visitLeave(child1));
        assertTrue(visitor.visitEnter(child2));
        assertTrue(visitor.visitLeave(child2));
        assertTrue(visitor.visitLeave(parent));

        assertThat(this.graphBuilder, hasNodesAndEdges(
                new String[]{
                        "\"groupId:parent:jar:version:compile\"[label=\"groupId:parent:jar:version:compile\"]",
                        "\"groupId:child:jar:version:compile\"[label=\"groupId:child1:jar:version:compile\"]",
                        "\"groupId:child:jar:version:compile\"[label=\"groupId:child2:jar:version:compile\"]"},
                new String[]{
                        "\"groupId:parent:jar:version:compile\" -> \"groupId:child1:jar:version:compile\"",
                        "\"groupId:parent:jar:version:compile\" -> \"groupId:child2:jar:version:test\""}));

    }

    private static org.eclipse.aether.graph.DependencyNode createMavenDependencyNode(String artifactId, org.eclipse.aether.graph.DependencyNode... children) {
        return createMavenDependencyNode(artifactId, "compile", children);
    }

    private static org.eclipse.aether.graph.DependencyNode createMavenDependencyNode(String artifactId, String scope, org.eclipse.aether.graph.DependencyNode... children) {
        Dependency dependency = new Dependency(createArtifact(artifactId), scope);
        DefaultDependencyNode node = new DefaultDependencyNode(dependency);
        node.setChildren(asList(children));

        return node;
    }

    private static org.eclipse.aether.artifact.Artifact createArtifact(String artifactId) {
        return new org.eclipse.aether.artifact.DefaultArtifact("groupId", artifactId, "", "jar", "version");
    }

}