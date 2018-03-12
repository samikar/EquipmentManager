package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class LocalDBHandler {
	/*
    public Connection initDB() {
    	Properties appProperties = PropertyUtils.loadProperties();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("Driver loaded");
		} catch (Exception ex) {
			System.out.println(ex.toString());
            return null;
		}
        try{
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
    */
}