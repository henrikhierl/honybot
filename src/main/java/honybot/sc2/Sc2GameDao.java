package honybot.sc2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import honybot.sql.SQLHelper;

public class Sc2GameDao {
	private static String tableName = "sc2game";
	
	
	public static boolean addGame(Sc2Game game){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "INSERT INTO "+tableName+" (user_id, player, opponent, player_race, opponent_race, result, length, region, session, played_date) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement query = conn.prepareStatement(sql)){
				query.setInt(1, game.getUser_id());
				query.setString(2, game.getPlayer());
				query.setString(3, game.getOpponent());
				query.setString(4, game.getPlayerRace());
				query.setString(5, game.getOpponentRace());
				query.setString(6, game.getResult());
				query.setInt(7, game.getLength());
				query.setString(8, game.getRegion());
				query.setInt(9, game.getSession());
				query.setTimestamp(10, game.getPlayedDate());
				int result = query.executeUpdate();
				if(result > 0){
					return true;
				}else{
					return false;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static Sc2Stats getStatsBySession(int user_id, int session){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT " +
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'p' AND result = 'defeat' AND session = ?) AS 'plose',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'r' AND result = 'defeat' AND session = ?) AS 'rlose',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 't' AND result = 'defeat' AND session = ?) AS 'tlose',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'z' AND result = 'defeat' AND session = ?) AS 'zlose',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'p' AND result = 'victory' AND session = ?) AS 'pwin',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'r' AND result = 'victory' AND session = ?) AS 'rwin',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 't' AND result = 'victory' AND session = ?) AS 'twin',"+
					"(SELECT COUNT(*) FROM "+tableName+" WHERE user_id = ? AND opponent_race = 'z' AND result = 'victory' AND session = ?) AS 'zwin'";
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)){

				preparedStatement.setInt(1, user_id);
				preparedStatement.setInt(3, user_id);
				preparedStatement.setInt(5, user_id);
				preparedStatement.setInt(7, user_id);
				preparedStatement.setInt(9, user_id);
				preparedStatement.setInt(11, user_id);
				preparedStatement.setInt(13, user_id);
				preparedStatement.setInt(15, user_id);

				preparedStatement.setInt(2, user_id);
				preparedStatement.setInt(4, user_id);
				preparedStatement.setInt(6, user_id);
				preparedStatement.setInt(8, user_id);
				preparedStatement.setInt(10, user_id);
				preparedStatement.setInt(12, user_id);
				preparedStatement.setInt(14, user_id);
				preparedStatement.setInt(16, user_id);

				ResultSet result = preparedStatement.executeQuery();
				while(result.next()){
					return new Sc2Stats(
							result.getInt("pwin"),
							result.getInt("plose"),
							result.getInt("rwin"),
							result.getInt("rlose"),
							result.getInt("twin"),
							result.getInt("tlose"),
							result.getInt("zwin"),
							result.getInt("zlose")
							);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Sc2Stats stats = null;
		return stats;
	}

	public static int getGameAmount(int user_id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE user_id = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, user_id);
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

	public static LinkedList<Sc2Game> getAllGamesPaginated(int user_id, int offset, int perPage) {
		LinkedList<Sc2Game> games = new LinkedList<>();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE user_id = ? ORDER BY played_date DESC LIMIT ?,?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, user_id);
				preparedStatement.setInt(2, offset);
				preparedStatement.setInt(3, perPage);
				ResultSet result = preparedStatement.executeQuery();
				
				while (result.next()) {
					Sc2Game game = new Sc2Game(
							user_id,
							result.getString("player"),
							result.getString("opponent"),
							result.getString("player_race"),
							result.getString("opponent_race"),
							result.getString("result"),
							result.getInt("length"),
							result.getString("region"),
							result.getInt("session"),
							result.getTimestamp("played_date")
							);

					games.add(game);
				}
				return games;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
