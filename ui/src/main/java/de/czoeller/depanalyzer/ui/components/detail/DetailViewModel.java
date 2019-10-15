package de.czoeller.depanalyzer.ui.components.detail;

import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;

public class DetailViewModel {

    private final ReadOnlyStringWrapper groupId = new ReadOnlyStringWrapper(this, "groupId");
    private final ReadOnlyStringWrapper artifactId = new ReadOnlyStringWrapper(this, "artifactId");
    private final ReadOnlyStringWrapper version = new ReadOnlyStringWrapper(this, "version");
    private final ReadOnlyStringWrapper issues = new ReadOnlyStringWrapper(this, "issues");
    private final ObjectProperty<GraphDependencyNode> selectedNode = new SimpleObjectProperty<>(this, "selectedNode");

    public DetailViewModel() {
        selectedNode.addListener((observable, oldValue, newValue) -> {
            groupId.set(newValue.getDependencyNode().getArtifact().getGroupId());
            artifactId.set(newValue.getDependencyNode().getArtifact().getArtifactId());
            version.set(newValue.getDependencyNode().getArtifact().getVersion());
            issues.set("" + newValue.getIssues().size());
        });
    }

    public GraphDependencyNode getSelectedNode() {
        return selectedNode.get();
    }

    public ObjectProperty<GraphDependencyNode> selectedNodeProperty() {
        return selectedNode;
    }

    public void setSelectedNode(GraphDependencyNode selectedNode) {
        this.selectedNode.set(selectedNode);
    }

    public String getGroupId() {
        return groupId.get();
    }

    public ReadOnlyStringProperty groupIdProperty() {
        return groupId.getReadOnlyProperty();
    }

    public void setGroupId(String groupId) {
        this.groupId.set(groupId);
    }

    public String getArtifactId() {
        return artifactId.get();
    }

    public ReadOnlyStringProperty artifactIdProperty() {
        return artifactId.getReadOnlyProperty();
    }

    public void setArtifactId(String artifactId) {
        this.artifactId.set(artifactId);
    }

    public String getVersion() {
        return version.get();
    }

    public ReadOnlyStringProperty versionProperty() {
        return version.getReadOnlyProperty();
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public String getIssues() {
        return issues.get();
    }

    public ReadOnlyStringProperty issuesProperty() {
        return issues.getReadOnlyProperty();
    }

    public void setIssues(String issues) {
        this.issues.set(issues);
    }

}
