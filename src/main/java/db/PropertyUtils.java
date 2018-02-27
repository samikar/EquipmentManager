package db;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtils {
	final static String CONFIG_PATH = "C:\\EquipmentManager\\ConfigFile\\";
	final static String CONFIG_FILENAME = "app.properties";

	public static Properties loadProperties() {
		String appConfigPath = CONFIG_PATH + CONFIG_FILENAME;
		Properties appProperties = new Properties();
		try {
			appProperties.load(new FileInputStream(appConfigPath));

		} catch (Exception ex) {
			System.out.println("Error loading properties file: " + ex.toString());
			return null;
		}
		return appProperties;
	}
}
