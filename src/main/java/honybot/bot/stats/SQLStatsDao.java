package honybot.bot.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import honybot.login.User;
import honybot.sql.SQLHelper;

public class SQLStatsDao {

	public static boolean updateStat(String username, String stat, int amount) {
		try {
			String tableName = "chatstats";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO " + tableName + " (username, stat, value) "
					+ "VALUES(?,?,?) "
					+ "ON DUPLICATE KEY UPDATE value = value + VALUES(value)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				preparedStatement.setString(2, stat);
				preparedStatement.setInt(3, amount);
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

	public static int getStat(String stat) {
		try {
			String tableName = "chatstats";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT SUM(value) FROM chatstats WHERE stat = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, stat);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static List<ChannelViewerStat> getChannelViewerStats (User user, Timestamp start, Timestamp end) {
		return getChannelViewerStats (user.getId(), start, end);
	}
	
	public static List<ChannelViewerStat> getChannelViewerStats (int user_id, Timestamp start, Timestamp end) {
		List<ChannelViewerStat> stats = new ArrayList<>();
		try {
			String tableName = "channel_viewer_stat";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE user_id = ? AND timestamp between ? AND ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, user_id);
				preparedStatement.setTimestamp(2, start);
				preparedStatement.setTimestamp(3, end);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					ChannelViewerStat stat = new ChannelViewerStat(
								result.getInt("id"),
								result.getInt("user_id"),
								result.getInt("viewer"),
								result.getTimestamp("timestamp")
							);
					stats.add(stat);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return stats;
	}
	
}
