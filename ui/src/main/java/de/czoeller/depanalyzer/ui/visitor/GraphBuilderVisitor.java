package de.czoeller.depanalyzer.ui.visitor;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.visitor.ModelDependencyNodeVisitor;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class GraphBuilderVisitor implements ModelDependencyNodeVisitor {

    private DependencyNode rootNode;
    private Graph<ArtifactGraphNode, ArtifactGraphEdge> graph;
    private Stack<DependencyNode> path = new Stack<DependencyNode>();

    public GraphBuilderVisitor(Forest<ArtifactGraphNode, ArtifactGraphEdge> graph) {
        this.graph = new DirectedSparseMultigraph<ArtifactGraphNode, ArtifactGraphEdge>();
    }

    @Override
    public boolean visitEnter(DependencyNode node) {
        final ArtifactGraphNode graphNode = findNodeOrCreate(node);

        if(null == rootNode) {
            rootNode = node;
        }

        graphNode.setPrimaryLevel(path.size());
        path.push(node);

        node.getChildren()
            .stream()
            .unordered()
            .forEach(currentNode -> {
                final ArtifactGraphNode childGraphNode = findNodeOrCreate(currentNode);
                final ArtifactGraphEdge e = new ArtifactGraphEdge(node, currentNode);
                if(!graph.containsVertex(graphNode)) {
                    graph.addVertex(graphNode);
                }
                if(!graph.containsEdge(e)) {
                    graph.addEdge(e, graphNode, childGraphNode, EdgeType.DIRECTED);
                }

            });

        return true;
    }

    private ArtifactGraphNode findNodeOrCreate(DependencyNode node) {
        return graph.getVertices()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact().getArtifact().toString().equals(node.getArtifact().toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        System.out.println("Could not find " + node.getArtifact() + " in graph. So creating one");
                        final ArtifactGraphNode graphNode = new ArtifactGraphNode(node);
                        return graphNode;
                    });
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        path.pop();
        return true;
    }

    public Graph<ArtifactGraphNode, ArtifactGraphEdge> getGraph() {
        return graph;
    }
}
