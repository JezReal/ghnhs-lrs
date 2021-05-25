package io.github.jezreal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("prototype/prototype.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("ui/home.fxml"));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("LRS");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}