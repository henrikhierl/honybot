package honybot.bot.betting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.mysql.jdbc.Statement;

import honybot.sql.SQLHelper;

public class SQLBettingDao {
	
	/**
	 * Adds a Bet to the database and returns its ID
	 * @param channel - channelname that created the bet
	 * @param bet - Bet to add
	 * @param description - Description text
	 * @return id - ID of the created Bet
	 */
	public static int addBet(String channel, String description){
		
		try {
			String tableName = "bet";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO " + tableName + " (channel, description) "
					+ "VALUES(?,?) ";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, channel);
				preparedStatement.setString(2, description);
				//auto generate this in dbms
				//preparedStatement.setTimestamp(2, new Timestamp(Calendar.getInstance().getTime().getTime()));
				int affectedRows = preparedStatement.executeUpdate();
				
				if (affectedRows == 0) {
		            throw new SQLException("Creating bet failed, no rows affected.");
		        }
				try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                return generatedKeys.getInt(1);
		            }
		            else {
		                throw new SQLException("Creating bet failed, no ID obtained.");
		            }
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Adds Bet-Options to the database
	 * @param options - Options to add
	 * @param bet_id - ID of the bet the options are added to
	 * @return option_ids - IDs of the created options
	 */
	public static HashMap<String, Integer> addOptions(ArrayList<BetChoice> options, int bet_id){
		try {
			String tableName = "bet_option";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO " + tableName + " (bet_id, option_index, name_hr, quota) "
					+ "VALUES(?,?,?,?) ";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				//add option to batch
				for(BetChoice bet_option : options){
					preparedStatement.setInt(1, bet_id);
					preparedStatement.setString(2, bet_option.getOption());
					preparedStatement.setString(3, bet_option.getDescription());
					preparedStatement.setFloat(4, bet_option.getQuota());
					preparedStatement.addBatch();
				}
				int[] affectedRows = preparedStatement.executeBatch();
				if(affectedRows.length != options.size()){
					throw new SQLException("Creating bet_option failed, not enough IDs obtained.");
				}
				try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
					HashMap<String, Integer> id_map = new HashMap<>();
					for(BetChoice bet_option : options){
						if(generatedKeys.next()){
							String option = bet_option.getOption();
							int option_id = generatedKeys.getInt(1);
							id_map.put(option, option_id);
						}else{
							throw new SQLException("Creating bet_option failed, no ID obtained for " + bet_option.getOptionAndDescription());
						}
					}
					return id_map;
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Adds a wager to a bet to the database
	 * @param wagers - all wagers the users made
	 * @param option_id - IDs of the options mapped to its string counterpart
	 * @return - true if all wagers were added successfully, else false
	 */
	public static boolean addWagers(LinkedList<UserChoiceBet> wagers, HashMap<String, Integer> option_ids){
		try {
			String tableName = "bet_wager";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO " + tableName + " (option_id, viewername, amount, win) "
					+ "VALUES(?,?,?,?) ";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				//add option to batch
				for(UserChoiceBet wager : wagers){
					preparedStatement.setInt(1, option_ids.get(wager.getOption()));
					preparedStatement.setString(2, wager.getUsername());
					preparedStatement.setInt(3, wager.getAmount());
					preparedStatement.setBoolean(4, wager.getResult());
					preparedStatement.addBatch();
				}
				int[] affectedRows = preparedStatement.executeBatch();
				if(affectedRows.length == wagers.size()){
					return true;
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
