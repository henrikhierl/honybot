package honybot.web.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import honybot.sql.SQLHelper;
import honybot.web.WebUser;
import honybot.web.dao.UserDao;

public class UserDaoSQL implements UserDao {
	private static String tableName = "users";
	public Connection conn = null;

	public boolean connect() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(SQLHelper.getConnectionString());
			System.out.println("Successfully connected to DB");
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
			return false;
		} catch (SQLException e) {
			System.out.println("Unable to connect to database");
			System.out.println(e);
			return false;
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isConnected() {
		if (conn != null) {
			return true;
		}
		return false;
	}

	public Connection getInstance() {
		if (conn == null) {
			connect();
		}
		return conn;
	}

	@Override
	public WebUser getUserbyUsername(String username){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "SELECT * FROM " + tableName + " WHERE username = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, username);
					ResultSet result = preparedStatement.executeQuery();

					while (result.next()) {
						int id = result.getInt(1);
						String name = result.getString(2);
						String email = result.getString(3);
						String password = result.getString(4);
						boolean isEnabled = result.getBoolean(5);

						WebUser user = new WebUser(id, name, email, password, isEnabled);
						return user;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean registerUser(WebUser user){
		if (getUserbyUsername(user.getUsername()) == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
					String sql = "INSERT INTO " + tableName + "(username, email, password, enabled, sc2session) "
							+ "VALUES(?, ?, ?, ?, 0)";
					try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
						preparedStatement.setString(1, user.getUsername());
						preparedStatement.setString(2, user.getEmail());
						preparedStatement.setString(3, user.getPassword());
						preparedStatement.setBoolean(4, user.isEnabled());
						preparedStatement.executeUpdate();
						return true;
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}


	public boolean registerUserOld(WebUser user) {
		if (getUserbyUsername(user.getUsername()) == null) {
			conn = getInstance();
			if (conn != null) {
				String table = "users";
				String sql = "INSERT INTO " + table + "(username, email, password, enabled) "
						+ "VALUES(?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, user.getUsername());
					preparedStatement.setString(2, user.getEmail());
					preparedStatement.setString(3, user.getPassword());
					preparedStatement.setBoolean(4, user.isEnabled());
					preparedStatement.executeUpdate();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				System.out.println("conn is null");
				// connection is null
			}
			return false;
		} else {
			// User already exists
			return false;
		}
	}

}
