package fi.danfoss.equipmentmanager.controller;

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

import org.apache.poi.util.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import fi.danfoss.equipmentmanager.model.Equipment;
import fi.danfoss.equipmentmanager.model.EquipmentDao;
import fi.danfoss.equipmentmanager.model.Equipmenttype;
import fi.danfoss.equipmentmanager.model.EquipmenttypeDao;

@Ignore
public class ConfigurationControllerTest {
	
	private final static String TESTFILEPATH = "test_files" + File.separator;
	private final static String TESTFILENAME = "test_file.txt";
	private final static String EQUIPMENTFILE = "laitteet.xlsx";
	private final static String TYPETFILE = "luokat.xlsx";
	
	@Autowired
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ConfigurationController controller;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
    @BeforeClass
    public static void init() {
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
        
        edao.init();
        etdao.init();
        
		controller = new ConfigurationController();
    }
	
    @AfterClass
    public static void destroy() {
    	edao.destroy();
        etdao.destroy();
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
	public void testUploadEquipmentFile_OK() {
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
	public void testUploadEquipmentFile_WrongFileType() {
		File testFile = createTestFile(TESTFILEPATH, TESTFILENAME);
		ResponseEntity<Object> response = null;
		MultipartFile multipartFile = convertFileToMultipartFile(testFile);
		
		try {
			response = controller.uploadEquipmentFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("File extension should be \"xlsx\" (Excel spreadsheet).", response.getBody());
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
	}

	@Test
	public void testUploadTypeFile_OK() {
		File testFile = createTestFile(TESTFILEPATH, TYPETFILE);
		ResponseEntity<Object> response = null;
		MultipartFile multipartFile = convertFileToMultipartFile(testFile);
		
		try {
			response = controller.uploadTypeFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testUploadTypeFile_WrongFileType() {
		File testFile = createTestFile(TESTFILEPATH, TESTFILENAME);
		ResponseEntity<Object> response = null;
		MultipartFile multipartFile = convertFileToMultipartFile(testFile);
		
		try {
			response = controller.uploadTypeFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("File extension should be \"xlsx\" (Excel spreadsheet).", response.getBody());
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
	}

	@Test
	public void testGetEquipmentTypes() {
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipmenttype testEquipmentType2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeTypeName2);
		
		etdao.persist(testEquipmentType1);
		etdao.persist(testEquipmentType2);
		
		List<Equipmenttype> equipmentTypes = controller.getEquipmentTypes();
		
		assertEquals(2, equipmentTypes.size());
		assertEquals(equipmentTypeTypeCode1, equipmentTypes.get(0).getTypeCode());
		assertEquals(equipmentTypeTypeName1, equipmentTypes.get(0).getTypeName());
		assertEquals(equipmentTypeTypeCode2, equipmentTypes.get(1).getTypeCode());
		assertEquals(equipmentTypeTypeName2, equipmentTypes.get(1).getTypeName());
	}

	@Test
	public void testGetEquipment() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "TestEquipment2";
		String equipmentName3 = "TestEquipment3";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
		String equipmentSerial3 = "TestSerial3";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
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
		assertEquals(equipmentTypeTypeName1, equipments.get(0).getEquipmenttype().getTypeName());
		
		assertEquals(equipmentName2, equipments.get(1).getName());
		assertEquals(equipmentSerial2, equipments.get(1).getSerial());
		assertEquals(equipmentStatusEnabled, equipments.get(1).getStatus());
		assertEquals(equipmentTypeTypeCode1, equipments.get(1).getEquipmenttype().getTypeCode());
		assertEquals(equipmentTypeTypeName1, equipments.get(1).getEquipmenttype().getTypeName());
		
		assertEquals(equipmentName3, equipments.get(2).getName());
		assertEquals(equipmentSerial3, equipments.get(2).getSerial());
		assertEquals(equipmentStatusEnabled, equipments.get(2).getStatus());
		assertEquals(equipmentTypeTypeCode1, equipments.get(2).getEquipmenttype().getTypeCode());
		assertEquals(equipmentTypeTypeName1, equipments.get(2).getEquipmenttype().getTypeName());		
	}

	@Test
	public void testDisableEquipment() {
		int equipmentStatusEnabled = 1;
		int equipmentStatusDisabled = 0;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentId;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment DBequipment = null;
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		
		etdao.persist(testEquipmentType1);
		equipmentId = edao.persist(testEquipment1);
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		controller.disableEquipment(Integer.toString(equipmentId));
		edao.refresh();
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentStatusDisabled, DBequipment.getStatus());
	}

	@Test
	public void testEnableEquipmentTest() {
		int equipmentStatusEnabled = 1;
		int equipmentStatusDisabled = 0;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment DBequipment = null;
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusDisabled, testEquipmentType1);
		
		etdao.persist(testEquipmentType1);
		int equipmentId = edao.persist(testEquipment1);
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentStatusDisabled, DBequipment.getStatus());
		controller.enableEquipment(Integer.toString(equipmentId));
		edao.refresh();
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
	}

	@Test
	public void testInsertEquipment_OK() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeId;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipment DBequipment = null;
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId = etdao.persist(testEquipmentType1);
		controller.insertEquipment(equipmentName1, equipmentSerial1, Integer.toString(equipmentTypeId));
		edao.refresh();
		DBequipment = edao.getBySerial(equipmentSerial1);
		
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
	}

