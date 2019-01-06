package honybot.sc2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import honybot.sql.SQLHelper;

public class SQLSc2Dao {

	public static LeagueRange getLeagueRange(String region, int mmr){
		try {
			String tableName = "sc_league_boundary";
			Class.forName("com.mysql.jdbc.Driver");

			String sql = "SELECT * from "+tableName+" WHERE region = ? and lower <= ? and upper >= ?";
			try (Connection conn = DriverManager.getConnection(SQLHelper.getConnectionString());
					PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

				preparedStatement.setString(1, region);
				preparedStatement.setInt(2, mmr);
				preparedStatement.setInt(3, mmr);
				ResultSet result = preparedStatement.executeQuery();

				if(result.next()){
					return new LeagueRange(
							region, 
							result.getString("league"), 
							result.getInt("tier"), 
							result.getInt("lower"), 
							result.getInt("upper")
							);
				}				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
}
