package com.imjustdoom.doomlauncher.justdoomlauncher.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imjustdoom.doomlauncher.justdoomlauncher.files.setting.JsonSetting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JsonFile {

    private File file;
    private JsonObject json;
    private List<JsonSetting> settings;

    public JsonFile(File file, List<JsonSetting> settings, JsonObject json) {
        this.file = file;
        this.json = json;
        this.settings = settings;
    }

    public JsonFile(File file, List<JsonSetting> settings) {
        this(file, settings, new JsonObject());
    }

    public JsonFile(File file) {
        this(file, new ArrayList<>(), new JsonObject());
    }

    public void setSetting(String key, Object value) {
        for (JsonSetting setting : this.settings) {
            if (!setting.isSetting(key, value)) continue;
            setting.setValue(value);
        }
    }

    public JsonSetting getSetting(String key) {
        for (JsonSetting setting : this.settings) {
            if (!setting.isSetting(key)) continue;
            return setting;
        }
        return null;
    }

    public List<JsonSetting> getSettings() {
        return settings;
    }

    public void save() throws IOException {
        Writer writer = new FileWriter(file);

        JsonObject baseJson = new JsonObject();
        for (JsonSetting setting : this.settings) {
            JsonObject json = baseJson;

            for (String elmt : setting.getPath().split("\\.")) {
                if (elmt == null || elmt.equals("")) continue;
                if (!json.has(elmt)) {
                    json.add(elmt, new JsonObject());
                }
                json = json.get(elmt).getAsJsonObject();
            }

            if (json.get(setting.getKey()) == null || setting.isOverride() || setting.isUpdated()) {
                if (setting.isBoolean()) json.addProperty(setting.getKey(), (Boolean) setting.getValue());
                else if (setting.isInteger()) json.addProperty(setting.getKey(), (Integer) setting.getValue());
                else if (setting.isString()) json.addProperty(setting.getKey(), (String) setting.getValue());
                setting.setUpdated(false);
            }
        }

        this.json = baseJson;
        new Gson().toJson(baseJson, writer);
        writer.flush();
        writer.close();
    }

    public void load() {
        for (JsonSetting setting : this.settings) {

            for (String elmt : setting.getPath().split("\\.")) {
                if (elmt == null || elmt.equals("")) continue;
                if (!json.has(elmt)) continue;
                json = json.get(elmt).getAsJsonObject();
            }

            if (json.get(setting.getKey()) != null) {
                if (setting.isBoolean()) setting.setValue(json.get(setting.getKey()).getAsBoolean());
                else if (setting.isInteger()) setting.setValue(json.get(setting.getKey()).getAsInt());
                else if (setting.isString()) setting.setValue(json.get(setting.getKey()).getAsString());
            }
        }
    }
}