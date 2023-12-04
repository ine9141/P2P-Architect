package core.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogHandler {
    private static File file = new File("log.txt");
    private static FileWriter fileWriter;

    static {
        try {
            fileWriter = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedWriter bw = new BufferedWriter(fileWriter);


    public static void makeLog(String message) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        bw.write("[" + now + "] : " + message + "\n");
    }
}
