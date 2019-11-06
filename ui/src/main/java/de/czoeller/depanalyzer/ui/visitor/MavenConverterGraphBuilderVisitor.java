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
package de.czoeller.depanalyzer.ui.visitor;

import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class MavenConverterGraphBuilderVisitor implements DependencyVisitor {

    private org.eclipse.aether.graph.DependencyNode rootNode;
    private MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph;
    private Stack<org.eclipse.aether.graph.DependencyNode> path = new Stack<>();

    public MavenConverterGraphBuilderVisitor(MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph) {
        this.graph = graph;
    }

    @Override
    public boolean visitEnter(org.eclipse.aether.graph.DependencyNode node) {
        final GraphDependencyNode graphNode = findNodeOrCreate(node);

        if(null == rootNode) {
            rootNode = node;
        }

        graphNode.setPrimaryLevel(path.size());
        path.push(node);

        node.getChildren()
            .stream()
            .unordered()
            .forEach(currentNode -> {
                final DependencyNode currentDependencyNode = new de.czoeller.depanalyzer.metamodel.DependencyNode(currentNode);
                final de.czoeller.depanalyzer.metamodel.DependencyNode nodeDependencyNode = new de.czoeller.depanalyzer.metamodel.DependencyNode(node);

                final GraphDependencyNode childGraphNode = new GraphDependencyNode(currentDependencyNode);
                final GraphDependencyEdge e = new GraphDependencyEdge(nodeDependencyNode, currentDependencyNode);

                graph.addEdge(graphNode, childGraphNode, e);
            });

        return true;
    }

    private GraphDependencyNode findNodeOrCreate(org.eclipse.aether.graph.DependencyNode node) {
        return graph.nodes()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact().toString().equals(new de.czoeller.depanalyzer.metamodel.DependencyNode(node).toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        final GraphDependencyNode graphNode = new GraphDependencyNode(new de.czoeller.depanalyzer.metamodel.DependencyNode(node));
                        graph.addNode(graphNode);
                        return graphNode;
                    });
    }

    @Override
    public boolean visitLeave(org.eclipse.aether.graph.DependencyNode node) {
        path.pop();
        return true;
    }

    public ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> getGraph() {
        return ImmutableNetwork.copyOf(graph);
    }
}
