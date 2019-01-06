package honybot.bot.settings.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import honybot.bot.settings.BetSettings;
import honybot.bot.settings.PointSettings;
import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.VipSettings;
import honybot.sql.SQLHelper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class SQLSettingsDao implements SettingsDao {
	private static String tableName = "settings";

	public static PointSettings getPointSettings(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting LIKE 'points%'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();
				boolean enabled = true;
				String pointsName = "Fritten";
				String command = "!fritten";
				boolean whisper = true;
				String reply = "$points $pointname $time $vipname $viplevel";
				int pointsGet = 0;
				int pointsTime = 0;
				int initialPoints = 0;

				while (result.next()) {
					String setting = result.getString(2);
					String value = result.getString(3);

					if (setting.equals("points-check")) {
						if (value.equals("on")) {
							enabled = true;
						} else {
							enabled = false;
						}
					} else if (setting.equals("points-name")) {
						pointsName = value;
					} else if (setting.equals("points-command")) {
						command = value;
					} else if (setting.equals("points-whisper")) {
						if (value.equals("on")) {
							whisper = true;
						} else {
							whisper = false;
						}
					} else if (setting.equals("points-reply")) {
						reply = value;
					} else if (setting.equals("points-get")) {
						pointsGet = Integer.parseInt(value);
					} else if (setting.equals("points-time")) {
						pointsTime = Integer.parseInt(value);
					} else if (setting.equals("points-initial")) {
						initialPoints = Integer.parseInt(value);
					}
				}
				PointSettings settings = new PointSettings(enabled, pointsName, command, whisper, reply, pointsGet,
						pointsTime, initialPoints);
				return settings;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static String getPointsname(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT value FROM " + tableName + " WHERE username = ? AND setting LIKE 'points-name'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getString(1);
				}
				return "Points";
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static VipSettings getVipSettings(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting LIKE 'vip%'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				boolean enabled = true;
				boolean autoExtend = true;
				boolean doesStack = true;
				int bonus = 3;
				String cost = "100*x^2+500"; // maybe expression instead of
												// string?
				String costCommand = "!vipcost";
				String vipCommand = "!getVip";
				String[] levels = { "Seedling", "Plantlet", "Potato", "Super Potato", "Frittling", "Frittus Longus", "Frittus Maximus" };
				String suffix = "-VIP";
				while (result.next()) {
					String setting = result.getString(2);
					String value = result.getString(3);
					setting = setting.replaceAll("\\s+", "");

					if (setting.equalsIgnoreCase("vip-check")) {
						if (value.equalsIgnoreCase("on")) {
							enabled = true;
						} else {
							enabled = false;
						}
					} else if (setting.equalsIgnoreCase("vip-check-extension")) {
						if (value.equalsIgnoreCase("on")) {
							autoExtend = true;
						} else {
							autoExtend = false;
						}
					} else if (setting.equalsIgnoreCase("vip-stacking")) {
						if (value.equalsIgnoreCase("on")) {
							doesStack = true;
						} else {
							doesStack = false;
						}
					} else if (setting.equalsIgnoreCase("vip-bonus")) {
						bonus = Integer.parseInt(value);
					} else if (setting.equalsIgnoreCase("vip-cost")) {
						cost = value;
					} else if (setting.equalsIgnoreCase("vip-cost-command")) {
						costCommand = value;
					} else if (setting.equalsIgnoreCase("vip-command")) {
						vipCommand = value;
					} else if (setting.equalsIgnoreCase("vip-levels")) {
						levels = value.split(",");
						for(int i = 0; i < levels.length; i++){
							levels[i] = levels[i].trim();
						}
					} else if (setting.equalsIgnoreCase("vip-suffix")) {
						suffix = value;
					}
				}
				VipSettings settings = new VipSettings(enabled, autoExtend, doesStack, bonus, cost, costCommand,
						vipCommand, levels, suffix);
				return settings;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static Expression getCostFunction(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT value FROM " + tableName + " WHERE username = ? AND setting LIKE 'vip-cost'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					String value = result.getString(1);
					Expression exp = new ExpressionBuilder(value).variables("x").build();
					return exp;
				}
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static BetSettings getBetSettings(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting LIKE 'betting%'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				boolean enabled = true;
				int timeToBet = 180;
				int notification = 30;
				float winQuota = 1.5f;
				float loseQuota = 1.5f;
				boolean mmrQuota = false;

				while (result.next()) {
					String setting = result.getString(2);
					String value = result.getString(3);

					if (setting.equals("betting-check")) {
						if (value.equals("on")) {
							enabled = true;
						} else {
							enabled = false;
						}
					} else if (setting.equals("betting-time")) {
						timeToBet = Integer.parseInt(value);
						;
					} else if (setting.equals("betting-notification")) {
						notification = Integer.parseInt(value);
					} else if (setting.equals("betting-win")) {
						winQuota = Float.parseFloat(value);
					} else if (setting.equals("betting-lose")) {
						loseQuota = Float.parseFloat(value);
					} else if (setting.equals("betting-mmr-quotas-check")) {
						if (value.equals("on")) {
							mmrQuota = true;
						} else {
							mmrQuota = false;
						}
					}
				}
				BetSettings settings = new BetSettings(enabled, timeToBet, notification, winQuota, loseQuota, mmrQuota);
				return settings;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	
	public static Sc2Settings getSC2Settings(String username) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND setting LIKE 'sc2%'";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				ResultSet result = preparedStatement.executeQuery();

				boolean enabled = false;
				String id = "";
				String name = "";
				String region = "";
				String race = "";
				boolean titleEnabled = false;
				String ingameTitle = "";
				String menuTitle = "";
				boolean showGames = false;


				while (result.next()) {
					String setting = result.getString(2);
					String value = result.getString(3);

					if (setting.equals("sc2-check")) {
						if (value.equals("on")) {
							enabled = true;
						} else {
							enabled = false;
						}
					} else if (setting.equals("sc2-id")) {
						id = value;
					} else if (setting.equals("sc2-name")) {
						name = value;
					} else if (setting.equals("sc2-region")) {
						region = value;
					} else if (setting.equals("sc2-race")) {
						race = value;
					} else if (setting.equals("sc2-title-check")) {
						if (value.equals("on")) {
							titleEnabled = true;
						} else {
							titleEnabled = false;
						}
					} else if (setting.equals("sc2-title-ingame")) {
						ingameTitle = value;
					} else if (setting.equals("sc2-title-menu")) {
						menuTitle = value;
					} else if (setting.equals("sc2-show-games")) {
						if (value.equals("on")) {
							showGames = true;
						} else {
							showGames = false;
						}
					}
				}
				Sc2Settings settings = new Sc2Settings(enabled, id, name, region, race, titleEnabled, ingameTitle, menuTitle, showGames);
				return settings;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	

	public static String getSetting(String username, String setting) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT value FROM " + tableName + " WHERE username = ? AND setting = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, username);
				preparedStatement.setString(2, setting);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					return result.getString("value");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}


	public static boolean setSetting(String username, String setting, String value) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "UPDATE " + tableName + " SET value = ? WHERE username = ? AND setting = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, value);
				preparedStatement.setString(2, username);
				preparedStatement.setString(3, setting);
				System.out.println(preparedStatement.toString());
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
}
