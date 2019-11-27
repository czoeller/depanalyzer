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
    private final ReadOnlyStringWrapper license = new ReadOnlyStringWrapper(this, "license");
    private final ReadOnlyIntegerWrapper nrIssues = new ReadOnlyIntegerWrapper(this, "nrIssues");
    private final ReadOnlyDoubleWrapper heat = new ReadOnlyDoubleWrapper(this, "heat");
    private final ObservableList<IssueTableViewModel> issues = FXCollections.observableArrayList();
    private final ObjectProperty<GraphDependencyNode> selectedNode = new SimpleObjectProperty<>(this, "selectedNode");

    public DetailViewModel() {
        selectedNode.addListener((observable, oldValue, newValue) -> {
            groupId.set(newValue.getArtifact().getGroupId());
            artifactId.set(newValue.getArtifact().getArtifactId());
            version.set(newValue.getArtifact().getVersion());
            license.set(newValue.getArtifact().getMetadataList().toString());
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

    public String getLicense() {
        return license.get();
    }

    public ReadOnlyStringProperty licenseProperty() {
        return license.getReadOnlyProperty();
    }

    public void setLicense(String license) {
        this.license.set(license);
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
