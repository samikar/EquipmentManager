package db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import utils.PropertyUtils;

public class DatabaseUtil {
	static Properties properties = PropertyUtils.loadProperties();
	private final static String url = properties.getProperty("DBurl");
	private final static String user = properties.getProperty("DBuser");
	private final static String password = properties.getProperty("DBpassword");
	private final static String driver = properties.getProperty("DBdriver");

	private static final EntityManagerFactory entityManagerFactory;
	static Map<String, String> persistenceMap = new HashMap<String, String>();

	static {
		setProperties();
		entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager", persistenceMap);
	}

	public static EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}

	public static void shutdown() {
		entityManagerFactory.close();
	}

	private static void setProperties() {
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", user);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", driver);
	}

}