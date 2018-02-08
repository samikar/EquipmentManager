package db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseUtil {

    private static EntityManagerFactory sessionFactory = buildSessionFactory();

    private static EntityManagerFactory buildSessionFactory() {
        try {
            //if (sessionFactory == null || !sessionFactory.isOpen()) {
        	if (sessionFactory == null) {
            	sessionFactory = Persistence.createEntityManagerFactory("EquipmentManager");
            }
            
            return sessionFactory;
        } catch (Throwable ex) {
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}