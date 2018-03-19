package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EquipmentDaoTest {
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
	public void testAddEquipment() {
		Equipment testEquipment = new Equipment();
		Equipmenttype testEquipmenttype = new Equipmenttype(1111, "TestType1");
		testEquipment.setName("TestEquipment1");
		testEquipment.setSerial("TestSerial1");
		testEquipment.setStatus(1);
		testEquipment.setEquipmenttype(testEquipmenttype);
		etdao.persist(testEquipmenttype);
		testEquipment.setEquipmentId(edao.persist(testEquipment));
		edao.initialize(testEquipment.getEquipmentId());
		assertEquals(testEquipment, edao.getDao());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetAll() {
		List<Equipment> equipments = edao.getAll();
		assertEquals(0, equipments.size());
		
		Equipmenttype et = new Equipmenttype(2222, "TestType2");
		etdao.persist(et);
		for (int i=0; i<5; i++) {
			Equipment equipmentToAdd = new Equipment();
			equipmentToAdd.setName("TestEquipment" + i);
			equipmentToAdd.setSerial("TestSerial" + i);
			equipmentToAdd.setEquipmenttype(et);
			equipmentToAdd.setStatus(1);
			edao.persist(equipmentToAdd);
		}
		equipments = edao.getAll();
		assertEquals(5, equipments.size());
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentIdBySerial_Found() {
		Equipmenttype et = new Equipmenttype(3333, "TestType3");
		etdao.persist(et);
		
		Equipment equipmentToAdd = new Equipment();
		equipmentToAdd.setName("TestEquipment3");
		equipmentToAdd.setSerial("TestSerial3");
		equipmentToAdd.setEquipmenttype(et);
		equipmentToAdd.setStatus(1);
		int equipmentId = edao.persist(equipmentToAdd);
		
		assertEquals(equipmentId, edao.getEquipmentIdBySerial("TestSerial3"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentIdBySerial_NotFound() {
		assertEquals(0, edao.getEquipmentIdBySerial("TestSerial4"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetBySerial() {
		Equipmenttype et = new Equipmenttype(4444, "TestType5");
		etdao.persist(et);
		
		Equipment equipmentToAdd = new Equipment();
		equipmentToAdd.setName("TestEquipment5");
		equipmentToAdd.setSerial("TestSerial5");
		equipmentToAdd.setEquipmenttype(et);
		equipmentToAdd.setStatus(1);
		edao.persist(equipmentToAdd);
		equipmentToAdd.setEquipmentId(edao.persist(equipmentToAdd));
		Equipment equipmentFromDB = edao.getBySerial("TestSerial5");

		assertEquals(equipmentToAdd, equipmentFromDB);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetByType() {
		Equipmenttype et1 = new Equipmenttype(6666, "TestType6");
		Equipmenttype et2 = new Equipmenttype(7777, "TestType7");
		etdao.persist(et1);
		etdao.persist(et2);
		
		for (int i=0; i<5; i++) {
			Equipment equipmentToAdd1 = new Equipment();
			equipmentToAdd1.setName("TestEquipment6_" + i);
			equipmentToAdd1.setSerial("TestSerial6_" + i);
			equipmentToAdd1.setEquipmenttype(et1);
			equipmentToAdd1.setStatus(1);
			edao.persist(equipmentToAdd1);
		}
		
		for (int i=0; i<3; i++) {
			Equipment equipmentToAdd1 = new Equipment();
			equipmentToAdd1.setName("TestEquipment7_" + i);
			equipmentToAdd1.setSerial("TestSerial7_" + i);
			equipmentToAdd1.setEquipmenttype(et2);
			equipmentToAdd1.setStatus(1);
			edao.persist(equipmentToAdd1);
		}
		
		List<Equipment> equipmentOfType1 = edao.getByTypeCode(6666);
		List<Equipment> equipmentOfType2 = edao.getByTypeCode(7777);
		
		assertEquals(5, equipmentOfType1.size());
		assertEquals(3, equipmentOfType2.size());
		
		for (Equipment equipment: equipmentOfType1) {
			assertEquals(et1, equipment.getEquipmenttype());
		}
		for (Equipment equipment: equipmentOfType2) {
			assertEquals(et2, equipment.getEquipmenttype());
		}
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEnabledByType() {
		//TODO: To be continued...
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
