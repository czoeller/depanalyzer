package de.czoeller.depanalyzer.metamodel;

import com.google.common.collect.ImmutableSet;
import de.czoeller.depanalyzer.metamodel.visitor.ModelDependencyNodeVisitor;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Representation of a dependency graph node. It adapts these Maven-specific classes:
 * <ul>
 * <li>{@link org.apache.maven.artifact.Artifact}</li>
 * <li>{@link org.eclipse.aether.graph.DependencyNode}</li>
 * </ul>
 */
public final class DependencyNode {

    private static boolean isParent;
    private final Artifact artifact;
    private final String effectiveVersion;
    private NodeResolution resolution;
    private final Set<String> scopes;
    private final Set<String> classifiers;
    private final Set<String> types;
    private List<DependencyNode> children;
    private List<Issue> issues;

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
        this.children = new ArrayList<>();

        if (!isNullOrEmpty(artifact.getClassifier())) {
            this.classifiers.add(artifact.getClassifier());
        }

        this.issues = new ArrayList<>();
    }

    public void setResolution(NodeResolution nodeResolution) {
        this.resolution = nodeResolution;
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
        this.children = other.getChildren();
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
                return NodeResolution.OMITTED_FOR_DUPLICATE;
            }

            return NodeResolution.OMITTED_FOR_CONFLICT;
        }

        return NodeResolution.INCLUDED;
    }

    private static NodeResolution determineNodeResolution(Artifact artifact) {
        if (isParent || artifact.getScope() == null) {
            return NodeResolution.PARENT;
        }

        return NodeResolution.INCLUDED;
    }

    private static String determineEffectiveVersion(org.eclipse.aether.graph.DependencyNode dependencyNode) {
        org.eclipse.aether.graph.DependencyNode winner = (org.eclipse.aether.graph.DependencyNode) dependencyNode.getData().get(ConflictResolver.NODE_DATA_WINNER);
        if (winner != null) {
            return winner.getArtifact().getVersion();
        }

        return dependencyNode.getArtifact().getVersion();
    }

    public List<DependencyNode> getChildren() {
        return children;
    }


    /**
     * Applies the specified dependency node visitor to this dependency node and its children.
     *
     * @param visitor the dependency node visitor to use
     * @return the visitor result of ending the visit to this node
     */
    public boolean accept( ModelDependencyNodeVisitor visitor ) {
        if ( visitor.visitEnter( this ) )
        {
            for ( DependencyNode child : children )
            {
                if ( !child.accept( visitor ) )
                {
                    break;
                }
            }
        }

        return visitor.visitLeave( this );
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public Stream<DependencyNode> flattened() {
        return Stream.concat(
                Stream.of(this),
                children.stream().flatMap(DependencyNode::flattened));
    }

    public String getIdentifier() {
        return String.format("%s:%s:%s:%s:%s", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getScope());
    }
}
