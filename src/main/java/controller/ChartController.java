package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Equipment;
import model.EquipmentDao;
import model.EquipmentUsage;
import model.Reservation;
import model.ReservationDao;

@RestController
public class ChartController {
	// Length of one workday in hours
	private final double WORKDAY = 7.5;
	
	@RequestMapping("/rest/chartTest")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "{\"id\":\"hello\"}";
	}

	@RequestMapping("rest/usageByType") 
	public List<EquipmentUsage> usageByType(@RequestParam(value = "type") String type,
			@RequestParam(value = "start") String startStr,
			@RequestParam(value = "end") String endStr) {
		if (type == null || type.isEmpty()) {
    		throw new IllegalArgumentException("Equipment type must not be empty");
    	}
		else if (startStr == null || startStr.isEmpty()) {
    		throw new IllegalArgumentException("Start date must not be empty");
    	}
		else if (endStr == null || endStr.isEmpty()) {
    		throw new IllegalArgumentException("End date must not be empty");
    	}		
		else {
			List<EquipmentUsage> usageList = new ArrayList<EquipmentUsage>();
			
			EquipmentDao edao = new EquipmentDao();
			edao.init();
			List<Equipment> equipmentOfType = edao.getByType(Integer.parseInt(type));
			edao.destroy();
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			
			for (Equipment eq : equipmentOfType) {
				EquipmentUsage currentUsage = usageBySerial(eq.getSerial(), start, end);
				usageList.add(currentUsage);
			}
			
			return usageList;
		}
	}
	
	@RequestMapping("/rest/usageBySerial")
	public EquipmentUsage usageBySerial2(@RequestParam(value = "serial") String serial,
			@RequestParam(value = "start") String startStr,
			@RequestParam(value = "end") String endStr) {
		
		if (serial == null || serial.isEmpty()) {
    		throw new IllegalArgumentException("Serial number must not be empty");
    	}
		else if (startStr == null || startStr.isEmpty()) {
    		throw new IllegalArgumentException("Start date must not be empty");
    	}
		else if (endStr == null || endStr.isEmpty()) {
    		throw new IllegalArgumentException("End date must not be empty");
    	}		
		else {
			EquipmentDao edao = new EquipmentDao();
			ReservationDao rdao = new ReservationDao();
			edao.init();
			Equipment equipment = edao.getBySerial(serial);
			EquipmentUsage usage = new EquipmentUsage(equipment);
			List<Reservation> reservationsInRange = null;
			
			// Parse dates from epoch to Date
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			edao.destroy();
			rdao.destroy();

			reservationsInRange = rdao.getBySerialAndDate(serial, start, end);
			
			for (Reservation currentReservation : reservationsInRange) {
				double hours = 0;
				hours = hoursInReservation(currentReservation, start, end);
				switch (currentReservation.getReservationType()) {
					case 0:
						usage.setInUse(hours);
						break;
					case 1:
						usage.setCalibration(hours);
						break;
					case 2:
						usage.setMaintenance(hours);
						break;
				}
			}
			double available = workhoursInRange(start, end) - usage.getInUse() - usage.getCalibration() - usage.getMaintenance();			
			usage.setAvailable(available);

			return usage;
		}
	}
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
	public EquipmentUsage usageBySerial(String serial, Date start, Date end) {
		EquipmentDao edao = new EquipmentDao();
		ReservationDao rdao = new ReservationDao();
		edao.init();
		rdao.init();
		Equipment equipment = edao.getBySerial(serial);
		EquipmentUsage usage = new EquipmentUsage(equipment);
		List<Reservation> reservationsInRange = null;
		reservationsInRange = rdao.getBySerialAndDate(serial, start, end);
		
		for (Reservation currentReservation : reservationsInRange) {
			double hours = 0;
			hours = hoursInReservation(currentReservation, start, end);
			switch (currentReservation.getReservationType()) {
				case 0:
					usage.setInUse(hours);
					break;
				case 1:
					usage.setCalibration(hours);
					break;
				case 2:
					usage.setMaintenance(hours);
					break;
			}
		}
		double available = workhoursInRange(start, end) - usage.getInUse() - usage.getCalibration() - usage.getMaintenance();			
		usage.setAvailable(available);
		edao.destroy();
		rdao.destroy();
		return usage;
	}
	
	public double hoursInReservation(Reservation reservation, Date start, Date end) {
		double workhours = 0;
		if (reservation.getDateTake().after(end) || reservation.getDateReturn().before(start))
			return 0;
		else {
			if (reservation.getDateTake().before(start))
				reservation.setDateTake(start);
			if (reservation.getDateReturn().after(end))
				reservation.setDateReturn(end);
			
			workhours = workhoursInRange(reservation.getDateTake(), reservation.getDateReturn());
			
			return workhours;
		}
	}

	public double workhoursInStartAndEnd(Date startDate, Date endDate) {
		double workhours = 0;
		long startMinutes = 0;
		long endMinutes = 0;
		long resultMilliseconds;

		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Calendar cal = Calendar.getInstance();

		long daysInBetween = ChronoUnit.DAYS.between(start, end);

		if (daysInBetween > 0) {
			// Set workday to end at 16:00 at startDate
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 16);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			resultMilliseconds = cal.getTime().getTime() - startDate.getTime();
			startMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);

			// Set workday to start at 08:30 at endDate
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 8);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			resultMilliseconds = endDate.getTime() - cal.getTime().getTime();
			endMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);	
		}
		else {
			resultMilliseconds = endDate.getTime() - startDate.getTime();
			endMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);
		}
		workhours = (double) (startMinutes + endMinutes) / 60;
		return workhours;
	}

	public double workhoursInRange(Date startDate, Date endDate) {
		double workhours = 0;
		double workhoursBetween = 0;
		double workhoursAtStartAndEnd = 0;
		
		int workdays = 0; // FOR DEBUGGING
		
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		long daysInBetween = ChronoUnit.DAYS.between(start, end);
		
		if (daysInBetween > 0) {
			for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
				DayOfWeek dayOfWeek = date.getDayOfWeek();
				if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
					// System.out.println("Day: " + date.getDayOfWeek());
					workhoursBetween += WORKDAY;
					workdays++;
				}
			}
			// Substract the work hours for start and end day
			workhoursBetween -= 15;
		}
		System.out.println("Workdays: " + workdays);
		System.out.println("Workhours between start and end: " + workhoursBetween);
		workhoursAtStartAndEnd = workhoursInStartAndEnd(startDate, endDate);
		System.out.println("Workhours at start and end: " + workhoursAtStartAndEnd);
		workhours = workhoursBetween + workhoursAtStartAndEnd;
		
		return workhours;
	}

	/*
	public List<Reservation> reservationsInRange(List<Reservation> reservations, Date startDate, Date endDate) {
		List<Reservation> reservationsInRange = new ArrayList<Reservation>();
		for (Reservation currentReservation : reservations) {
			if ((currentReservation.getDateTake().after(startDate)
					|| currentReservation.getDateReturn().after(startDate))
					&& currentReservation.getDateReturn().before(endDate)) {
				reservationsInRange.add(currentReservation);
			}
		}
		return reservationsInRange;
	}
	*/

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String startString = "1-1-2018 08:30:00";
		String endString = "1-06-2018 16:00:00";
		Date start = null;
		Date end = null;

		try {
			start = sdf.parse(startString);
			end = sdf.parse(endString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ChartController cc = new ChartController();

		ReservationDao rdao = new ReservationDao();
		rdao.init();
		rdao.initialize(4);
		Reservation r = rdao.getDao();
		rdao.destroy();
		System.out.println("WorkHoursInRange: " + cc.hoursInReservation(r, start, end));
	}
}
