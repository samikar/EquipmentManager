package db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.Test;

public class DatabaseutilTest {
	
	@Test
	public void testEntityManagerFactory_connection() {
		EntityManager em = null;
		em = DatabaseUtil.getEntityManager();
		assertNotNull(em);
		em.close();
	}
	
	@Test
	public void testEntityManagerFactory_closeConnection() {
		EntityManager em = null;
		em = DatabaseUtil.getEntityManager();
		assertNotNull(em);
		em.close();
		assertFalse(em.isOpen());
	}
}