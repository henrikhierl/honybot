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

public class PointSettingsController {
	private static String tableName = "settings";

	public static JSONObject handleSettings(String channelname, Request req) {
		try {
			HashMap<String, String> settings = new HashMap<>();

			settings.put("points-check", req.queryParams("points-check"));
			settings.put("points-whisper", req.queryParams("points-whisper"));
			settings.put("points-name", req.queryParams("points-name"));
			String command = req.queryParams("points-command");
			if (command.contains(" ")) {
				return JSONResponseHelper.createError("Command must not contain spaces!");
			}
			if (command.equalsIgnoreCase("")) {
				return JSONResponseHelper.createError("Command must not be empty!");
			}
			settings.put("points-command", command);
			settings.put("points-reply", req.queryParams("points-reply"));
			//initial points
			try {
				int initialPoints = Integer.parseInt(req.queryParams("points-initial"));
				settings.put("points-initial", req.queryParams("points-initial"));
			} catch (Exception e) {
				return JSONResponseHelper.createError("Initial points must be an integer!");
			}
			//time interval
			try {
				int time = Integer.parseInt(req.queryParams("points-time"));
				if(time < 0){
					return JSONResponseHelper.createError("Time interval must not be negative!");
				}
				settings.put("points-time", req.queryParams("points-time"));
			} catch (Exception e) {
				return JSONResponseHelper.createError("Time interval must be an integer!");
			}
			//points per interval
			try {
				int get = Integer.parseInt(req.queryParams("points-get"));
				settings.put("points-get", req.queryParams("points-get"));
			} catch (Exception e) {
				return JSONResponseHelper.createError("Points get must be an integer!");
			}
			if (saveSettings(channelname, settings)) {
				return JSONResponseHelper.createSuccess("Settings saved!");
			}
			return JSONResponseHelper.createError("Error while saving settings!");
		} catch (Exception ex) {
			ex.printStackTrace();
			return JSONResponseHelper.createError("Error saving settings");
		}
	
	}

	public JSONObject validateSettings() {
		return null;
	}

	private static boolean saveSettings(String channelname, Map<String, String> settings) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				
				String sqlUpsert = "INSERT INTO " + tableName + "(username, setting, value) "
						+ "VALUES(?,?,?) "
						+ "ON DUPLICATE KEY UPDATE value = VALUES(value)";

				try (PreparedStatement preparedStatement = conn.prepareStatement(sqlUpsert)) {
					settings.forEach((key, value) -> {
						try {
							if(value != null){
								preparedStatement.setString(1, channelname);
								preparedStatement.setString(2, key);
								preparedStatement.setString(3, value);
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

			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting like 'points-%'";
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
