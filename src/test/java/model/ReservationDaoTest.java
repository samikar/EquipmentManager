package model;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class ReservationDaoTest {
	
	@Autowired
	private static EmployeeDao empdao;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ReservationDao rdao;
	
    @BeforeClass
    public static void init() {
    	empdao = new EmployeeDao();
    	edao = new EquipmentDao();
    	etdao = new EquipmenttypeDao();
    	rdao = new ReservationDao();
        
    	empdao.initTest();
        edao.initTest();
        etdao.initTest();
        rdao.initTest();
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
    @Transactional
    @Rollback(true)
    public void testAddReservation() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode = 1111;
    	int reservationType = 0;
    	String employeeName = "Test Employee1";
    	String employeeId = "123456789";
    	String equipmentName = "Test Employee1";
    	String equipmentSerial = "TestSerial1";
    	String equipmentTypeName = "TestType1";
    	
    	Employee testEmployee = new Employee(employeeId, employeeName);
    	Equipmenttype testEquipmentType = new Equipmenttype(equipmentTypeCode, equipmentTypeName);
    	Equipment testEquipment = new Equipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmentType);
    	Reservation testReservation = new Reservation();
    	
    	empdao.persist(testEmployee);
    	etdao.persist(testEquipmentType);
    	edao.persist(testEquipment);
    	
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	
    	testReservation.setDateTake(dateTake);
    	testReservation.setDateReturn(dateReturn);
    	testReservation.setEmployee(testEmployee);
    	testReservation.setEquipment(testEquipment);
    	testReservation.setReservationType(reservationType);
    	rdao.persist(testReservation);
  	
    	rdao.destroy();
    	rdao.initTest();
    	rdao.initialize(testReservation.getReservationId());
    	Reservation DBreservation = new Reservation();
    	DBreservation = rdao.getDao();
    	
    	LocalDateTime returnTest = LocalDateTime.ofInstant(Instant.ofEpochMilli(testReservation.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime takeTest = LocalDateTime.ofInstant(Instant.ofEpochMilli(testReservation.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime returnDB = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime takeDB = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation.getDateTake().getTime()), ZoneId.systemDefault());
    	
    	assertEquals(returnTest.getYear(), returnDB.getYear());
    	assertEquals(returnTest.getMonth(), returnDB.getMonth());
    	assertEquals(takeTest.getDayOfMonth(), takeDB.getDayOfMonth());
    	assertEquals(returnTest.getHour(), returnDB.getHour());
    	assertEquals(returnTest.getMinute(), returnDB.getMinute());
    	assertEquals(reservationType, DBreservation.getReservationType());
    	
    	assertEquals(employeeId, DBreservation.getEmployee().getEmployeeId());
    	assertEquals(equipmentSerial, DBreservation.getEquipment().getSerial());
    	assertEquals(equipmentTypeCode, DBreservation.getEquipment().getEquipmenttype().getTypeCode());
    	
    }
    
    public void emptyTables() {
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