	@Test
	public void testInsertEquipment_SerialEmpty() {
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeId;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId = etdao.persist(testEquipmentType1);
		exception.expect(IllegalArgumentException.class);
		controller.insertEquipment(equipmentName1, equipmentSerial1, Integer.toString(equipmentTypeId));
	}
	
	@Test
	public void testInsertEquipment_NameEmpty() {
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeId;
		String equipmentName1 = "";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId = etdao.persist(testEquipmentType1);
		exception.expect(IllegalArgumentException.class);
		controller.insertEquipment(equipmentName1, equipmentSerial1, Integer.toString(equipmentTypeId));
	}
	
	@Test
	public void testInsertEquipment_EquipmentTypeIdEmpty() {
		String equipmentTypeTypeCode1 = "";
		String equipmentName1 = "";
		String equipmentSerial1 = "TestSerial1";
		
		exception.expect(IllegalArgumentException.class);
		controller.insertEquipment(equipmentName1, equipmentSerial1, equipmentTypeTypeCode1);
	}
	

	@Test
	public void testInsertEquipment_SerialDuplicate() {
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeId;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "TestEquipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId = etdao.persist(testEquipmentType1);
		controller.insertEquipment(equipmentName1, equipmentSerial1, Integer.toString(equipmentTypeId));
		exception.expect(IllegalArgumentException.class);
		controller.insertEquipment(equipmentName2, equipmentSerial1, Integer.toString(equipmentTypeId));
	}
	
