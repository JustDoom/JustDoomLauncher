package com.imjustdoom.doomlauncher.justdoomlauncher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.LauncherApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.Config;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.ConstantSettings;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.JsonFile;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.ProjectFiles;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.setting.JsonSetting;
import com.imjustdoom.doomlauncher.justdoomlauncher.process.GameProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.Project;
import javafx.application.Application;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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

        // Load/Create requires files/file handlers
        this.files = new ProjectFiles(Path.of(new File(JustDoomLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toURI()));
        this.files.init();

        Config.init();

        // Add projects
        projectFronts.put(1, new Project(1, "Falling James", "", "Java", "1.1.1", "JustDoom",
                "Amazing falling game", new File(this.files.getLauncherFilePath().toString() + "/assets/images/placeholder.png"),
                "https://flappyac.com/file.jar"));

        projectFronts.put(2, new Project(2, "Sabre", "", "Java", "1.0.0", "Project-Cepi",
                "Amazing minestom pre built jar with useful features\nHas a cool config",
                new File(this.files.getLauncherFilePath().toString() + "/assets/images/placeholder.png"),
                "https://github.com/Project-Cepi/Sabre/releases/download/latest/sabre-1.0.0-all.jar"));

        // Load projects from files
        for (File dir : files.getFilePath().toFile().listFiles()) {
            if (!dir.isDirectory()) continue;
            for (File file : dir.listFiles()) {
                if(!file.getName().equals("data.json")) continue;

                JsonFile jsonFile = new JsonFile(file);
                jsonFile.getSettings().add(new JsonSetting("id", "", -1, Integer.class));
                jsonFile.getSettings().add(new JsonSetting("name", "", "", String.class));
                jsonFile.getSettings().add(new JsonSetting("version", "", "", String.class));
                jsonFile.getSettings().add(new JsonSetting("description", "", "", String.class));
                jsonFile.getSettings().add(new JsonSetting("imageBase64", "", "", String.class)); // TODO: get default imageBase64
                jsonFile.getSettings().add(new JsonSetting("author", "", "", String.class));
                jsonFile.getSettings().add(new JsonSetting("main", "", "", String.class));
                jsonFile.getSettings().add(new JsonSetting("startup", "", "", String.class));

                JsonReader reader = null;
                try {
                    reader = new JsonReader(new FileReader(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                JsonElement parser = new JsonParser().parse(reader);

                if(parser.isJsonNull()) {
                    jsonFile.setJson(new JsonObject());
                    try {
                        jsonFile.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    jsonFile.setJson(parser.getAsJsonObject());
                    jsonFile.load();
                }

                Project project = this.projectFronts.get(jsonFile.getSetting("id").getAsInteger());
                project.setInstalled(true);
                project.setFile(jsonFile);
                project.setDirectory(dir.getAbsolutePath());
            }
        }

        new Thread(() -> Application.launch(LauncherApplication.class, args)).start();
    }

    public boolean checkLauncherUptoDate() {
        String latestVersion = getLatestVersion();
        return latestVersion == null || latestVersion.equals(ConstantSettings.VERSION);
    }

    public String getLatestVersion() {
        try {
            URL uri = new URL(ConstantSettings.LAUNCHER_VERSION_JSON);
            uri.openConnection().setUseCaches(false);
            // Clear the cache for this connection so that the latest version is downloaded

            InputStream inputStream = uri.openStream();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.setLenient(true);
            JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

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