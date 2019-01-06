package honybot.bot;

import java.util.HashMap;
import java.util.List;

import honybot.bot.listener.BetCommandListener;
import honybot.bot.listener.CommandListener;
import honybot.bot.listener.CounterCommandListener;
import honybot.bot.listener.PointCommandListener;
import honybot.bot.listener.RewardListener;
import honybot.bot.listener.StarcraftCommandListener;
import honybot.bot.listener.VipCommandListener;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.config.ConfigManager;
import honybot.twitch.Channel;


public class BotHandler {
	private static HashMap<String, IRCBot> bots = new HashMap<>();
	private static String botOauth = ConfigManager.getTwitchOauth();

	public static void restartBots() {
		createAndStartBots(BotSaveLoadManager.loadChannelsFromFile());
	}

	public static synchronized void createAndStartBots(List<String> channelNames) {
		channelNames.stream().forEach(channelName -> createAndStartBot(channelName, channelName));
	}
	
	public static synchronized boolean createAndStartBot(String name, String channel){
		if(!channel.startsWith("#")){
			channel = "#" + channel;
		}
		
		if (bots.containsKey(name)) {
			System.out.println("bot already connected");
			return false;
		}
		try {
			//create bot
			MessageHandler messageHandler = new MessageHandler(name);
			IRCBot bot = new IRCBot(name, messageHandler);
			//TODO-ts: only for testing, set false before deploying
			bot.setVerbose(true);
			bot.connect("irc.chat.twitch.tv", 6667, botOauth);
			bot.joinChannel(channel);
			bot.setMessageDelay(100);
			bot.sendRawLine("CAP REQ :twitch.tv/membership");
			bot.sendRawLine("CAP REQ :twitch.tv/commands");
			bot.sendRawLine("CAP REQ :twitch.tv/tags");
			bot.addListener(new PointCommandListener(name, messageHandler, bot));
			bot.addListener(new CommandListener(name, messageHandler, bot));
			bot.addListener(new VipCommandListener(name, messageHandler, bot));
			bot.addListener(new BetCommandListener(name, messageHandler, bot));
			bot.addListener(new StarcraftCommandListener(name, messageHandler));
			bot.addListener(new CounterCommandListener(name, messageHandler, bot));
			bot.addListener(new RewardListener(name, messageHandler, bot));
			bots.put(name, bot);
			autoJoinCommunity(name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean destroyAndStopBot(String name){
		if (!bots.containsKey(name)) {
			return false;
		}
		IRCBot bot = bots.remove(name);
		BetCommandListener betCommandListener = (BetCommandListener) bot.getBetListener();
		if (betCommandListener != null) {
			if (betCommandListener.cancelBet(name)) {
				System.out.println("Cancelled bet for " + name);
			}
		}
		TimerHandler.removeTimersByUsername(name);
		bot.disconnect();
		bot.dispose();
		System.out.println("stopped bot for " + name);
		return true;
	}
	
	public static boolean hasBot(String name){
		return bots.containsKey(name);
	}
	
	public static IRCBot getBot(String name){
		if(bots.containsKey(name)){
			return bots.get(name);
		}
		return null;
	}
	
	public static HashMap<String, IRCBot> getBots(){		
		return BotHandler.bots;
	}
	

	private static void autoJoinCommunity(String name) {
		String value = SQLSettingsDao.getSetting(name, "auto-community");
		if(value.equalsIgnoreCase("on")){
			Channel.setHonybotCommunity(name);
		}
	}
}
