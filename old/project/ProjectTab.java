package com.imjustdoom.justdoomlauncher.old.project;

import java.io.File;

public class ProjectTab {

    private int id;
    private String name;
    private File logo;
    private String version;

    public ProjectTab(int id, String name, File logo, String version) {
        this.name = name;
        this.logo = logo;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public File getLogo() {
        return logo;
    }

    public String getVersion() {
        return version;
    }
}
