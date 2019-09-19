package de.czoeller.depanalyzer.ui.visitor;

import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.Objects;
import java.util.Stack;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class MavenConverterGraphBuilderVisitor implements DependencyVisitor {

    private DependencyNode rootNode;
    private Graph<ArtifactGraphNode, ArtifactGraphEdge> graph;
    private Stack<DependencyNode> path = new Stack<>();

    public MavenConverterGraphBuilderVisitor(Forest<ArtifactGraphNode, ArtifactGraphEdge> graph) {
        this.graph = graph;
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
                final de.czoeller.depanalyzer.core.dependency.DependencyNode currentDependencyNode = new de.czoeller.depanalyzer.core.dependency.DependencyNode(currentNode);
                final de.czoeller.depanalyzer.core.dependency.DependencyNode nodeDependencyNode = new de.czoeller.depanalyzer.core.dependency.DependencyNode(node);

                final ArtifactGraphNode childGraphNode = new ArtifactGraphNode(currentDependencyNode);
                final ArtifactGraphEdge e = new ArtifactGraphEdge(nodeDependencyNode, currentDependencyNode);
                graph.addEdge(e, graphNode, childGraphNode, EdgeType.DIRECTED);
            });

        return true;
    }

    private ArtifactGraphNode findNodeOrCreate(DependencyNode node) {
        return graph.getVertices()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(artifactGraphNode -> artifactGraphNode.getArtifact().toString().equals(new de.czoeller.depanalyzer.core.dependency.DependencyNode(node).toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        final ArtifactGraphNode graphNode = new ArtifactGraphNode(new de.czoeller.depanalyzer.core.dependency.DependencyNode(node));
                        graph.addVertex(graphNode);
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
