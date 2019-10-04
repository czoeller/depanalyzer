package de.czoeller.depanalyzer.ui.components;

import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;

import java.awt.*;
import java.util.function.Function;

import static de.czoeller.depanalyzer.metamodel.NodeResolution.PARENT;

public class EdgeStrokeTransformator implements Function<GraphDependencyEdge, Stroke> {

    @Override
    public Stroke apply(GraphDependencyEdge edge) {
        float dash[] = {10.0f};
        float dash_parent[] = {15.0f};
        final BasicStroke basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        final BasicStroke parentStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_parent, 0.0f);

        return edge.getSource().getResolution() == PARENT ? parentStroke : basicStroke;
    }
}