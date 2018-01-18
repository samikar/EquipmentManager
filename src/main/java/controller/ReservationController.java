package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Equipment;
import model.EquipmentDao;
import model.Reservation;
import model.ReservationDao;


@RestController
public class ReservationController {
	
    @RequestMapping("/rest/test")
    public String hello(@RequestParam(value="name", defaultValue="World") String name) {
        return "{\"id\":\"hello\"}";
    }

    @RequestMapping("/rest/getallreservations")
    public List<Reservation> getallreservations() {	
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getAll();
    }  
    
    @RequestMapping("/rest/take")
    public Reservation takeEquipment(
			//@RequestParam(value="dateTake", defaultValue="0") String dateTake,
			@RequestParam(value="employeeId_take", defaultValue="0") String employeeId_take,
			@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
			@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
    	// Parse dates from epoch to Date
    	//Date dt = new Date(Long.parseLong(dateTake) * 1000);
    	Date currentDate = new Date();
    	    	
		ReservationDao rdao = new ReservationDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();
		
		Reservation reservation = new Reservation();
		edao.initialize(Integer.parseInt(equipmentId));			
		Equipment e = edao.getDao(); 
		
		reservation.setDateTake(currentDate);
		reservation.setEmployeeId_take(employeeId_take);
		reservation.setEquipment(e);
		reservation.setReservationType(Integer.parseInt(reservationType));

		rdao.persist(reservation);	
		
		return reservation;
	}
    
    @RequestMapping("/rest/return")
    public Reservation returnEquipment(
    		@RequestParam(value="reservationId", defaultValue="0") String reservationId,
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
    		@RequestParam(value="employeeId_return", defaultValue="0") String employeeId_return)	{
    	
    	ReservationDao rdao = new ReservationDao();
		
    	rdao.init();
		rdao.initialize(Integer.parseInt(reservationId));
    	Reservation reservation = rdao.getDao();
		    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	
    	reservation.setDateReturn(dr);
    	reservation.setEmployeeId_return(employeeId_return);
    	    	    	
    	rdao.update(reservation);
    	return reservation;
    }    
    
    @RequestMapping("/rest/insert")
    public Reservation insertReservation(
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
    		@RequestParam(value="dateTake", defaultValue="0") String dateTake,
    		@RequestParam(value="employeeId_return", defaultValue="0") String employeeId_return,
    		@RequestParam(value="employeeId_take", defaultValue="0") String employeeId_take,
    		@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
    		@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
    	ReservationDao rdao = new ReservationDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();
    	Reservation reservation = new Reservation();
    	
    	edao.initialize(Integer.parseInt(equipmentId));			
		Equipment e = edao.getDao(); 
    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	Date dt = new Date(Long.parseLong(dateTake) * 1000);
    	
    	reservation.setDateReturn(dr);
    	reservation.setDateTake(dt);
    	reservation.setEmployeeId_return(employeeId_return);
    	reservation.setEmployeeId_take(employeeId_take);
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
    		@RequestParam(value="employeeId_return", defaultValue="0") String employeeId_return,
    		@RequestParam(value="employeeId_take", defaultValue="0") String employeeId_take,
    		@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
    		@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
    	ReservationDao rdao = new ReservationDao();
		EquipmentDao edao = new EquipmentDao();
		rdao.init();
		edao.init();
    	Reservation reservation = new Reservation();
    	
    	edao.initialize(Integer.parseInt(equipmentId));			
		Equipment e = edao.getDao(); 
    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	Date dt = new Date(Long.parseLong(dateTake) * 1000);
    	
    	reservation.setReservationId(Integer.parseInt(reservationId));
    	reservation.setDateReturn(dr);
    	reservation.setDateTake(dt);
    	reservation.setEmployeeId_return(employeeId_return);
    	reservation.setEmployeeId_take(employeeId_take);
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
    
    @RequestMapping("/rest/getbyType")
    public List<Reservation> QueryTest2(@RequestParam(value="reservationType", defaultValue="0") String reservationType) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.queryTest(reservationType);
    }
    

    @RequestMapping("/rest/getbyEmployeeId")
    public List<Reservation> getbyEmployeeId(@RequestParam(value="employeeId_take", defaultValue="0") String employeeId) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getByEmployeeId(employeeId);
    }
    
    //TODO:
    @RequestMapping("/rest/getbyEquipmentId")
    public List<Reservation> getbyEquipmentId(@RequestParam(value="equipmentId", defaultValue="0") String equipmentId) {
    	ReservationDao dao = new ReservationDao();
		dao.init();
		return dao.getByEquipmentId(equipmentId);
    }
    
//    //TODO:
//    @RequestMapping("/getbyEquipmentType")
//    public List<Reservation> getReservationsByEquipmentId() {
//    	
//    }
//    
}
