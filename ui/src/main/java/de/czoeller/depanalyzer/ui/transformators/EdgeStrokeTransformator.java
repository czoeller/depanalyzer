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

import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class EdgeStrokeTransformator extends TraceBasedTransformator implements Function<GraphDependencyEdge, Stroke> {

    protected final Stroke THIN = new BasicStroke(1);
    protected final Stroke THICK = new BasicStroke(2);

    public EdgeStrokeTransformator(Map<GraphDependencyNode, Set<GraphDependencyNode>> paths, BiPredicate<GraphDependencyEdge, Set<GraphDependencyNode>> inPath) {
        super(paths, inPath);
    }

    @Override
    public Stroke apply(GraphDependencyEdge edge) {
        if(isInPath(edge)) {
            return THICK;
        } else {
            return THIN;
        }
    }

}
