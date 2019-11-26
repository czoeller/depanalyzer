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
package de.czoeller.depanalyzer.ui.components.stats;

import de.czoeller.depanalyzer.ui.model.MainModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    @FXML
    public Label nrNodesLabel;
    @FXML
    public Label nrProjectNodesLabel;
    @FXML
    public Label nrNodesWithIssueLabel;
    @FXML
    public Label deepestNodeLabel;

    private StatsViewModel viewModel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // The viewModel is not set yet. Need to call postInitialize from extern
    }

    public void postInitialize() {
        nrNodesLabel.textProperty().bind(viewModel.nrNodesProperty().asString());
        nrProjectNodesLabel.textProperty().bind(viewModel.nrProjectNodesProperty().asString());
        nrNodesWithIssueLabel.textProperty().bind(viewModel.nrNodesWithIssueProperty().asString());
        deepestNodeLabel.textProperty().bind(viewModel.deepestNodeProperty());
    }

    public void setModel(MainModel model) {
        viewModel = new StatsViewModel(model);
    }


}
