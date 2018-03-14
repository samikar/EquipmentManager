package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


//@ContextConfiguration(locations = "classpath:application-context-test.xml\"")
@ContextConfiguration(locations = "classpath:application-context-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeDaoTest {
	
	@Autowired
	private EmployeeDao empdao;
	
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
	public void addEmployeeTest() {		
		Employee testEmployee = new Employee();
		testEmployee.setEmployeeId("123456789");
		testEmployee.setName("Unit Test");		
		empdao.persist(testEmployee);
		List<Employee> employees = empdao.getAll();
		assertEquals(testEmployee.getEmployeeId(), employees.get(0).getEmployeeId());
		assertEquals(testEmployee.getName(), employees.get(0).getName());
	}
}
