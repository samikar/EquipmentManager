package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EquipmentTypeDaoTest {
	private EquipmenttypeDao etdao = new EquipmenttypeDao(); 

	@Ignore
	@Test
	@Transactional
	@Rollback(true)
	public void testAddEmployee() {
		Equipmenttype testEmployee = new Equipmenttype();
		testEmployee.setTypeCode(9999);
		testEmployee.setTypeName("Test type");
		
		etdao.init();
		etdao.persist(testEmployee);
		List<Equipmenttype> employees = etdao.getAll();
		assertEquals(testEmployee.getTypeCode(), employees.get(0).getTypeCode());
		assertEquals(testEmployee.getTypeName(), employees.get(0).getTypeName());
		etdao.destroy();
		
	}
}
