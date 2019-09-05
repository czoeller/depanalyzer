package de.czoeller.depanalyzer.input.impl;

import de.czoeller.depanalyzer.core.Core;
import de.czoeller.depanalyzer.input.InputParser;
import de.czoeller.depanalyzer.input.internal.Booter;
import de.czoeller.depanalyzer.input.visitor.GraphBuilderVisitor;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Dependency;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;

import java.io.File;

class InputParserImpl implements InputParser {

    public static void main(String[] args) {
        InputParser inputParser = new InputParserImpl();
        final Dependency dependency = inputParser.buildDependencyGraph(Core.TEST_ARTIFACT_SPRING);
        System.out.println(dependency.flattened().count());
    }
    @Override
    public Dependency buildDependencyGraph(Artifact root) {
        RepositorySystem system = Booter.newRepositorySystem();
        RepositorySystemSession session = Booter.newRepositorySystemSession( system );

        org.eclipse.aether.artifact.Artifact artifact = new org.eclipse.aether.artifact.DefaultArtifact( root.getVersion().getGroupId() + ":" + root.getVersion().getArtifactId() + ":" + root.getVersion().getVersion() );
        artifact.setFile(new File(root.getArtifactPath()));

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new org.eclipse.aether.graph.Dependency( artifact, "" ) );
        collectRequest.setRepositories( Booter.newRepositories( system, session ) );

        CollectResult collectResult = null;
        try {
            collectResult = system.collectDependencies( session, collectRequest );
            final GraphBuilderVisitor graphBuilder = new GraphBuilderVisitor();
            collectResult.getRoot().accept(graphBuilder);
            return graphBuilder.getGraph();
        } catch (DependencyCollectionException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not build graph", e);
        }

    }

}
