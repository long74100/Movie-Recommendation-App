package edu.northeastern.cs4500.model.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class is used to connect to the local database. This tool builds a connection between front end 
 * operation and back end database. Also, it will get the online movie information to the local database. 
 * When user search a movie online which already exists in local database, local database will provide information 
 * without doing any API Call. Otherwise, local database will store the movie which it does not contain but user search online.
 * 
 * @author lgj81
 */
public class LocalSQLConnectService {
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
	public LocalSQLConnectService() {
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
     * To delete the given movie from the local database
     * @param id the given movie id
     */
    public void deleteFromMovieTable(String id) {
    	try {
    		String query = "delete from Movie where movie_id = \"" + id + "\"";
    		int row = connectStatement.executeUpdate(query);
    		System.out.println("delete row " + row + " id: " + id);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
}
