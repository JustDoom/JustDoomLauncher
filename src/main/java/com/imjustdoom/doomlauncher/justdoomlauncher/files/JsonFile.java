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
    private List<JsonSetting> missingSettings = new ArrayList<>();

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

        JsonObject json = this.json.deepCopy();
        for(JsonSetting setting : this.settings) {

            JsonObject tmpJson = json;
            for (String elmt : setting.getPath().split("\\.")) {
                if(elmt == null || elmt.equals("")) continue;
                if(tmpJson.has(elmt)) tmpJson = tmpJson.get(elmt).getAsJsonObject();
                else {
                    tmpJson.add(elmt, new JsonObject());
                    tmpJson = tmpJson.get(elmt).getAsJsonObject();
                }
            }

            // Set the value
            if (setting.isBoolean()) tmpJson.addProperty(setting.getKey(), (Boolean) setting.getValue());
            else if (setting.isInteger()) tmpJson.addProperty(setting.getKey(), (Integer) setting.getValue());
            else if (setting.isString()) tmpJson.addProperty(setting.getKey(), (String) setting.getValue());
        }

        new Gson().toJson(json, writer);
        writer.flush();
        writer.close();
    }

    public void load() {
        JsonObject json = this.json.deepCopy();

        SETTINGS:
        for (JsonSetting setting : this.settings) {

            JsonObject tmpJson = json.deepCopy();
            for (String elmt : setting.getPath().split("\\.")) {
                if(elmt == null || elmt.equals("")) continue;
                if(tmpJson.has(elmt)) tmpJson = tmpJson.get(elmt).getAsJsonObject();
                else {
                    missingSettings.add(setting);
                    continue SETTINGS;
                }
            }

            if(tmpJson.get(setting.getKey()) == null) {
                missingSettings.add(setting);
                continue;
            }

            if (setting.isBoolean()) setting.setValue(tmpJson.get(setting.getKey()).getAsBoolean());
            else if (setting.isInteger()) setting.setValue(tmpJson.get(setting.getKey()).getAsInt());
            else if (setting.isString()) setting.setValue(tmpJson.get(setting.getKey()).getAsString());
        }

        try {
            Writer writer = new FileWriter(file);

            // Add missing settings
            for (JsonSetting setting : missingSettings) {

                JsonObject tmpJson = json;
                for (String elmt : setting.getPath().split("\\.")) {
                    if (elmt == null || elmt.equals("")) continue;
                    if (tmpJson.has(elmt)) tmpJson = tmpJson.get(elmt).getAsJsonObject();
                    else {
                        tmpJson.add(elmt, new JsonObject());
                        tmpJson = tmpJson.get(elmt).getAsJsonObject();
                    }
                }

                // Set the value
                if (setting.isBoolean()) tmpJson.addProperty(setting.getKey(), (Boolean) setting.getValue());
                else if (setting.isInteger()) tmpJson.addProperty(setting.getKey(), (Integer) setting.getValue());
                else if (setting.isString()) tmpJson.addProperty(setting.getKey(), (String) setting.getValue());
            }

            new Gson().toJson(json, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setJson(JsonObject json) {
        this.json = json;
    }

    public JsonObject getJson() {
        return json;
    }
}