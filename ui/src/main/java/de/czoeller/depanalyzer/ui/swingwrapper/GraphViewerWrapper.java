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
package de.czoeller.depanalyzer.ui.swingwrapper;

import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Network;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.ui.ColorScheme;
import de.czoeller.depanalyzer.ui.animation.NodeFillHighlightAnimation;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.Layouts;
import de.czoeller.depanalyzer.ui.model.MainModel;
import de.czoeller.depanalyzer.ui.scorer.HeatMapScorer;
import de.czoeller.depanalyzer.ui.scorer.ScoreToHeatTransformer;
import de.czoeller.depanalyzer.ui.transformators.*;
import de.czoeller.depanalyzer.ui.transformators.EdgeStrokeTransformator;
import de.czoeller.depanalyzer.ui.transformators.NodeDrawPaintTransformator;
import de.czoeller.depanalyzer.ui.transformators.NodeStrokeTransformator;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.layout.algorithms.*;
import edu.uci.ics.jung.layout.model.Point;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutAlgorithmTransition;
import edu.uci.ics.jung.visualization.renderers.BasicNodeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Slf4j
public class GraphViewerWrapper {

    private final MainModel model;
    private final SwingNode swingNodeViewer;
    private final SwingNode swingNodeSatelliteViewer;
    private VisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vv;
    private SatelliteVisualizationViewer<GraphDependencyNode, GraphDependencyEdge> vvs;
    private final ObjectProperty<GraphDependencyNode> selectedNodeProperty = new SimpleObjectProperty<>();
    /** Holds paths for selected node to all the project nodes */
    @Getter
    private final Map<GraphDependencyNode, Set<GraphDependencyNode>> pathsForProjectNodes = new HashMap<>();
    private final BiPredicate<GraphDependencyEdge, Set<GraphDependencyNode>> areEndpointsOfThisEdgeInThePath = (e, predecessors) -> {
        EndpointPair<GraphDependencyNode> endpoints = vv.getModel().getNetwork().incidentNodes(e);
        GraphDependencyNode v1 = endpoints.nodeU();
        GraphDependencyNode v2 = endpoints.nodeV();
        return !v1.equals(v2) && predecessors.contains(v1) && predecessors.contains(v2);
    };

    public GraphViewerWrapper(MainModel model, SwingNode swingNodeViewer, SwingNode swingNodeSatelliteViewer) {
        this.model = model;
        this.swingNodeViewer = swingNodeViewer;
        this.swingNodeSatelliteViewer = swingNodeSatelliteViewer;
        init();
    }

    private void init() {
        try {
            SwingUtilities.invokeAndWait(this::run);
        } catch (InterruptedException | InvocationTargetException e) {
            log.warn("Exception in GraphViewerWrapper", e);
        }
    }

    private void run() {
        LayoutAlgorithm<GraphDependencyNode> layout = new CircleLayoutAlgorithm<>();

        final Dimension vvDimension = new Dimension(400, 480);
        final Dimension vvsDimension = new Dimension(((int) (vvDimension.getWidth() * 0.5)), ((int) (vvDimension.getHeight() * 0.5)));

        vv = new VisualizationViewer<>(model.getGraph(), layout, vvDimension);
        vvs = new SatelliteVisualizationViewer<>(vv, vvsDimension);
        vv.setBackground(ColorScheme.BG_COLOR);
        vvs.setBackground(ColorScheme.BG_COLOR);
        final HeatMapScorer<GraphDependencyNode, GraphDependencyEdge> heatMapScorer = new HeatMapScorer<>(model.getGraph());
        final ScoreToHeatTransformer<GraphDependencyNode, GraphDependencyEdge> nodeFillHeatmapTransformer = new ScoreToHeatTransformer<>(heatMapScorer);
        final NodeStrokeTransformator nodeStrokeTransformator = new NodeStrokeTransformator();
        final NodeDrawPaintTransformator nodeDrawPaintTransformator = new NodeDrawPaintTransformator(vv);

        vv.getRenderContext()
          .setNodeFillPaintFunction(nodeFillHeatmapTransformer);
        vvs.getRenderContext()
           .setNodeFillPaintFunction(nodeFillHeatmapTransformer);
        vv.getRenderContext()
          .setNodeStrokeFunction(nodeStrokeTransformator);
        vvs.getRenderContext()
           .setNodeStrokeFunction(nodeStrokeTransformator);
        vv.getRenderContext()
          .setEdgeDrawPaintFunction(e -> ColorScheme.EDGE.COLOR);
        vv.getRenderContext()
          .setEdgeStrokeFunction(new EdgeStrokeTransformator(pathsForProjectNodes, areEndpointsOfThisEdgeInThePath));
        vv.getRenderContext()
          .setEdgeDrawPaintFunction(new EdgeDrawPaintTransformator(pathsForProjectNodes, areEndpointsOfThisEdgeInThePath));
        vv.getRenderContext()
          .setArrowFillPaintFunction(e -> ColorScheme.EDGE.ARROW_COLOR);
        vv.getRenderContext()
          .setArrowDrawPaintFunction(e -> ColorScheme.EDGE.ARROW_CONTOUR_COLOR);
        vv.setNodeToolTipFunction(node -> "<html><h1>" + node.getId() + "</h1>" + node.getArtifact().toString() + "<br />heat: " + node.getHeat() + "</html>");
        vv.getRenderContext()
          .setNodeLabelFunction(n -> n.getId()
                                      .toString());
        vv.getRenderer()
          .getNodeLabelRenderer()
          .setPositioner(new BasicNodeLabelRenderer.InsidePositioner());
        vv.getRenderer()
          .getNodeLabelRenderer()
          .setPosition(Renderer.NodeLabel.Position.AUTO);

        vv.getRenderContext()
          .setNodeDrawPaintFunction(nodeDrawPaintTransformator);
        vvs.getRenderContext()
           .setNodeDrawPaintFunction(nodeDrawPaintTransformator);
        vv.getPickedNodeState()
          .addItemListener(e -> {
              GraphDependencyNode pickedNode = (GraphDependencyNode) e.getItem();
              SwingUtilities.invokeLater(() -> updatePath(pickedNode));
              Platform.runLater(() -> selectedNodeProperty.set(pickedNode));
          });

        final DefaultModalGraphMouse<GraphDependencyNode, GraphDependencyEdge> gm = new DefaultModalGraphMouse<>();
        gm.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(gm);

        vv.setPreferredSize(vvDimension);
        vvs.setPreferredSize(vvsDimension);
        swingNodeViewer.setContent(vv);
        swingNodeSatelliteViewer.setContent(vvs);

        // Edge animation
        AnimationTimerTask at = new AnimationTimerTask(vvs);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(at, 10, 30);
    }

