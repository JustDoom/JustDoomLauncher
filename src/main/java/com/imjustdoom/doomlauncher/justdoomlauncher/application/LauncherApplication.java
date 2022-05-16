package com.imjustdoom.doomlauncher.justdoomlauncher.application;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.BackupProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.DownloadProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.GameProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.UpdateLauncherProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.Project;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.ProjectFront;
import com.imjustdoom.doomlauncher.justdoomlauncher.settings.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LauncherApplication extends Application {

    private Parent scene;
    public static Stage primaryStage;
    private int selectedProject = -1;

    ScrollPane scrollPane;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(JustDoomLauncher.class.getResource("launcher-view.fxml"));
        scene = fxmlLoader.load();
        stage.setTitle("JustDoom Launcher " + Settings.VERSION);

        Scene scene1 = new Scene(scene, 600, 400);
        stage.setScene(scene1);
        stage.show();

        stage.setOnHiding(event -> Platform.runLater(() -> System.exit(0)));

        VBox vBox = ((VBox) scene.lookup("#projects"));
        scrollPane = ((ScrollPane) scene.lookup("#scrollPane"));

        // vBox.getChildren().clear();
        boolean notWhite = true;
        for(int id : JustDoomLauncher.INSTANCE.getProjects().keySet()) {
            ProjectFront projectFront = JustDoomLauncher.INSTANCE.getProjects().get(id).getFront();
            AnchorPane projectPane = new AnchorPane();

            if(notWhite) projectPane.setStyle("-fx-background-color: #d3d3d3;");
            notWhite = !notWhite;
            projectPane.setPrefWidth(200);
            projectPane.setOnMouseClicked(this::onProjectClicked);

            ImageView imageView = new ImageView(projectFront.getLogo().getAbsolutePath());
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);

            projectPane.getChildren().add(imageView);

            Label nameLabel = new Label(projectFront.getName());
            nameLabel.setLayoutX(36);
            projectPane.getChildren().add(nameLabel);

            Label versionLabel = new Label(projectFront.getVersion());
            versionLabel.setLayoutX(36);
            versionLabel.setLayoutY(15);

            projectPane.getChildren().add(versionLabel);

            projectPane.setId(id + "");

            vBox.getChildren().add(projectPane);
        }

        scrollPane.applyCss();
        scrollPane.layout();

        scrollPane.setVvalue(scrollPane.getVmax());

        scene.lookup("#projectButton").setOnMouseClicked(this::onInstallClick);
        scene.lookup("#projectUninstall").setOnMouseClicked(this::onUninstallClick);

        this.selectedProject = 1;
        loadProjectInfo(JustDoomLauncher.INSTANCE.getProjects().get(1));

        new Thread(() -> {
            boolean uptoDate = JustDoomLauncher.INSTANCE.checkLauncherUptoDate();
            if (!uptoDate) {
                Platform.runLater(() -> {
                    UpdateApplication updateApplication = new UpdateApplication();
                    try {
                        updateApplication.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }).start();
    }

    public void onProjectClicked(MouseEvent event) {
        Pane pane = event.getTarget() instanceof Pane ? ((Pane) event.getTarget()) : ((Pane) ((Node) event.getTarget()).getParent().getParent());
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(Integer.parseInt(pane.getId()));

        this.selectedProject = Integer.parseInt(pane.getId());

        loadProjectInfo(project);
    }

    public void onInstallClick(MouseEvent event) {
        if(selectedProject == -1) {
            return;
        }
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(this.selectedProject);

        if(project.isInstalled()) {
            if(JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
                JustDoomLauncher.INSTANCE.getGameProcesses().get(project.getId()).kill();
                JustDoomLauncher.INSTANCE.getGameProcesses().remove(project.getId());
                loadProjectInfo(project);
                return;
            }

            GameProcess gameProcess = new GameProcess(project.getJson().get("main").getAsString(),
                    project.getJson().get("startup").getAsString(), project.getDirectory(), true);

            JustDoomLauncher.INSTANCE.getGameProcesses().put(project.getId(), gameProcess);
            new Thread(gameProcess::run).start();
        } else {
            try {
                if(project.getId() == 2) {
                    SabreVersionsApplication sabreVersionsApplication = new SabreVersionsApplication();
                    sabreVersionsApplication.start();
                }
                JustDoomLauncher.INSTANCE.getFiles().createDirectory(project.getName(), project);
                DownloadProcess downloadProcess = new DownloadProcess(project.getDownloadUrl(), project.getDirectory());
                new Thread(() -> {
                    try {
                        downloadProcess.download(project.getJson());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                System.out.println("Downloaded");

                project.setInstalled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadProjectInfo(project);
    }

    public void onUninstallClick(MouseEvent event) {
        if(selectedProject == -1) {
            return;
        }
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(this.selectedProject);

        if(project.isInstalled()) {
            try {
                //BackupProcess backupProcess = new BackupProcess(Path.of(project.getDirectory()));
                //backupProcess.backup();

                if(JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
                    JustDoomLauncher.INSTANCE.getGameProcesses().get(project.getId()).kill();
                }
                JustDoomLauncher.INSTANCE.getFiles().deleteDirectory(project.getName());
                project.setInstalled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }

        loadProjectInfo(project);
    }

    public void loadProjectInfo(Project project) {
        ((Label) scene.lookup("#projectName")).setText(project.getName());
        ((Label) scene.lookup("#projectVersion")).setText(project.getVersion());
        ((TextArea) scene.lookup("#projectDescription")).setText(project.getDescription());
        ImageView imageView = ((ImageView) scene.lookup("#projectLogo"));
        imageView.setImage(new Image(project.getLogo().getAbsolutePath()));
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);

        if(JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
            ((Button) scene.lookup("#projectButton")).setText("Kill");
        } else if (project.isInstalled()) {
            ((Button) scene.lookup("#projectButton")).setText("Play");
        } else {
            ((Button) scene.lookup("#projectButton")).setText("Install");
        }
    }
}