package de.czoeller.depanalyzer.core.input.resolver;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

@Slf4j
public class Slf4jMavenRepositoryListener extends AbstractRepositoryListener {

    public Slf4jMavenRepositoryListener() {
    }

    public void artifactDeployed(RepositoryEvent event) {
        log.info("Deployed " + event.getArtifact() + " to " + event.getRepository());
    }

    public void artifactDeploying(RepositoryEvent event) {
        log.trace("Deploying " + event.getArtifact() + " to " + event.getRepository());
    }

    public void artifactDescriptorInvalid(RepositoryEvent event) {
        log.debug("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException()
                                                                                        .getMessage());
    }

    public void artifactDescriptorMissing(RepositoryEvent event) {
        log.debug("Missing artifact descriptor for " + event.getArtifact());
    }

    public void artifactInstalled(RepositoryEvent event) {
        log.info("Installed " + event.getArtifact() + " to " + event.getFile());
    }

    public void artifactInstalling(RepositoryEvent event) {
        log.trace("Installing " + event.getArtifact() + " to " + event.getFile());
    }

    public void artifactResolved(RepositoryEvent event) {
        log.info("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
    }

    public void artifactDownloading(RepositoryEvent event) {
        log.trace("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
    }

    public void artifactDownloaded(RepositoryEvent event) {
        log.info("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
    }

    public void artifactResolving(RepositoryEvent event) {
        log.trace("Resolving artifact " + event.getArtifact());
    }

    public void metadataDeployed(RepositoryEvent event) {
        log.info("Deployed " + event.getMetadata() + " to " + event.getRepository());
    }

    public void metadataDeploying(RepositoryEvent event) {
        log.trace("Deploying " + event.getMetadata() + " to " + event.getRepository());
    }

    public void metadataInstalled(RepositoryEvent event) {
        log.info("Installed " + event.getMetadata() + " to " + event.getFile());
    }

    public void metadataInstalling(RepositoryEvent event) {
        log.trace("Installing " + event.getMetadata() + " to " + event.getFile());
    }

    public void metadataInvalid(RepositoryEvent event) {
        log.debug("Invalid metadata " + event.getMetadata());
    }

    public void metadataResolved(RepositoryEvent event) {
        log.info("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
    }

    public void metadataResolving(RepositoryEvent event) {
        log.trace("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
    }
}
