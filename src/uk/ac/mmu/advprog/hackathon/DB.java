package uk.ac.mmu.advprog.hackathon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; // import the ArrayList class

import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;


/**
 * Handles database access from within your web service
 * @author You, Mainly!
 */
public class DB implements AutoCloseable {
	
	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/NaPTAN.db";
	
	//allows us to re-use the connection between queries if desired
	private Connection connection = null;
	Connection c = null;
	
	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		}
		catch (SQLException sqle) {
			error(sqle);
		}
	}
	/**
	 * Returns the number of entries in the database, by counting rows
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM Stops");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	/**

	Retrieves the number of stops in a particular locality from the database.

	@param LocalityName The name of the locality to search for.

	@return The number of stops in the specified locality, or -1 if an error occurs.
	*/
	public int getNumberOfStopsInParticularLocality(String LocalityName) {

		int result = -1;
		try {
			PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) AS Number FROM Stops WHERE LocalityName = ?");
			s.setString(1,LocalityName);
			ResultSet rs = s.executeQuery();
			
			
			while(rs.next()) { 
				result = rs.getInt(rs.findColumn("Number"));
			}
			
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return result;
	}
	/**

	Retrieves information about stops in a particular locality and of a particular type from the database and returns it as a JSON array.
	@param LocalityName The name of the locality to search for.
	@param type The type of stop to search for.
	@return A JSON array containing the stop information, or an empty array if an error occurs.
	*/
	public JSONArray gettypes(String LocalityName, String type) {
		JSONArray result = new JSONArray();
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM Stops WHERE LocalityName = ? AND StopType = ?; ");
			s.setString(1,LocalityName);
			s.setString(2, type);
			ResultSet rs = s.executeQuery();
			
			

			while(rs.next()) {
				
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("name", rs.getString("CommonName") != null ? rs.getString("CommonName") : "{}");
				jsonobject.put("locality", rs.getString("LocalityName") != null ? rs.getString("LocalityName") : "{}");
							
				JSONObject jsonobject2 = new JSONObject();
				jsonobject2.put("indicator", rs.getString("Indicator") != null ? rs.getString("Indicator") : "{}");
				jsonobject2.put("bearing", rs.getString("Bearing") != null ? rs.getString("Bearing") : "{}");
				jsonobject2.put("street", rs.getString("Street") != null ? rs.getString("Street") : "{}");
				jsonobject2.put("landmark", rs.getString("Landmark") != null ? rs.getString("Landmark") : "{}");
							
				jsonobject.put("location",jsonobject2);
							
				result.put(jsonobject);
			}
			

				
			
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return result; 
	}

	public int getspecficroute(String type, String latitude, String longitude) throws SQLException {
		int result =-1;
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM stops WHERE StopType = ? AND Latitude IS NOT NULL AND Longitude IS NOT NULL ORDER BY ( ((? - Latitude) * (? - Latitude)) +(? * ((? - Longitude) * (? - Longitude)))) ASC LIMIT 5;");
			s.setString(1, type);
			s.setString(2, latitude);
			s.setString(3,latitude);
			s.setString(4, longitude);
			s.setString(5, longitude);
			
			ResultSet rs = s.executeQuery();
			
			

			


		}
		catch(SQLException sqle) {
			error(sqle);
		}
		return result;
		
			
			 

	}

	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if ( !connection.isClosed() ) {
				connection.close();
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the programme
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
}

	
