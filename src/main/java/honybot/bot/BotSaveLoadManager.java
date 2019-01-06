package honybot.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BotSaveLoadManager {

    private static String path = "botsToRestart.txt";

    public static void saveChannelsToFile (List<String> channelNames) {
        System.out.println("Saving channels with running bot to file: " + path);
        try {
            Files.write(Paths.get(path), String.join("\r\n", channelNames).getBytes());
            System.out.println("Success writing file.");
        } catch (IOException e) {
            System.err.println("Could not save channels to file:");
            e.printStackTrace();
        }
    }

    public static List<String> loadChannelsFromFile () {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Exception when trying to load active channels from file");
        }
        return new ArrayList<>();
    }

}
