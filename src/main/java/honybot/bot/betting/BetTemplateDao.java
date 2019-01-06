package honybot.bot.betting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.Statement;

import honybot.sql.SQLHelper;

public class BetTemplateDao {
	
	public static BetTemplate getTemplateById(int id) {
		try {
			String tableName = "bet_template";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE ID = ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, id);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					BetTemplate template = new BetTemplate(
						result.getInt("ID"),
						result.getInt("user_id"),
						result.getString("name"),
						result.getString("arguments"),
						result.getString("description"),
						result.getString("type")
					);
					return template;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static Map<String, BetTemplate> getBetTemplatesByChannel(int channel_id) {
		HashMap<String, BetTemplate> templates = new HashMap<>();
		try {
			String tableName = "bet_template";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE user_id = ? ORDER BY type ASC, name";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, channel_id);
				ResultSet result = preparedStatement.executeQuery();

				while(result.next()){
					String name = result.getString("name");
					BetTemplate template = new BetTemplate(
						result.getInt("ID"),
						result.getInt("user_id"),
						name,
						result.getString("arguments"),
						result.getString("description"),
						result.getString("type")
					);
					templates.put(name, template);
				}
				return templates;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return templates;
	}
	
	public static int addTemplate(BetTemplate template) {
		try {
			String tableName = "bet_template";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "INSERT INTO "+tableName+"(user_id, name, arguments, description, type) "
					+ " VALUES(?,?,?,?,?)";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setInt(1, template.getUserId());
				preparedStatement.setString(2, template.getName());
				preparedStatement.setString(3, template.getArguments());
				preparedStatement.setString(4, template.getDescription());
				preparedStatement.setString(5, template.getBetType());
				
				preparedStatement.executeUpdate();
				
				ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next())
                {
                	int id = rs.getInt(1);
                	template.setId(id);
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
	
	public static boolean updateTemplate(BetTemplate template) {
		String tableName = "bet_template";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "UPDATE " + tableName + 
					" SET user_id = ?," +
					" name = ?," +
					" arguments = ?," +
					" description = ?," +
					" type = ? " +
					" WHERE id = ?";
			
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setInt(1, template.getUserId());
				preparedStatement.setString(2, template.getName());
				preparedStatement.setString(3, template.getArguments());
				preparedStatement.setString(4, template.getDescription());
				preparedStatement.setString(5, template.getBetType());
				preparedStatement.setInt(6, template.getId());
				
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
	
	public static boolean removeTemplateById(int id) {
		String tableName = "bet_template";
		
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
