package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EmployeeDaoTest {
	
	private EmployeeDao empdao = new EmployeeDao();
	
    @Before
    public void init() {
        empdao.init();
    }

    @After
    public void destroy() {
        empdao.destroy();
    }
	
	@Test
	@Transactional
	@Rollback(true)
	public void testAddEmployee() {		
		Employee testEmployee = new Employee();
		testEmployee.setEmployeeId("123456789");
		testEmployee.setName("Unit Test");		
		empdao.persist(testEmployee);
		List<Employee> employees = empdao.getAll();
		assertEquals(testEmployee.getEmployeeId(), employees.get(0).getEmployeeId());
		assertEquals(testEmployee.getName(), employees.get(0).getName());
	}
}
