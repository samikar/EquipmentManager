package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EquipmenttypeDaoTest {
	@Autowired
	private static EquipmenttypeDao etdao;
	private static EquipmentDao edao;

	@BeforeClass
	public static void init() {
		edao = new EquipmentDao();
		etdao = new EquipmenttypeDao();
		edao.initTest();
		etdao.initTest();
	}

	@AfterClass
	public static void destroy() {
		edao.destroy();
		etdao.destroy();
	}
	
	@Before
	public void initTables() {
		emptyTables();
	}
	
	@After
	public void destroyTables() {
		emptyTables();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testAddequipmentType() {
		int equipmentTypeTypeCode = 1111;
		String equipmentTypeName = "TestType1";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		etdao.initialize(testEquipmenttype.getEquipmentTypeId());
		Equipmenttype DBequipmenttype = etdao.getDao();
		
		assertEquals(equipmentTypeName, DBequipmenttype.getTypeName());
		assertEquals(equipmentTypeTypeCode, DBequipmenttype.getTypeCode());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testAddAndDeleteequipmentTypeTest() {
		int equipmentTypeTypeCode = 2222;
		String equipmentTypeName = "TestType2";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		List<Equipmenttype> equipmentTypes = etdao.getAll();

		// equipmentType exists in table
		assertEquals(1, equipmentTypes.size());

		testEquipmenttype = equipmentTypes.get(0);
		etdao.initialize(testEquipmenttype.getEquipmentTypeId());
		etdao.delete();

		equipmentTypes = etdao.getAll();
		// equipmentTypes table empty
		assertEquals(0, equipmentTypes.size());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmenttypeIdByTypeCode_Found() {
		int equipmentTypeTypeCode = 3333;
		String equipmentTypeName = "TestType3";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);
		etdao.persist(testEquipmenttype);
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmentTypeTypeCode));
		Equipmenttype DBequipmenttype = etdao.getDao();

		assertEquals(equipmentTypeName, DBequipmenttype.getTypeName());
		assertEquals(equipmentTypeTypeCode, DBequipmenttype.getTypeCode());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmenttypeIdByTypeCode_NotFound() {
		int equipmentTypeTypeCode = 4444;
		int nonExistentTypeCode = 4455;
		String equipmentTypeName = "TestType3";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);	
		etdao.persist(testEquipmenttype);
	
		assertEquals(0, etdao.getEquipmentTypeIdByTypeCode(nonExistentTypeCode));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTypeCodeExists_True() {
		int equipmentTypeTypeCode = 5555;
		String equipmentTypeName = "TestType5";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeName);	
		etdao.persist(testEquipmenttype);
		assertTrue(etdao.typeCodeExists(testEquipmenttype.getTypeCode()));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTypeCodeExists_False() {
		assertFalse(etdao.typeCodeExists(0));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentTypesWithEquipment() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 1111;
		int equipmentCount = 2;
		int equipmentTypeCount = 4; 
		String equipmentName = "TestEquipment6";
		String equipmentSerial = "TestSerial6";
		String equipmentTypeName = "TestType6_";
		
		// Insert 4 Equipmenttypes
		for (int i = 1; i <= equipmentTypeCount; i++) {
			Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode * i, equipmentTypeName + i);
			etdao.persist(testEquipmenttype);
		}

		// No Equipmenttypes with equipment found
		List<Equipmenttype> equipmentTypesWithEquipment = etdao.getEquipmentTypesWithEquipment();
		assertEquals(0, equipmentTypesWithEquipment.size());
		
		// Insert 2 Equipment
		for (int i = 1; i <= equipmentCount; i++) {
			etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmentTypeTypeCode * i));
			Equipmenttype testEquipmenttype = etdao.getDao();
			Equipment testEquipment = new Equipment(equipmentName + i, equipmentSerial + i, equipmentStatusEnabled, testEquipmenttype);
			edao.persist(testEquipment);
		}

		// Database connection needs to be refreshed
		etdao.destroy();
		etdao.initTest();

		equipmentTypesWithEquipment = etdao.getEquipmentTypesWithEquipment();
		assertEquals(equipmentCount, equipmentTypesWithEquipment.size());
		
		// Asser that EquipmentTypeCodes match with those which were added 
		int i = 1;
		for (Equipmenttype foundEquipmenttype : equipmentTypesWithEquipment) {
			assertEquals(foundEquipmenttype.getTypeCode(), equipmentTypeTypeCode * i);
			i++;
		}
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
