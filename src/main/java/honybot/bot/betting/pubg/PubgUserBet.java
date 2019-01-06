package honybot.bot.betting.pubg;

public class PubgUserBet implements Comparable<PubgUserBet>{

	private String username;
	private int option, amount;
	private boolean result;
	
	public PubgUserBet(String username, int option, int amount) {
		super();
		this.username = username;
		this.option = option;
		this.amount = amount;
	}

	public String getUsername() {
		return username;
	}

	public int getOption() {
		return option;
	}

	public int getAmount() {
		return amount;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	@Override
	public int compareTo(PubgUserBet bet) {
		return option - bet.getOption();
	}
	
	
}
