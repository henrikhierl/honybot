package honybot.rewards;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;

public class RedeemedReward {
	
	private int id;
	private int reward_id;
	private String viewername;
	private String comment;
	private Timestamp timeStamp;
	
	public RedeemedReward(int id, int reward_id, String viewername, String comment, Timestamp timeStamp) {
		super();
		this.id = id;
		this.reward_id = reward_id;
		this.viewername = viewername;
		this.comment = comment;
		this.timeStamp = timeStamp;
	}

	public int getId() {
		return id;
	}

	public int getReward_id() {
		return reward_id;
	}

	public String getViewername() {
		return viewername;
	}

	public String getComment() {
		return comment;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}	
	
	public String getTimeStampAsString() {
		return timeStamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("d MMM Y - H:mm"));
	}
	
	public Reward getReward() {
		return RewardsDao.getRewardById(this.reward_id);
	}
	
	public String getRewardTitle() {
		Reward reward = RewardsDao.getRewardById(this.reward_id);
		if(reward == null) {
			return "Deleted";
		}
		return reward.getTitle();
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("rewardId", getReward_id());
		json.put("rewardTitle", getRewardTitle());
		json.put("viewerName", getViewername());
		json.put("comment", getComment());
		json.put("timestamp", getTimeStamp().getTime());
		json.put("imageUrl", getReward().getImage_url());
		return json;
	}
	
}
