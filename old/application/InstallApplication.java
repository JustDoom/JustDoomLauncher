package com.imjustdoom.justdoomlauncher.old.application;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class InstallApplication {

    private ProgressBar progressBar;
    private Label percentage;

    private FXMLLoader fxmlLoader;

    public InstallApplication() {
        this.fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("install-view.fxml"));
    }

    public void start() throws IOException {

        Stage stage = new Stage();
        stage.setTitle("Installing...");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader.load(), 200, 100);
        stage.setScene(scene);
        stage.show();

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) stage.setMaximized(false);
        });

        this.percentage = (Label) scene.lookup("#percentage");
        this.progressBar = (ProgressBar) scene.lookup("#bar");
    }

    public void close() {
        Stage stage = (Stage) progressBar.getScene().getWindow();
        stage.close();
    }

    public void update(double progress) {
        progressBar.setProgress(progress);
        percentage.setText(String.format("%.2f", progress * 100) + "%");
    }

    public void cancelInstall() {
        Stage stage = (Stage) progressBar.getScene().getWindow();
        stage.close();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getPercentage() {
        return percentage;
    }
}
