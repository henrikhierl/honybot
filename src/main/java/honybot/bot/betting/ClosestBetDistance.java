package honybot.bot.betting;

public class ClosestBetDistance implements Comparable<ClosestBetDistance>{

	protected float distance;
	protected UserClosestBet userBet;
	
	public ClosestBetDistance(float distance, UserClosestBet userBet) {
		this.distance = distance;
		this.userBet = userBet;
	}

	public float getDistance() {
		return distance;
	}

	public UserClosestBet getUserBet() {
		return userBet;
	}

	@Override
	public int compareTo(ClosestBetDistance dist) {
		float compare = this.getDistance() - dist.getDistance();
		if(compare < 0) {
			return -1;
		} else if(compare == 0) {
			return 0;
		} else {
			return 1;
		}	
	}
}
