package de.czoeller.depanalyzer.ui.components;

import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.UIModel;
import de.czoeller.depanalyzer.ui.model.UIModel.Layouts;
import de.czoeller.depanalyzer.ui.scorer.HeatMapScorer;
import de.czoeller.depanalyzer.ui.scorer.ScoreToHeatTransformer;
import edu.uci.ics.jung.layout.algorithms.*;
import edu.uci.ics.jung.visualization.BaseVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutAlgorithmTransition;
import edu.uci.ics.jung.visualization.renderers.BasicNodeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.swing.*;
import java.awt.*;

public class GraphComponent extends JComponent {
    private final UIModel model;

    /** the visual component and renderer for the graph */
    private VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv;
    /** the visual detail view */
    private DetailPane detailPane;
    /** the visual satellite view */
    private SatelliteVisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vvs;

    public GraphComponent(UIModel model) {
        this.model = model;
        setPreferredSize(new Dimension(600, 300));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        Dimension preferredSize1 = new Dimension(300, 300);
        Dimension preferredSize2 = new Dimension(300, 300);

        // create one model that both views will share
        VisualizationModel<GraphDependencyNode, GraphDependencyEdge> vm = new BaseVisualizationModel<GraphDependencyNode, GraphDependencyEdge>(model.getGraph(), createLayout(model.getSelectedLayout()), preferredSize1);

        // create 2 views that share the same model
        vv = new VisualizationViewer<>(vm, preferredSize1);
        detailPane = new DetailPane();
        vvs = new SatelliteVisualizationViewer<>(vv, preferredSize2);

        HeatMapScorer<GraphDependencyNode, GraphDependencyEdge> heatMapScorer = new HeatMapScorer<>(model.getGraph());
        ScoreToHeatTransformer<GraphDependencyNode, GraphDependencyEdge> nodeFillHeatmapTransformer = new ScoreToHeatTransformer<>(heatMapScorer);

        vv.getRenderContext().setNodeFillPaintFunction(nodeFillHeatmapTransformer);
        vv.getRenderContext().setEdgeDrawPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setEdgeStrokeFunction(new EdgeStrokeTransformator());
        vv.getRenderContext().setArrowFillPaintFunction(e -> Color.lightGray);
        vv.getRenderContext().setArrowDrawPaintFunction(e -> Color.lightGray);
        vv.setNodeToolTipFunction(node -> "<html><h1>" + node.getId() +"</h1>" + node.getArtifact().toString() + "<br />heat: " + node.getHeat() +"</html>");

        vv.getRenderContext().setNodeLabelFunction(n -> n.getId().toString());
        vv.getRenderer().getNodeLabelRenderer().setPositioner(new BasicNodeLabelRenderer.InsidePositioner());
        vv.getRenderer().getNodeLabelRenderer().setPosition(Renderer.NodeLabel.Position.AUTO);

        vv.getRenderContext().setNodeDrawPaintFunction(v -> vv.getPickedNodeState().isPicked(v) ? Color.CYAN : Color.BLACK);
        vv.getPickedNodeState().addItemListener(detailPane);

        final DefaultModalGraphMouse<Number, Number> gm = new DefaultModalGraphMouse<>();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(gm);

        Container panel = new JPanel(new BorderLayout());
        Container rightPanel = new JPanel(new GridLayout(2, 1));

        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        panel.add(gzsp);
        rightPanel.add(detailPane.$$$getRootComponent$$$());
        rightPanel.add(vvs);
        panel.add(rightPanel, BorderLayout.EAST);

        add(panel);
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
