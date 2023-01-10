package com.imjustdoom.justdoomlauncher.application;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Group group = new Group();
        Scene scene = new Scene(group, 600, 400);

        stage.setTitle("Launcher Settings");
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
    }
}