package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class ReservationTest {
	
	ReservationDao rdao = new ReservationDao();
	private Reservation reservation = new Reservation();
	/*
	@Test
	public void reservationsFound() {
		List<Reservation> reservations = rdao.getAll();
		assertEquals(reservations.size(), 167);
		//assertThat(EquipmentDataReader.readEquipmentFromFile("test_files/nosuchfile.txt"),  containsString("Equipment file not found:"));
		rdao.destroy();
	}
	*/
	
	/*
	public static Reservation generateRandomOpenReservation() {
		EmployeeDao empdao = new EmployeeDao();
		EquipmentDao eqdao = new EquipmentDao();
		ReservationDao rdao = new ReservationDao();
		empdao.init();
		eqdao.init();
		rdao.init();
		
		Employee testEmployee = empdao.getEmployeeByEmployeeId("999999999");
		
		// Declare DateTimeFormatter with desired format
//		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		// Save current LocalDateTime into a variable
	    LocalDateTime localDateTime = LocalDateTime.now();
	    
//	    Date currentDate = new Date();
//	    LocalDateTime ldt = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());

	    Random random = new Random();
		Reservation testReservation = new Reservation();
		Equipment testEquipment = eqdao.getRandomAvailable();
		
	    //Get random amount of days between 1 and 180
		int takeDateRandom = random.nextInt(359) + 1;
		int returnDateRandom = random.nextInt(358-takeDateRandom) + 1;
		
		Date takeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(takeDateRandom).toInstant());
		
		Date returnDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(returnDateRandom).toInstant());
		
		testReservation.setDateTake(takeDate);
		testReservation.setDateReturn(returnDate);
		testReservation.setEquipment(testEquipment);
		testReservation.setEmployee(testEmployee);
		testReservation.setReservationType(random.nextInt(3));
		rdao.persist(testReservation);
		
		empdao.destroy();
		eqdao.destroy();
		rdao.destroy();
		
		return testReservation;
	}
	
	public static void main(String[] args) {
		for (int i=0; i<50; i++) {
			generateRandomOpenReservation();
		}
	}
	 */
}
