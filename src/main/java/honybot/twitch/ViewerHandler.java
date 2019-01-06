package honybot.twitch;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import honybot.config.ConfigManager;

public class ViewerHandler {
	
	private static final String client_id = ConfigManager.getTwitchClientId();
	
	public static JSONObject getChannel(String channel){
		try {
			Document doc = Jsoup.connect("https://api.twitch.tv/kraken/channels/"+channel)
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
	
	public static ArrayList<String> getCurrentViewers(String channel){
		if(channel.startsWith("#")){
			channel = channel.substring(1);
		}
		String docText = "";
		ArrayList<String> viewers = new ArrayList<>();

		for(int tries = 0; tries < 20; tries++){
			try {
				Document doc = getDoc(channel);
				docText = doc.body().text();
				if(!docText.equals("") && !docText.equals(" ")){
					if(tries > 0){
						System.out.println("retry "+tries+" to get viewers");
					}
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(docText);
					if(obj.containsKey("error")){
						return null;
					}
					long viewer_count = (long) obj.get("chatter_count");
					obj = (JSONObject) obj.get("chatters");
					System.out.println("got " + viewer_count + " viewers over tmi.twitch.tv/group/user/"+channel+"/chatters");
					viewers.addAll((JSONArray) obj.get("moderators"));
					//viewers.addAll((JSONArray) obj.get("staff"));
					//viewers.addAll((JSONArray) obj.get("admin"));
					//viewers.addAll((JSONArray) obj.get("global_mods"));
					viewers.addAll((JSONArray) obj.get("viewers"));
					return viewers;
				}	
			} catch (Exception e) {
				System.out.println("Error Parsing docText: " + docText);
			}
		}
		return viewers;
	}
	
	public static Document getDoc(String channel) throws IOException{
		return Jsoup.connect("https://tmi.twitch.tv/group/user/"+channel+"/chatters")
				.header("Client-ID", client_id)
				.ignoreHttpErrors(true)
				.ignoreContentType(true)
				.timeout(10000)
				.get();
	}
	

	
}
