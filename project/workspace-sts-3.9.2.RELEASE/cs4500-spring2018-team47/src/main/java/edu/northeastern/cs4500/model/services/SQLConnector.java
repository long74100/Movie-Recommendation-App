package edu.northeastern.cs4500.model.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;


public class SQLConnector {
	// the local database URL
	private static String url = "jdbc:mysql://cs4500-spring2018-team47-dev.cmtcd3hyzi5a.us-east-2.rds.amazonaws.com/SpoiledTomatillos";
	// database username
	private static String username = "RuairiMSmillie";
	// database password
	private static String password = "TbthaGCmiimWrtayxr4MBEcD3tVB3sY";
	// this will be used to contain the Query command
	private static String command = "";
	// this is the operation status on database
	private enum Status {get, insert, delete, update};
	private static Status status = null;
	private static Connection connector = null;
	private static Statement connectStatement = null;
	private static ResultSet myResult = null;
	
	/**
	 * The constructor
	 * The constructor will automatically create connection to local database
	 */
	public SQLConnector() {
		try {
			connector = DriverManager.getConnection(url, username, password);
			connectStatement = connector.createStatement();
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
		
	}
	
    
    /**
     * To check if the given movie Id already exists in the database
     * @param movieId the movieId of checked movie
     * @return true if given movie exists in local database, else return false
     */
    public boolean containMovie(String movieId) {
    	try {
    		String sqlcmd = "select * from Movie where movie_id = '" + movieId + "'";
    		myResult = connectStatement.executeQuery(sqlcmd);
    		if(myResult.next()) {
    			return true;
    		}
    	}
    	catch (SQLException ep) {
    		ep.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * To insert the data into the given table
     * @param data the data will be inserted
     * @param tableName the destination table that the data will be inserted to
     */
    public void insertData(String data, String tableName) {
    	try {
    		String query = "insert into " + tableName + " values" + data;
    		connectStatement.executeUpdate(query);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    
    
    
    /**
	 * To execute the interaction with backend database
	 * @param args
	 */
	
	/**
    public static void main(String[] args) {
    	try {
    		// 1. get connection to database
    		connector = DriverManager.getConnection(url, username, password);
    		// 2. create statement
    		connectStatement = connector.createStatement();
    		// 3. Execute SQL Query
    		myResult = connectStatement.executeQuery("select * from SpoiledTomatillos.Actor");
    		// 4. Process the result set
    		while(myResult.next()) {
    			System.out.println(myResult.getString("actor_id") + "--->" + myResult.getString("actor_name"));
    		}
    		
    		//insert data
    		String sql = "insert into User values (000005, \"tester\", 'admin', 'admin');";
    		connectStatement.executeUpdate(sql);
    		
    		
    		
    		//Update data
    		String sql2 = "update User set userpass='setSample' where id=000005";
    		connectStatement.executeUpdate(sql2);
    	
    		
    		
    		//delete data
    		String sql3 = "delete from User where username=\"tester\"";
    		int deletedRow = connectStatement.executeUpdate(sql3);
    		System.out.println("deleted row is: " + deletedRow);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
   */
}
