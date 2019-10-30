/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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