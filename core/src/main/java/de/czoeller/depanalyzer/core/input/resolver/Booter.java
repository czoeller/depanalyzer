package de.czoeller.depanalyzer.core.input.resolver;

import com.google.common.collect.Lists;
import org.apache.maven.cli.transfer.Slf4jMavenTransferListener;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Booter {

    public static RepositorySystem newRepositorySystem() {
        return ManualRepositorySystemFactory.newRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system ) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( System.getProperty("user.home") + "/.m2/repository" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        session.setTransferListener( new Slf4jMavenTransferListener() );
        session.setRepositoryListener( new Slf4jMavenRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static List<RemoteRepository> newRepositories( RepositorySystem system, RepositorySystemSession session ) {
        return new ArrayList<>( Arrays.asList( newCentralRepository() ) );
    }

    private static RemoteRepository newCentralRepository() {
        return new RemoteRepository.Builder( "central", "default", "http://central.maven.org/maven2/" ).build();
    }

    public static List<RemoteRepository> repositoryFor(String id, String type, String url) {
        return Lists.newArrayList( new RemoteRepository.Builder(id, type, url).build() );
    }
}