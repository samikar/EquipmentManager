package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtils {
	final static String PROPERTIES_PATH = "C:\\EquipmentManager\\ConfigFile\\";
	final static String PROPERTIES_FILENAME = "app.properties";
	final static String TESTPROPERTIES_PATH = "C:\\EquipmentManager\\ConfigFile\\";
	final static String TESTPROPERTIES_FILENAME = "app_test.properties";

	public static Properties loadProperties() {
		String appConfigPath = PROPERTIES_PATH + PROPERTIES_FILENAME;
		Properties appProperties = new Properties();
		try {
			appProperties.load(new FileInputStream(appConfigPath));

		} catch (Exception ex) {
			System.out.println("Error loading properties file: " + ex.toString());
			return null;
		}
		return appProperties;
	}
	
	public static Properties loadTestProperties() {
		String appConfigPath = TESTPROPERTIES_PATH + TESTPROPERTIES_FILENAME;
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
