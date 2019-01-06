package honybot.bot.betting;

public class BetTemplate {

	int id;
	int user_id;
	String name;
	String arguments;
	String description;
	String betType;			//TODO: replace by enum?
	
	public BetTemplate(int id, int user_id, String name, String arguments, String description, String betType) {
		this.id = id;
		this.user_id = user_id;
		this.name = name;
		this.arguments = arguments;
		this.description = description;
		this.betType = betType;		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBetType() {
		return betType;
	}

	public void setBetType(String betType) {
		this.betType = betType;
	}
	

	
	
	
}
