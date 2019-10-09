package de.czoeller.depanalyzer.ui;


import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import de.czoeller.depanalyzer.ui.components.GraphComponent;
import de.czoeller.depanalyzer.ui.components.TopComponent;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.UIModel;

import javax.swing.*;
import java.awt.*;

public class Application {

    private void run() {
        // create a simple graph for the demo
        Network<GraphDependencyNode, GraphDependencyEdge> graph = createGraph();

        final UIModel model = new UIModel(graph, UIModel.Layouts.FR, UIModel.Analyzers.METRICS);
        final TopComponent topComponent = new TopComponent(model);
        final GraphComponent graphComponent = new GraphComponent(model);

        model.addLayoutChangedListener((layout) -> graphComponent.modelUpdated(layout));
        model.addSearchChangedListener((search) -> graphComponent.modelUpdated(search));
        model.addAnalyzerChangedListener((analyzer) -> graphComponent.modelUpdated(analyzer));

        JPanel layout = new JPanel();
        layout.setPreferredSize(new Dimension(1000, 900));
        layout.setLayout(new BorderLayout(2,1));
        layout.add(topComponent.$$$getRootComponent$$$(), BorderLayout.NORTH);
        layout.add(graphComponent, BorderLayout.CENTER);

        final JFrame jf = new JFrame();
        jf.getContentPane().add(layout);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }

    ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> createGraph() {
        MutableNetwork<GraphDependencyNode, GraphDependencyEdge> g = NetworkBuilder.directed().expectedNodeCount(200).expectedEdgeCount(400).allowsSelfLoops(false).build();
        final ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph = GraphFactory.realGraphFromExampleProject(g);
        return graph;
    }

    public static void main(String[] args) {
        new Application().run();
    }
}
