package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class RestartApplication {

    private Button restart, later;
    private Stage stage;

    private Parent fxmlLoader;

    public RestartApplication() {
        try {
            this.fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("restart-view.fxml")).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        stage = new Stage();
        stage.setTitle("Restart");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader, 200, 100);
        stage.setScene(scene);
        stage.show();

        stage.setOnHiding(event -> Platform.runLater(() -> {
            System.exit(0);
        }));

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) stage.setMaximized(false);
        });

        ((Button) scene.lookup("#exit")).setOnAction(event -> {
            close();
            System.exit(0);
        });
    }

    public void close() {
        stage.close();
    }
}
