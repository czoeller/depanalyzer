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

import static de.czoeller.depanalyzer.ui.util.ControlsUtils.safeChangeListener;

public class MainViewModel {

    private final MainModel model;
    private ListProperty<Layouts> layoutsProperty = new SimpleListProperty<>();
    private ListProperty<Analyzers> analyzerResultsProperty = new SimpleListProperty<>();
    private ObjectProperty<Layouts> selectedLayoutProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Analyzers> selectedAnalyzerResultProperty = new SimpleObjectProperty<>();
    private ObjectProperty<GraphDependencyNode> selectedNodeProperty = new SimpleObjectProperty<>();
    private StringProperty searchTextProperty = new SimpleStringProperty();
    private GraphViewerWrapper graphViewerWrapper;
    public MainViewModel(MainModel model, SwingNode swingNodeViewer, SwingNode swingNodeSatelliteViewer) {
        this.model = model;
        Globals.selectedAnalyzerProperty().bind(selectedAnalyzerResultProperty());
        this.graphViewerWrapper = new GraphViewerWrapper(model, swingNodeViewer, swingNodeSatelliteViewer);
        layoutsProperty.set(FXCollections.observableArrayList(Layouts.values()));
        analyzerResultsProperty.set(FXCollections.observableArrayList(Analyzers.values()));

        selectedNodePropertyProperty().bind(graphViewerWrapper.selectedNodePropertyProperty());
        // Actions
        selectedLayoutProperty.addListener(safeChangeListener($ -> changeLayoutAction()));
        selectedAnalyzerResultProperty.addListener(safeChangeListener($ -> changeAnalyzerResults()));
        searchTextProperty.addListener(safeChangeListener($ -> changeSearchTextAction()));
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
