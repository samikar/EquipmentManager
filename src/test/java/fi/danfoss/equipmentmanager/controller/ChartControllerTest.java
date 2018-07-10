package fi.danfoss.equipmentmanager.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import fi.danfoss.equipmentmanager.model.Employee;
import fi.danfoss.equipmentmanager.model.EmployeeDao;
import fi.danfoss.equipmentmanager.model.Equipment;
import fi.danfoss.equipmentmanager.model.EquipmentDao;
import fi.danfoss.equipmentmanager.model.EquipmentUsage;
import fi.danfoss.equipmentmanager.model.Equipmenttype;
import fi.danfoss.equipmentmanager.model.EquipmenttypeDao;
import fi.danfoss.equipmentmanager.model.MonthlyUsage;
import fi.danfoss.equipmentmanager.model.Reservation;
import fi.danfoss.equipmentmanager.model.ReservationDao;
import fi.danfoss.equipmentmanager.utils.PropertyUtils;

//@Ignore
public class ChartControllerTest {
	private static Properties properties = PropertyUtils.loadProperties();
	private final static double WORKDAY = Double.parseDouble(properties.getProperty("WORKDAY"));
	private final int STARTHOUR = Integer.parseInt(properties.getProperty("STARTHOUR"));
	private final int STARTMINUTE = Integer.parseInt(properties.getProperty("STARTMINUTE"));
	private final int ENDHOUR = Integer.parseInt(properties.getProperty("ENDHOUR"));
	private final int ENDMINUTE = Integer.parseInt(properties.getProperty("ENDMINUTE"));
	
	final static Logger logger = Logger.getLogger(ChartControllerTest.class);
	
	@Autowired
	private static EmployeeDao empdao;
	private static EquipmentDao edao;
	private static EquipmenttypeDao etdao;
	private static ReservationDao rdao;
	private static ChartController controller;

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
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
        
		controller = new ChartController();
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
    public void testUsageBySerial_serialEmpty() {
    	String serial = "";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(serial, startStr, endStr);
    }
    
    @Test
    public void testUsageBySerial_startEmpty() {
    	String serial = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = "";
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(serial, startStr, endStr);
    }
    
    @Test
    public void testUsageBySerial_endEmpty() {
    	String serial = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = "";
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(serial, startStr, endStr);
    }
                    
