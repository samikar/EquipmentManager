package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import db.DatabaseUtil;
import model.Employee;
import model.EmployeeDao;
import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.Reservation;
import model.ReservationDao;
import utils.PropertyUtils;

public class ConfigurationControllerTest {
	private static Properties properties = PropertyUtils.loadProperties();
	
	private final static String TESTFILEPATH = "test_files" + File.separator;
	private final static String TESTFILENAME = "test_file.txt";
	private final static String NOSUCHFILE = "NOSUCHFILE.txt";
	private final static String EQUIPMENTFILE = "laitteet.xlsx";
	private final static String TYPETFILE = "luokat.xlsx";
	
	@Autowired
	private static String testDBurl;
	private static String testDBuser;
	private static String testDBpassword;
	private static String testDBdriver;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ConfigurationController controller;
	
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
        
		controller = new ConfigurationController();
		controller.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
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
		deleteTestFiles();
	}
	
    @After
    public void endTest() {
    	emptyTables();
    	deleteTestFiles();
    }
	
	@Test
	public void uploadEquipmentFileTest_OK() {
		File testFile = createTestFile(TESTFILEPATH, EQUIPMENTFILE);
		
		ResponseEntity<Object> response = null;
		MultipartFile multipartFile = convertFileToMultipartFile(testFile);
		
		try {
			response = controller.uploadEquipmentFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void uploadEquipmentFileTest_wrongFileType() {
		File testFile = createTestFile(TESTFILEPATH, TESTFILENAME);
		ResponseEntity<Object> response = null;
		MultipartFile multipartFile = convertFileToMultipartFile(testFile);
		
		try {
			response = controller.uploadEquipmentFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
	}
	
	@Test
	public void getEquipmentTypesTest() {
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentTypeName1 = "TestType1";
		String equipmentTypeName2 = "TestType2";
		
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeName1);
		Equipmenttype testEquipmentType2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeName2);
		
		etdao.persist(testEquipmentType1);
		etdao.persist(testEquipmentType2);
		
		List<Equipmenttype> equipmentTypes = controller.getEquipmentTypes();
		
		assertEquals(2, equipmentTypes.size());
		assertEquals(equipmentTypeTypeCode1, equipmentTypes.get(0).getTypeCode());
		assertEquals(equipmentTypeName1, equipmentTypes.get(0).getTypeName());
		assertEquals(equipmentTypeTypeCode2, equipmentTypes.get(1).getTypeCode());
		assertEquals(equipmentTypeName2, equipmentTypes.get(1).getTypeName());
	}
	
	@Test
	public void getEquipmentTest() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "TestEquipment2";
		String equipmentName3 = "TestEquipment3";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
		String equipmentSerial3 = "TestSerial3";
		String equipmentTypeName1 = "TestType1";
		
		
		
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment testEquipment2 = new Equipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType1);
		Equipment testEquipment3 = new Equipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmentType1);
		
		etdao.persist(testEquipmentType1);
		edao.persist(testEquipment1);
		edao.persist(testEquipment2);
		edao.persist(testEquipment3);
		
		List<Equipment> equipments = controller.getEquipment();
		
		assertEquals(3, equipments.size());
		assertEquals(equipmentName1, equipments.get(0).getName());
		assertEquals(equipmentSerial1, equipments.get(0).getSerial());
		assertEquals(equipmentStatusEnabled, equipments.get(0).getStatus());
		assertEquals(equipmentTypeTypeCode1, equipments.get(0).getEquipmenttype().getTypeCode());
		assertEquals(equipmentTypeName1, equipments.get(0).getEquipmenttype().getTypeName());
		
		assertEquals(equipmentName2, equipments.get(1).getName());
		assertEquals(equipmentSerial2, equipments.get(1).getSerial());
		assertEquals(equipmentStatusEnabled, equipments.get(1).getStatus());
		assertEquals(equipmentTypeTypeCode1, equipments.get(1).getEquipmenttype().getTypeCode());
		assertEquals(equipmentTypeName1, equipments.get(1).getEquipmenttype().getTypeName());
		
		assertEquals(equipmentName3, equipments.get(2).getName());
		assertEquals(equipmentSerial3, equipments.get(2).getSerial());
		assertEquals(equipmentStatusEnabled, equipments.get(2).getStatus());
		assertEquals(equipmentTypeTypeCode1, equipments.get(2).getEquipmenttype().getTypeCode());
		assertEquals(equipmentTypeName1, equipments.get(2).getEquipmenttype().getTypeName());		
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
	
	public MultipartFile convertFileToMultipartFile(File file) {
		MultipartFile multipartFile = null;	
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
		
		return multipartFile;
	}
	
	public void deleteTestFiles() {
    	Path testFile = Paths.get(TESTFILEPATH + TESTFILENAME);
    	if(!testFile.toFile().exists()) { 
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
    	
    	List<Equipmenttype> equpmentTypes = etdao.getAll();
    	for (Equipmenttype currentEquipmentType : equpmentTypes) {
    		etdao.initialize(currentEquipmentType.getEquipmentTypeId());
    		etdao.delete();
    	}
    }
}
