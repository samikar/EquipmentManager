package fi.danfoss.equipmentmanager.utils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

//@Ignore
public class EMPropertyUtilsTest {
	
	@Test
	public void propertyFileFound() {	
		ClassLoader classLoader = this.getClass().getClassLoader();
        File propertiesFile = new File(classLoader.getResource(EMPropertyUtils.PROPERTIES_FILENAME).getFile());
        assertTrue(propertiesFile.exists());
	}
	
	@Test
	public void propertiesCanBeRead() {
		assertNotNull(EMPropertyUtils.loadProperties());
	}
	
	@Test
	public void allPropertiesExist() {
		Properties properties = EMPropertyUtils.loadProperties();
		
		// Database
		assertNotNull(properties.getProperty("DBurl"));
		assertNotNull(properties.getProperty("DBuser"));
		assertNotNull(properties.getProperty("DBpassword"));
		assertNotNull(properties.getProperty("DBdriver"));
				
		// AD
		assertNotNull(properties.getProperty("ADuser"));
		assertNotNull(properties.getProperty("ADpassword"));
		assertNotNull(properties.getProperty("ADurl"));
		
		// Workday
		assertNotNull(properties.getProperty("WORKDAY"));
		assertNotNull(properties.getProperty("STARTHOUR"));
		assertNotNull(properties.getProperty("STARTMINUTE"));
		assertNotNull(properties.getProperty("ENDHOUR"));
		assertNotNull(properties.getProperty("ENDMINUTE"));

		// Paths
		assertNotNull(properties.getProperty("TempFilePath"));
				
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
		Properties properties = EMPropertyUtils.loadProperties();
		
		assertNotEquals(0, properties.getProperty("DBurl").length());
		assertNotEquals(0, properties.getProperty("DBuser").length());
		assertNotEquals(0, properties.getProperty("DBpassword").length());
		assertNotEquals(0, properties.getProperty("DBdriver").length());
		
		assertNotEquals(0, properties.getProperty("ADuser").length());
		assertNotEquals(0, properties.getProperty("ADpassword").length());
		assertNotEquals(0, properties.getProperty("ADurl").length());
		
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
