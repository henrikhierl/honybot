package honybot.sc2;

public class Sc2Stats {
	
	private int pWon;
	private int pLost;
	private int rWon;
	private int rLost;
	private int tWon;
	private int tLost;
	private int zWon;
	private int zLost;
	
	public Sc2Stats(int pWon, int pLost, int rWon, int rLost, int tWon, int tLost, int zWon, int zLost) {
		super();
		this.pWon = pWon;
		this.pLost = pLost;
		this.rWon = rWon;
		this.rLost = rLost;
		this.tWon = tWon;
		this.tLost = tLost;
		this.zWon = zWon;
		this.zLost = zLost;
	}

	public int getpWon() {
		return pWon;
	}

	public int getpLost() {
		return pLost;
	}

	public int getrWon() {
		return rWon;
	}

	public int getrLost() {
		return rLost;
	}

	public int gettWon() {
		return tWon;
	}

	public int gettLost() {
		return tLost;
	}

	public int getzWon() {
		return zWon;
	}

	public int getzLost() {
		return zLost;
	}

	
	
}
