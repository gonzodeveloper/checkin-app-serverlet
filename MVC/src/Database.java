/*
 * Kyle Hart 
 * 18 April 2017
 *
 * Project: CheckIn_App
 * Description: This functions as the "model" component of the MVC.
 * 				The class contains methods that interact with the MySQL database such as:
 * 				Logging a user in, creating users, authenticating users, adding "checkins" for users.
 * 				This all input to the database is sanitized so as to avoid SQL injection with this class.
 * 				All passwords are hashed before storage in database.
 * 
 */


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Database {
	
	private static Connection conn;
	private static String uid;
	
	
	/*
	 * Opens a connection to the database. This object should be instantiated in the constructor or init() method
	 * of the serverlet
	 */
	Database(){
		try {
			openConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		uid = null;
	}
	/*
	 * Closes the connection to the database, should be called in the serverlet's destroy() method
	 */
	protected void closeConn(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Creates a connection to the database, and instantiates the conn so that it may create statements
	 */
	private static void openConnection() throws ClassNotFoundException, SQLException {
	// Load the JDBC driver
	Class.forName("com.mysql.jdbc.Driver");
	// Connect to a database
	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/checkin_app?useSSL=false", "webuser",
			"C@shRul3sEverything@roundMe");

	}
	/*
	 * Returns boolean of whether or not the client has logged in. Note, the client must be logged in to
	 * call the checkin(), getUserCheckins() methods
	 */
	public boolean isLoggedIn(){
		return !(uid == null);
	}
	/*
	 * Authenticates the user's handle and password. If the password is correct it will "login" for that user and
	 * return true. If there is no such handle or if the password is incorrect, the method will return false and
	 * the user will not be logged in
	 */
	public boolean authenticate(String handle, String pwd){
		handle = cleanInput(handle);
		pwd = cleanInput(pwd);
		Statement statement;
		try {
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery
					("SELECT password, uid FROM users WHERE handle = " + "'" + handle + "'");
			if(!resultSet.isBeforeFirst())
				return false;
			pwd = hash(pwd);
			resultSet.next();
		    if( !(resultSet.getString("password").equals(pwd))){
		    	System.out.println("bad pw");
		    	return false;

		    }
		    else{
		    	uid = resultSet.getString("uid");
		    	return true;
		    }
		} catch (SQLException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
	
	}
	
	
	/*
	 * creates a new user with a given handle, pwd, fname.lname, city and state. If this is a duplicate handle the method
	 * will NOT create the user and return false. Restrictions: handle <= 16 characters, fname <= 25 characters, 
	 * lname <= 25 charactes, city <= 50 characters, state <= 2 characters
	 */
	public boolean createNewUser(String handle, String pwd, String fname, String lname, String city, String state){
		handle = cleanInput(handle);
		pwd = cleanInput(pwd);
		fname = cleanInput(fname);
		lname = cleanInput(lname);
		city = cleanInput(city);
		state = cleanInput(state);
		try {
			pwd = hash(pwd);
	    	Statement statement = conn.createStatement();
			statement.executeUpdate
					("INSERT INTO users(handle, password, first_name, last_name, city, state)"
					+ "VALUES('"+ handle +"', '" + pwd +"', '" + fname + "', '" + lname + "', '" + city + "', '" + state +"')");
		} catch (SQLException | NoSuchAlgorithmException e) {
			return false;
		}
	    return true;
		
	}
	/*
	 * Hashing function used to store passwords in the database and authenticate in the controller
	 */
	private static String hash(String pwd) throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] bytes = digest.digest(pwd.getBytes(StandardCharsets.UTF_8));
		StringBuffer hashed = new StringBuffer();
		for(byte b: bytes)
			hashed.append(Integer.toHexString(0xff & b));
		return hashed.toString();
		
	}
	/*
	 * Creates a place in the database. Returns false if the place already exists or if any of the parameters 
	 * violate table constraints. Restriction s: pid<=256 characters, unique, pname <= 50 characters, ptype<= 20 characters
	 * city <= 50 characters, state <= 2 characters
	 */
	public boolean addPlace(String pid, String pname, String ptype, String city, String state){
		pid = cleanInput(pid);
		pname = cleanInput(pname);
		ptype = cleanInput(ptype);
		city = cleanInput(city);
		state = cleanInput(state);
		try {
	    	Statement statement = conn.createStatement();
			statement.executeUpdate
					("INSERT INTO places(pid, pname, ptype, city, state)"
							+ "VALUES('"+ pid +"', '" + pname + "', '" + ptype + "', '" + city + "', '" + state +"')");
		} catch (SQLException e) {
			return false;
		}
	    return true;
	}
	/*
	 * Creates a new entry in the checkin table in the database with the users id, place id (pid), and the current 
	 * date and time. The method will fail if the user is not "logged in" or if the specified place has not been entered 
	 * into the database.
	 */
	public boolean checkin(String pid){
		pid = cleanInput(pid);
	
		try {
	    	Statement statement = conn.createStatement();
			statement.executeUpdate
					("INSERT INTO checkins(uid, pid, date, time)"
							+ "VALUES('"+ uid +"', '" + pid + "', CURDATE(), CURTIME())");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return true;
	}
	/*
	 * Auxiliary method that queries the database and returns the results in a JSON formatted string.
	 */
	private String resultSetToJson(String query) {
        List<Map<String, Object>> listOfMaps = null;
        try {
            QueryRunner queryRunner = new QueryRunner();
            listOfMaps = queryRunner.query(conn, query, new MapListHandler());
        } catch (SQLException se) {
            throw new RuntimeException("Couldn't query the database.", se);
        } 
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(listOfMaps);
    }
	
	/*
	 * Returns a "table" of the users recorded checkins (must be logged in). Table includes place name, city and state, 
	 * date and time visited. Output is a string in JSON format.
	 */
	public String getUserCheckins(){
		String query = "SELECT pname AS 'Place Name', CONCAT(city, ' ', state) AS 'Location', "
								+ "CONCAT(date, ' ', time) AS 'Visited'"
						+ "FROM checkins JOIN places USING(pid)"
						+ "WHERE uid = " + uid;
		return resultSetToJson(query);
	}
	/*
	 * Returns a "scoreboard" for places. Table includes place name, location, and total number of
	 * checkins. Output is a string in JSON format. User does NOT need to be logged in to view.
	 */
	public String getPlaceScoreboard(){
		String query = "SELECT pname AS 'Place Name', CONCAT(city, ', ', state) AS 'Location', "
							+ "COUNT(*) AS 'Checkins'"
						+ "FROM places JOIN checkins USING(pid)"
						+ "GROUP BY pid";
		return resultSetToJson(query);		
	}
	
	/*
	 * Returns a "scoreboard" for USERS. Table includes user handles, hometown, and total number of
	 * checkins. Output is a string in JSON format. User does NOT need to be logged in to view.
	 */
	public String getUserScoreboard(){
		String query = "SELECT handle, CONCAT(city, ', ', state) AS 'Hometown',"
							+ " COUNT(*) AS 'Checkins'"
						+ "FROM users JOIN checkins USING (uid)"
						+ "GROUP BY uid";
		return resultSetToJson(query);	 
	}
	
	/*
	 * Auxiliary method to remove punctuation from input that goes to query. Helps prevent SQL injection by preventing
	 * hijacking of queries by prematurely ending with ";" or concatenation with "||“ 
	 */
	private static String cleanInput(String dirty){
    	return dirty.replaceAll("[\\\\({});|]", "");
	}

}
