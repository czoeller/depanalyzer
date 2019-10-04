package de.czoeller.depanalyzer.ui.scorer;

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
        vs.evaluate();
    }

    @Override
    public Paint apply(N v) {
        if (vs.getNodeScore(v) > 5) {
            return Color.red;
        }
        return Color.black;
    }
}
