package honybot.bot;

import java.util.*;

public class StopRunningBotsThread implements Runnable {

    private String stopMessage = "Honybot is going down for maintenance. It will automatically reconnect when maintenance is done, which should only take a few minutes.";

    @Override
    public void run() {
        HashMap<String, IRCBot> botHashMap = BotHandler.getBots();
        botHashMap.values().stream().forEach(bot -> {
            bot.sendMessage("#" + bot.getUsername(), stopMessage);
        });
        List<String> channelNames = new ArrayList<>(botHashMap.keySet());
        // save the channel names of running bots to a file so they can be restarted later
        BotSaveLoadManager.saveChannelsToFile(channelNames);
        // disconnects and destroys the bots. Also takes care of canceling bets so that no points will be lost
        channelNames.stream().forEach(BotHandler::destroyAndStopBot);
    }
}
