package honybot.rewards;

public class RewardFromRequestResponse {

	boolean success;
	String message;
	Reward reward;
	
	public RewardFromRequestResponse(boolean success, String message, Reward reward) {
		super();
		this.success = success;
		this.message = message;
		this.reward = reward;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Reward getReward() {
		return reward;
	}
	
	
	
}
