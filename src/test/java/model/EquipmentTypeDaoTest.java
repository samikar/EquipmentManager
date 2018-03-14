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
		
	}
}
