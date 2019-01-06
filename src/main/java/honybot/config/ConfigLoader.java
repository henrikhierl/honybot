package honybot.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

	public static Properties loadConfigProps() {
		String configPath = "config.properties";
		Properties configProps = new Properties();
		try {
			configProps.load(new FileInputStream(configPath));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Config file not found. You need to create a config.properties file.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR: Failed to load config file.");
			System.exit(1);
		}
		return configProps;
	}
	
	public static Integer loadInteger(Properties properties, String propertyName) {
		String valueString = properties.getProperty(propertyName);
		try {
			return Integer.parseInt(valueString);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
}
