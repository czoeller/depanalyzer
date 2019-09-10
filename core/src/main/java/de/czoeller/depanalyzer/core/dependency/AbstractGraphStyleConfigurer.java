package de.czoeller.depanalyzer.core.dependency;

public abstract class AbstractGraphStyleConfigurer implements GraphStyleConfigurer {

    protected boolean showGroupId;
    protected boolean showArtifactId;
    protected boolean showVersionsOnNodes;
    protected boolean showTypes;
    protected boolean showClassifiers;
    protected boolean showVersionOnEdges;
    protected boolean showOptional;

    @Override
    public final GraphStyleConfigurer showGroupIds(boolean showGroupId) {
        this.showGroupId = showGroupId;
        return this;
    }

    @Override
    public final GraphStyleConfigurer showArtifactIds(boolean showArtifactId) {
        this.showArtifactId = showArtifactId;
        return this;
    }

    @Override
    public final GraphStyleConfigurer showTypes(boolean showTypes) {
        this.showTypes = showTypes;
        return this;
    }

    @Override
    public final GraphStyleConfigurer showClassifiers(boolean showClassifiers) {
        this.showClassifiers = showClassifiers;
        return this;
    }

    @Override
    public final GraphStyleConfigurer showVersionsOnNodes(boolean showVersionsOnNodes) {
        this.showVersionsOnNodes = showVersionsOnNodes;
        return this;
    }

    @Override
    public final GraphStyleConfigurer showVersionsOnEdges(boolean showVersionOnEdges) {
        this.showVersionOnEdges = showVersionOnEdges;
        return this;
    }

    @Override
    public GraphStyleConfigurer showOptional(boolean optional) {
        this.showOptional = optional;
        return this;
    }

    // Only relevant for the text graph. Don't do anything here.
    @Override
    public GraphStyleConfigurer repeatTransitiveDependencies(boolean repeatTransitiveDependencies) {
        return this;
    }
}