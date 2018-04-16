package fi.danfoss.equipmentmanager.controller;

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

import com.google.gson.Gson;

import fi.danfoss.equipmentmanager.model.Employee;
import fi.danfoss.equipmentmanager.model.EmployeeDao;
import fi.danfoss.equipmentmanager.model.Equipment;
import fi.danfoss.equipmentmanager.model.EquipmentDao;
import fi.danfoss.equipmentmanager.model.EquipmentStatus;
import fi.danfoss.equipmentmanager.model.Equipmenttype;
import fi.danfoss.equipmentmanager.model.Reservation;
import fi.danfoss.equipmentmanager.model.ReservationDao;

@RestController
public class ReservationController {
	// Database properties
	private ReservationDao rdao;
	private EmployeeDao empdao;
	private EquipmentDao edao;
	
	@RequestMapping("/rest/test")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "{\"id\":\"hello\"}";
	}

	@RequestMapping("/rest/getallreservations")
	public List<Reservation> getAllReservations() {
		rdao = new ReservationDao();
		rdao.init();
		List<Reservation> result = rdao.getAll();
		rdao.destroy();
		return result;
	}

	@RequestMapping("/rest/take")
	public Reservation takeEquipment(@RequestParam(value = "employeeId") String employeeId,
			@RequestParam(value = "serial") String serial,
			@RequestParam(value = "reservationType") String reservationType) {
	
		rdao = new ReservationDao();
		empdao = new EmployeeDao();
		edao = new EquipmentDao();
		rdao.init();
		empdao.init();
		edao.init();

		if (employeeId == null || employeeId.isEmpty()) {
			throw new IllegalArgumentException("Employee ID must not be empty");
		} 
		else if (serial == null || serial.isEmpty()) {
			throw new IllegalArgumentException("Serial number must not be empty");
		} 
		else if (reservationType == null || reservationType.isEmpty()) {
			throw new IllegalArgumentException("Reservation type must be selected");
		}
		else if (edao.getEquipmentIdBySerial(serial) == 0) {
			throw new IllegalArgumentException("No equipment found for serial number: " + serial);
		} 
		else if (rdao.serialHasOpenReservation(serial)) {
			throw new IllegalArgumentException("Open reservation for serial number " + serial + " already found");
		} 
		else if (!empdao.employeeExists(employeeId)) {
			throw new IllegalArgumentException("No employee found for employeeId: " + employeeId);
		} 
		else {
			Reservation reservation = new Reservation();
			
			// EmployeeDao needs to be refreshed in case a new employee has been added to DB
			empdao.refresh();
			edao.refresh();
			
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
	
	@RequestMapping("/rest/returnSingle")
	public Reservation returnSingle(@RequestParam(value = "serial") String serial) {
		edao = new EquipmentDao();
		rdao = new ReservationDao();
		edao.init();
		rdao.init();
		if (serial == null || serial.isEmpty()) {
			rdao.destroy();
			throw new IllegalArgumentException("Serial number must not be empty");
		}
		else if (edao.getEquipmentIdBySerial(serial) == 0) {
			throw new IllegalArgumentException("No equipment found for serial number: " + serial); 
		}
		else if (!rdao.serialHasOpenReservation(serial)) {
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
	
	@RequestMapping("/rest/returnMultiple")
	public String returnMultiple(@RequestParam(value = "resIds") String resIds) {
		if (resIds == null || resIds.isEmpty()) {
			throw new IllegalArgumentException("No serial number selected");
		}

		int[] idsInt;
		Gson gson = new Gson();
		try {
			idsInt = gson.fromJson(resIds, int[].class);
		} catch (Exception e) {
			return "Error formatting JSON String"; 
		}

		for (int i = 0; i < idsInt.length; i++) {
			Date currentDate = new Date();
			Reservation reservation = new Reservation();
			rdao = new ReservationDao();
			rdao.init();
			rdao.initialize(idsInt[i]);

			reservation = rdao.getDao();
			reservation.setDateReturn(currentDate);
			rdao.update(reservation);
			rdao.destroy();
		}
		return "Done";
	}

	@RequestMapping("/rest/getbyEmployeeId")
	public List<Reservation> getbyEmployeeId(@RequestParam(value = "employeeId") String employeeId) {
		if (employeeId == null || employeeId.isEmpty()) {
			throw new IllegalArgumentException("Employee ID must not be empty");
		}

		rdao = new ReservationDao();
		rdao.init();
		List<Reservation> reservations = rdao.getOpenByEmployeeId(employeeId);
		rdao.destroy();
		if (reservations.size() > 0)
			return reservations;
		else
			throw new IllegalArgumentException("No reservations found for employeeId " + employeeId);
	}

	@RequestMapping("rest/getEquipmentStatus")
	public List<EquipmentStatus> getEquipmentStatus() {
		edao = new EquipmentDao();
		rdao = new ReservationDao();
		edao.init();
		rdao.init();

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
	public Employee getEmployee(@RequestParam(value = "employeeId") String employeeId) {
		empdao = new EmployeeDao();
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
