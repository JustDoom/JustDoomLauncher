package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.UpdateLauncherProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.settings.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
