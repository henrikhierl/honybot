package honybot.bot.listener;

import java.util.ArrayList;
import java.util.Random;

import org.jibble.pircbot.User;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.twitch.ViewerHandler;

public class PointCommandListener implements ChatEventListener{
	
	private MessageHandler messageHandler;
	public IRCBot bot;
	private String name;
	
	
	public PointCommandListener(String name, MessageHandler messageHandler, IRCBot bot){
		this.name = name;
		this.messageHandler = messageHandler;
		this.bot = bot;
		updateSettings();
	}
	
	public void onMessage(ChannelMessageEvent event){
		// !points
		if(!bot.getSettings().getPointSettings().isEnabled()){
			return;
		}
		String command = event.getMessageByIndex(0);
		if(command.equalsIgnoreCase(bot.getSettings().getPointSettings().getCommand())){
			// TODO: maybe show points again instead of linking to website?
			messageHandler.sendMessage(false, bot, event.getChannel(), "Check your stats on https://www.honybot.com/streamers/" + name + "?q=" + event.getUser());

		}
		// !giveall
		else if( command.equalsIgnoreCase("!giveall") && (event.getMessage().length == 2) && event.isOp() ){
			giveAll(event);
		}
		// !modgive
		else if( (command.equalsIgnoreCase("!modgive") || command.equalsIgnoreCase("!admingive")) && (event.isOp()) && (event.getMessage().length == 3) ){
			modGiveUser(event);
		}
		// !give
		else if( command.equalsIgnoreCase("!give")  && (event.getMessage().length == 3)){
			try{
				String receiver = ListenerHelper.getOptionFromMessage(event.getMessage()).toLowerCase();
				ViewerUser sender = SQLViewerUserDao.getViewerByName(name, event.getUser());
				int amount = ListenerHelper.getAmountFromMessage(event.getMessage(), sender);
				if(amount > 0){
					if((sender != null) && !sender.getName().equalsIgnoreCase(receiver) && SQLViewerUserDao.hasViewer(name, receiver)){
						if(sender.getPoints() >= amount){
							SQLViewerUserDao.changePoints(name, sender.getName(), amount * (-1));
							messageHandler.sendMessage(true, event.getBot(), sender.getName(), sender.getName() + " successfully sent " + amount + " " + bot.getSettings().getPointSettings().getPointsName() + " to " + receiver);
							SQLViewerUserDao.changePoints(name, receiver, amount);
						}
					}
				}
			} catch (NumberFormatException numEx){
				//
			}
		} 
		
		else if( command.equalsIgnoreCase("!throw") && (event.getMessage().length == 2)){
			ViewerUser sender = SQLViewerUserDao.getViewerByName(name, event.getUser());
			if(sender != null){
				try{
					int minAmount = 100;
					int amount;
					if (event.getMessageByIndex(1).equalsIgnoreCase("all")) {
						amount = sender.getPoints();
					} else {
						amount = Integer.valueOf(event.getMessageByIndex(1));
					}
					if((amount >= minAmount) && (sender.getPoints() >= amount) && (amount >= 0)){
						User[] users = event.getBot().getUsers(event.getChannel());
						ArrayList<String> viewerList = new ArrayList<>();
						for (User user : users) {
							viewerList.add(user.getNick());
						}
						
						ArrayList<ViewerUser> catchList = new ArrayList<ViewerUser>();
						for(String currentNick : viewerList) {
							if( !currentNick.equalsIgnoreCase(sender.getName()) && !currentNick.equalsIgnoreCase("honybot") && !currentNick.equalsIgnoreCase("nightbot") ){
								//userList.add(new MyUser(currentNick, 0, false));
								catchList.add(new ViewerUser(currentNick, 0, 0, 0, 0, 0, 0, 0, 0));
							}
						}
						int giveAmount = 0;
						int newAmount = 0;
						int userCount = catchList.size();
						if(userCount > 0){
							SQLViewerUserDao.changePoints(name, sender.getName(), amount * (-1));
							Random random = new Random();
							int index = 0;
							while(amount >= 20){
								giveAmount = random.nextInt(10) + 10; //everyone gets at least 10 points except the last one
								index = random.nextInt(userCount);
								newAmount = catchList.get(index).getPoints() + giveAmount;
								catchList.get(index).setPoints(newAmount);
								amount = amount - giveAmount;
							}
							if(amount > 0){
								//give rest to 1 user
								index = random.nextInt(userCount);
								newAmount = catchList.get(index).getPoints() + amount;
								catchList.get(index).setPoints(newAmount);
							}
							String nickName;
							String winningUsers = "";
							int catchCount = 0;
							for(int i = 0; i < catchList.size(); i++){
								giveAmount = catchList.get(i).getPoints();
								if(giveAmount > 0){
									catchCount++;
									nickName = catchList.get(i).getName();
									SQLViewerUserDao.changePoints(name, nickName, giveAmount);
									winningUsers += nickName + " (" + giveAmount + ") ";
									if(i < catchList.size()-1){
											winningUsers += ", ";								
									}
								}
							}
							messageHandler.sendMessage(false, bot, event.getChannel(), catchCount + " viewers caught " + bot.getSettings().getPointSettings().getPointsName() + " thrown by " + sender.getName() + ". " + winningUsers);
						}else{
							messageHandler.sendMessage(false, bot, event.getChannel(), "Not enough viewers in channel");
						}
						
					}
					
				}catch(NumberFormatException numEx){
					//numEx.printStackTrace();
				}
			}
		}
		
		//TODO giveToVIP - gives points to all online vip viewers
	}

	public void updateSettings(){		

	}
	
	public void onDisconnect(){
	}
	
	public boolean giveAll(ChannelMessageEvent event){
		try{
			int amount = Integer.valueOf(event.getMessageByIndex(1));
			ArrayList<String> viewers = ViewerHandler.getCurrentViewers(event.getChannel());
			if(viewers.size()> 0){
				if(SQLViewerUserDao.changePoints(name, viewers, amount)){
					messageHandler.sendMessage(false, event.getBot(), event.getChannel(), amount + " " + bot.getSettings().getPointSettings().getPointsName() + " for everyone!");
				}
			}
			return true;
		}catch(NumberFormatException numEx){
			//numEx.printStackTrace();
		}
		return false;
	}
	
	public boolean modGiveUser(ChannelMessageEvent event){
		try{
			int amount = Integer.valueOf(event.getMessageByIndex(2));
			String receiver = event.getMessageByIndex(1).toLowerCase();
			if(SQLViewerUserDao.changePoints(name, receiver, amount)){
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), receiver + " just got " + amount + " " + bot.getSettings().getPointSettings().getPointsName());
				return true;
			}
		}catch(NumberFormatException numEx){
			//numEx.printStackTrace();
		}
		return false;
	}

	
}
