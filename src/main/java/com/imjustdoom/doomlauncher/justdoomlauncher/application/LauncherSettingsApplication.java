package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.JsonFile;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.setting.JsonSetting;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherSettingsApplication {

    private Stage stage;
    private final List<JsonSetting> changedSettings = new ArrayList<>();

    private final JsonFile json = JustDoomLauncher.INSTANCE.getFiles().getLauncherFile();

    public LauncherSettingsApplication() {
        //this.fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("launcher-settings-view.fxml")).load();
    }

    public void start() throws IOException {

        Group group = new Group();
        Scene scene = new Scene(group, 600, 400);

        stage = new Stage();
        stage.setTitle("Launcher Settings");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);
        stage.setScene(scene);
        stage.show();

        BorderPane borderPane = new BorderPane();
        group.getChildren().add(borderPane);

        Text text = new Text("Launcher Settings");
        text.setStyle("-fx-font-size: 24px; -fx-text-alignment: center; -fx-font-weight: bold; -fx-font-family: \"Arial\"; -fx-start-margin: 10px;");
        text.applyCss();
        text.wrappingWidthProperty().set(scene.getWidth());
        text.setTranslateY(50);
        borderPane.getChildren().add(text);

        CheckBox updateCheckBox = new CheckBox("Automatically check for updates");
        updateCheckBox.setSelected(json.getSetting("update").getAsBoolean());
        updateCheckBox.setTranslateX(10);
        updateCheckBox.setTranslateY(100);
        group.getChildren().add(updateCheckBox);
        updateCheckBox.setOnAction(event -> {
            CheckBox checkBox = (CheckBox) event.getSource();
            JsonSetting setting = json.getSetting("update").copy();
            if (changedSettings.contains(setting)) {
                changedSettings.remove(setting);
                return;
            }
            setting.setValue(checkBox.isSelected());
            changedSettings.add(setting);
        });

        CheckBox consoleCheckBox = new CheckBox("Open the console on launch");
        consoleCheckBox.setSelected(json.getSetting("openConsole").getAsBoolean());
        consoleCheckBox.setTranslateX(10);
        consoleCheckBox.setTranslateY(120);
        group.getChildren().add(consoleCheckBox);
        consoleCheckBox.setOnAction(event -> {
            CheckBox checkBox = (CheckBox) event.getSource();
            JsonSetting setting = json.getSetting("openConsole").copy();
            if (changedSettings.contains(setting)) {
                changedSettings.remove(setting);
                return;
            }
            setting.setValue(checkBox.isSelected());
            changedSettings.add(setting);
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-border-width: 0; -fx-background-color: lime;");
        saveButton.setTranslateX(20);
        saveButton.setTranslateY(140);
        group.getChildren().add(saveButton);
        saveButton.setOnAction(event -> {
            try {
                for (JsonSetting setting : json.getSettings()) {
                    for (JsonSetting setting1 : changedSettings) {
                        if (setting.isSetting(setting1)) {
                            setting.setValue(setting1.getValue());
                            setting.setUpdated(true);
                        }
                    }
                }
                json.save();
                changedSettings.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-border-width: 0; -fx-background-color: red;");
        cancelButton.setTranslateX(100);
        cancelButton.setTranslateY(140);
        group.getChildren().add(cancelButton);
        cancelButton.setOnAction(event -> close());


//        stage = new Stage();
//        stage.setTitle("Settings");
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(LauncherApplication.primaryStage);
//
//        //Scene scene = new Scene(fxmlLoader);
//        stage.setScene(scene);
//        stage.show();
//
//        ((Button) scene.lookup("#back")).setOnAction(event -> close());
//
//        ((CheckBox) scene.lookup("#update")).setSelected(json.getSetting("update").getAsBoolean());
//        ((CheckBox) scene.lookup("#update")).setOnAction(event -> {
//            CheckBox checkBox = (CheckBox) event.getSource();
//            JsonSetting setting = json.getSetting("update").copy();
//            if (changedSettings.contains(setting)) {
//                System.out.println("remove");
//                changedSettings.remove(setting);
//                return;
//            }
//            setting.setValue(checkBox.isSelected());
//            changedSettings.add(setting);
//        });
//
//        ((CheckBox) scene.lookup("#console")).setSelected(json.getSetting("openConsole").getAsBoolean());
//        ((CheckBox) scene.lookup("#console")).setOnAction(event -> {
//            CheckBox checkBox = (CheckBox) event.getSource();
//            JsonSetting setting = json.getSetting("openConsole").copy();
//            if (changedSettings.contains(setting)) {
//                changedSettings.remove(setting);
//                return;
//            }
//            setting.setValue(checkBox.isSelected());
//            changedSettings.add(setting);
//        });
//
//        ((Button) scene.lookup("#save")).setOnAction(event -> {
//            try {
//                for (JsonSetting setting : json.getSettings()) {
//                    for (JsonSetting setting1 : changedSettings) {
//                        if (setting.isSetting(setting1)) {
//                            setting.setValue(setting1.getValue());
//                            setting.setUpdated(true);
//                        }
//                    }
//                }
//                System.out.println("save");
//                json.save();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    public void close() {
        stage.close();
    }
}
