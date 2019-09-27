package de.czoeller.depanalyzer.metamodel.visitor;

import de.czoeller.depanalyzer.metamodel.DependencyNode;

/**
 * A visitor for nodes of the dependency graph.
 *
 * @see DependencyNode#accept(ModelDependencyNodeVisitor)
 */
public interface ModelDependencyNodeVisitor {

    /**
     * Notifies the visitor of a node visit before its children have been processed.
     *
     * @param node The dependency node being visited, must not be {@code null}.
     * @return {@code true} to visit child nodes of the specified node as well, {@code false} to skip children.
     */
    boolean visitEnter(DependencyNode node);

    /**
     * Notifies the visitor of a node visit after its children have been processed. Note that this method is always
     * invoked regardless whether any children have actually been visited.
     *
     * @param node The dependency node being visited, must not be {@code null}.
     * @return {@code true} to visit siblings nodes of the specified node as well, {@code false} to skip siblings.
     */
    boolean visitLeave(DependencyNode node);

}