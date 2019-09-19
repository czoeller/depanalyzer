package de.czoeller.depanalyzer.ui;

import de.czoeller.depanalyzer.ui.components.TopComponent;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import de.czoeller.depanalyzer.ui.core.DependencyGraphScene;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.ObservableGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

public class Application {

    private static class GraphAndForest {

        private final ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> graph;
        private final Forest<ArtifactGraphNode, ArtifactGraphEdge> forest;

        public GraphAndForest(ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> graph, Forest<ArtifactGraphNode, ArtifactGraphEdge> forest) {
            this.graph = graph;
            this.forest = forest;
        }

        public static GraphAndForest exampleGraph() {
            Forest<ArtifactGraphNode, ArtifactGraphEdge> forest = new DelegateForest<>();
            ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> g = new ObservableGraph<>(GraphFactory.realGraphFromExampleProject(forest));
            return new GraphAndForest(g, forest);
        }
    }

    private void run() {
        final JFrame jf = new JFrame("Visual Library + JUNG Demo");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final GraphAndForest gf = GraphAndForest.exampleGraph();
        DependencyGraphScene scene = new DependencyGraphScene(gf.graph, gf.forest);

        final JScrollPane pane = new JScrollPane();
        scene.setAnimateIterativeLayouts(true);
        JComponent sceneView = scene.getView();
        if (sceneView == null) {
            sceneView = scene.createView();
            // vlv: print
            sceneView.putClientProperty("print.printable", true); // NOI18N
        }
        pane.setViewportView(sceneView);
        //scene.cleanLayout(pane);
        scene.setSelectedObjects(Collections.singleton(scene.getRootGraphNode()));

        jf.setLayout(new BorderLayout());
        jf.add(new TopComponent(scene), BorderLayout.NORTH);
        jf.add(pane, BorderLayout.CENTER);
        jf.setSize(new Dimension(1280, 720));
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent we) {
                //scene.relayout(true);
                scene.validate();

            }
        });
        jf.setVisible(true);
    }


    public static void main(String[] args) {
        new Application().run();
    }
}
