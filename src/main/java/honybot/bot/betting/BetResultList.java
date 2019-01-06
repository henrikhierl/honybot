package honybot.bot.betting;

import java.util.LinkedList;

public class BetResultList {

	protected LinkedList<UserChoiceBet> winners = new LinkedList<>();
	protected LinkedList<UserChoiceBet> losers = new LinkedList<>();
	
	public BetResultList (LinkedList<UserChoiceBet> winners, LinkedList<UserChoiceBet> losers) {
		this.winners = winners;
		this.losers = losers;
	}
	
	LinkedList<UserChoiceBet> getWinners () {
		return this.winners; 
	}
	
	LinkedList<UserChoiceBet> getLosers () {
		return this.losers; 
	}
	
}
