package de.czoeller.depanalyzer.core.dependency;

import de.czoeller.depanalyzer.core.graph.GraphBuilder;

/**
 * API to configure the style of the dependency graph.
 */
public interface GraphStyleConfigurer {

    GraphStyleConfigurer showGroupIds(boolean showGroupId);

    GraphStyleConfigurer showArtifactIds(boolean showArtifactId);

    GraphStyleConfigurer showTypes(boolean showTypes);

    GraphStyleConfigurer showClassifiers(boolean showClassifiers);

    GraphStyleConfigurer showVersionsOnNodes(boolean showVersionsOnNodes);

    GraphStyleConfigurer showVersionsOnEdges(boolean showVersionOnEdges);

    GraphStyleConfigurer showOptional(boolean optional);

    GraphStyleConfigurer repeatTransitiveDependencies(boolean repeatTransitiveDependencies);

    GraphBuilder<DependencyNode> configure(GraphBuilder<DependencyNode> graphBuilder);
}
