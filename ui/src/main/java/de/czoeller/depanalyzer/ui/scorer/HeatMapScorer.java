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
