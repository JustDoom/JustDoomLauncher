package com.imjustdoom.justdoomlauncher.old.process;

import com.imjustdoom.justdoomlauncher.old.application.InstallApplication;
import com.imjustdoom.justdoomlauncher.old.files.JsonFile;
import javafx.application.Platform;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadProcess {

    private String download;
    private String path;

    public DownloadProcess(String download, String path) {
        this.download = download;
        this.path = path;
    }

    public void download(JsonFile jsonFile) throws IOException {
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

        jsonFile.getSetting("main").setValue(filename);
        jsonFile.save();

        Platform.runLater(installApplication::close);
    }

    public String getDownload() {
        return download;
    }

    public String getPath() {
        return path;
    }
}
