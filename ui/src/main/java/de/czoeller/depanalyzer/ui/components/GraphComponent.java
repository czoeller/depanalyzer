package de.czoeller.depanalyzer.ui.components;

import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.UIModel;
import de.czoeller.depanalyzer.ui.model.UIModel.Layouts;
import de.czoeller.depanalyzer.ui.scorer.HeatMapScorer;
import de.czoeller.depanalyzer.ui.scorer.ScoreToHeatTransformer;
import edu.uci.ics.jung.layout.algorithms.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.layout.LayoutAlgorithmTransition;
import edu.uci.ics.jung.visualization.renderers.BasicNodeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GraphComponent extends JComponent {
    private final UIModel model;

    /** the visual component and renderer for the graph */
    private VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv;

    public GraphComponent(UIModel model) {
        this.model = model;
        setPreferredSize(new Dimension(600, 300));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        vv = new VisualizationViewer<>(model.getGraph(), new Dimension(600, 300));

        HeatMapScorer<GraphDependencyNode, GraphDependencyEdge> heatMapScorer = new HeatMapScorer<>(model.getGraph());
        ScoreToHeatTransformer<GraphDependencyNode, GraphDependencyEdge> nodeFillHeatmapTransformer = new ScoreToHeatTransformer<>(heatMapScorer);

        vv.getRenderContext().setNodeFillPaintFunction(nodeFillHeatmapTransformer);
        vv.getRenderContext().setEdgeDrawPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setArrowFillPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setArrowDrawPaintFunction(e -> Color.lightGray);
        vv.setNodeToolTipFunction(node -> "<html><h1>" + node.getId() +"</h1>" + node.getArtifact().toString() + "<br />heat: " + node.getHeat() +"</html>");

        vv.getRenderContext().setNodeLabelFunction(n -> n.getId().toString());
        vv.getRenderer().getNodeLabelRenderer().setPositioner(new BasicNodeLabelRenderer.InsidePositioner());
        vv.getRenderer().getNodeLabelRenderer().setPosition(Renderer.NodeLabel.Position.AUTO);

        vv.getRenderContext().setNodeDrawPaintFunction(v -> vv.getPickedNodeState().isPicked(v) ? Color.CYAN : Color.BLACK);
        vv.getPickedNodeState().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                    System.out.println("selected " + e.getItem());
            }
        });

        final DefaultModalGraphMouse<Number, Number> gm = new DefaultModalGraphMouse<>();
        gm.setMode(ModalGraphMouse.Mode.PICKING);

        vv.setGraphMouse(gm);
        add(vv);
    }

    public void modelUpdated(Layouts layoutType) {
        //initComponents();
        //revalidate();
        LayoutAlgorithmTransition.animate(vv, createLayout(layoutType));
    }

    private static LayoutAlgorithm<GraphDependencyNode> createLayout(Layouts layoutType) {
        switch (layoutType) {
            case CIRCLE:
                return new CircleLayoutAlgorithm<>();
            case DIRECTED_ACYCLIC_GRAPH:
                return new DAGLayoutAlgorithm<>();
            case FR:
                return new FRLayoutAlgorithm<>();
            case KK:
                return new KKLayoutAlgorithm<>();
            case SELF_ORGANIZING_MAP:
                return new ISOMLayoutAlgorithm<>();
            case SPRING:
                return new SpringLayoutAlgorithm<>();
            case FR_BH_VISITOR:
                return new FRBHVisitorLayoutAlgorithm<>();
            case SPRING_BH_VISITOR:
                return new SpringBHVisitorLayoutAlgorithm<>();
            default:
                throw new IllegalArgumentException("Unrecognized layout type");
        }
    }
}
