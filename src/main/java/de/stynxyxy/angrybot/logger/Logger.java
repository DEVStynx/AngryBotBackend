package de.stynxyxy.angrybot.logger;

import de.stynxyxy.angrybot.util.TimeUtil;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    public static final File LogFilePath = new File("src\\main\\resources\\logs\\logs.log");

    public static void log(String log) {
        Logger.WriteToLogFile(LogFilePath,log);
        System.out.println(log);
    }

    public static void warn(String warning) {
        Logger.WriteToLogFile(LogFilePath,"Warning: "+warning);
        System.err.println(warning);
    }
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    private static void WriteToLogFile(File file, String content) {
        try {
            List<String> original = getLogFileContent(file);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            for (String line: original) {
                fileWriter.write(line+"\n");
                System.out.println("Content: "+line);
            }
            fileWriter.write(TimeUtil.formatTime(TimeUtil.getCurrentTime())+": "+content);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<String> getLogFileContent(File logfile) {
        ArrayList<String> content = new ArrayList<>();
        try {
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            BufferedReader fileReader = new BufferedReader(new FileReader(logfile));

            String line;
            while (((line = fileReader.readLine()) != null )) {
                content.add(line+"");

            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;

    }
}
