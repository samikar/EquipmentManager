package db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
	
	@Ignore
	@Test
	public void testADConnection() {
		assertNotNull(handler);
	}
	
	@Ignore
	@Test
	public void findEmptyString() {
		assertEquals(handler.findEmployeeName(""), "");
	}
	
	@Ignore
	@Test
	public void findAbcd() {
		assertEquals(handler.findEmployeeName("abcd"), "");
	}
	
	@Ignore
	@Test
	public void findVesaLaisi() {
		assertEquals(handler.findEmployeeName("00186763"), "Vesa Laisi");
	}

}
