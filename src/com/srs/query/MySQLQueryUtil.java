//$Id$
package com.srs.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.srs.connection.MySQLConnectionHandler;
import com.srs.exception.ComponentException;
import com.srs.exception.ErrorCode.ComponentErrorCode;

public class MySQLQueryUtil {

	private MySQLQueryUtil() {

	}

	public static void excecuteQuery(String query) throws ComponentException {
		Statement statement;
		try {
			statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			statement.execute(query);
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
	}

	public static int getAlreadyBookedSeatCount(long userId) throws ComponentException {
		try {
			Statement statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT COUNT(*) as count FROM Booking where user_id=" + userId);
			while (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
		return 0;
	}

	public static boolean isValidUser(String emailId, String password) throws ComponentException {
		Statement statement;
		try {
			statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			String query = "SELECT * FROM User where email_id='" + emailId + "' and password='" + password + "'";
			ResultSet resultSet = statement.executeQuery(query);
			return resultSet.next();
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
	}

	public static long getUserId(String emailId) throws ComponentException {
		Statement statement;
		try {
			statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT user_id FROM User where email_Id='" + emailId + "'");
			while (resultSet.next()) {
				return resultSet.getLong("user_id");
			}
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
		return -1L;
	}

	public static void insertBookedSeats(long userId, long showId, Set<Long> seatIds, long paymentId)
			throws ComponentException {
		long bookingTime = System.currentTimeMillis();
		Statement statement;
		try {
			statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			for (long seatId : seatIds) {
				String insertQuery = "insert into Booking(show_id, user_id, seat_id, payment_id, booking_time) values("
						+ showId + "," + userId + "," + seatId + "," + paymentId + "," + bookingTime + ")";
				statement.execute(insertQuery);
			}
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
	}
	
	public static boolean isAnyOneSeatAlreadybooked(long showId, Set<Long> seatIds) throws ComponentException{
		Statement statement;
		try {
			statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
			for (long seatId : seatIds) {
				String existCheck = "select booking_id from Booking where show_id=" + showId + " and seat_id="+ seatId;
				ResultSet resultSet = statement.executeQuery(existCheck);
				if (resultSet.next()) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new ComponentException(ComponentErrorCode.ERROR_IN_SQL_QUERY, e);
		}
	}
}
