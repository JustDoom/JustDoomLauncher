package com.imjustdoom.doomlauncher.justdoomlauncher.project;

import java.io.File;

public class ProjectFront {

    private int id;
    private String name;
    private File logo;
    private String version;

    public ProjectFront(int id, String name, File logo, String version) {
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
