package honybot.bot.Event;

import java.util.Arrays;

import honybot.bot.IRCBot;

public class ChannelMessageEvent {



	private IRCBot bot;
	private String channel;
	private String user;
	private String[] message;
	private boolean op;
	
	public ChannelMessageEvent( IRCBot bot, String channel, String user, String[] message, boolean op) {
		super();
		this.bot = bot;
		this.channel = channel;
		this.user = user;
		this.message = message;
		this.op = op;
	}

	public IRCBot getBot(){
		return bot;
	}
	
	public String getChannel() {
		return channel;
	}

	public String getUser() {
		return user;
	}

	public String[] getMessage() {
		return message;
	}
	
	public String getMessageByIndex(int i){
		if( i >= 0){
			if(message.length >= i){
				return message[i];
			}
		}
		return "";
	}
	
	public boolean isOp(){
		return op;
	}
	
	@Override
	public String toString() {
		return "ChannelMessageEvent [bot=" + bot + ", channel=" + channel + ", user=" + user + ", message="
				+ Arrays.toString(message) + ", op=" + op + "]";
	}
}
