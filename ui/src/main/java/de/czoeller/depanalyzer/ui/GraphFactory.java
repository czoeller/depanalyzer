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
package de.czoeller.depanalyzer.ui;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.google.common.collect.Lists;
import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.czoeller.depanalyzer.core.Core;
import de.czoeller.depanalyzer.core.config.Config;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.CVEIssue;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.visitor.GraphBuilderVisitor;
import de.czoeller.depanalyzer.ui.visitor.MavenConverterGraphBuilderVisitor;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.eclipse.aether.graph.DependencyNode;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            final de.czoeller.depanalyzer.metamodel.DependencyNode dependencyNode = new de.czoeller.depanalyzer.metamodel.DependencyNode(new DefaultArtifact(name, name, name, name, name, "jar", new DefaultArtifactHandler()));
            if(i % 3 == 0) {
                for (int j = 0; j < i; j++) {
                    int x = (i % (j + 1) % 3);
                    dependencyNode.addIssues(Analyzers.METRICS, Lists.newArrayList(new MetricIssue(Issue.Severity.values()[x], "Example Metrics description", 0.9f)));
                }
            } else if(i % 10 == 0) {
                for (int j = 0; j < i; j++) {
                    int x = (i % (j + 1) % 3);
                    dependencyNode.addIssues(Analyzers.CVE, Lists.newArrayList(new CVEIssue(Issue.Severity.values()[x],"Example CVE description")));
                }
            }

            nodeMap.put(i, dependencyNode);
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
        final de.czoeller.depanalyzer.metamodel.DependencyNode rootNode = getDependencyNodeCachedOrNew();

        final GraphBuilderVisitor graphBuilderVisitor = new GraphBuilderVisitor(forest);
        rootNode.accept(graphBuilderVisitor);

        return graphBuilderVisitor.getGraph();
    }

    private static de.czoeller.depanalyzer.metamodel.DependencyNode getDependencyNodeCachedOrNew() {

        final File resultFile = new File("results.dar");
        de.czoeller.depanalyzer.metamodel.DependencyNode dependencyNode = null;

        if(resultFile.exists() && !Config.INSTANCE.hasChanged()) {
            final Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            try(Input in = new Input(new FileInputStream(resultFile))) {
                kryo.register(de.czoeller.depanalyzer.metamodel.DependencyNode.class);
                kryo.register(Artifact.class);
                kryo.register(DefaultArtifact.class);
                kryo.register(DefaultArtifactHandler.class);
                kryo.register(File.class);
                kryo.register(org.apache.maven.artifact.versioning.VersionRange.class);
                kryo.register(org.apache.maven.artifact.versioning.DefaultArtifactVersion.class);
                kryo.register(org.apache.maven.artifact.versioning.ComparableVersion.class);
                kryo.register(Class.forName("org.apache.maven.artifact.versioning.ComparableVersion$ListItem"));
                kryo.register(Class.forName("org.apache.maven.artifact.versioning.ComparableVersion$IntegerItem"));
                kryo.register(Class.forName("org.apache.maven.artifact.versioning.ComparableVersion$StringItem"));
                kryo.register(Class.forName("java.util.Collections$EmptyList"));

                dependencyNode = kryo.readObject(in, de.czoeller.depanalyzer.metamodel.DependencyNode.class);
            } catch (FileNotFoundException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            final Core core = new Core();
            core.analyzePOM(Config.INSTANCE.getTargetPomFile());
            dependencyNode = core.getDependencyNode();
        }

        if(null == dependencyNode) {
            throw new RuntimeException("Could not retrieve dependency graph.");
        }

        Globals.analyzedProjectProperty().set(dependencyNode.getIdentifier());

        return dependencyNode;
    }

}
