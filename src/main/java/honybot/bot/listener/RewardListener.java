package honybot.bot.listener;

import java.util.Map;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.rewards.RedeemedReward;
import honybot.rewards.Reward;
import honybot.rewards.RewardsDao;

public class RewardListener implements ChatEventListener{
	
	private Map<String, Reward> rewards;
	private MessageHandler messageHandler;
	private IRCBot bot;
	private String name;
	
	public RewardListener(String channelName, MessageHandler messageHandler, IRCBot bot){
		this.name = channelName;
		this.messageHandler = messageHandler;
		this.bot = bot;
		rewards = RewardsDao.getRewardsByChannelAsMap(channelName);
	}

	@Override
	public void onMessage(ChannelMessageEvent event) {
		if(!bot.getSettings().getPointSettings().isEnabled()) {
			return;
		}
		String command = event.getMessageByIndex(0);
		
		//check if command is reward
		if(!rewards.containsKey(command)){
			return;
		}
		Reward reward = rewards.get(command);
		ViewerUser sender = SQLViewerUserDao.getViewerByName(name, event.getUser());
		if(sender == null){
			return;
		}
		//check permissions
		if(reward.getPermission() <= 0){
			//nothing to do
		}else if(reward.getPermission() == 1){
			if(!event.isOp() || !event.getUser().equalsIgnoreCase(name)){		//maybe streamer is not flagged as op/mod?
				//user is not mod or admin
				return;
			}
		}else if(reward.getPermission() >= 2){
			if(!event.getUser().equalsIgnoreCase(name)){
				return;
			}
		}
		//check if user has required VIP level
		if( (sender.getVip() < reward.getVip()) && bot.getSettings().getVipSettings().isEnabled() ){
			messageHandler.sendMessage(false, bot, event.getChannel(), "Your VIP-Level is not high enough (" +sender.getVip()+"/"+reward.getVip()+")");
			return;
		}
		//check if user has enough points
		if(sender.getPoints() < reward.getCost()){
			// send message if user does not have enough points
			messageHandler.sendMessage(false, bot, event.getChannel(), "You dont have enough points (" +sender.getPoints()+"/"+reward.getCost()+")");
			return;
		}
		//take points from user
		if(!SQLViewerUserDao.changePoints(name, event.getUser(), (reward.getCost() * (-1)) ) ){
			messageHandler.sendMessage(false, event.getBot(), event.getChannel(), "Something went wrong while trying to deduct points.");
			return;
		}
		// build viewer message
		String message = "";
		for(int i = 1; i < event.getMessage().length; i++){
			//rebuilds message from user, but leaves out command -> i = 1
			message = message + event.getMessageByIndex(i) + " ";
		}
		message = message.trim();
		// add redeem to database
		RedeemedReward redeem = new RedeemedReward(-1, reward.getId(), sender.getName(), message, null);
		RewardsDao.addRedeemedReward(redeem);
		
		String response = reward.getResponse();
		response = response.replaceAll("\\$input", message);
		response = response.replaceAll("\\$user", event.getUser());
		response = response.replaceAll("\\$me", name);
		
		messageHandler.sendMessage(true, event.getBot(), event.getUser(), response);
	}

	@Override
	public void updateSettings() {
		rewards = RewardsDao.getRewardsByChannelAsMap(name);
		
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

}
