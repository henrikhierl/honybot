package honybot.login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import honybot.sql.SQLHelper;

public class SQLUserDao {
	private static String tableName = "user";
	
	public static User getUserByName(String username){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "SELECT * FROM "+tableName+" WHERE username = ? LIMIT 1";
				try (PreparedStatement query = conn.prepareStatement(sql)){
					query.setString(1, username);
					ResultSet result = query.executeQuery();
					User user = null;

					while (result.next()) {
						user = new User(
								result.getInt("id"),
								username,
								result.getString("twitch_auth"),
								result.getBoolean("enabled"),
								result.getInt("sc2session")
								);
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
	
	public static boolean registerUser(User user){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "INSERT INTO "+tableName+" (username, twitch_auth, enabled) "
						+ "VALUES (?,?,?)";
				try (PreparedStatement query = conn.prepareStatement(sql)){
					query.setString(1, user.getUsername());
					query.setString(2, user.getTwitch_auth());
					query.setBoolean(3, user.isEnabled());
					int result = query.executeUpdate();
					if(result > 0){
						return true;
					}else{
						return false;
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
		return false;
	}
	
	public static boolean updateTwitchAuth(User user){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "UPDATE "+tableName+" SET twitch_auth = ? WHERE username = ?";
				try (PreparedStatement query = conn.prepareStatement(sql)){
					query.setString(1, user.getTwitch_auth());
					query.setString(2, user.getUsername());
					int result = query.executeUpdate();
					if(result > 0){
						return true;
					}else{
						return false;
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
		return false;
	}

	public static boolean updateEnabled(User user){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "UPDATE "+tableName+" SET enabled = ? WHERE username = ?";
				try (PreparedStatement query = conn.prepareStatement(sql)){
					query.setBoolean(1, user.isEnabled());
					query.setString(2, user.getUsername());
					int result = query.executeUpdate();
					if(result > 0){
						return true;
					}else{
						return false;
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
		return false;
	}
	

	public static boolean increaseSession(User user){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "UPDATE "+tableName+" SET sc2session = sc2session + 1 WHERE username = ?";
				try (PreparedStatement query = conn.prepareStatement(sql)){
					query.setString(1, user.getUsername());
					int result = query.executeUpdate();
					if(result > 0){
						return true;
					}else{
						return false;
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
		return false;
	}
	
}
