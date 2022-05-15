package com.imjustdoom.doomlauncher.justdoomlauncher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.LauncherApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.ProjectFiles;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.GameProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.Project;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.ProjectFront;
import com.imjustdoom.doomlauncher.justdoomlauncher.settings.Settings;
import javafx.application.Application;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

        projectFronts.put(1, new Project(1, "Falling James", "", "Java", "1.1.1", "JustDoom",
                "Amazing falling game", new File(this.files.getLauncherFilePath().toString() + "/assets/images/placeholder.png"),
                "https://flappyac.com/file.jar"));

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
        try {
            URI uri = new URI(Settings.LAUNCHER_JSON_ONLINE);
            InputStream inputStream = uri.toURL().openStream();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.setLenient(true);
            JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

            return jsonElement.get("version").getAsString().equals(Settings.VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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