<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MVC Function Tests</title>
</head>
<body>

<strong>Login</strong><br/>
<form action="/MVC/Controller" method="post">
    <input type="hidden" name="action" value="login" />	
	USER HANDLE: <br/>
	<input type="text" name="handle" /><br/>
	PASSWORD <br/>
	<input type="password" name="password" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Geocode</strong><br/>
	<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="geocode" />
	Address<br/>
	<input type="text" name="address" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Reverse Geocode</strong><br/>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="reverse" />
	Lat-Long Coordinates<br/>
	<input type="text" name="latlng" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Nearby Places</strong><br/>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="nearby" />
	Location<br/>
	<input type="text" name="location" /><br/>
	Type<br/>
	<input type="text" name="type" /><br/>
	Rank By <br/>
	<input type="text" name="rankby" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Place Details</strong><br/>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="details" />
	Place-ID<br/>
	<input type="text" name="placeid" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Static Map</strong><br/>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="map" />
	Center (lat-long, NO SPACES)<br/>
	<input type="text" name="center" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>Check-in</strong><br/>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="checkin" />
	Place ID<br/>
	<input type="text" name="placeid" /><br/>
	Place Name<br/>
	<input type="text" name="pname" /><br/>
	Type<br/>
	<input type="text" name="type" /><br/>
	City<br/>
	<input type="text" name="city" /><br/>
	State<br/>
	<input type="text" name="state" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>View YOUR Checkins</strong>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="usercheckins" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>View User Scoreboard</strong>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="userscore" />
	<input type="submit" value="OK" /> 
</form><p/>

<strong>View Place Scoreboard</strong>
<form action="/MVC/Controller" method="get">
	<input type="hidden" name="action" value="placescore" />
	<input type="submit" value="OK" /> 
</form><p/>

</body>
</html>