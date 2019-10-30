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
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SortEvent;
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


    @FXML
    public void onSort(SortEvent<TableView<IssueTableViewModel>> tableViewSortEvent) {
        issuesTableView.refresh();
    }
}
