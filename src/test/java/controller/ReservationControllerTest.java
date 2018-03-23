package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    	
    	empdao.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
    	edao.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
    	etdao.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
    	rdao.setProperties(properties.getProperty("testDBurl"), properties.getProperty("testDBuser"), properties.getProperty("testDBpassword"), properties.getProperty("testDBdriver"));
        
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
	
//	@Test
//	public void testGetAllReservations() {
//    	int equipmentStatusEnabled = 1;
//    	int equipmentTypeCode = 1111;
//    	int reservationType = 0;
//    	LocalDateTime currentDateTime =  LocalDateTime.now();
//    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
//    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    	String employeeName = "Test Employee";
//    	String employeeId = "123456789";
//    	String equipmentName = "TestEquipment";
//		String equipmentSerial = "TestSerial";
//		String equipmentTypeName = "TestType";
//		
//		Employee testEmployee = addEmployee(employeeId, employeeName);
//		Equipmenttype testEquipmentType = addEquipmenttype(equipmentTypeCode, equipmentTypeName);
//		Equipment testEquipment = addEquipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmentType);
//		Reservation testReservation = addReservation(reservationType, reservationType, dateTake, dateReturn, testEmployee, testEquipment);
//		
//		ReservationController controller = new ReservationController();
//		
//		List<Reservation> reservations = controller.getallreservations();
//		Reservation DBreservation = reservations.get(0);
//		assertEquals(employeeName, DBreservation.getEmployee().getName());
//	}
	
//    @Test
//    public void testTakeEquipment_employeeIdEmpty() throws ClientProtocolException, IOException  {
//    	int equipmentStatusEnabled = 1;
//    	int equipmentTypeCode = 1111;
//    	int reservationType = 0;
//    	LocalDateTime currentDateTime =  LocalDateTime.now();
//    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
//    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    	String employeeName = "Test Employee";
//    	String employeeId = "123456789";
//    	String equipmentName = "TestEquipment";
//		String equipmentSerial = "TestSerial";
//		String equipmentTypeName = "TestType";
//		String restUri = "http://localhost:8080/rest/take";
//		StringBuilder uriBuilder = new StringBuilder(); 
//		
//		Employee testEmployee = addEmployee(employeeId, employeeName);
//		Equipmenttype testEquipmentType = addEquipmenttype(equipmentTypeCode, equipmentTypeName);
//		Equipment testEquipment = addEquipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmentType);
//		Reservation testReservation = addReservation(reservationType, reservationType, dateTake, dateReturn, testEmployee, testEquipment);
//		
//		uriBuilder.append(restUri)
//			.append(restUri)
//			.append("?employeeId=" + employeeId)
//			.append("&serial=" + equipmentSerial)
//			.append("&reservationType=" + reservationType);
//		// Given
//		// String name = RandomStringUtils.randomAlphabetic( 8 );
//
////		 HttpUriRequest request = new HttpGet("http://localhost:8080/rest/test");
//		
//		HttpUriRequest request = new HttpGet("http://localhost:8080/rest/getallreservations");
////		HttpUriRequest request = new HttpGet(uriBuilder.toString());
//
//		// When
//		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
//
//		// Then
//		// assertEquals(HttpStatus.SC_NOT_FOUND,
//		// httpResponse.getStatusLine().getStatusCode());
//		Reservation reservation = retrieveResourceFromResponse(httpResponse, Reservation.class);
//		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
//		assertThat( "eugenp", Matchers.is( reservation.getEmployee().getName()));
//    }
	
//    @Test
//    public void testTakeEquipment_employeeIdEmpty() throws ClientProtocolException, IOException  {
//    	int equipmentStatusEnabled = 1;
//    	int equipmentTypeCode = 1111;
//    	int reservationType = 0;
//    	LocalDateTime currentDateTime =  LocalDateTime.now();
//    	Date dateTake = Date.from(currentDateTime.minusMonths(6).atZone(ZoneId.systemDefault()).toInstant()); 
//    	Date dateReturn = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    	String employeeName = "Test Employee";
//    	String employeeId = "123456789";
//    	String equipmentName = "TestEquipment";
//		String equipmentSerial = "TestSerial";
//		String equipmentTypeName = "TestType";
//		String restUri = "http://localhost:8080/rest/take";
//		StringBuilder uriBuilder = new StringBuilder(); 
//		
//		Employee testEmployee = addEmployee(employeeId, employeeName);
//		Equipmenttype testEquipmentType = addEquipmenttype(equipmentTypeCode, equipmentTypeName);
//		Equipment testEquipment = addEquipment(equipmentName, equipmentSerial, equipmentStatusEnabled, testEquipmentType);
//		Reservation testReservation = addReservation(reservationType, reservationType, dateTake, dateReturn, testEmployee, testEquipment);
//		
//		uriBuilder.append(restUri)
//			.append(restUri)
//			.append("?employeeId=" + employeeId)
//			.append("&serial=" + equipmentSerial)
//			.append("&reservationType=" + reservationType);
//		// Given
//		// String name = RandomStringUtils.randomAlphabetic( 8 );
//
////		 HttpUriRequest request = new HttpGet("http://localhost:8080/rest/test");
//		
//		HttpUriRequest request = new HttpGet("http://localhost:8080/rest/take?employeeId=123456789&serial=TestSerial&reservationType=0");
////		HttpUriRequest request = new HttpGet(uriBuilder.toString());
//
//		// When
//		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
//
//		// Then
//		// assertEquals(HttpStatus.SC_NOT_FOUND,
//		// httpResponse.getStatusLine().getStatusCode());
//		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
//    }
    
    
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
    public Reservation addReservation(int equipmentStatus, int reservationType, Date dateTake, Date dateReturn,
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
    
	public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {

		String jsonFromResponse = EntityUtils.toString(response.getEntity());
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(jsonFromResponse, clazz);
	}
}
