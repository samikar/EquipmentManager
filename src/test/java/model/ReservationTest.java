package model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class ReservationTest {
	
	private Reservation reservation = new Reservation();
	
	public static void generateMockReservations(int count) {
		EmployeeDao empdao = new EmployeeDao();
		EquipmentDao eqdao = new EquipmentDao();
		ReservationDao rdao = new ReservationDao();
		empdao.init();
		eqdao.init();
		rdao.init();
		
		empdao.initialize(1);
		Employee testEmployee = empdao.getDao();
		
		// Declare DateTimeFormatter with desired format
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		// Save current LocalDateTime into a variable
	    LocalDateTime localDateTime = LocalDateTime.now();
	    
	    Date currentDate = new Date();
	    LocalDateTime ldt = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());

	    Random random = new Random();
		
		for (int i=0; i<count; i++) {
			Reservation testReservation = new Reservation();
			Equipment testEquipment = eqdao.getRandomAvailable();
			
		    //Get random amount of days between 1 and 180
			int randomAmountOfDays1 = random.nextInt(179) + 1;
			int randomAmountOfDays2 = random.nextInt(179) + 1;
			
			Date date1 = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(randomAmountOfDays1).toInstant());
			Date date2 = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(randomAmountOfDays2).toInstant());
			
			if (date1.before(date2)) {
				testReservation.setDateTake(date1);
				testReservation.setDateReturn(date2);
			}
			else if (date2.before(date1)) {
				testReservation.setDateTake(date2);
				testReservation.setDateReturn(date1);
			}
			else {
				date1 = Date.from(localDateTime.atZone(ZoneId.systemDefault()).minusDays(randomAmountOfDays1 + 5).toInstant());
				testReservation.setDateTake(date1);
				testReservation.setDateReturn(date2);
			}
			
			int type = random.nextInt(2);
			
			testReservation.setEquipment(testEquipment);
			testReservation.setEmployee(testEmployee);
			testReservation.setReservationType(type);
			rdao.persist(testReservation);
		}
	}

	
	public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
	
	public static void main(String[] args) {
		generateMockReservations(90);
	}

}
