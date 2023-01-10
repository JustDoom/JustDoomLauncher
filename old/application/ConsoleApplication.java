package com.imjustdoom.justdoomlauncher.old.application;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;

public class ConsoleApplication {
    private Stage stage;
    private TextArea console;
    private TextField textField;

    public final void start(OutputStream stdin) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("console-view.fxml"));

        this.stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(LauncherApplication.primaryStage);

        final Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();

        console = (TextArea) scene.lookup("#console");
        this.textField = (TextField) scene.lookup("#input");

        this.textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addText(textField.getText() + "\n");
                try {
                    stdin.write(textField.getText().getBytes());
                    stdin.write(System.lineSeparator().getBytes());
                    stdin.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                textField.setText("");
            }
        });

        final URL styleSheetUrl = getStyleSheetUrl();
        if (styleSheetUrl != null) {
            scene.getStylesheets().add(styleSheetUrl.toString());
        }
    }

    protected URL getStyleSheetUrl() {
        final String styleSheetName = "style.css";
        URL url = getClass().getResource(styleSheetName);
        if (url != null) {
            return url;
        }
        url = ConsoleApplication.class.getResource(styleSheetName);
        return url;
    }

    public void setTitle(final String title) {
        Platform.runLater(() -> this.stage.setTitle(title));
    }

    public void addText(final String text) {
        Platform.runLater(() -> this.console.appendText(text));
    }
}
