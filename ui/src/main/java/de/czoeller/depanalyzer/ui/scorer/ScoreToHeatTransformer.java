package de.czoeller.depanalyzer.ui.scorer;

import de.czoeller.depanalyzer.ui.model.HasHeat;

import java.awt.*;
import java.util.function.Function;

import static de.czoeller.depanalyzer.ui.util.GradientUtil.createMultiGradient;

public class ScoreToHeatTransformer<N extends HasHeat, E> implements Function<N, Paint> {

    private final HeatMapScorer<N, E> vs;

    private static final Color[] multiGradient = createMultiGradient(new Color[]{Color.black, new Color(105, 0, 0), new Color(192, 23, 0), new Color(255, 150, 38), Color.red}, 100);

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
        return multiGradient[colorIndex];
    }
}
