module com.imjustdoom.doomlauncher.justdoomlauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    exports com.imjustdoom.doomlauncher.justdoomlauncher;
    exports com.imjustdoom.doomlauncher.justdoomlauncher.application;
    opens com.imjustdoom.doomlauncher.justdoomlauncher.application to javafx.fxml;
    //exports com.imjustdoom.doomlauncher.justdoomlauncher.controller;
    //opens com.imjustdoom.doomlauncher.justdoomlauncher.controller to javafx.fxml;
}