package honybot.bot.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import honybot.bot.Command.Command;
import honybot.bot.dao.CustomCommandsDao;
import honybot.sql.SQLHelper;

public class SQLCustomCommandsDao implements CustomCommandsDao {
	private static String tableName = "commands";

	public static boolean addCommand(String username, String command, String text, int cost, boolean whisper,
			boolean takesUserInput, int permission, int vip) {
		System.out.println("adding command");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "REPLACE INTO " + tableName
						+ "(username, command, text, cost, whisper, takesUserInput, permission, vip) "
						+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, username);
					preparedStatement.setString(2, command);
					preparedStatement.setString(3, text);
					preparedStatement.setInt(4, cost);
					preparedStatement.setBoolean(5, whisper);
					preparedStatement.setBoolean(6, takesUserInput);
					preparedStatement.setInt(7, permission);
					preparedStatement.setInt(8, vip);
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
		return false;
	}

	public static boolean removeCommand(String username, String command) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "DELETE FROM " + tableName + " WHERE username= ? AND command= ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				preparedStatement.setString(2, command);
				preparedStatement.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static Map<String, Command> getCommands(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE username = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				HashMap<String, Command> map = new HashMap<>();
				String command = "";
				String text = "";
				int cost = 0;
				boolean whisper;
				boolean takesUserInput = false;
				int permission = 0;
				int vip = 0;

				while (result.next()) {
					command = result.getString(2);
					text = result.getString(3);
					cost = result.getInt(4);
					whisper = result.getBoolean(5);
					takesUserInput = result.getBoolean(6);
					permission = result.getInt(7);
					vip = result.getInt(8);
					Command com = new Command(command, text, cost, whisper, takesUserInput, permission, vip);
					map.put(command, com);
				}
				return map;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
