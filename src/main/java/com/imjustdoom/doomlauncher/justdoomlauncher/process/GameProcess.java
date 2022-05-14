package com.imjustdoom.doomlauncher.justdoomlauncher.process;

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

            if(true) {
                builder.command("cmd.exe", "/c", startupCommand);
            }else {
                builder.command("sh", "-c", startupCommand);
            }

            process = builder.start();

            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            printStream(inputStream);
            printStream(errorStream);

            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            outputStream.flush();
            outputStream.close();

            if(!isFinished) {
                process.destroyForcibly();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        process.children().forEach((p) -> {
            p.destroyForcibly();
            System.out.println(p.info());
        });
        System.out.println(process.info());
        process.destroyForcibly();
    }

    public String getMain() {
        return main;
    }

    public String getStartupCommand() {
        return startupCommand;
    }

    private static void printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
