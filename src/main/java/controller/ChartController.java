package controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.EmployeeDao;
import model.Equipment;
import model.EquipmentDao;
import model.EquipmentUsage;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.MonthlyUsage;
import model.Reservation;
import model.ReservationDao;
import utils.PropertyUtils;

@RestController
public class ChartController {
	Properties properties = PropertyUtils.loadProperties();
	private final double WORKDAY = Double.parseDouble(properties.getProperty("WORKDAY"));
	private final int STARTHOUR = Integer.parseInt(properties.getProperty("STARTHOUR"));
	private final int STARTMINUTE = Integer.parseInt(properties.getProperty("STARTMINUTE"));
	private final int ENDHOUR = Integer.parseInt(properties.getProperty("ENDHOUR"));
	private final int ENDMINUTE = Integer.parseInt(properties.getProperty("ENDMINUTE"));

	ReservationDao rdao;
	EmployeeDao empdao;
	EquipmentDao edao;
	EquipmenttypeDao etdao;
	
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

	@RequestMapping("/rest/usageByType") 
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
	
	@RequestMapping("/rest/monthlyUsageByType")
	public List<MonthlyUsage> getMonthlyUsageByType(@RequestParam(value = "typeCode") String typeCode,
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
			List<MonthlyUsage> result = getMonthlyUsageByType(typeCode, start, end);

			return result;
		}
	}
	
	@RequestMapping("/rest/monthlyUsage")
	public List<MonthlyUsage> getMonthlyUsage(@RequestParam(value = "serial") String serial,
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
			
			List<MonthlyUsage> result = getMonthlyUsage(serial, start, end);
			return result;
		}
	}
	
	@RequestMapping("/rest/getEquipmentTypesWithEquipment")
	public List<Equipmenttype> getEquipmentTypesWithEquipment() {
		etdao = new EquipmenttypeDao();
		etdao.init();
		List<Equipmenttype> result = etdao.getEquipmentTypesWithEquipment();
		etdao.destroy();
		return result;
	}
	
	public List<EquipmentUsage> getUsageByType(String typeCode, Date start, Date end) {
		List<EquipmentUsage> usageList = new ArrayList<EquipmentUsage>();
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentOfType = edao.getEnabledByTypeCode(Integer.parseInt(typeCode));
		edao.destroy();
		
		for (Equipment eq : equipmentOfType) {
			EquipmentUsage currentUsage = getUsageBySerial(eq.getSerial(), start, end);
			usageList.add(currentUsage);
		}
		return usageList;
	}
	
	public EquipmentUsage getUsageBySerial(String serial, Date start, Date end) {
		edao = new EquipmentDao();
		rdao = new ReservationDao();
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
	
	public List<MonthlyUsage> getMonthlyUsageByType(String typeCode, Date start, Date end) {
		List<MonthlyUsage> usageByTypeMonthly = new ArrayList<MonthlyUsage>();
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentOfType = edao.getEnabledByTypeCode(Integer.parseInt(typeCode));
		edao.destroy();
		
		for (Equipment eq : equipmentOfType) {
			List<MonthlyUsage> currentEquipmentMonthlyUsage = getMonthlyUsage(eq.getSerial(), start, end);
			if (usageByTypeMonthly.size() == 0) {
				usageByTypeMonthly = currentEquipmentMonthlyUsage;
			}
				
			else {
				for (int i=0; i < currentEquipmentMonthlyUsage.size(); i++) {
					MonthlyUsage currentUsage = currentEquipmentMonthlyUsage.get(i);
					MonthlyUsage totalUsage = usageByTypeMonthly.get(i);
					totalUsage.setAvailable(totalUsage.getAvailable() + currentUsage.getAvailable());
					totalUsage.setInUse(totalUsage.getInUse() + currentUsage.getInUse());
					totalUsage.setCalibration(totalUsage.getCalibration() + currentUsage.getCalibration());
					totalUsage.setMaintenance(totalUsage.getMaintenance() + currentUsage.getMaintenance());
					usageByTypeMonthly.set(i, totalUsage);
				}
			}
		}
		return usageByTypeMonthly;
	}
	
	public List<MonthlyUsage> getMonthlyUsage(String serial, Date start, Date end) {
		List<MonthlyUsage> monthlyUsage = new ArrayList<MonthlyUsage>();
		
		// Convert Dates to LocalDateTime
		LocalDateTime startConstraint = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()); 
		LocalDateTime endConstraint = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
		
		// Set start constraint time to start at defined starting time of workday
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
			
			
			monthlyUsage.add(mUsage);
			// Moves start date to first the day of the month
			startCurrent = startCurrent.minusDays(startCurrent.getDayOfMonth() - 1);
			// Move dates one month ahead
			startCurrent = startCurrent.plusMonths(1);
			endCurrent = endCurrent.plusMonths(1);
		} while (startCurrent.isBefore(endConstraint));

		return monthlyUsage;
	}

	public double hoursInReservation(Reservation reservation, Date start, Date end) {
		double workhours = 0;
		
		// If reservation has no return date, set return date to end date
		if (reservation.getDateReturn() == null)
			reservation.setDateReturn(end);
		if (reservation.getDateTake().after(end) || reservation.getDateReturn().before(start))
			return 0;
		else {
			if (reservation.getDateTake().before(start))
				reservation.setDateTake(start);
			else if (reservation.getDateReturn().after(end))
				reservation.setDateReturn(end);
			
			workhours = workhoursInRange(reservation.getDateTake(), reservation.getDateReturn());
			
			return workhours;
		}
	}

	public double workhoursNotAtStartAndEnd(Date startDate, Date endDate) {
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
		workhours = (WORKDAY * 2) - ((double) (startMinutes + endMinutes) / 60);
		return workhours;
	}
	
	public double workhoursInRange(Date startDate, Date endDate) {
    	int workDays = 0;
    	LocalDateTime startLdt = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    	LocalDateTime endLdt = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
    	while (startLdt.isBefore(endLdt)) {
    		if (!startLdt.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !startLdt.getDayOfWeek().equals(DayOfWeek.SUNDAY))
    			workDays++;
    		startLdt = startLdt.plusDays(1);
    	}
    	return workDays * WORKDAY;
	}

	/*
	public double workhoursInRange(Date startDate, Date endDate) {
		double workhours = 0;
		//double workhoursBetween = 0;
		double workhoursAtStartAndEnd = 0;
		
//		int workdays = 0; // FOR DEBUGGING
		
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		if (start.isEqual(end)) {
			workhours = ((double) endDate.getTime() - (double) startDate.getTime()) / (60 * 60 * 1000);
//			System.out.println("One day: " + workhours);
			return workhours;
		}
//		System.out.println("Start: " + start.toString() + " End: " + end.toString());
//		long daysInBetween = ChronoUnit.DAYS.between(start, end);
//		System.out.println("Days between start & end: " + daysInBetween);

		LocalDate date = start;
		do {
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
				// System.out.println("Day: " + date.getDayOfWeek());
				workhours += WORKDAY;
//				workdays++;
			}
			date = date.plusDays(1);
		// Loop until date has passed end date
		} while (date.isBefore(end.plusDays(1)));

		// Substract the work hours for start and end day
		//workhoursBetween -= WORKDAY * 2;

//		System.out.println("Workdays: " + workdays);
//		System.out.println("Workhours between start and end: " + workhours);

		workhoursAtStartAndEnd = workhoursNotAtStartAndEnd(startDate, endDate);
//		System.out.println("Workhours not at start & end: " + workhoursAtStartAndEnd);
		workhours -= workhoursAtStartAndEnd;
//		System.out.println("Total workhours: " + workhours);
		return workhours;
	}
	*/
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
}

	