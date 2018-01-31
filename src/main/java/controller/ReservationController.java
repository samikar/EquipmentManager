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

import db.ADHandler;
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
    public String hello(@RequestParam(value="name", defaultValue="World") String name) {
        return "{\"id\":\"hello\"}";
    }
    
    @RequestMapping("/rest/ADtest")
    public String adTest(@RequestParam(value="employeeId", defaultValue="World") String employeeId) {
    	String name = ADHandler.findEmployeeName(employeeId);
        return name;
    }

    @RequestMapping("/rest/getallreservations")
    public List<Reservation> getallreservations() {	
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getAll();
    }  
    
    @RequestMapping("/rest/take")
    public Reservation takeEquipment(
			@RequestParam(value="employeeId") String employeeId,
			@RequestParam(value="serial") String serial,
			@RequestParam(value="reservationType") String reservationType)	{
    	
    	
    	ReservationDao rdao = new ReservationDao();
    	EmployeeDao empdao = new EmployeeDao();
    	EquipmentDao edao = new EquipmentDao();
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
    	else if (rdao.reservationOpenBySerial(serial)) {
			throw new IllegalArgumentException("Open reservation for serial number " + serial + " already found");
		}
    	else if (!empdao.employeeInDB(employeeId) && !empdao.employeeInAD(employeeId)) {
    		throw new IllegalArgumentException("No employee found for employeeId: " + employeeId);
    	}
		else {
			Reservation reservation = new Reservation();
			edao.initialize(edao.getEquipmentIdBySerial(serial));
			Equipment e = edao.getDao(); 
			Date currentDate = new Date();
			
	
/*
			if (empdao.employeeInDB(employeeId))
				reservation.setEmployee(empdao.getEmployeeByEmployeeId(employeeId));
			else 
				reservation.setEmployee(empdao.addEmployeeToDB(employeeId));
*/
			reservation.setDateTake(currentDate);
			reservation.setEmployee(empdao.getEmployeeByEmployeeId(employeeId));
			reservation.setEquipment(e);
			reservation.setReservationType(Integer.parseInt(reservationType));

			rdao.persist(reservation);	
			return reservation;
		}
	}
    
    @RequestMapping("/rest/returnMultiple")
    public String returnMultiple(@RequestParam(value="resIds") String resIds) {
    	String[] idsStr = resIds.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(",");

    	int[] idsInt = new int[idsStr.length];

    	for (int i = 0; i < idsStr.length; i++) {
    	    try {
    	    	idsInt[i] = Integer.parseInt(idsStr[i]);
    	    } catch (NumberFormatException nfe) {
    	    	return "Error formatting parameter string";
    	    };
    	}
    	
    	for (int i=0;i<idsInt.length;i++) {
    		Date currentDate = new Date();
    		ReservationDao rdao = new ReservationDao();
    		Reservation reservation = new Reservation();
    		rdao.init();
    		rdao.initialize(idsInt[i]);
    		
    		reservation = rdao.getDao();
    		reservation.setDateReturn(currentDate);
    		rdao.persist(reservation);
    	}
    	return "Complete";
    }	
    
    @RequestMapping("/rest/return")
    public Reservation returnEquipment(
    		@RequestParam(value="reservationId", defaultValue="0") String reservationId,
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn)	{
    	
    	ReservationDao rdao = new ReservationDao();
    	    			
    	rdao.init();
    	rdao.initialize(Integer.parseInt(reservationId));
    	Reservation reservation = rdao.getDao();
		    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	reservation.setDateReturn(dr);
    	rdao.update(reservation);
    	
    	return reservation;
    }    
    
    @RequestMapping("/rest/insert")
    public Reservation insertReservation(
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
    		@RequestParam(value="dateTake", defaultValue="0") String dateTake,
    		@RequestParam(value="employeeKey", defaultValue="0") String employeeKey,
    		@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
    		@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
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
    	return reservation;
    }
    
    @RequestMapping("/rest/update")
    public Reservation updateReservation(
    		@RequestParam(value="reservationId", defaultValue="0") String reservationId,
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
    		@RequestParam(value="dateTake", defaultValue="0") String dateTake,
    		@RequestParam(value="employeeKey", defaultValue="0") String employeeKey,
    		@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
    		@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
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
    	return reservation;
    }
    
    // NOTE: testdbuser has no DELETE priviliges 
    @RequestMapping("/rest/deletereservation")
    public void deleteReservation(@RequestParam(value="reservationId", defaultValue="0") String reservationId) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		dao.initialize(Integer.parseInt(reservationId));
		dao.delete();
    }
    
    @RequestMapping("/rest/getbyReservationType")
    public List<Reservation> getbyReservationType(@RequestParam(value="reservationType", defaultValue="0") String reservationType) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getByType(reservationType);
    }

    @RequestMapping("/rest/getbyEmployeeId")
    public List<Reservation> getbyEmployeeId(@RequestParam(value="employeeId", defaultValue="0") String employeeId) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getByEmployeeId(employeeId);
    }
    
    @RequestMapping("/rest/getbyEquipmentId")
    public List<Reservation> getbyEquipmentId(@RequestParam(value="equipmentId", defaultValue="0") String equipmentId) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getByEquipmentId(equipmentId);
    }

	@RequestMapping("rest/getEquipmentStatus")
	public List<EquipmentStatus> getEquipmentStatus() {
		ReservationDao rdao = new ReservationDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();

		List<Reservation> reservationList = rdao.getOpen();
		List<Equipment> equipmentList = edao.getEquipmentOrderedByType();
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
	public Employee getEmployeeName(@RequestParam(value="employeeId") String employeeId) {
		EmployeeDao empdao = new EmployeeDao();
		empdao.init();
		Employee emp = empdao.getEmployeeByEmployeeId(employeeId);
		return emp;
	}
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
    
    /*
    @RequestMapping("/rest/getbyEquipmentType")
    public List<Reservation> getbyEquipmentType(@RequestParam(value="equipmentType", defaultValue="0") String equipmentId) {
    	
    }
    */  
    
    /*
    @RequestMapping("/serialtest")
    public String serialTest(@RequestParam(value="serial", defaultValue="0") String serial) {
    	EquipmentDao edao = new EquipmentDao();
		edao.init();
		int foo = edao.getEquipmentIdBySerial(serial);
		if (foo > 0)
			return "Truu dat";
		else
			return "Fälse";
    }
    */
    
//    //TODO:
//    @RequestMapping("/getbyEquipmentType")
//    public List<Reservation> getReservationsByEquipmentId() {
//    	
//    }
//    
}
