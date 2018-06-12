package fi.danfoss.equipmentmanager.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import fi.danfoss.equipmentmanager.utils.PropertyUtils;

public class DatabaseUtil {
	static Properties properties = PropertyUtils.loadProperties();
	private final static String url = properties.getProperty("DBurl");
	private final static String user = properties.getProperty("DBuser");
	private final static String password = properties.getProperty("DBpassword");
	private final static String driver = properties.getProperty("DBdriver");
	final static Logger logger = Logger.getLogger(DatabaseUtil.class);

	private static final EntityManagerFactory entityManagerFactory;
	static Map<String, String> persistenceMap = new HashMap<String, String>();

	/**
	 * Creates DB-connection
	 */
	static {
		logger.info("Setting DB connection properties...");
		setProperties();
		logger.info("DB connection properties set!");
		logger.info("Connecting to database...");
		entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager", persistenceMap);
		logger.info("Connection complete!");	
	}

	/**
	 * Creates EntityManager
	 * 
	 * @return
	 */
	public static EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	/**
	 * Closes DB-connection
	 * 
	 */
	public static void shutdown() {
		entityManagerFactory.close();
	}

	/**
	 * Sets DB-connection properties
	 * 
	 */
	private static void setProperties() {
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", user);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", driver);
	}

}