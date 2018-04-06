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

import db.DatabaseUtil;
import utils.PropertyUtils;

public class EmployeeDaoTest {
	private static Properties properties = PropertyUtils.loadProperties();
	
	@Autowired
	private static String testDBurl;
	private static String testDBuser;
	private static String testDBpassword;
	private static String testDBdriver;
	private static EmployeeDao empdao;
	
    @BeforeClass
    public static void init() {
    	testDBurl = properties.getProperty("testDBurl");
    	testDBuser = properties.getProperty("testDBuser");
    	testDBpassword = properties.getProperty("testDBpassword");
    	testDBdriver = properties.getProperty("testDBdriver");
    	
    	empdao = new EmployeeDao();
    	
    	empdao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
        
    	empdao.init();
    }

    @AfterClass
    public static void destroy() {
        empdao.destroy();
        DatabaseUtil.shutdown();
    }
    
	@Before
	public void initTest() {
		emptyTables();
	}
	
	@After
	public void endTest() {
		emptyTables();
	}
	
    @Test
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
	public void employeeExistsTrue() {
    	String vesaLaisiID = "00186763";
		assertTrue(empdao.employeeExists(vesaLaisiID));
    }
    
    @Ignore
    @Test
	public void employeeExistsFalse() {
		assertFalse(empdao.employeeExists("foobar"));
    }
    
    @Ignore
    @Test
	public void employeeInDBTrue() {
		String employeeName = "Unit Test5";
		String employeeId = "000000005";
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	
		empdao.persist(testEmployee);
				
		assertTrue(empdao.employeeInDB(testEmployee.getEmployeeId()));
    }
    
    @Ignore
    @Test
	public void employeeInDBFalse() {				
		assertFalse(empdao.employeeInDB("foobar"));
    }
    
    @Ignore
    @Test
	public void employeeInADTrue() {				
    	String vesaLaisiID = "00186763";
    	assertFalse(empdao.employeeInDB(vesaLaisiID));
		assertTrue(empdao.employeeInAD(vesaLaisiID));
		assertTrue(empdao.employeeInDB(vesaLaisiID));		
    }

    @Ignore
    @Test
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
