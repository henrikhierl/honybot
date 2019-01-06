package honybot.content;

import org.json.simple.JSONObject;

import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.stats.SQLStatsDao;
import honybot.config.ConfigManager;

public class FrontendContentHandler {

	public static String twitch_login_url = ConfigManager.getTwitchLoginURL();
	
	@SuppressWarnings("unchecked")
	public static JSONObject getIndexContent(){
		JSONObject content = new JSONObject();
		//get chat lines received
		content.put("lines_received", SQLStatsDao.getStat("linesreceived"));
		//get commands used
		int commands_used = SQLStatsDao.getStat("linessent");
		commands_used += SQLStatsDao.getStat("whisperssent");
		content.put("lines_sent", commands_used);
		//get viewers seen
		content.put("viewers_seen", SQLViewerUserDao.getViewerAmount());
		content.put("twitch_login", twitch_login_url);
		
		return content;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getTwitchLoginUrl(){
		JSONObject content = new JSONObject();
		content.put("twitch_login", twitch_login_url);		
		return content;
	}
}
