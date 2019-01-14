package honybot.config;

import java.util.Properties;

import com.mysql.jdbc.StringUtils;

public class ConfigManager {

	// General
	private static String adminPassword = "qvfPgFfDQeAjLM9jhbvN5gcjDanrAsvQMBwU8BqNGPWhtVtAXNdSUf2pgdEHwZp8";
	
	// Webserver
	private static int port = 4567;
	
	// Twitch
        private static String twitchChatBotName = "";
	private static String twitchOauth = "";
	private static String twitchClientId = "";
	private static String twitchClientSecret = "";
	private static String twitchCommunityId = "";
        private static String twitchOauthToken = "";
	private static String twitchLoginRedirectURI = "";

	// twitch constant config
	private static final String twitchScopes = "user_read+channel_read+channel_editor";
	private static final String twitchVerify = "true";
	private static final String twitchGrantType = "authorization_code";
	
	// must be generated after config has loaded
	private static String twitchLoginURL = "";
	
	// Database
	private static String dbHost = "";
	private static String dbPort = "3306";
	private static String dbName = "";
	private static String dbUser = "";
	private static String dbPassword = "";
	
	static {
		Properties configProps = ConfigLoader.loadConfigProps();
		ConfigManager.loadAdminPassword(configProps);
		ConfigManager.loadPort(configProps);
		
                ConfigManager.loadTwitchChatBotName(configProps);
		ConfigManager.loadTwitchOauth(configProps);
		ConfigManager.loadTwitchClientId(configProps);
		ConfigManager.loadTwitchClientSecret(configProps);
		ConfigManager.loadTwitchCommunityId(configProps);
		ConfigManager.loadTwitchOauthToken(configProps);
		ConfigManager.loadTwitchLoginRedirectURI(configProps);
		
		ConfigManager.generateTwitchLoginURL();
		
		ConfigManager.loadDbHost(configProps);
		ConfigManager.loadDbPort(configProps);
		ConfigManager.loadDbName(configProps);
		ConfigManager.loadDbUser(configProps);
		ConfigManager.loadDbPassword(configProps);
	}

	public static String getAdminPassword() {
		return adminPassword;
	}

	public static int getPort() {
		return port;
	}

        public static String getTwitchChatBotName() { 
                return twitchChatBotName;
        }
        
	public static String getTwitchOauth() {
		return twitchOauth;
	}

	public static String getTwitchClientId() {
		return twitchClientId;
	}

	public static String getTwitchClientSecret() {
		return twitchClientSecret;
	}

	public static String getTwitchCommunityId() {
		return twitchCommunityId;
	}

	public static String getTwitchgranttype() {
		return twitchGrantType;
	}

	public static String getTwitchOauthToken() {
		return twitchOauthToken;
	}

	public static String getTwitchLoginRedirectURI() {
		return twitchLoginRedirectURI;
	}

	public static String getTwitchLoginURL() {
		return twitchLoginURL;
	}

	public static String getDbHost() {
		return dbHost;
	}

	public static String getDbPort() {
		return dbPort;
	}

	public static String getDbName() {
		return dbName;
	}

	public static String getDbUser() {
		return dbUser;
	}

	public static String getDbPassword() {
		return dbPassword;
	}
	
	private static void loadAdminPassword(Properties configProps) {
		String adminPassword = configProps.getProperty("adminPassword");
		if (StringUtils.isEmptyOrWhitespaceOnly(adminPassword)) {
			System.out.println("WARNING: Admin password is empty. Using default password: " + ConfigManager.adminPassword);
		} else {
			ConfigManager.adminPassword = adminPassword;
		}
	}
	
	private static void loadPort (Properties configProps) {
		Integer port = ConfigLoader.loadInteger(configProps, "port");
		if (port == null) {
			System.out.println("WARNING: Port is empty. Using default Port: " + ConfigManager.port);
		} else {
			if (port > 65535 || port < 0) {
				System.out.println("WARNING: Port must be a number between 0 and 65535. Using default port:" + ConfigManager.port);
			} else {
				ConfigManager.port = port;
			}
		}
	}
        
        private static void loadTwitchChatBotName(Properties configProps) {
		twitchChatBotName = configProps.getProperty("twitchChatBotName", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchChatBotName)) {
			System.out.println("WARNING: twitchOauth is empty.");
		}
	}
	
	private static void loadTwitchOauth(Properties configProps) {
		twitchOauth = configProps.getProperty("twitchOauth", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchOauth)) {
			System.out.println("WARNING: twitchOauth is empty.");
		}
	}
	
	private static void loadTwitchClientId(Properties configProps) {
		twitchClientId = configProps.getProperty("twitchClientId", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchClientId)) {
			System.out.println("WARNING: twitchClientId is empty.");
		}
	}
	
	private static void loadTwitchClientSecret(Properties configProps) {
		twitchClientSecret = configProps.getProperty("twitchClientSecret", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchClientSecret)) {
			System.out.println("WARNING: twitchClientSecret is empty.");
		}
	}
	
	private static void loadTwitchCommunityId(Properties configProps) {
		twitchCommunityId = configProps.getProperty("twitchCommunityId", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchCommunityId)) {
			System.out.println("WARNING: twitchCommunityId is empty.");
		}
	}
	
	private static void loadTwitchOauthToken(Properties configProps) {
		twitchOauthToken = configProps.getProperty("twitchOauthToken", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchOauthToken)) {
			System.out.println("WARNING: twitchOauthToken is empty.");
		}
	}
	
	private static void loadTwitchLoginRedirectURI(Properties configProps) {
		twitchLoginRedirectURI = configProps.getProperty("twitchLoginRedirectURI", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(twitchLoginRedirectURI)) {
			System.out.println("WARNING: twitchLoginRedirectURI is empty.");
		}
	}
	
	private static void generateTwitchLoginURL () {
		twitchLoginURL = "https://api.twitch.tv/kraken/oauth2/authorize"
			    + "?response_type=code"
			    + "&client_id=" + twitchClientId
			    + "&redirect_uri=" + twitchLoginRedirectURI
			    + "&scope=" + twitchScopes
			    + "&force_verify=" + twitchVerify;
	}
	
	private static void loadDbHost(Properties configProps) {
		dbHost = configProps.getProperty("dbHost", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(dbHost)) {
			System.out.println("WARNING: dbHost is empty.");
		}
	}
	
	private static void loadDbPort (Properties configProps) {
		Integer port = ConfigLoader.loadInteger(configProps, "dbPort");
		if (port == null) {
			System.out.println("WARNING: dbPort is empty. Using default Port: " + ConfigManager.dbPort);
		} else {
			if (port > 65535 || port < 0) {
				System.out.println("WARNING: dbPort must be a number between 0 and 65535. Using default port:" + ConfigManager.dbPort);
			} else {
				ConfigManager.dbPort = port.toString();
			}
		}
	}
	
	private static void loadDbName(Properties configProps) {
		dbName = configProps.getProperty("dbName", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(dbName)) {
			System.out.println("WARNING: dbName is empty.");
		}
	}
	
	private static void loadDbUser(Properties configProps) {
		dbUser = configProps.getProperty("dbUser", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(dbUser)) {
			System.out.println("WARNING: dbUser is empty.");
		}
	}
	
	private static void loadDbPassword(Properties configProps) {
		dbPassword = configProps.getProperty("dbPassword", "");
		if (StringUtils.isEmptyOrWhitespaceOnly(dbPassword)) {
			System.out.println("WARNING: dbPassword is empty.");
		}
	}
}
