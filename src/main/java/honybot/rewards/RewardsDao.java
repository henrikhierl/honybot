package honybot.rewards;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import com.mysql.jdbc.Statement;

import honybot.sql.SQLHelper;

public class RewardsDao {
	
	public static Reward getRewardById(int id) {
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE ID = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					Reward reward = new Reward(
						result.getInt("ID"),
						result.getString("username"),
						result.getString("command"),
						result.getString("title"),
						result.getString("response"),
						result.getString("description"),
						result.getString("image_url"),
						result.getInt("cost"),
						result.getInt("permission"),
						result.getInt("vip"),
						result.getBoolean("enabled")
					);
					return reward;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static Reward getRewardByCommandAndUsername(String command, String username) {
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE command = ? AND username = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, command);
				preparedStatement.setString(2, username);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					Reward reward = new Reward(
						result.getInt("ID"),
						result.getString("username"),
						result.getString("command"),
						result.getString("title"),
						result.getString("response"),
						result.getString("description"),
						result.getString("image_url"),
						result.getInt("cost"),
						result.getInt("permission"),
						result.getInt("vip"),
						result.getBoolean("enabled")
					);
					return reward;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static Reward getRewardByCommandUsernameAndNotCommandID(String command, String username, int command_id) {
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE command = ? AND username = ? AND id <> ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, command);
				preparedStatement.setString(2, username);
				preparedStatement.setInt(3, command_id);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					Reward reward = new Reward(
						result.getInt("ID"),
						result.getString("username"),
						result.getString("command"),
						result.getString("title"),
						result.getString("response"),
						result.getString("description"),
						result.getString("image_url"),
						result.getInt("cost"),
						result.getInt("permission"),
						result.getInt("vip"),
						result.getBoolean("enabled")
					);
					return reward;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static HashMap<String, Reward> getRewardsByChannelAsMap(String channelName) {
		HashMap<String, Reward> rewards = new HashMap<>();
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE username = ? AND enabled = true ORDER BY cost DESC";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelName);
				ResultSet result = preparedStatement.executeQuery();

				while(result.next()){
					String command = result.getString("command");
					Reward reward = new Reward(
						result.getInt("ID"),
						result.getString("username"),
						command,
						result.getString("title"),
						result.getString("response"),
						result.getString("description"),
						result.getString("image_url"),
						result.getInt("cost"),
						result.getInt("permission"),
						result.getInt("vip"),
						result.getBoolean("enabled")
					);
					rewards.put(command, reward);
				}
				return rewards;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return rewards;
	}

	public static LinkedList<Reward> getRewardsByChannelAsList(String channelName) {
		LinkedList<Reward> rewards = new LinkedList<>();
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE username = ? AND enabled = true ORDER BY cost DESC";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelName);
				ResultSet result = preparedStatement.executeQuery();

				while(result.next()){
					String command = result.getString("command");
					Reward reward = new Reward(
						result.getInt("ID"),
						result.getString("username"),
						command,
						result.getString("title"),
						result.getString("response"),
						result.getString("description"),
						result.getString("image_url"),
						result.getInt("cost"),
						result.getInt("permission"),
						result.getInt("vip"),
						result.getBoolean("enabled")
					);
					rewards.push(reward);
				}
				return rewards;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return rewards;
	}
	
	/**
	 * adds a reward to the database and returns the id of the created reward
	 * @param reward
	 * @return generated id of created reward
	 */
	public static int addReward(Reward reward) {		
		try {
			String tableName = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO "+tableName+"(username, command, title, response, description, image_url, cost, permission, vip, enabled) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, reward.getUsername());
				preparedStatement.setString(2, reward.getCommand());
				preparedStatement.setString(3, reward.getTitle());
				preparedStatement.setString(4, reward.getResponse());
				preparedStatement.setString(5, reward.getDescription());
				preparedStatement.setString(6, reward.getImage_url());
				preparedStatement.setInt(7, reward.getCost());
				preparedStatement.setInt(8, reward.getPermission());
				preparedStatement.setInt(9, reward.getVip());
				preparedStatement.setBoolean(10, reward.isEnabled());
				
				preparedStatement.executeUpdate();
				
				ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next())
                {
                	int id = rs.getInt(1);
                	reward.setId(id);
                    return id;
                }

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return -1;
	}

	public static boolean hasRedeems(Reward reward) {
		try {
			String tableName = "redeemed_reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * FROM " + tableName + " WHERE reward_id = ? LIMIT 1";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, reward.getId());

				ResultSet result = preparedStatement.executeQuery();
                if(result.next()){
                	return true;
                }
                return false;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static boolean removeRewardById(int id) {
		String tableName = "reward";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				int result = preparedStatement.executeUpdate();
				return result > 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static boolean updateReward(Reward reward) {
		String tableName = "reward";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "UPDATE " + tableName + 
					" SET username = ?," +
					" command = ?," +
					" title = ?," +
					" response = ?," +
					" description = ?," +
					" image_url = ?," +
					" cost = ?," +
					" permission = ?," +
					" vip = ?," +
					" enabled = ? " +
					" WHERE ID = ?";
			
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, reward.getUsername());
				preparedStatement.setString(2, reward.getCommand());
				preparedStatement.setString(3, reward.getTitle());
				preparedStatement.setString(4, reward.getResponse());
				preparedStatement.setString(5, reward.getDescription());
				preparedStatement.setString(6, reward.getImage_url());
				preparedStatement.setInt(7, reward.getCost());
				preparedStatement.setInt(8, reward.getPermission());
				preparedStatement.setInt(9, reward.getVip());
				preparedStatement.setBoolean(10, reward.isEnabled());
				preparedStatement.setInt(11, reward.getId());
				
				int result = preparedStatement.executeUpdate();
				return result > 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public static LinkedList<RedeemedReward> getRedeemedRewardsByChannel(String channelName) {
		LinkedList<RedeemedReward> redeems = new LinkedList<>();
		try {
			String tableName = "redeemed_reward";
			String rewardsTable = "reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT r.ID, r.reward_id, r.viewername, r.comment, r.timestamp from "+tableName+" AS r LEFT JOIN " + rewardsTable + 
					" ON r.reward_id = " + rewardsTable + ".ID " +
					" WHERE username = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, channelName);
				ResultSet result = preparedStatement.executeQuery();

				while(result.next()){
					RedeemedReward reward = new RedeemedReward(
						result.getInt("ID"),
						result.getInt("reward_id"),
						result.getString("viewername"),
						result.getString("comment"),
						result.getTimestamp("timestamp")
					);
					redeems.push(reward);
				}
				return redeems;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return redeems;
	}
	
	public static boolean addRedeemedReward(RedeemedReward redeem) {		
		try {
			String tableName = "redeemed_reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO "+tableName+"(reward_id, viewername, comment) "
					+ " VALUES(?,?,?)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, redeem.getReward_id());
				preparedStatement.setString(2, redeem.getViewername());
				preparedStatement.setString(3, redeem.getComment());
				
				int result = preparedStatement.executeUpdate();
				
				return result > 0;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static RedeemedReward getRedeemedRewardById(int id) {
		try {
			String tableName = "redeemed_reward";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE ID = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					RedeemedReward redeem = new RedeemedReward(
						result.getInt("ID"),
						result.getInt("reward_id"),
						result.getString("viewername"),
						result.getString("comment"),
						result.getTimestamp("timestamp")
					);
					return redeem;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static boolean removeRedeemedRewardById(int id) {
		String tableName = "redeemed_reward";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				int result = preparedStatement.executeUpdate();
				return result > 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	
	
}
