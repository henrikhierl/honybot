package honybot.bot.dao;

import java.util.Map;

import honybot.bot.Command.Command;

public interface CustomCommandsDao {

	public static boolean addCommand(String username, String command, String text, int cost, boolean takesUserInput){
		return false;
	};
	
	public static boolean removeCommand(String username, String command){
		return false;
	}
	
	public static Map<String, Command> getCommands(String username){
		return null;
	};
	
}
