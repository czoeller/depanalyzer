package de.czoeller.depanalyzer.ui.scorer;

import com.google.common.graph.Network;
import de.czoeller.depanalyzer.ui.model.HasHeat;
import edu.uci.ics.jung.algorithms.scoring.AbstractIterativeScorer;
import edu.uci.ics.jung.algorithms.scoring.NodeScorer;

public class HeatMapScorer<N extends HasHeat, E> extends AbstractIterativeScorer<N, E, Double> implements NodeScorer<N, Double> {

    public HeatMapScorer(Network<N, E> g) {
        super(g);
        initialize();
    }

    @Override
    protected double update(N v) {
        setOutputValue(v, v.getHeat());
        return 0.0;
    }
}
