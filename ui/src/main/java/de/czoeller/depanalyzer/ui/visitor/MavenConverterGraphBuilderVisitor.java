package de.czoeller.depanalyzer.ui.visitor;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class MavenConverterGraphBuilderVisitor implements DependencyVisitor {

    private org.eclipse.aether.graph.DependencyNode rootNode;
    private Graph<ArtifactGraphNode, ArtifactGraphEdge> graph;
    private Stack<org.eclipse.aether.graph.DependencyNode> path = new Stack<>();

    public MavenConverterGraphBuilderVisitor(Forest<ArtifactGraphNode, ArtifactGraphEdge> graph) {
        this.graph = graph;
    }

    @Override
    public boolean visitEnter(org.eclipse.aether.graph.DependencyNode node) {
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
                final DependencyNode currentDependencyNode = new de.czoeller.depanalyzer.metamodel.DependencyNode(currentNode);
                final de.czoeller.depanalyzer.metamodel.DependencyNode nodeDependencyNode = new de.czoeller.depanalyzer.metamodel.DependencyNode(node);

                final ArtifactGraphNode childGraphNode = new ArtifactGraphNode(currentDependencyNode);
                final ArtifactGraphEdge e = new ArtifactGraphEdge(nodeDependencyNode, currentDependencyNode);
                graph.addEdge(e, graphNode, childGraphNode, EdgeType.DIRECTED);
            });

        return true;
    }

    private ArtifactGraphNode findNodeOrCreate(org.eclipse.aether.graph.DependencyNode node) {
        return graph.getVertices()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact().toString().equals(new de.czoeller.depanalyzer.metamodel.DependencyNode(node).toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        final ArtifactGraphNode graphNode = new ArtifactGraphNode(new de.czoeller.depanalyzer.metamodel.DependencyNode(node));
                        graph.addVertex(graphNode);
                        return graphNode;
                    });
    }

    @Override
    public boolean visitLeave(org.eclipse.aether.graph.DependencyNode node) {
        path.pop();
        return true;
    }

    public Graph<ArtifactGraphNode, ArtifactGraphEdge> getGraph() {
        return graph;
    }
}
