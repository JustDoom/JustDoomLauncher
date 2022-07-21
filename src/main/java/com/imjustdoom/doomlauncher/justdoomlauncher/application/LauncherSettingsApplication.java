package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.JsonFile;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.setting.JsonSetting;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherSettingsApplication {

    private Stage stage;

    private Parent fxmlLoader;
    private List<JsonSetting> changedSettings = new ArrayList<>();

    private final JsonFile json = JustDoomLauncher.INSTANCE.getFiles().getLauncherFile();

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

        ((Button) scene.lookup("#back")).setOnAction(event -> close());

        ((CheckBox) scene.lookup("#update")).setSelected(json.getSetting("update").getAsBoolean());
        ((CheckBox) scene.lookup("#update")).setOnAction(event -> {
            CheckBox checkBox = (CheckBox) event.getSource();
            JsonSetting setting = json.getSetting("update").copy();
            if (changedSettings.contains(setting)) {
                System.out.println("remove");
                changedSettings.remove(setting);
                return;
            }
            setting.setValue(checkBox.isSelected());
            changedSettings.add(setting);
        });

        ((CheckBox) scene.lookup("#console")).setSelected(json.getSetting("openConsole").getAsBoolean());
        ((CheckBox) scene.lookup("#console")).setOnAction(event -> {
            CheckBox checkBox = (CheckBox) event.getSource();
            JsonSetting setting = json.getSetting("openConsole").copy();
            if (changedSettings.contains(setting)) {
                changedSettings.remove(setting);
                return;
            }
            setting.setValue(checkBox.isSelected());
            changedSettings.add(setting);
        });

        ((Button) scene.lookup("#save")).setOnAction(event -> {
            try {
                for (JsonSetting setting : json.getSettings()) {
                    for (JsonSetting setting1 : changedSettings) {
                        if (setting.isSetting(setting1)) {
                            setting.setValue(setting1.getValue());
                            setting.setUpdated(true);
                        }
                    }
                }
                System.out.println("save");
                json.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void close() {
        stage.close();
    }
}
