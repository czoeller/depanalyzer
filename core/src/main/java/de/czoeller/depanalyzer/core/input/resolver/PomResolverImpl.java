package de.czoeller.depanalyzer.core.input.resolver;

import de.czoeller.depanalyzer.core.NodesAndEdgesDefinitionTreeBuilder;
import de.czoeller.depanalyzer.core.builder.ProjectBuilder;
import de.czoeller.depanalyzer.core.dependency.AggregatingGraphFactory;
import de.czoeller.depanalyzer.core.dependency.MavenGraphAdapter;
import de.czoeller.depanalyzer.core.dependency.dot.DotGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.dependency.dot.style.resource.BuiltInStyleResource;
import de.czoeller.depanalyzer.core.dependency.dot.style.resource.ClasspathStyleResource;
import de.czoeller.depanalyzer.core.dependency.dot.style.resource.StyleResource;
import de.czoeller.depanalyzer.core.graph.DependencyNodeIdRenderer;
import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.graph.Node;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import lombok.val;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

import static de.czoeller.depanalyzer.core.graph.dot.DotUtils.createDotGraphImage;
import static de.czoeller.depanalyzer.core.graph.dot.DotUtils.writeGraphFile;

public class PomResolverImpl implements PomResolver {

    private StyleConfiguration loadStyleConfiguration() {
        // default style resources
        ClasspathStyleResource defaultStyleResource = BuiltInStyleResource.DEFAULT_STYLE.createStyleResource(getClass().getClassLoader());
        Set<StyleResource> styleResources = new LinkedHashSet<>();

        // load and print
        StyleConfiguration styleConfiguration = StyleConfiguration.load(defaultStyleResource, styleResources.toArray(new StyleResource[0]));
        // System.out.println("Using effective style configuration:\n" + styleConfiguration.toJson());

        return styleConfiguration;
    }

    @Override
    public PomResolverResult resolvePom(File pomFile) {

        final File file = pomFile;
        final ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.build(file);

        final MavenProject project = projectBuilder.getParentProject();
        final Supplier<Collection<MavenProject>> projectSupplier = projectBuilder.getProjectSupplier();

        project.setArtifact(createArtifact(pomFile, project.getGroupId(), project.getArtifactId(), project.getVersion(), "compile", project.getPackaging(), ""));

        final MavenGraphAdapter mavenGraphAdapter = new MavenGraphAdapter(new AetherDependencyNodeResolver());


        final DependencyNodeIdRenderer nodeIdRenderer = DependencyNodeIdRenderer.versionlessId()
                                                                                .withClassifier(true)
                                                                                .withType(true)
                                                                                .withScope(true);

        final StyleConfiguration styleConfiguration = loadStyleConfiguration();
        final DotGraphStyleConfigurer dotGraphStyleConfigurer = new DotGraphStyleConfigurer(styleConfiguration);
        final GraphBuilder<DependencyNode> graphBuilder = dotGraphStyleConfigurer.showGroupIds(false)
                                                                                 .showArtifactIds(true)
                                                                                 .repeatTransitiveDependencies(false)
                                                                                 .showVersionsOnEdges(false)
                                                                                 .configure(GraphBuilder.create(nodeIdRenderer));

        final AggregatingGraphFactory graphFactory = new AggregatingGraphFactory(mavenGraphAdapter, projectSupplier, graphBuilder, true, true);


        String dependencyGraph = graphFactory.createGraph(project);
        try {
            Path graphFilePath = Paths.get( pomFile.getAbsoluteFile().getParent(), "target", project.getArtifactId() + ".dot");
            Path graphFilePathPNG = Paths.get( pomFile.getAbsoluteFile().getParent(),"target", project.getArtifactId() + ".png");
            writeGraphFile(dependencyGraph, graphFilePath);
            createDotGraphImage(graphFilePathPNG, dependencyGraph);

            System.out.println(dependencyGraph);

            final Map<String, Node<DependencyNode>> nodeDefinitions = graphBuilder.getNodeDefinitions();
            final Set<Edge> edges = graphBuilder.getEdges();

            final val builder = new NodesAndEdgesDefinitionTreeBuilder(nodeDefinitions, edges);

            return new PomResolverResult(builder.build(), nodeDefinitions, edges);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not resolve pom");
    }

    private org.apache.maven.artifact.Artifact createArtifact(File file, String groupId, String artifactId, String version, String scope, String type, String classifier) {
        DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, version, scope, type, classifier, new DefaultArtifactHandler());
        artifact.setFile(file);
        artifact.setResolved(true);
        return artifact;
    }

}
