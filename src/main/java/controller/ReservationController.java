package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Employee;
import model.EmployeeDao;
import model.Equipment;
import model.EquipmentDao;
import model.EquipmentStatus;
import model.Equipmenttype;
import model.Reservation;
import model.ReservationDao;

@RestController
public class ReservationController {

	@RequestMapping("/rest/test")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "{\"id\":\"hello\"}";
	}

	@RequestMapping("/rest/getallreservations")
	public List<Reservation> getallreservations() {
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		List<Reservation> result = rdao.getAll();
		rdao.destroy();
		return result;
	}

	@RequestMapping("/rest/take")
	public Reservation takeEquipment(@RequestParam(value = "employeeId") String employeeId,
			@RequestParam(value = "serial") String serial,
			@RequestParam(value = "reservationType") String reservationType) {

		ReservationDao rdao = new ReservationDao();
		EmployeeDao empdao = new EmployeeDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		empdao.init();
		edao.init();

		if (employeeId == null || employeeId.isEmpty()) {
			throw new IllegalArgumentException("Employee ID must not be empty");
		} else if (serial == null || serial.isEmpty()) {
			throw new IllegalArgumentException("Serial number must not be empty");
		} else if (reservationType == null || reservationType.isEmpty()) {
			throw new IllegalArgumentException("Reservation type must be selected");
		}

		else if (edao.getEquipmentIdBySerial(serial) == 0) {
			throw new IllegalArgumentException("No equipment found for serial number: " + serial);
		} else if (rdao.serialHasOpenReservation(serial)) {
			throw new IllegalArgumentException("Open reservation for serial number " + serial + " already found");
		} else if (!empdao.employeeExists(employeeId)) {
			throw new IllegalArgumentException("No employee found for employeeId: " + employeeId);
		} else {
			Reservation reservation = new Reservation();
			
			// EmployeeDao needs to be refreshed in case a new employee has been added to DB
			empdao.destroy();
			empdao.init();
			empdao.initialize(empdao.getEmployeeKeyByEmployeeId(employeeId));
			edao.initialize(edao.getEquipmentIdBySerial(serial));
			Employee emp = empdao.getDao();
			Equipment e = edao.getDao();
			Date currentDate = new Date();

			reservation.setDateTake(currentDate);
			reservation.setEmployee(emp);
			reservation.setEquipment(e);
			reservation.setReservationType(Integer.parseInt(reservationType));

			rdao.persist(reservation);
			empdao.destroy();
			edao.destroy();
			rdao.destroy();
			return reservation;
		}
	}

	@RequestMapping("/rest/returnMultiple")
	public String returnMultiple(@RequestParam(value = "resIds") String resIds) {
		String[] idsStr = resIds.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(",");

		int[] idsInt = new int[idsStr.length];

		for (int i = 0; i < idsStr.length; i++) {
			try {
				idsInt[i] = Integer.parseInt(idsStr[i]);
			} catch (NumberFormatException nfe) {
				return "Error formatting parameter string";
			}
		}

		for (int i = 0; i < idsInt.length; i++) {
			Date currentDate = new Date();
			ReservationDao rdao = new ReservationDao();
			Reservation reservation = new Reservation();
			rdao.init();
			rdao.initialize(idsInt[i]);

			reservation = rdao.getDao();
			reservation.setDateReturn(currentDate);
			rdao.update(reservation);
			rdao.destroy();
		}
		return "Complete";
	}

	@RequestMapping("/rest/returnSingle")
	public Reservation returnSingle(@RequestParam(value = "serial") String serial) {
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		if (serial == null || serial.isEmpty()) {
			rdao.destroy();
			throw new IllegalArgumentException("Serial number must not be empty");
		} else if (!rdao.serialHasOpenReservation(serial)) {
			rdao.destroy();
			throw new IllegalArgumentException("No open reservation found for serial number " + serial);
		} else {
			rdao.initialize(rdao.getOpenReservationIdBySerial(serial));
			Reservation reservation = rdao.getDao();
			Date currentDate = new Date();
			reservation.setDateReturn(currentDate);
			rdao.update(reservation);
			rdao.destroy();
			return reservation;
		}
	}

	@RequestMapping("/rest/insert")
	public Reservation insertReservation(@RequestParam(value = "dateReturn", defaultValue = "0") String dateReturn,
			@RequestParam(value = "dateTake", defaultValue = "0") String dateTake,
			@RequestParam(value = "employeeKey", defaultValue = "0") String employeeKey,
			@RequestParam(value = "equipmentId", defaultValue = "0") String equipmentId,
			@RequestParam(value = "reservationType", defaultValue = "0") String reservationType) {

		ReservationDao rdao = new ReservationDao();
		EmployeeDao empdao = new EmployeeDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		empdao.init();
		edao.init();
		Reservation reservation = new Reservation();

		empdao.initialize(Integer.parseInt(employeeKey));
		edao.initialize(Integer.parseInt(equipmentId));

		Employee emp = empdao.getDao();
		Equipment e = edao.getDao();

		// Parse dates from epoch to Date
		Date dr = new Date(Long.parseLong(dateReturn) * 1000);
		Date dt = new Date(Long.parseLong(dateTake) * 1000);

		reservation.setDateReturn(dr);
		reservation.setDateTake(dt);

		reservation.setEmployee(emp);

		reservation.setEquipment(e);
		reservation.setReservationType(Integer.parseInt(reservationType));

		rdao.persist(reservation);
		rdao.destroy();
		empdao.destroy();
		edao.destroy();
		return reservation;
	}

	@RequestMapping("/rest/update")
	public Reservation updateReservation(
			@RequestParam(value = "reservationId", defaultValue = "0") String reservationId,
			@RequestParam(value = "dateReturn", defaultValue = "0") String dateReturn,
			@RequestParam(value = "dateTake", defaultValue = "0") String dateTake,
			@RequestParam(value = "employeeKey", defaultValue = "0") String employeeKey,
			@RequestParam(value = "equipmentId", defaultValue = "0") String equipmentId,
			@RequestParam(value = "reservationType", defaultValue = "0") String reservationType) {

		ReservationDao rdao = new ReservationDao();
		EmployeeDao empdao = new EmployeeDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();
		empdao.init();
		Reservation reservation = new Reservation();

		empdao.initialize(Integer.parseInt(employeeKey));
		edao.initialize(Integer.parseInt(equipmentId));
		Employee emp = empdao.getDao();
		Equipment e = edao.getDao();

		// Parse dates from epoch to Date
		Date dr = new Date(Long.parseLong(dateReturn) * 1000);
		Date dt = new Date(Long.parseLong(dateTake) * 1000);

		reservation.setReservationId(Integer.parseInt(reservationId));
		reservation.setDateReturn(dr);
		reservation.setDateTake(dt);
		reservation.setEmployee(emp);
		reservation.setEquipment(e);
		reservation.setReservationType(Integer.parseInt(reservationType));

		rdao.update(reservation);
		rdao.destroy();
		empdao.destroy();
		edao.destroy();
		return reservation;
	}

	// NOTE: testdbuser has no DELETE priviliges
	@RequestMapping("/rest/deletereservation")
	public void deleteReservation(@RequestParam(value = "reservationId", defaultValue = "0") String reservationId) {
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		rdao.initialize(Integer.parseInt(reservationId));
		rdao.delete();
		rdao.destroy();
	}

	@RequestMapping("/rest/getbyReservationType")
	public List<Reservation> getbyReservationType(
			@RequestParam(value = "reservationType", defaultValue = "0") String reservationType) {
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		List<Reservation> result = rdao.getByType(reservationType);
		rdao.destroy();
		return result;
	}

	@RequestMapping("/rest/getbyEmployeeId")
	public List<Reservation> getbyEmployeeId(@RequestParam(value = "employeeId") String employeeId) {
		if (employeeId == null || employeeId.isEmpty()) {
			throw new IllegalArgumentException("Employee ID must not be empty");
		}

		ReservationDao rdao = new ReservationDao();
		rdao.init();
		List<Reservation> reservations = rdao.getByEmployeeId(employeeId);
		rdao.destroy();
		if (reservations.size() > 0)
			return reservations;
		else
			throw new IllegalArgumentException("No reservations found for employeeId " + employeeId);
	}

	@RequestMapping("/rest/getbyEquipmentId")
	public List<Reservation> getbyEquipmentId(
			@RequestParam(value = "equipmentId", defaultValue = "0") String equipmentId) {
		ReservationDao rdao = new ReservationDao();
		rdao.init();
		List<Reservation> result = rdao.getByEquipmentId(equipmentId);
		rdao.destroy();
		return result;
	}

	@RequestMapping("rest/getEquipmentStatus")
	public List<EquipmentStatus> getEquipmentStatus() {
		ReservationDao rdao = new ReservationDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();

		List<Reservation> reservationList = rdao.getOpen();
		List<Equipment> equipmentList = edao.getOrderedByTypeName();
		rdao.destroy();
		edao.destroy();
		List<EquipmentStatus> equipmentStatusList = new ArrayList<EquipmentStatus>();

		for (Equipment e : equipmentList) {

			EquipmentStatus eStatus = new EquipmentStatus();
			eStatus.setEquipmentId(e.getEquipmentId());
			eStatus.setName(e.getName());
			eStatus.setSerial(e.getSerial());

			if (e.getEquipmenttype() == null) {
				Equipmenttype eType = new Equipmenttype();
				eType.setEquipmentTypeId(0);
				eType.setTypeCode(0);
				eType.setTypeName("Unknown");
				eStatus.setEquipmenttype(eType);
			} else
				eStatus.setEquipmenttype(e.getEquipmenttype());

			String availability = "Available";
			eStatus.setEmployeeName("");
			for (Reservation r : reservationList) {
				if (r.getEquipment().getEquipmentId() == eStatus.getEquipmentId()) {
					eStatus.setEmployeeName(r.getEmployee().getName());

					switch (r.getReservationType()) {
					case 0:
						availability = "In use";
						break;
					case 1:
						availability = "Calibration";
						break;
					case 2:
						availability = "Maintenance";
						break;
					}
				}
			}
			eStatus.setAvailability(availability);
			equipmentStatusList.add(eStatus);
		}
		return equipmentStatusList;
	}

	@RequestMapping("rest/getEmployee")
	public Employee getEmployeeName(@RequestParam(value = "employeeId") String employeeId) {
		EmployeeDao empdao = new EmployeeDao();
		empdao.init();
		Employee result = empdao.getEmployeeByEmployeeId(employeeId);
		empdao.destroy();
		return result;
	}

	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}
