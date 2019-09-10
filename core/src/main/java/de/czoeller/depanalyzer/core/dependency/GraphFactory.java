package de.czoeller.depanalyzer.core.dependency;

import org.apache.maven.project.MavenProject;

public interface GraphFactory {
    /**
     * Creates a graph for the given {@link MavenProject}.
     *
     * @param project The maven project to create the graph for.
     * @return The String representation of the created graph.
     * @throws DependencyGraphException In case that the graph cannot be created.
     */
    String createGraph(MavenProject project);
}
