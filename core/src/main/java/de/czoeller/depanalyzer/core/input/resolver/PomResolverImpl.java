package de.czoeller.depanalyzer.core.input.resolver;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.builder.ProjectBuilder;
import de.czoeller.depanalyzer.core.dependency.AggregatingGraphFactory;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.MavenGraphAdapter;
import de.czoeller.depanalyzer.core.dependency.dot.DotGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.dependency.text.TextGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.graph.DependencyNodeIdRenderer;
import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.graph.Node;
import de.czoeller.depanalyzer.core.graph.dot.DotUtils;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PomResolverImpl implements PomResolver {
    @Override
    public DependencyNode resolvePom(File pomFile) {
        final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        final Model model;
        try {
            model = mavenreader.read(new BufferedReader(new FileReader(pomFile)));
            model.setPomFile(pomFile);

            final MavenProject project = new MavenProject(model);
            project.setArtifact(createArtifact(pomFile, project.getGroupId(), project.getArtifactId(), project.getVersion(), "compile", project.getPackaging(), ""));

            final MavenGraphAdapter mavenGraphAdapter = new MavenGraphAdapter(new AetherDependencyNodeResolver());



            final DependencyNodeIdRenderer nodeIdRenderer = DependencyNodeIdRenderer.versionlessId()
                                                                                    .withClassifier(true)
                                                                                    .withType(true)
                                                                                    .withScope(true);

            final GraphBuilder<DependencyNode> graphBuilder = GraphBuilder.create(nodeIdRenderer);
            final TextGraphStyleConfigurer textGraphStyleConfigurer = new TextGraphStyleConfigurer();
            textGraphStyleConfigurer.showGroupIds(true);
            textGraphStyleConfigurer.showArtifactIds(true);
            textGraphStyleConfigurer.configure(graphBuilder);

            final DotGraphStyleConfigurer dotGraphStyleConfigurer = new DotGraphStyleConfigurer(new StyleConfiguration());
            dotGraphStyleConfigurer.showGroupIds(true);
            dotGraphStyleConfigurer.showArtifactIds(true);
            dotGraphStyleConfigurer.configure(graphBuilder);

            final Supplier<Collection<MavenProject>> projectSupplier = () -> {
                //TODO: populate dynamically
                return Lists.newArrayList(project);
            };

            final AggregatingGraphFactory graphFactory = new AggregatingGraphFactory(mavenGraphAdapter, projectSupplier,
                    graphBuilder, false, true);


            String dependencyGraph = graphFactory.createGraph(project);
            final DependencyNode rootNode = graphBuilder.getRootNode();

            //Path graphFilePath = Paths.get("exm.dot");
            //Path graphFilePathPNG = Paths.get("exm.png");
            //writeGraphFile(dependencyGraph, graphFilePath);
            //DotUtils.createDotGraphImage(graphFilePathPNG, dependencyGraph);

            System.out.println(dependencyGraph);

            return rootNode;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not resolve pom");
    }

    @Override
    public PomResolverResult resolvePomExperimental(File pomFile) {

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

            final GraphBuilder<DependencyNode> graphBuilder = GraphBuilder.create(nodeIdRenderer);
            final TextGraphStyleConfigurer textGraphStyleConfigurer = new TextGraphStyleConfigurer();
            textGraphStyleConfigurer.showGroupIds(true);
            textGraphStyleConfigurer.showArtifactIds(true);
            textGraphStyleConfigurer.configure(graphBuilder);

            final DotGraphStyleConfigurer dotGraphStyleConfigurer = new DotGraphStyleConfigurer(new StyleConfiguration());
            dotGraphStyleConfigurer.showGroupIds(true);
            dotGraphStyleConfigurer.showArtifactIds(true);
            dotGraphStyleConfigurer.configure(graphBuilder);



            final AggregatingGraphFactory graphFactory = new AggregatingGraphFactory(mavenGraphAdapter, projectSupplier,
                    graphBuilder, true, false);


            String dependencyGraph = graphFactory.createGraph(project);
            final DependencyNode rootNode = graphBuilder.getRootNode();
try {
            Path graphFilePath = Paths.get("exm.dot");
            Path graphFilePathPNG = Paths.get("exm.png");
            //writeGraphFile(dependencyGraph, graphFilePath);
            DotUtils.createDotGraphImage(graphFilePathPNG, dependencyGraph);

            System.out.println(dependencyGraph);

            final Map<String, Node<DependencyNode>> nodeDefinitions = graphBuilder.getNodeDefinitions();
            final Set<Node<DependencyNode>> nodes = new HashSet<>(nodeDefinitions.values());
            final Set<Edge> edges = graphBuilder.getEdges();

            return new PomResolverResult(rootNode, nodeDefinitions, edges);
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
