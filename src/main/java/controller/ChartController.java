package controller;

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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Reservation;
import model.ReservationDao;

@RestController
public class ChartController {
	@RequestMapping("/rest/chartTest")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "{\"id\":\"hello\"}";
	}

	@RequestMapping("/rest/usageBySerial")
	// public String[] usageBySerial(
	public List<Reservation> usageBySerial(@RequestParam(value = "serial") String serial,
			@RequestParam(value = "dateStartConstraint") String dateStartConstraintStr,
			@RequestParam(value = "dateEndConstraint") String dateEndConstraintStr) {

		ReservationDao rdao = new ReservationDao();

		List<Reservation> reservationsAll = rdao.getBySerial(serial);

		// Parse dates from epoch to Date
		Date dateStartConstraint = new Date(Long.parseLong(dateStartConstraintStr) * 1000);
		Date dateEndConstraint = new Date(Long.parseLong(dateEndConstraintStr) * 1000);

		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		System.out.println("Start: " + sdf.format(dateStartConstraint));
		System.out.println("End: " + sdf.format(dateEndConstraint));

		List<Reservation> reservationsInRange = reservationsInRange(reservationsAll, dateStartConstraint, dateEndConstraint);
		
		System.out.println("Work hours: " + workhoursInRange(dateStartConstraint, dateEndConstraint));

		return reservationsInRange;
	}

	public float workhoursInStartAndEnd(Date startDate, Date endDate) {
		float workhours = 0;
		long startMinutes = 0;
		long endMinutes = 0;
		long resultMilliseconds;

		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Calendar cal = Calendar.getInstance();

		long daysInBetween = ChronoUnit.DAYS.between(start, end);

		System.out.println("Days in between: " + daysInBetween);

		if (daysInBetween > 0) {
			// Set workday to end at 16:00 at startDate
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 16);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			// String pattern = "yyyy-MM-dd HH:mm:ss";
			// SimpleDateFormat sdf = new SimpleDateFormat(pattern);

			resultMilliseconds = cal.getTime().getTime() - startDate.getTime();
			startMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);

			// System.out.println("Start: " + sdf.format(startDate));
			// System.out.println("Start: 1: " + startDate.getTime() + " 2: " +
			// cal.getTime().getTime() + " Result: " + startMinutes);

			// Set workday to start at 08:30 at endDate
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 8);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			resultMilliseconds = endDate.getTime() - cal.getTime().getTime();
			endMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);

			// System.out.println("End: " + sdf.format(endDate));
			// System.out.println("end: 1: " + cal.getTime().getTime() + " 2: " +
			// endDate.getTime() + " Result: " + endMinutes);			
		}
		else {
			resultMilliseconds = endDate.getTime() - startDate.getTime();
			endMinutes = TimeUnit.MILLISECONDS.toMinutes(resultMilliseconds);
		}
		workhours = (float) (startMinutes + endMinutes) / 60;
		return workhours;
	}

	public float workhoursInRange(Date startDate, Date endDate) {
		float workhours = 0;
		float workhoursBetween = 0;
		float workhoursAtStartAndEnd = 0;
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		long daysInBetween = ChronoUnit.DAYS.between(start, end);
		
		if (daysInBetween > 0) {
			for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
				DayOfWeek dayOfWeek = date.getDayOfWeek();
				if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
					// System.out.println("Day: " + date.getDayOfWeek());
					workhoursBetween += 7.5;
				}
			}
			// Substract the work hours for start and end day
			workhoursBetween -= 15;
		}
		System.out.println("Workhours between start and end: " + workhoursBetween);
		workhoursAtStartAndEnd = workhoursInStartAndEnd(startDate, endDate);
		System.out.println("Workhours at start and end: " + workhoursAtStartAndEnd);
		workhours = workhoursBetween + workhoursAtStartAndEnd;
		return workhours;
	}

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

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String startString = "1-10-2017 09:00:00";
		String endString = "25-06-2018 13:00:00";
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

		System.out.println("WorkHoursInRange: " + cc.workhoursInRange(start, end));
	}
}
