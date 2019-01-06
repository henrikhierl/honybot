package honybot.sc2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import honybot.sql.SQLHelper;

public class SQLPlayerDao {

	private static int currentSeason = 34;
	private static String tableName = "sc2player";
	
	//TODO: Not working, no idea why, player is null, sc2info is not
	public static Sc2ChatInfo getPlayerForChat(String id, String name, String race, String region) {
		System.out.println(id + " " + name + " " + race + " " + region);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				String sql = "SELECT clan_tag, league, tier, mmr, wins, losses, streak_current " + " FROM " + tableName
						+ " WHERE id = ? AND name like ? AND race = ? AND region = ? ORDER BY season DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, id);
					preparedStatement.setString(2, "%"+name+"%");
					preparedStatement.setString(3, race);
					preparedStatement.setString(4, region);
					ResultSet result = preparedStatement.executeQuery();

					while (result.next()) {
						int mmr = result.getInt(4);
						float percentile = getMMRPercentile(mmr) * 100;
						Sc2ChatInfo info = new Sc2ChatInfo(race, // race
								result.getString(1), // clantag
								name, // playername
								result.getString(2), // league
								result.getInt(3), // tier
								mmr, // mmr
								result.getInt(5), // wins
								result.getInt(6), // losses
								result.getInt(7), // winstreakcurrent
								round(percentile, 2) // percentile
						);
						return info;
					}
					return null;
				} catch (Exception e) {
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

	// maybe put this in getPlayer?
	public static float getMMRPercentile(int mmr) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "SELECT (SELECT COUNT(*) from " + tableName + " where mmr < ?)/(SELECT COUNT(*) FROM " + tableName + ") as percentile";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setInt(1, mmr);
					ResultSet result = preparedStatement.executeQuery();
					while (result.next()) {
						return result.getFloat("percentile");
					}
					return -1;
				} catch (SQLException e) {
					e.printStackTrace();
					return -1;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return -1;
	}

	public static ArrayList<Sc2Player> getPossibleOpponents(String region, String race, String name) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT region, race, mmr, streak_current, last_played " + " FROM " + tableName
					+ " WHERE region = ? and race = ? and name like ? and season = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, region);
				preparedStatement.setString(2, race);
				preparedStatement.setString(3, name+"#%");
				preparedStatement.setInt(4, currentSeason);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<Sc2Player> players = new ArrayList<>();
				while (result.next()) {
					Sc2Player player = new Sc2Player(result.getString("region"), result.getString("race"), result.getInt("mmr"),
							result.getInt("streak_current"), result.getLong("last_played"));
					players.add(player);
				}
				return players;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static Sc2Player getPlayer(String id, String name, String race, String region) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {

				
				String sql = "SELECT region, race, mmr, streak_current, last_played FROM " + tableName
						+ " WHERE id = ? AND name like ? AND race = ? AND region = ? ORDER BY season DESC LIMIT 1";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
					preparedStatement.setString(1, id);
					preparedStatement.setString(2, "%"+name+"%");
					preparedStatement.setString(3, race);
					preparedStatement.setString(4, region);
					ResultSet result = preparedStatement.executeQuery();
					while (result.next()) {
						Sc2Player player = new Sc2Player(result.getString(1), result.getString(2), result.getInt(3),
								result.getInt(4), result.getLong(5));
						return player;
					}
					return null;
				}catch(Exception e){
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
	

	// maybe put this in getViewer?
	public static JSONObject getGameStats(int user_id) {
		JSONObject content = new JSONObject();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "select "+
					    "sum(case when result != 'victory' then 1 else 0 end) loss," +
					    "sum(case when result = 'victory' then 1 else 0 end) win," +
					    "sum(case when opponent_race = 'p' and result != 'victory' then 1 else 0 end) vPloss," +
					    "sum(case when opponent_race = 'p' and result = 'victory' then 1 else 0 end) vPwin," +
					    "sum(case when opponent_race = 't' and result != 'victory' then 1 else 0 end) vTloss," +
					    "sum(case when opponent_race = 't' and result = 'victory' then 1 else 0 end) vTwin," +
					    "sum(case when opponent_race = 'z' and result != 'victory' then 1 else 0 end) vZloss," +
					    "sum(case when opponent_race = 'z' and result = 'victory' then 1 else 0 end) vZwin" +
					" from sc2game"  +
					" WHERE user_id = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

					preparedStatement.setInt(1, user_id);
					ResultSet result = preparedStatement.executeQuery();
					while (result.next()) {
						content.put("games-total", result.getString("win") + ":" + result.getString("loss"));
						content.put("vP", result.getString("vPwin") + ":" + result.getString("vPloss"));
						content.put("vT", result.getString("vTwin") + ":" + result.getString("vTloss"));
						content.put("vZ", result.getString("vZwin") + ":" + result.getString("vZloss"));
					}
					return content;
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static float round(float number, int scale) {
		int pow = 10;
		for (int i = 1; i < scale; i++)
			pow *= 10;
		float tmp = number * pow;
		return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
	}

}
