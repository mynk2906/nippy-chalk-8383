package com.Dao.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

	public static Connection provideConnection() {
		
		Connection con=null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String url="jdbc:mysql://localhost:3306/tender";
		try{
			con=DriverManager.getConnection(url,"root","1234");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}
}
