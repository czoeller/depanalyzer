package de.czoeller.depanalyzer.ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.IOException;

public class MenuController {

    private static final @StaticResource String helpView = "de/czoeller/depanalyzer/ui/controller/HelpView.fxml";

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void about(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(helpView));
        Parent root;
        try {
            root = loader.load();
            loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
