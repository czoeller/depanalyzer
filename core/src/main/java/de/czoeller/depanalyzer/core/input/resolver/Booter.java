package de.czoeller.depanalyzer.core.input.resolver;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Booter
{
    public static RepositorySystem newRepositorySystem()
    {
        return ManualRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.sisu.SisuRepositorySystemFactory.newRepositorySystem();
        // return org.eclipse.aether.examples.plexus.PlexusRepositorySystemFactory.newRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( System.getProperty("user.home") + "/.m2/repository" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static List<RemoteRepository> newRepositories( RepositorySystem system, RepositorySystemSession session )
    {
        return new ArrayList<>( Arrays.asList( newCentralRepository() ) );
    }

    private static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "http://central.maven.org/maven2/" ).build();
    }
}