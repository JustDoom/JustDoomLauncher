package com.imjustdoom.doomlauncher.justdoomlauncher.project;

import com.google.gson.JsonObject;

import java.io.File;

public class Project {

    private int id;
    private String name;
    private String path;
    private String type;
    private String version;
    private String author;
    private String description;
    private File logo;
    private boolean installed;
    private JsonObject json;
    private String directory;
    private String downloadUrl;

    private ProjectFront front;

    public Project(int id, String name, String path, String type, String version, String author, String description,
                   File logo, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.version = version;
        this.author = author;
        this.description = description;
        this.logo = logo;
        this.installed = false;
        this.downloadUrl = downloadUrl;

        this.front = new ProjectFront(id, name, logo, version);
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public File getLogo() {
        return logo;
    }

    public ProjectFront getFront() {
        return front;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public JsonObject getJson() {
        return json;
    }

    public void setJson(JsonObject json) {
        this.json = json;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
