package de.czoeller.depanalyzer.ui.components.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.IOException;

public class MenuController {

    private static final @StaticResource String helpView = "de/czoeller/depanalyzer/ui/components/help/HelpView.fxml";

    @FXML
    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void help(ActionEvent actionEvent) {
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

    @FXML
    public void about(ActionEvent actionEvent) {
    }
}
