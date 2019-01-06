package honybot.sql;

import honybot.config.ConfigManager;

public class SQLHelper {

	private static String dbHost = "";
	private static String dbPort = "";
	private static String dbName = "";
	private static String dbUser = "";
	private static String dbPassword = "";
	private static String connectionString = "";
	
	static {
		dbHost = ConfigManager.getDbHost();
		dbPort = ConfigManager.getDbPort();
		dbName = ConfigManager.getDbName();
		dbUser = ConfigManager.getDbUser();
		dbPassword = ConfigManager.getDbPassword();
		
		connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + 
				"?" + "user=" + dbUser + "&" + "password=" + dbPassword + "&useSSL=false";
	}
	
	public static String getConnectionString(){
		return connectionString;
	}
}
