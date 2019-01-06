package honybot.web.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import honybot.bot.dao.impl.SQLCounterDao;
import honybot.sql.SQLHelper;
import honybot.web.dao.SettingsDao;

public class SettingsDaoSQL implements SettingsDao {
	private static String tableName = "settings";
	public Connection conn = null;
	
	@Override
	public boolean connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(SQLHelper.getConnectionString());
            System.out.println("Successfully connected to DB");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Not Found");
            return false;
        } catch (SQLException e) {
        	System.out.println("Connecting Not Possible");
        	e.printStackTrace();
        	return false;
        } catch (Exception e){
        	e.printStackTrace();
        	return false;
        }
	}
	
	@Override
	public boolean isConnected(){
		return conn != null;
	}
	
	public Connection getInstance()
	{
	  if(conn == null){
		  connect();
	  }
	  return conn;
	}

	@Override
	public boolean addSettings(String username, Map<String, String[]> settings) {
		// TODO Auto-generated method stub
		
		return false;
	}
	
	@Override
	@Deprecated
	public boolean initSettings(String username) {
		conn = getInstance();
		if(conn != null){
			try {
				
				String sql = "INSERT INTO " + tableName + "(username, setting, value) " +
	                     "VALUES(?, ?, ?)";
			    PreparedStatement preparedStatement = conn.prepareStatement(sql);

			    Map<String, String[]> params = getInitSettings(username);
	        	params.forEach( (key, value) -> { 
	        		try {
						preparedStatement.setString(1, username);
						preparedStatement.setString(2, key);
						preparedStatement.setString(3, value[0]);
						preparedStatement.addBatch();
					} catch (SQLException e) {
						//TODO maybe add a boolean that it failed?
						e.printStackTrace();
					}
	        	});
			    preparedStatement.executeBatch();
			    preparedStatement.close();
			    return true;
			    
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    	return false;
		    }
		  }else{
			  System.out.println("Database connection is null");
		  }
		return false;
	}
	
	public static boolean createDefaultSettings(String username){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				
				String sql = "INSERT INTO "+tableName+" (username, setting, value) " +
	                     "VALUES(?, ?, ?)";
				
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					Map<String, String[]> params = getInitSettings(username);
		        	params.forEach( (key, value) -> { 
						try {
							preparedStatement.setString(1, username);
							preparedStatement.setString(2, key);
							preparedStatement.setString(3, value[0]);
							preparedStatement.addBatch();
						} catch (SQLException e) {
							e.printStackTrace();
						}
		        	});
				    preparedStatement.executeBatch();
				    preparedStatement.close();
				    return true;
				}catch (SQLException e) {
					e.printStackTrace();
				}
			    
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;	
	}

	@Override
	public boolean updateSettings(String username, Map<String, String[]>settings) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				
				String sql = "UPDATE " + tableName + " SET value = ? WHERE username = ? AND setting = ?";
				
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					
					settings = validateSettings(settings);
					settings.forEach( (key, value) -> { 
	        			try {
		        			preparedStatement.setString(1, value[0]);
		        			preparedStatement.setString(2, username);
		        			preparedStatement.setString(3, key);
							preparedStatement.addBatch();
						} catch (SQLException e) {
							e.printStackTrace();
						}
		        	});
					preparedStatement.executeBatch();
				    return true;
				}catch (SQLException e) {
					e.printStackTrace();
				}
			    
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean removeSettings() {
		return false;
	}

	public static Map<String, String[]> getInitSettings(String username){
		
		//maybe put in its own database and "copy"?
		Map<String, String[]> map = new HashMap<>();

		map.put("auto-community", new String[]{"on"});
		
		map.put("twitch-name", new String[]{username});
		map.put("nick-name", new String[]{username});

		map.put("points-check", new String[]{"true"});
		map.put("points-name", new String[]{"Points"});
		map.put("points-command", new String[]{"!points"});
		map.put("points-whisper", new String[]{"on"});
		map.put("points-reply", new String[]{"$user: $points $pointname"});
		map.put("points-time", new String[]{"5"});
		map.put("points-get", new String[]{"5"});
		map.put("points-initial", new String[]{"25"});
		
		map.put("vip-check", new String[]{"on"});
		map.put("vip-command", new String[]{"!getvip"});
		map.put("vip-cost-command", new String[]{"!vipcost"});
		map.put("vip-cost", new String[]{"100x^2"});
		map.put("vip-bonus", new String[]{"3"});
		map.put("vip-stacking", new String[]{"on"});
		map.put("vip-levels", new String[]{"Bronze,Silver,Gold"});
		map.put("vip-suffix", new String[]{"-VIP"});
		map.put("vip-check-extension", new String[]{"on"});
		
		map.put("betting-check", new String[]{"on"});
		map.put("betting-win", new String[]{"1.5"});
		map.put("betting-lose", new String[]{"1.5"});
		map.put("betting-time", new String[]{"180"});
		map.put("betting-notification", new String[]{"30"});

		map.put("sc2-check", new String[]{"off"});
		map.put("sc2-region", new String[]{""});
		map.put("sc2-id", new String[]{""});
		map.put("sc2-race", new String[]{""});
		map.put("sc2-name", new String[]{""});
		map.put("sc2-token", new String[]{SQLCounterDao.buildRandomString(50)});
		map.put("sc2-title-check", new String[]{"on"});
		map.put("sc2-title-ingame", new String[]{"$mmr $PR v$or vs $opp"});
		map.put("sc2-title-menu", new String[]{"$mmr $L $tier $PR searching new game"});
		map.put("sc2-show-games", new String[]{"on"});
		//...
		return map;
	}

	public Map<String, String[]> validateSettings(Map<String, String[]> settings){
		//TODO add validation for numbers, and url?
		
		List<String> keysToRemove = new ArrayList<>();
		
		settings.forEach((key, value) -> {
			if(value[0].isEmpty()){
				keysToRemove.add(key);
			}
		});
		
		keysToRemove.forEach(settings::remove);
		
		return settings;
	}
	
	@Override
	public Map<String, String[]> expandSettings(Map<String, String[]> settings) {
		
		String[] checkboxes = {"points-check", "points-whisper", "vip-check", "vip-stacking", "vip-check-extension", "betting-check", "sc2-check"};

		for (String checkbox : checkboxes) {
			if (!settings.containsKey(checkbox)) {
				settings.put(checkbox, new String[]{"off"});
			}
		}
		return settings;
	}
	
}
