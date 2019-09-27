package de.czoeller.depanalyzer.core.dependency;

import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.input.resolver.DependencyNodeResolver;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.OrArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyCollectionException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.eclipse.aether.util.graph.transformer.ConflictResolver.CONFIG_PROP_VERBOSE;

/**
 * Adapter for Aether's dependency graph.
 */
public final class MavenGraphAdapter {

    private final DependencyNodeResolver dependencyNodeResolver;

    public MavenGraphAdapter(DependencyNodeResolver dependencyNodeResolver) {
        this.dependencyNodeResolver = dependencyNodeResolver;
    }

    public org.eclipse.aether.graph.DependencyNode buildDependencyGraph(MavenProject project, GraphBuilder<DependencyNode> graphBuilder) {
        DefaultDependencyResolutionRequest request = new DefaultDependencyResolutionRequest();
        request.setMavenProject(project);
        //request.setRepositorySession(getVerboseRepositorySession(project));

        try {
            org.eclipse.aether.graph.DependencyNode root = this.dependencyNodeResolver.resolve(request);
            ArtifactFilter transitiveDependencyFilter = createTransitiveDependencyFilter(project);

            GraphBuildingVisitor visitor = new GraphBuildingVisitor(graphBuilder);
            root.accept(visitor);

            return root;
        } catch (DependencyCollectionException e) {
            throw new DependencyGraphException(e);
        }
    }

    private static RepositorySystemSession getVerboseRepositorySession(MavenProject project) {
        @SuppressWarnings("deprecation")
        RepositorySystemSession repositorySession = project.getProjectBuildingRequest().getRepositorySession();
        DefaultRepositorySystemSession verboseRepositorySession = new DefaultRepositorySystemSession(repositorySession);
        verboseRepositorySession.setConfigProperty(CONFIG_PROP_VERBOSE, "true");
        verboseRepositorySession.setReadOnly();
        repositorySession = verboseRepositorySession;
        return repositorySession;
    }

    private ArtifactFilter createTransitiveDependencyFilter(MavenProject project) {
        List<String> dependencyKeys = new ArrayList<>(project.getDependencies().size());
        for (Dependency dependency : project.getDependencies()) {
            dependencyKeys.add(dependency.getManagementKey());
        }

        // Matches direct dependencies or the configured transitive dependencies or the project itself
        OrArtifactFilter artifactFilter = new OrArtifactFilter();
        //TODO: artifactFilter.add(this.transitiveIncludeExcludeFilter);
        artifactFilter.add(new StrictPatternIncludesArtifactFilter(dependencyKeys));
        artifactFilter.add(new StrictPatternIncludesArtifactFilter(singletonList(project.getArtifact().toString())));

        return artifactFilter;
    }
}
