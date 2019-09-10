package de.czoeller.depanalyzer.core;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.impl.DummyAnalyzerImpl;
import de.czoeller.depanalyzer.core.dependency.AggregatingGraphFactory;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.MavenGraphAdapter;
import de.czoeller.depanalyzer.core.input.resolver.AetherDependencyNodeResolver;
import de.czoeller.depanalyzer.core.dependency.text.TextGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.graph.DependencyNodeIdRenderer;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Main {

    private DependencyNode dependencyNode;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        readPOM();
        analyze();
    }

    private void readPOM() {
        final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        final File pomFile = new File("core/pom.xml");
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

            final Supplier<Collection<MavenProject>> projectSupplier = () -> {
                //TODO: populate dynamically
                return Lists.newArrayList(project);
            };

            final AggregatingGraphFactory graphFactory = new AggregatingGraphFactory(mavenGraphAdapter, projectSupplier,
                    graphBuilder, false, true);


            String dependencyGraph = graphFactory.createGraph(project);
            this.dependencyNode = graphBuilder.getRootNode();

            System.out.println(dependencyGraph);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private org.apache.maven.artifact.Artifact createArtifact(File file, String groupId, String artifactId, String version, String scope, String type, String classifier) {
        DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, version, scope, type, classifier, new DefaultArtifactHandler());
        artifact.setFile(file);
        artifact.setResolved(true);
        return artifact;
    }

    private void analyze() {
        final Analyzer dummyAnalyzer = new DummyAnalyzerImpl();
        final Artifact artifact = new Artifact(this.dependencyNode.getArtifact().getFile().getAbsolutePath());
        final Map<Artifact, List<Issue>> issues = dummyAnalyzer.analyze(artifact);
        System.out.println(issues);
    }
}
