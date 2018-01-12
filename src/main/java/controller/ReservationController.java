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
    public void deleteReservation(@RequestParam(value="idEquipmentReservation", defaultValue="0") String idEquipmentReservation) {
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		dao.initialize(Integer.parseInt(idEquipmentReservation));
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
    	
    	System.out.println("Date return: " + dateReturn + "\nDate Take: " + dateTake);
    	System.out.println("Date return: " + dr.toString() + "\nDate Take: " + dt.toString());
    	System.out.println("Date return: " + sdf.format(dr).toString() + "\nDate Take: " + sdf.format(dt).toString());
    	
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
    		@RequestParam(value="idEquipmentReservation", defaultValue="0") String idEquipmentReservation,
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
    	
    	reservation.setIdEquipmentReservation(Integer.parseInt(idEquipmentReservation));
    	reservation.setDateReturn(dr);
    	reservation.setDateTake(dt);
    	reservation.setEmployeeId_return(employeeId_return);
    	reservation.setEmployeeId_take(employeeId_take);
    	reservation.setEquipmentId(equipmentId);
    	reservation.setReservationType(Integer.parseInt(reservationType));
    	
    	dao.update(reservation);
    	return reservation;
    }
    
    @RequestMapping("/querytest")
    public List<Equipmentreservation> QueryTest2(@RequestParam(value="reservationType", defaultValue="0") String reservationType) {
    	EquipmentreservationDao dao = new EquipmentreservationDao();
		dao.init();
		return dao.queryTest(reservationType);
    }
    /*
    //TODO:
    @RequestMapping("/getreservationsbydate")
    public List<Equipmentreservation> getReservationsByDate() {
    	
    }
    
    //TODO:
    @RequestMapping("/getreservationsbyequipmentid")
    public List<Equipmentreservation> getReservationsByEquipmentId() {
    	
    }
    
    //TODO:
    @RequestMapping("/getreservationsbyequipmenttype")
    public List<Equipmentreservation> getReservationsByEquipmentId() {
    	
    }
    */
}
