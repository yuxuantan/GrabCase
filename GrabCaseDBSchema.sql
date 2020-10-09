CREATE DATABASE GrabCaseDB;
USE GrabCaseDB;

-- create table for trips: to improve
CREATE TABLE Trips (
	VendorID varchar(20), -- What is vendor ID ? only 1 or 2 - 2 vendors with yellow cabs maybe - tiny int
    tpep_pickup_datetime datetime,  -- time of pickup
    tpep_dropoff_datetime datetime, -- time of dropoff
    passenger_count varchar(20), -- how many customers in the car tinyint
    trip_distance float,-- 2dp, total 6 digits should definitely be enough (miles) - decimal(6,2)
    RatecodeID varchar(20), --  FINAL rate code in effect at the end of the trip. (normally 1-6) small int should be enough. I saw 1, and I saw 99 max. tinyint
-- 1= Standard rate 2=JFK 3=Newark 4=Nassau or Westchester 5=Negotiated fare 6=Group ride
    store_and_fwd_flag char(1), --  Y or N (not important - stored in vehicle or immediate send to vendor bc internet)
    PULocationID varchar(20),-- 3 digit max. TLC taxi zone in which taximeter was engaged - smallint
    DOLocationID varchar(20),-- 3 digit max. TLC taxi zone ... disengaged - smallint
    payment_type varchar(20),-- 1-6 (not impt) - tinyint
    fare_amount float,-- 1dp or 0 dp. time and dist fare - decimal(6,2)
    extra float,-- 1dp or 0dp decimal(6,2)
    mta_tax float, -- mostly 0.5 else 0 decimal(6,2)
    tip_amount float,-- max 2dp decimal(6,2)
    tolls_amount float,-- max 2dp, else 0 decimal(6,2)
    improvement_surcharge float,--  0.3 decimal(6,2)
    total_amount float,-- 2dp max decimal(6,2)
    congestion_surcharge decimal(6,2) -- 2dp max - not inside decimal(6,2) - this one cannot float first row already truncated
);

-- Create table for location mapping
CREATE TABLE LocationMappings (
	LocationID int PRIMARY KEY,
    Borough varchar(50),
    Zone varchar(50),
    service_zone varchar(50)
);


-- Manual Bootstrap data downloaded to disk 
-- LOAD DATA 
-- 	INFILE '/Users/tanyuxuan/Projects/GrabCase/05_2020.csv'
--     INTO TABLE GrabCaseDB.Trips
--     FIELDS TERMINATED BY ','
--     IGNORE 1 LINES;

-- LOAD Taxi zone mapping data from local disk
-- LOAD DATA 
-- 	INFILE '/Users/tanyuxuan/Projects/GrabCase/taxi+_zone_lookup.csv'
--     INTO TABLE GrabCaseDB.LocationMappings
--     FIELDS TERMINATED BY ','
--     IGNORE 1 LINES;
 