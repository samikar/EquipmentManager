package db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

public class ADHandlerTest {
	ADHandler handler = new ADHandler();
	
	@Test
	public void testADConnection() {
		assertNotNull(handler.init());
		handler.close();
	}
	
	@Test
	public void findEmptyString() {
		handler.init();
		assertEquals(handler.findEmployeeName(""), "");
		handler.close();
	}
	
	
	@Test
	public void findAbcd() {
		handler.init();
		assertEquals(handler.findEmployeeName("abcd"), "");
		handler.close();
	}
	
	
	@Test
	public void findVesaLaisi() {
		handler.init();
		assertEquals(handler.findEmployeeName("00186763"), "Vesa Laisi");
		handler.close();
	}

}
