package honybot.sc2;

public class LeagueRange {

	private String Region;
	private String leagueName;
	private int tier;
	private int lower;
	private int upper;
	
	public LeagueRange(String region, String leagueName, int tier, int lower, int upper) {
		super();
		Region = region;
		this.leagueName = leagueName;
		this.tier = tier;
		this.lower = lower;
		this.upper = upper;
	}

	public String getRegion() {
		return Region;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public int getTier() {
		return tier;
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}
	
	
	
}
