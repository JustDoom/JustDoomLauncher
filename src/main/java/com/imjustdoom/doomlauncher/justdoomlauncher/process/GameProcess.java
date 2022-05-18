package com.imjustdoom.doomlauncher.justdoomlauncher.process;

import com.imjustdoom.doomlauncher.justdoomlauncher.JustDoomLauncher;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.ConsoleApplication;
import com.imjustdoom.doomlauncher.justdoomlauncher.application.ErrorApplication;
import javafx.application.Platform;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class GameProcess {

    private String main;
    private String startupCommand;
    private String directory;
    private boolean useConsole;

    private Process process;

    public GameProcess(String main, String startupCommand, String directory, boolean useConsole) {
        this.main = main;
        this.startupCommand = startupCommand.replaceAll("%file%", main);
        this.directory = directory;
        this.useConsole = useConsole;
    }

    public void run() {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(directory));

            System.out.println(directory);
            System.out.println(startupCommand);

            if (true) {
                builder.command("cmd.exe", "/c", startupCommand);
                //builder.command(startupCommand.split(" "));
            } else {
                builder.command("sh", "-c", startupCommand);
            }

            process = builder.start();

            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            OutputStreamWriter stdinWriter = new OutputStreamWriter(stdin);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
            BufferedReader error = new BufferedReader(new InputStreamReader(stderr));

            DataOutputStream dataOutputStream = new DataOutputStream(stdin);

            ConsoleApplication consoleApplication = new ConsoleApplication();
            if(JustDoomLauncher.INSTANCE.getFiles().getLauncherFile().getSetting("openConsole").getAsBoolean()) {
                new Thread(() -> Platform.runLater(() -> {
                    try {
                        consoleApplication.start(dataOutputStream);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })).start();

                new Thread(() -> {
                    try {
                        console(stdout, consoleApplication);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                try {
                    console(stderr, consoleApplication);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //printStream(stderr, consoleApplication);

                boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
                stdin.flush();
                stdin.close();

                if (!isFinished) {
                    process.destroyForcibly();
                }
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

        System.out.println("error: " + error);
        //kill();
    }

    private void printStream(InputStream inputStream, ConsoleApplication application) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line).append("\n");
                application.addText(line + "\n");
            }
            if(stringBuilder.toString().equals("")) return;
            error(stringBuilder.toString());
        }
    }

    private void console(InputStream inputStream, ConsoleApplication application) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line.replace(">", ""));
                application.addText(line.replace("> ", "").replace(">", "") + "\n");
            }
        }
    }

    public Process getProcess() {
        return process;
    }
}
