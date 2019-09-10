package de.czoeller.depanalyzer.ui;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import edu.uci.ics.jung.graph.Graph;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

import static de.czoeller.depanalyzer.ui.AetherUtils.getDependencyNode;

public class ExampleGraph {

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> createTree() {

        DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
        DependencyNode node3 = getDependencyNode("c2", "util", "0.0.1");

        node.setChildren(Lists.newArrayList(node2, node3));

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor();
        node.accept(graphBuilderVisitor);

        return null;
    }

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> realDependencyTree() {



        throw new IllegalStateException("Could not build graph");
    }

}
