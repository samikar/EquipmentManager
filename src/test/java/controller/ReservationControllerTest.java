package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.ws.Response;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import model.Employee;
import model.EmployeeDao;
import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.Reservation;
import model.ReservationDao;
import utils.PropertyUtils;

public class ReservationControllerTest {
	private static Properties properties = PropertyUtils.loadProperties();
	
	@Autowired
	private static String testDBurl;
	private static String testDBuser;
	private static String testDBpassword;
	private static String testDBdriver;
	private static EmployeeDao empdao;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ReservationDao rdao;
	
    @BeforeClass
    public static void init() {
    	testDBurl = properties.getProperty("testDBurl");
    	testDBuser = properties.getProperty("testDBuser");
    	testDBpassword = properties.getProperty("testDBpassword");
    	testDBdriver = properties.getProperty("testDBdriver");
    	
    	empdao = new EmployeeDao();
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
    	rdao = new ReservationDao();
    	
    	empdao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    	edao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    	etdao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
    	rdao.setProperties(testDBurl, testDBuser, testDBpassword, testDBdriver);
        
    	empdao.init();
        edao.init();
        etdao.init();
        rdao.init();
    }

    @AfterClass
    public static void destroy() {
    	empdao.destroy();
        edao.destroy();
        etdao.destroy();
        rdao.destroy();
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
	public void testGetAllReservations_noReservations() {
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;
		List<Reservation> reservations = controller.getAllReservations();
		assertEquals(0, reservations.size());
	}
	
	@Test
	public void testGetAllReservations_1Reservation1() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;

    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment);
    	
    	List<Reservation> reservations = controller.getAllReservations();
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn.getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    	
    	assertEquals(dateTakeLdt.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation1Take.getMinute());

    	assertEquals(dateReturnLdt.getYear(), DBreservation1Return.getYear());
    	assertEquals(dateReturnLdt.getMonth(), DBreservation1Return.getMonth());
    	assertEquals(dateReturnLdt.getDayOfMonth(), DBreservation1Return.getDayOfMonth());
    	assertEquals(dateReturnLdt.getHour(), DBreservation1Return.getHour());
    	assertEquals(dateReturnLdt.getMinute(), DBreservation1Return.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(employeeId1, DBreservation1.getEmployee().getEmployeeId());
    	assertEquals(employeeName1, DBreservation1.getEmployee().getName());
    	assertEquals(equipmentSerial1, DBreservation1.getEquipment().getSerial());
    	assertEquals(equipmentName1, DBreservation1.getEquipment().getName());
    	assertEquals(equipmentStatusEnabled, DBreservation1.getEquipment().getStatus());
    	assertEquals(equipmentTypeCode, DBreservation1.getEquipment().getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, DBreservation1.getEquipment().getEquipmenttype().getTypeName());
	}
	
	@Test
	public void testGetAllReservations_3Reservations() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake1 = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(currentDateTime.minusMonths(4).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake3 = Date.from(currentDateTime.minusMonths(1).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn2 = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeId2 = "222222222";
    	String employeeId3 = "333333333";
    	String employeeName1 = "Test Employee1";
    	String employeeName2 = "Test Employee2";
    	String employeeName3 = "Test Employee3";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentName3 = "Test Equipment3";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentSerial3 = "TestSerial3";
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;
    	
    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Employee testEmployee2 = addEmployee(employeeId2, employeeName2);
    	Employee testEmployee3 = addEmployee(employeeId3, employeeName3);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmenttype2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype1);
    	Equipment testEquipment3 = addEquipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmenttype2);
    	addReservation(reservationType, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationType, dateTake2, dateReturn2, testEmployee2, testEquipment2);
    	addReservation(reservationType, dateTake3, null, testEmployee3, testEquipment3);
    	
