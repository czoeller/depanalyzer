package de.czoeller.depanalyzer.ui.visitor;

import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class GraphBuilderVisitor implements DependencyVisitor {

    private DependencyNode rootNode;
    private Graph<ArtifactGraphNode, ArtifactGraphEdge> graph = new OrderedSparseMultigraph<>();
    private Stack<DependencyNode> path = new Stack<>();

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
                final ArtifactGraphNode childGraphNode = new ArtifactGraphNode(currentNode);
                final ArtifactGraphEdge e = new ArtifactGraphEdge(node, currentNode);
                graph.addEdge(e, graphNode, childGraphNode, EdgeType.DIRECTED);
            });

        return true;
    }

    private ArtifactGraphNode findNodeOrCreate(DependencyNode node) {
        return graph.getVertices()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact() == node)
                    .findFirst()
                    .orElse(new ArtifactGraphNode(node));
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
