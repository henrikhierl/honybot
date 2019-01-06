package honybot.bot.stats;

import java.util.HashMap;

public class ChatStats {
	
	private static int linesToBuffer = 10;
	
	private static HashMap<String, Integer> linesReceived = new HashMap<>();
	private static HashMap<String, Integer> linesSent = new HashMap<>();
	private static HashMap<String, Integer> whispersSent = new HashMap<>();
	private static HashMap<String, Integer> commands = new HashMap<>();
		
	public static void addLineReceived(String name){
		if(linesReceived.containsKey(name)){
			int numberOfLines = linesReceived.get(name) + 1;
			if(numberOfLines == linesToBuffer){
				if(SQLStatsDao.updateStat(name, "linesreceived", linesToBuffer)){
					linesReceived.replace(name, 0);
				}				
			}else{
				linesReceived.replace(name, numberOfLines);
			}			
		}else{
			linesReceived.put(name, 1);
		}		
	}
	
	public static void addLineSent(String name){
		if(linesSent.containsKey(name)){
			int numberOfLines = linesSent.get(name) + 1;
			if(numberOfLines == linesToBuffer){
				if(SQLStatsDao.updateStat(name, "linessent", linesToBuffer)){
					linesSent.replace(name, 0);
				}				
			}else{
				linesSent.replace(name, numberOfLines);
			}			
		}else{
			linesSent.put(name, 1);
		}		
	}

	public static void addWhisperSent(String name){
		if(whispersSent.containsKey(name)){
			int numberOfLines = whispersSent.get(name) + 1;
			if(numberOfLines == linesToBuffer){
				if(SQLStatsDao.updateStat(name, "whisperssent", linesToBuffer)){
					whispersSent.replace(name, 0);
				}				
			}else{
				whispersSent.replace(name, numberOfLines);
			}			
		}else{
			whispersSent.put(name, 1);
		}		
	}
	
	public static void addCommand(String name){
		if(commands.containsKey(name)){
			int numberOfCommands = commands.get(name) + 1;
			if(numberOfCommands == linesToBuffer){
				if(SQLStatsDao.updateStat(name, "commands", linesToBuffer)){
					commands.replace(name, 0);
				}				
			}else{
				commands.replace(name, numberOfCommands);
			}			
		}else{
			commands.put(name, 1);
		}		
	}
}
