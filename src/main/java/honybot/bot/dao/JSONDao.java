package honybot.bot.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONDao {
	
	public JSONObject getObjectFromURL(String url){
		
		BufferedReader reader = null;
		try (InputStream inStream = new URL(url).openStream()){	
			
			reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));
			String jsonText = readAll(reader);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject)parser.parse(jsonText);
			return json;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private String readAll(Reader rd) throws IOException {		//Helper for getGameStatus and getUIStatus
		StringBuilder sBuilder = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sBuilder.append((char) cp);
		}
		return sBuilder.toString();
	}
	
}
