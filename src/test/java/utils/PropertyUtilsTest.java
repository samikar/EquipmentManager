package utils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Test;

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
		
		assertNotEquals(0, properties.getProperty("DBurl").length());
		assertNotEquals(0, properties.getProperty("DBuser").length());
		assertNotEquals(0, properties.getProperty("DBpassword").length());
		assertNotEquals(0, properties.getProperty("DBdriver").length());
		
		assertNotEquals(0, properties.getProperty("testDBurl").length());
		assertNotEquals(0, properties.getProperty("testDBuser").length());
		assertNotEquals(0, properties.getProperty("testDBpassword").length());
		assertNotEquals(0, properties.getProperty("testDBdriver").length());
		
		assertNotEquals(0, properties.getProperty("WORKDAY").length());
		assertNotEquals(0, properties.getProperty("STARTHOUR").length());
		assertNotEquals(0, properties.getProperty("STARTMINUTE").length());
		assertNotEquals(0, properties.getProperty("ENDHOUR").length());
		assertNotEquals(0, properties.getProperty("ENDMINUTE").length());
		
		assertNotEquals(0, properties.getProperty("EquipmentFileFirstDataRow").length());
		assertNotEquals(0, properties.getProperty("EquipmentFileLastDataRow").length());
		assertNotEquals(0, properties.getProperty("EquipmentFileNameColumn").length());
		assertNotEquals(0, properties.getProperty("EquipmentFileSerialColumn").length());
		assertNotEquals(0, properties.getProperty("EquipmentFileTypeColumn").length());
		
		assertNotEquals(0, properties.getProperty("TypeFileFirstDataRow").length());
		assertNotEquals(0, properties.getProperty("TypeFileLastDataRow").length());
		assertNotEquals(0, properties.getProperty("TypeFileTypeNameColumn").length());
		assertNotEquals(0, properties.getProperty("TypeFileTypeCodeColumn").length());
	}
}
