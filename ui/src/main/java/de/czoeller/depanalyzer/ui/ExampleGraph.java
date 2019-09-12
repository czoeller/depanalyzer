package de.czoeller.depanalyzer.ui;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverImpl;
import de.czoeller.depanalyzer.core.input.resolver.PomResolverResult;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphEdge;
import de.czoeller.depanalyzer.ui.core.ArtifactGraphNode;
import de.czoeller.depanalyzer.ui.visitor.GraphBuilderVisitor;
import de.czoeller.depanalyzer.ui.visitor.MavenConverterGraphBuilderVisitor;
import edu.uci.ics.jung.graph.Graph;
import org.eclipse.aether.graph.DependencyNode;

import java.io.File;

import static de.czoeller.depanalyzer.ui.AetherUtils.getDependencyNode;

public class ExampleGraph {

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> createTree() {

        DependencyNode node = getDependencyNode("root", "art", "1.0.1");
        DependencyNode node2 = getDependencyNode("c1", "tool", "0.1.5");
        DependencyNode node3 = getDependencyNode("c2", "util", "0.0.1");

        node.setChildren(Lists.newArrayList(node2, node3));

        final MavenConverterGraphBuilderVisitor mavenConverterGraphBuilderVisitor = new MavenConverterGraphBuilderVisitor();
        node.accept(mavenConverterGraphBuilderVisitor);

        return mavenConverterGraphBuilderVisitor.getGraph();
    }

    public static Graph<ArtifactGraphNode, ArtifactGraphEdge> realDependencyTree() {
        final PomResolverImpl pomResolver = new PomResolverImpl();
        final File pomFile = new File("core/pom.xml");
        final PomResolverResult pomResolverResult = pomResolver.resolvePomExperimental(pomFile);

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor();
        pomResolverResult.getRootNode().accept(graphBuilderVisitor);

        return graphBuilderVisitor.getGraph();
    }

}
