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
package de.czoeller.depanalyzer.ui.swingwrapper;

import com.google.common.graph.Network;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;

import java.util.function.Function;

public class HeatBasedLabelProvider implements Function<GraphDependencyNode, String> {

    private final Network<GraphDependencyNode, GraphDependencyEdge> graph;

    public HeatBasedLabelProvider(Network<GraphDependencyNode, GraphDependencyEdge> graph) {
        this.graph = graph;
    }

    private double calculateTotalHeat() {
        return graph.nodes()
             .stream()
             .map(GraphDependencyNode::getHeat)
             .mapToDouble(Double::doubleValue)
             .sum();
    }

    @Override
    public String apply(GraphDependencyNode graphDependencyNode) {

        if(graphDependencyNode.getHeat() > (0.10 * calculateTotalHeat())) {
            return graphDependencyNode.getArtifact().getArtifactId();
        }

        return "";
    }
}
