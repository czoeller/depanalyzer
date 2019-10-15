package de.czoeller.depanalyzer.ui.controller;

import de.czoeller.depanalyzer.ui.Application;
import de.czoeller.depanalyzer.ui.model.Analyzers;
import de.czoeller.depanalyzer.ui.model.Layouts;
import de.czoeller.depanalyzer.ui.model.UIModel;
import de.czoeller.depanalyzer.ui.model.UIViewModel;
import de.czoeller.depanalyzer.ui.util.ControlsUtils;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    public GridPane gridPane;

    @FXML
    public TextField searchTextField;

    @FXML
    public ComboBox<Layouts> layoutComboBox;

    @FXML
    public ComboBox<Analyzers> analyzerResultComboBox;

    @FXML
    public Label debugLabel;

    @FXML
    public SegmentedButton layoutSegmentedButton;

    @FXML
    public Rectangle legendColorRect;

    @FXML
    private SwingNode swingNodeViewer;

    @FXML
    private SwingNode swingNodeSatelliteViewer;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField textField;

    @FXML
    private AnchorPane leftPane;

    @FXML
    private AnchorPane centerPane;

    @FXML
    private AnchorPane rightPane;

    @FXML
    public DetailController detailController;

    private UIViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final UIModel model = Application.getUIModel();
        viewModel = new UIViewModel(model, swingNodeViewer, swingNodeSatelliteViewer);

        // Bindings
        textField.textProperty().bind(Application.getPrimaryStage().widthProperty().asString());
        searchTextField.textProperty().bindBidirectional(viewModel.searchTextProperty());

        layoutComboBox.setItems(viewModel.layoutsProperty());
        layoutComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.selectedLayoutProperty().set(newValue));
        layoutComboBox.getSelectionModel().selectFirst();

        layoutSegmentedButton.getButtons().addAll(buildLayoutButtons(viewModel.layoutsProperty()));
        layoutSegmentedButton.getToggleGroup().selectedToggleProperty().addListener(ControlsUtils.safeToggleChangeListener((newValue) -> viewModel.selectedLayoutProperty().set(newValue)));
        ControlsUtils.selectFirstToggle(layoutSegmentedButton);

        analyzerResultComboBox.setItems(viewModel.analyzerResultsProperty());
        analyzerResultComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.selectedAnalyzerResultProperty().set(newValue));
        analyzerResultComboBox.getSelectionModel().selectFirst();
        debugLabel.textProperty().bind(viewModel.selectedLayoutProperty().asString());

        // Sizes
        leftPane.prefWidthProperty().bind(Application.getPrimaryStage().widthProperty().divide(3));
        centerPane.prefWidthProperty().bind(Application.getPrimaryStage().widthProperty().divide(3));
        rightPane.prefWidthProperty().bind(Application.getPrimaryStage().widthProperty().divide(3));

        detailController.selectedNodeProperty().bind(viewModel.selectedNodePropertyProperty());

        Application.getPrimaryStage().titleProperty().bind(Bindings.concat("Dependency Analyzer - ").concat(viewModel.selectedLayoutProperty()));
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

}