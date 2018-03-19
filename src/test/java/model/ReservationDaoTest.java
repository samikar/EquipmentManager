package model;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    	Employee testEmployee = new Employee();
    	Equipmenttype testEquipmentType = new Equipmenttype();
    	Equipment testEquipment = new Equipment();
    	Reservation testReservation = new Reservation();
    	
    	testEmployee.setEmployeeId("123456789");
    	testEmployee.setName("Test Employee1");
    	testEmployee.setEmployeeKey(empdao.persist(testEmployee));
    	
    	testEquipmentType.setTypeCode(1111);
    	testEquipmentType.setTypeName("Test Type1");
    	testEquipmentType.setEquipmentTypeId(etdao.persist(testEquipmentType));
    	
    	testEquipment.setName("Test Equipment1");
    	testEquipment.setSerial("TE1_001");
    	testEquipment.setStatus(1);
    	testEquipment.setEquipmenttype(testEquipmentType);
    	testEquipment.setEquipmentId(edao.persist(testEquipment));
    	
    	LocalDateTime currentDateTime =  LocalDateTime.now();
    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
    	
    	testReservation.setDateTake(dateTake);
    	testReservation.setDateReturn(dateReturn);
    	testReservation.setEmployee(testEmployee);
    	testReservation.setEquipment(testEquipment);
    	testReservation.setReservationType(0);
    	testReservation.setReservationId(rdao.persist(testReservation));
  	
    	rdao.destroy();
    	rdao.initTest();
    	rdao.initialize(testReservation.getReservationId());
    	Reservation DBreservation = new Reservation();
    	DBreservation = rdao.getDao();
    	
    	LocalDateTime returnTest = LocalDateTime.ofInstant(Instant.ofEpochMilli(testReservation.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime takeTest = LocalDateTime.ofInstant(Instant.ofEpochMilli(testReservation.getDateTake().getTime()), ZoneId.systemDefault());
    	LocalDateTime returnDB = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation.getDateReturn().getTime()), ZoneId.systemDefault());
    	LocalDateTime takeDB = LocalDateTime.ofInstant(Instant.ofEpochMilli(DBreservation.getDateTake().getTime()), ZoneId.systemDefault());
    	
    	assertEquals(testReservation.getReservationId(), DBreservation.getReservationId());
    	assertEquals(returnTest.getYear(), returnDB.getYear());
    	assertEquals(returnTest.getMonth(), returnDB.getMonth());
    	assertEquals(takeTest.getDayOfMonth(), takeDB.getDayOfMonth());
    	assertEquals(returnTest.getHour(), returnDB.getHour());
    	assertEquals(returnTest.getMinute(), returnDB.getMinute());
    	assertEquals(testReservation.getEmployee().getEmployeeId(), DBreservation.getEmployee().getEmployeeId());
    	assertEquals(testReservation.getEquipment().getEquipmentId(), DBreservation.getEquipment().getEquipmentId());
    	assertEquals(testReservation.getReservationType(), DBreservation.getReservationType());
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