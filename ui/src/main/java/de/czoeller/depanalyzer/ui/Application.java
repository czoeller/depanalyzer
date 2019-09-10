package de.czoeller.depanalyzer.ui;

import de.czoeller.depanalyzer.ui.components.TopComponent;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import de.czoeller.depanalyzer.ui.core.DependencyGraphScene;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.ObservableGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

public class Application {

    private static class GraphAndForest {

        private final ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> graph;
        private final DelegateForest<ArtifactGraphNode, ArtifactGraphEdge> forest;

        public GraphAndForest(ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> graph, DelegateForest<ArtifactGraphNode, ArtifactGraphEdge> forest) {
            this.graph = graph;
            this.forest = forest;
        }

        public static GraphAndForest exampleGraph() {
            DelegateForest<ArtifactGraphNode, ArtifactGraphEdge> forest = new DelegateForest<>();
            ObservableGraph<ArtifactGraphNode, ArtifactGraphEdge> g = new ObservableGraph<>(ExampleGraph.realDependencyTree());
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
        scene.cleanLayout(pane);
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

        /*final Path path = Paths.get(".");

        File pomXml = new File(path.toFile(), "pom.xml");
        DependencyGraphScene scene = null;
        try {
            InputStream is = new FileInputStream(pomXml);
            final MavenXpp3Reader reader = new MavenXpp3Reader();

            Model model;

            model = reader.read(is);
            final MavenProject prj = new MavenProject(model);

            DependencyTreeBuilder treeBuilder = new DefaultDependencyTreeBuilder();
            final DependencyNode root = treeBuilder.buildDependencyTree(prj);

            scene = new DependencyGraphScene(prj, model);
            //scene2.setAnimateIterativeLayouts(animateLayouts.isSelected());
            GraphConstructor constr = new GraphConstructor(scene);

            root.accept(constr);

        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException(e);
        } catch (DependencyTreeBuilderException e) {
            e.printStackTrace();
        }

        if (scene == null) {
            throw new IllegalStateException("Could not load scene");
        }*/

        //jf.setLayout(new BorderLayout());
        //jf.add(new JScrollPane(scene.createView()), BorderLayout.CENTER);
    }


    public static void main(String[] args) {
        new Application().run();
    }
}
