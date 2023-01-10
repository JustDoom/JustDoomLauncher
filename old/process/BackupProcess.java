package com.imjustdoom.justdoomlauncher.old.process;

import com.imjustdoom.justdoomlauncher.JustDoomLauncher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupProcess {

    private Path[] backupPaths;

    public BackupProcess(Path... path) {
        this.backupPaths = path;
    }

    public void backup() throws IOException {
        for (Path path : backupPaths) {
            System.out.println("Backup: " + path.toString());
            System.out.println(JustDoomLauncher.INSTANCE.getFiles().getLauncherFilePath() + "\\");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(Path.of(JustDoomLauncher.INSTANCE.getFiles().getLauncherFilePath() + "\\test.zip").toFile()));
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(path.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
            zos.close();
        }
    }

    public Path[] getBackupPaths() {
        return backupPaths;
    }
}
