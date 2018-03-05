package db;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EquipmentDataReaderTest {
	
	@Test
	public void equipmentFileNotFound() {
		assertThat(EquipmentDataReader.readEquipmentFromFile("test_files/nosuchfile.txt"),  containsString("Equipment file not found:"));
	}
	
	@Test
	public void equipmentFileCouldNotBeRead() {
		assertThat(EquipmentDataReader.readEquipmentFromFile("test_files/test_file.txt"),  containsString("Equipment file could not be read:"));
	}
	
	@Test
	public void equipmentTypeFileNotFound() {
		assertThat(EquipmentDataReader.readEquipmentTypesFromFile("test_files/nosuchfile.txt"),  containsString("Equipment file not found:"));
	}
	
	@Test
	public void equipmentTypeFileCouldNotBeRead() {
		assertThat(EquipmentDataReader.readEquipmentTypesFromFile("test_files/test_file.txt"),  containsString("Equipment file could not be read:"));
	}

}
