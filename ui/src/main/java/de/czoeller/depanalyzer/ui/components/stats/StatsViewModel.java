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
package de.czoeller.depanalyzer.ui.components.stats;

import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.MainModel;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Optional;

@Slf4j
class StatsViewModel {

    private ReadOnlyLongWrapper nrNodesProperty = new ReadOnlyLongWrapper();
    private ReadOnlyLongWrapper nrProjectNodesProperty = new ReadOnlyLongWrapper();
    private ReadOnlyLongWrapper nrNodesWithIssueProperty = new ReadOnlyLongWrapper();
    private ReadOnlyStringWrapper deepestNodeProperty = new ReadOnlyStringWrapper();

    StatsViewModel(MainModel model) {

        final long nrNodes = model.getGraph()
                                  .nodes()
                                  .size();
        final long nrProjectNodes = model.getGraph()
                                         .nodes()
                                         .stream()
                                         .filter(GraphDependencyNode::isProjectNode)
                                         .count();
        final long nrIssueNodes = model.getGraph()
                                          .nodes()
                                          .stream()
                                          .filter(n -> !n.getIssues().isEmpty())
                                          .count();
        final Optional<GraphDependencyNode> maxDepth = model.getGraph()
                                                       .nodes()
                                                       .stream()
                                                       .max(Comparator.comparing(GraphDependencyNode::getDepth));
        log.info("Nr nodes: {}", nrNodes);
        log.info("Project nodes: {}", nrProjectNodes);
        log.info("Nr issue nodes: {}", nrIssueNodes);

        final String deepestNode = String.format("%d (%s)", maxDepth.get().getDepth(), maxDepth.get().getIdentifier());
        log.info("Deepest node: ", maxDepth.get().getPrimaryLevel() + deepestNode);

        nrNodesProperty.set(nrNodes);
        nrProjectNodesProperty.set(nrProjectNodes);
        nrNodesWithIssueProperty.set(nrIssueNodes);
        deepestNodeProperty.set(deepestNode);
    }

    ReadOnlyLongProperty nrNodesProperty() {
        return nrNodesProperty.getReadOnlyProperty();
    }

    ReadOnlyLongProperty nrProjectNodesProperty() {
        return nrProjectNodesProperty.getReadOnlyProperty();
    }

    ReadOnlyLongProperty nrNodesWithIssueProperty() {
        return nrNodesWithIssueProperty.getReadOnlyProperty();
    }

    ReadOnlyStringProperty deepestNodeProperty() {
        return deepestNodeProperty.getReadOnlyProperty();
    }
}
