package database;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class Dao {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/GrabCaseDB?allowPublicKeyRetrieval=true&useSSL=false";

// Database credentials
	static final String USER = "root";
	static final String PASS = "P@ssw0rd";

	public int[] fetchTripCount(String dateStr) throws ClassNotFoundException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;

		int[] result = null;

		try {
			// STEP 1: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 2: Open a connection
			System.out.println("Connecting to the database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 3: Get requested month and year
			String[] arr = dateStr.split("-");
			String year = arr[0];
			String month = arr[1];
			System.out.println("Query year:" + year + " Query month:" + month);

			// STEP 4: Execute query - to check if the month has been loaded
			System.out.println("Executing query ... check if date is already loaded in DB");
			preparedStatement = connection
					.prepareStatement("SELECT COUNT(*) as cnt FROM LoadHistory WHERE yr=" + year + " AND mth=" + month);
			ResultSet rs = preparedStatement.executeQuery();
			int count = -1;
			// Retrieve count col name
			if (rs.next())
				count = Integer.parseInt(rs.getString("cnt"));

			// if not loaded into db yet
			if (count == 0) {
				System.out.println("Data is NOT in DB yet.");

				// STEP 4a: Download CSV file
				String dirName = "/Users/tanyuxuan/Projects/GrabCase";
				try {
					System.out.println("Downloading csv...");
					saveFileFromUrlWithJavaIO(dirName + "/" + month + "_" + year + ".csv",
							"https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_" + year + "-" + month
									+ ".csv");
					System.out.println("Download finished");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// STEP 4b: Execute query - To check total number of months in db
				System.out.println("Executing query ...  check number of months in db");
				preparedStatement = connection.prepareStatement("SELECT COUNT(*) as NumMths FROM LoadHistory LIMIT 1");
				rs = preparedStatement.executeQuery();
				int numMths = 0;
				// Retrieve NumMths by col name
				if (rs.next()) {
					numMths = Integer.parseInt(rs.getString("NumMths"));
				}
				System.out.println("numMths in db = " + numMths);

				// if db has 3 months
				if (numMths == 3) {
					// DELETION

					// STEP 4c: Execute query - to get mth year to be deleted (oldest)
					System.out.println("Executing query.. to get month & year to be deleted");
					preparedStatement = connection.prepareStatement("SELECT mth, yr FROM LoadHistory LIMIT 1");
					rs = preparedStatement.executeQuery();
					int deleteMth = -1;
					int deleteYr = -1;

					if (rs.next()) {
						deleteMth = Integer.parseInt(rs.getString("mth"));
						deleteYr = Integer.parseInt(rs.getString("yr"));
					}
					System.out.println("year to delete:" + deleteYr + " mth to delete:" + deleteMth);

					// STEP 4d: Execute query - to delete data from trips db - not working!
					System.out.println("Executing query.. to delete data from trips db");
					preparedStatement = connection
							.prepareStatement("DELETE FROM Trips WHERE MONTH(tpep_pickup_datetime)=" + deleteMth
									+ " AND YEAR(tpep_pickup_datetime)=" + deleteYr);
					preparedStatement.execute();

					// STEP 4e: Execute query - to delete data from load history - not working!
					System.out.println("Executing query.. to delete data from load history db");
					preparedStatement = connection
							.prepareStatement("DELETE FROM LoadHistory WHERE mth=" + deleteMth + " AND yr=" + deleteYr);
					preparedStatement.execute();

				}
				// STEP 5: Execute query - load data into db - have to use statement instead of
				// prepared statement!
				System.out.println("Executing query ... loading data from csv into db!");

				statement = connection.createStatement();
				statement.executeUpdate("LOAD DATA INFILE '/Users/tanyuxuan/Projects/GrabCase/" + month + "_" + year
						+ ".csv' INTO TABLE GrabCaseDB.Trips FIELDS TERMINATED BY ',' IGNORE 1 LINES");

				// STEP 6: Execute query - update load history
				System.out.println("Executing query ... updating load history");
				preparedStatement = connection
						.prepareStatement("INSERT INTO LoadHistory values (" + month + "," + year + ")");
				preparedStatement.execute();

			} else {
				System.out.println("Data is already inside db!");
			}

			// STEP 7: Execute query - run query to get the result
			System.out.println("Executing query ... fetching results...");
			preparedStatement = connection.prepareStatement(
					"SELECT date_format( tpep_pickup_datetime, '%H' ) as hr, COUNT(*) as total FROM Trips " + "WHERE"
							+ " tpep_pickup_datetime between CONCAT(?,' 00:00:00') AND CONCAT(?,' 23:59:59') "
							+ "GROUP BY" + "	date_format( tpep_pickup_datetime, '%H' ) " + "ORDER BY"
							+ "	date_format( tpep_pickup_datetime, '%H' )");
			preparedStatement.setString(1, dateStr);
			preparedStatement.setString(2, dateStr);
			rs = preparedStatement.executeQuery();

			// Retrieve count for each hour with col name and populate into result array
			result = new int[24];
			while (rs.next()) {
				int hour = Integer.parseInt(rs.getString("hr"));
				int total = Integer.parseInt(rs.getString("total"));
				result[hour] = total;
			}
			System.out.println("Obtained results!");

			return result;

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					connection.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

		return result;

	}

	public int fetchCheapestTime(String startLoc, String endLoc) throws ClassNotFoundException {
		int result = -1;
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			// STEP 1: Register JDBC driver

			Class.forName(JDBC_DRIVER);

			// STEP 2: Open a connection
			System.out.println("Connecting to the database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected db successfully...");

			// STEP 3: Execute a query - to get min hour
			System.out.println("Creating statement...");
			preparedStatement = connection
					.prepareStatement("SELECT date_format(tpep_pickup_datetime, '%H') as hr, AVG(total_amount) as fare "
							+ "FROM TRIPS WHERE PULocationID=" + startLoc + " AND DOLocationID=" + endLoc
							+ " GROUP BY hr ORDER BY fare LIMIT 1");
			ResultSet rs = preparedStatement.executeQuery();
			// Retrieve min hour from col name
			if (rs.next())
				result = Integer.parseInt(rs.getString("hr"));

			rs.close();

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					connection.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

		return result;
	}

	// Method to download file into path Using Java IO
	public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
			throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(fileUrl).openStream());
			fout = new FileOutputStream(fileName);
			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
	}
}