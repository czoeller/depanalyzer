package de.czoeller.depanalyzer.ui.model;

import com.google.common.graph.Network;

public class UIModel {
    private final Network<GraphDependencyNode, GraphDependencyEdge> graph;

    public UIModel(Network<GraphDependencyNode, GraphDependencyEdge> graph) {
        this.graph = graph;
    }

    public Network<GraphDependencyNode, GraphDependencyEdge> getGraph() {
        return graph;
    }
}
