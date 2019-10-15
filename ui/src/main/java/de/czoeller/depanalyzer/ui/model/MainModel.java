package de.czoeller.depanalyzer.ui.model;

import com.google.common.graph.Network;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;

public class MainModel {
    private final Network<GraphDependencyNode, GraphDependencyEdge> graph;

    public MainModel(Network<GraphDependencyNode, GraphDependencyEdge> graph) {
        this.graph = graph;
    }

    public Network<GraphDependencyNode, GraphDependencyEdge> getGraph() {
        return graph;
    }
}
