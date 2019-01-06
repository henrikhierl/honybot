package honybot.bot.listener;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.VipSettings;
import honybot.helpers.VipHelper;

public class VipCommandListener implements ChatEventListener {

	private MessageHandler messageHandler;
	private IRCBot bot;
	private String name;
	
	public VipCommandListener(String name, MessageHandler messageHandler, IRCBot bot){
		this.name = name;
		this.messageHandler = messageHandler;
		this.bot = bot;
		System.out.println(bot.getSettings().getVipSettings().toString());
	}
	
	@Override
	public void onMessage(ChannelMessageEvent event) {
		if(!bot.getSettings().getPointSettings().isEnabled() || !bot.getSettings().getVipSettings().isEnabled()){
			return;
		}
		//!getVip
		if( event.getMessageByIndex(0).equalsIgnoreCase(bot.getSettings().getVipSettings().getVipCommand()) ){
			ViewerUser viewer = SQLViewerUserDao.getViewerByName(name, event.getUser());
			if(viewer != null){
				int points = viewer.getPoints();
				int currentLevel = viewer.getVip();
				//checks if user exceeds maximum amount of vip levels
				if(!bot.getSettings().getVipSettings().doesAutoExtend()){
					int maxVipLevel = bot.getSettings().getVipSettings().getLevels().length;
					if(currentLevel >= maxVipLevel){
						messageHandler.sendMessage(true, bot, event.getUser(), "You already reached the maximum VIP-Level");
						return;
					}
				}
				int vipCost = getVipCost(currentLevel+1);
				if(points >= vipCost){
					SQLViewerUserDao.changePoints(name, event.getUser(), vipCost * (-1));
					if(SQLViewerUserDao.setVIP(name, event.getUser(), currentLevel+1)){
						onVipChange(event.getChannel(), event.getUser(), currentLevel+1);
					}
				}else{
					messageHandler.sendMessage(true, bot, event.getUser(), "You dont have enough " + bot.getSettings().getPointSettings().getPointsName() + " (" 
							+ points + "/" + vipCost + ")");
				}
			}
		//!vipCost
		}else if( event.getMessageByIndex(0).equalsIgnoreCase(bot.getSettings().getVipSettings().getCostCommand()) ){
			ViewerUser viewer = SQLViewerUserDao.getViewerByName(name, event.getUser());
			if(viewer != null){
				//TODO check if user is max VIP level. give better feedback what the next level will be and how many points he has
				int cost = getVipCost(viewer.getVip()+1);
				messageHandler.sendMessage(true, bot, event.getUser(), "Next level will cost " + cost + " " + bot.getSettings().getPointSettings().getPointsName());
			}
		//!vips
		}else if( event.getMessageByIndex(0).equalsIgnoreCase("!vips") ){
			String link = "All VIPs can be found here: honybot.com/streamers/" + name + "/vip";
			messageHandler.sendMessage(false, bot, event.getChannel(), link);
		//!setVIP - mod/admin only
		} else if ( event.getMessageByIndex(0).equalsIgnoreCase("!setVip") && (event.getMessage().length == 3) && event.isOp() ){
			try{
				String receiver = ListenerHelper.getOptionFromMessage(event.getMessage());
				int level = ListenerHelper.getAmountFromMessage(event.getMessage());
				if(level >= 0){
					if(SQLViewerUserDao.setVIP(name, receiver, level)){
						onVipChange(event.getChannel(), receiver, level);
					}
				}
			}catch(NumberFormatException e){
				
			}
			
		} else if ( event.getMessageByIndex(0).equalsIgnoreCase("!giveVip") && (event.getMessage().length == 2) && event.isOp() ){
			try{
				String receiver = event.getMessageByIndex(1);
				ViewerUser viewer = SQLViewerUserDao.getViewerByName(name, receiver);
				if(viewer != null){
					int level = viewer.getVip()+1;
					if(level >= 0){
						if(SQLViewerUserDao.setVIP(name, receiver, level)){
							onVipChange(event.getChannel(), receiver, level);
						}
					}
				}
			}catch(NumberFormatException e){
				
			}
			
		}
		
	}
	
	private void onVipChange(String channelname, String username, int level) {
		if(level < 0){
			//Error, no levels < 0 allowed
		}
		else if(level == 0){
			messageHandler.sendMessage(false, bot, channelname, username + " is not a VIP anymore");
		}else{
			VipSettings settings = bot.getSettings().getVipSettings();
			String vipName = VipHelper.getVipLevelName(settings, level);
			messageHandler.sendMessage(false, bot, channelname, username + " is now " + vipName);
		}
	}

	public int getVipCost(int level){
		return (int) bot.getSettings().getVipSettings().getCostFunction().setVariable("x", level).evaluate();
	}

	@Override
	public void updateSettings() {
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
	}

}
