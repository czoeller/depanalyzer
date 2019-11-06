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
package de.czoeller.depanalyzer.ui.transformators;

import de.czoeller.depanalyzer.ui.ColorScheme;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.*;
import java.util.function.Function;

public class NodeDrawPaintTransformator implements Function<GraphDependencyNode, Paint> {

    private final VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv;

    public NodeDrawPaintTransformator(VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv) {
        this.vv = vv;
    }

    @Override
    public Paint apply(GraphDependencyNode graphDependencyNode) {
        if(graphDependencyNode.isProjectNode()) {
            return vv.getPickedNodeState().isPicked(graphDependencyNode) ? ColorScheme.NODE.PROJECT_PICKED_CONTOUR_HL_COLOR : ColorScheme.NODE.PROJECT_CONTOUR;
        } else {
            return vv.getPickedNodeState().isPicked(graphDependencyNode) ? ColorScheme.NODE.PICKED_CONTOUR_HL_COLOR : ColorScheme.NODE.CONTOUR;
        }
    }
}
