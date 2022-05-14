package com.imjustdoom.doomlauncher.justdoomlauncher.process;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.InstallApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.LauncherApplication;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadProcess {

    private String download;
    private String path;

    public DownloadProcess(String download, String path) {
        this.download = download;
        this.path = path;
    }

    public void download(JsonObject jsonObject) throws IOException {
        String filename = "file.jar";

        InstallApplication installApplication = new InstallApplication();

        new Thread(() -> Platform.runLater(() -> {
            try {
                installApplication.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })).start();
        try {

            URL url = new URL(download);
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            long completeFileSize = httpConnection.getContentLength();

            BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path + "/" + filename);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

            byte[] data = new byte[1024];
            int byteContent;
            int downloaded = 0;
            while ((byteContent = in.read(data, 0, 1024)) != -1) {
                downloaded += byteContent;

                final double currentProgress = ((((double) downloaded) / ((double) completeFileSize)));

                Platform.runLater(() -> installApplication.update(currentProgress));

                bout.write(data, 0, byteContent);
            }
            bout.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonObject.addProperty("main", filename);

        Writer writer = new FileWriter(path + "/data.json");
        new Gson().toJson(jsonObject, writer);
        writer.flush();
        writer.close();

        Platform.runLater(installApplication::close);
    }

    public String getDownload() {
        return download;
    }

    public String getPath() {
        return path;
    }
}
