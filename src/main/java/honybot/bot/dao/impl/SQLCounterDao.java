package honybot.bot.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import honybot.Counter;
import honybot.bot.Command.CounterCommand;
import honybot.sql.SQLHelper;

public class SQLCounterDao {	
	public static boolean addCounter(String username, String name, int init_value){
		System.out.println("adding counter");
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "INSERT INTO " + tableName + "(username, name, value, initial_value, path) "
						+ "VALUES(?, ?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
					preparedStatement.setString(1, username);
					preparedStatement.setString(2, name);
					preparedStatement.setInt(3, init_value);
					preparedStatement.setInt(4, init_value);
					preparedStatement.setString(5, buildRandomString(32));
					int affectedRows = preparedStatement.executeUpdate();
					int counter_id = 0;
					if(affectedRows == 1){
						try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				            if (generatedKeys.next()) {
				            	counter_id = generatedKeys.getInt(1);
				            }
				            else {
				                throw new SQLException("Creating counter failed, no ID obtained.");
				            }
				        }
						return addDefaultCommands(counter_id, username, name, init_value);
					}
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
	
	public static boolean removeCounter(String channelname, String name){
		System.out.println("removing counter");
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "DELETE FROM " + tableName + " WHERE username = ? AND name = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, channelname);
					preparedStatement.setString(2, name);
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

	private static boolean addDefaultCommands(int counter_id, String username, String name, int init_val) {
		String command = "!" + name;
		String text = "Increased " + name + " to $value";
		if(!addCommand(counter_id, command, text, 0, 2 , 0, "increase")){
			return false;
		}
		command = "!dec" + name;
		text = "Decreased " + name + " to $value";
		if(!addCommand(counter_id, command, text, 0, 2 , 0, "decrease")){
			return false;
		}
		command = "!reset" + name;
		text = name + " was reset to " + init_val;
		if(!addCommand(counter_id, command, text, 0, 2 , 0, "reset")){
			return false;
		}
		return true;
	}
	
	public static boolean addCommand(int counter_id, String command, String text, int cost, int permission, int vip, String type) {
		command = command.toLowerCase();
		System.out.println("adding command");
		try {
			String tableName = "counter_command";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "INSERT INTO " + tableName + "(counter_id, command, output, cost, permission, vip, type) "
						+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setInt(1, counter_id);
					preparedStatement.setString(2, command);
					preparedStatement.setString(3, text);
					preparedStatement.setInt(4, cost);
					preparedStatement.setInt(5, permission);
					preparedStatement.setInt(6, vip);
					preparedStatement.setString(7, type);
					preparedStatement.executeUpdate();
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					//return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}	
	
	//TODO: implement removeCounterCommand
	public static boolean removeCommand(){
		
		return false;
	}
	
	public static Map<String, CounterCommand> getCommands(String channelname){
		try {
			String tableName = "counter_command";
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				String sql = "SELECT * FROM counter_command "
		    	  		+ "INNER JOIN counter "
		    	  		+ "ON counter_command.counter_id = counter.id "
		    	  		+ "WHERE counter.username = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, channelname);
					ResultSet result = preparedStatement.executeQuery();
					
					HashMap<String, CounterCommand> map = new HashMap<>();
			    	  int counter_id = 0;
			    	  String command = "";
			    	  String text = "";
			    	  int cost = 0;
			    	  int permission = 0;
			    	  int vip = 0;
			    	  String type = "";
			    	  
			    	  while (result.next()) {
			    		  counter_id = result.getInt(2);
			    		  command = result.getString(3);
			    		  text = result.getString(4);
			    		  cost = result.getInt(5);
			    		  permission = result.getInt(6);
			    		  vip = result.getInt(7);
			    		  type = result.getString(8);
			    		  CounterCommand com = new CounterCommand(counter_id, command, text, cost, permission, vip, type);
			    		  map.put(command, com);
			    	  }
			    	  return map;
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

	public static boolean changeCounterValue(int id, int amount) {
		System.out.println("adding counter");
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "UPDATE " + tableName + " SET value = (value + ?) WHERE id = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setInt(1, amount);
					preparedStatement.setInt(2, id);
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
	

	public static boolean resetCounterValue(int id) {
		System.out.println("adding counter");
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "UPDATE " + tableName + " SET value = initial_value WHERE id = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setInt(1, id);
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
	
	public static int getCounterValue(int id) {
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			
			String sql = "SELECT value FROM "+tableName+" where id = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				ResultSet result = preparedStatement.executeQuery();

		    	int value = -999;
		    	  
		    	while (result.next()) {
		    		value = result.getInt(1);
		    		return value;
		    	}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		//TODO return something better
		return -999;
	}

	public static int getCounterValue(String path) {
		System.out.println("getting value by path");
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())) {
				String sql = "SELECT value FROM " + tableName + " WHERE path = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, path);
					ResultSet result = preparedStatement.executeQuery();	

			    	  int value = -999;
			    	  while (result.next()) {
			    		  value = result.getInt("value");
			    		  return value;
			    	  }
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//TODO return something better
		return -999;
	}

	public static Counter getCounterByPath(String path) {
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				
				String sql = "SELECT * FROM " + tableName + " WHERE path = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, path);
					ResultSet result = preparedStatement.executeQuery();	

					while (result.next()) {
			    		  String name = result.getString("name");
			    		  int value = result.getInt("value");
			    		  int init_value = result.getInt("initial_value");
			    		  Counter counter = new Counter(name, value, init_value, path);
			    		  return counter;
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

	public static Counter getCounterByName(String username, String counterName) {
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				
				String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND name = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, username);
					preparedStatement.setString(2, counterName);
					ResultSet result = preparedStatement.executeQuery();	

					while (result.next()) {
			    		  String name = result.getString("name");
			    		  int value = result.getInt("value");
			    		  int init_value = result.getInt("initial_value");
			    		  Counter counter = new Counter(name, value, init_value, result.getString("path"));
			    		  return counter;
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
	
	public static ArrayList<Counter> getCounters(String username) {
		try {
			String tableName = "counter";
			Class.forName("com.mysql.jdbc.Driver");
			try(Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString())){
				
				String sql = "SELECT * FROM "+tableName+" where username = ?";
				try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
					preparedStatement.setString(1, username);
					ResultSet result = preparedStatement.executeQuery();	
					ArrayList<Counter> counters = new ArrayList<>();
					while (result.next()) {
						String name = result.getString("name");
						int value = result.getInt("value");
						int init_value = result.getInt("initial_value");
						String path = result.getString("path");
						Counter counter = new Counter(name, value, init_value, path);
						counters.add(counter);
					}
		    	  return counters;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static String buildRandomString(int size){
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		String output = sb.toString();
		return output;
	}	
}
