package honybot.bot;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.jibble.pircbot.PircBot;

import honybot.config.ConfigManager;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.listener.BetCommandListener;
import honybot.bot.listener.ChatEventListener;
import honybot.bot.listener.CommandListener;
import honybot.bot.listener.CounterCommandListener;
import honybot.bot.listener.PointCommandListener;
import honybot.bot.settings.Settings;
import honybot.bot.stats.ChatStats;

public class IRCBot extends PircBot {
	
	private List<ChatEventListener> listeners;
	private Settings settings;
	private String username;
	private MessageHandler mHandler;
	
	public IRCBot(String username, MessageHandler mHandler){
		this.username = username;
		settings = new Settings(username, this);
		this.setName(ConfigManager.getTwitchOauth());
		listeners = new LinkedList<>();
		this.mHandler = mHandler;
	}
	
	public void onUnknown(String line) {
		if (line.startsWith(":tmi.twitch.tv CAP * ACK")) {
		} else {
			String _channelPrefixes = "#&+!";

			int colon = line.indexOf(":"); // position of first "doppelpunkt"
			String twitchTags = line.substring(0, colon);
			line = line.substring(colon);
			String[] tagArray = twitchTags.split(";");
			boolean isOP = false;

			if (line.startsWith(":tmi.twitch.tv USERSTATE")) {
				return;
			}

			if (tagArray[0].startsWith("@")) {
				tagArray[1] = tagArray[1].substring(7);
				String[] badgesArray = tagArray[1].split(",");

				for (int i = 0; i < badgesArray.length; i++) {
					if (badgesArray[i].equalsIgnoreCase("broadcaster/1")
							|| badgesArray[i].equalsIgnoreCase("moderator/1")) {
						isOP = true;
					}
				}
			}
			String sourceNick = "";
			String sourceLogin = "";
			String sourceHostname = "";

			StringTokenizer tokenizer = new StringTokenizer(line);
			String senderInfo = tokenizer.nextToken();
			String command = tokenizer.nextToken();
			String target = null;

			int exclamation = senderInfo.indexOf("!");
			int at = senderInfo.indexOf("@");
			if (senderInfo.startsWith(":")) {
				if (exclamation > 0 && at > 0 && exclamation < at) {
					sourceNick = senderInfo.substring(1, exclamation);
					sourceLogin = senderInfo.substring(exclamation + 1, at);
					sourceHostname = senderInfo.substring(at + 1);
				} else {
					return;

				}
			}

			command = command.toUpperCase();
			if (sourceNick.startsWith(":")) {
				sourceNick = sourceNick.substring(1);
			}
			if (target == null) {
				target = tokenizer.nextToken();
			}
			if (target.startsWith(":")) {
				target = target.substring(1);
			}

			// Check for CTCP requests.
			if (command.equals("PRIVMSG") && _channelPrefixes.indexOf(target.charAt(0)) >= 0) {
				handleMessage(target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2),
						isOP);
			} else if (command.equals("PRIVMSG")) {
				this.onPrivateMessage(sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
			} else {
				// If we reach this point, then we've found something that the
				// PircBot
				// Doesn't currently deal with.
				// this.handleLine(line);
			}
		}
	}
	
	public void handleMessage(String channel, String sender, String login, String hostname, String message, boolean op){
		ChatStats.addLineReceived(username);
		if(!SQLViewerUserDao.hasViewer(username, sender)){
			int initialPoints = settings.getPointSettings().getInitialPoints();
			SQLViewerUserDao.addNewViewer(username, sender, initialPoints);
		}
		String[] splitMessage = message.split(" ");
		ChannelMessageEvent event = new ChannelMessageEvent(this, channel, sender, splitMessage, op);
		//send event to all Listeners
		listeners.forEach((listener)->{
			listener.onMessage(event);
		});
	}
	
	public String getUsername(){
		return username;
	}
	
	public Settings getSettings(){
		return settings;
	}
	
	public void sendWhisper(String username, String message){
		this.sendRawLine("PRIVMSG #jtv :/w " + username + " " + message);
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {

	}
	
	public void onJoin(String channel, String sender, String login, String hostname) {
		if(!SQLViewerUserDao.hasViewer(username, sender)){
			int initialPoints = settings.getPointSettings().getInitialPoints();
			SQLViewerUserDao.addNewViewer(username, sender, initialPoints);
		}
	}
	
	public boolean addListener(ChatEventListener listener){
		return listeners.add(listener);
	}
	
	public MessageHandler getMessageHandler(){
		return mHandler;
	}
	
	public ChatEventListener getBetListener(){
		ListIterator<ChatEventListener> iterator = listeners.listIterator();
		while(iterator.hasNext()){
			ChatEventListener currentListener = iterator.next();
			if(currentListener.getClass() == BetCommandListener.class){
				return currentListener;
			}
		}
		return null;
	}

	public ChatEventListener getPointsListener(){
		ListIterator<ChatEventListener> iterator = listeners.listIterator();
		while(iterator.hasNext()){
			ChatEventListener currentListener = iterator.next();
			if(currentListener.getClass() == PointCommandListener.class){
				return currentListener;
			}
		}
		return null;
	}
	
	public ChatEventListener getCommandListener(){
		ListIterator<ChatEventListener> iterator = listeners.listIterator();
		while(iterator.hasNext()){
			ChatEventListener currentListener = iterator.next();
			if(currentListener.getClass() == CommandListener.class){
				return currentListener;
			}
		}
		return null;
	}

	public ChatEventListener getCounterCommandListener(){
		ListIterator<ChatEventListener> iterator = listeners.listIterator();
		while(iterator.hasNext()){
			ChatEventListener currentListener = iterator.next();
			if(currentListener.getClass() == CounterCommandListener.class){
				return currentListener;
			}
		}
		return null;
	}
}
