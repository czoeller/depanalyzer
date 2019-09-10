package de.czoeller.depanalyzer.core.dependency;

import org.apache.maven.project.DependencyResolutionException;
import org.eclipse.aether.RepositoryException;

/**
 * Wrapper for {@link DependencyResolutionException}.
 */
public final class DependencyGraphException extends RuntimeException {

    private static final long serialVersionUID = 3167396359488785529L;

    public DependencyGraphException(RepositoryException cause) {
        super(cause);
    }
}
