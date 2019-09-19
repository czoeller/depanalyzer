package de.czoeller.depanalyzer.ui;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverResult;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import de.czoeller.depanalyzer.ui.visitor.GraphBuilderVisitor;
import de.czoeller.depanalyzer.ui.visitor.MavenConverterGraphBuilderVisitor;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import org.eclipse.aether.graph.DependencyNode;

import java.io.File;

import static de.czoeller.depanalyzer.ui.AetherUtils.getDependencyNode;

public class GraphFactory {

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> exampleGraph(Forest<ArtifactGraphNode, ArtifactGraphEdge> forest) {

        DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
        DependencyNode node3 = getDependencyNode("c2", "util", "0.0.1");

        node.setChildren(Lists.newArrayList(node2, node3));

        final MavenConverterGraphBuilderVisitor mavenConverterGraphBuilderVisitor = new MavenConverterGraphBuilderVisitor(forest);
        node.accept(mavenConverterGraphBuilderVisitor);

        return mavenConverterGraphBuilderVisitor.getGraph();
    }

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> realGraphFromExampleProject(Forest<ArtifactGraphNode, ArtifactGraphEdge> forest) {
        final PomResolverImpl pomResolver = new PomResolverImpl();
        //final File pomFile = new File("core/pom.xml");
        final File pomFile = new File("C:\\Users\\noex_\\AppData\\Local\\Temp\\mvvmFX\\pom.xml");
        final PomResolverResult pomResolverResult = pomResolver.resolvePom(pomFile);

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor(forest);
        pomResolverResult.getRootNode().accept(graphBuilderVisitor);

        return graphBuilderVisitor.getGraph();
    }

}