    	List<Reservation> reservations = controller.getAllReservations();
    	assertEquals(3, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	Reservation DBreservation2 = reservations.get(1);
    	Reservation DBreservation3 = reservations.get(2);
    	
    	LocalDateTime dateTakeLdt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake1.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateTakeLdt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake2.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateTakeLdt3 = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake3.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn1.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn2.getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation2Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation2.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation3Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation3.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation2Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation2.getDateReturn().getTime()), ZoneId.systemDefault());
    	
    	assertEquals(dateTakeLdt1.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt2.getYear(), DBreservation2Take.getYear());
    	assertEquals(dateTakeLdt3.getYear(), DBreservation3Take.getYear());
    	assertEquals(dateTakeLdt1.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt2.getMonth(), DBreservation2Take.getMonth());
    	assertEquals(dateTakeLdt3.getMonth(), DBreservation3Take.getMonth());
    	assertEquals(dateTakeLdt1.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt2.getDayOfMonth(), DBreservation2Take.getDayOfMonth());
    	assertEquals(dateTakeLdt3.getDayOfMonth(), DBreservation3Take.getDayOfMonth());
    	assertEquals(dateTakeLdt1.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt2.getHour(), DBreservation2Take.getHour());
    	assertEquals(dateTakeLdt3.getHour(), DBreservation3Take.getHour());
    	assertEquals(dateTakeLdt1.getMinute(), DBreservation1Take.getMinute());
    	assertEquals(dateTakeLdt2.getMinute(), DBreservation2Take.getMinute());
    	assertEquals(dateTakeLdt3.getMinute(), DBreservation3Take.getMinute());

    	assertEquals(dateReturnLdt1.getYear(), DBreservation1Return.getYear());
    	assertEquals(dateReturnLdt2.getYear(), DBreservation2Return.getYear());
    	assertEquals(dateReturnLdt1.getMonth(), DBreservation1Return.getMonth());
    	assertEquals(dateReturnLdt2.getMonth(), DBreservation2Return.getMonth());
    	assertEquals(dateReturnLdt1.getDayOfMonth(), DBreservation1Return.getDayOfMonth());
    	assertEquals(dateReturnLdt2.getDayOfMonth(), DBreservation2Return.getDayOfMonth());
    	assertEquals(dateReturnLdt1.getHour(), DBreservation1Return.getHour());
    	assertEquals(dateReturnLdt2.getHour(), DBreservation2Return.getHour());
    	assertEquals(dateReturnLdt1.getMinute(), DBreservation1Return.getMinute());
    	assertEquals(dateReturnLdt2.getMinute(), DBreservation2Return.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(reservationType, DBreservation2.getReservationType());
    	assertEquals(reservationType, DBreservation3.getReservationType());
    	assertEquals(employeeId1, DBreservation1.getEmployee().getEmployeeId());
    	assertEquals(employeeId2, DBreservation2.getEmployee().getEmployeeId());
    	assertEquals(employeeId3, DBreservation3.getEmployee().getEmployeeId());
    	assertEquals(employeeName1, DBreservation1.getEmployee().getName());
    	assertEquals(employeeName2, DBreservation2.getEmployee().getName());
    	assertEquals(employeeName3, DBreservation3.getEmployee().getName());
    	assertEquals(equipmentSerial1, DBreservation1.getEquipment().getSerial());
    	assertEquals(equipmentSerial2, DBreservation2.getEquipment().getSerial());
    	assertEquals(equipmentSerial3, DBreservation3.getEquipment().getSerial());
    	assertEquals(equipmentName1, DBreservation1.getEquipment().getName());
    	assertEquals(equipmentName2, DBreservation2.getEquipment().getName());
    	assertEquals(equipmentName3, DBreservation3.getEquipment().getName());
    	assertEquals(equipmentStatusEnabled, DBreservation1.getEquipment().getStatus());
    	assertEquals(equipmentStatusEnabled, DBreservation2.getEquipment().getStatus());
    	assertEquals(equipmentStatusEnabled, DBreservation3.getEquipment().getStatus());
    	assertEquals(equipmentTypeCode1, DBreservation1.getEquipment().getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeCode1, DBreservation2.getEquipment().getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeCode2, DBreservation3.getEquipment().getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, DBreservation1.getEquipment().getEquipmenttype().getTypeName());
    	assertEquals(equipmentTypeName1, DBreservation2.getEquipment().getEquipmenttype().getTypeName());
    	assertEquals(equipmentTypeName2, DBreservation3.getEquipment().getEquipmenttype().getTypeName());
    	
	}
	
	@Test
	public void testTakeEquipment_success() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationType = 0;
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;

    	addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);

    	// Reservation table empty
    	List<Reservation> reservations = controller.getAllReservations();
    	assertEquals(0, reservations.size());
    	// Add one reservation
    	controller.takeEquipment(employeeId1, equipmentSerial1, Integer.toString(reservationType));
    	// One reservation found in Reservation table
    	reservations = controller.getAllReservations();
    	assertEquals(1, reservations.size());
    	 
    	Reservation DBreservation1 = reservations.get(0);
    
    	assertNull(DBreservation1.getDateReturn());
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(employeeId1, DBreservation1.getEmployee().getEmployeeId());
    	assertEquals(employeeName1, DBreservation1.getEmployee().getName());
    	assertEquals(equipmentSerial1, DBreservation1.getEquipment().getSerial());
    	assertEquals(equipmentName1, DBreservation1.getEquipment().getName());
    	assertEquals(equipmentStatusEnabled, DBreservation1.getEquipment().getStatus());
    	assertEquals(equipmentTypeCode, DBreservation1.getEquipment().getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, DBreservation1.getEquipment().getEquipmenttype().getTypeName());
	}
	
	@Test
	public void testTakeEquipment_noEmployeeId() {
    	int reservationType = 0;
    	String equipmentSerial1 = "TestSerial";
    	
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;
   	
    	// Add one reservation
    	boolean thrown = false;
    	String exceptionMessage = "";
    	try {
    		controller.takeEquipment(null, equipmentSerial1, Integer.toString(reservationType));
    	} catch (IllegalArgumentException e) {
    		thrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(thrown);
    	assertEquals("Employee ID must not be empty", exceptionMessage);
	}
	
	@Test
	public void testTakeEquipment_noSerial() {
    	int reservationType = 0;
    	String employeeId1 = "111111111";

		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;

    	// Add one reservation
    	boolean thrown = false;
    	String exceptionMessage = "";
    	try {
    		controller.takeEquipment(employeeId1, null, Integer.toString(reservationType));
    	} catch (IllegalArgumentException e) {
    		thrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(thrown);
    	assertEquals("Serial number must not be empty", exceptionMessage);
	}
	
	@Test
	public void testTakeEquipment_noReservationType() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	
		ReservationController controller = new ReservationController();
		controller.DBurl = testDBurl;
		controller.DBuser = testDBuser;
		controller.DBpassword = testDBpassword;
		controller.DBdriver = testDBdriver;

    	addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	
    	// Add one reservation
    	boolean thrown = false;
    	String exceptionMessage = "";
    	try {
    		controller.takeEquipment(employeeId1, equipmentSerial1, null);
    	} catch (IllegalArgumentException e) {
    		thrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(thrown);
    	assertEquals("Reservation type must be selected", exceptionMessage);
	}
    
    public Employee addEmployee(String employeeId, String employeeName) {
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	empdao.persist(testEmployee);
    	return testEmployee;
    }
    
    public Equipmenttype addEquipmenttype(int equipmentTypeCode, String equipmentTypeName) {
    	Equipmenttype testEquipmentType = new Equipmenttype(equipmentTypeCode, equipmentTypeName);
    	etdao.persist(testEquipmentType);
    	return testEquipmentType;
    }
    
    public Equipment addEquipment(String equipmentName, String equipmentSerial, int equipmentStatus, Equipmenttype equipmentType) {
    	Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatus, equipmentType);
    	edao.persist(testEquipment);
    	return testEquipment;
    }
    
    /**
     * 
     * Returns Reservation from DB
     * @return
     */
    public Reservation addReservation(int reservationType, Date dateTake, Date dateReturn,
    							Employee employee, Equipment equipment) {

    	Reservation testReservation = new Reservation();
   
    	testReservation.setDateTake(dateTake);
    	testReservation.setDateReturn(dateReturn);
    	testReservation.setEmployee(employee);
    	testReservation.setEquipment(equipment);
    	testReservation.setReservationType(reservationType);
    	
    	rdao.initialize(rdao.persist(testReservation));
    	return rdao.getDao();
    }
	
    public void emptyTables() {
	 	empdao.destroy();
        edao.destroy();
        etdao.destroy();
        rdao.destroy();
		empdao.init();
        edao.init();
        etdao.init();
        rdao.init();
    	List<Reservation> reservations = rdao.getAll();
    	for (Reservation currentReservation: reservations) {
    		rdao.initialize(currentReservation.getReservationId());
    		rdao.delete();
    	}
    	
    	List<Employee> employees = empdao.getAll();
    	for (Employee currentEmployee : employees) {
    		empdao.initialize(currentEmployee.getEmployeeKey());
    		empdao.delete();
    	}
    	
    	List<Equipment> equipments = edao.getAll();
    	for (Equipment currentEquipment : equipments) {
    		edao.initialize(currentEquipment.getEquipmentId());
    		edao.delete();
    	}
    	
    	List<Equipmenttype> equpmentTypes = etdao.getAll();
    	for (Equipmenttype currentEquipmentType : equpmentTypes) {
    		etdao.initialize(currentEquipmentType.getEquipmentTypeId());
    		etdao.delete();
    	}
    }
   
}
