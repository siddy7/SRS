
//$Id$

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.srs.connection.MySQLConnectionHandler;
import com.srs.constants.ConfigurationHandler;
import com.srs.constants.ConfigurationHandler.ConfigKey;
import com.srs.query.MySQLQueryUtil;

public class InitializeSystem {

	public static void main(String args[]) throws Exception {
		// create db if not created
		checkAndCreateDatabase();
		// create mysql tables if not created
		checkAndCreateMysqlTables();
		// initialize test data
		loadTestData();
	}

	private static void checkAndCreateDatabase() throws Exception {
		String mysqlURI = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_URI);
		String userName = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_USER);
		String password = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_PASSWORD);
		Connection connection = DriverManager.getConnection("jdbc:" + mysqlURI, userName, password);
		String dbName = ConfigurationHandler.getInstance().getStringValue(ConfigKey.MYSQL_DBNAME);
		String createQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;
		Statement statement = connection.createStatement();
		statement.execute(createQuery);
		connection.close();
	}

	private static void checkAndCreateMysqlTables() throws Exception {
		String userTable = "create table if not exists User (user_id bigint auto_increment primary key, email_id varchar(512) "
				+ "not null, password varchar(50) not null)";
		MySQLQueryUtil.excecuteQuery(userTable);

		String screenTable = "create table if not exists Screen(screen_id bigint auto_increment primary key, is_active boolean not"
				+ " null default 1, screen_name varchar(100) not null)";
		MySQLQueryUtil.excecuteQuery(screenTable);
		String showTable = "create table if not exists Shows(show_id bigint auto_increment primary key, screen_id bigint not null,"
				+ "FOREIGN key (screen_id) REFERENCES Screen(screen_id))";
		MySQLQueryUtil.excecuteQuery(showTable);
		String seatTable = "create table if not exists Seat(seat_id bigint auto_increment primary key, screen_id bigint not "
				+ "null, foreign "
				+ "key (screen_id) references Screen(screen_id), row_name char not null, col_name char not " + "null)";
		MySQLQueryUtil.excecuteQuery(seatTable);
		String bookingTable = "create table if not exists Booking(booking_id bigint auto_increment primary key, show_id bigint not "
				+ "null," + "foreign key (show_id) references Shows(show_id), user_id bigint not null, foreign key "
				+ "(user_id) references User(user_id), seat_id "
				+ "bigint not null, foreign key (seat_id) references Seat(seat_id), payment_id bigint not "
				+ "null, booking_time bigint not null)";
		MySQLQueryUtil.excecuteQuery(bookingTable);
	}

	private static void loadTestData() throws Exception {
		// truncate all tables;
		MySQLQueryUtil.excecuteQuery("SET FOREIGN_KEY_CHECKS = 0");
		String[] tables = new String[] { "Booking", "User", "Seat", "Shows", "Screen" };
		for (String table : tables) {
			String truncateQuery = "truncate " + table;
			MySQLQueryUtil.excecuteQuery(truncateQuery);
		}
		MySQLQueryUtil.excecuteQuery("SET FOREIGN_KEY_CHECKS = 1");

		String userInsert = "insert into User(email_id,password) values('sid@sid.com', 'sidsid')";
		MySQLQueryUtil.excecuteQuery(userInsert);

		String screenInsert = "insert into Screen (screen_name) values('screen 1')";
		MySQLQueryUtil.excecuteQuery(screenInsert);

		String screenGet = "select screen_id from Screen where screen_name='screen 1'";
		Statement statement = MySQLConnectionHandler.getInstance().getConnection().createStatement();
		ResultSet resultSet = statement.executeQuery(screenGet);
		resultSet.next();
		long screenId = resultSet.getLong("screen_id");
		String showInsert = "insert into Shows(screen_id) values (" + screenId + ")";
		MySQLQueryUtil.excecuteQuery(showInsert);

		for (int i = 1; i < 9; ++i) {
			String insertSeat = "insert into Seat(screen_id, row_name, col_name) values(" + screenId + ", 'A', '" + i
					+ "')";
			MySQLQueryUtil.excecuteQuery(insertSeat);
			insertSeat = "insert into Seat(screen_id, row_name, col_name) values(" + screenId + ", 'B', '" + i + "')";
			MySQLQueryUtil.excecuteQuery(insertSeat);
		}

	}
}
