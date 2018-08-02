package fi.danfoss.equipmentmanager.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import fi.danfoss.equipmentmanager.model.EMProperties;

public class EMPropertyUtils {
	final static String PROPERTIES_FILENAME = "emanager.properties";
	final static Logger logger = Logger.getLogger(EMPropertyUtils.class);
	final static ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/**
	 * Read properties from file 
	 * @return					Properties
	 */
	public static Properties loadProperties() {
		Properties appProperties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(PROPERTIES_FILENAME)){
			appProperties.load(resourceStream);

		} catch (Exception e) {
			logger.error("Error loading properties file: " + e.toString());
			return null;
		}
		return appProperties;
	}
	
	/**
	 * Return properties as an EMProperties Object 
	 * @return					EMProperties
	 */
	public static EMProperties getEMProperties() {
		Properties properties = EMPropertyUtils.loadProperties();
		EMProperties EMprops = new EMProperties();
		
		EMprops.setDBurl(properties.getProperty("DBurl"));
		EMprops.setDBuser(properties.getProperty("DBuser"));
		EMprops.setDBpassword(properties.getProperty("DBpassword"));
		EMprops.setDBdriver(properties.getProperty("DBdriver"));
		EMprops.setADuser(properties.getProperty("ADuser"));
		EMprops.setADpassword(properties.getProperty("ADpassword"));
		EMprops.setADurl(properties.getProperty("ADurl"));
		EMprops.setWORKDAY(properties.getProperty("WORKDAY"));
		EMprops.setSTARTHOUR(properties.getProperty("STARTHOUR"));
		EMprops.setSTARTMINUTE(properties.getProperty("STARTMINUTE"));
		EMprops.setENDHOUR(properties.getProperty("ENDHOUR"));
		EMprops.setENDMINUTE(properties.getProperty("ENDMINUTE"));
		EMprops.setTempFilePath(properties.getProperty("TempFilePath"));
		
		return EMprops;
	}
	
	/**
	 * Save properties to file
	 */
	public static void setEMProperties(EMProperties newProperties) {
		Parameters params = new Parameters();
		// Read config data from file
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
																		.configure(params.fileBased()
																		.setFileName(PROPERTIES_FILENAME));
		try
		{
		    Configuration config = builder.getConfiguration();
			config.setProperty("DBurl", newProperties.getDBurl());
			config.setProperty("DBuser", newProperties.getDBuser());
			config.setProperty("DBpassword", newProperties.getDBpassword());
			config.setProperty("DBdriver", newProperties.getDBdriver());
			config.setProperty("ADuser", newProperties.getADuser());
			config.setProperty("ADpassword", newProperties.getADpassword());
			config.setProperty("ADurl", newProperties.getADurl());
			config.setProperty("WORKDAY", newProperties.getWORKDAY());
			config.setProperty("STARTHOUR", newProperties.getSTARTHOUR());
			config.setProperty("STARTMINUTE", newProperties.getSTARTMINUTE());
			config.setProperty("ENDHOUR", newProperties.getENDHOUR());
			config.setProperty("ENDMINUTE", newProperties.getSTARTMINUTE());
			config.setProperty("TempFilePath", newProperties.getTempFilePath());
		    // Save config
			builder.save();
		}
		catch(ConfigurationException cex)
		{
		    logger.error("Loading configuration file failed: " + cex.getMessage());
		}
	}
}