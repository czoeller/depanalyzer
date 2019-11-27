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
import de.czoeller.depanalyzer.metamodel.visitor.ModelDependencyNodeVisitor;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
@Slf4j
public class GraphBuilderVisitor implements ModelDependencyNodeVisitor {

    private DependencyNode rootNode;
    private MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph;
    private Stack<DependencyNode> path = new Stack<DependencyNode>();

    public GraphBuilderVisitor(MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph) {
        this.graph = graph;
    }

    @Override
    public boolean visitEnter(DependencyNode node) {
        final GraphDependencyNode graphNode = findNodeOrCreate(node);

        if(null == rootNode) {
            rootNode = node;
        }

        graphNode.setDepth(path.size());
        path.push(node);

        node.getChildren()
            .stream()
            .unordered()
            .forEach(currentNode -> {
                final GraphDependencyNode childGraphNode = findNodeOrCreate(currentNode);
                final GraphDependencyEdge e = new GraphDependencyEdge(node, currentNode);
                if(!graph.nodes().contains(graphNode)) {
                    graph.addNode(childGraphNode);
                }
                if(!graph.edges().contains(e)) {
                    graph.addEdge(graphNode, childGraphNode, e);
                }

            });

        return true;
    }

    private GraphDependencyNode findNodeOrCreate(DependencyNode node) {
        return graph.nodes()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact().toString().equals(node.getArtifact().toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        log.trace("Could not find '{}' in graph. So creating one", node.getArtifact());
                        final GraphDependencyNode graphNode = new GraphDependencyNode(node);
                        return graphNode;
                    });
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        path.pop();
        return true;
    }

    public ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> getGraph() {
        return ImmutableNetwork.copyOf(graph);
    }
}
