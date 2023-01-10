package com.imjustdoom.justdoomlauncher.old.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.justdoomlauncher.old.files.setting.JsonSetting;
import com.imjustdoom.justdoomlauncher.old.project.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectFiles {

    private final Path filePath;
    private Path launcherFilePath, mainFilePath;
    private JsonFile launcherJson;

    public ProjectFiles(Path filePath) {
        this.filePath = Path.of(filePath.getParent() + "/JustDoomLauncherFiles");
        this.mainFilePath = filePath.getParent();
    }

    public void init() {

        // Create launcher files directory
        if (!filePath.toFile().exists()) {
            filePath.toFile().mkdirs();
        }

        // Create launcher files directory
        launcherFilePath = Path.of(filePath + "/launcher");
        if (!Files.exists(launcherFilePath)) {
            try {
                Files.createDirectory(launcherFilePath);
                Files.createDirectory(Path.of(launcherFilePath + "/assets"));
                Files.createDirectory(Path.of(launcherFilePath + "/assets/images"));

                InputStream stream = JustDoomLauncher.class.getResourceAsStream("/assets/placeholder.png");
                assert stream != null;
                Files.copy(stream, Path.of(launcherFilePath + "/assets/images/placeholder.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create backup directory
        Path projectsFilePath = Path.of(filePath + "/backup");
        if (!Files.exists(projectsFilePath)) {
            try {
                Files.createDirectory(projectsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File launcherJsonFile = createNewFile(filePath + "/launcher/launcher.json");
        List<JsonSetting> launcherJsonSettings = new ArrayList<>();
        launcherJsonSettings.add(new JsonSetting("version", "", ConstantSettings.VERSION, String.class, true));
        launcherJsonSettings.add(new JsonSetting("update", "settings", true, Boolean.class));
        launcherJsonSettings.add(new JsonSetting("openConsole", "settings", true, Boolean.class));

        this.launcherJson = new JsonFile(launcherJsonFile, launcherJsonSettings);

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(launcherJsonFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonElement parser = new JsonParser().parse(reader);

        if (parser.isJsonNull()) {
            launcherJson.setJson(new JsonObject());
            try {
                launcherJson.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            launcherJson.setJson(parser.getAsJsonObject());
            launcherJson.load();
        }
    }

    public void createDirectory(String directory, Project project) throws IOException {

        Path path = Paths.get(filePath.toString(), directory);
        if (!path.toFile().exists()) path.toFile().mkdirs();

        File file = createNewFile(path + "/data.json");
        JsonFile jsonFile = new JsonFile(file);

        jsonFile.getSettings().add(new JsonSetting("id", "", project.getId(), Integer.class));
        jsonFile.getSettings().add(new JsonSetting("name", "", project.getName(), String.class));
        jsonFile.getSettings().add(new JsonSetting("version", "", project.getVersion(), String.class));
        jsonFile.getSettings().add(new JsonSetting("imageBase64", "", "", String.class));
        jsonFile.getSettings().add(new JsonSetting("description", "", project.getDescription(), String.class));
        jsonFile.getSettings().add(new JsonSetting("author", "", project.getAuthor(), String.class));
        jsonFile.getSettings().add(new JsonSetting("main", "", "", String.class));

        String startup;
        if (new File(JustDoomLauncher.INSTANCE.getFiles().mainFilePath + "\\jre\\bin\\java.exe").exists()) {
            startup = "\"ustDoomLauncher.INSTANCE.getFiles().getMainFilePath()" + "\\jre\\bin\\java.exe\" -jar %file%";
        } else {
            startup = "java -jar %file%";
        }
        jsonFile.getSettings().add(new JsonSetting("startup", "", startup, String.class));

        jsonFile.load();

        project.setInstalled(true);
        project.setFile(jsonFile);
        project.setDirectory(path.toAbsolutePath().toString());

        //jsonElement.getAsJsonObject().addProperty("imageBase64", project.getLogo());
    }

    public void deleteDirectory(String directory) throws IOException {
        Path path = Paths.get(filePath.toString(), directory);
        if (path.toFile().exists()) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    public File createNewFile(Path path) {
        return createNewFile(path.toString());
    }

    public File createNewFile(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Path getFilePath() {
        return filePath;
    }

    public Path getMainFilePath() {
        return mainFilePath;
    }

    public Path getLauncherFilePath() {
        return launcherFilePath;
    }

    public JsonFile getLauncherFile() {
        return launcherJson;
    }

    public void setLauncherFile(JsonFile json) {
        launcherJson = json;
    }
}