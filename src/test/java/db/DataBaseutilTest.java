package db;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;

public class DataBaseutilTest {

	@Test
	public void testEntityManagerFactory() {
		assertNotNull(DatabaseUtil.getSessionFactory());
		DatabaseUtil.shutdown();
	}
}
