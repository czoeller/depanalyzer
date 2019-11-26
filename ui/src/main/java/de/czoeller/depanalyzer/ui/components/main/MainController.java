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
import de.czoeller.depanalyzer.ui.GUI;
import de.czoeller.depanalyzer.ui.Globals;
import de.czoeller.depanalyzer.ui.components.detail.DetailController;
import de.czoeller.depanalyzer.ui.components.stats.StatsController;
import de.czoeller.depanalyzer.ui.model.Layouts;
import de.czoeller.depanalyzer.ui.model.MainModel;
import de.czoeller.depanalyzer.ui.swingwrapper.GraphViewerWrapper;
import de.czoeller.depanalyzer.ui.util.ControlsUtils;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.HyperlinkLabel;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    public HyperlinkLabel statsLink;

    @FXML
    public GridPane gridPane;

    @FXML
    public TextField searchTextField;

    @FXML
    public ComboBox<Layouts> layoutComboBox;

    @FXML
    public ComboBox<Analyzers> analyzerResultComboBox;

    @FXML
    public ComboBox<GraphViewerWrapper.LabelProviders> labelProvidersComboBox;

    @FXML
    public Label analyzedProjectLabel;

    @FXML
    public SegmentedButton layoutSegmentedButton;

    @FXML
    private SwingNode swingNodeViewer;

    @FXML
    private SwingNode swingNodeSatelliteViewer;

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane centerPane;

    @FXML
    private AnchorPane leftPane;

    @FXML
    private AnchorPane rightPane;

    @FXML
    public DetailController detailController;

    private MainViewModel viewModel;
    private PopOver popOver = new PopOver();

    private static final @StaticResource
    String statsView = "de/czoeller/depanalyzer/ui/components/stats/StatsView.fxml";
    private MainModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = GUI.getUIModel();
        viewModel = new MainViewModel(model, swingNodeViewer, swingNodeSatelliteViewer);

        // Bindings
        analyzedProjectLabel.textProperty().bind(viewModel.analyzedProjectProperty());
        searchTextField.textProperty().bindBidirectional(viewModel.searchTextProperty());

        layoutComboBox.setItems(viewModel.layoutsProperty());
        layoutComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.selectedLayoutProperty().set(newValue));
        layoutComboBox.getSelectionModel().selectFirst();

        layoutSegmentedButton.getButtons().addAll(buildLayoutButtons(viewModel.layoutsProperty()));
        layoutSegmentedButton.getToggleGroup().selectedToggleProperty().addListener(ControlsUtils.safeToggleChangeListener((newValue) -> viewModel.selectedLayoutProperty().set(newValue)));
        ControlsUtils.selectFirstButton(layoutSegmentedButton);

        analyzerResultComboBox.setItems(viewModel.analyzerResultsProperty());
        analyzerResultComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.selectedAnalyzerResultProperty().set(newValue));
        analyzerResultComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> popOver.hide());
        analyzerResultComboBox.getSelectionModel().selectFirst();

        labelProvidersComboBox.setItems(viewModel.labelProvidersProperty());
        labelProvidersComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.selectedLabelProviderProperty().set(newValue));
        labelProvidersComboBox.getSelectionModel().selectFirst();

        // Sizes
        leftPane.prefWidthProperty().set(1);
        centerPane.prefWidthProperty().bind(GUI.getPrimaryStage().widthProperty().divide(1.5));
        rightPane.prefWidthProperty().bind(GUI.getPrimaryStage().widthProperty().divide(3));

        detailController.selectedNodeProperty().bind(viewModel.selectedNodePropertyProperty());

        GUI.getPrimaryStage().titleProperty().bind(Bindings.format("Dependency Analyzer - %s - %s", Globals.analyzedProjectArtifactIdProperty(), viewModel.selectedLayoutProperty()));
    }

    private List<ToggleButton> buildLayoutButtons(List<Layouts> layouts) {
        return layouts.stream()
                      .map(l -> {
                          final ToggleButton toggleButton = new ToggleButton(l.toString());
                          toggleButton.setUserData(l);
                          return toggleButton;
                      })
                      .collect(Collectors.toList());
    }

    public void pickModeAction() {
        viewModel.pickModeAction();
    }

    public void transformModeAction() {
        viewModel.transformModeAction();
    }

    @FXML
    public void showStats(ActionEvent actionEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(statsView));
        try {
            Parent root = loader.load();
            StatsController ctrl = loader.getController();
            ctrl.setModel(model);
            ctrl.postInitialize();

            popOver = new PopOver(root);
            popOver.setTitle("Stats");
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            popOver.setAutoHide(false);
            popOver.setCloseButtonEnabled(true);
            popOver.setHeaderAlwaysVisible(true);
            popOver.setCornerRadius(4);
            popOver.show(statsLink);

        } catch (IOException e) {
            throw new IllegalStateException("Could not load Stats Controller", e);
        }
    }
}