package honybot.bot.settings.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.post.JSONResponseHelper;
import honybot.sc2.SQLPlayerDao;
import honybot.sc2.Sc2Player;
import honybot.sql.SQLHelper;
import spark.Request;

public class SC2SettingsController {
	private static String tableName = "settings";

	
	public static JSONObject handleSettings(String channelname, Request req) {
		HashMap<String, String> settings = new HashMap<>();

		settings.put("sc2-check", req.queryParams("sc2-check"));
		settings.put("sc2-region", req.queryParams("sc2-region"));
		settings.put("sc2-id", req.queryParams("sc2-id"));
		String race = req.queryParams("sc2-race");
		if (!race.equalsIgnoreCase("terran") && !race.equalsIgnoreCase("zerg") && !race.equalsIgnoreCase("protoss")
				&& !race.equalsIgnoreCase("random")) {
			return JSONResponseHelper.createError("Invalid Race");
		}
		settings.put("sc2-race", race);
		settings.put("sc2-name", req.queryParams("sc2-name"));
		settings.put("sc2-title-check", req.queryParams("sc2-title-check"));
		settings.put("sc2-title-ingame", req.queryParams("sc2-title-ingame"));
		settings.put("sc2-title-menu", req.queryParams("sc2-title-menu"));
		settings.put("sc2-show-games", req.queryParams("sc2-show-games"));


		if (saveSettings(channelname, settings)) {
			return JSONResponseHelper.createSuccess("Settings saved!");
		}
		return JSONResponseHelper.createError("Error while saving settings!");
	}
	

	public static JSONObject testSettings(String channelname, Request req) {
		
		Sc2Player player = SQLPlayerDao.getPlayer(req.queryParams("sc2-id"), req.queryParams("sc2-name"), req.queryParams("sc2-race"), req.queryParams("sc2-region"));
		
		if(player != null){
			return JSONResponseHelper.createSuccess("User found in database. Your current MMR: " + player.getMmr());
		}
		
		return JSONResponseHelper.createError("User could not be found in database. Your settings might still be valid if you haven't played a game this season "
				+ "and are thus not included in the database.");
	}
	
	public static boolean testPlayerProfile(String channelname){
		Sc2Settings settings = SQLSettingsDao.getSC2Settings(channelname);
		if(settings == null){
			return false;
		}
		Sc2Player player = SQLPlayerDao.getPlayer(settings.getId(), settings.getName(), settings.getRace(), settings.getRegion());
		if(player == null){
			return false;
		}
		
		return true;
	}

	public JSONObject validateSettings() {
		return null;
	}

	private static boolean saveSettings(String channelname, Map<String, String> settings) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				String sql = "UPDATE " + tableName + " SET value = ? WHERE username = ? AND setting = ?";

				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					settings.forEach((key, value) -> {
						try {
							if(value != null){
								preparedStatement.setString(1, value);
								preparedStatement.setString(2, channelname);
								preparedStatement.setString(3, key);
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

	public static JSONObject getSettings(String channelname) {
		JSONObject obj = new JSONObject();

		try {
			String tableName = "settings";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				try (Statement query = conn.createStatement()) {
					String sql = "SELECT * FROM " + tableName + " WHERE username = '" + channelname
							+ "' AND setting like 'sc2-%'";
					ResultSet result = query.executeQuery(sql);
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

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return obj;
	}

	public static String getUsernameByAuth(String auth) {
		try {
			String tableName = "settings";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT username FROM " + tableName + " WHERE setting = 'sc2-token' AND value = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, auth);
				ResultSet result = preparedStatement.executeQuery();
				while (result.next()) {
					return result.getString("username");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "";
	}

}
