package fi.danfoss.equipmentmanager.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ADHandlerTest {
	static ADHandler handler;
	@BeforeClass
	public static void init() {
		 handler = new ADHandler();
		 handler.init();
	}
	
	@AfterClass
	public static void destroy() {
		handler.close();
	}
	
	@Test
	public void testADConnection() {
		assertNotNull(handler);
	}
	
	@Test
	public void findEmptyString() {
		assertEquals(handler.findEmployeeName(""), "");
	}
	
	@Test
	public void findAbcd() {
		assertEquals(handler.findEmployeeName("abcd"), "");
	}
	
	@Test
	public void findVesaLaisi() {
		assertEquals(handler.findEmployeeName("00186763"), "Vesa Laisi");
	}

}
