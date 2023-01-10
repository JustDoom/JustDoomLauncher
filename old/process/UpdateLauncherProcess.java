package com.imjustdoom.justdoomlauncher.old.process;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.justdoomlauncher.old.application.InstallApplication;
import com.imjustdoom.justdoomlauncher.old.application.RestartApplication;
import com.imjustdoom.justdoomlauncher.old.autoupdater.AutoUpdaterDownloadProcess;
import com.imjustdoom.justdoomlauncher.old.files.ConstantSettings;
import javafx.application.Platform;

import java.io.IOException;

public class UpdateLauncherProcess {

    public UpdateLauncherProcess() {
    }

    public void updateLauncher() {
        InstallApplication installApplication = new InstallApplication();
        RestartApplication restartApplication = new RestartApplication();

        new Thread(() -> Platform.runLater(() -> {
            try {
                installApplication.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();

        AutoUpdaterDownloadProcess downloadProcess = new AutoUpdaterDownloadProcess(ConstantSettings.DOWNLOAD_URL
                + JustDoomLauncher.INSTANCE.getLatestVersion() + "/launcher.jar", JustDoomLauncher.INSTANCE.getFiles().getMainFilePath() + "\\JustDoomLauncher-1.0-SNAPSHOT.jar");
        new Thread(downloadProcess::run).start();

        while (downloadProcess.getCurrentProgress() < 1.0) {
            Platform.runLater(() -> installApplication.update(downloadProcess.getCurrentProgress()));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Platform.runLater(installApplication::close);
        new Thread(() -> Platform.runLater(() -> {
            try {
                restartApplication.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }
}
