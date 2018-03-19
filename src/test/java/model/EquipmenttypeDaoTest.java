package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
		Equipmenttype equipmentTypeToAdd = new Equipmenttype();

		equipmentTypeToAdd.setTypeName("Unit Test1");
		equipmentTypeToAdd.setTypeCode(1111);

		etdao.persist(equipmentTypeToAdd);
		List<Equipmenttype> equipmentTypes = etdao.getAll();
		assertEquals(equipmentTypeToAdd.getTypeName(), equipmentTypes.get(equipmentTypes.size() - 1).getTypeName());
		assertEquals(equipmentTypeToAdd.getTypeCode(), equipmentTypes.get(equipmentTypes.size() - 1).getTypeCode());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testAddAndDeleteequipmentTypeTest() {
		Equipmenttype equipmentTypeToDelete = new Equipmenttype();

		equipmentTypeToDelete.setTypeName("Unit Test2");
		equipmentTypeToDelete.setTypeCode(2222);
		etdao.persist(equipmentTypeToDelete);
		List<Equipmenttype> equipmentTypes = etdao.getAll();

		// equipmentType exists in table
		assertEquals(1, equipmentTypes.size());

		equipmentTypeToDelete = equipmentTypes.get(0);
		etdao.initialize(equipmentTypeToDelete.getEquipmentTypeId());
		etdao.delete();

		equipmentTypes = etdao.getAll();
		// equipmentTypes table empty
		assertEquals(0, equipmentTypes.size());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmenttypeIdByTypeCode_Found() {
		Equipmenttype equipmenttypeToSearch = new Equipmenttype();

		equipmenttypeToSearch.setTypeName("Unit Test3");
		equipmenttypeToSearch.setTypeCode(3333);
		etdao.persist(equipmenttypeToSearch);
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmenttypeToSearch.getTypeCode()));
		Equipmenttype equipmentTypeFound = etdao.getDao();

		assertEquals(equipmenttypeToSearch.getTypeName(), equipmentTypeFound.getTypeName());
		assertEquals(equipmenttypeToSearch.getTypeCode(), equipmentTypeFound.getTypeCode());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmenttypeIdByTypeCode_NotFound() {
		Equipmenttype equipmenttypeToSearch = new Equipmenttype();

		equipmenttypeToSearch.setTypeName("Unit Test4");
		equipmenttypeToSearch.setTypeCode(4444);
		etdao.persist(equipmenttypeToSearch);
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(equipmenttypeToSearch.getTypeCode()));
		Equipmenttype equipmentTypeFound = etdao.getDao();

		assertEquals(equipmenttypeToSearch.getTypeName(), equipmentTypeFound.getTypeName());
		assertEquals(equipmenttypeToSearch.getTypeCode(), equipmentTypeFound.getTypeCode());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTypeCodeExists_True() {
		Equipmenttype equipmenttypeToSearch = new Equipmenttype();

		equipmenttypeToSearch.setTypeName("Unit Test5");
		equipmenttypeToSearch.setTypeCode(5555);
		etdao.persist(equipmenttypeToSearch);
		assertTrue(etdao.typeCodeExists(equipmenttypeToSearch.getTypeCode()));
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
		// Insert 4 Equipmenttypes
		for (int i = 6; i < 10; i++) {
			Equipmenttype equipmentTypeToAdd = new Equipmenttype();
			equipmentTypeToAdd.setTypeName("EquipmentType" + i);
			equipmentTypeToAdd.setTypeCode(i * 1111);
			etdao.persist(equipmentTypeToAdd);
		}

		// Insert 2 Equipment
		for (int i = 6; i < 8; i++) {
			Equipment equipmentToAdd = new Equipment();
			equipmentToAdd.setName("Equipment" + (i - 5));
			equipmentToAdd.setSerial("MI_" + (i - 5));
			equipmentToAdd.setStatus(1);
			etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(i * 1111));
			equipmentToAdd.setEquipmenttype(etdao.getDao());
			edao.persist(equipmentToAdd);
		}

		// Database connection needs to be refreshed
		etdao.destroy();
		etdao.initTest();

		List<Equipmenttype> equipmentTypesWithEquipment = etdao.getEquipmentTypesWithEquipment();
		assertEquals(2, equipmentTypesWithEquipment.size());
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(6666));
		assertEquals(etdao.getDao(), equipmentTypesWithEquipment.get(0));
		etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(7777));
		assertEquals(etdao.getDao(), equipmentTypesWithEquipment.get(1));

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
