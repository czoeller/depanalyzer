package de.czoeller.depanalyzer.ui.components.detail;

import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailController implements Initializable {

    public Text groupIdText;
    public Text artifactIdText;
    public Text versionText;
    public Text nrIssuesText;
    public Text heatText;
    public TableView<IssueTableViewModel> issuesTableView;
    private DetailViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new DetailViewModel();
        groupIdText.textProperty().bind(viewModel.groupIdProperty());
        artifactIdText.textProperty().bind(viewModel.artifactIdProperty());
        versionText.textProperty().bind(viewModel.versionProperty());
        nrIssuesText.textProperty().bind(viewModel.nrIssuesProperty().asString());
        heatText.textProperty().bind(viewModel.heatProperty().asString());
        issuesTableView.setItems(viewModel.getIssues());
    }

    public ObjectProperty<GraphDependencyNode> selectedNodeProperty() {
        return viewModel.selectedNodeProperty();
    }
}
