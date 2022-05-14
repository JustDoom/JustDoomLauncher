package com.imjustdoom.doomlauncher.justdoomlauncher.files;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.project.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ProjectFiles {

    private final Path filePath;
    private Path launcherFilePath;

    public ProjectFiles(Path filePath) {
        this.filePath = Path.of(filePath.getParent() + "/JustDoomLauncherFiles");
    }

    public void init() {
        if (!filePath.toFile().exists()) {
            filePath.toFile().mkdirs();
        }

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
    }

    public void createDirectory(String directory, Project project) throws IOException {
        Path path = Paths.get(filePath.toString(), directory);
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }

        Path data = Path.of(path + "/data.json");
        if (!Files.exists(data)) {
            System.out.printf("Could not find %s, generating it for you...\n", "data.json");
            InputStream stream = JustDoomLauncher.class.getResourceAsStream("/assets/" + "data.json");
            assert stream != null;
            Files.copy(stream, Path.of(path + "/data.json"));
        }

        JsonReader reader = new JsonReader(Files.newBufferedReader(new File(path + "/data.json").toPath()));
        reader.setLenient(true);
        JsonObject jsonElement = new JsonParser().parse(reader).getAsJsonObject();

        jsonElement.getAsJsonObject().addProperty("id", project.getId());
        jsonElement.getAsJsonObject().addProperty("name", project.getName());
        jsonElement.getAsJsonObject().addProperty("version", project.getVersion());
        jsonElement.getAsJsonObject().addProperty("description", project.getDescription());
        jsonElement.getAsJsonObject().addProperty("author", project.getAuthor());
        jsonElement.getAsJsonObject().addProperty("startup", "java -jar %file%");

        Writer writer = new FileWriter(path + "/data.json");
        new Gson().toJson(jsonElement, writer);
        writer.flush();
        writer.close();

        project.setInstalled(true);

        project.setJson(jsonElement);
        project.setDirectory(path.toAbsolutePath().toString());

        //jsonElement.getAsJsonObject().addProperty("imageBase64", project.getLogo());
    }

    public void deleteDirectory(String directory) throws IOException {
        Path path = Paths.get(filePath.toString(), directory);
        if (path.toFile().exists()) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }



    public Path getFilePath() {
        return filePath;
    }

    public Path getLauncherFilePath() {
        return launcherFilePath;
    }
}