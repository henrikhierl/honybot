package honybot.bot.listener;

import java.util.HashMap;

import honybot.bot.ViewerUser;
import honybot.bot.betting.BetChoice;
import honybot.bot.betting.UserChoiceBet;

public class ListenerHelper {
	
	
	/**
	 * 
	 * @param message - Array of 3 Strings. [1] and [2] will be checked for an integer
	 * @return - returns the first integer value from a string found in the array
	 * @throws NumberFormatException - thrown when neither of the strings can be parsed to an integer
	 */
	public static int getAmountFromMessage(String[] message, ViewerUser user) throws NumberFormatException {
		int amount = 0;
		boolean validNumber = false;
		if(message[1].equalsIgnoreCase("all") || message[2].equalsIgnoreCase("all")){
			return user.getPoints();
		}
		//tries to parse message[1] or message[2] to integer. can be used for !give or !bet
		try{
			amount = Integer.parseInt(message[1]);
			validNumber = true;
		}catch(Exception NumberFormatException){
			try{
				amount = Integer.parseInt(message[2]);
				validNumber = true;
			}catch(Exception NumberFormatException2){
				
			}
		}
		if(validNumber){
			return amount;
		}
		throw new NumberFormatException();
	}
	

	public static int getAmountFromMessage(String[] message) throws NumberFormatException {
		int amount = 0;
		boolean validNumber = false;
		//tries to parse message[1] or message[2] to integer. can be used for !give or !bet
		try{
			amount = Integer.parseInt(message[1]);
			validNumber = true;
		}catch(Exception NumberFormatException){
			try{
				amount = Integer.parseInt(message[2]);
				validNumber = true;
			}catch(Exception NumberFormatException2){
				
			}
		}
		if(validNumber){
			return amount;
		}
		throw new NumberFormatException();
	}
	
	
	public static String getOptionFromMessage(String[] message) {
		String option = "";
		if(message[1].equalsIgnoreCase("all")){
			return message[2];
		}else if(message[2].equalsIgnoreCase("all")){
			return message[1];
		}
		//if the message[1] not an integer it is returned as option, if it is an integer, message[2] is returned as option
		try{
			Integer.parseInt(message[1]);
			return message[2];
		}catch(NumberFormatException e){
			return message[1];
		}
	}


	public static UserChoiceBet getBetFromMessage(HashMap<String, BetChoice> options, ViewerUser user, String[] messageArray) {
		String firstParameter = messageArray[1];
		String secondParameter = messageArray[2];
		int amount = 0;
		//check if firstParameter is valid betting option
		if(options.get(firstParameter) != null){
			try{
				if(secondParameter.equalsIgnoreCase("all")){
					amount = user.getPoints();
				}else{
					amount = Integer.parseInt(secondParameter);
				}
				return new UserChoiceBet(user.getName(), firstParameter, amount);
			}catch(Exception e){
				//do nothing
			}
		}
		if(options.get(secondParameter) != null){
			try{
				if(firstParameter.equalsIgnoreCase("all")){
					amount = user.getPoints();
				}else{
					amount = Integer.parseInt(firstParameter);
				}
				return new UserChoiceBet(user.getName(), secondParameter, amount);
			}catch(Exception e){
				//do nothing
			}
		}
		return null;
	}
}
