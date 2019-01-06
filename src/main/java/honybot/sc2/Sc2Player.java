package honybot.sc2;

public class Sc2Player {
	
	private String name;
	private String region;
	private String race;
	private int mmr;
	private int streak_current;
	private long lastplayed;
	
	public Sc2Player(String region, String race, int mmr, int streak_current, long lastplayed) {
		super();
		this.region = region;
		this.race = race;
		this.mmr = mmr;
		this.streak_current = streak_current;
		this.lastplayed = lastplayed;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRegion() {
		return region;
	}

	public String getRace() {
		return race;
	}

	public int getMmr() {
		return mmr;
	}

	public int getStreak_current() {
		return streak_current;
	}

	public long getLastplayed() {
		return lastplayed;
	}
	
}
