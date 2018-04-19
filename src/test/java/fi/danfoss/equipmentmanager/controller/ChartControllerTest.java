package fi.danfoss.equipmentmanager.controller;

import static org.junit.Assert.assertEquals;

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
import fi.danfoss.equipmentmanager.model.Reservation;
import fi.danfoss.equipmentmanager.model.ReservationDao;
import fi.danfoss.equipmentmanager.utils.PropertyUtils;

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
    public void testGetEquipmentTypesWithEquipment_noTypes() {
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_noTypesWithEquipment() {
    	int equipmentTypeCode1 = 1111;
    	String equipmentTypeName1 = "TestType1";
    	addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeWithEnabledEquipment() {
    	int equipmentTypeCode1 = 1111;
    	int equipmentStatusEnabled = 1;
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusEnabled, testEquipmentType1);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(1, equipmentTypes.size());
    	Equipmenttype DBequipmenttype = equipmentTypes.get(0);
    	assertEquals(equipmentTypeCode1, DBequipmenttype.getTypeCode());
    	assertEquals(equipmentTypeName1, DBequipmenttype.getTypeName());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_twoTypesWithEnabledEquipment() {
    	int equipmentTypeCode1 = 1111;
    	int equipmentTypeCode2 = 2222;
    	int equipmentStatusEnabled = 1;
    	String equipmentName1 = "Test Equipment1";
    	String equipmentName2 = "Test Equipment1";
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
    			assertEquals(equipmentTypeCode1, currentEquipmenttype.getTypeCode());
    			assertEquals(equipmentTypeName1, currentEquipmenttype.getTypeName());
    		}
    		else if (currentEquipmenttype.getTypeCode() == equipmentTypeCode2) {
    			assertEquals(equipmentTypeCode2, currentEquipmenttype.getTypeCode());
    			assertEquals(equipmentTypeName2, currentEquipmenttype.getTypeName());
    		}
    	}
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_oneTypeWithDisabledEquipment() {
    	int equipmentTypeCode1 = 1111;
    	int equipmentStatusDisabled = 0;
    	String equipmentName1 = "Test Equipment1";
    	String equipmentSerial1 = "TestSerial1";
    	String equipmentTypeName1 = "TestType1";
    	
    	Equipmenttype testEquipmentType1 = addEquipmenttype(equipmentTypeCode1, equipmentTypeName1);
    	addEquipment(equipmentName1, equipmentSerial1, equipmentStatusDisabled, testEquipmentType1);
    	List<Equipmenttype> equipmentTypes = controller.getEquipmentTypesWithEquipment();
    	assertEquals(0, equipmentTypes.size());
    }
    
    @Test
    public void testGetEquipmentTypesWithEquipment_twoTypesWithEnabledAndDisabledEquipment() {
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
