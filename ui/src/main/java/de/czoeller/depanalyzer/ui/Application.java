package de.czoeller.depanalyzer.ui;

public class Application {

    public static void main(String[] args) {
        try {
            Class.forName( "javafx.fxml.FXMLLoader" );
        } catch( ClassNotFoundException e ) {
            throw new IllegalStateException("JavaFX is required by this application but it seems like it is not installed. Please use Oracle JDK1.8 or install javafx.");
        }

        GUI.launch(GUI.class, args);
    }
}
