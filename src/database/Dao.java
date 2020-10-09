package database;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dao {

	public int[] fetchTripCount(String dateStr) throws ClassNotFoundException {

		int[] result = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/GrabCaseDB?allowPublicKeyRetrieval=true&useSSL=false", "root",
					"P@ssw0rd");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check connection
		try {
			System.out.println("Established connection to " + connection.getMetaData().getURL());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement preparedStatement = null;
		// Step 2:Create a statement using connection object
		try {
			preparedStatement = connection.prepareStatement(
					"SELECT date_format( tpep_pickup_datetime, '%H' ) as hr, COUNT(*) as total FROM Trips " + "WHERE"
							+ " tpep_pickup_datetime between CONCAT(?,' 00:00:00') AND CONCAT(?,' 23:59:59') "
							+ "GROUP BY" + "	date_format( tpep_pickup_datetime, '%H' ) " + "ORDER BY"
							+ "	date_format( tpep_pickup_datetime, '%H' )");

			preparedStatement.setString(1, dateStr);
			preparedStatement.setString(2, dateStr);

			ResultSet rs = preparedStatement.executeQuery();

			result = new int[24];

			while (rs.next()) {
				int hour = Integer.parseInt(rs.getString("hr"));
				int total = Integer.parseInt(rs.getString("total"));
				result[hour] = total;
			}
			rs.last();

			// check number of rows returned
			if (rs.getRow() == 0) {

				// get datestr and figure out month and year to download
				String[] arr = dateStr.split("-");
				String year = arr[0];
				String month = arr[1];
				System.out.println("year" + year + "mth" + month);

				// Download file (with year and mth)
				String dirName = "/Users/tanyuxuan/Projects/GrabCase";
				try {
					saveFileFromUrlWithJavaIO(dirName + "/" + month + "_" + year + ".csv",
							"https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_" + year + "-" + month
									+ ".csv");
					System.out.println("finished");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Statement statement = null;
				// load into db
				try {
					statement = connection.createStatement();
					statement.executeUpdate("LOAD DATA INFILE '/Users/tanyuxuan/Projects/GrabCase/" + month + "_" + year
							+ ".csv' INTO TABLE GrabCaseDB.Trips FIELDS TERMINATED BY ',' IGNORE 1 LINES");

					// run query again
					rs = preparedStatement.executeQuery();
					result = new int[24];
					while (rs.next()) {
						int hour = Integer.parseInt(rs.getString("hr"));
						int total = Integer.parseInt(rs.getString("total"));
						result[hour] = total;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;

	}

	public int fetchCheapestTime(String startLoc, String endLoc) throws ClassNotFoundException {
		int result = -1;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/GrabCaseDB?allowPublicKeyRetrieval=true&useSSL=false", "root",
					"P@ssw0rd");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Check connection
		try {
			System.out.println("Established connection to " + connection.getMetaData().getURL());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection
					.prepareStatement("SELECT date_format(tpep_pickup_datetime, '%H') as hr, AVG(total_amount) as fare "
							+ "FROM TRIPS WHERE PULocationID=" + startLoc + " AND DOLocationID=" + endLoc
							+ " GROUP BY hr ORDER BY fare LIMIT 1");

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
				result = Integer.parseInt(rs.getString("hr"));
			System.out.println(result);

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

// // Using Java IO
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