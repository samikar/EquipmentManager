package db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import utils.PropertyUtils;

public class DatabaseUtil {

//    private static EntityManagerFactory sessionFactory = buildSessionFactory();
	private static Properties properties = PropertyUtils.loadProperties();
	private static EntityManagerFactory entityManagerFactory;
	static Map<String, String> persistenceMap = new HashMap<String, String>();

    private static EntityManagerFactory buildSessionFactory() {
        try {
        	if (entityManagerFactory == null) {
        		entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager", persistenceMap);
            }

            return entityManagerFactory;
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getSessionFactory() {
    	persistenceMap.put("javax.persistence.jdbc.url", properties.getProperty("DBurl"));
    	persistenceMap.put("javax.persistence.jdbc.user", properties.getProperty("DBuser"));
    	persistenceMap.put("javax.persistence.jdbc.password", properties.getProperty("DBpassword"));
    	persistenceMap.put("javax.persistence.jdbc.driver", properties.getProperty("DBdriver"));
    	entityManagerFactory = buildSessionFactory();
        return entityManagerFactory;
    }
    
    public static EntityManagerFactory getTestSessionFactory() {
    	persistenceMap.put("javax.persistence.jdbc.url", properties.getProperty("testDBurl"));
    	persistenceMap.put("javax.persistence.jdbc.user", properties.getProperty("testDBuser"));
    	persistenceMap.put("javax.persistence.jdbc.password", properties.getProperty("testDBpassword"));
    	persistenceMap.put("javax.persistence.jdbc.driver", properties.getProperty("testDBdriver"));
    	entityManagerFactory = buildSessionFactory();
    	return entityManagerFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}