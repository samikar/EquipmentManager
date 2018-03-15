package utils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import utils.PropertyUtils;

public class PropertyUtilsTest {
	@Test
	public void propertyFileFound() {
		Path file = Paths.get(PropertyUtils.PROPERTIES_PATH + PropertyUtils.PROPERTIES_FILENAME);
		assertTrue(file.toFile().exists());
	}
	
	@Test
	public void propertiesCanBeRead() {
		assertNotNull(PropertyUtils.loadProperties());
	}
	
	@Test
	public void allPropertiesExist() {
		Properties properties = PropertyUtils.loadProperties();
		
		// Database
		assertNotNull(properties.getProperty("DBurl"));
		assertNotNull(properties.getProperty("DBuser"));
		assertNotNull(properties.getProperty("DBpassword"));
		assertNotNull(properties.getProperty("DBdriver"));
		
		// Test database
		assertNotNull(properties.getProperty("testDBurl"));
		assertNotNull(properties.getProperty("testDBuser"));
		assertNotNull(properties.getProperty("testDBpassword"));
		assertNotNull(properties.getProperty("testDBdriver"));
		
		// Workday
		assertNotNull(properties.getProperty("WORKDAY"));
		assertNotNull(properties.getProperty("STARTHOUR"));
		assertNotNull(properties.getProperty("STARTMINUTE"));
		assertNotNull(properties.getProperty("ENDHOUR"));
		assertNotNull(properties.getProperty("ENDMINUTE"));
		
		// Equipment file
		assertNotNull(properties.getProperty("EquipmentFileFirstDataRow"));
		assertNotNull(properties.getProperty("EquipmentFileLastDataRow"));
		assertNotNull(properties.getProperty("EquipmentFileNameColumn"));
		assertNotNull(properties.getProperty("EquipmentFileSerialColumn"));
		assertNotNull(properties.getProperty("EquipmentFileTypeColumn"));
		
		// Type file
		assertNotNull(properties.getProperty("TypeFileFirstDataRow"));
		assertNotNull(properties.getProperty("TypeFileLastDataRow"));
		assertNotNull(properties.getProperty("TypeFileTypeNameColumn"));
		assertNotNull(properties.getProperty("TypeFileTypeCodeColumn"));
	}
	
	@Test
	public void allPropertiesHaveValues() {
		Properties properties = PropertyUtils.loadProperties();
		
		assertNotEquals(properties.getProperty("DBurl").length(), 0);
		assertNotEquals(properties.getProperty("DBuser").length(), 0);
		assertNotEquals(properties.getProperty("DBpassword").length(), 0);
		assertNotEquals(properties.getProperty("DBdriver").length(), 0);
		
		assertNotEquals(properties.getProperty("testDBurl").length(), 0);
		assertNotEquals(properties.getProperty("testDBuser").length(), 0);
		assertNotEquals(properties.getProperty("testDBpassword").length(), 0);
		assertNotEquals(properties.getProperty("testDBdriver").length(), 0);
		
		assertNotEquals(properties.getProperty("WORKDAY").length(), 0);
		assertNotEquals(properties.getProperty("STARTHOUR").length(), 0);
		assertNotEquals(properties.getProperty("STARTMINUTE").length(), 0);
		assertNotEquals(properties.getProperty("ENDHOUR").length(), 0);
		assertNotEquals(properties.getProperty("ENDMINUTE").length(), 0);
		
		assertNotEquals(properties.getProperty("EquipmentFileFirstDataRow").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileLastDataRow").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileNameColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileSerialColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileTypeColumn").length(), 0);
		
		assertNotEquals(properties.getProperty("TypeFileFirstDataRow").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileLastDataRow").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeNameColumn").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeCodeColumn").length(), 0);
	}
}
