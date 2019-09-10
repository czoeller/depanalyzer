package de.czoeller.depanalyzer.core.input.resolver;

import org.apache.maven.project.DependencyResolutionRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;

public interface DependencyNodeResolver {
    DependencyNode resolve(DependencyResolutionRequest request) throws DependencyCollectionException;
}
