package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EquipmentDaoTest {
	@Autowired
	private EquipmenttypeDao etdao;
	private EquipmentDao edao;

	@Before
	public void init() {
		edao = new EquipmentDao();
		etdao = new EquipmenttypeDao();
		edao.initTest();
		etdao.initTest();
		emptyTables();
	}

	@After
	public void destroy() {
		emptyTables();
		edao.destroy();
		etdao.destroy();
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetAll() {
		List<Equipment> equipments = edao.getAll();
		assertEquals(0, equipments.size());
		
		Equipmenttype et = new Equipmenttype(1111, "UnitTestType1");
		etdao.persist(et);
		for (int i=0; i<5; i++) {
			Equipment equipmentToAdd = new Equipment();
			equipmentToAdd.setName("UnitTestEquipment" + i);
			equipmentToAdd.setSerial("UnitTestSerial" + i);
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
		Equipmenttype et = new Equipmenttype(2222, "UnitTestType2");
		etdao.persist(et);
		
		Equipment equipmentToAdd = new Equipment();
		equipmentToAdd.setName("Unittest2");
		equipmentToAdd.setSerial("UnitTestSerial2");
		equipmentToAdd.setEquipmenttype(et);
		equipmentToAdd.setStatus(1);
		int equipmentId = edao.persist(equipmentToAdd);
		
		assertEquals(equipmentId, edao.getEquipmentIdBySerial("UnitTestSerial2"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetEquipmentIdBySerial_NotFound() {
		assertEquals(0, edao.getEquipmentIdBySerial("UnitTestSerial3"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetBySerial() {
		Equipmenttype et = new Equipmenttype(4444, "UnitTestType4");
		etdao.persist(et);
		
		Equipment equipmentToAdd = new Equipment();
		equipmentToAdd.setName("Unittest4");
		equipmentToAdd.setSerial("UnitTestSerial4");
		equipmentToAdd.setEquipmenttype(et);
		equipmentToAdd.setStatus(1);
		edao.persist(equipmentToAdd);
		equipmentToAdd.setEquipmentId(edao.persist(equipmentToAdd));
		Equipment equipmentFromDB = edao.getBySerial("UnitTestSerial4");

		assertEquals(equipmentToAdd, equipmentFromDB);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testGetByType() {
		Equipmenttype et1 = new Equipmenttype(5555, "UnitTestType5");
		Equipmenttype et2 = new Equipmenttype(6666, "UnitTestType6");
		etdao.persist(et1);
		etdao.persist(et2);
		
		for (int i=0; i<5; i++) {
			Equipment equipmentToAdd1 = new Equipment();
			equipmentToAdd1.setName("UnitTest5_" + i);
			equipmentToAdd1.setSerial("UnitTestSerial5_" + i);
			equipmentToAdd1.setEquipmenttype(et1);
			equipmentToAdd1.setStatus(1);
			edao.persist(equipmentToAdd1);
		}
		
		for (int i=0; i<3; i++) {
			Equipment equipmentToAdd1 = new Equipment();
			equipmentToAdd1.setName("UnitTest6_" + i);
			equipmentToAdd1.setSerial("UnitTestSerial6_" + i);
			equipmentToAdd1.setEquipmenttype(et2);
			equipmentToAdd1.setStatus(1);
			edao.persist(equipmentToAdd1);
		}
		
		List<Equipment> equipmentOfType1 = edao.getByTypeCode(5555);
		List<Equipment> equipmentOfType2 = edao.getByTypeCode(6666);
		
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
