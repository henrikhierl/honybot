package honybot.controller;

import java.io.Console;
import java.util.HashMap;

import honybot.bot.BotHandler;
import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;

public class CommandLineController implements Runnable{
	
	@Override
	public void run() {
		
		while(true){
			Console console = System.console();
			if(console == null){
				return;
			}
			String input = console.readLine();
			if(input == null){
				continue;
			}
			if(input.startsWith("send all")){
				String message = input.substring(9);
				sendMessageToAll(message);
			}else if(input.startsWith("maintenance")){
				String minutes = input.substring(12);
				maintenanceMessage(minutes);
			}
		}
		
	}
	
	protected void sendMessageToAll(String message){
		HashMap<String, IRCBot> bots = BotHandler.getBots();
		bots.forEach((channelname, bot) -> {
			String channel = "";
			if(!channelname.startsWith("#")){
				channel = "#"+channelname;
			}else{
				channel = channelname;
			}
			MessageHandler mHandler = bot.getMessageHandler();
			mHandler.sendMessage(false, bot, channel, message);
		});
		System.out.println("sent message to " + bots.size() + " channels");
	}
	
	protected void maintenanceMessage(String minutes){
		String message = "ATTENTION: Honybot is going offline for maintenance in " + minutes + " minutes!";
		sendMessageToAll(message);
	}

}
