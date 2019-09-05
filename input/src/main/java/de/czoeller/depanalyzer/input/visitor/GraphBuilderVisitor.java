package de.czoeller.depanalyzer.input.visitor;

import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Dependency;
import de.czoeller.depanalyzer.metamodel.Version;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Collects nodes into a graph with nodes and edges.
 * TODO: handle cycles.
 */
public class GraphBuilderVisitor implements DependencyVisitor {

    private Dependency rootNode;
    private Stack<Dependency> path = new Stack<>();

    @Override
    public boolean visitEnter(@Nonnull DependencyNode theirNode) {
        final Dependency myNode = mapNode(theirNode);

        if(null == rootNode) {
            rootNode = myNode;
        }

        path.push(myNode);

        theirNode.getChildren()
            .stream()
            .unordered()
            .map(this::mapNode)
                 .forEach(e -> myNode.getChildren().add(e));

        return true;
    }


    private Version extractVersion(Supplier<String> groupId, Supplier<String> artifactId, Supplier<String> version) {
        return new Version(groupId.get(), artifactId.get(), version.get());
    }

    private Dependency mapNode(@Nonnull DependencyNode theirNode) {
        final org.eclipse.aether.artifact.Artifact artifact1 = theirNode.getArtifact();
        final Version version = extractVersion(artifact1::getGroupId, artifact1::getArtifactId, artifact1::getVersion);
        final Dependency myNode = findNodeOrCreate(theirNode);
        final File file = theirNode.getArtifact().getFile();
        final String path = file != null ? file.getAbsolutePath() : "";
        final Artifact artifact = new Artifact(path);
        artifact.setVersion(version);
        myNode.setArtifact(artifact);

        return myNode;
    }

    private Dependency findNodeOrCreate(DependencyNode node) {
        if(rootNode == null) {
            return new Dependency();
        } else if(checkEqual(rootNode, node)) {
            return rootNode;
        } else {
            return rootNode.flattened()
                    .filter(n -> checkEqual(n, node))
                    .findFirst()
                    .orElseGet(Dependency::new);
        }
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        path.pop();
        return true;
    }

    private boolean checkEqual(Dependency dependency, DependencyNode dependencyNode) {
        return dependency.getArtifact().getVersion().getGroupId().equals(dependencyNode.getArtifact().getGroupId())
            && dependency.getArtifact().getVersion().getArtifactId().equals(dependencyNode.getArtifact().getArtifactId())
            && dependency.getArtifact().getVersion().getVersion().equals(dependencyNode.getArtifact().getVersion());
    }

    public Dependency getGraph() {
        if (rootNode == null) {
            throw new IllegalStateException("Rootnode is null");
        }
        return rootNode;
    }
}
