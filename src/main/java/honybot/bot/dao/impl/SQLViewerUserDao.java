package honybot.bot.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.jibble.pircbot.User;

import honybot.bot.ViewerUser;
import honybot.bot.dao.ViewerUserDao;
import honybot.sql.SQLHelper;

public class SQLViewerUserDao implements ViewerUserDao {
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ArrayList<ViewerUser> getAllViewers(String channelname) {
		ArrayList<ViewerUser> users = new ArrayList<>();

		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				String sql = "SELECT * FROM " + tableName + " WHERE channel = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					while (result.next()) {
						ViewerUser user = new ViewerUser(
								result.getString(2),
								result.getInt(3),
								result.getInt(4),
								result.getInt(5),
								result.getInt(6),
								result.getInt(7),
								result.getInt(8),
								result.getInt(9),
								result.getInt(10)
						);
						users.add(user);
					}
					return users;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static ArrayList<ViewerUser> getViewersPaginated(String channelname, int offset, int perPage) {
		ArrayList<ViewerUser> users = new ArrayList<>();

		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				String sql = "SELECT * FROM " + tableName + " WHERE channel = ? AND name <> 'honybot' ORDER BY points DESC LIMIT ?,?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					preparedStatement.setInt(2, offset);
					preparedStatement.setInt(3, perPage);
					ResultSet result = preparedStatement.executeQuery();
					while (result.next()) {
						ViewerUser user = new ViewerUser(
								result.getString(2),
								result.getInt(3),
								result.getInt(4),
								result.getInt(5),
								result.getInt(6),
								result.getInt(7),
								result.getInt(8),
								result.getInt(9),
								result.getInt(10)
						);
						users.add(user);
					}
					return users;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static int getViewerAmount(String channelname) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE channel = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				ResultSet result = preparedStatement.executeQuery();
				while (result.next()) {
					return result.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	public static int getViewerAmount() {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					Statement query = conn.createStatement()) {

				String sql = "SELECT COUNT(DISTINCT name) AS amount FROM " + tableName;
				ResultSet result = query.executeQuery(sql);
				while (result.next()) {
					return result.getInt("amount");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	public static ViewerUser getViewerByName(String channelname, String username) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM " + tableName + " WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				preparedStatement.setString(2, username);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					ViewerUser user = new ViewerUser(
							result.getString(2),
							result.getInt(3),
							result.getInt(4),
							result.getInt(5),
							result.getInt(6),
							result.getInt(7),
							result.getInt(8),
							result.getInt(9),
							result.getInt(10)
					);
					return user;
				}
				return null;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static boolean addNewViewer(String channelname, String username, int points) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "INSERT INTO " + tableName + "(channel, name, points) VALUES(?, ?, ?)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
				preparedStatement.setString(1, channelname);
				preparedStatement.setString(2, username);
				preparedStatement.setInt(3, points);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				return true;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static boolean importViewers(String channelname, ArrayList<ViewerUser> users){
		
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "INSERT IGNORE INTO " + tableName + " (channel, name, points) VALUES(?, ?, ?)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
				
				for(ViewerUser user : users){
					preparedStatement.setString(1, channelname);
					preparedStatement.setString(2, user.getName());
					preparedStatement.setInt(3, user.getPoints());
					preparedStatement.addBatch();
				}
				preparedStatement.executeUpdate();
				preparedStatement.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static boolean hasViewer(String channelname, String username) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT * FROM " + tableName + " WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				preparedStatement.setString(2, username);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return true;
				}
			} catch (SQLException e) {
			}
		} catch (Exception e1) {
		}
		return false;
	}

