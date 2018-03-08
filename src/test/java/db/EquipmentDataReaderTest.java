package db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EquipmentDataReaderTest {
	private final String testFilePath = "test_files" + File.separator;
	
	@Test
	public void equipmentFileNotFound() {
		assertTrue(EquipmentDataReader.readEquipmentFromFile(testFilePath + "nosuchfile.txt").equals("Equipment file not found!"));
	}
	
	@Test
	public void equipmentTypeFileNotFound() {
		assertTrue(EquipmentDataReader.readEquipmentTypesFromFile(testFilePath + "nosuchfile.txt").equals("Equipment type file not found!"));
	}
	
	@Test
	public void verifyFileExtensionTrue() {
		assertTrue(EquipmentDataReader.verifyFileExtension(testFilePath + "laitteet.xlsx"));
	}

	@Test
	public void verifyFileExtensionFalse() {
		assertFalse(EquipmentDataReader.verifyFileExtension(testFilePath + "test_file.txt"));
	}
	
	@Test
	public void verifyFileExtensionNoSuchFile() {
		assertFalse(EquipmentDataReader.verifyFileExtension(testFilePath + "nosuchfile.txt"));
	}
	
	@Test
	public void verifyEquipmentFileHeadersOK() {
		assertTrue(EquipmentDataReader.verifyEquipmentFileHeaders(testFilePath + "laitteet.xlsx").equals("OK"));
	}
	
	@Test
	public void verifyEquipmentFileHeadersNotOK() {
		assertFalse(EquipmentDataReader.verifyEquipmentFileHeaders(testFilePath + "laitteet_wrong.xlsx").equals("OK"));
	}
	
	@Test
	public void verifyTypeFileHeadersOK() {
		assertTrue(EquipmentDataReader.verifyTypeFileHeaders(testFilePath + "luokat.xlsx").equals("OK"));
	}
	
	@Test
	public void verifyTypeFileHeadersNotOK() {
		assertFalse(EquipmentDataReader.verifyTypeFileHeaders(testFilePath + "luokat_wrong.xlsx").equals("OK"));
	}
	
	
	
	@Test
	public void writeFileTest() {
		File oldFile = new File(testFilePath + "test_file.txt");
	    FileInputStream input = null;
	    MultipartFile multipartFile = null;
		try {
			input = new FileInputStream(oldFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			 multipartFile = new MockMultipartFile("file", "new_" + oldFile.getName(), "text/plain", IOUtils.toByteArray(input));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    File newFile = EquipmentDataReader.writeFile(multipartFile, testFilePath);
	    
	    byte[] oldFileBytes = null;
	    byte[] newFileBytes = null;
	    
		try {
			oldFileBytes = Files.readAllBytes(oldFile.toPath());
			newFileBytes = Files.readAllBytes(newFile.toPath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    assertTrue(Arrays.equals(oldFileBytes, newFileBytes));
	}
	
	// TODO: Bugittaa, ei poista tiedostoa
	@Test
	public void deleteFileTest() {
		Path file = Paths.get(testFilePath + "new_test_file.txt");
		if(!file.toFile().exists()) { 
			System.out.println("**** NO FILE *****");
			List<String> fileContents = Arrays.asList("Foo", "bar", "Foobar");
			
//			file = Paths.get(testFilePath + "new_test_file.txt");
			try {
				Files.write(file, fileContents, Charset.forName("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		// File exists
		assertTrue(file.toFile().exists());
		// Method supposed to return true if file deleted succesfully
		assertTrue(EquipmentDataReader.deleteFile(file.toFile()));
		// Checking if file deleted
		assertFalse(file.toFile().exists());
		
	}
	
	@Test
	public void deleteNonExistentFile() {
		Path file = Paths.get(testFilePath + "nosuchfile.txt");
		assertFalse(EquipmentDataReader.deleteFile(file.toFile()));
	}
	
	@Test
	public void deleteNull() {
		assertFalse(EquipmentDataReader.deleteFile(null));
	}
}
