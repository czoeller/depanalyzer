package de.czoeller.depanalyzer.ui;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.Node;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverResult;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import de.czoeller.depanalyzer.ui.visitor.GraphBuilderVisitor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import org.apache.maven.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;

import java.io.File;
import java.util.Map;

import static de.czoeller.depanalyzer.ui.AetherUtils.getDependencyNode;

public class ExampleGraph {

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> createTree() {

        DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
        DependencyNode node3 = getDependencyNode("c2", "util", "0.0.1");

        node.setChildren(Lists.newArrayList(node2, node3));

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor();
        node.accept(graphBuilderVisitor);

        return graphBuilderVisitor.getGraph();
    }

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> realDependencyTree() {
        SparseMultigraph<ArtifactGraphNode, ArtifactGraphEdge> graph = new SparseMultigraph<>();
        //DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        //DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
//
//
        //node.getChildren().add(node2);
//
        //final ArtifactGraphNode v1 = new ArtifactGraphNode(node);
        //final ArtifactGraphNode v2 = new ArtifactGraphNode(node2);
        //final ArtifactGraphEdge e1 = new ArtifactGraphEdge(node, node2);
        //graph.addVertex(v1);
        //graph.addEdge(e1, v1, v2, EdgeType.DIRECTED);
//
        //v1.setPrimaryLevel(0);
        //v2.setPrimaryLevel(1);
//
        final PomResolverImpl pomResolver = new PomResolverImpl();
        final File pomFile = new File("core/pom.xml");
        final PomResolverResult pomResolverResult = pomResolver.resolvePomExperimental(pomFile);

        final Map<String, Node<de.czoeller.depanalyzer.core.dependency.DependencyNode>> nodeDefinitions = pomResolverResult.getNodeDefinitions();

        for (Edge edge : pomResolverResult.getEdges()) {

            final Node<de.czoeller.depanalyzer.core.dependency.DependencyNode> dependencyNodeNode = nodeDefinitions.get(edge.getToNodeId());
            final Artifact artifact = dependencyNodeNode.nodeObject.getArtifact();
            ArtifactGraphNode v1 = new ArtifactGraphNode(dependencyNodeNode.nodeObject);
            //graph.getVertices().stream().filter(v -> v == v1).findFirst().orElseGet(ArtifactGraphNode::new)
            //graph.add
        }


        return graph;

        //throw new IllegalStateException("Could not build graph");
    }

}
