package honybot.bot.settings.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import honybot.post.JSONResponseHelper;
import honybot.sql.SQLHelper;
import spark.Request;

public class BetSettingsController {
	private static String tableName = "settings";	
	
	public static JSONObject handleSettings(String channelname, Request req){
		HashMap<String, String> settings = new HashMap<>();
		
		settings.put("betting-check", req.queryParams("betting-check"));
		settings.put("betting-mmr-quotas-check", req.queryParams("betting-mmr-quotas-check"));
		try{
			float win = Float.parseFloat(req.queryParams("betting-win"));
			if(win <= 0){
				return JSONResponseHelper.createError("Win Multiplier must be positive!");
			}
			settings.put("betting-win", req.queryParams("betting-win"));
		}catch(Exception e){
			return JSONResponseHelper.createError("Win Multiplier must be a floating point number of format: X.Y!");
		}
		try{
			float lose = Float.parseFloat(req.queryParams("betting-lose"));;
			if(lose <= 0){
				return JSONResponseHelper.createError("Lose Multiplier must be positive!");
			}
			settings.put("betting-lose", req.queryParams("betting-lose"));
		}catch(Exception e){
			return JSONResponseHelper.createError("Lose Mulitplier must be a floating point number of format: X.Y!");
		}
		try{
			int time = Integer.parseInt(req.queryParams("betting-time"));
			if(time <= 0){
				return JSONResponseHelper.createError("Time to bet must be positive!");
			}
			settings.put("betting-time", req.queryParams("betting-time"));
		}catch(Exception e){
			return JSONResponseHelper.createError("Time to bet must be an Integer!");
		}
		try{
			int notify = Integer.parseInt(req.queryParams("betting-notification"));
			if(notify < 0){
				return JSONResponseHelper.createError("Notification time must not be negative!");
			}
			settings.put("betting-notification", req.queryParams("betting-notification"));
		}catch(Exception e){
			return JSONResponseHelper.createError("Time to bet must be an Integer!");
		}
		if(saveSettings(channelname, settings)){
			return JSONResponseHelper.createSuccess("Settings saved!");
		}
		return JSONResponseHelper.createError("Error while saving settings!");
	}
	
	public JSONObject validateSettings(){
		return null;
	}
	
	private static boolean saveSettings(String channelname, Map<String, String> settings) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				
				String sql = "INSERT INTO " + tableName + "(username, setting, value) VALUES(?,?,?) "
						+ "ON DUPLICATE KEY "
						+ "UPDATE value = ? ";
				
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					settings.forEach( (key, value) -> {
		        		try {
		        			if(value != null){
		        				preparedStatement.setString(1, channelname);
			        			preparedStatement.setString(2, key);
			        			preparedStatement.setString(3, value);
		        				preparedStatement.setString(4, value);
			        			preparedStatement.addBatch();
		        			}
		        			
						} catch (SQLException e) {
							e.printStackTrace();
						}
		        	});
					preparedStatement.executeBatch();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static JSONObject getSettings(String channelname){
		JSONObject obj = new JSONObject();
		
		try {
			String tableName = "settings";
			Class.forName("com.mysql.jdbc.Driver");
			
			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting like 'betting-%'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					String value = result.getString("value");
					if (value.equalsIgnoreCase("on")) {
						obj.put(result.getString("setting"), true);
					} else if (value.equalsIgnoreCase("off")) {
						obj.put(result.getString("setting"), false);
					} else {
						obj.put(result.getString("setting"), value);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return obj;
	}
	
}
