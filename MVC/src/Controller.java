
/*
 * Kyle Hart
 * 19 APRIL 2017
 * 
 * Project: CheckIn_App
 * Description: Java serverlet that functions as the "controller" in the MVC model.
 * 			Takes parameters from http get and post requests and either returns json objects to the browser, or takes action in the database.
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;


import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String apiKey = "####################";
	private static Database database;
       
   
    public Controller() {
        super();
        database = new Database();
    }
        

	/**
	 * This doGet method takes parameters from the browser (such as those sent from other pages in action forms), and
	 * produces appropriate output. The main parameter is "action" which will determine output and any other sub-
	 * parameters might be needed to perform this action.
	 * 
	 * "action" = ...
	 * 		"null": nothing happens
	 * 		"geocode": takes parameters "address", returns a json object of the geolocation information on the address
	 * 		"reverse": takes parameters "latlng" (geocoordinates), and returns information such as address and locale to
	 * 					a json object
	 * 		"nearby": takes parameters "location" (geocoordinates), "type" (such as 'restaurant'), and "rankby" (such as
	 * 					'distance'), and returns a json object with information on places nearby
	 * 		"details": takes parameters "placeid" (google place id), and returns json object containing information on the place
	 * 		"map": takes parameters "center" (geocoordinates), "zoom", "size" and "format" (see Google StaticMapsAPI) and
	 * 				returns a jpg image.
	 * 		"checkin": requires parameters "placeid", "pname" (place name), "type", "city" and "state". The user must be logged
	 * 					in for this action to succeed. The database records each check-in "user/place/time-checked-in" and adds an
	 * 					entry with information on the place
	 * 		"usercheckins": no additional parameters. User must be logged in. Prints json object with information on the
	 * 						given user's previous checkins (place, location, time of visit)
	 * 		"userscore": no additional parameters. Prints a json object with information on each user, their hometown, and 
	 * 					their total number of checkins
	 * 		"placescore":    no additional parameters. Promts json object with information on each place, 
	 * 				place name, location, number of checkins.
	 * 
	 * Not the prettiest doGet() but she gets the job done
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		String output = "";
		
		if (action == null){
			PrintWriter out = response.getWriter();
			out.write(output);
			out.close();
		}
		else if (action.equals("geocode")){
			PrintWriter out = response.getWriter();
			output = geocode(request.getParameter("address"));
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		
		else if (action.equals("reverse")){
			PrintWriter out = response.getWriter();
			output = reverseGeocode(request.getParameter("latlng"));
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		else if (action.equals("nearby")){
			PrintWriter out = response.getWriter();
			output = getNearby(request.getParameter("location"), request.getParameter("type"),
					request.getParameter("rankby"));
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		else if (action.equals("details")){
			PrintWriter out = response.getWriter();
			output = getPlaceDetails(request.getParameter("placeid"));
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		else if (action.equals("map")){
		    ServletOutputStream out = response.getOutputStream();
			response.setContentType("image/jpeg");  

		    BufferedImage image = this.getStaticMap(request.getParameter("center"), request.getParameter("markers"), request.getParameter("zoom"), 
		    		request.getParameter("size"), request.getParameter("format"));
		    ImageIO.write(image, "jpg", out);
			out.close();
		}
		else if (action.equals("checkin")){
			PrintWriter out = response.getWriter();
			if(database.isLoggedIn() == false)
				output = "Not logged in";
			else{
				
				database.addPlace(request.getParameter("placeid"), request.getParameter("pname"),
						request.getParameter("type"), request.getParameter("city"), request.getParameter("state"));
				database.checkin(request.getParameter("placeid"));
				output = "Checked into " + request.getParameter("placename");
			}
			response.setContentType("text/html");
			out.write(output);
			out.close();
		}
		else if (action.equals("usercheckins")){
			PrintWriter out = response.getWriter();
			if(database.isLoggedIn() == false)
				output = "Not Logged in";
			else
				output = database.getUserCheckins();
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		else if (action.equals("placescore")){
			PrintWriter out = response.getWriter();
			output = database.getPlaceScoreboard();
			response.setContentType("application/json");
			out.write(output);
			out.close();
		}
		else if (action.equals("userscore")){
			PrintWriter out = response.getWriter();
			output = database.getUserScoreboard();
			response.setContentType("application/json");
			out.write(output);
		}
		else {
			PrintWriter out = response.getWriter();
			out.write(output);
			out.close();
		}	
		
	}
	/**
	 * 
	 * "login": takes parameters "handle" and "password" and attempts to login in the user. Fails if user
	 * 			does not exist or if password is bad. (Note: for security reasons this should be sent over 
	 * 			an ssl connection or the client should hash the password in the browser )
	 * 
	 *  @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String output = "";
		String action = request.getParameter("action");
		if(action.equals("login")){
			PrintWriter out = response.getWriter();
			database.authenticate(request.getParameter("handle"), request.getParameter("password"));
			output = "Logged in = " + String.valueOf(database.isLoggedIn());
			response.setContentType("text/html");
			out.write(output);
			out.close();
		}
		doGet(request, response);
	}
	/*
	 * Returns json object with information for given address (See Google Geocoding API)
	 */
	private String geocode(String address){
		if (address == null)
			address = "1600+Amphitheatre+Parkway,+Mountain+View,+CA";		//Google HQ
		address = address.replaceAll(" ", "+");
		String output = this.queryGoogle("https://maps.google.com/maps/api/geocode/json?address="+ address +"&key="+ apiKey);
		return output;
	}
	
	/*
	 * Returns json object with information for given location (See Google ReverseGeocoding API)
	 */
	private String reverseGeocode(String latlng){
		if(latlng == null)
			latlng = "37.4220147,-122.0840693";			// GOOGLE HQ
		String output = this.queryGoogle("https://maps.google.com/maps/api/geocode/json?latlng="+ latlng +"&key="+ apiKey);
		return output;
	}
	
	/*
	 * Returns json object with information on places nearby given lat-long coordinates.
	 * Can be refined with type and rankby (See Google Geocoding API)
	 */
	private String getNearby(String location, String type, String rankby){
    	if(location == null)
    		location = "37.4220147,-122.0840693";		//GOOGLE HQ
    	if(type == null)
    		type = "restaurant";
    	if(rankby == null)
    		rankby = "distance";
    	
    	String output = this.queryGoogle("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" 
    						+ location +"&type=" + type + "&rankby=" + rankby +"&key="+ apiKey );
       return output;
    }
    
	/*
	 * Returns json object with information for given placeid(See Google Places API)
	 */
    private String getPlaceDetails(String placeid){
    	if(placeid == null)
    		placeid = "ChIJK6v1W2cSAHwRK4VkZ1O45Fw";  // The Shack in Hawaii Kai
    	String output = this.queryGoogle("https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeid 
    			+"&key="+ apiKey);
    	return output;
    }
    /*
	 * Returns jpg image of google map of given center(lat-long), can be formated with "zoom"
	 * "size" and "format" parameters (See Google StaticMaps API)
	 */
    private BufferedImage getStaticMap(String center, String markers, String zoom, String size, String format){
    	if (center == null)
    		center = "37.4220147,-122.0840693";	// defaults to Googleplex
    	if (markers == null)
    		markers = "color:red|" + center;
    	if(zoom == null)
    		zoom = "15";
    	if(size == null)
    		size = "400x400";
    	if(format == null)
    		format = "jpg";
    	URL url;
		BufferedImage image = null;
    	try {
			url = new URL("https://maps.googleapis.com/maps/api/staticmap?center=" + center + "&markers=" 
					+ markers + "&zoom=" + zoom + "&size=" + size + "&format=" + format);
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return image;
    }
    /*
     *  Auxiliary method that interfaces with google's we APIs to retrieve information for other methods
     */
    private String queryGoogle(String url){
    	 HttpGet httpGet = new HttpGet(url);
         HttpClient client =  HttpClientBuilder.create().build();
         HttpResponse response;
         StringBuilder stringBuilder = new StringBuilder();

         try {
             response = client.execute(httpGet);
             HttpEntity entity = response.getEntity();
             InputStream stream = entity.getContent();
             int k;
             while ((k = stream.read()) != -1) 
                 stringBuilder.append((char) k);
             
         } catch (IOException e) {
         	 e.printStackTrace();
             } 
        return stringBuilder.toString(); 
    }
    /*
     * Automatically called by browser when serverlet is "destroyed", only needed to close connection with database
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy(){
    	database.closeConn();
    }
    
	
}
