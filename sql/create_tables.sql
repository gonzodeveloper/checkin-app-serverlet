/*
* Kyle Hart
* 30 APR 2017
* 
* Project: CheckIn_App
* Description: Creates the schema/tables to be used by the check-in application.
* 		Tables include users, places and checkins.
*		Also creates trigger that limits users to 1 "checkin" per minute
*		Creates 'webuser' which will be used by the application
*/
CREATE DATABASE IF NOT EXISTS checkin_app;

USE checkin_app;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS places;
DROP TABLE IF EXISTS checkins;

CREATE USER 'webuser'@'localhost' 
IDENTIFIED BY 'C@shRul3sEverything@roundMe';
GRANT SELECT, UPDATE, INSERT 
	ON checkin_app.* 
    TO 'webuser'@'localhost';

CREATE TABLE users (
    uid INT NOT NULL AUTO_INCREMENT,
    handle VARCHAR(16) NOT NULL UNIQUE,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    password VARCHAR(64),
    CONSTRAINT USER_PK PRIMARY KEY (uid)
);

CREATE TABLE places (
    pid VARCHAR(256) NOT NULL, 
    pname VARCHAR(50) NOT NULL,
    ptype VARCHAR(20),
    city VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    CONSTRAINT PLACE_PK PRIMARY KEY (pid)
);

CREATE TABLE checkins (
    ch_id INT NOT NULL AUTO_INCREMENT,
    uid INT NOT NULL,
    pid VARCHAR(256) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    CONSTRAINT CHECKIN_PK PRIMARY KEY (ch_id),
    CONSTRAINT CHK_USER_FK FOREIGN KEY (uid)
        REFERENCES users (uid)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT CHK_PLACE_FK FOREIGN KEY (pid)
        REFERENCES places (pid)
        ON UPDATE CASCADE ON DELETE CASCADE
);
  
/*
* Trigger limits checkins for each user to 1 per minute
*/  

DELIMITER $$

CREATE TRIGGER frequency_check
BEFORE INSERT ON checkins
FOR EACH ROW
BEGIN
    DECLARE msg VARCHAR(100);
    IF (TIME_TO_SEC(TIMEDIFF( NOW(), (SELECT MAX(TIMESTAMP(date, time))
										FROM checkins 
										WHERE uid = NEW.uid))) < 60) 
								THEN
        set msg = "Checkins limited to once per minute for every user";
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;
END$$

DELIMITER ;

