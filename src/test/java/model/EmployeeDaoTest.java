package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
	public void testAddEmployee() {
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
	public void testAddAndDeleteEmployeeTest() {
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
    
    @Test
    @Transactional
    @Rollback(true)
	public void testGetEmployeeByEmployeeId() {
    	Employee employeeToSearch = new Employee();
    	
    	employeeToSearch.setName("Unit Test3");
    	employeeToSearch.setEmployeeId("000000003");
		empdao.persist(employeeToSearch);
		Employee foundEmployee = empdao.getEmployeeByEmployeeId(employeeToSearch.getEmployeeId());
		assertEquals(employeeToSearch.getName(), foundEmployee.getName());
		assertEquals(employeeToSearch.getEmployeeId(), foundEmployee.getEmployeeId());
    }
    
    @Test
    @Transactional
    @Rollback(true)
	public void testGetEmployeeKeyByEmployeeId() {
    	Employee employeeToSearch = new Employee();
    	
    	employeeToSearch.setName("Unit Test4");
    	employeeToSearch.setEmployeeId("000000004");
		empdao.persist(employeeToSearch);
		int employeeKey = empdao.getEmployeeKeyByEmployeeId(employeeToSearch.getEmployeeId());
		
		assertEquals(employeeToSearch.getEmployeeKey(), employeeKey);
    }
    
//    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeExistsTrue() {
    	String vesaLaisiID = "00186763";
		assertTrue(empdao.employeeExists(vesaLaisiID));
    }
    
//    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeExistsFalse() {
		assertFalse(empdao.employeeExists("foobar"));
    }
    
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInDBTrue() {
    	Employee employeeToSearch = new Employee();
    	
    	employeeToSearch.setName("Unit Test5");
    	employeeToSearch.setEmployeeId("000000005");
		empdao.persist(employeeToSearch);
				
		assertTrue(empdao.employeeInDB(employeeToSearch.getEmployeeId()));
    }
    
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInDBFalse() {				
		assertFalse(empdao.employeeInDB("foobar"));
    }
    
//    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInADTrue() {				
    	String vesaLaisiID = "00186763";
    	assertFalse(empdao.employeeInDB(vesaLaisiID));
		assertTrue(empdao.employeeInAD(vesaLaisiID));
		assertTrue(empdao.employeeInDB(vesaLaisiID));		
    }

//    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInADFalse() {				
    	assertFalse(empdao.employeeInDB("foobar"));		
    }
    
    public void truncateTable() {
    	List<Employee> employees = empdao.getAll();
    	for (Employee currentEmployee : employees) {
    		empdao.initialize(currentEmployee.getEmployeeKey());
    		empdao.delete();
    	}
    }
}
