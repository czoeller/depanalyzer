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

public class Application extends javafx.application.Application {

    private static final @StaticResource String mainView = "de/czoeller/depanalyzer/ui/components/main/MainView.fxml";

    public static Stage primaryStage;

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
        final ImmutableNetwork<GraphDependencyNode, GraphDependencyEdge> graph = GraphFactory.exampleComplexGraph(g);
        return graph;
    }

    public static void main(String[] parameters) {
        launch(parameters);
    }
}
