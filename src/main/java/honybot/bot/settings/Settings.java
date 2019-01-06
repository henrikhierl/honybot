package honybot.bot.settings;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import honybot.bot.IRCBot;
import honybot.bot.TimerHandler;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.twitch.Channel;
import honybot.twitch.ViewerHandler;

public class Settings {
	
	private PointSettings pointSettings;
	private VipSettings vipSettings;
	private BetSettings betSettings;
	private Sc2Settings sc2Settings;
	
	private IRCBot bot;
	private static final int timeCycle = 2;
	
	public Settings(String username, IRCBot bot){
		pointSettings = SQLSettingsDao.getPointSettings(username);
		vipSettings = SQLSettingsDao.getVipSettings(username);
		betSettings = SQLSettingsDao.getBetSettings(username);
		sc2Settings = SQLSettingsDao.getSC2Settings(username);
		this.bot = bot;
		handleTimers(username);
	}
	

	public PointSettings getPointSettings() {
		return pointSettings;
	}

	public VipSettings getVipSettings() {
		return vipSettings;
	}
	
	public BetSettings getBetSettings() {
		return betSettings;
	}
	
	public Sc2Settings getSc2Settings(){
		return sc2Settings;
	}
	
	public void reloadAllSettings(String username){
		reloadPointSettings(username);
		reloadVipSettings(username);
		reloadBetSettings(username);
	}

	public void reloadPointSettings(String username){
		pointSettings = SQLSettingsDao.getPointSettings(username);
		handlePointTimer(username);
	}
	
	public void reloadVipSettings(String username){
		vipSettings = SQLSettingsDao.getVipSettings(username);
	}
	
	public void reloadBetSettings(String username){
		betSettings = SQLSettingsDao.getBetSettings(username);
	}
	
	public void handleTimers(String username){
		handlePointTimer(username);
		handleTimeTimer(username);
	}
	
	public void handlePointTimer(String username){
		if(getPointSettings().isEnabled()){
			//check if interval and points get per interval are greater 0, if not then don't start timer
			int pointsInterval = getPointSettings().getPointsTime();
			if(pointsInterval <= 0){
				return;
			}
			int pointsGet = getPointSettings().getPointsGet();
			if(pointsGet <= 0){
				return;
			}
			//add timer to TimerHandler
			PointTimer pointTimer = new PointTimer(username, bot, "#"+username, pointsGet, getVipSettings().getBonus());
			String timerName = username + "-points";
			System.out.println("adding pointsTimer " + timerName + " to TimerHandler");
			TimerHandler.addFixedRateTimer(timerName, pointTimer, pointsInterval, pointsInterval, TimeUnit.MINUTES);
		}else{
			//remove timer from TimerHandler
			System.out.println("removing pointsTimer fromTimerHandler");
			String timerName = username + "-points";
			TimerHandler.removeAndCancelTimer(timerName);
		}
	}
	
	public void handleTimeTimer(String username){
		if(!TimerHandler.hasTimer(username+"-timer")){
			Timer timer = new Timer(username, bot, "#"+username);
			TimerHandler.addFixedRateTimer(username + "-timer", timer, timeCycle, timeCycle, TimeUnit.MINUTES);
		}
	}
	
	public void onDisconnect(String username){
		TimerHandler.removeAndCancelTimer(username+"-points");
		TimerHandler.removeAndCancelTimer(username+"-time");
	}
	
	public class PointTimer implements Runnable {
		private String name;
		private IRCBot ircBot;
		private String channel;
		private int amount;
		private int bonus;
		
		public PointTimer(String name, IRCBot ircBot, String channel, int amount, int bonus){
			this.name = name;
			this.ircBot = ircBot;
			this.channel = channel;
			this.amount = amount;
			this.bonus = bonus;
		}
		
		@Override
		public void run(){
			//If channel is offline, users won't get points
			if(!Channel.isLive(name)){
				System.out.println("------Channel " + name + " is offline. No points were given------");
				return;
			}
			ArrayList<String> viewers = ViewerHandler.getCurrentViewers(channel);
			if(SQLViewerUserDao.changePointsWithTimer(name, viewers , amount, bonus, true)){
				System.out.println("------Gave Points to "+ viewers.size() +" users------");
			}else{
				System.out.println("------Failed giving Points to "+ viewers.size() +" Users in channel------");
				
			}
		}
	}
	
	public class Timer implements Runnable {
		private IRCBot ircBot;
		private String channel;
		private String name;
		private static final int timeCycle = 2;
		
		public Timer(String name, IRCBot ircBot, String channel){
			this.name = name;
			this.ircBot = ircBot;
			this.channel = channel;
		}
		
		@Override
		public void run(){
			if(!Channel.isLive(name)){
				System.out.println("------Channel " + name + " is offline. No time was given------");
				return;
			}		
			
			ArrayList<String> viewers = ViewerHandler.getCurrentViewers(channel);
			
			if(SQLViewerUserDao.addTime(name, viewers, timeCycle)){
				System.out.println("------Giving time to "+viewers.size()+" viewers in channel------");
			}else{
				System.out.println("------Failed giving time to "+ viewers.size() +" viewers in channel------");
			}
		}
	}
	
}
