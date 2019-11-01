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
import de.czoeller.depanalyzer.ui.swingwrapper.GraphViewerWrapper;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class EdgeDrawPaintTransformator implements Function<GraphDependencyEdge, Paint> {

    private final GraphViewerWrapper gvw;

    public EdgeDrawPaintTransformator(GraphViewerWrapper graphViewerWrapper) {
        this.gvw = graphViewerWrapper;
    }

    @Override
    public Paint apply(GraphDependencyEdge edge) {
        for (Map.Entry<GraphDependencyNode, Set<GraphDependencyNode>> es : gvw.getMPreds().entrySet()) {
            if (gvw.isBlessed(es.getValue(), edge)) {
                return ColorScheme.EDGE.TRACE_HL_COLOR;
            } else {
                return ColorScheme.EDGE.COLOR;
            }
        }
        return ColorScheme.EDGE.COLOR;
    }
}
