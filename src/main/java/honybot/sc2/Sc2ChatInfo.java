package honybot.sc2;

public class Sc2ChatInfo {
	
	private String race;
	private String clantag;
	private String playername;
	private String league;
	private int tier;
	private int mmr;
	private int wins;
	private int losses;
	private int winstreakcurrent;
	private float percentile;
	
	public Sc2ChatInfo(String race, String clantag, String playername, String league, int tier, int mmr, int wins, int losses,
			int winstreakcurrent, float percentile) {
		super();
		this.race = race;
		this.clantag = clantag;
		this.playername = playername;
		this.league = league;
		this.tier = tier;
		this.mmr = mmr;
		this.wins = wins;
		this.losses = losses;
		this.winstreakcurrent = winstreakcurrent;
		this.percentile = percentile;
	}

	public String getRace() {
		return race;
	}

	public String getClantag() {
		return clantag;
	}

	public String getPlayername() {
		return playername;
	}

	public String getLeague() {
		return league;
	}

	public int getTier() {
		return tier;
	}
	
	public int getMMR(){
		return mmr;
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public int getWinstreakcurrent() {
		return winstreakcurrent;
	}

	public float getPercentile() {
		return percentile;
	}
	
	
	
}
