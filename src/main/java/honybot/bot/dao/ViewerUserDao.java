package honybot.bot.dao;

import honybot.bot.settings.PointSettings;

public interface ViewerUserDao {
	
	public boolean isConnected();
	
	public boolean connect();
	
	public static PointSettings getSettings(String name){
		return null;
	};
	
	public static int getPoints(String username){
		return 0;
	};
	
	public static int getTime(String username){
		return 0;
	};
	
}
