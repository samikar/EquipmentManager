package db;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;

public class DataBaseUtilTest {

	@Test
	public void testEntityManagerFactory() {
		assertNotNull(DatabaseUtil.getSessionFactory());
		DatabaseUtil.shutdown();
	}
}
