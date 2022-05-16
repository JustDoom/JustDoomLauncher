package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.google.gson.JsonObject;
import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.Config;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherSettingsApplication {

    private Stage stage;

    private Parent fxmlLoader;

    public LauncherSettingsApplication() {
        try {
            this.fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("launcher-settings-view.fxml")).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        stage = new Stage();
        stage.setTitle("Settings");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader);
        stage.setScene(scene);
        stage.show();

        ((Button) scene.lookup("#back")).setOnAction(event -> {
            close();
        });

        JsonObject json = JustDoomLauncher.INSTANCE.getFiles().getLauncherFile();
        ((CheckBox) scene.lookup("#update")).setSelected(Boolean.parseBoolean(Config.getSetting("update", json)));
        ((CheckBox) scene.lookup("#update")).setOnAction(event -> {
            CheckBox checkBox = (CheckBox) event.getSource();
            Config.setSetting("update", checkBox.isSelected() ? "true" : "false", json);
        });
    }

    public void close() {
        stage.close();
    }
}
