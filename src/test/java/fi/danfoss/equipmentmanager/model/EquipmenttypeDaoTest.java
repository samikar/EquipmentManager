package fi.danfoss.equipmentmanager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.danfoss.equipmentmanager.utils.PropertyUtils;

public class EquipmenttypeDaoTest {
	
	@Autowired
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	
    @BeforeClass
    public static void init() {
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
        
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
	public void testAddequipmentType() {
		int equipmentTypeTypeCode = 1111;
		String equipmentTypeTypeName = "TestType1";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);
		etdao.persist(testEquipmenttype);
		etdao.initialize(testEquipmenttype.getEquipmentTypeId());
		Equipmenttype DBequipmenttype = etdao.getDao();
		
		assertEquals(equipmentTypeTypeName, DBequipmenttype.getTypeName());
		assertEquals(equipmentTypeTypeCode, DBequipmenttype.getTypeCode());
	}

	@Test
	public void testAddAndDeleteequipmentTypeTest() {
		int equipmentTypeTypeCode = 2222;
		String equipmentTypeTypeName = "TestType2";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);
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
	public void testGetEquipmenttypeIdByTypeCode_Found() {
		int equipmentTypeTypeCode = 3333;
		String equipmentTypeTypeName = "TestType3";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);
		etdao.persist(testEquipmenttype);
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmentTypeTypeCode));
		Equipmenttype DBequipmenttype = etdao.getDao();

		assertEquals(equipmentTypeTypeName, DBequipmenttype.getTypeName());
		assertEquals(equipmentTypeTypeCode, DBequipmenttype.getTypeCode());
	}

	@Test
	public void testGetEquipmenttypeIdByTypeCode_NotFound() {
		int equipmentTypeTypeCode = 4444;
		int nonExistentTypeCode = 4455;
		String equipmentTypeTypeName = "TestType3";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);	
		etdao.persist(testEquipmenttype);
	
		assertEquals(0, etdao.getEquipmentTypeIdByTypeCode(nonExistentTypeCode));
	}

	@Test
	public void testGetByTypeCode_Found() {
		int equipmentTypeTypeCode = 1111;
		String equipmentTypeTypeName = "TestType3";
		Equipmenttype DBequipmenttype = null;
		Equipmenttype testEquipmenttype = null;
		
		testEquipmenttype= new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);	
		etdao.persist(testEquipmenttype);
		DBequipmenttype = etdao.getByTypeCode(equipmentTypeTypeCode);
		
		assertEquals(equipmentTypeTypeCode, DBequipmenttype.getTypeCode());
		assertEquals(equipmentTypeTypeName, DBequipmenttype.getTypeName());
	}
	
	@Test
	public void testGetByTypeCode_NotFound() {
		int equipmentTypeTypeCode = 1111;
		Equipmenttype DBequipmenttype = null;
		
		DBequipmenttype = etdao.getByTypeCode(equipmentTypeTypeCode);
		
		assertNull(DBequipmenttype);
	}
	
	@Test
	public void testTypeCodeExists_True() {
		int equipmentTypeTypeCode = 5555;
		String equipmentTypeTypeName = "TestType5";
		
		Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode, equipmentTypeTypeName);	
		etdao.persist(testEquipmenttype);
		assertTrue(etdao.typeCodeExists(testEquipmenttype.getTypeCode()));
	}

	@Test
	public void testTypeCodeExists_False() {
		assertFalse(etdao.typeCodeExists(0));
	}

	@Test
	public void testGetEquipmentTypesWithEquipment() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeTypeCode = 1111;
		int equipmentCount = 2;
		int equipmentTypeCount = 4; 
		String equipmentName = "TestEquipment6";
		String equipmentSerial = "TestSerial6";
		String equipmentTypeTypeName = "TestType6_";
		
		// Insert 4 Equipmenttypes
		for (int i = 1; i <= equipmentTypeCount; i++) {
			Equipmenttype testEquipmenttype = new Equipmenttype(equipmentTypeTypeCode * i, equipmentTypeTypeName + i);
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
		etdao.refresh();

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