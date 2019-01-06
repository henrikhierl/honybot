package honybot.bot.betting.pubg;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PubgBet {

	private HashMap<String, PubgUserBet> bets; 
	private PubgBetType type;
	private double winnerAmount;
	
	public PubgBet(PubgBetType type, double winnerAmount){
		bets = new HashMap<>();
		this.type = type;
		
		if(type == PubgBetType.PERCENTAGE){
			winnerAmount = Math.max(winnerAmount, 0);
			winnerAmount = Math.min(winnerAmount, 1);
		}
		this.winnerAmount = winnerAmount;
	}

	public boolean addUser(String username, int option, int amount){
		if(bets.containsKey(username)){
			return false;
		}
		PubgUserBet userBet = new PubgUserBet(username, option, amount);
		bets.put(username, userBet);
		return true;
	}
	
	public LinkedList<PubgUserBet> getUsers(){
		return new LinkedList<PubgUserBet>(bets.values());
	}
	
	public LinkedList<PubgUserBet> getUsersSorted(){
		LinkedList<PubgUserBet> sortedUsers = new LinkedList<>();
		for (Map.Entry<String, PubgUserBet> entry : bets.entrySet()) {
			sortedUsers.add(entry.getValue());
		}
		Collections.sort(sortedUsers);
		
		return sortedUsers;
	}
	
	
	public List<PubgPlaceDistance> getWinners(int place){
		
		List<PubgPlaceDistance> winners;
		if(type == PubgBetType.ABSOLUTE) {
			winners = getBestPercentage(winnerAmount, place);
		}else{
			winners = getBestAbsolute((int) winnerAmount, place);
		}

		return winners;
	}
	
	public List<PubgPlaceDistance> getBestPercentage(double percentage, int place) {
		int amount = (int) (bets.size() * percentage);
		if(0 == amount){
			amount = 1;
		}
		return getBestAbsolute(amount, place);
	}
	
	public List<PubgPlaceDistance> getBestAbsolute(int amount, int place) {
		LinkedList<PubgPlaceDistance> betsSorted = getUserBetsSortedByDistance(place);
		
		if(amount >= betsSorted.size()){
			// if e.g. only 2 person placed a bet, and top 2 would win, only 50% of players are winners
			amount = amount / 2;
		}
		if(amount < 0) {
			amount = 0;
		}
		return betsSorted.subList(0, amount);
	}
	
	public LinkedList<PubgPlaceDistance> getUserBetsSortedByDistance(int place) {
		LinkedList<PubgPlaceDistance> distancesToResult = new LinkedList<>();
		
		for(PubgUserBet userBet : bets.values()) {
			int distance = Math.abs(userBet.getOption() - place);
			PubgPlaceDistance placeDistance = new PubgPlaceDistance(distance, userBet);
			distancesToResult.add(placeDistance);
		}
		Collections.sort(distancesToResult);
		
		return distancesToResult;
	}

	public LinkedList<PubgUserBet> getLosers(String winningOption){
		LinkedList<PubgUserBet> losers = new LinkedList<>();
		
		return losers;
	}
	
}
