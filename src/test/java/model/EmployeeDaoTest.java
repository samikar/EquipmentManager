package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import utils.PropertyUtils;

public class EmployeeDaoTest {
	private static Properties properties = PropertyUtils.loadProperties();
	@Autowired
	private static EmployeeDao empdao;
	
    @BeforeClass
    public static void init() {
    	empdao = new EmployeeDao();
    	empdao.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
        empdao.init();
    }

    @AfterClass
    public static void destroy() {
        empdao.destroy();
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
	public void testAddEmployee() {
    	String employeeId = "000000001";
    	String employeeName = "Unit Test1";
    	Employee testEmployee = new Employee(employeeId, employeeName);

		empdao.persist(testEmployee);
		empdao.initialize(testEmployee.getEmployeeKey());
		Employee DBemployee = empdao.getDao();

		assertEquals(employeeId, DBemployee.getEmployeeId());
		assertEquals(employeeName, DBemployee.getName());
				
	}
    
    @Test
    @Transactional
    @Rollback(true)
	public void testAddAndDeleteEmployeeTest() {
		String employeeName = "Unit Test2";
		String employeeId = "000000002";
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	
		empdao.persist(testEmployee);
		List<Employee> employees = empdao.getAll();
    	
    	// Employee exists in table 
		assertEquals(1, employees.size());
    	
		testEmployee = employees.get(0);
    	empdao.initialize(testEmployee.getEmployeeKey());
    	empdao.delete();
    	
    	employees = empdao.getAll();
    	// Employees table empty
    	assertEquals(0, employees.size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
	public void testGetEmployeeByEmployeeId() {
		String employeeName = "Unit Test3";
		String employeeId = "000000003";
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	
		empdao.persist(testEmployee);
		Employee DBemployee = empdao.getEmployeeByEmployeeId(testEmployee.getEmployeeId());
		assertEquals(employeeName, DBemployee.getName());
		assertEquals(employeeId, DBemployee.getEmployeeId());
    }
    
    @Test
    @Transactional
    @Rollback(true)
	public void testGetEmployeeKeyByEmployeeId() {
		String employeeName = "Unit Test4";
		String employeeId = "000000004";
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	
		empdao.persist(testEmployee);
		int employeeKey = empdao.getEmployeeKeyByEmployeeId(testEmployee.getEmployeeId());
		
		assertEquals(employeeKey, testEmployee.getEmployeeKey());
    }
    
    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeExistsTrue() {
    	String vesaLaisiID = "00186763";
		assertTrue(empdao.employeeExists(vesaLaisiID));
    }
    
    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeExistsFalse() {
		assertFalse(empdao.employeeExists("foobar"));
    }
    
    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInDBTrue() {
		String employeeName = "Unit Test5";
		String employeeId = "000000005";
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	
		empdao.persist(testEmployee);
				
		assertTrue(empdao.employeeInDB(testEmployee.getEmployeeId()));
    }
    
    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInDBFalse() {				
		assertFalse(empdao.employeeInDB("foobar"));
    }
    
    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInADTrue() {				
    	String vesaLaisiID = "00186763";
    	assertFalse(empdao.employeeInDB(vesaLaisiID));
		assertTrue(empdao.employeeInAD(vesaLaisiID));
		assertTrue(empdao.employeeInDB(vesaLaisiID));		
    }

    @Ignore
    @Test
    @Transactional
    @Rollback(true)
	public void employeeInADFalse() {				
    	assertFalse(empdao.employeeInDB("foobar"));		
    }
    
    public void emptyTables() {
    	List<Employee> employees = empdao.getAll();
    	for (Employee currentEmployee : employees) {
    		empdao.initialize(currentEmployee.getEmployeeKey());
    		empdao.delete();
    	}
    }
}
