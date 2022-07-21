package com.imjustdoom.doomlauncher.justdoomlauncher.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Config {

    public static class Settings {
        public static boolean DEBUG = false;
        public static boolean AUTO_UPDATE = true;
        public static boolean OPEN_CONSOLE = true;
    }

    public static void init() {
        JsonFile json = JustDoomLauncher.INSTANCE.getFiles().getLauncherFile();

        Settings.AUTO_UPDATE = json.getSetting("update").getAsBoolean();
        Settings.OPEN_CONSOLE = json.getSetting("openConsole").getAsBoolean();
    }

    public static void saveConfig(JsonObject json) throws IOException {
        Writer writer = new FileWriter(JustDoomLauncher.INSTANCE.getFiles().getFilePath() + "/launcher/launcher.json");
        new Gson().toJson(json, writer);
        writer.flush();
        writer.close();
    }

    public static void setSetting(String key, String value, JsonObject json, boolean save) {
        json.get("settings").getAsJsonObject().addProperty(key, value);

        if (!save) return;
        try {
            saveConfig(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}