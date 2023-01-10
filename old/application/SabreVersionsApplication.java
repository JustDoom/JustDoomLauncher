package com.imjustdoom.justdoomlauncher.old.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.justdoomlauncher.old.process.DownloadProcess;
import com.imjustdoom.justdoomlauncher.old.project.Project;
import com.sun.javafx.scene.control.LabeledText;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class SabreVersionsApplication {

    private Stage stage;
    private Button back;

    public SabreVersionsApplication() {

    }

    public void start(Project project) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("sabre-view.fxml"));

        stage = new Stage();
        stage.setTitle("Sabre Versions");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();

        back = (Button) scene.lookup("#back");
        back.setOnAction(event -> close());

        VBox versions = (VBox) scene.lookup("#versions");

        URL uri = new URL("https://api.github.com/repos/Project-Cepi/Sabre/releases");
        uri.openConnection().setUseCaches(false);
        // Clear the cache for this connection so that the latest version is downloaded

        InputStream inputStream = uri.openStream();

        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.setLenient(true);
        JsonArray jsonElement = new JsonParser().parse(reader).getAsJsonArray();

        boolean notWhite = true;
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            int id = element.getAsJsonObject().get("id").getAsInt();

            AnchorPane pane = new AnchorPane();
            if (notWhite) pane.setStyle("-fx-background-color: #d3d3d3;");
            notWhite = !notWhite;
            pane.setPrefWidth(versions.getPrefWidth());
            pane.setPrefHeight(200);
            pane.setPadding(new Insets(5));

            Label text = new Label(element.getAsJsonObject().get("name").getAsString());
            text.setLayoutY(5);
            text.setLayoutX(5);
            pane.getChildren().add(text);

            Button button = new Button("Download");
            button.setId(id + "");
            button.setLayoutY(21);
            button.setLayoutX(5);
            button.setOnMouseClicked(event -> {
                try {
                    Button b = (Button) ((LabeledText) event.getTarget()).getParent();
                    int id2 = Integer.parseInt(b.getId());
                    JustDoomLauncher.INSTANCE.getFiles().createDirectory(project.getName(), project);

                    URL releaseUri = new URL("https://api.github.com/repos/Project-Cepi/Sabre/releases/" + id2);
                    releaseUri.openConnection().setUseCaches(false);

                    InputStream releaseInputStream = releaseUri.openStream();

                    JsonReader releaseReader = new JsonReader(new InputStreamReader(releaseInputStream));
                    releaseReader.setLenient(true);
                    JsonObject releaseJsonElement = new JsonParser().parse(releaseReader).getAsJsonObject();

                    DownloadProcess downloadProcess = new DownloadProcess(
                            releaseJsonElement.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString(),
                            project.getDirectory());

                    close();
                    new Thread(() -> {
                        try {
                            downloadProcess.download(project.getFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    project.setInstalled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pane.getChildren().add(button);

            versions.getChildren().add(pane);
        }

        ScrollPane scrollPane = (ScrollPane) scene.lookup("#scrollPane");

        scrollPane.applyCss();
        scrollPane.layout();

        scrollPane.setVvalue(scrollPane.getVmax());
    }

    public void close() {
        stage.close();
    }
}