	public static boolean changePointsWithTimer(String channel, User[] users, int amount, int vipBonus, boolean stacking) {
		stacking = true;
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql;
			if (stacking) {
				sql = "UPDATE " + tableName + " SET points = (points + ? + (vip * ?) ) WHERE channel = ? AND name = ?";
			} else {
				// TODO implement stacking = false;
				sql = "UPDATE " + tableName + " SET points = (points + ? + (vip * ?) ) WHERE channel = ? AND name = ?";
			}
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				for (int i = 0; i < users.length; i++) {
					preparedStatement.setInt(1, amount);
					preparedStatement.setInt(2, vipBonus);
					preparedStatement.setString(3, channel);
					preparedStatement.setString(4, users[i].getNick());
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean changePointsWithTimer(String channel, ArrayList<String> users, int amount, int vipBonus, boolean stacking) {
		stacking = true;
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql;
			if (stacking) {
				sql = "UPDATE " + tableName + " SET points = (points + ? + (vip * ?) ) WHERE channel = ? AND name = ?";
			} else {
				// TODO implement stacking = false;
				sql = "UPDATE " + tableName + " SET points = (points + ? + (vip * ?) ) WHERE channel = ? AND name = ?";
			}
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				for (int i = 0; i < users.size(); i++) {
					preparedStatement.setInt(1, amount);
					preparedStatement.setInt(2, vipBonus);
					preparedStatement.setString(3, channel);
					preparedStatement.setString(4, users.get(i));
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean changePoints(String channel, User[] users, int amount) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "UPDATE " + tableName + " SET points = (points + ?) WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				for (int i = 0; i < users.length; i++) {
					preparedStatement.setInt(1, amount);
					preparedStatement.setString(2, channel);
					preparedStatement.setString(3, users[i].getNick());
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean changePoints(String channel, ArrayList<String> users, int amount) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "UPDATE " + tableName + " SET points = (points + ?) WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				for (int i = 0; i < users.size(); i++) {
					preparedStatement.setInt(1, amount);
					preparedStatement.setString(2, channel);
					preparedStatement.setString(3, users.get(i));
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean changePoints(String channel, String user, int amount) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "UPDATE " + tableName + " SET points = (points + ?) WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, amount);
				preparedStatement.setString(2, channel);
				preparedStatement.setString(3, user);
				preparedStatement.addBatch();
				preparedStatement.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean addTime(String channel, ArrayList<String> viewers, int amount) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "UPDATE " + tableName + " SET time = (time + ?) WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				for (int i = 0; i < viewers.size(); i++) {
					preparedStatement.setInt(1, amount);
					preparedStatement.setString(2, channel);
					preparedStatement.setString(3, viewers.get(i));
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean setVIP(String channelname, String username, int newLevel) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "UPDATE " + tableName + " SET vip = (?) WHERE channel = ? AND name = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, newLevel);
				preparedStatement.setString(2, channelname);
				preparedStatement.setString(3, username);
				preparedStatement.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static LinkedList<ViewerUser> getAllVips(String channelname, int minLevel) {
		LinkedList<ViewerUser> vips = new LinkedList<>();

		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE channel = ? AND vip >= ? ORDER BY vip DESC";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				preparedStatement.setInt(2, minLevel);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					ViewerUser user = new ViewerUser(
							result.getString(2),
							result.getInt(3),
							result.getInt(4),
							result.getInt(5),
							result.getInt(6),
							result.getInt(7),
							result.getInt(8),
							result.getInt(9),
							result.getInt(10)
					);
					vips.add(user);
				}
				return vips;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static int getVipsWithLevelAmount(String channelname, int level) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE channel = ? AND vip = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				preparedStatement.setInt(2, level);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getInt(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	public static int getVipAmount(String channelname) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE channel = ? AND vip > 0 ";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getInt(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	public static LinkedList<ViewerUser> getAllVipsPaginated(String channelname, int offset, int perPage) {
		LinkedList<ViewerUser> vips = new LinkedList<>();

		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE channel = ? AND vip > 0 ORDER BY vip DESC LIMIT ?,?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				preparedStatement.setInt(2, offset);
				preparedStatement.setInt(3, perPage);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					ViewerUser user = new ViewerUser(
							result.getString(2),
							result.getInt(3),
							result.getInt(4),
							result.getInt(5),
							result.getInt(6),
							result.getInt(7),
							result.getInt(8),
							result.getInt(9),
							result.getInt(10)
					);
					vips.add(user);
				}
				return vips;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static int getMaxVIP(String username) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT vip FROM " + tableName + " WHERE channel = ? ORDER BY vip DESC LIMIT 1";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getInt(1);
				}
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param channel
	 *            - Streamer Channel
	 * @param user
	 *            - Name of viewer
	 * @param outcome
	 *            - win/true or lose/false
	 * @param amount
	 *            - points won/lost
	 * @param betValue
	 *            - points bet
	 */

	public static boolean updateBetStats(String channel, String user, boolean outcome, int amount, int betValue) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				int won = 0;
				if (outcome) {
					won++;
				}
				String pointDifference; // pointswon or pointslost
				if (outcome) {
					pointDifference = " pointswon = (pointswon + ?)";
				} else {
					pointDifference = " pointslost = (pointslost + ?)";
				}
				String sql = "UPDATE " + tableName + " SET bets = (bets + 1), " + "betswon = (betswon + ?), "
						+ pointDifference + ", " + "totalbetvalue = (totalbetvalue + ?) "
						+ "WHERE channel = ? AND name = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setInt(1, won);
					preparedStatement.setInt(2, amount);
					preparedStatement.setInt(3, betValue);
					preparedStatement.setString(4, channel);
					preparedStatement.setString(5, user);
					preparedStatement.executeUpdate();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static ViewerUser getGlobalBetStats(String channelname) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT SUM(points), SUM(time), SUM(bets), SUM(betswon), SUM(pointswon), SUM(pointslost), SUM(totalbetvalue), SUM(vip) "
					+ "FROM " + tableName + " WHERE channel = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					ViewerUser user = new ViewerUser(
							"stats",
							result.getInt(1),
							result.getInt(2),
							result.getInt(3),
							result.getInt(4),
							result.getInt(5),
							result.getInt(6),
							result.getInt(7),
							result.getInt(8)
					);
					return user;
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	// TODO: there must be a smarter way to to this
	public static String[][] getTopBetters(String channelname) {
		try {
			String tableName = "viewers";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String[][] output = new String[8][2];

				String sql = "SELECT name, bets FROM " + tableName
						+ " WHERE name NOT IN ('nightbot', 'honybot') AND channel = ? "
						+ " ORDER BY bets DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[0][0] = result.getString(1);
					output[0][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, betswon FROM " + tableName + " WHERE name NOT IN ('nightbot', 'honybot') "
						+ " AND channel = ? ORDER BY betswon DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[1][0] = result.getString(1);
					output[1][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, (bets-betswon) as betslost FROM " + tableName
						+ " WHERE name NOT IN ('nightbot', 'honybot') AND channel = ? ORDER BY betslost DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[2][0] = result.getString(1);
					output[2][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, pointswon FROM " + tableName + " WHERE name NOT IN ('nightbot', 'honybot') "
						+ " AND channel = ? ORDER BY pointswon DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[3][0] = result.getString(1);
					output[3][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, pointslost FROM " + tableName
						+ " WHERE name NOT IN ('nightbot', 'honybot') AND channel = ?"
						+ " ORDER BY pointslost DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[4][0] = result.getString(1);
					output[4][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, totalbetvalue FROM " + tableName
						+ " WHERE name NOT IN ('nightbot', 'honybot') AND channel = ?"
						+ " ORDER BY totalbetvalue DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[5][0] = result.getString(1);
					output[5][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, points FROM " + tableName + " WHERE name NOT IN ('nightbot', 'honybot') "
						+ " AND channel = ? ORDER BY points DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[6][0] = result.getString(1);
					output[6][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

				sql = "SELECT name, time FROM " + tableName + " WHERE name NOT IN ('nightbot', 'honybot', ?) "
						+ " AND channel = ? ORDER BY time DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, channelname);
					preparedStatement.setString(2, channelname);
					ResultSet result = preparedStatement.executeQuery();
					result.next();
					output[7][0] = result.getString(1);
					output[7][1] = Integer.toString(result.getInt(2));
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
				return output;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static String getVipFunction(String channelname) {
		try {
			String tableName = "settings";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT value FROM " + tableName + " WHERE username = ? AND setting = 'vip-cost'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelname);
				ResultSet result = preparedStatement.executeQuery();
				while (result.next()) {
					String function = result.getString("value");
					return function;
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
