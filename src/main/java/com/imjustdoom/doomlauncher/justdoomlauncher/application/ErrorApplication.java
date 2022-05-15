package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ErrorApplication {

    private Stage stage;
    private TextArea errorArea;

    public ErrorApplication() {

    }

    public void start() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("error-view.fxml"));

        stage = new Stage();
        stage.setTitle("Error...");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();

        errorArea = (TextArea) scene.lookup("#error");
    }

    public void close() {
        stage.close();
    }

    public void setError(String error) {
        errorArea.setText(error);
    }
}
