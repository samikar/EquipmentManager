package model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class EmployeeDaoTest {
	@Autowired
	private EmployeeDao empdao;
	
    @Before
    public void init() {
    	empdao = new EmployeeDao();
        empdao.initTest();
        truncateTable();
    }

    @After
    public void destroy() {
    	truncateTable();
        empdao.destroy();
    }
	
    @Test
    @Transactional
    @Rollback(true)
	public void addEmployeeTest() {
    	int i = 0;
		String id = "";
		Employee employeeToAdd = new Employee();

		employeeToAdd.setName("Unit Test1");
		employeeToAdd.setEmployeeId("000000001");
		
		empdao.persist(employeeToAdd);
		List<Employee> employees = empdao.getAll();
		assertEquals(employeeToAdd.getEmployeeId(), employees.get(employees.size()-1).getEmployeeId());
		assertEquals(employeeToAdd.getName(), employees.get(employees.size()-1).getName());
	}
    
    @Test
    @Transactional
    @Rollback(true)
	public void addAndDeleteEmployeeTest() {
    	Employee employeeToDelete = new Employee();
    	
    	
    	employeeToDelete.setName("Unit Test2");
    	employeeToDelete.setEmployeeId("000000002");
		empdao.persist(employeeToDelete);
		List<Employee> employees = empdao.getAll();
    	
    	// Employee exists in table 
		assertEquals(1, employees.size());
    	
    	employeeToDelete = employees.get(0);
    	empdao.initialize(employeeToDelete.getEmployeeKey());
    	empdao.delete();
    	
    	employees = empdao.getAll();
    	// Employees table empty
    	assertEquals(0, employees.size());
    }
    
    public void truncateTable() {
    	List<Employee> employees = empdao.getAll();
    	for (Employee currentEmployee : employees) {
    		empdao.initialize(currentEmployee.getEmployeeKey());
    		empdao.delete();
    	}
    }
}
