package de.czoeller.depanalyzer.core.dependency;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.czoeller.depanalyzer.core.dependency.NodeResolution.*;

/**
 * Representation of a dependency graph node. It adapts these Maven-specific classes:
 * <ul>
 * <li>{@link org.apache.maven.artifact.Artifact}</li>
 * <li>{@link org.eclipse.aether.graph.DependencyNode}</li>
 * </ul>
 */
public final class DependencyNode {

    private final Artifact artifact;
    private final String effectiveVersion;
    private final NodeResolution resolution;
    private final Set<String> scopes;
    private final Set<String> classifiers;
    private final Set<String> types;


    public DependencyNode(Artifact artifact) {
        this(artifact, determineNodeResolution(artifact), artifact.getVersion());
    }

    public DependencyNode(org.eclipse.aether.graph.DependencyNode dependencyNode) {
        this(createMavenArtifact(dependencyNode), determineResolution(dependencyNode), determineEffectiveVersion(dependencyNode));
    }

    private DependencyNode(Artifact artifact, NodeResolution resolution, String effectiveVersion) {
        if (artifact == null) {
            throw new NullPointerException("Artifact must not be null");
        }

        // FIXME: better create a copy of the artifact and set the missing attributes there.
        if (artifact.getScope() == null) {
            artifact.setScope("compile");
        }

        this.effectiveVersion = effectiveVersion;
        this.scopes = new TreeSet<>();
        this.classifiers = new TreeSet<>();
        this.types = new TreeSet<>();
        this.artifact = artifact;
        this.resolution = resolution;
        this.scopes.add(artifact.getScope());
        this.types.add(artifact.getType());

        if (!isNullOrEmpty(artifact.getClassifier())) {
            this.classifiers.add(artifact.getClassifier());
        }
    }

    public void merge(DependencyNode other) {
        if (this == other) {
            return;
        }

        if (this.artifact.isOptional()) {
            this.artifact.setOptional(other.getArtifact().isOptional());
        }
        this.scopes.addAll(other.scopes);
        this.classifiers.addAll(other.classifiers);
        this.types.addAll(other.types);
    }

    public Artifact getArtifact() {
        return this.artifact;
    }

    public NodeResolution getResolution() {
        return this.resolution;
    }

    public Set<String> getScopes() {
        return ImmutableSet.copyOf(this.scopes);
    }

    public Set<String> getClassifiers() {
        return ImmutableSet.copyOf(this.classifiers);
    }

    public Set<String> getTypes() {
        return ImmutableSet.copyOf(this.types);
    }


    /**
     * Returns the <strong>effective</strong> version of this node, i.e. the version that is actually used. This is
     * important for nodes with a resolution of {@link NodeResolution#OMITTED_FOR_CONFLICT} where
     * {@code getArtifact().getVersion()} will return the omitted version.
     *
     * @return The effective version of this node.
     */
    public String getEffectiveVersion() {
        return this.effectiveVersion;
    }

    /**
     * Returns the <strong>effective</strong> scope of this node, i.e. the scope that is actually used. This is important
     * if scopes are merged and a node may have more than one scope.
     *
     * @return The effective scope of this node.
     */
    public String getEffectiveScope() {
        if (this.scopes.size() > 0) {
            return this.scopes.iterator().next();
        }

        // should never happen
        return null;
    }


    @Override
    public String toString() {
        return this.artifact.toString();
    }

    private static Artifact createMavenArtifact(org.eclipse.aether.graph.DependencyNode dependencyNode) {
        org.eclipse.aether.artifact.Artifact artifact = dependencyNode.getArtifact();
        String scope = null;
        boolean optional = false;
        if (dependencyNode.getDependency() != null) {
            scope = dependencyNode.getDependency().getScope();
            optional = dependencyNode.getDependency().isOptional();
        }

        DefaultArtifact mavenArtifact = new DefaultArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion(),
                scope,
                artifact.getProperty("type", artifact.getExtension()),
                artifact.getClassifier(),
                null
        );
        mavenArtifact.setOptional(optional);

        mavenArtifact.setFile(dependencyNode.getArtifact().getFile());

        return mavenArtifact;
    }

    private static NodeResolution determineResolution(org.eclipse.aether.graph.DependencyNode dependencyNode) {
        org.eclipse.aether.graph.DependencyNode winner = (org.eclipse.aether.graph.DependencyNode) dependencyNode.getData().get(
                ConflictResolver.NODE_DATA_WINNER);

        if (winner != null) {
            if (winner.getArtifact().getVersion().equals(dependencyNode.getArtifact().getVersion())) {
                return OMITTED_FOR_DUPLICATE;
            }

            return OMITTED_FOR_CONFLICT;
        }

        return INCLUDED;
    }

    private static NodeResolution determineNodeResolution(Artifact artifact) {
        if (artifact.getScope() == null) {
            return NodeResolution.PARENT;
        }

        return INCLUDED;
    }

    private static String determineEffectiveVersion(org.eclipse.aether.graph.DependencyNode dependencyNode) {
        org.eclipse.aether.graph.DependencyNode winner = (org.eclipse.aether.graph.DependencyNode) dependencyNode.getData().get(ConflictResolver.NODE_DATA_WINNER);
        if (winner != null) {
            return winner.getArtifact().getVersion();
        }

        return dependencyNode.getArtifact().getVersion();
    }
}