    @Test
    public void testUsageBySerial_fullWeek() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_weekEndOverlap() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).plusDays(4).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(7*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_notFullWeek() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(3*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_notFullHoursAtTake() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).plusHours(4).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY - 4, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_notFullMinutesAtTake() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals((5*WORKDAY) - 0.25, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_notFullHoursAtReturn() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusHours(3).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY - 3, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_notFullMinutesAtReturn() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusMinutes(45).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals((5*WORKDAY) - 0.75, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_noReturn() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = getFriday(LocalDate.now().atTime(23,59));
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, null, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_twoReservations() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(30);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(30);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().minusDays(7).atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().minusDays(7).atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());    
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	ZoneId zoneId = ZoneId.systemDefault();
    	    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals((10*WORKDAY), usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_calibration() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeCalibration = 1;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeCalibration, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(0, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(5*WORKDAY, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageBySerial_maintenance() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeMaintenance = 2;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeMaintenance, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.usageBySerial(equipmentSerial1, startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(0, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(5*WORKDAY, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testUsageByType_typeCodeEmpty() {
    	String typeCode = "";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageByType(typeCode, startStr, endStr);
    }
    
    @Test
    public void testUsageByType_startEmpty() {
    	String typeCode = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = "";
    	exception.expect(IllegalArgumentException.class);
    	controller.usageByType(typeCode, startStr, endStr);
    }
    
    @Test
    public void testUsageByType_endEmpty() {
    	String typeCode = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = "";
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageByType(typeCode, startStr, endStr);
    }
    
    @Test
    public void testUsageByType_oneEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testUsageByType_twoEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment2);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(2, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
    		if (currentUsage.getSerial().equals(equipmentSerial1)) {
	        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
	        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
	        	assertEquals(0, currentUsage.getCalibration(), 0);
	        	assertEquals(0, currentUsage.getMaintenance(), 0);
	        	assertEquals(equipmentSerial1, currentUsage.getSerial());
	        	assertEquals(equipmentName1, currentUsage.getName());
	        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
	        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
	        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    		}
    		else if (currentUsage.getSerial().equals(equipmentSerial2)) {
	        	assertEquals(3*WORKDAY, currentUsage.getInUse(), 0);
	        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
	        	assertEquals(0, currentUsage.getCalibration(), 0);
	        	assertEquals(0, currentUsage.getMaintenance(), 0);
	        	assertEquals(equipmentSerial2, currentUsage.getSerial());
	        	assertEquals(equipmentName2, currentUsage.getName());
	        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
	        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
	        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    		}
    	}
    }
    
    @Test
    public void testUsageByType_oneEquipmentNoReservation() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testUsageByType_noEquipment() {
    	int equipmentTypeCode1 = 1111;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String equipmentTypeName1 = "TestType1";

    	addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	    	
    	assertEquals(0, usageList.size());
    }
    
    @Test
    public void testUsageByType_noType() {
    	int equipmentTypeCode1 = 1111;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	    	
    	assertEquals(0, usageList.size());
    }
    
    @Test
    public void testUsageByType_twoTypes() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmentType2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType2);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment2);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {    		
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testUsageByType_available() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";


    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }    
    
    @Test
    public void testUsageByType_inUse() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testUsageByType_calibration() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeMaintenance = 1;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeMaintenance, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(5*WORKDAY, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testUsageByType_maintenance() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeCalibration = 2;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	ZoneId zoneId = ZoneId.systemDefault();
    	
    	String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
    	String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeCalibration, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.usageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(5*WORKDAY, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testMonthlyUsageByType_typeCodeEmpty() {
    	String typeCode = "";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(typeCode, startStr, endStr);
    }
    
    @Test
    public void testMonthlyUsageByType_startEmpty() {
    	String typeCode = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = Long.toString(endLdt.atZone(zoneId).toEpochSecond());
    	String startStr = "";
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(typeCode, startStr, endStr);
    }
    
    @Test
    public void testMonthlyUsageByType_endEmpty() {
    	String typeCode = "foobar";
    	LocalDateTime endLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	LocalDateTime startLdt = endLdt.minusMonths(6);
    	ZoneId zoneId = ZoneId.systemDefault();
    	String endStr = "";
    	String startStr = Long.toString(startLdt.atZone(zoneId).toEpochSecond());
    	exception.expect(IllegalArgumentException.class);
    	controller.usageBySerial(typeCode, startStr, endStr);
    }
    
    @Test
    public void testMonthlyUsageByType_oneEquipmentOneReservationOneMonth() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeCode1 = 1111;
		int reservationTypeInUse = 0;

		ZoneId zoneId = ZoneId.systemDefault();

		// Set constraints to 1.1 - 30-6 of current year
		LocalDateTime startConstraint = LocalDate.now().withDayOfYear(1).atTime(0,0);
		LocalDateTime endConstraint = LocalDate.now().withDayOfYear(1).plusMonths(6).withDayOfMonth(1).minusDays(1).atTime(23,59);

		// Reservation lasts from the first Monday of January to the next Friday   
		Date dateTake1 = Date.from(getMonday(startConstraint.withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn1 = Date.from(getFriday(startConstraint.withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());

		String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
		String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee";
		String equipmentName1 = "Test Equipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeName1 = "TestType1";

		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
		Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);

		List<MonthlyUsage> monthlyUsageList = controller.monthlyUsageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);

		LocalDateTime currentMonthStart = startConstraint;
		LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
		assertEquals(6, monthlyUsageList.size());
		int months = 0;
		for (MonthlyUsage currentMonthlyUsage : monthlyUsageList) {
			double workHoursInConstraints = workHoursInConstraints(currentMonthStart, currentMonthEnd);
			assertEquals(currentMonthStart.getMonth().name() + ", " + currentMonthStart.getYear(), currentMonthlyUsage.getMonth());
			if (currentMonthlyUsage.getMonth().equals("JANUARY, " + startConstraint.getYear())) {
				assertEquals(5 * WORKDAY, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
				months++;
			}
			else {
				
				assertEquals(0, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
			}
			
			currentMonthStart = currentMonthStart.plusMonths(1);
			currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
			months++;
		}
		assertEquals(monthlyUsageList.size(), months - 1);
    }
    
    @Test
    public void testMonthlyUsageByType_twoEquipmentTwoReservationOneMonth() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeCode1 = 1111;
		int reservationTypeInUse = 0;

		ZoneId zoneId = ZoneId.systemDefault();

		// Set constraints to 1.1 - 30-6 of current year
		LocalDateTime startConstraint = LocalDate.now().withDayOfYear(1).atTime(0,0);
		LocalDateTime endConstraint = LocalDate.now().withDayOfYear(1).plusMonths(6).withDayOfMonth(1).minusDays(1).atTime(23,59);

		// Reservation lasts from the first Monday of January to the next Friday   
		Date dateTake1 = Date.from(getMonday(startConstraint.withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn1 = Date.from(getFriday(startConstraint.withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());

		String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
		String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee";
		String equipmentName1 = "Test Equipment1";
		String equipmentName2 = "Test Equipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
		String equipmentTypeName1 = "TestType1";

		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
		Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType1);
		addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment2);


		List<MonthlyUsage> monthlyUsageList = controller.monthlyUsageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);

		LocalDateTime currentMonthStart = startConstraint;
		LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
		assertEquals(6, monthlyUsageList.size());
		int currentReservation = 1;
		for (MonthlyUsage currentMonthlyUsage : monthlyUsageList) {
			// Multiply by 2, because two pieces of equipment in type
			double workHoursInConstraints = workHoursInConstraints(currentMonthStart, currentMonthEnd) * 2;
			assertEquals(currentMonthStart.getMonth().name() + ", " + currentMonthStart.getYear(), currentMonthlyUsage.getMonth());
			if (currentReservation == 1) {
				assertEquals(10 * WORKDAY, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
			}
			else {
				
				assertEquals(0, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
			}
			
			currentMonthStart = currentMonthStart.plusMonths(1);
			currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
			currentReservation++;
		}
    }
    
    @Test
    public void testMonthlyUsageByType_OneEquipmentOneReservationEveryMonth() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeCode1 = 1111;
		int reservationTypeInUse = 0;

		ZoneId zoneId = ZoneId.systemDefault();

		// Set constraints to 1.1 - 30-6 of current year
		LocalDateTime startConstraint = LocalDate.now().withDayOfYear(1).atTime(0,0);
		LocalDateTime endConstraint = LocalDate.now().withDayOfYear(1).plusMonths(6).withDayOfMonth(1).minusDays(1).atTime(23,59);

		// Reservation lasts from the first Monday of January to the next Friday   
		Date dateTake1 = Date.from(getMonday(startConstraint.withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateTake2 = Date.from(getMonday(startConstraint.plusMonths(1).plusDays(7).withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateTake3 = Date.from(getMonday(startConstraint.plusMonths(2).plusDays(7).withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateTake4 = Date.from(getMonday(startConstraint.plusMonths(3).plusDays(7).withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateTake5 = Date.from(getMonday(startConstraint.plusMonths(4).plusDays(7).withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateTake6 = Date.from(getMonday(startConstraint.plusMonths(5).plusDays(7).withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn1 = Date.from(getFriday(startConstraint.withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn2 = Date.from(getFriday(startConstraint.plusMonths(1).plusDays(7).withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn3 = Date.from(getFriday(startConstraint.plusMonths(2).plusDays(7).withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn4 = Date.from(getFriday(startConstraint.plusMonths(3).plusDays(7).withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn5 = Date.from(getFriday(startConstraint.plusMonths(4).plusDays(7).withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn6 = Date.from(getFriday(startConstraint.plusMonths(5).plusDays(7).withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		
		String startConstraintStr = Long.toString(startConstraint.atZone(zoneId).toEpochSecond());
		String endConstraintStr = Long.toString(endConstraint.atZone(zoneId).toEpochSecond());
		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee";
		String equipmentName1 = "Test Equipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeName1 = "TestType1";

		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
		Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		
		addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake3, dateReturn3, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake4, dateReturn4, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake5, dateReturn5, testEmployee1, testEquipment1);
		addReservation(reservationTypeInUse, dateTake6, dateReturn6, testEmployee1, testEquipment1);

		List<MonthlyUsage> monthlyUsageList = controller.monthlyUsageByType(Integer.toString(equipmentTypeCode1), startConstraintStr, endConstraintStr);

		LocalDateTime currentMonthStart = startConstraint;
		LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
		assertEquals(6, monthlyUsageList.size());
		for (MonthlyUsage currentMonthlyUsage : monthlyUsageList) {

			double workHoursInConstraints = workHoursInConstraints(currentMonthStart, currentMonthEnd);
			assertEquals(currentMonthStart.getMonth().name() + ", " + currentMonthStart.getYear(), currentMonthlyUsage.getMonth());

			assertEquals(5 * WORKDAY, currentMonthlyUsage.getInUse(), 0);
			assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration()
					- currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
			assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
			assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
			
			currentMonthStart = currentMonthStart.plusMonths(1);
			currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
		}
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_noTypesNoEquipment() {
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeNoEquipment() {
    	int equipmentTypeCode1 = 1111;
    	String equipmentTypeName1 = "TestType1";
    	
    	addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    
    @Test
    public void testGetEquipmentTypesWithEquipment_twoTypesNoEquipment() {
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";
    	
    	addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeOneEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
		String equipmentName1 = "Test Equipment1";
		String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(1, equipmentTypes.size());
    	Equipmenttype DBEquipmentType = equipmentTypes.get(0);
    	assertEquals(equipmentTypeCode1, DBEquipmentType.getTypeCode());
    	assertEquals(equipmentTypeName1, DBEquipmentType.getTypeName());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeOneDisabledEquipment() {
    	int equipmentStatusDisabled = 0;
    	int equipmentTypeCode1 = 1111;
		String equipmentName1 = "Test Equipment1";
		String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusDisabled, testEquipmentType1);
    	
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeOneDisabledAndOneEnabledEquipment() {
    	int equipmentStatusDisabled = 0;
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
		String equipmentName1 = "Test Equipment1";
		String equipmentName2 = "Test Equipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addEquipment(equipmentName2, equipmentSerial2, equipmentStatusDisabled, testEquipmentType1);
    	
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(1, equipmentTypes.size());
    	Equipmenttype DBEquipmentType = equipmentTypes.get(0);
    	assertEquals(equipmentTypeCode1, DBEquipmentType.getTypeCode());
    	assertEquals(equipmentTypeName1, DBEquipmentType.getTypeName());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_twoTypesTwoEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int typesFound = 0;
		String equipmentName1 = "Test Equipment1";
		String equipmentName2 = "Test Equipment2";
		String equipmentSerial1 = "TestSerial1";
		String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmentType2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType2);
    	
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(2, equipmentTypes.size());
    	
    	for (Equipmenttype currentEquipmenttype : equipmentTypes) {
    		if (currentEquipmenttype.getTypeCode() == equipmentTypeCode1) {
    			assertEquals(equipmentTypeName1, currentEquipmenttype.getTypeName());
    			typesFound++;
    		}
    		else if(currentEquipmenttype.getTypeCode() == equipmentTypeCode2) {
    			assertEquals(equipmentTypeName2, currentEquipmenttype.getTypeName());
    			typesFound++;
    		}
    	}
    	assertEquals(2, typesFound);
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_twoTypesOneEnabledOneDisabledEquipment() {
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int equipmentStatusDisabled = 0;
    	int equipmentStatusEnabled = 1;
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmentType2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusDisabled, testEquipmentType1);
    	addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType2);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(1, equipmentTypes.size());
    	
    	Equipmenttype DBequipmenttype = equipmentTypes.get(0);
    	assertEquals(equipmentTypeCode2, DBequipmenttype.getTypeCode());
    	assertEquals(equipmentTypeName2, DBequipmenttype.getTypeName());
    }

    @Test
    public void testGetUsageBySerial_fullWeek() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    
    @Test
    public void testGetUsageBySerial_weekEndOverlap() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).plusDays(4).atZone(ZoneId.systemDefault()).toInstant()); 
    	
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(7*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_notFullWeek() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(3*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_notFullHoursAtTake() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).plusHours(4).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY - 4, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUageBySerial_notFullHoursAtTake() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).plusHours(4).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY - 4, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }

    @Test
    public void testGetUsageBySerial_notFullMinutesAtReturn() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusMinutes(45).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals((5*WORKDAY) - 0.75, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_noReturn() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = getFriday(LocalDate.now().atTime(23,59));
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, null, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(5*WORKDAY, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_twoReservations() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(30);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(30);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().minusDays(7).atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().minusDays(7).atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());    
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment1);
   	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals((10*WORKDAY), usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_calibration() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeCalibration = 1;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeCalibration, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(0, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(5*WORKDAY, usage.getCalibration(), 0);
    	assertEquals(0, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageBySerial_maintenance() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeMaintenance = 2;

    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 

    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeMaintenance, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	EquipmentUsage usage = controller.getUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	assertEquals(0, usage.getInUse(), 0);
    	assertEquals(workHoursInConstraints - usage.getInUse() - usage.getCalibration() - usage.getMaintenance(), usage.getAvailable(), 0);
    	assertEquals(0, usage.getCalibration(), 0);
    	assertEquals(5*WORKDAY, usage.getMaintenance(), 0);
    	assertEquals(equipmentSerial1, usage.getSerial());
    	assertEquals(equipmentName1, usage.getName());
    	assertEquals(equipmentStatusEnabled, usage.getStatus());
    	assertEquals(equipmentTypeCode1, usage.getEquipmenttype().getTypeCode());
    	assertEquals(equipmentTypeName1, usage.getEquipmenttype().getTypeName());
    }
    
    @Test
    public void testGetUsageByType_oneEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testGetUsageByType_twoEquipment() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment2);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(2, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
    		if (currentUsage.getSerial().equals(equipmentSerial1)) {
	        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
	        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
	        	assertEquals(0, currentUsage.getCalibration(), 0);
	        	assertEquals(0, currentUsage.getMaintenance(), 0);
	        	assertEquals(equipmentSerial1, currentUsage.getSerial());
	        	assertEquals(equipmentName1, currentUsage.getName());
	        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
	        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
	        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    		}
    		else if (currentUsage.getSerial().equals(equipmentSerial2)) {
	        	assertEquals(3*WORKDAY, currentUsage.getInUse(), 0);
	        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
	        	assertEquals(0, currentUsage.getCalibration(), 0);
	        	assertEquals(0, currentUsage.getMaintenance(), 0);
	        	assertEquals(equipmentSerial2, currentUsage.getSerial());
	        	assertEquals(equipmentName2, currentUsage.getName());
	        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
	        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
	        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    		}
    	}
    }
    
    @Test
    public void testGetUsageByType_oneEquipmentNoReservation() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testGetUsageByType_noEquipment() {
    	int equipmentTypeCode1 = 1111;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	String equipmentTypeName1 = "TestType1";

    	addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	    	
    	assertEquals(0, usageList.size());
    }
    
    @Test
    public void testGetUsageByType_noType() {
    	int equipmentTypeCode1 = 1111;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	    	
    	assertEquals(0, usageList.size());
    }
    
    @Test
    public void testGetUsageByType_twoTypes() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateTake2 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn2 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).minusDays(2).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment2";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentSerial2 = "TestSerial2";
    	String equipmentTypeName1 = "TestType1";
    	String equipmentTypeName2 = "TestType2";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipmenttype testEquipmentType2 = addEquipmenttype(equipmentTypeCode2, equipmentTypeName2);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	Equipment testEquipment2 = addEquipment(equipmentName2, equipmentSerial2, equipmentStatusEnabled, testEquipmentType2);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	addReservation(reservationTypeInUse, dateTake2, dateReturn2, testEmployee1, testEquipment2);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {    		
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testGetUsageByType_available() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";


    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }    
    
    @Test
    public void testGetUsageByType_inUse() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeInUse = 0;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(5*WORKDAY, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testGetUsageByType_calibration() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeMaintenance = 1;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeMaintenance, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(5*WORKDAY, currentUsage.getCalibration(), 0);
        	assertEquals(0, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    
    @Test
    public void testGetUsageByType_maintenance() {
    	int equipmentStatusEnabled = 1;
    	int equipmentTypeCode1 = 1111;
    	int reservationTypeCalibration = 2;
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	Date dateTake1 = Date.from(getMonday(LocalDate.now().atTime(STARTHOUR,STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
    	Date dateReturn1 = Date.from(getFriday(LocalDate.now().atTime(ENDHOUR,ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant()); 
    	String employeeId1 = "111111111";
    	String employeeName1 = "Test Employee";
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";

    	Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	addReservation(reservationTypeCalibration, dateTake1, dateReturn1, testEmployee1, testEquipment1);
    	
    	List<EquipmentUsage> usageList = controller.getUsageByType(equipmentTypeCode1, startConstraint, endConstraint);
    	double workHoursInConstraints = workHoursInConstraints(startConstraint, endConstraint);
    	
    	assertEquals(1, usageList.size());
    	for (EquipmentUsage currentUsage : usageList) {
        	assertEquals(0, currentUsage.getInUse(), 0);
        	assertEquals(workHoursInConstraints - currentUsage.getInUse() - currentUsage.getCalibration() - currentUsage.getMaintenance(), currentUsage.getAvailable(), 0);
        	assertEquals(0, currentUsage.getCalibration(), 0);
        	assertEquals(5*WORKDAY, currentUsage.getMaintenance(), 0);
        	assertEquals(equipmentSerial1, currentUsage.getSerial());
        	assertEquals(equipmentName1, currentUsage.getName());
        	assertEquals(equipmentStatusEnabled, currentUsage.getStatus());
        	assertEquals(equipmentTypeCode1, currentUsage.getEquipmenttype().getTypeCode());
        	assertEquals(equipmentTypeName1, currentUsage.getEquipmenttype().getTypeName());
    	}
    }
    
    @Test
    public void testGetMonthlyUsageBySerial_noSuchSerial() {
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(15);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(15);
    	String equipmentSerial1 = "TestSerial1";
    	assertNull(controller.getMonthlyUsageBySerial(equipmentSerial1, startConstraint, endConstraint));
    }
    
    @Test
    public void testGetMonthlyUsageBySerial_OneReservationOneMonth() {
		int equipmentStatusEnabled = 1;
		int equipmentTypeCode1 = 1111;
		int reservationTypeInUse = 0;

		// Set constraints to 1.1 - 30-6 of current year
		LocalDateTime startConstraint = LocalDate.now().withDayOfYear(1).atTime(0,0);
		LocalDateTime endConstraint = LocalDate.now().withDayOfYear(1).plusMonths(6).withDayOfMonth(1).minusDays(1).atTime(23,59);

		// Reservation lasts from the first Monday of January to the next Friday   
		Date dateTake1 = Date.from(getMonday(startConstraint.withHour(STARTHOUR).withMinute(STARTMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		Date dateReturn1 = Date.from(getFriday(startConstraint.withHour(ENDHOUR).withMinute(ENDMINUTE)).atZone(ZoneId.systemDefault()).toInstant());
		

		String employeeId1 = "111111111";
		String employeeName1 = "Test Employee";
		String equipmentName1 = "Test Equipment1";
		String equipmentSerial1 = "TestSerial1";
		String equipmentTypeName1 = "TestType1";

		Employee testEmployee1 = addEmployee(employeeId1, employeeName1);
		Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
		Equipment testEquipment1 = addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
		addReservation(reservationTypeInUse, dateTake1, dateReturn1, testEmployee1, testEquipment1);
		
		List<MonthlyUsage> monthlyUsageList = controller.getMonthlyUsageBySerial(equipmentSerial1, startConstraint, endConstraint);
		assertEquals(6, monthlyUsageList.size());
		LocalDateTime currentMonthStart = startConstraint;
		LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
		int months = 0;
		for (MonthlyUsage currentMonthlyUsage : monthlyUsageList) {
			double workHoursInConstraints = workHoursInConstraints(currentMonthStart, currentMonthEnd);
			assertEquals(currentMonthStart.getMonth().name() + ", " + currentMonthStart.getYear(), currentMonthlyUsage.getMonth());
			if (currentMonthlyUsage.getMonth().equals("JANUARY, " + startConstraint.getYear())) {
				assertEquals(5 * WORKDAY, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
				months++;
			}
			else {
				
				assertEquals(0, currentMonthlyUsage.getInUse(), 0);
				assertEquals(workHoursInConstraints - currentMonthlyUsage.getInUse() - currentMonthlyUsage.getCalibration() -
						currentMonthlyUsage.getMaintenance(), currentMonthlyUsage.getAvailable(), 0);
				assertEquals(0, currentMonthlyUsage.getCalibration(), 0);
				assertEquals(0, currentMonthlyUsage.getMaintenance(), 0);
			}
			
			currentMonthStart = currentMonthStart.plusMonths(1);
			currentMonthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59);
			months++;
		}
		assertEquals(monthlyUsageList.size(), months - 1);
    }
    
    // TODO: tbc
    
    
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
    
    public LocalDateTime getMonday(LocalDateTime originDate) {
    	return originDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));	
    }
    
    public LocalDateTime getFriday(LocalDateTime originDate) {
    	if (originDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || originDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
	    	return originDate.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
    	}
    	else {
	    	return originDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
    	}	
    }
    
    public static double workHoursInConstraints(LocalDateTime start, LocalDateTime end) {
    	int workDays = 0;
    	while (start.isBefore(end)) {
    		if (!start.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !start.getDayOfWeek().equals(DayOfWeek.SUNDAY))
    			workDays++;
    		start = start.plusDays(1);
    	}
    	return workDays * WORKDAY;
    }
    
        
    public static void main(String[] args) {
    	controller = new ChartController();
    	LocalDateTime startConstraint = LocalDate.now().atTime(0,0).minusDays(10);
    	LocalDateTime endConstraint = LocalDate.now().atTime(23,59).plusDays(10);
    	String startStr = startConstraint.getDayOfWeek() + "/" +  startConstraint.getDayOfMonth() + "/" + startConstraint.getMonthValue() + "-" + startConstraint.getYear();
    	String endStr = endConstraint.getDayOfWeek() + "/" +  endConstraint.getDayOfMonth() + "/" + endConstraint.getMonthValue() + "-" + endConstraint.getYear();
    	System.out.println(startStr + " - " + endStr);
    	double hours = workHoursInConstraints(startConstraint, endConstraint);
    	System.out.println("Hours: " + hours);
    	
    	
    	System.out.println("Controller Hours: " + controller.workHoursInRange(startConstraint, endConstraint));
    }
    
}
