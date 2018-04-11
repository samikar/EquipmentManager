package utils;

import static org.junit.Assert.assertEquals;
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
import java.util.Properties;

import org.apache.poi.util.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import db.DatabaseUtil;
import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;

@Ignore
public class EquipmentDataReaderTest {
	private static Properties properties = PropertyUtils.loadProperties();
	
	@Autowired
	private static String testDBurl;
	private static String testDBuser;
	private static String testDBpassword;
	private static String testDBdriver;
	private final String TESTFILEPATH = "test_files" + File.separator;
	private final String TESTFILENAME = "test_file.txt";
	private final String NOSUCHFILE = "NOSUCHFILE.txt";
	private final String TESTEQUIPMENTFILE = "laitteet.xlsx";
	private final String TESTTYPETFILE = "luokat.xlsx";
	private final String TESTEQUIPMENTSERIAL = "MI_PHE3 6 20 S";
	private final String TESTEQUIPMENTNAME = "20kV Jännitteenkoetin";
	private final int TESTTYPECODE = 1370;
	private final String TESTTYPENAME = "JÄNNITTEENKOETIN";
	
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	static EquipmentDataReader equipmentDataReader;
	
    @BeforeClass
    public static void init() {
    	testDBurl = properties.getProperty("testDBurl");
    	testDBuser = properties.getProperty("testDBuser");
    	testDBpassword = properties.getProperty("testDBpassword");
    	testDBdriver = properties.getProperty("testDBdriver");
    	
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
    	edao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    	etdao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    	edao.init();
        etdao.init();
        
		equipmentDataReader = new EquipmentDataReader();
		equipmentDataReader.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    }
    
    @AfterClass
    public static void destroy() {
        edao.destroy();
        etdao.destroy();
        DatabaseUtil.shutdown();
    }
    
	@Before
	public void initTest() {

		emptyTables();
		deleteTestFile();
	}
	
    @After
    public void endTest() {
    	emptyTables();
    	deleteTestFile();
    }
	
	@Test
	public void equipmentFileNotFound() {
		assertTrue(equipmentDataReader.readEquipmentFromFile(TESTFILEPATH + NOSUCHFILE).equals("Equipment file not found!"));
	}
	
	@Test
	public void equipmentTypeFileNotFound() {
		assertTrue(equipmentDataReader.readEquipmentTypesFromFile(TESTFILEPATH + NOSUCHFILE).equals("Equipment type file not found!"));
	}
	
	@Test
	public void verifyFileExtensionTrue() {
		assertTrue(equipmentDataReader.verifyFileExtension(TESTFILEPATH + TESTEQUIPMENTFILE));
	}

	@Test
	public void verifyFileExtensionFalse() {
		assertFalse(equipmentDataReader.verifyFileExtension(TESTFILEPATH + TESTFILENAME));
	}
	
	@Test
	public void verifyFileExtensionNoSuchFile() {
		assertFalse(equipmentDataReader.verifyFileExtension(TESTFILEPATH + NOSUCHFILE));
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
		assertTrue(equipmentDataReader.writeFile(multipartFile, TESTFILEPATH).equals(file));
	}
	
//	@Ignore
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
			newFile = equipmentDataReader.writeFile(multipartFile, TESTFILEPATH);
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
		// Method returns true if file deleted succesfully
		assertTrue(equipmentDataReader.deleteFile(testFile));
		// Checking if file deleted
		assertFalse(testFile.exists());
		
	}
	
//	@Ignore
	@Test
	public void readEquipmentTypesFromFile() {		
		equipmentDataReader.readEquipmentTypesFromFile(TESTFILEPATH + TESTTYPETFILE);
		etdao.refresh();
		
		int equipmenTypeId = etdao.getEquipmentTypeIdByTypeCode(TESTTYPECODE);

		etdao.initialize(equipmenTypeId);
		Equipmenttype DBequipmenttype = etdao.getDao();
		
		assertEquals(TESTTYPENAME, DBequipmenttype.getTypeName());
		assertEquals(TESTTYPECODE, DBequipmenttype.getTypeCode());
		
		
	}
	
//	@Ignore
	@Test
	public void readEquipmentFromFileTest() {
		equipmentDataReader.readEquipmentTypesFromFile(TESTFILEPATH + TESTTYPETFILE);
		equipmentDataReader.readEquipmentFromFile(TESTFILEPATH + TESTEQUIPMENTFILE);
		
		edao.refresh();
		
		Equipment DBequipment = edao.getBySerial(TESTEQUIPMENTSERIAL);
		
		assertEquals(TESTEQUIPMENTNAME, DBequipment.getName());
		assertEquals(TESTEQUIPMENTSERIAL, DBequipment.getSerial());
		assertEquals(TESTTYPENAME, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(TESTTYPECODE, DBequipment.getEquipmenttype().getTypeCode());	
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
	
	public void deleteTestFile() {
    	Path testFile = Paths.get(TESTFILEPATH + TESTFILENAME);
    	if(testFile.toFile().exists()) { 
	    	try {
				Files.delete(testFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    	}
	}
	
    public void emptyTables() {
    	edao.refresh();
    	etdao.refresh();
            	
    	List<Equipment> equipments = edao.getAll();
    	for (Equipment currentEquipment : equipments) {
    		edao.initialize(currentEquipment.getEquipmentId());
    		edao.delete();
    	}
    	
    	List<Equipmenttype> equipmentTypes = etdao.getAll();
    	for (Equipmenttype currentEquipmentType : equipmentTypes) {
    		etdao.initialize(currentEquipmentType.getEquipmentTypeId());
    		etdao.delete();
    	}
    }
}
