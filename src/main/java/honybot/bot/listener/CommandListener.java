package honybot.bot.listener;


import java.util.Map;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Command.Command;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLCustomCommandsDao;
import honybot.bot.dao.impl.SQLViewerUserDao;

public class CommandListener implements ChatEventListener{
	
	private Map<String, Command> commands;
	private MessageHandler messageHandler;
	private IRCBot bot;
	private String name;
	
	
	public CommandListener(String name, MessageHandler messageHandler, IRCBot bot){
		this.name = name;
		this.messageHandler = messageHandler;
		this.bot = bot;
		commands = SQLCustomCommandsDao.getCommands(name);
		
		String channel = "#" + name;
	}
	

	@Override
	public void onMessage(ChannelMessageEvent event) {
		if(event.getMessageByIndex(0).equalsIgnoreCase("!addcommand") && (event.getMessage().length >= 3) && (event.isOp())){
			String command = event.getMessageByIndex(1);
			String response = "";
			for(int i = 2; i < event.getMessage().length; i++){
				//rebuilds message from user, but leaves out command -> i = 1
				response = response + event.getMessageByIndex(i) + " ";
			}
			if(SQLCustomCommandsDao.addCommand(name, command, response, 0, false, false, 0, 0)){
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), "Command \""+ command +"\"successfully added.");
				updateSettings();
			} else{
				messageHandler.sendMessage(true, event.getBot(), event.getUser(), "Sorry, there was an error while adding the command");
			}
		} else if(event.getMessageByIndex(0).equalsIgnoreCase("!addwhispercommand") && (event.getMessage().length >= 3) && (event.isOp())){
			String command = event.getMessageByIndex(1);
			String response = "";
			for(int i = 2; i < event.getMessage().length; i++){
				//rebuilds message from user, but leaves out command -> i = 1
				response = response + event.getMessageByIndex(i) + " ";
			}
			if(SQLCustomCommandsDao.addCommand(name, command, response, 0, true, false, 0, 0)){
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), "Command \""+ command +"\"successfully added.");
				updateSettings();
			} else{
				messageHandler.sendMessage(true, event.getBot(), event.getUser(), "Sorry, there was an error while adding the command");
			}
		} else if(event.getMessageByIndex(0).equalsIgnoreCase("!removecommand") && (event.getMessage().length == 2) && (event.isOp())){
			String command = event.getMessageByIndex(1);
			if(SQLCustomCommandsDao.removeCommand(name, command)){
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), "Command \""+ command +"\"successfully removed.");
				updateSettings();
			} else{
				messageHandler.sendMessage(true, event.getBot(), event.getUser(), "Sorry, there was an error while removing the command");
			}
		}
		//if custom command
		else if(commands.containsKey(event.getMessageByIndex(0))){
			Command com = commands.get(event.getMessageByIndex(0));
			String response = com.getText();
			if(com.getPermission() <= 0){
				//nothing to do
			}else if(com.getPermission() == 1){
				if(!event.isOp() || !event.getUser().equalsIgnoreCase(name)){		//maybe streamer is is not flagged as op/mod!
					//user is not mod or admin
					return;
				}
			}else if(com.getPermission() >= 2){
				if(!event.getUser().equalsIgnoreCase(name)){
					return;
				}
			}
			if((com.getCost() > 0) && bot.getSettings().getPointSettings().isEnabled()){
				ViewerUser user = SQLViewerUserDao.getViewerByName(name, event.getUser());
				//check for cost
				if(user == null){
					return;
				}
				if(user.getPoints() >= com.getCost()){
					//take points
					if(!SQLViewerUserDao.changePoints(name, event.getUser(), (com.getCost() * (-1)) ) ){
						messageHandler.sendMessage(true, event.getBot(), event.getUser(), "Something went wrong while trying to deduct points.");
						return;
					}
				}else{
					//return if not enough points
					return;
				}
			}
			if((com.getVip() > 0) && bot.getSettings().getVipSettings().isEnabled()){
				ViewerUser user = SQLViewerUserDao.getViewerByName(name, event.getUser());
				//check for cost
				if(user == null){
					return;
				}
				if(user.getVip() < com.getVip()){
					return;
				}
			}
			if(com.takesUserInput()){
				String message = "";
				for(int i = 1; i < event.getMessage().length; i++){
					//rebuilds message from user, but leaves out command -> i = 1
					message = message + event.getMessageByIndex(i) + " ";
				}
				response = response.replaceAll("\\$input", message);
				
			}
			response = response.replaceAll("\\$user", event.getUser());
			response = response.replaceAll("\\$me", name);
			
			if(com.isWhisper()){
				messageHandler.sendMessage(true, event.getBot(), event.getUser(), response);
			}else{
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), response);
			}
		}
	}
	
	public void updateCommands() {
		commands = SQLCustomCommandsDao.getCommands(name);
	}

	@Override
	public void updateSettings() {
		updateCommands();
	}
	
	@Override
	public void onDisconnect(){
	}

	
}

