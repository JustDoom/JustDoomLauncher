package com.imjustdoom.doomlauncher.justdoomlauncher.process;

import com.imjustdoom.doomlauncher.justdoomlauncher.application.ErrorApplication;
import javafx.application.Platform;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class GameProcess {

    private String main;
    private String startupCommand;
    private String directory;

    private Process process;

    public GameProcess(String main, String startupCommand, String directory) {
        this.main = main;
        this.startupCommand = startupCommand.replaceAll("%file%", main);
        this.directory = directory;
    }

    public void run() {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(directory));

            System.out.println(directory);
            System.out.println(startupCommand);

            if (true) {
                builder.command("cmd.exe", "/c", startupCommand);
            } else {
                builder.command("sh", "-c", startupCommand);
            }

            process = builder.start();

            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            //printStream(inputStream);
            printStream(errorStream);

            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();

            if (!isFinished) {
                process.destroyForcibly();
            }
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage());
        }
    }

    public void kill() {
        process.children().forEach(ProcessHandle::destroyForcibly);
        System.out.println(process.info());
        process.destroyForcibly();
    }

    public String getMain() {
        return main;
    }

    public String getStartupCommand() {
        return startupCommand;
    }

    public void error(String error) {
        Platform.runLater(() -> {
            ErrorApplication errorApplication = new ErrorApplication();
            try {
                errorApplication.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            errorApplication.setError(error);
        });

        kill();
    }

    private void printStream(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line).append("\n");
            }
            if(stringBuilder.toString().equals("")) return;
            error(stringBuilder.toString());
        }
    }
}
