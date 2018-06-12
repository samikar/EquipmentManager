package fi.danfoss.equipmentmanager.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtils {
	final static String PROPERTIES_FILENAME = "emanager.properties";
	final static Logger logger = Logger.getLogger(PropertyUtils.class);
	final static ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/**
	 * Read properties from file 
	 * @param serial			Serial number to search
	 * @return					Properties
	 */
	public static Properties loadProperties() {
		Properties appProperties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(PROPERTIES_FILENAME)){
			//appProperties.load(new FileInputStream(appConfigPath));
			appProperties.load(resourceStream);

		} catch (Exception e) {
			logger.error("Error loading properties file: " + e.toString());
			return null;
		}
		return appProperties;
	}
}
