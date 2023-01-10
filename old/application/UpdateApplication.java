package com.imjustdoom.justdoomlauncher.old.application;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.justdoomlauncher.old.process.UpdateLauncherProcess;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateApplication {

    private Stage stage;

    public UpdateApplication() {

    }

    public void start() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("update-view.fxml"));

        stage = new Stage();
        stage.setTitle("Update");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader.load(), 200, 100);
        stage.setScene(scene);
        stage.show();

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) stage.setMaximized(false);
        });

        ((Button) scene.lookup("#skip")).setOnAction(event -> {
            close();
        });

        ((Button) scene.lookup("#download")).setOnAction(event -> {
            UpdateLauncherProcess updateLauncherProcess = new UpdateLauncherProcess();
            new Thread(updateLauncherProcess::updateLauncher).start();

            close();
        });
    }

    public void close() {
        stage.close();
    }
}
