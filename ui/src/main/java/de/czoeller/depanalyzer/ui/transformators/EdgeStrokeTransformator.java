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

import de.czoeller.depanalyzer.metamodel.NodeResolution;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;

import java.awt.*;
import java.util.function.Function;

public class EdgeStrokeTransformator implements Function<GraphDependencyEdge, Stroke> {

    @Override
    public Stroke apply(GraphDependencyEdge edge) {
        float dash[] = {10.0f};
        float dash_parent[] = {15.0f};
        final BasicStroke basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        final BasicStroke parentStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_parent, 0.0f);

        if (edge.getSource().getResolution() == NodeResolution.PARENT) {
            return parentStroke;
        }
        return basicStroke;
    }
}