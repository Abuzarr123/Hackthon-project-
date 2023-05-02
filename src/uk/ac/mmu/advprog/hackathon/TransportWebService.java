package uk.ac.mmu.advprog.hackathon;
import static spark.Spark.get;
import static spark.Spark.port;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the setting up and starting of the web service
 * You will be adding additional routes to this class, and it might get quite large
 * Feel free to distribute some of the work to additional child classes, like I did with DB
 * @author You, Mainly!
 */
public class TransportWebService {

	/**
	 * Main program entry point, starts the web service
	 * @param args not used
	 */
	public static void main(String[] args) {		
		port(8088);
		
		//Simple route so you can check things are working...
		//Accessible via http://localhost:8088/test in your browser
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}			
		});
		/**
		@param request The request object containing the client's request.

		@param response The response object to which the server will send its response.

		@return The number of stops in the specified locality, or the string "Invalid Request" if the 'locality' parameter is not present or is empty.

		@throws Exception If an error occurs while handling the request.
		*/
		
		get("/stopcount",new Route() {
			//route made for a path that is getting the stopcount

			@Override
			public Object handle(Request request, Response reponse) throws Exception {
				// this handles the method for processing the request and returning a reponse
				try(DB db = new DB()){
					String l = request.queryParams("locality");
					//checks that the locality parameter is present 
					
					if ( l == null || l.isEmpty()) {
						return ("Invalid Request");
					}
					//if the locality parameter is not responding return with a error message
					
					return  db.getNumberOfStopsInParticularLocality(l);
					// if the locality parameter is present then the DB object will get the number of stops in that specfic locality and return it 
				}
			}
			
		});
		/**
		@param request The request object containing the client's request.

		@param response The response object to which the server will send its response.

		@return A JSON array containing the stop information for the specified locality and type, or the string "Invalid Request" if the 'locality' parameter is not present or is empty.

		@throws Exception If an error occurs while handling the request.
		*/
		
		get("/stops", new Route(){
			// route made for a path that is getting the stopcount
			@Override
			public Object handle(Request request, Response reponse) throws Exception {
		// this method handles the request and does the reponse 

				try(DB db = new DB()){
					String l = request.queryParams("locality");
					String t = request.queryParams("type");
					// it will try to retrieve the locality and type parameters from the request
					
					reponse.header("Content-Type","application/json");
					//the content-type header of the reponse will be set to application/json before the data retrieved from the database is returned
					if (l == null || l.isEmpty()) {
						return "Invalid request";
					}//if the locality parameter is not present then it will return invalid request
					
					return db.gettypes(l,t);

					
				}
			}
			
		});
		get("/nearest", new Route(){
			//this is a route made for a path that is getting the nearest path

			@Override
			public Object handle(Request request, Response reponse) throws Exception {
				// this is the method that handles the request and does the response.
				try(DB db = new DB()) {
					String t = request.queryParams("type");
					String latitude = request.queryParams("latitude");
					String longitude = request.queryParams("longitude");
					//it will try to retrieve the type, latitude and  parameters from the request.
					return db.getspecficroute(t,latitude,longitude) ;
					//the data this is returned from the database will be retrieved in XML

				}
			}
			
		});
		
		System.out.println("Server up! Don't forget to kill the program when done!");
	}

}
