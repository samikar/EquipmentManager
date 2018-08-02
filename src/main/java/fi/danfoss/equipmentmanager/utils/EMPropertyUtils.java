package fi.danfoss.equipmentmanager.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import fi.danfoss.equipmentmanager.model.EMProperties;

public class EMPropertyUtils {
	final static String PROPERTIES_FILENAME = "c:\\temp\\emanager.properties";
	final static Logger logger = Logger.getLogger(EMPropertyUtils.class);
	final static ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/**
	 * Read properties from file 
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
	
	public static void setEMProperties(EMProperties newProperties) {
		Parameters params = new Parameters();
		// Read data from this file
		logger.debug("**************** Riiding fileee");
		File propertiesFile = new File(PROPERTIES_FILENAME);

		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
																		.configure(params.fileBased().setFile(propertiesFile));
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
		    // config contains all properties read from the file
			logger.debug("**************** Seiving filee");
			builder.save();
		}
		catch(ConfigurationException cex)
		{
		    // loading of the configuration file failed
		}
		

		
		
		/*
		File file = new File(PROPERTIES_FILENAME);
		
		PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
		
        
        try {
			layout.load(new InputStreamReader(new FileInputStream(file)));
   	
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

			layout.save(new FileWriter("c:\\temp\\" + PROPERTIES_FILENAME, false));
		} catch (ConfigurationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		*/
		
		/*
		try {
			PropertiesConfiguration conf = new PropertiesConfiguration("propFile.properties");
			props.setProperty("key", "value");
			conf.save();    
			
			
			Properties props = new Properties();
			props.setProperty("DBurl", newProperties.getDBurl());
			props.setProperty("DBuser", newProperties.getDBuser());
			props.setProperty("DBpassword", newProperties.getDBpassword());
			props.setProperty("DBdriver", newProperties.getDBdriver());
			props.setProperty("ADuser", newProperties.getADuser());
			props.setProperty("ADpassword", newProperties.getADpassword());
			props.setProperty("ADurl", newProperties.getADurl());
			props.setProperty("WORKDAY", newProperties.getWORKDAY());
			props.setProperty("STARTHOUR", newProperties.getSTARTHOUR());
			props.setProperty("STARTMINUTE", newProperties.getSTARTMINUTE());
			props.setProperty("ENDHOUR", newProperties.getENDHOUR());
			props.setProperty("ENDMINUTE", newProperties.getSTARTMINUTE());
			props.setProperty("TempFilePath", newProperties.getTempFilePath());
			logger.debug("******************* Kiriootetaha!!!");
			FileOutputStream out = new FileOutputStream("emanager.properties");
			props.store(out, null);
			out.close();
			logger.debug("******************* Suljetaha!!!");
			
		} catch (IOException e) {
			logger.error("Error writing properties file: " + e.getMessage());
			e.printStackTrace();
		}
		*/
	}
	
}
