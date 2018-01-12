package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import model.Equipmentreservation;
import model.EquipmentreservationDao;


@RestController
public class ReservationController {
	
    @RequestMapping("/test")
    public String hello(@RequestParam(value="name", defaultValue="World") String name) {
        return "{\"id\":\"hello\"}";
    }

    @RequestMapping("/getallreservations")
    public List<Equipmentreservation> getallreservations(@RequestParam(value="name", defaultValue="World") String name) {	
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		return dao.getDaos();
    }
    
    // NOTE: testdbuser has no DELETE priviliges 
    @RequestMapping("/deletereservation")
    public void deleteReservation(@RequestParam(value="reservationId", defaultValue="0") String reservationId) {
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		dao.initialize(Integer.parseInt(reservationId));
		dao.delete();
    }
    
    @RequestMapping("/insertreservation")
    public Equipmentreservation insertReservation(
			@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
			@RequestParam(value="dateTake", defaultValue="0") String dateTake,
			@RequestParam(value="employeeId_return", defaultValue="0") String employeeId_return,
			@RequestParam(value="employeeId_take", defaultValue="0") String employeeId_take,
			@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
			@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	Date dt = new Date(Long.parseLong(dateTake) * 1000);
    	    	
		EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		Equipmentreservation reservation = new Equipmentreservation();
		
		reservation.setDateReturn(dr);
		reservation.setDateTake(dt);
		reservation.setEmployeeId_return(employeeId_return);
		reservation.setEmployeeId_take(employeeId_take);
		reservation.setEquipmentId(equipmentId);
		reservation.setReservationType(Integer.parseInt(reservationType));

		dao.persist(reservation);	
		
		return reservation;
	}
    
    @RequestMapping("/updatereservation")
    public Equipmentreservation updateReservation(
    		@RequestParam(value="reservationId", defaultValue="0") String reservationId,
    		@RequestParam(value="dateReturn", defaultValue="0") String dateReturn,
    		@RequestParam(value="dateTake", defaultValue="0") String dateTake,
    		@RequestParam(value="employeeId_return", defaultValue="0") String employeeId_return,
    		@RequestParam(value="employeeId_take", defaultValue="0") String employeeId_take,
    		@RequestParam(value="equipmentId", defaultValue="0") String equipmentId,
    		@RequestParam(value="reservationType", defaultValue="0") String reservationType)	{
    	
    	EquipmentreservationDao dao = new EquipmentreservationDao();
    	dao.init();
    	Equipmentreservation reservation = new Equipmentreservation();
    	
    	// Parse dates from epoch to Date
    	Date dr = new Date(Long.parseLong(dateReturn) * 1000);
    	Date dt = new Date(Long.parseLong(dateTake) * 1000);
    	
    	reservation.setreservationId(Integer.parseInt(reservationId));
    	reservation.setDateReturn(dr);
    	reservation.setDateTake(dt);
    	reservation.setEmployeeId_return(employeeId_return);
    	reservation.setEmployeeId_take(employeeId_take);
    	reservation.setEquipmentId(equipmentId);
    	reservation.setReservationType(Integer.parseInt(reservationType));
    	
    	dao.update(reservation);
    	return reservation;
    }
    
    @RequestMapping("/getbyType")
    public List<Equipmentreservation> QueryTest2(@RequestParam(value="reservationType", defaultValue="0") String reservationType) {
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		return dao.queryTest(reservationType);
    }
    
//    //TODO:
//    @RequestMapping("/getbyDate")
//    public List<Equipmentreservation> getReservationsByDate() {
//    	
//    }
    
    //TODO:
    @RequestMapping("/getbyEquipmentId")
    public List<Equipmentreservation> getReservationsByEquipmentId(@RequestParam(value="equipmentId", defaultValue="0") String equipmentId) {
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		return dao.reservationsByEquipmentId(equipmentId);
    }
    
//    //TODO:
//    @RequestMapping("/getbyEquipmentType")
//    public List<Equipmentreservation> getReservationsByEquipmentId() {
//    	
//    }
//    
}
