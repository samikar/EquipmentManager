package db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseUtil {

	private static EntityManagerFactory entityManagerFactory;
	static Map<String, String> persistenceMap = new HashMap<String, String>();

	private static EntityManagerFactory buildSessionFactory() {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager", persistenceMap);
			return entityManagerFactory;
		} catch (Throwable ex) {
			System.err.println("Initial EntityManagerFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static void setProperties(String url, String user, String password, String driver) {
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", user);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", driver);
	}

	public static EntityManagerFactory getSessionFactory() {
		entityManagerFactory = buildSessionFactory();
		return entityManagerFactory;
	}

	public static EntityManagerFactory getTestSessionFactory() {
		entityManagerFactory = buildSessionFactory();
		return entityManagerFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}
}