package utils;

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
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import utils.EquipmentDataReader;

public class EquipmentDataReaderTest {
	private final String TESTFILEPATH = "test_files" + File.separator;
	private final String TESTFILENAME = "test_file.txt";
	private final String NOSUCHFILE = "NOSUCHFILE.txt";
	private final String EQUIPMENTFILE = "laitteet.xlsx";
	private final String TYPEFILE = "luokat.xlsx";
	private final String EQUIPMENTFILE_WRONGHEADERS = "laitteet_wrong.xlsx";
	private final String TYPEFILE_WRONGHEADERS = "luokat_wrong.xlsx";
	
	
	@Test
	public void equipmentFileNotFound() {
		assertTrue(EquipmentDataReader.readEquipmentFromFile(TESTFILEPATH + NOSUCHFILE).equals("Equipment file not found!"));
	}
	
	@Test
	public void equipmentTypeFileNotFound() {
		assertTrue(EquipmentDataReader.readEquipmentTypesFromFile(TESTFILEPATH + NOSUCHFILE).equals("Equipment type file not found!"));
	}
	
	@Test
	public void verifyFileExtensionTrue() {
		assertTrue(EquipmentDataReader.verifyFileExtension(TESTFILEPATH + EQUIPMENTFILE));
	}

	@Test
	public void verifyFileExtensionFalse() {
		assertFalse(EquipmentDataReader.verifyFileExtension(TESTFILEPATH + TESTFILENAME));
	}
	
	@Test
	public void verifyFileExtensionNoSuchFile() {
		assertFalse(EquipmentDataReader.verifyFileExtension(TESTFILEPATH + NOSUCHFILE));
	}
	
	@Test
	public void verifyEquipmentFileHeadersOK() {
		assertTrue(EquipmentDataReader.verifyEquipmentFileHeaders(TESTFILEPATH + EQUIPMENTFILE).equals("OK"));
	}
	
	@Test
	public void verifyEquipmentFileHeadersNotOK() {
		assertFalse(EquipmentDataReader.verifyEquipmentFileHeaders(TESTFILEPATH + EQUIPMENTFILE_WRONGHEADERS).equals("OK"));
	}
	
	@Test
	public void verifyTypeFileHeadersOK() {
		assertTrue(EquipmentDataReader.verifyTypeFileHeaders(TESTFILEPATH + TYPEFILE).equals("OK"));
	}
	
	@Test
	public void verifyTypeFileHeadersNotOK() {
		assertFalse(EquipmentDataReader.verifyTypeFileHeaders(TESTFILEPATH + TYPEFILE_WRONGHEADERS).equals("OK"));
	}
	
	@Test
	public void writeFileWritingSuccesful() {
		MultipartFile multipartFile = null;
		File file = createTestFile(TESTFILEPATH, TESTFILENAME);
		FileInputStream input = null;
		
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(EquipmentDataReader.writeFile(multipartFile, TESTFILEPATH).equals(file));
	}
	
	@Test
	public void writeFileContentsEqual() {
		byte[] oldFileBytes = null;
		byte[] newFileBytes = null;
		File oldFile = createTestFile(TESTFILEPATH, TESTFILENAME);
		File newFile = null;
		FileInputStream input = null;
		MultipartFile multipartFile = null;
		
		try {
			input = new FileInputStream(oldFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			multipartFile = new MockMultipartFile("file", oldFile.getName(), "text/plain", IOUtils.toByteArray(input));
			newFile = EquipmentDataReader.writeFile(multipartFile, TESTFILEPATH);
			input.close();
			oldFileBytes = Files.readAllBytes(oldFile.toPath());
			newFileBytes = Files.readAllBytes(newFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(Arrays.equals(oldFileBytes, newFileBytes));
	}
	
	@Test
	public void deleteFileTest() {
		File testFile = createTestFile(TESTFILEPATH, TESTFILENAME);
		// File exists
		assertTrue(testFile.exists());
		// Method supposed to return true if file deleted succesfully
		assertTrue(EquipmentDataReader.deleteFile(testFile));
		// Checking if file deleted
		assertFalse(testFile.exists());
		
	}
	
	public File createTestFile(String path, String fileName) {
		Path file = Paths.get(path + fileName);
		if(!file.toFile().exists()) { 
			List<String> fileContents = Arrays.asList("Foo", "bar", "Foobar");
			try {
				Files.write(file, fileContents, Charset.forName("UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return file.toFile();
	}
}
