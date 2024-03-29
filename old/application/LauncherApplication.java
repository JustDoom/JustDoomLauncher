package com.imjustdoom.justdoomlauncher.old.application;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.justdoomlauncher.old.files.ConstantSettings;
import com.imjustdoom.justdoomlauncher.old.process.DownloadProcess;
import com.imjustdoom.justdoomlauncher.old.process.GameProcess;
import com.imjustdoom.justdoomlauncher.old.project.Project;
import com.imjustdoom.justdoomlauncher.old.project.ProjectTab;
import com.imjustdoom.justdoomlauncher.old.files.Config;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

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
        stage.setTitle("JustDoom Launcher " + ConstantSettings.VERSION);

        Scene scene1 = new Scene(scene, 600, 400);
        stage.setScene(scene1);
        stage.show();

        stage.setOnHiding(event -> Platform.runLater(() -> System.exit(0)));

        VBox vBox = ((VBox) scene.lookup("#projects"));
        scrollPane = ((ScrollPane) scene.lookup("#scrollPane"));

        // vBox.getChildren().clear();
        boolean notWhite = true;
        for (int id : JustDoomLauncher.INSTANCE.getProjects().keySet()) {
            ProjectTab projectTab = JustDoomLauncher.INSTANCE.getProjects().get(id).getFront();
            AnchorPane projectPane = new AnchorPane();

            if (notWhite) projectPane.setStyle("-fx-background-color: #d3d3d3;");
            notWhite = !notWhite;
            projectPane.setPrefWidth(200);
            projectPane.setOnMouseClicked(this::onProjectClicked);

            ImageView imageView = new ImageView(new Image(projectTab.getLogo().toURI().toString()));
            imageView.setFitWidth(32);
            imageView.setFitHeight(32);

            projectPane.getChildren().add(imageView);

            Label nameLabel = new Label(projectTab.getName());
            nameLabel.setLayoutX(36);
            projectPane.getChildren().add(nameLabel);

            Label versionLabel = new Label(projectTab.getVersion());
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
            if (!JustDoomLauncher.INSTANCE.getFiles().getLauncherFile().getSetting("update").getAsBoolean()) return;
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

        ((Button) scene.lookup("#settings")).setOnAction(event -> {
            LauncherSettingsApplication launcherSettingsApplication = new LauncherSettingsApplication();
            try {
                launcherSettingsApplication.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onProjectClicked(MouseEvent event) {
        Pane pane = event.getTarget() instanceof Pane ? ((Pane) event.getTarget()) : ((Pane) ((Node) event.getTarget()).getParent().getParent());
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(Integer.parseInt(pane.getId()));

        this.selectedProject = Integer.parseInt(pane.getId());

        loadProjectInfo(project);
    }

    public void onInstallClick(MouseEvent event) {
        if (selectedProject == -1) {
            return;
        }
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(this.selectedProject);

        if (project.isInstalled()) {
            if (JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
                JustDoomLauncher.INSTANCE.getGameProcesses().get(project.getId()).kill();
                JustDoomLauncher.INSTANCE.getGameProcesses().remove(project.getId());
                loadProjectInfo(project);
                return;
            }

            GameProcess gameProcess = new GameProcess(project.getFile().getSetting("main").getAsString(),
                    project.getFile().getSetting("startup").getAsString(), project.getDirectory(),
                    Config.Settings.OPEN_CONSOLE);

            JustDoomLauncher.INSTANCE.getGameProcesses().put(project.getId(), gameProcess);
            new Thread(gameProcess::run).start();
        } else {
            try {
                if (project.getId() == 2) {
                    SabreVersionsApplication sabreVersionsApplication = new SabreVersionsApplication();
                    sabreVersionsApplication.start(project);
                    return;
                }
                JustDoomLauncher.INSTANCE.getFiles().createDirectory(project.getName(), project);
                DownloadProcess downloadProcess = new DownloadProcess(project.getDownloadUrl(), project.getDirectory());
                new Thread(() -> {
                    try {
                        downloadProcess.download(project.getFile());
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
        if (selectedProject == -1) {
            return;
        }
        Project project = JustDoomLauncher.INSTANCE.getProjects().get(this.selectedProject);

        if (project.isInstalled()) {
            try {
                //BackupProcess backupProcess = new BackupProcess(Path.of(project.getDirectory()));
                //backupProcess.backup();

                if (JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
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
        imageView.setImage(new Image(project.getLogo().toURI().toString()));
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);

        if (JustDoomLauncher.INSTANCE.getGameProcesses().containsKey(project.getId())) {
            ((Button) scene.lookup("#projectButton")).setText("Kill");
        } else if (project.isInstalled()) {
            ((Button) scene.lookup("#projectButton")).setText("Play");
        } else {
            ((Button) scene.lookup("#projectButton")).setText("Install");
        }
    }
}