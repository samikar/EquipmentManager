package db;

import java.util.ArrayList;
import java.util.Date;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import model.Reservation;

public class DBTest extends LocalDBHandler {

    public ArrayList<Reservation> fetchData() {
    	ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        Connection connection = initDB();
        
		if(connection!=null){
			
            try{
	            Statement stmt = connection.createStatement();
    	        //ResultSet rs = stmt.executeQuery("SELECT * FROM sensordata");
                ResultSet rs = stmt.executeQuery("SELECT * FROM emanager_testdb.reservation;");
                   
				while(rs.next()){
					Reservation er = new Reservation();
                    er.setReservationId(rs.getInt("reservationId"));
                    //er.setEquipment(rs.getString("equipmentId"));
                    er.setEmployeeId_take(rs.getString("employeeId_take"));
                    er.setEmployeeId_return(rs.getString("employeeId_return"));
                    er.setDateTake(rs.getTimestamp("date_take"));
                    er.setDateReturn(rs.getTimestamp("date_return"));
                    er.setReservationType(rs.getInt("reservationType"));
					reservations.add(er);
				}
                return reservations;
			} catch (Exception ex) {
				System.out.println(ex.toString());
				return null;
			}
            finally{
				closeDB(connection);
            }
		}
        else
            return null;
    }

    public static void main(String[] args) {

    	DBTest dbt = new DBTest();
    	ArrayList<Reservation> resArr = dbt.fetchData();
    	StringBuffer sb = new StringBuffer();
    	
    	
    	for (Reservation reservation : resArr) {
    		sb.append("id:" + reservation.getReservationId() +
    				  " equipmentId: " + reservation.getEquipment().getEquipmentId() +
    				  " employeeId_take: " + reservation.getEmployeeId_take() +
    				  " employeeId_return: " + reservation.getEmployeeId_return() +
    				  " date_take: " + reservation.getDateTake().toString() +
    				  " date_return: " + reservation.getDateReturn().toString() +
    				  " reservationType: " + reservation.getReservationType() + "\n");
    	}
    	
    	System.out.println(sb.toString());
    }
}