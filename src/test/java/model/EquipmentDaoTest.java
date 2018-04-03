package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import utils.PropertyUtils;

public class EquipmentDaoTest {
	private static Properties properties = PropertyUtils.loadProperties();
	
	@Autowired
	private static String testDBurl;
	private static String testDBuser;
	private static String testDBpassword;
	private static String testDBdriver;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	
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
    }

	@AfterClass
	public static void destroy() {
		edao.destroy();
		etdao.destroy();
	}
	
	@Before
	public void initTest() {
		emptyTables();
	}
	
	@After
	public void endTest() {
		emptyTables();
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAddEquipment() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 1111;
		String equipmentName = "TestEquipment1";
		String equipmentSerial = "TestSerial1";
		String equipmentTypeName = "TestType1";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmenttype);
		etdao.persist(testEquipmenttype);
		edao.persist(testEquipment);
		edao.initialize(testEquipment.getEquipmentId());
		assertEquals(testEquipment, edao.getDao());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetAll() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 2222;
		int equipmentCount = 5;
		String equipmentName = "TestEquipment2_";
		String equipmentSerial = "TestSerial2_";
		String equipmentTypeName = "TestType2";
		
		List<Equipment> equipments = edao.getAll();
		assertEquals(0, equipments.size());
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		for (int i=0; i<equipmentCount; i++) {
			Equipment testEquipment = new Equipment(equipmentName + i, equipmentSerial + i, equipmentStatusEnabled, testEquipmenttype);
			edao.persist(testEquipment);
		}
		equipments = edao.getAll();
		assertEquals(equipmentCount, equipments.size());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentIdBySerial_Found() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 3333;
		String equipmentName = "TestEquipment3";
		String equipmentSerial = "TestSerial3";
		String equipmentTypeName = "TestType3";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmenttype);
		int equipmentId = edao.persist(testEquipment);
		
		assertEquals(equipmentId, edao.getEquipmentIdBySerial(equipmentSerial));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentIdBySerialNotFound() {
		String equipmentSerial = "TestSerial4";
		assertEquals(0, edao.getEquipmentIdBySerial(equipmentSerial));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetBySerial() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 5555;
		String equipmentName = "TestEquipment5";
		String equipmentSerial = "TestSerial5";
		String equipmentTypeName = "TestType5";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		
		Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmenttype);
		edao.persist(testEquipment);
		Equipment equipmentFromDB = edao.getBySerial(equipmentSerial);

		assertEquals(equipmentName, equipmentFromDB.getName());
		assertEquals(equipmentSerial, equipmentFromDB.getSerial());
		assertEquals(equipmentStatusEnabled, equipmentFromDB.getStatus());
		assertEquals(testEquipmenttype.getTypeCode(), equipmentFromDB.getEquipmenttype().getTypeCode());
		assertEquals(testEquipmenttype.getTypeName(), equipmentFromDB.getEquipmenttype().getTypeName());
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetByType() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 6666;
		int equipmentTypeTypeCode2 = 7777;
		int equipmentType1Count = 5;
		int equipmentType2Count = 3;
		String equipmentName1 = "TestEquipment5_";
		String equipmentName2 = "TestEquipment6_";
		String equipmentSerial1 = "TestSerial5_";
		String equipmentSerial2 = "TestSerial6_";
		String equipmentTypeName1 = "TestType6";
		String equipmentTypeName2 = "TestType7";
		
		Equipmenttype testEquipmenttype1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeName1);
		Equipmenttype testEquipmenttype2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeName2);
		etdao.persist(testEquipmenttype1);
		etdao.persist(testEquipmenttype2);
		
		for (int i=0; i<equipmentType1Count; i++) {
			Equipment testEquipment = new Equipment(equipmentName1 + i, equipmentSerial1 + i, equipmentStatusEnabled, testEquipmenttype1);
			edao.persist(testEquipment);
		}
		
		for (int i=0; i<equipmentType2Count; i++) {
			Equipment testEquipment = new Equipment(equipmentName2 + i, equipmentSerial2 + i, equipmentStatusEnabled, testEquipmenttype2);
			edao.persist(testEquipment);
		}
		
		List<Equipment> equipmentOfType1 = edao.getByTypeCode(equipmentTypeTypeCode1);
		List<Equipment> equipmentOfType2 = edao.getByTypeCode(equipmentTypeTypeCode2);
		
		assertEquals(equipmentType1Count, equipmentOfType1.size());
		assertEquals(equipmentType2Count, equipmentOfType2.size());
		
		for (Equipment equipment: equipmentOfType1) {
			assertEquals(testEquipmenttype1, equipment.getEquipmenttype());
		}
		for (Equipment equipment: equipmentOfType2) {
			assertEquals(testEquipmenttype2, equipment.getEquipmenttype());
		}
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEnabledByType() {
		int equipmentStatusDisabled = 0;
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 8888;
		String equipmentName1 = "TestEquipment7";
		String equipmentName2 = "TestEquipment8";
		String equipmentName3 = "TestEquipment9";
		String equipmentSerial1 = "TestSerial5";
		String equipmentSerial2 = "TestSerial6";
		String equipmentSerial3 = "TestSerial7";
		String equipmentTypeName = "TestType6";

		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);

		// No equipment of type
		assertEquals(0, edao.getEnabledByTypeCode(equipmentTypeTypeCode).size());
		
		// Add first (enabled) equipment of type
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
		Equipment testEquipment2 = new Equipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype);
		Equipment testEquipment3 = new Equipment(equipmentName3, equipmentSerial3, equipmentStatusDisabled, testEquipmenttype);
		
		edao.persist(testEquipment1);
		assertEquals(1, edao.getEnabledByTypeCode(equipmentTypeTypeCode).size());
		assertEquals(equipmentName1, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(0).getName());
		assertEquals(equipmentSerial1, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(0).getSerial());
		assertEquals(equipmentStatusEnabled, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(0).getStatus());
		
		// Add second (enabled) equipment of type
		edao.persist(testEquipment2);
		assertEquals(2, edao.getEnabledByTypeCode(equipmentTypeTypeCode).size());
		assertEquals(equipmentName2, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(1).getName());
		assertEquals(equipmentSerial2, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(1).getSerial());
		assertEquals(equipmentStatusEnabled, edao.getEnabledByTypeCode(equipmentTypeTypeCode).get(1).getStatus());
		
		// Add third (disabled) equipment of type
		edao.persist(testEquipment3);
		assertEquals(2, edao.getEnabledByTypeCode(equipmentTypeTypeCode).size());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetOrderedByTypeName() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode1 = 1111;
		int equipmentTypeTypeCode2 = 2222;
		int equipmentTypeTypeCode3 = 3333;
		String equipmentName1 = "TestEquipment7";
		String equipmentName2 = "TestEquipment8";
		String equipmentName3 = "TestEquipment9";
		String equipmentSerial1 = "TestSerial5";
		String equipmentSerial2 = "TestSerial6";
		String equipmentSerial3 = "TestSerial7";
		String equipmentTypeName1 = "CTestType";
		String equipmentTypeName2 = "BTestType";
		String equipmentTypeName3 = "ATestType";

		Equipmenttype testEquipmenttype1 = new Equipmenttype(equipmentTypeTypeCode1, equipmentTypeName1);
		Equipmenttype testEquipmenttype2 = new Equipmenttype(equipmentTypeTypeCode2, equipmentTypeName2);
		Equipmenttype testEquipmenttype3 = new Equipmenttype(equipmentTypeTypeCode3, equipmentTypeName3);
		Equipment testEquipment1 = new Equipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);
		Equipment testEquipment2 = new Equipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype2);
		Equipment testEquipment3 = new Equipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmenttype3);
		
		etdao.persist(testEquipmenttype1);
		etdao.persist(testEquipmenttype2);
		etdao.persist(testEquipmenttype3);
		edao.persist(testEquipment1);
		edao.persist(testEquipment2);
		edao.persist(testEquipment3);
		
		List<Equipment> equipmentList = edao.getOrderedByTypeName();
		
		assertEquals(testEquipment3, equipmentList.get(0));
		assertEquals(testEquipment2, equipmentList.get(1));
		assertEquals(testEquipment1, equipmentList.get(2));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testSerialExists_true() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 1111;
		String equipmentName = "TestEquipment1";
		String equipmentSerial = "TestSerial1";
		String equipmentTypeName = "TestType!";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmenttype);
		
		etdao.persist(testEquipmenttype);
		edao.persist(testEquipment);
		
		assertTrue(edao.serialExists(equipmentSerial));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testSerialExists_false() {
		String noSuchSerial = "foobar";
		assertFalse(edao.serialExists(noSuchSerial));
	}

	public void emptyTables() {
		List<Equipment> equipments = edao.getAll();
		for (Equipment currentEquipment : equipments) {
			edao.initialize(edao.getEquipmentIdBySerial(currentEquipment.getSerial()));
			edao.delete();
		}

		List<Equipmenttype> equipmentTypes = etdao.getAll();
		for (Equipmenttype currentEquipmenttype : equipmentTypes) {
			etdao.initialize(currentEquipmenttype.getEquipmentTypeId());
			etdao.delete();
		}
	}
}
