package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import db.DatabaseUtil;
import model.Employee;
import model.EmployeeDao;
import model.Equipment;
import model.EquipmentDao;
import model.EquipmentStatus;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.Reservation;
import model.ReservationDao;
import utils.PropertyUtils;

public class ReservationControllerTest {
	@Autowired
	private static EmployeeDao empdao;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ReservationDao rdao;
	private static ReservationController controller;
	
	final static Logger logger = Logger.getLogger(ReservationControllerTest.class);
	
    @BeforeClass
    public static void init() {    	
    	empdao = new EmployeeDao();
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
    	rdao = new ReservationDao();
 
    	empdao.init();
        edao.init();
        etdao.init();
        rdao.init();
        
		controller = new ReservationController();
    }

    @AfterClass
    public static void destroy() {
    	empdao.destroy();
        edao.destroy();
        etdao.destroy();
        rdao.destroy();
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
	public void testGetAllReservations_noReservations() {
		List<Reservation> reservations = controller.getAllReservations();
		assertEquals(0, reservations.size());
	}
	
	@Test
	public void testGetAllReservations_1Reservation() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	
    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	addReservation(reservationTypeInUse, dateTake, dateReturn, testEmployee, testEquipment);
    	
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
    	
    	assertEquals(reservationTypeInUse, DBreservation1.getReservationType());
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
    	int reservationTypeInUse = 0;
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
    	
    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Employee testEmployee2 = addEmployee(employeeId2, employeeName2);
    	Employee testEmployee3 = addEmployee(employeeId3, employeeName3);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmenttype2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype1);
    	Equipment testEquipment3 = addEquipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmenttype2);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee2, testEquipment2);
    	addReservation(reservationTypeInUse, dateTake3, null, testEmployee3, testEquipment3);
    	
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
    	
    	assertEquals(reservationTypeInUse, DBreservation1.getReservationType());
    	assertEquals(reservationTypeInUse, DBreservation2.getReservationType());
    	assertEquals(reservationTypeInUse, DBreservation3.getReservationType());
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
	public void testTakeEquipment_OK() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationTypeInUse = 0;
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";

    	addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);

    	// Reservation table empty
    	List<Reservation> reservations = controller.getAllReservations();
    	assertEquals(0, reservations.size());
    	// Add one reservation
    	controller.takeEquipment(employeeId1, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	// One reservation found in Reservation table
    	reservations = controller.getAllReservations();
    	assertEquals(1, reservations.size());
    	 
    	Reservation DBreservation1 = reservations.get(0);
    
    	assertNull(DBreservation1.getDateReturn());
    	assertEquals(reservationTypeInUse, DBreservation1.getReservationType());
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
		boolean exceptionThrown = false;
		int reservationTypeInUse = 0;
    	String equipmentSerial1 = "TestSerial";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "Employee ID must not be empty";
    	
    	try {
    		controller.takeEquipment(null, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testTakeEquipment_noSerial() {
		boolean exceptionThrown = false;
		int reservationTypeInUse = 0;
    	String employeeId1 = "111111111";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "Serial number must not be empty";    	
    	
    	try {
    		controller.takeEquipment(employeeId1, null, Integer.toString(reservationTypeInUse));
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testTakeEquipment_noReservationType() {
		boolean exceptionThrown = false;
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "Reservation type must be selected";

    	addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	
    	try {
    		controller.takeEquipment(employeeId1, equipmentSerial1, null);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testTakeEquipment_serialNotFound() {
		boolean exceptionThrown = false;
		int reservationTypeInUse = 0;
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentSerial1 = "NoSuchSerial";
    	String expectedExceptionMessage = "No equipment found for serial number: " + equipmentSerial1;
    	String exceptionMessage = "";

    	addEmployee(employeeId1, employeeName1);
    	    	
    	try {
    		controller.takeEquipment(employeeId1, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);

	}
	
	@Test
	public void testTakeEquipment_openReservationFound() {
		boolean exceptionThrown = false;
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationTypeInUse = 0;
    	String employeeId1 = "111111111";
    	String employeeId2 = "222222222";
    	String employeeName1 = "Test Employee1";
    	String employeeName2 = "Test Employee2";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "Open reservation for serial number " + equipmentSerial1;

    	addEmployee(employeeId1, employeeName1);
    	addEmployee(employeeId2, employeeName2);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	    	
    	// Add one reservation
    	controller.takeEquipment(employeeId1, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	
    	try {
    		
    		controller.takeEquipment(employeeId2, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage + " already found", exceptionMessage);

	}
	
	@Ignore
	@Test
	public void testTakeEquipment_employeeNotFound() {
		boolean exceptionThrown = false;
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		int reservationTypeInUse = 0;
		String employeeId1 = "NoSuchEmployee";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "No employee found for employeeId: " + employeeId1;
    	
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	    	
    	try {
    		controller.takeEquipment(employeeId1, equipmentSerial1, Integer.toString(reservationTypeInUse));
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);

	}
	
	@Test
	public void testReturnSingle_noSerial() {
		boolean exceptionThrown = false;
		String equipmentSerial1 = "NoSuchSerial";
		String exceptionMessage = "";
		String expectedExceptionMessage = "No equipment found for serial number: " + equipmentSerial1;
    	    		
    	try {
    		controller.returnSingle(equipmentSerial1);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testReturnSingle_serialNotFound() { 	
		boolean exceptionThrown = false;
		String exceptionMessage = "";
    	    	
    	try {
    		controller.returnSingle(null);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals("Serial number must not be empty", exceptionMessage);
	}
	
	@Test
	public void testReturnSingle_noReservation() {
		boolean exceptionThrown = false;
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee1";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "No open reservation found for serial number " + equipmentSerial1;
    	
		addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	
    	try {
    		controller.returnSingle(equipmentSerial1);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testReturnSingle_noOpenReservation() {
    	boolean exceptionThrown = false;
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		int reservationTypeInUse = 0;
		LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee1";
    	String equipmentName1 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial";
    	String equipmentTypeName1 = "TestType";
    	String exceptionMessage = "";
    	String expectedExceptionMessage = "No open reservation found for serial number " + equipmentSerial1;
    	
		Employee testEmployee = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	addReservation(reservationTypeInUse, dateTake, dateReturn, testEmployee, testEquipment);
    	
    	try {
    		controller.returnSingle(equipmentSerial1);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
    	assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
    
	@Test
	public void testReturnMultiple_OK() {
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		int reservationTypeInUse = 0;
		Gson gson = new Gson();
		LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee1";
    	String equipmentName1 = "Test Equipment";
    	String equipmentName2 = "Test Equipment";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType";
    	String reservationIdsJSON = "";
    	
		Employee testEmployee = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype);
    	
    	Reservation testReservation1 = addReservation(reservationTypeInUse, dateTake, null, testEmployee, testEquipment1);
    	Reservation testReservation2 = addReservation(reservationTypeInUse, dateTake, null, testEmployee, testEquipment2);
    	
    	List<Reservation> reservations = controller.getAllReservations();
    	assertNull(reservations.get(0).getDateReturn());
    	assertNull(reservations.get(1).getDateReturn());
    	
    	// Put reservationIds to int array
    	int[] reservationIds = {testReservation1.getReservationId(), testReservation2.getReservationId()};
    	// Convert int array to JSON String
    	reservationIdsJSON = gson.toJson(reservationIds);
    	// Give JSON String as a parameter
    	controller.returnMultiple(reservationIdsJSON);

    	reservations = controller.getAllReservations();
    	assertNotNull(reservations.get(0).getDateReturn());
    	assertNotNull(reservations.get(1).getDateReturn());
	}
	
	@Test
	public void testReturnMultiple_invalidJSON() {
		String invalidJSONStr = "foobar";
    	assertEquals("Error formatting JSON String", controller.returnMultiple(invalidJSONStr));
	}
	
	@Test
	public void testGetByEmployeeId_OK() {
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		int reservationTypeInUse = 0;
		LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeId2 = "222222222";
		String employeeName1 = "Test Employee1";
		String employeeName2 = "Test Employee2";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentName3 = "Test Equipment3";
    	String equipmentName4 = "Test Equipment4";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentSerial3 = "TestSerial3";
    	String equipmentSerial4 = "TestSerial4";
    	String equipmentTypeName1 = "TestType";
    	
		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Employee testEmployee2 = addEmployee(employeeId2, employeeName2);
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment3 = addEquipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment4 = addEquipment(equipmentName4, equipmentSerial4, equipmentStatusEnabled, testEquipmenttype);
    	
    	addReservation(reservationTypeInUse, dateTake, null, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake, null, testEmployee1, testEquipment2);
    	addReservation(reservationTypeInUse, dateTake, null, testEmployee2, testEquipment3);
    	addReservation(reservationTypeInUse, dateTake, null, testEmployee2, testEquipment4);
    	
    	List<Reservation> reservations = controller.getbyEmployeeId(employeeId1);
    	assertEquals(2, reservations.size());
    	assertEquals(employeeId1, reservations.get(0).getEmployee().getEmployeeId());
    	assertEquals(employeeName1, reservations.get(0).getEmployee().getName());
    	assertEquals(equipmentSerial1, reservations.get(0).getEquipment().getSerial());
    	assertEquals(equipmentName1, reservations.get(0).getEquipment().getName());
    	assertEquals(employeeId1, reservations.get(1).getEmployee().getEmployeeId());
    	assertEquals(employeeName1, reservations.get(1).getEmployee().getName());
    	assertEquals(equipmentSerial2, reservations.get(1).getEquipment().getSerial());
    	assertEquals(equipmentName2, reservations.get(1).getEquipment().getName());
    	
    	reservations = controller.getbyEmployeeId(employeeId2);
    	assertEquals(2, reservations.size());
    	assertEquals(employeeId2, reservations.get(0).getEmployee().getEmployeeId());
    	assertEquals(employeeName2, reservations.get(0).getEmployee().getName());
    	assertEquals(equipmentSerial3, reservations.get(0).getEquipment().getSerial());
    	assertEquals(equipmentName3, reservations.get(0).getEquipment().getName());
    	assertEquals(employeeId2, reservations.get(1).getEmployee().getEmployeeId());
    	assertEquals(employeeName2, reservations.get(1).getEmployee().getName());
    	assertEquals(equipmentSerial4, reservations.get(1).getEquipment().getSerial());
    	assertEquals(equipmentName4, reservations.get(1).getEquipment().getName());
	}
	
	@Test
	public void testGetByEmployeeId_noEmployeeId() {
		boolean exceptionThrown = false;
		String exceptionMessage = "";
		String expectedExceptionMessage = "Employee ID must not be empty";		 
		
    	try {
    		controller.getbyEmployeeId(null);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
		assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testGetByEmployeeId_noReservations() {
		boolean exceptionThrown = false;
		String employeeId1 = "111111111";
		String exceptionMessage = "";
		String expectedExceptionMessage = "No reservations found for employeeId " + employeeId1;
		 
    	try {
    		controller.getbyEmployeeId(employeeId1);
    	} catch (IllegalArgumentException e) {
    		exceptionThrown = true;
    		exceptionMessage = e.getMessage();
    	}
		assertTrue(exceptionThrown);
    	assertEquals(expectedExceptionMessage, exceptionMessage);
	}
	
	@Test
	public void testGetEquipmentStatus() {
		int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
		int reservationTypeInUse = 0;
		int reservationTypeCalibration = 1;
		int reservationTypeMaintentance = 2;
		LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
		String employeeName1 = "Test Employee1";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentName3 = "Test Equipment3";
    	String equipmentName4 = "Test Equipment4";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentSerial3 = "TestSerial3";
    	String equipmentSerial4 = "TestSerial4";
    	String equipmentTypeName1 = "TestType";
    	String reservationTypeStrAvailable = "Available";
    	String reservationTypeStrInUse = "In use";
    	String reservationTypeStrCalibration = "Calibration";
    	String reservationTypeStrMaintenance = "Maintenance";
    	
		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
		
		Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment3 = addEquipment(equipmentName3, equipmentSerial3, equipmentStatusEnabled, testEquipmenttype);
    	Equipment testEquipment4 = addEquipment(equipmentName4, equipmentSerial4, equipmentStatusEnabled, testEquipmenttype);
    	
		addReservation(reservationTypeInUse, dateTake, null, testEmployee1, testEquipment2);
		addReservation(reservationTypeCalibration, dateTake, null, testEmployee1, testEquipment3);
		addReservation(reservationTypeMaintentance, dateTake, null, testEmployee1, testEquipment4);

		List<EquipmentStatus> equipmentStatusList = controller.getEquipmentStatus();
		
		if(logger.isDebugEnabled()){
			logger.debug("TestEquipment1: " + testEquipment1.getEquipmentId());
			logger.debug("TestEquipment2: " + testEquipment2.getEquipmentId());
			logger.debug("TestEquipment3: " + testEquipment3.getEquipmentId());
			logger.debug("TestEquipment4: " + testEquipment4.getEquipmentId());

			for (int i=0; i<equipmentStatusList.size(); i++) {
				logger.debug("List id" + i + ": " + equipmentStatusList.get(0).getEquipmentId());
			}
		}
		
//		System.out.println("TestEquipment1: " + testEquipment1.getEquipmentId());
//		System.out.println("TestEquipment2: " + testEquipment2.getEquipmentId());
//		System.out.println("TestEquipment3: " + testEquipment3.getEquipmentId());
//		System.out.println("TestEquipment4: " + testEquipment4.getEquipmentId());
//		
//		for (int i=0; i<equipmentStatusList.size(); i++) {
//			System.out.println("List id" + i + ": " + equipmentStatusList.get(0).getEquipmentId());
//		}
		
		assertEquals(4, equipmentStatusList.size());
		for (EquipmentStatus currentEquipmentStatus : equipmentStatusList) {
			if (currentEquipmentStatus.getEquipmentId() == testEquipment1.getEquipmentId()) {
				assertEquals(equipmentSerial1, equipmentStatusList.get(0).getSerial());
				assertEquals(equipmentName1, equipmentStatusList.get(0).getName());
				assertEquals(reservationTypeStrAvailable, equipmentStatusList.get(0).getAvailability());
			} else if (currentEquipmentStatus.getEquipmentId() == testEquipment2.getEquipmentId()) {
				assertEquals(equipmentSerial2, equipmentStatusList.get(1).getSerial());
				assertEquals(equipmentName2, equipmentStatusList.get(1).getName());
				assertEquals(reservationTypeStrInUse, equipmentStatusList.get(1).getAvailability());
			} else if (currentEquipmentStatus.getEquipmentId() == testEquipment3.getEquipmentId()) {
				assertEquals(equipmentSerial3, equipmentStatusList.get(2).getSerial());
				assertEquals(equipmentName3, equipmentStatusList.get(2).getName());
				assertEquals(reservationTypeStrCalibration, equipmentStatusList.get(2).getAvailability());
			} else if (currentEquipmentStatus.getEquipmentId() == testEquipment4.getEquipmentId()) {
				assertEquals(equipmentSerial4, equipmentStatusList.get(3).getSerial());
				assertEquals(equipmentName4, equipmentStatusList.get(3).getName());
				assertEquals(reservationTypeStrMaintenance, equipmentStatusList.get(3).getAvailability());
			}
		}
	}
	
	@Ignore
	@Test
	public void testGetEmployee_OK() {
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee1";
		ReservationController controller = new ReservationController();
		
		addEmployee(employeeId1, employeeName1);
		Employee DBEmployee = controller.getEmployee(employeeId1);
		assertEquals(employeeId1, DBEmployee.getEmployeeId());
		assertEquals(employeeName1, DBEmployee.getName());
	}
	
	@Ignore
	@Test
	public void testGetEmployee_noSuchEmployee() {
		String employeeId1 = "111111111";
		ReservationController controller = new ReservationController();
		Employee DBEmployee = controller.getEmployee(employeeId1);
		
		assertNull(DBEmployee);
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
    	empdao.refresh();
    	edao.refresh();
    	etdao.refresh();
    	rdao.refresh();
    	
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

