package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
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
import model.EquipmentUsageMonth;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.MonthlyUsage;
import model.Reservation;
import model.ReservationDao;

@RestController
public class ChartController {
	// Length of one workday in hours
	private final double WORKDAY = 7.5;
	private final int STARTHOUR = 8;
	private final int STARTMINUTE = 30;
	private final int ENDHOUR = 16;
	private final int ENDMINUTE = 0;
	
	@RequestMapping("/rest/chartTest")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "{\"id\":\"hello\"}";
	}

	@RequestMapping("rest/usageByType") 
	public List<EquipmentUsage> usageByType(@RequestParam(value = "typeCode") String typeCode,
			@RequestParam(value = "start") String startStr,
			@RequestParam(value = "end") String endStr) {
		if (typeCode == null || typeCode.isEmpty()) {
    		throw new IllegalArgumentException("Equipment type must not be empty");
    	}
		else if (startStr == null || startStr.isEmpty()) {
    		throw new IllegalArgumentException("Start date must not be empty");
    	}
		else if (endStr == null || endStr.isEmpty()) {
    		throw new IllegalArgumentException("End date must not be empty");
    	}		
		else {
			// Parse dates from epoch to Date
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			List<EquipmentUsage> result = getUsageByType(typeCode, start, end);
			
			return result;
		}
	}
	
	@RequestMapping("/rest/usageBySerial")
	public EquipmentUsage usageBySerial(@RequestParam(value = "serial") String serial,
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
			// Parse dates from epoch to Date
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			EquipmentUsage result = getUsageBySerial(serial, start, end);
				
			return result;
		}
	}
	
	@RequestMapping("/rest/usageByMonthType")
	public List<MonthlyUsage> usageByMonthType(@RequestParam(value = "typeCode") String typeCode,
			@RequestParam(value = "start") String startStr,
			@RequestParam(value = "end") String endStr) {
		if (typeCode == null || typeCode.isEmpty()) {
    		throw new IllegalArgumentException("Equipment type must not be empty");
    	}
		else if (startStr == null || startStr.isEmpty()) {
    		throw new IllegalArgumentException("Start date must not be empty");
    	}
		else if (endStr == null || endStr.isEmpty()) {
    		throw new IllegalArgumentException("End date must not be empty");
    	}		
		else {
			// Parse dates from epoch to Date
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			List<MonthlyUsage> result = new ArrayList<MonthlyUsage>();
			
			EquipmentDao edao = new EquipmentDao();
			edao.init();
			List<Equipment> equipmentList = edao.getByType(Integer.parseInt(typeCode));
			edao.destroy();
			
			for (Equipment eq : equipmentList) {
				List<MonthlyUsage> equipmentUsage = getUsageByMonth(eq.getSerial(), start, end);
				if (result.size() == 0) {
					System.out.println("Creating first data set...");
					result = equipmentUsage;
				}
					
				else {
					for (int i=0; i < equipmentUsage.size(); i++) {
						MonthlyUsage currentUsage = equipmentUsage.get(i);
						MonthlyUsage totalUsage = result.get(i);
						currentUsage.setAvailable(totalUsage.getAvailable() + currentUsage.getAvailable());
						currentUsage.setInUse(totalUsage.getInUse() + currentUsage.getInUse());
						currentUsage.setCalibration(totalUsage.getCalibration() + currentUsage.getCalibration());
						currentUsage.setMaintenance(totalUsage.getMaintenance() + currentUsage.getMaintenance());
						result.set(i, currentUsage);
					}
				}
			}
			return result;
		}
	}
	
	@RequestMapping("/rest/usageByMonth")
	public List<MonthlyUsage> usageByMonth(@RequestParam(value = "serial") String serial,
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
			// Parse dates from epoch to Date
			Date start = new Date(Long.parseLong(startStr) * 1000);
			Date end = new Date(Long.parseLong(endStr) * 1000);
			
			List<MonthlyUsage> result = getUsageByMonth(serial, start, end);
			return result;
		}
	}
	
	@RequestMapping("/rest/getEquipmentTypes")
	public List<Equipmenttype> getEquipmentTypes() {
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		etdao.init();
		List<Equipmenttype> result = etdao.getEquipmentTypesWithEquipment();
		etdao.destroy();
		return result;
	}
	
	public List<EquipmentUsage> getUsageByType(String typeCode, Date start, Date end) {
		List<EquipmentUsage> usageList = new ArrayList<EquipmentUsage>();
		EquipmentDao edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentOfType = edao.getByType(Integer.parseInt(typeCode));
		
		for (Equipment eq : equipmentOfType) {
			EquipmentUsage currentUsage = getUsageBySerial(eq.getSerial(), start, end);
			usageList.add(currentUsage);
		}
		
		edao.destroy();
		return usageList;
	}
	
	public EquipmentUsage getUsageBySerial(String serial, Date start, Date end) {
		EquipmentDao edao = new EquipmentDao();
		ReservationDao rdao = new ReservationDao();
		edao.init();
		rdao.init();
		
		Equipment equipment = edao.getBySerial(serial);
		EquipmentUsage usage = new EquipmentUsage(equipment);
		
		List<Reservation> reservationsInRange = null;
		reservationsInRange = rdao.getBySerialAndDate(serial, start, end);
		edao.destroy();
		rdao.destroy();
		
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
	
	public List<MonthlyUsage> getUsageByMonth(String serial, Date start, Date end) {
		List<MonthlyUsage> usageByMonth = new ArrayList<MonthlyUsage>();
		
		LocalDateTime startConstraint = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()); 
		LocalDateTime endConstraint = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
		
//		LocalDateTime startCurrent = startConstraint.with(startConstraint.plusHours(STARTHOUR).plusMinutes(STARTMINUTE));
//		LocalDateTime endCurrent = startConstraint.with(startConstraint.plusMonths(1).withDayOfMonth(1).minusDays(1).plusHours(ENDHOUR).plusMinutes(ENDMINUTE));
		
		LocalDateTime startCurrent = startConstraint.plusHours(STARTHOUR).plusMinutes(STARTMINUTE);
		LocalDateTime endCurrent = startConstraint.with(startConstraint.plusMonths(1).withDayOfMonth(1).minusDays(1));
		
		
		do {
			// For the last month in constraints
			if (endCurrent.isAfter(endConstraint)) {
				endCurrent = endConstraint;
				endCurrent = endCurrent.minusHours(endCurrent.getHour()).minusMinutes(endCurrent.getMinute());
				endCurrent = endCurrent.plusHours(ENDHOUR).plusMinutes(ENDMINUTE);
			}
			// Moves end date to the last day of the month
			else { 
				endCurrent = endCurrent.plusMonths(1).withDayOfMonth(1).minusDays(1);
				endCurrent = endCurrent.minusHours(endCurrent.getHour()).minusMinutes(endCurrent.getMinute());
				endCurrent = endCurrent.plusHours(ENDHOUR).plusMinutes(ENDMINUTE);
			}
			
		
			System.out.println("StartCurrent: " + startCurrent.getHour() + ":" + startCurrent.getMinute() + ":" + startCurrent.getSecond() + " " + startCurrent.getDayOfMonth() + "/" + startCurrent.getMonthValue() + "/" + startCurrent.getYear());
			System.out.println("EndCurrent:   " + endCurrent.getHour() + ":" + endCurrent.getMinute() + ":" + endCurrent.getSecond() + " " + endCurrent.getDayOfMonth() + "/" + endCurrent.getMonthValue() + "/" + endCurrent.getYear());
			
			
			
			EquipmentUsage eUsage = getUsageBySerial(serial, 
					Date.from(startCurrent.atZone(ZoneId.systemDefault()).toInstant()), 
					Date.from(endCurrent.atZone(ZoneId.systemDefault()).toInstant()));
			 
			MonthlyUsage mUsage = new MonthlyUsage();
			// Create month name & year for chart label
			StringBuilder sb = new StringBuilder();
			sb.append(startCurrent.getMonth().name() + ", " + endCurrent.getYear());
			// Set data to MonthlyUsage object
			mUsage.setMonth(sb.toString());
			mUsage.setAvailable(eUsage.getAvailable());
			mUsage.setCalibration(eUsage.getCalibration());
			mUsage.setInUse(eUsage.getInUse());
			mUsage.setMaintenance(eUsage.getMaintenance());
			
			
			usageByMonth.add(mUsage);
			// Moves start date to first the day of the month
			startCurrent = startCurrent.minusDays(startCurrent.getDayOfMonth() - 1);
			// Move dates one month ahead
			startCurrent = startCurrent.plusMonths(1);
			endCurrent = endCurrent.plusMonths(1);
		} while (startCurrent.isBefore(endConstraint));
				/*
		do {			
			System.out.println("StartCurrent: " + startCurrent.toString());
			System.out.println("EndCurrent: " + endCurrent.toString());
			
			Date startSearch = Date.from(startCurrent.atZone(ZoneId.systemDefault()).toInstant()); 
			Date endSearch = Date.from(endCurrent.atZone(ZoneId.systemDefault()).toInstant());
			
			
			
			Calendar cal =  Calendar.getInstance();
			cal.setTime(startSearch);

			cal.setTime(endSearch);
			
			EquipmentUsage eUsage = getUsageBySerial(serial, startSearch, endSearch);
			MonthlyUsage mUsage = new MonthlyUsage();
			
			// Create month name & year for chart label
			StringBuilder sb = new StringBuilder();
			sb.append(startCurrent.getMonth().name() + ", " + endCurrent.getYear());
			mUsage.setMonth(sb.toString());
			
			double available = workhoursInRange(startSearch, endSearch) - mUsage.getInUse() - mUsage.getCalibration() - mUsage.getMaintenance();			
			mUsage.setAvailable(available);
			mUsage.setInUse(eUsage.getInUse());
			mUsage.setMaintenance(eUsage.getMaintenance());
			mUsage.setCalibration(eUsage.getCalibration());
			
			usageByMonth.add(mUsage);
			
			startCurrent = startCurrent.plusMonths(1);
			System.out.println("Current month: " + startCurrent.getMonthValue());
			if (startConstraint.getMonthValue() == 1) {
				System.out.println("Testing.......");
				startCurrent = startCurrent.plusYears(1);
			}
			endCurrent = startCurrent.with(startConstraint.plusMonths(1).withDayOfMonth(1).minusDays(1));
			
			
		} while (startCurrent.isBefore(endConstraint));
		*/
		return usageByMonth;
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
			
//			System.out.println("Reservation: " + reservation.getReservationId() + " workhours: " + workhours);
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
			cal.set(Calendar.HOUR_OF_DAY, ENDHOUR);
			cal.set(Calendar.MINUTE, ENDMINUTE);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			resultMilliseconds = cal.getTime().getTime() - startDate.getTime();
			startMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);

			// Set workday to start at 08:30 at endDate
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, STARTHOUR);
			cal.set(Calendar.MINUTE, STARTMINUTE);
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
		System.out.println("Start: " + start.toString() + " End: " + end.toString());
		long daysInBetween = ChronoUnit.DAYS.between(start, end);
		System.out.println("Days between start & end: " + daysInBetween);
		
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
			workhoursBetween -= WORKDAY * 2;
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
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String startString = "15-12-2017 00:00:00";
		String endString = "25-12-2017 00:00:00";
		Date start = null;
		Date end = null;

		try {
			start = sdf.parse(startString);
			end = sdf.parse(endString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		rdao.initialize(7);
		Reservation res = rdao.getDao();
		rdao.destroy();

		ChartController cc = new ChartController();
		//double hours = cc.hoursInReservation(res, start, end);
		cc.getUsageByMonth("MI_08/2007", start, end);

		//List<MonthlyUsage> musage = cc.getUsageByMonth("MI_09A0364", start, end);
	}
}
