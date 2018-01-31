package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.Properties;
import java.io.FileInputStream;

public class LocalDBHandler {
	
//	public static void main(String[] args){
//		Vector v = new SensorDataDB().fetchSensorData(null, null);
//        System.out.println("Number of rows: " + v.size());
//    }
	
    public Connection initDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("Driver loaded");
		} catch (Exception ex) {
			System.out.println(ex.toString());
            return null;
		}
        try{
            //finding the current dir
            String rootPath = "C:\\EquipmentManager\\ConfigFile\\";
            //locating the config file
			String appConfigPath = rootPath + "app.properties";
            Properties appProperties = new Properties();
            appProperties.load(new FileInputStream(appConfigPath));
            //now read db address, db name, username and password from app.properties -file
			String connPath = "jdbc:mysql://" + appProperties.getProperty("dbaddress") + "/" + appProperties.getProperty("dbname") + "?user=" + appProperties.getProperty("user") + "&password="+ appProperties.getProperty("password");
			System.out.println(connPath);
			Connection conn = DriverManager.getConnection(connPath);
			
            return conn;
		} catch (Exception ex) {
			System.out.println(ex.toString());
            return null;
		}
    }

    public boolean closeDB(Connection _conn) {
        try{
            _conn.close();
            return true;
		} catch (Exception ex) {
			System.out.println(ex.toString());
            return false;
		}
    }
}