package db;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

import utils.PropertyUtils;

public class DatabaseutilTest {
	private static Properties properties = PropertyUtils.loadProperties();

	@Test
	public void testEntityManagerFactory_TestDB() {
		DatabaseUtil.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
		assertNotNull(DatabaseUtil.getSessionFactory());
		DatabaseUtil.shutdown();
	}
	
	@Test
	public void testEntityManagerFactory_ProdDB() {
		DatabaseUtil.setProperties(properties.getProperty("DBurl"), properties.getProperty("DBuser"), properties.getProperty("DBpassword"), properties.getProperty("DBdriver"));
		assertNotNull(DatabaseUtil.getSessionFactory());
		DatabaseUtil.shutdown();
	}
}