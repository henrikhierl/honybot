package honybot.bot.listener;

import java.util.Map;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Command.CounterCommand;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLCounterDao;
import honybot.bot.dao.impl.SQLViewerUserDao;

public class CounterCommandListener implements ChatEventListener{

	
	private Map<String, CounterCommand> commands;
	private MessageHandler messageHandler;
	private IRCBot bot;
	private String name;
	
	
	public CounterCommandListener(String name, MessageHandler messageHandler, IRCBot bot){
		this.name = name;
		this.messageHandler = messageHandler;
		this.bot = bot;
		commands = SQLCounterDao.getCommands(name);
		
		String channel = "#" + name;
	}
	
	@Override
	public void onMessage(ChannelMessageEvent event) {
		String userCommand = event.getMessageByIndex(0);
		userCommand = userCommand.toLowerCase();
		if(commands.containsKey(userCommand)){
			CounterCommand com = commands.get(userCommand);
			String response = com.getOutput();
			if(com.getPermission() <= 0){
				//nothing to do
			}else if(com.getPermission() == 1){
				if(!event.isOp() && !event.getUser().equalsIgnoreCase(name)){		//maybe streamer is is not flagged as op/mod!
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
			int counter_id = com.getCounter_id();
			String type = com.getType();
			if(type.equalsIgnoreCase("increase") || type.equalsIgnoreCase("decrease")){
				int amount = 1;
				if(event.getMessage().length > 1){
					try{
						amount = Integer.parseInt(event.getMessageByIndex(1));
					}catch(Exception e){
						//amount is not an int, increasing by 1 (default)
					}
				}
				if(type.equalsIgnoreCase("decrease")){
					amount *= -1;
				}
				SQLCounterDao.changeCounterValue(counter_id, amount);
				int newValue = SQLCounterDao.getCounterValue(counter_id);
				response = response.replaceAll("\\$value", Integer.toString(newValue));
				messageHandler.sendMessage(false, bot, event.getChannel(), response);
			}else if(type.equalsIgnoreCase("reset")){
				SQLCounterDao.resetCounterValue(counter_id);
				messageHandler.sendMessage(false, bot, event.getChannel(), response);
			} 
		}
		
	}

	public void updateCommands(){
		commands = SQLCounterDao.getCommands(name);
	}
	
	@Override
	public void updateSettings() {
		// TODO Auto-generated method stub
		commands = SQLCounterDao.getCommands(name);
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
	}

}
