package de.l3s.database;

import graphs.CreateGraphs;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.mysql.jdbc.PreparedStatement;

/**
 * @author tereza
 * 
 */
public class Connect {

	private Connection con = null;
	public static String dbUsed = "p";
	static Statement stmt = null;

	// entity related statements

	// connection settings

	//oracle db
	static String userName_oracle_liwa = "liwal3s";
	static String passwd_oracle_liwa = "liwa08";
	
	static String oracleURL_liwa = "jdbc:mysql://oracle.l3s.uni-hannover.de:3308/LiwaTerminology";

	//other db (for us pharos)
	static String driver = DB.getString("db.driver"); //$NON-NLS-1$
	static String userName = DB.getString("db.username"); //$NON-NLS-1$
	static String passwd = DB.getString("db.password"); //$NON-NLS-1$
	static String otherURL = DB.getString("db.url"); //$NON-NLS-1$
	
	public Connect() {
		super();
		// TODO Auto-generated constructor stub
		
		this.con = null;
	}

	public void dbbegin() {
		if(CreateGraphs.dbUsed!=null){
			dbUsed = CreateGraphs.dbUsed;
		}
		this.con = null;

		try {
			
			Class.forName(driver).newInstance();
			
			if(dbUsed.equals("p")){
				con = DriverManager.getConnection(otherURL, userName, passwd);
			}else{
				con = DriverManager.getConnection(oracleURL_liwa, userName_oracle_liwa, passwd_oracle_liwa);
			}
			System.out.println("Connected to db "+dbUsed);
			// entities

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		}
	}

	public Statement makeStatement() {
		try {
			stmt = con.createStatement();
			stmt.setQueryTimeout(100);
			return stmt;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void dbend() throws Throwable {

		if (con != null) {
			try {
				stmt = null;
				con.close();
				con = null;
				System.out.println("Database connection terminated");

			} catch (Exception e) { /* ignore close errors */
			}
		}

	}

	//	

}
