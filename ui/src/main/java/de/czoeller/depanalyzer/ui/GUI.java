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
package de.czoeller.depanalyzer.ui;

import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import de.czoeller.depanalyzer.ui.components.main.MainController;
import de.czoeller.depanalyzer.ui.model.GraphDependencyEdge;
import de.czoeller.depanalyzer.ui.model.GraphDependencyNode;
import de.czoeller.depanalyzer.ui.model.MainModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.IOException;

public class GUI extends javafx.application.Application {

    private static final @StaticResource String mainView = "de/czoeller/depanalyzer/ui/components/main/MainView.fxml";

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(mainView));
        Parent root = loader.load();
        MainController ctrl = loader.getController();
        Scene scene = new Scene(root);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN), ctrl::pickModeAction);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN), ctrl::transformModeAction);
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static MainModel getUIModel() {
        return new MainModel(createGraph());
    }

    private static ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> createGraph() {
        MutableNetwork<GraphDependencyNode, GraphDependencyEdge> g = NetworkBuilder.directed().expectedNodeCount(200).expectedEdgeCount(400).allowsSelfLoops(false).build();
        final ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph = GraphFactory.realGraphFromExampleProject(g);
        return graph;
    }
}
