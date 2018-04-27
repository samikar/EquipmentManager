package sqlservertesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class MSSqlTest {
	public static void main(String[] args) {

		
		SQLServerDataSource ds = new SQLServerDataSource();  
		ds.setUser("U319994");  
		ds.setPassword("");  
		ds.setServerName("FIVAS01DB01\\SQLHOTEL");
		ds.setInstanceName("SQLHOTEL");
//		ds.setPortNumber(1433);   
		ds.setDatabaseName("EquipmentManager");
		ds.setAuthentication("SqlPassword");
		// TODO: Sertifikaatti
		try {
			System.out.println("foo");
			Connection con = ds.getConnection();
			System.out.println("bar");
		} catch (SQLServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
}
