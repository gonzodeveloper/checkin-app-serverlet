# Checkin_App_Serveret

## A Java Serverlet interface that allows users to search for and log checkin's to "places" via the Google Geocoding API

**Controller**

This module was written in accordence with the  the Model-View-Controller framework. I have built a Java Serverlet, the Controller, which allows front-end pages (likely JSPs) to request information on various places. The serverlet primarily takes queries via HTTP GET requests, which it will parse for parameters and forward accordingly. The front-end will specify the type of query by specifiying the URL parameter "action", along with any other required parameters. For example a GET request could look like "www.your-name.com/Controller?action=geocode&address=ADDRESS_HERE", the Controller will return a JSON object with the geolocation info on the object; "www.your-name.com/Controller?action=usercheckins" will return a JSON object from the database of the given user's previous checkins (note: user must be logged in for this.

In addition to handling GET requests, the Controller also takes POST requests to login users. This prevents the password from showing up in the URL field. The doPost() method passes the login parameters to the Database class which handles them accordingly.

**Model/View**

The "Model" of this framework is the Database class. Since the application is running along side a MySQL server we use the Database to run the JDBC connector, create new "users" in the database, hash their passwords, sanitize inputs, and respond to requests from the Contoller by returning JSON objects with user and "checkin" information. 
 
I have not fully constructed a "View" for this framework, aside from a single page "index.jsp" that can be used to test the applications functionality. You can also run the serverlet and test GET requests by manually typing in URL parameters.

**Database**

The database for the application is a standard MySQL database. I've proveded a script for creating the tables, granting privilages to the serverlet as "webuser", and creating a trigger limit users' to one login per minute. The schema has three tables: users, places, and checkins. The users login names are stored along with hashed passwords and other meta data. Places and their metadata are inserted into the database only once a user has logged a checkin, otherwise the table is empty.

**SETUP**

This should run on a Tomcat Server 2.5 or 3.0, with version Java 8 or newer. In order for the program to complie in an IDE (particularly Eclipse), the included Jar files should be added to the project's buildpath and enabled for export. In order to run on the Tomcat server, the project should be exported as a .war file and placed in the servers root directory (whichever director has the conf file /usr/share/tomcat or /opt/tomcat. Likewise, the Tomcat directory and its subfolders should be given the appropriate permissions via the chmod and chown commands (eclipse will need write and execute permissions for development)
