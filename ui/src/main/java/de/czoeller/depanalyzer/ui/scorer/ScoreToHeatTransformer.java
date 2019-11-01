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
package de.czoeller.depanalyzer.ui.scorer;

import de.czoeller.depanalyzer.ui.ColorScheme;
import de.czoeller.depanalyzer.ui.model.HasHeat;

import java.awt.*;
import java.util.function.Function;

public class ScoreToHeatTransformer<N extends HasHeat, E> implements Function<N, Paint> {

    private final HeatMapScorer<N, E> vs;

    /**
     * Creates an instance based on the specified NodeScorer. Maps
     *
     * @param vs the NodeScorer which will retrieve the score for each node
     */
    public ScoreToHeatTransformer(HeatMapScorer<N, E> vs) {
        this.vs = vs;
    }

    @Override
    public Paint apply(N v) {
        final int colorIndex = Math.min(99, vs.getNodeScore(v).intValue());
        return ColorScheme.NODE.HEAT_COLOR_GRADIENT[colorIndex];
    }
}
