package com.imjustdoom.doomlauncher.justdoomlauncher.process;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.InstallApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.RestartApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.autoupdater.AutoUpdaterDownloadProcess;
import com.imjustdoom.doomlauncher.justdoomlauncher.settings.Settings;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateLauncherProcess {

    public UpdateLauncherProcess() {
    }

    public void updateLauncher() {
        InstallApplication installApplication = new InstallApplication();
        RestartApplication restartApplication = new RestartApplication();

        new Thread(() -> Platform.runLater(() ->{
            try {
                installApplication.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();

        AutoUpdaterDownloadProcess downloadProcess = new AutoUpdaterDownloadProcess(Settings.LATEST_DOWNLOAD, JustDoomLauncher.INSTANCE.getFiles().getMainFilePath() + "\\JustDoomLauncher-1.0-SNAPSHOT.jar");
        new Thread(downloadProcess::run).start();

        System.out.println(downloadProcess.getDownloadState());
        while (downloadProcess.getCurrentProgress() < 1.0) {
            System.out.println(downloadProcess.getCurrentProgress());
            Platform.runLater(() -> installApplication.update(downloadProcess.getCurrentProgress()));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(0);

        Platform.runLater(installApplication::close);
        System.out.println(1);
        new Thread(() -> Platform.runLater(() -> {
            try {
                System.out.println(2);
                restartApplication.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }
}
