package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class ReservationTest {
	
	private Reservation reservation = new Reservation();
	
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
		int randomAmountOfDays = random.nextInt(179) + 1;
		
		Date takeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(randomAmountOfDays).toInstant());
		
		testReservation.setDateTake(takeDate);
	
		testReservation.setEquipment(testEquipment);
		testReservation.setEmployee(testEmployee);
		testReservation.setReservationType(random.nextInt(3));
		rdao.persist(testReservation);
		
		empdao.destroy();
		eqdao.destroy();
		rdao.destroy();
		
		return testReservation;
	}

	
	public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
	
	public static void main(String[] args) {
		//generateRandomOpenReservation();
	}

}
