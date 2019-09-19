package de.czoeller.depanalyzer.core.dependency;

import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.Collection;
import java.util.function.Supplier;

public class AggregatingGraphFactory implements GraphFactory {

    private final MavenGraphAdapter mavenGraphAdapter;
    private final Supplier<Collection<MavenProject>> subProjectSupplier;
    private final GraphBuilder<DependencyNode> graphBuilder;
    private final boolean includeParentProjects;
    private final boolean reduceEdges;

    public AggregatingGraphFactory(
            MavenGraphAdapter mavenGraphAdapter,
            Supplier<Collection<MavenProject>> subProjectSupplier,
            GraphBuilder<DependencyNode> graphBuilder,
            boolean includeParentProjects,
            boolean reduceEdges) {
        this.mavenGraphAdapter = mavenGraphAdapter;
        this.subProjectSupplier = subProjectSupplier;
        this.graphBuilder = graphBuilder;
        this.includeParentProjects = includeParentProjects;
        this.reduceEdges = reduceEdges;
    }

    @Override
    public String createGraph(MavenProject parent) {
        this.graphBuilder.graphName(parent.getArtifactId());

        if (this.includeParentProjects) {
            buildModuleTree(parent, this.graphBuilder);
        }

        Collection<MavenProject> collectedProjects = this.subProjectSupplier.get();
        for (MavenProject collectedProject : collectedProjects) {
            // Process project only if its artifact is not filtered
            if (isPartOfGraph(collectedProject)) {
                this.mavenGraphAdapter.buildDependencyGraph(collectedProject, this.graphBuilder);
            }
        }

        // Add the project as single node if the graph is empty
        Artifact artifact = parent.getArtifact();
        if (this.graphBuilder.isEmpty()) {
            this.graphBuilder.addNode(new DependencyNode(artifact));
        }

        if (this.reduceEdges) {
            this.graphBuilder.reduceEdges();
        }

        return this.graphBuilder.toString();
    }

    private void buildModuleTree(MavenProject parentProject, GraphBuilder<DependencyNode> graphBuilder) {
        Collection<MavenProject> collectedProjects = parentProject.getCollectedProjects();
        for (MavenProject collectedProject : collectedProjects) {
            MavenProject child = collectedProject;
            MavenProject parent = collectedProject.getParent();

            while (parent != null) {
                DependencyNode parentNode = filterProject(parent);
                DependencyNode childNode = filterProject(child);

                graphBuilder.addEdge(parentNode, childNode);

                // Stop if we reached the original parent project!
                if (parent.equals(parentProject)) {
                    break;
                }

                child = parent;
                parent = parent.getParent();
            }
        }
    }

    private boolean isPartOfGraph(MavenProject project) {

        return true;
        /*boolean isIncluded = true;
        // Project is not filtered and is a parent project
        if (isIncluded && project.getModules().size() > 0) {
            return this.includeParentProjects;
        }

        return isIncluded;*/
    }

    private DependencyNode filterProject(MavenProject project) {
        Artifact artifact = project.getArtifact();
        final DependencyNode dependencyNode = new DependencyNode(artifact);
        dependencyNode.setParent();
        return dependencyNode;
    }

}