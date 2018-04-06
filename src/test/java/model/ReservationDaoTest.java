package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import db.DatabaseUtil;
import utils.PropertyUtils;

public class ReservationDaoTest {
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
    public void testAddReservation() {
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

    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype = addEquipmenttype(equipmentTypeCode, equipmentTypeName1);
    	Equipment testEquipment = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype);
    	Reservation DBreservation1 = addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment);
    	
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
    	assertEquals(testEmployee, DBreservation1.getEmployee());
    	assertEquals(testEquipment, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerial() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	
    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmenttype1);
    	
    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment1);
    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment1);
    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment2);
    	
    	List<Reservation> equipmentSerial1reservations = rdao.getBySerial(equipmentSerial1);
    	List<Reservation> equipmentSerial2reservations = rdao.getBySerial(equipmentSerial2);
    	
    	Reservation DBreservation1 = equipmentSerial1reservations.get(0);
    	Reservation DBreservation2 = equipmentSerial1reservations.get(1);
    	Reservation DBreservation3 = equipmentSerial2reservations.get(0);
    	
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn.getTime()), ZoneId.systemDefault());
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation2Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation2.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation3Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation3.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation2Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation2.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation3Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation3.getDateReturn().getTime()), ZoneId.systemDefault());

    	assertEquals(2, equipmentSerial1reservations.size());
    	assertEquals(1, equipmentSerial2reservations.size());
    	
    	assertEquals(dateTakeLdt.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation1Take.getMinute());
    	assertEquals(dateTakeLdt.getYear(), DBreservation2Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation2Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation2Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation2Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation2Take.getMinute());
    	assertEquals(dateTakeLdt.getYear(), DBreservation3Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation3Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation3Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation3Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation3Take.getMinute());
    	
    	assertEquals(dateReturnLdt.getYear(), DBreservation1Return.getYear());
    	assertEquals(dateReturnLdt.getMonth(), DBreservation1Return.getMonth());
    	assertEquals(dateReturnLdt.getDayOfMonth(), DBreservation1Return.getDayOfMonth());
    	assertEquals(dateReturnLdt.getHour(), DBreservation1Return.getHour());
    	assertEquals(dateReturnLdt.getMinute(), DBreservation1Return.getMinute());
    	assertEquals(dateReturnLdt.getYear(), DBreservation2Return.getYear());
    	assertEquals(dateReturnLdt.getMonth(), DBreservation2Return.getMonth());
    	assertEquals(dateReturnLdt.getDayOfMonth(), DBreservation2Return.getDayOfMonth());
    	assertEquals(dateReturnLdt.getHour(), DBreservation2Return.getHour());
    	assertEquals(dateReturnLdt.getMinute(), DBreservation2Return.getMinute());
    	assertEquals(dateReturnLdt.getYear(), DBreservation3Return.getYear());
    	assertEquals(dateReturnLdt.getMonth(), DBreservation3Return.getMonth());
    	assertEquals(dateReturnLdt.getDayOfMonth(), DBreservation3Return.getDayOfMonth());
    	assertEquals(dateReturnLdt.getHour(), DBreservation3Return.getHour());
    	assertEquals(dateReturnLdt.getMinute(), DBreservation3Return.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(reservationType, DBreservation2.getReservationType());
    	assertEquals(reservationType, DBreservation3.getReservationType());
    	
    	assertEquals(testEmployee, DBreservation1.getEmployee());
    	assertEquals(testEmployee, DBreservation2.getEmployee());
    	assertEquals(testEmployee, DBreservation3.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    	assertEquals(testEquipment1, DBreservation2.getEquipment());
    	assertEquals(testEquipment2, DBreservation3.getEquipment());
    }
    
    @Test
    public void testGetOpenByEmployeeId() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeId2 = "222222222";
    	String employeeName1 = "Test Employee1";
    	String employeeName2 = "Test Employee2";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Employee testEmployee2 = addEmployee(employeeId2, employeeName2);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);
    	
    	addReservation(reservationType, dateTake, null, testEmployee1, testEquipment1);
    	addReservation(reservationType, dateTake, null, testEmployee1, testEquipment1);
    	addReservation(reservationType, dateTake, null, testEmployee2, testEquipment1);
    	
    	List<Reservation> employeeId1reservations = rdao.getOpenByEmployeeId(employeeId1);
    	List<Reservation> employeeId2reservations = rdao.getOpenByEmployeeId(employeeId2);
    	
    	Reservation DBreservation1 = employeeId1reservations.get(0);
    	Reservation DBreservation2 = employeeId1reservations.get(1);
    	Reservation DBreservation3 = employeeId2reservations.get(0);
    	
    	assertEquals(2, employeeId1reservations.size());
    	assertEquals(1, employeeId2reservations.size());
    	
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation2Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation2.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation3Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation3.getDateTake().getTime()), ZoneId.systemDefault());

    	assertEquals(dateTakeLdt.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation1Take.getMinute());
    	assertEquals(dateTakeLdt.getYear(), DBreservation2Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation2Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation2Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation2Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation2Take.getMinute());
    	assertEquals(dateTakeLdt.getYear(), DBreservation3Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation3Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation3Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation3Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation3Take.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(reservationType, DBreservation2.getReservationType());
    	assertEquals(reservationType, DBreservation3.getReservationType());
    	
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEmployee1, DBreservation2.getEmployee());
    	assertEquals(testEmployee2, DBreservation3.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    	assertEquals(testEquipment1, DBreservation2.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_NoReservations() {
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String equipmentSerial1 = "TestSerial1";

    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(0, reservations.size());
    }
    
    @Test
    public void testGetBySerialAndDate_BeforeRange() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(12).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.minusMonths(8).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(0, reservations.size());
    }
    
    @Test
    public void testGetBySerialAndDate_EndInRange() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(12).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.minusMonths(5).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, dateReturn, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn.getTime()), ZoneId.systemDefault());
    	
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
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_EndInRangeOpen() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(12).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, null, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	
    	assertEquals(dateTakeLdt.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation1Take.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_CompletelyInRange() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.minusMonths(4).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, dateReturn, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn.getTime()), ZoneId.systemDefault());
    	
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
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_StartInRangeOpen() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, null, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	
    	assertEquals(dateTakeLdt.getYear(), DBreservation1Take.getYear());
    	assertEquals(dateTakeLdt.getMonth(), DBreservation1Take.getMonth());
    	assertEquals(dateTakeLdt.getDayOfMonth(), DBreservation1Take.getDayOfMonth());
    	assertEquals(dateTakeLdt.getHour(), DBreservation1Take.getHour());
    	assertEquals(dateTakeLdt.getMinute(), DBreservation1Take.getMinute());
    	
    	assertEquals(reservationType, DBreservation1.getReservationType());
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_StartInRange() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.plusMonths(3).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, dateReturn, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(1, reservations.size());
    	Reservation DBreservation1 = reservations.get(0);
    	
    	LocalDateTime DBreservation1Take = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime DBreservation1Return = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation1.getDateReturn().getTime()), ZoneId.systemDefault());
    
    	LocalDateTime dateTakeLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTake.getTime()), ZoneId.systemDefault());
    	LocalDateTime dateReturnLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateReturn.getTime()), ZoneId.systemDefault());
    	
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
    	assertEquals(testEmployee1, DBreservation1.getEmployee());
    	assertEquals(testEquipment1, DBreservation1.getEquipment());
    }
    
    @Test
    public void testGetBySerialAndDate_AfterRange() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date start = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date end = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake = Date.from(currentDateTime.plusMonths(1).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.plusMonths(3).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	addReservation(reservationType, dateTake, dateReturn, testEmployee1, testEquipment1);    	
    	List<Reservation> reservations = rdao.getBySerialAndDate(equipmentSerial1, start, end);
    	assertEquals(0, reservations.size());
    }
    
    @Test
    public void testSerialHasOpenReservation() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	assertFalse(rdao.serialHasOpenReservation(equipmentSerial1));
    	addReservation(reservationType, dateTake, dateReturn, testEmployee, testEquipment1);
    	assertFalse(rdao.serialHasOpenReservation(equipmentSerial1));
    	addReservation(reservationType, dateTake, null, testEmployee, testEquipment1);
    	assertTrue(rdao.serialHasOpenReservation(equipmentSerial1));
    }
    
    @Test
    public void testGetOpenReservationIdBySerial() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationType = 0;
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake1 = Date.from(currentDateTime.minusMonths(12).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn1 = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(currentDateTime.minusMonths(3).atZone(ZoneId.systemDefault()).toInstant());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	Employee testEmployee = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmenttype1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmenttype1);

    	// No reservations in DB
    	assertEquals(0, rdao.getOpenReservationIdBySerial(equipmentSerial1));
    	
    	// Closed reservation in DB
    	addReservation(reservationType, dateTake1, dateReturn1, testEmployee, testEquipment1);
    	assertEquals(0, rdao.getOpenReservationIdBySerial(equipmentSerial1));
    	
    	// Open reservation in DB
    	int openReservationId = addReservation(reservationType, dateTake2, null, testEmployee, testEquipment1).getReservationId();
    	assertEquals(openReservationId, rdao.getOpenReservationIdBySerial(equipmentSerial1));
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
    	
    	List<Equipmenttype> equipmentTypes = etdao.getAll();
    	for (Equipmenttype currentEquipmentType : equipmentTypes) {
    		etdao.initialize(currentEquipmentType.getEquipmentTypeId());
    		etdao.delete();
    	}
    }
}