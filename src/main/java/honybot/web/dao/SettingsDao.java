package honybot.web.dao;

import java.util.Map;

public interface SettingsDao {
	
	boolean isConnected();
	
	boolean connect();
	
	boolean addSettings(String username, Map<String, String[]>settings);
	
	public boolean initSettings(String username);
	
	boolean updateSettings(String username, Map<String, String[]>settings);
	
	boolean removeSettings();
	
	Map<String, String[]> expandSettings(Map<String, String[]> settings);
	
}
