package de.czoeller.depanalyzer.ui.components.detail;

import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class DetailViewModel {

    private final ReadOnlyStringWrapper groupId = new ReadOnlyStringWrapper(this, "groupId");
    private final ReadOnlyStringWrapper artifactId = new ReadOnlyStringWrapper(this, "artifactId");
    private final ReadOnlyStringWrapper version = new ReadOnlyStringWrapper(this, "version");
    private final ReadOnlyIntegerWrapper nrIssues = new ReadOnlyIntegerWrapper(this, "nrIssues");
    private final ReadOnlyDoubleWrapper heat = new ReadOnlyDoubleWrapper(this, "heat");
    private final ObservableList<IssueTableViewModel> issues = FXCollections.observableArrayList();
    private final ObjectProperty<GraphDependencyNode> selectedNode = new SimpleObjectProperty<>(this, "selectedNode");

    public DetailViewModel() {
        selectedNode.addListener((observable, oldValue, newValue) -> {
            groupId.set(newValue.getDependencyNode().getArtifact().getGroupId());
            artifactId.set(newValue.getDependencyNode().getArtifact().getArtifactId());
            version.set(newValue.getDependencyNode().getArtifact().getVersion());
            nrIssues.set(newValue.getIssues().size());
            heat.set(newValue.getHeat());
            initialize();
        });
    }

    public void initialize() {
        issues.setAll(selectedNode.get().getIssues().stream().map(IssueTableViewModel::new).collect(Collectors.toList()));
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

    public Integer getNrIssues() {
        return nrIssues.get();
    }

    public ReadOnlyIntegerProperty nrIssuesProperty() {
        return nrIssues.getReadOnlyProperty();
    }

    public void setNrIssues(Integer nrIssues) {
        this.nrIssues.set(nrIssues);
    }

    public double getHeat() {
        return heat.get();
    }

    public ReadOnlyDoubleWrapper heatProperty() {
        return heat;
    }

    public ObservableList<IssueTableViewModel> getIssues() {
        return issues;
    }
}
