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
package de.czoeller.depanalyzer.ui.components.main;

import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.ui.Globals;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.Layouts;
import de.czoeller.depanalyzer.ui.model.MainModel;
import de.czoeller.depanalyzer.ui.swingwrapper.GraphViewerWrapper;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.czoeller.depanalyzer.ui.util.ControlsUtils.delayListener;
import static de.czoeller.depanalyzer.ui.util.ControlsUtils.safeChangeListener;

public class MainViewModel {

    private final MainModel model;
    private ListProperty<Layouts> layoutsProperty = new SimpleListProperty<>();
    private ListProperty<Analyzers> analyzerResultsProperty = new SimpleListProperty<>();
    private ObjectProperty<Layouts> selectedLayoutProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Analyzers> selectedAnalyzerResultProperty = new SimpleObjectProperty<>();
    private ObjectProperty<GraphDependencyNode> selectedNodeProperty = new SimpleObjectProperty<>();
    private StringProperty searchTextProperty = new SimpleStringProperty();
    private StringProperty analyzedProjectProperty = new SimpleStringProperty();
    private GraphViewerWrapper graphViewerWrapper;
    public MainViewModel(MainModel model, SwingNode swingNodeViewer, SwingNode swingNodeSatelliteViewer) {
        this.model = model;
        Globals.selectedAnalyzerProperty().bind(selectedAnalyzerResultProperty());
        this.graphViewerWrapper = new GraphViewerWrapper(model, swingNodeViewer, swingNodeSatelliteViewer);
        this.layoutsProperty.set(FXCollections.observableArrayList(Layouts.values()));
        this.analyzerResultsProperty.set(FXCollections.observableArrayList(Analyzers.values()));
        this.analyzedProjectProperty.bind(Globals.analyzedProjectProperty());

        selectedNodePropertyProperty().bind(graphViewerWrapper.selectedNodePropertyProperty());
        // Actions
        this.selectedLayoutProperty.addListener(safeChangeListener($ -> changeLayoutAction()));
        this.selectedAnalyzerResultProperty.addListener(safeChangeListener($ -> changeAnalyzerResults()));
        this.searchTextProperty.addListener(delayListener(safeChangeListener($ -> changeSearchTextAction())));
    }

    public GraphDependencyNode getSelectedNodeProperty() {
        return selectedNodeProperty.get();
    }

    public ObjectProperty<GraphDependencyNode> selectedNodePropertyProperty() {
        return selectedNodeProperty;
    }

    public void setSelectedNodeProperty(GraphDependencyNode selectedNodeProperty) {
        this.selectedNodeProperty.set(selectedNodeProperty);
    }

    public StringProperty searchTextProperty() {
        return searchTextProperty;
    }

    public String getSearchText() {
        return searchTextProperty.get();
    }

    public StringProperty analyzedProjectProperty() {
        return analyzedProjectProperty;
    }

    public String getAnalyzedProject() {
        return analyzedProjectProperty.get();
    }

    public ObservableList<Layouts> layoutsProperty() {
        return layoutsProperty;
    }

    public ObservableList<Analyzers> analyzerResultsProperty() {
        return analyzerResultsProperty;
    }

    public ObjectProperty<Layouts> selectedLayoutProperty() {
        return selectedLayoutProperty;
    }

    public Layouts getSelectedLayout() {
        return selectedLayoutProperty.get();
    }

    public ObjectProperty<Analyzers> selectedAnalyzerResultProperty() {
        return selectedAnalyzerResultProperty;
    }

    public Analyzers getSelectedAnalyzerResult() {
        return selectedAnalyzerResultProperty.get();
    }

    public List<String> getAvailableNodeNames() {
        return model.getGraph().nodes().stream().map(GraphDependencyNode::getArtifact).flatMap(v -> Stream.of(v.getGroupId(), v.getArtifactId())).map(Object::toString).distinct().collect(Collectors.toList());
    }

    public void pickModeAction() {
        graphViewerWrapper.setPickMode();
    }

    public void transformModeAction() {
        graphViewerWrapper.setTransformMode();
    }

    public void changeLayoutAction() {
        graphViewerWrapper.setSelectedLayout(getSelectedLayout());
    }

    private void changeAnalyzerResults() {
        graphViewerWrapper.setAnalyzerResults(getSelectedAnalyzerResult());
    }

    public void changeSearchTextAction() {
        graphViewerWrapper.setSearch(getSearchText());
    }
}
