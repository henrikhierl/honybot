package honybot.bot.betting.pubg;

public class PubgPlaceDistance implements Comparable<PubgPlaceDistance> {

	private int distance;
	private PubgUserBet userBet;
	
	public PubgPlaceDistance(int distance, PubgUserBet userBet) {
		super();
		this.distance = distance;
		this.userBet = userBet;
	}

	public int getDistance() {
		return distance;
	}

	public PubgUserBet getUserBet() {
		return userBet;
	}

	@Override
	public int compareTo(PubgPlaceDistance compObj) {
		return this.distance - compObj.distance;
	}
	
	
	
}
