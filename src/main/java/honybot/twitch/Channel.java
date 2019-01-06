package honybot.twitch;

import java.io.IOException;

import honybot.login.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import honybot.config.ConfigManager;
import honybot.login.SQLUserDao;

public class Channel {
	
	private static final String root = "https://api.twitch.tv/kraken/";
	private static final String client_id = ConfigManager.getTwitchClientId();
	private static final String community_id = ConfigManager.getTwitchCommunityId();

	public static JSONObject getChannel(String channel){
		String url = root + "channels/" + channel;
		return getFromUrl(url);
	}
	
	public static JSONObject getStream(String channel){
		try {
			Document doc = Jsoup.connect(root+"streams/"+channel)
					.header("Client-ID", client_id)
					.ignoreHttpErrors(true)
					.ignoreContentType(true)
				    .get();
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(doc.body().text());
			if(obj.containsKey("error")){
				return null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject getFromUrl(String url){
		try {
			Document doc = Jsoup.connect(url)
					.header("Client-ID", client_id)
				    .ignoreContentType(true)
				    .get();
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(doc.body().text());
			if(obj.containsKey("error")){
				return null;
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isLive(String channel){
		JSONObject streamObj = getStream(channel);
		if(streamObj == null){
			return false;
		}
		if(streamObj.containsKey("error")){
			return false;
		}
		if(streamObj.get("stream") != null){
			return true;
		}
		return false;
	}

	public static boolean isLive(JSONObject channelObj){
		if(channelObj == null){
			return false;
		}
		if(channelObj.containsKey("error")){
			return false;
		}
		if(channelObj.get("stream") != null){
			return true;
		}
		return false;
	}
	
	public static long getViewers(String channel){
		JSONObject streamObj = getStream(channel);
		if(streamObj == null){
			return 0;
		}
		if(!isLive(streamObj)){
			return 0;
		}
		long viewers = 0;
		JSONObject stream = (JSONObject) streamObj.get("stream");
		viewers = (long) stream.get("viewers");
		return viewers;
		
	}
	
	public static boolean setTitle(String channel, String title){
		String url = root+"channels/"+channel;
		User user = SQLUserDao.getUserByName(channel);
		if (user == null) {
			return false;
		}
		String auth = user.getTwitch_auth();
		JSONObject channelObj = new JSONObject();
		JSONObject status = new JSONObject();
		status.put("status", title);
		channelObj.put("channel", status);
		if(twitchPut(url, auth, channelObj)){
			return true;
		}
		return false;
	}

	public static boolean setGame(String channel, String game){
		
		return false;
	}
	
	public static boolean setHonybotCommunity(String channel){
		JSONObject channelObj = getChannel(channel);
		if(channel == null){
			return false;
		}
		long channel_id = (long) channelObj.get("_id");
		String url = root+"channels/"+channel_id+"/community" + community_id;
		String auth = SQLUserDao.getUserByName(channel).getTwitch_auth();
		if(twitchPutV5(url, auth)){
			return true;
		}
		return false;
	}
	
	public static boolean twitchPut(String url, String auth, JSONObject content){
		try {
			Response response = Jsoup.connect(url)
					.timeout(20000)
					.header("Content-Type", "application/json")
					.header("Client-ID", client_id)
					.header("Authorization", "OAuth " + auth)
					.ignoreContentType(true)
					.requestBody(content.toJSONString())
					.method(Connection.Method.PUT)
					.execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean twitchPutV5(String url, String auth){
		try {
			Response response = Jsoup.connect(url)
					.timeout(20000)
					.header("Accept", "application/vnd.twitchtv.v5+json")
					.header("Authorization", "OAuth " + auth)
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.method(Connection.Method.PUT)
					.execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
