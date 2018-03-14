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
	
	@Ignore
	@Test
	public void testPropertyFileFound() {
		Path file = Paths.get(PropertyUtils.TESTPROPERTIES_PATH + PropertyUtils.TESTPROPERTIES_FILENAME);
		assertTrue(file.toFile().exists());
	}
	
	@Test
	public void propertiesCanBeRead() {
		assertNotNull(PropertyUtils.loadProperties());
	}
	
	@Ignore
	@Test
	public void testPropertiesCanBeRead() {
		assertNotNull(PropertyUtils.loadTestProperties());
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
		assertNotNull(properties.getProperty("EquipmentFileRowsBeforeData"));
		assertNotNull(properties.getProperty("EquipmentFileRowsAfterData"));
		assertNotNull(properties.getProperty("EquipmentFileDescriptionColumn"));
		assertNotNull(properties.getProperty("EquipmentFileDescriptionString"));
		assertNotNull(properties.getProperty("EquipmentFileSerialColumn"));
		assertNotNull(properties.getProperty("EquipmentFileSerialString"));
		assertNotNull(properties.getProperty("EquipmentFileTypeCodeColumn"));
		assertNotNull(properties.getProperty("EquipmentFileTypeCodeString"));
		
		// Equipment type file	
		assertNotNull(properties.getProperty("TypeFileRowsBeforeData"));
		assertNotNull(properties.getProperty("TypeFileRowsAfterData"));
		assertNotNull(properties.getProperty("TypeFileTypeNameColumn"));
		assertNotNull(properties.getProperty("TypeFileTypeNameStr"));
		assertNotNull(properties.getProperty("TypeFileTypeCodeColumn"));
		assertNotNull(properties.getProperty("TypeFileTypeCodeStr"));
	}
	
	@Ignore
	@Test
	public void allTestPropertiesExist() {
		Properties properties = PropertyUtils.loadTestProperties();
		
		assertNotNull(properties.getProperty("dbaddress"));
		assertNotNull(properties.getProperty("dbname"));
		assertNotNull(properties.getProperty("dbpassword"));
		assertNotNull(properties.getProperty("dbuser"));
		assertNotNull(properties.getProperty("ADuser"));
		assertNotNull(properties.getProperty("ADpassword"));
		assertNotNull(properties.getProperty("ADURL"));
		
		assertNotNull(properties.getProperty("WORKDAY"));
		assertNotNull(properties.getProperty("STARTHOUR"));
		assertNotNull(properties.getProperty("STARTMINUTE"));
		assertNotNull(properties.getProperty("ENDHOUR"));
		assertNotNull(properties.getProperty("ENDMINUTE"));
		
		assertNotNull(properties.getProperty("EquipmentFileRowsBeforeData"));
		assertNotNull(properties.getProperty("EquipmentFileRowsAfterData"));
		assertNotNull(properties.getProperty("EquipmentFileDescriptionColumn"));
		assertNotNull(properties.getProperty("EquipmentFileDescriptionString"));
		assertNotNull(properties.getProperty("EquipmentFileSerialColumn"));
		assertNotNull(properties.getProperty("EquipmentFileSerialString"));
		assertNotNull(properties.getProperty("EquipmentFileTypeCodeColumn"));
		assertNotNull(properties.getProperty("EquipmentFileTypeCodeString"));
		
		assertNotNull(properties.getProperty("TypeFileRowsBeforeData"));
		assertNotNull(properties.getProperty("TypeFileRowsAfterData"));
		assertNotNull(properties.getProperty("TypeFileTypeNameColumn"));
		assertNotNull(properties.getProperty("TypeFileTypeNameStr"));
		assertNotNull(properties.getProperty("TypeFileTypeCodeColumn"));
		assertNotNull(properties.getProperty("TypeFileTypeCodeStr"));
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
		
		assertNotEquals(properties.getProperty("EquipmentFileRowsBeforeData").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileRowsAfterData").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileDescriptionColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileDescriptionString").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileSerialColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileSerialString").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileTypeCodeColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileTypeCodeString").length(), 0);
		
		assertNotEquals(properties.getProperty("TypeFileRowsBeforeData").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileRowsAfterData").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeNameColumn").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeNameStr").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeCodeColumn").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeCodeStr").length(), 0);
	}
	
	@Ignore
	@Test
	public void allTestPropertiesHaveValues() {
		Properties properties = PropertyUtils.loadTestProperties();
		
		assertNotEquals(properties.getProperty("dbaddress").length(), 0);
		assertNotEquals(properties.getProperty("dbname").length(), 0);
		assertNotEquals(properties.getProperty("dbpassword").length(), 0);
		assertNotEquals(properties.getProperty("dbuser").length(), 0);
		assertNotEquals(properties.getProperty("ADuser").length(), 0);
		assertNotEquals(properties.getProperty("ADpassword").length(), 0);
		assertNotEquals(properties.getProperty("ADURL").length(), 0);
		
		assertNotEquals(properties.getProperty("WORKDAY").length(), 0);
		assertNotEquals(properties.getProperty("STARTHOUR").length(), 0);
		assertNotEquals(properties.getProperty("STARTMINUTE").length(), 0);
		assertNotEquals(properties.getProperty("ENDHOUR").length(), 0);
		assertNotEquals(properties.getProperty("ENDMINUTE").length(), 0);
		
		assertNotEquals(properties.getProperty("EquipmentFileRowsBeforeData").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileRowsAfterData").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileDescriptionColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileDescriptionString").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileSerialColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileSerialString").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileTypeCodeColumn").length(), 0);
		assertNotEquals(properties.getProperty("EquipmentFileTypeCodeString").length(), 0);
		
		assertNotEquals(properties.getProperty("TypeFileRowsBeforeData").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileRowsAfterData").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeNameColumn").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeNameStr").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeCodeColumn").length(), 0);
		assertNotEquals(properties.getProperty("TypeFileTypeCodeStr").length(), 0);
	}
}
