package honybot.bot.listener;

import honybot.bot.Event.ChannelMessageEvent;

public interface ChatEventListener {
	
	public void onMessage(ChannelMessageEvent event);	
	public void updateSettings();
	public void onDisconnect();
}
