package com.imjustdoom.doomlauncher.justdoomlauncher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.LauncherApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.ProjectFiles;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.GameProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.Project;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.Config;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JustDoomLauncher {

    public static JustDoomLauncher INSTANCE;

    private final Map<Integer, Project> projectFronts = new HashMap<>();
    private final Map<Integer, GameProcess> gameProcesses = new HashMap<>();
    private final ProjectFiles files;

    public JustDoomLauncher(String[] args) throws URISyntaxException, IOException {

        INSTANCE = this;

        this.files = new ProjectFiles(Path.of(new File(JustDoomLauncher.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).toURI()));

        this.files.init();

        Config.init();

        projectFronts.put(1, new Project(1, "Falling James", "", "Java", "1.1.1", "JustDoom",
                "Amazing falling game", new File(this.files.getLauncherFilePath().toString() + "/assets/images/placeholder.png"),
                "https://flappyac.com/file.jar"));

        projectFronts.put(2, new Project(2, "Sabre", "", "Java", "1.0.0", "Project-Cepi",
                "Amazing minestom pre built jar with useful features\nHas a cool config",
                new File(this.files.getLauncherFilePath().toString() + "/assets/images/placeholder.png"),
                "https://github.com/Project-Cepi/Sabre/releases/download/latest/sabre-1.0.0-all.jar"));

        for (File dir : files.getFilePath().toFile().listFiles()) {
            if (!dir.isDirectory()) continue;

            for (File file : dir.listFiles()) {
                if (file.getName().equals("data.json")) {
                    try {
                        JsonReader reader = new JsonReader(Files.newBufferedReader(file.toPath()));
                        reader.setLenient(true);
                        JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

                        this.projectFronts.get(jsonElement.getAsJsonObject().get("id").getAsInt()).setInstalled(true);

                        this.projectFronts.get(jsonElement.getAsJsonObject().get("id").getAsInt()).setJson(jsonElement);
                        this.projectFronts.get(jsonElement.getAsJsonObject().get("id").getAsInt()).setDirectory(dir.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        new Thread(() -> Application.launch(LauncherApplication.class, args)).start();
    }

    public boolean checkLauncherUptoDate() {
        String latestVersion = getLatestVersion();
        return latestVersion == null || latestVersion.equals(Config.VERSION);
    }

    public String getLatestVersion() {
        try {
            URL uri = new URL(Config.LAUNCHER_JSON_ONLINE);
            uri.openConnection().setUseCaches(false);
            // Clear the cache for this connection so that the latest version is downloaded

            InputStream inputStream = uri.openStream();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.setLenient(true);
            JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

            System.out.println(jsonElement.get("version").getAsString());
            return jsonElement.get("version").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        new JustDoomLauncher(args);
    }

    public Map<Integer, Project> getProjects() {
        return projectFronts;
    }

    public ProjectFiles getFiles() {
        return files;
    }

    public Map<Integer, GameProcess> getGameProcesses() {
        return gameProcesses;
    }
}