	@Test
	public void testInsertType_OK() {
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype DBequipmenttype = null;

		controller.insertType(equipmentTypeTypeName1, Integer.toString(equipmentTypeTypeCode1));
		etdao.refresh();
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmentTypeTypeCode1));
		DBequipmenttype = etdao.getDao();
		
		assertEquals(1, etdao.getAll().size());
		assertEquals(equipmentTypeTypeCode1, DBequipmenttype.getTypeCode());
		assertEquals(equipmentTypeTypeName1, DBequipmenttype.getTypeName());
	}
	
	@Test
	public void testInsertType_TypeNameEmpty() {
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeName1 = "";
		
		exception.expect(IllegalArgumentException.class);
		controller.insertType(equipmentTypeTypeName1, Integer.toString(equipmentTypeTypeCode1));
	}
	
	@Test
	public void testInsertType_TypeCodeDuplicate() {
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
				
		controller.insertType(equipmentTypeTypeName1, Integer.toString(equipmentTypeTypeCode1));
		exception.expect(IllegalArgumentException.class);
		controller.insertType(equipmentTypeTypeName2, Integer.toString(equipmentTypeTypeCode1));
	}
	
	@Test
	public void testInsertType_TypeCodeString() {
		String equipmentTypeTypeCode1 = "foobar";
		String equipmentTypeTypeName1 = "TestType1";
		
		exception.expect(IllegalArgumentException.class);
		controller.insertType(equipmentTypeTypeName1, equipmentTypeTypeCode1);
	}
	
	@Test
	public void testUpdateEquipment_OK() {
		int equipmentId, equipmentTypeId2;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "TestEquipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipmenttype testEquipmentType2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeTypeName2);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment DBequipment = null;
		
		etdao.persist(testEquipmentType1);
		equipmentTypeId2 = etdao.persist(testEquipmentType2);
		equipmentId = edao.persist(testEquipment1);
		
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
		
		controller.updateEquipment(Integer.toString(equipmentId), equipmentName2, equipmentSerial2, Integer.toString(equipmentTypeId2));
		edao.refresh();
		DBequipment = edao.getBySerial(equipmentSerial2);
		assertEquals(1, edao.getAll().size());
		assertEquals(equipmentId, DBequipment.getEquipmentId());
		assertEquals(equipmentName2, DBequipment.getName());
		assertEquals(equipmentSerial2, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName2, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode2, DBequipment.getEquipmenttype().getTypeCode());
	}
	
	@Test
	public void testUpdateEquipment_NameEmpty() {
		int equipmentId, equipmentTypeId1;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment DBequipment = null;
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		equipmentId = edao.persist(testEquipment1);
				
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
		exception.expect(IllegalArgumentException.class);
		controller.updateEquipment(Integer.toString(equipmentId), equipmentName2, equipmentSerial1, Integer.toString(equipmentTypeId1));
	}
	
	@Test
	public void testUpdateEquipment_SerialEmpty() {
		int equipmentId, equipmentTypeId1;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "";
		String equipmentTypeTypeName1 = "TestType1";
		
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment DBequipment = null;
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		equipmentId = edao.persist(testEquipment1);
				
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
		exception.expect(IllegalArgumentException.class);
		controller.updateEquipment(Integer.toString(equipmentId), equipmentName1, equipmentSerial2, Integer.toString(equipmentTypeId1));
	}
	
	@Test
	public void testUpdateEquipment_EquipmentTypeIdEmpty() {
		int equipmentId;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeId2 = "";
		
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment DBequipment = null;
		
		etdao.persist(testEquipmentType1);
		equipmentId = edao.persist(testEquipment1);
				
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
		exception.expect(IllegalArgumentException.class);
		controller.updateEquipment(Integer.toString(equipmentId), equipmentName1, equipmentSerial1, equipmentTypeId2);
	}
	
	@Test
	public void testUpdateEquipment_SerialDuplicate() {
		int equipmentId, equipmentTypeId1;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentName1 = "TestEquipment1";
		String equipmentName2 = "TestEquipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipmenttype testEquipmentType2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeTypeName2);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment testEquipment2 = new Equipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType1);
		Equipment DBequipment = null;
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		etdao.persist(testEquipmentType2);
		equipmentId = edao.persist(testEquipment1);
		edao.persist(testEquipment2);
		
		DBequipment = edao.getBySerial(equipmentSerial1);
		assertEquals(equipmentName1, DBequipment.getName());
		assertEquals(equipmentSerial1, DBequipment.getSerial());
		assertEquals(equipmentStatusEnabled, DBequipment.getStatus());
		assertEquals(equipmentTypeTypeName1, DBequipment.getEquipmenttype().getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipment.getEquipmenttype().getTypeCode());
		
		exception.expect(IllegalArgumentException.class);
		controller.updateEquipment(Integer.toString(equipmentId), equipmentName1, equipmentSerial2, Integer.toString(equipmentTypeId1));
	}
	
	@Test
	public void testUpdateType_OK() {
		int equipmentTypeId1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipmenttype DBequipmentType = null;
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		DBequipmentType = etdao.getByTypeCode(equipmentTypeTypeCode1);
		
		assertEquals(equipmentTypeTypeName1, DBequipmentType.getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipmentType.getTypeCode());
		controller.updateType(Integer.toString(equipmentTypeId1), equipmentTypeTypeName2, Integer.toString(equipmentTypeTypeCode2));
		edao.refresh();
		DBequipmentType = etdao.getByTypeCode(equipmentTypeTypeCode1);
		assertEquals(equipmentTypeTypeName1, DBequipmentType.getTypeName());
		assertEquals(equipmentTypeTypeCode1, DBequipmentType.getTypeCode());
	}
	
	@Test
	public void testUpdateType_TypeNameEmpty() {
		int equipmentTypeId1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		exception.expect(IllegalArgumentException.class);
		controller.updateType(Integer.toString(equipmentTypeId1), equipmentTypeTypeName2, Integer.toString(equipmentTypeTypeCode1));
	}
	
	@Test
	public void testUpdateType_TypeCodeEmpty() {
		int equipmentTypeId1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeCode2 = "";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		exception.expect(IllegalArgumentException.class);
		controller.updateType(Integer.toString(equipmentTypeId1), equipmentTypeTypeName1,equipmentTypeTypeCode2);
	}
	
	@Test
	public void testUpdateType_TypeCodeInvalid() {
		int equipmentTypeId1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeCode2 = "foobar";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		exception.expect(IllegalArgumentException.class);
		controller.updateType(Integer.toString(equipmentTypeId1), equipmentTypeTypeName1, equipmentTypeTypeCode2);
	}
	
	@Test
	public void testUpdateType_TypeCodeDuplicate() {
		int equipmentTypeId1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		String equipmentTypeTypeName1 = "TestType1";
		String equipmentTypeTypeName2 = "TestType2";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipmenttype testEquipmentType2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeTypeName2);
		
		equipmentTypeId1 = etdao.persist(testEquipmentType1);
		etdao.persist(testEquipmentType2);
		exception.expect(IllegalArgumentException.class);
		controller.updateType(Integer.toString(equipmentTypeId1), equipmentTypeTypeName1, Integer.toString(equipmentTypeTypeCode2));
	}
	
	@Test
	public void testDeleteEquipment_OK() {
		int equipmentId;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		
		etdao.persist(testEquipmentType1);
		equipmentId = edao.persist(testEquipment1);
		
		assertTrue(edao.serialExists(equipmentSerial1));
		controller.deleteEquipment(Integer.toString(equipmentId));
		edao.refresh();
		assertFalse(edao.serialExists(equipmentSerial1));
	}
	
	@Test
	public void testDeleteEquipment_EquipmentIdEmpty() {
		String equipmentId = "";
		exception.expect(IllegalArgumentException.class);
		controller.deleteEquipment(equipmentId);
	}
	
	@Test
	public void testDeleteEquipment_EquipmentIdInvalid() {
		String equipmentId = "foobar";
		exception.expect(IllegalArgumentException.class);
		controller.deleteEquipment(equipmentId);
	}
	
	@Test
	public void testDeleteEquipment_EquipmentIdNotFound() {
		int equipmentId = 1;
		exception.expect(IllegalArgumentException.class);
		controller.deleteEquipment(Integer.toString(equipmentId));
	}
	
	@Test
	public void testDeleteType_OK() {
		int equipmentTypeId;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
				
		equipmentTypeId = etdao.persist(testEquipmentType1);	
		assertTrue(etdao.equipmentTypeIdExists(equipmentTypeId));
		controller.deleteType(Integer.toString(equipmentTypeId));
		etdao.refresh();
		assertFalse(etdao.equipmentTypeIdExists(equipmentTypeId));
	}
	
	@Test
	public void testDeleteType_EquipmentTypeIdEmpty() {
		String equipmentTypeId = "";
		exception.expect(IllegalArgumentException.class);
		controller.deleteType(equipmentTypeId);
	}
	
	@Test
	public void testDeleteType_EquipmentTypeIdInvalid() {
		String equipmentTypeId = "foobar";
		exception.expect(IllegalArgumentException.class);
		controller.deleteType(equipmentTypeId);
	}

	@Test
	public void testDeleteType_EquipmentTypeIdNotFound() {
		int equipmentTypeId = 1111;
		exception.expect(IllegalArgumentException.class);
		controller.deleteType(Integer.toString(equipmentTypeId));
	}
	
	@Test
	public void testDeleteType_EquipmentTypeHasEquipment() {
		int equipmentTypeId;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		String equipmentName1 = "TestEquipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeTypeName1 = "TestType1";
		Equipmenttype testEquipmentType1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeTypeName1);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		
		equipmentTypeId = etdao.persist(testEquipmentType1);
		edao.persist(testEquipment1);
		exception.expect(IllegalArgumentException.class);
		controller.deleteType(Integer.toString(equipmentTypeId));
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
