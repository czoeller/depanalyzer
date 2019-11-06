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
package de.czoeller.depanalyzer.core.input.resolver;

import com.google.common.collect.Iterables;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.project.DependencyResolutionRequest;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AetherDependencyNodeResolver implements DependencyNodeResolver {

    @Override
    public DependencyNode resolve(DependencyResolutionRequest request) throws DependencyCollectionException {

        final String groupId = request.getMavenProject().getGroupId();
        final String artifactId = request.getMavenProject().getArtifactId();
        final String version = request.getMavenProject().getVersion();

        //TODO: check artifactHandler default
        request.getMavenProject().setArtifact(new DefaultArtifact(groupId, artifactId,version, "compile", "jar", null, new DefaultArtifactHandler("jar")));

        RepositorySystem system = Booter.newRepositorySystem();
        RepositorySystemSession session = Booter.newRepositorySystemSession(system );

        org.eclipse.aether.artifact.Artifact artifact = new org.eclipse.aether.artifact.DefaultArtifact( groupId + ":" + artifactId + ":" + version );
        //artifact.setFile(new File(root.getArtifactPath()));

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new org.eclipse.aether.graph.Dependency( artifact, "compile" ) );
        collectRequest.setRepositories( Booter.newRepositories( system, session ) );

        CollectResult collectResult = null;
        collectResult = system.collectDependencies(session, collectRequest );

        resolveArtifacts(session, system, collectResult);

        return collectResult.getRoot();
    }

    private void resolveArtifacts(RepositorySystemSession session, RepositorySystem system, CollectResult collectResult) {
        final DependencyNode root = collectResult.getRoot();
        resolveArtifacts(session, system, root);
    }

    /**
     * Resolves artifacts for a DependencyNode-Tree.
     * Warning: nodes are modified.
     * @param session
     * @param system
     * @param nodes
     */
    private void resolveArtifacts(RepositorySystemSession session, RepositorySystem system, DependencyNode ... nodes ) {
        for (DependencyNode node : nodes) {

            final ArtifactRequest artifactRequest = new ArtifactRequest(node);
            artifactRequest.setRepositories(Booter.newRepositories( system, session ));
            try {
                final ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
                final Artifact resolvedArtifact = artifactResult.getArtifact();
                node.setArtifact(resolvedArtifact);
            } catch (ArtifactResolutionException e) {
                System.out.println("Failed to resolve artifact " + node.getArtifact().toString());
                System.out.println("... Try to resolve artifact in it's repositories");
                try {
                    artifactRequest.setRepositories( artifactRequest.getDependencyNode().getRepositories() );
                    final ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
                    final Artifact resolvedArtifact = artifactResult.getArtifact();
                    System.out.println("... Found it");
                    node.setArtifact(resolvedArtifact);
                } catch (ArtifactResolutionException ex) {
                    System.out.println("... Failed to resolve artifact in repositories too " + node.getArtifact().toString());
                    ex.printStackTrace();
                }
            }
            resolveArtifacts(session, system, Iterables.toArray(node.getChildren(), DependencyNode.class));
        }
    }

    private static <T extends DependencyNode> Collection<? extends DependencyNode> convertToFlatList(Collection<T> collection) {
        return collection.stream()
                         .flatMap(i -> Stream.concat(Stream.of(i), convertToFlatList(i.getChildren()).stream()))
                         .collect(Collectors.toList());
    }

}
