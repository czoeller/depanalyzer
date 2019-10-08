package de.czoeller.depanalyzer.ui;

import com.google.common.collect.Lists;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.czoeller.depanalyzer.core.Main;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.visitor.GraphBuilderVisitor;
import de.czoeller.depanalyzer.ui.visitor.MavenConverterGraphBuilderVisitor;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.eclipse.aether.graph.DependencyNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static de.czoeller.depanalyzer.ui.AetherUtils.getDependencyNode;

public class GraphFactory {

    public static ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> exampleGraph(MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph) {

        DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
        DependencyNode node3 = getDependencyNode("c2", "util", "0.0.1");

        node.setChildren(Lists.newArrayList(node2, node3));

        final MavenConverterGraphBuilderVisitor mavenConverterGraphBuilderVisitor = new MavenConverterGraphBuilderVisitor(graph);
        node.accept(mavenConverterGraphBuilderVisitor);

        return mavenConverterGraphBuilderVisitor.getGraph();
    }

    public static ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> exampleComplexGraph(MutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph) {
        final Map<Integer, de.czoeller.depanalyzer.metamodel.DependencyNode> nodeMap = new HashMap<>();
        for (int i = 0; i < 150; i++) {
            final String name = String.valueOf(i);
            nodeMap.put(i, new de.czoeller.depanalyzer.metamodel.DependencyNode(new DefaultArtifact(name, name, name, name, name, "jar", new DefaultArtifactHandler())));
        }
        Supplier<GraphDependencyNode> nodeFactory =
                new Supplier<GraphDependencyNode>() {
                    int count;

                    public GraphDependencyNode get() {
                        return new GraphDependencyNode(nodeMap.get(count++));
                    }
                };
        Supplier<GraphDependencyEdge> edgeFactory =
                new Supplier<GraphDependencyEdge>() {
                    int count;

                    public GraphDependencyEdge get() {
                        return new GraphDependencyEdge(nodeMap.get(count++), nodeMap.get(count++));
                    }
                };

        BarabasiAlbertGenerator<GraphDependencyNode, GraphDependencyEdge> generator = new BarabasiAlbertGenerator<>(NetworkBuilder.directed().allowsParallelEdges(true),
                nodeFactory,
                edgeFactory,
                4,
                3);
        generator.evolveGraph(20);
        return ImmutableNetwork.copyOf(generator.get());
    }

    public static ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> realGraphFromExampleProject(MutableNetwork<GraphDependencyNode, GraphDependencyEdge> forest) {
        final Main main = new Main();
        main.analyzePOM(new File("pom.xml"));

        final de.czoeller.depanalyzer.metamodel.DependencyNode rootNode = main.getDependencyNode();

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor(forest);
        rootNode.accept(graphBuilderVisitor);

        return graphBuilderVisitor.getGraph();
    }

}
