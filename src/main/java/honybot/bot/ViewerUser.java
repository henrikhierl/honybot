package honybot.bot;

public class ViewerUser {
	
	private String name;
	private int points;
	private int time;
	private int bets;
	private int betswon;
	private int pointswon;
	private int pointslost;
	private int totalbetvalue;
	private int vip;
	
	public ViewerUser(String name, int points, int time, int bets, int betswon, int pointswon, int pointslost, int totalbetvalue,
			int vip) {
		
		super();
		this.name = name;
		this.points = points;
		this.time = time;
		this.bets = bets;
		this.betswon = betswon;
		this.pointswon = pointswon;
		this.pointslost = pointslost;
		this.totalbetvalue = totalbetvalue;
		this.vip = vip;
	}
	
	public ViewerUser(String name, int points){
		super();
		this.name = name;
		this.points = points;
		this.time = 0;
		this.bets = 0;
		this.betswon = 0;
		this.pointswon = 0;
		this.pointslost = 0;
		this.totalbetvalue = 0;
		this.vip = 0;
	}
	
	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}

	public int getTime() {
		return time;
	}

	public int getBets() {
		return bets;
	}

	public int getBetswon() {
		return betswon;
	}

	public int getPointswon() {
		return pointswon;
	}

	public int getPointslost() {
		return pointslost;
	}

	public int getTotalbetvalue() {
		return totalbetvalue;
	}

	public int getVip() {
		return vip;
	}

	public void setPoints(int newAmount) {
		points = newAmount;
		
	}
	
}
