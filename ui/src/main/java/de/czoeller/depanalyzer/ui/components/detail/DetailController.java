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
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class DetailController implements Initializable {

    public Text groupIdText;
    public Text artifactIdText;
    public Text versionText;
    public Text licenseText;
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
        licenseText.textProperty().bind(viewModel.licenseProperty());
        nrIssuesText.textProperty().bind(viewModel.nrIssuesProperty().asString());
        heatText.textProperty().bind(viewModel.heatProperty().asString());
        issuesTableView.setItems(viewModel.getIssues());
        issuesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        issuesTableView.setOnKeyPressed(event -> {
            if (keyCodeCopy.match(event)) {
                copySelectionToClipboard(issuesTableView);
            }
        });
    }

    public static void copySelectionToClipboard(final TableView<?> table) {
        final Set<Integer> rows = new TreeSet<>();
        for (final TablePosition<?, ?> tablePosition : table.getSelectionModel().getSelectedCells()) {
            rows.add(tablePosition.getRow());
        }
        final StringBuilder strb = new StringBuilder();
        boolean firstRow = true;
        for (final Integer row : rows) {
            if (!firstRow) {
                strb.append('\n');
            }
            firstRow = false;
            boolean firstCol = true;
            for (final TableColumn<?, ?> column : table.getColumns()) {
                if (!firstCol) {
                    strb.append('\t');
                }
                firstCol = false;
                final Object cellData = column.getCellData(row);
                strb.append(cellData == null ? "" : cellData.toString());
            }
        }
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(strb.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public ObjectProperty<GraphDependencyNode> selectedNodeProperty() {
        return viewModel.selectedNodeProperty();
    }

    @FXML
    public void onSort(SortEvent<TableView<IssueTableViewModel>> tableViewSortEvent) {
        issuesTableView.refresh();
    }
}
