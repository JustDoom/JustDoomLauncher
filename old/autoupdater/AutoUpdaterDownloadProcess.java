package com.imjustdoom.justdoomlauncher.old.autoupdater;

import com.imjustdoom.justdoomlauncher.old.enums.DownloadState;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class AutoUpdaterDownloadProcess implements Runnable {

    private String downloadUrl;
    private String path;
    private double currentProgress = 0.0;
    private DownloadState state = DownloadState.PAUSED;

    public AutoUpdaterDownloadProcess(String url, String path) {
        this.downloadUrl = url;
        this.path = path;
    }

    public double getCurrentProgress() {
        return currentProgress;
    }

    public DownloadState getDownloadState() {
        return state;
    }

    @Override
    public void run() {
        System.out.println("Download process started");

        try {

            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            HttpURLConnection httpConnection = (HttpURLConnection) (connection);
            httpConnection.setUseCaches(false);
            long completeFileSize = httpConnection.getContentLength();

            BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

            byte[] data = new byte[1024];
            int byteContent;
            int downloaded = 0;
            state = DownloadState.DOWNLOADING;
            while ((byteContent = in.read(data, 0, 1024)) != -1) {
                downloaded += byteContent;

                currentProgress = ((((double) downloaded) / ((double) completeFileSize)));

                bout.write(data, 0, byteContent);
            }
            bout.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        state = DownloadState.COMPLETED;
        System.out.println("Download process finished");
    }
}