    private void updatePath(GraphDependencyNode pickedNode) {

        final List<GraphDependencyNode> projectNodes = vv.getModel()
                                                    .getNetwork()
                                                    .nodes()
                                                    .stream()
                                                    .filter(GraphDependencyNode::isProjectNode)
                                                    .collect(Collectors.toList());

        for (GraphDependencyNode projectNode : projectNodes) {
            pathsForProjectNodes.remove(pickedNode);
            pathsForProjectNodes.put(projectNode, computePred(projectNode, pickedNode));
        }

    }

    private Set<GraphDependencyNode> computePred(GraphDependencyNode projectNode, GraphDependencyNode pickedNode) {
        GraphDependencyNode mFrom = projectNode;
        GraphDependencyNode mTo = pickedNode;
        Network<GraphDependencyNode, GraphDependencyEdge> mGraph = vv.getModel().getNetwork();

        if (mFrom == null || mTo == null) {
            return Sets.newHashSet();
        }
        BFSDistanceLabeler<GraphDependencyNode> bdl = new BFSDistanceLabeler<>();
        bdl.labelDistances(mGraph.asGraph(), mFrom);
        Set<GraphDependencyNode> currentPred = new HashSet<>();

        // grab a predecessor
        GraphDependencyNode v = mTo;
        Set<GraphDependencyNode> prd = bdl.getPredecessors(v);
        currentPred.add(mTo);
        while (prd != null && prd.size() > 0) {
            v = prd.iterator().next();
            currentPred.add(v);
            if (v.isProjectNode() || v.equals(mFrom)) {
                break;
            }
            prd = bdl.getPredecessors(v);
        }

        return currentPred;
    }

    public GraphDependencyNode getSelectedNodeProperty() {
        return selectedNodeProperty.get();
    }

    public ObjectProperty<GraphDependencyNode> selectedNodePropertyProperty() {
        return selectedNodeProperty;
    }

    public void setPickMode() {
        ((ModalGraphMouse) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.PICKING);
    }

    public void setTransformMode() {
        ((ModalGraphMouse) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.TRANSFORMING);
    }

    public void setSelectedLayout(Layouts layoutType) {
        LayoutAlgorithmTransition.animate(vv, createLayout(layoutType));
    }

    public void setSearch(String search) {
        final List<GraphDependencyNode> nodes = model.getGraph().nodes().stream().filter(n -> n.getArtifact().getArtifactId().contains(search)).collect(Collectors.toList());
        new NodeFillHighlightAnimation<>(vv, nodes, ColorScheme.EDGE.SEARCH_HL_COLOR);
        new NodeFillHighlightAnimation<>(vvs, nodes, ColorScheme.EDGE.SEARCH_HL_COLOR);
    }

    public void setAnalyzerResults(Analyzers analyzer) {
        log.debug("Analyzer set to " + analyzer);
        vv.repaint();
    }

    public void setLabelProvider(LabelProviders newLabelProvider) {

        switch (newLabelProvider) {
            case ID:
                vv.getRenderContext().setNodeLabelFunction(n -> n.getId().toString());
                break;
            case ARTIFACT_ID:
                vv.getRenderContext().setNodeLabelFunction(n -> n.getArtifact().getArtifactId());
                break;
            case IMPORTANT_ARTIFACT_ID:
                vv.getRenderContext().setNodeLabelFunction(new HeatBasedLabelProvider(vv.getModel().getNetwork()));
                break;
            default:
                throw new IllegalStateException("No label provider for '" + newLabelProvider + "'");
        }
        vv.repaint();
    }

    private static LayoutAlgorithm<GraphDependencyNode> createLayout(Layouts layoutType) {
        switch (layoutType) {
            case CIRCLE:
                return new CircleLayoutAlgorithm<>();
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

                  Color[] colors = {ColorScheme.EDGE.ANIMATION_COLOR, ColorScheme.EDGE.ANIMATION_HL_COLOR, ColorScheme.EDGE.ANIMATION_COLOR};
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

    public enum LabelProviders {
        ID("id"),
        ARTIFACT_ID("artifactId"),
        IMPORTANT_ARTIFACT_ID("optimized artifactId");

        LabelProviders(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }
}
