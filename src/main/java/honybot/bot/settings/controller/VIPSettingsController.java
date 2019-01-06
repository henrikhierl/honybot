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

import honybot.post.JSONResponseHelper;
import honybot.sql.SQLHelper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import spark.Request;

public class VIPSettingsController {
	private static String tableName = "settings";

	public static JSONObject handleSettings(String channelname, Request req){
		HashMap<String, String> settings = new HashMap<>();
		
		settings.put("vip-check", req.queryParams("vip-check"));
		settings.put("vip-check-extension", req.queryParams("vip-check-extension"));
		settings.put("vip-stacking", req.queryParams("vip-stacking"));
		String cost_function = req.queryParams("vip-cost");
		try{
			Expression exp = new ExpressionBuilder(cost_function)
					.variable("x")
					.build()
					.setVariable("x", 1);
			ValidationResult result = exp.validate();
			if(!result.isValid()){
				System.out.println(result.getErrors().toString());
				return JSONResponseHelper.createError("Cost Function not valid!");
			}
		}catch(Exception e){
			return JSONResponseHelper.createError("Error in Cost Function!");
		}
		settings.put("vip-cost", req.queryParams("vip-cost"));		
		settings.put("vip-suffix", req.queryParams("vip-suffix"));
		settings.put("vip-levels", req.queryParams("vip-levels"));
		
		String command = req.queryParams("vip-command");
		if(command.contains(" ")){
			return JSONResponseHelper.createError("VIP command must not contain spaces!");
		}
		if(command.equalsIgnoreCase("")){
			return JSONResponseHelper.createError("VIP command must not be empty!");
		}
		settings.put("vip-command", command);

		String cost_command = req.queryParams("vip-cost-command");
		if(cost_command.contains(" ")){
			return JSONResponseHelper.createError("VIP-cost command must not contain spaces!");
		}
		if(cost_command.equalsIgnoreCase("")){
			return JSONResponseHelper.createError("VIP-cost command must not be empty!");
		}
		settings.put("vip-cost-command", cost_command);
		
		try{
			int time = Integer.parseInt(req.queryParams("vip-bonus"));
			settings.put("vip-bonus", req.queryParams("vip-bonus"));
		}catch(Exception e){
			return JSONResponseHelper.createError("VIP bonus must be an Integer!");
		}

		if(saveSettings(channelname, settings)){
			return JSONResponseHelper.createSuccess("Settings saved!");
		}
		return JSONResponseHelper.createError("Error while saving settings!");
	}
	
	private static boolean saveSettings(String channelname, Map<String, String> settings) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				
				String sql = "UPDATE " + tableName + " SET value = ? WHERE username = ? AND setting = ?";
				
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					settings.forEach( (key, value) -> {
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
	
	public static JSONObject getSettings(String channelname){
		JSONObject obj = new JSONObject();
		
		try {
			String tableName = "settings";
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				
			      try (Statement query = conn.createStatement()){
			    	  String sql = "SELECT * FROM " + tableName + " WHERE username = '" + channelname + "' AND setting like 'vip-%'";
			    	  ResultSet result = query.executeQuery(sql);
			    	  while (result.next()) {
			    		  String value = result.getString("value");
			    		  if(value.equalsIgnoreCase("on")){
			    			  obj.put(result.getString("setting"), true);
			    		  }else if(value.equalsIgnoreCase("off")){
			    			  obj.put(result.getString("setting"), false);
			    		  }else{
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
}
