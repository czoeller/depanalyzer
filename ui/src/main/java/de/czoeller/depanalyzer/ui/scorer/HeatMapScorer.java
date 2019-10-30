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

import com.google.common.collect.Maps;
import com.google.common.graph.Network;
import de.czoeller.depanalyzer.ui.model.HasHeat;
import edu.uci.ics.jung.algorithms.scoring.NodeScorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class HeatMapScorer<N extends HasHeat, E> implements NodeScorer<N, Double> {

    /** The graph for which scores are to be generated. */
    protected Network<N, ?> graph;
    protected Map<N, Double> map;

    public HeatMapScorer(Network<N, ?> graph) {
        this.graph = graph;
        init();
    }

    protected void init() {
        map = Maps.asMap(graph.nodes(), HasHeat::getHeat);

        final double max = Collections.max(map.entrySet(), Comparator.comparing(Map.Entry::getValue)).getValue();
        final double min = Collections.min(map.entrySet(), Comparator.comparing(Map.Entry::getValue)).getValue();

        map = Maps.asMap(map.keySet(), n -> normalizedHeat(n.getHeat(), min, max));
    }

    private double normalizedHeat(double heat, double min, double max) {
        return ((heat - min) / (max - min)) * 100;
    }

    @Override
    public Double getNodeScore(N n) {
        init();
        return map.get(n);
    }

    @Override
    public Map<N, Double> nodeScores() {
        init();
        return map;
    }
}
