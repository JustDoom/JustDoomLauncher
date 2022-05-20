package com.imjustdoom.doomlauncher.justdoomlauncher.project;

import com.imjustdoom.doomlauncher.justdoomlauncher.files.JsonFile;

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
    private JsonFile file;
    private String directory;
    private String downloadUrl;

    private ProjectTab tab;

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

        this.tab = new ProjectTab(id, name, logo, version);
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

    public ProjectTab getFront() {
        return tab;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public JsonFile getFile() {
        return file;
    }

    public void setFile(JsonFile file) {
        this.file = file;
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
