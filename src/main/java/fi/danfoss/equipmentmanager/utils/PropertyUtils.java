package fi.danfoss.equipmentmanager.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import fi.danfoss.equipmentmanager.db.ADHandler;

public class PropertyUtils {
	final static String PROPERTIES_PATH = "C:\\EquipmentManager\\ConfigFile\\";
	final static String PROPERTIES_FILENAME = "app.properties";
	final static Logger logger = Logger.getLogger(PropertyUtils.class);

	public static Properties loadProperties() {
		String appConfigPath = PROPERTIES_PATH + PROPERTIES_FILENAME;
		Properties appProperties = new Properties();
		try {
			appProperties.load(new FileInputStream(appConfigPath));

		} catch (Exception e) {
			logger.error("Error loading properties file: " + e.toString());
			return null;
		}
		return appProperties;
	}
}
