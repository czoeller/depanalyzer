package de.czoeller.depanalyzer.ui.components;

import com.google.common.graph.EndpointPair;
import de.czoeller.depanalyzer.ui.animation.NodeFillHighlightAnimation;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.UIModel;
import de.czoeller.depanalyzer.ui.model.UIModel.Layouts;
import de.czoeller.depanalyzer.ui.scorer.HeatMapScorer;
import de.czoeller.depanalyzer.ui.scorer.ScoreToHeatTransformer;
import edu.uci.ics.jung.layout.algorithms.*;
import edu.uci.ics.jung.layout.model.Point;
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
import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.TimerTask;
import java.util.stream.Collectors;

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
        vvs.getRenderContext().setNodeFillPaintFunction(nodeFillHeatmapTransformer);

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

        // Edge animation
        AnimationTimerTask at = new AnimationTimerTask(vvs);
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(at, 10, 30);

        add(panel);
    }

    public void modelUpdated(Layouts layoutType) {
        //initComponents();
        //revalidate();
        LayoutAlgorithmTransition.animate(vv, createLayout(layoutType));
    }

    public void modelUpdated(String search) {
        val nodes = model.getGraph().nodes().stream().filter(n -> n.getArtifact().getArtifactId().contains(search)).collect(Collectors.toList());
        new NodeFillHighlightAnimation<>(vv, nodes, Color.yellow);
        new NodeFillHighlightAnimation<>(vvs, nodes, Color.yellow);
    }

    public void modelUpdated(UIModel.Analyzers analyzer) {
        System.out.println("Analyzer set to " + analyzer);
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

    class AnimationTimerTask extends TimerTask {

        private final double width = 0.123; // Size of the colored line.
        private final double stepsize = 0.01;
        private double keyframe = 0 + width; // Between 0.0 and 1.0
        private VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv;

        public AnimationTimerTask(VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv) {
            this.vv = vv;
        }

        @Override
        public void run() {
            vv.getRenderContext()
              .setEdgeDrawPaintFunction(edge -> {

                  // Find both points of the edge.
                  EndpointPair<GraphDependencyNode> pair = model.getGraph()
                                                                .incidentNodes(edge);


                  final Point p1_f = vv.getModel()
                                       .getLayoutModel()
                                       .get(pair.source());

                  final Point p2_f = vv.getModel()
                                       .getLayoutModel()
                                       .get(pair.target());

                  final Point2D.Double p1d = new Point2D.Double(p1_f.x, p1_f.y);
                  final Point2D.Double p2d = new Point2D.Double(p2_f.x, p2_f.y);
                  final Point2D p1 = vv.getTransformSupport()
                                       .transform(vv, p1d);
                  final Point2D p2 = vv.getTransformSupport()
                                       .transform(vv, p2d);

                  // This code won't handle self-edges.
                  if (p1.equals(p2)) {
                      return Color.red;
                  }

                  Color[] colors = {Color.gray, Color.red, Color.gray};
                  float start = (float) Math.max(0.0, keyframe - width);
                  float end = (float) Math.min(1.0, keyframe + width);
                  float[] fractions = {start, (float) keyframe, end};
                  return new LinearGradientPaint(p1, p2, fractions, colors);
              });
            vv.repaint();
            keyframe += stepsize;
            keyframe %= 1.0;
        }
    }

}
