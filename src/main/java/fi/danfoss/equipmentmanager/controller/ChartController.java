package fi.danfoss.equipmentmanager.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	final static Logger logger = Logger.getLogger(ChartController.class);
		
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
			// Parse epoch strings to LocalDateTime
			LocalDateTime start = epochToLocalDateTime(Long.parseLong(startStr));
			LocalDateTime end = epochToLocalDateTime(Long.parseLong(endStr));			
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
			LocalDateTime start = epochToLocalDateTime(Long.parseLong(startStr));
			LocalDateTime end = epochToLocalDateTime(Long.parseLong(endStr));
			List<EquipmentUsage> result = getUsageByType(Integer.parseInt(typeCode), start, end);
			
			return result;
		}
	}
	
	@RequestMapping("/rest/monthlyUsageByType")
	public List<MonthlyUsage> monthlyUsageByType(@RequestParam(value = "typeCode") String typeCode,
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
			LocalDateTime start = epochToLocalDateTime(Long.parseLong(startStr));
			LocalDateTime end = epochToLocalDateTime(Long.parseLong(endStr));
			List<MonthlyUsage> result = getMonthlyUsageByType(typeCode, start, end);

			return result;
		}
	}
	
	/*
	@RequestMapping("/rest/monthlyUsageBySerial")
	public List<MonthlyUsage> monthlyUsageBySerial(@RequestParam(value = "serial") String serial,
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
	*/
	
	@RequestMapping("/rest/getEquipmentTypesWithEquipment")
	public List<Equipmenttype> getEquipmentTypesWithEquipment() {
		etdao = new EquipmenttypeDao();
		etdao.init();
		List<Equipmenttype> result = etdao.getEquipmentTypesWithEquipment();
		etdao.destroy();
		return result;
	}
	
	public EquipmentUsage getUsageBySerial(String serial, LocalDateTime start, LocalDateTime end) {
		Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
		
		edao = new EquipmentDao();
		rdao = new ReservationDao();
		edao.init();
		rdao.init();
		
		if (!edao.serialExists(serial)) {
			edao.destroy();
			rdao.destroy();
			return null;
		}
		
		Equipment equipment = edao.getBySerial(serial);
		EquipmentUsage usage = new EquipmentUsage(equipment);
		
		List<Reservation> reservationsInRange = null;
		reservationsInRange = rdao.getBySerialAndDate(serial, startDate, endDate);
		edao.destroy();
		rdao.destroy();
		
		for (Reservation currentReservation : reservationsInRange) {
			double hours = 0;
			hours = hoursInReservation(currentReservation, start, end);
			
			switch (currentReservation.getReservationType()) {
				case 0:
					usage.setInUse(usage.getInUse() + hours);
					break;
				case 1:
					usage.setCalibration(usage.getCalibration() + hours);
					break;
				case 2:
					usage.setMaintenance(usage.getMaintenance() + hours);
					break;
			}
		}
		double available = workHoursInRange(start, end) - usage.getInUse() - usage.getCalibration() - usage.getMaintenance();			
		usage.setAvailable(available);

		return usage;
	}
	
	public List<EquipmentUsage> getUsageByType(int typeCode, LocalDateTime start, LocalDateTime end) {
		List<EquipmentUsage> usageList = new ArrayList<EquipmentUsage>();
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentOfType = edao.getEnabledByTypeCode(typeCode);
		edao.destroy();
		
		for (Equipment eq : equipmentOfType) {
			EquipmentUsage currentUsage = getUsageBySerial(eq.getSerial(), start, end);
			usageList.add(currentUsage);
		}
		return usageList;
	}
		
	public List<MonthlyUsage> getMonthlyUsageBySerial(String serial, LocalDateTime startConstraint, LocalDateTime endConstraint) {		
		List<MonthlyUsage> monthlyUsage = new ArrayList<MonthlyUsage>();
		
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
			
			edao.init();
			if (!edao.serialExists(serial)) {
				edao.destroy();
				return null;
			}
			edao.destroy();
			
			EquipmentUsage eUsage = getUsageBySerial(serial, startCurrent, endCurrent);
			 
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
	
	public List<MonthlyUsage> getMonthlyUsageByType(String typeCode, LocalDateTime startConstraint, LocalDateTime endConstraint) {		
		List<MonthlyUsage> usageByTypeMonthly = new ArrayList<MonthlyUsage>();
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentOfType = edao.getEnabledByTypeCode(Integer.parseInt(typeCode));
		edao.destroy();
		
		for (Equipment eq : equipmentOfType) {
			List<MonthlyUsage> currentEquipmentMonthlyUsage = getMonthlyUsageBySerial(eq.getSerial(), startConstraint, endConstraint);
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

	public double hoursInReservation(Reservation reservation, LocalDateTime start, LocalDateTime end) {
		double workHours = 0;
		Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
		
		// If reservation has no return date, set return date to constraint end date
		if (reservation.getDateReturn() == null) {
			reservation.setDateReturn(endDate);
		}
		if (reservation.getDateTake().after(endDate) || reservation.getDateReturn().before(startDate))
			return 0;
		else {
			if (reservation.getDateTake().before(startDate))
				reservation.setDateTake(startDate);
			else if (reservation.getDateReturn().after(endDate))
				reservation.setDateReturn(endDate);
			LocalDateTime takeLDT = LocalDateTime.ofInstant(reservation.getDateTake().toInstant(), ZoneId.systemDefault());
			LocalDateTime returnLDT = LocalDateTime.ofInstant(reservation.getDateReturn().toInstant(), ZoneId.systemDefault());
			
			workHours = workHoursInRange(takeLDT, returnLDT);
			workHours -= workHoursNotAtStartAndEnd(takeLDT, returnLDT);
			
			return workHours;
		}
	}
	
	public double workHoursNotAtStartAndEnd(LocalDateTime start, LocalDateTime end) {
		double workHours = 0;
		double startMinutes = 0;
		double endMinutes = 0;
		
		LocalDateTime firstWorkDayStart = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), STARTHOUR, STARTMINUTE);
		LocalDateTime lastWorkDayEnd = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), ENDHOUR, ENDMINUTE);
		
		if (start.isAfter(firstWorkDayStart)) {
			startMinutes = firstWorkDayStart.until(start, ChronoUnit.MINUTES);
		}
		
		if (lastWorkDayEnd.isAfter(end)) {
			endMinutes = end.until(lastWorkDayEnd, ChronoUnit.MINUTES);
		}

		workHours = (startMinutes / 60) + (endMinutes / 60);
		return workHours;
	}

	
	public double workHoursInRange(LocalDateTime start, LocalDateTime end) {
//		logger.debug("************ Start: " + start + " End: " + end);
    	int workDays = 0;
    	while (start.isBefore(end)) {	
    		if (!start.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !start.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
    			workDays++;
    		}
    			
    		start = start.plusDays(1);
    		
    	}
//    	logger.debug("**************** WorkDays: " + workDays);
    	return workDays * WORKDAY;
	}
	
	public static LocalDateTime epochToLocalDateTime(long epoch) {
		Instant instant = Instant.ofEpochSecond(epoch);
		LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		return date;
	}
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
}

	