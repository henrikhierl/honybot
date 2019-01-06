package honybot.bot.stats;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

public class ChannelViewerStat {
	
	private int id;
	private int user_id;
	private int viewerAmount;
	private Timestamp timestamp;
	
	public ChannelViewerStat(int id, int user_id, int viewerAmount, Timestamp timestamp) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.viewerAmount = viewerAmount;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public int getUser_id() {
		return user_id;
	}

	public int getViewerAmount() {
		return viewerAmount;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public JSONObject toJSON () {
		JSONObject json = new JSONObject();
		json.put("id", this.getId());
		json.put("user_id", this.getUser_id());
		json.put("viewerAmount", this.getViewerAmount());
		json.put("timestamp", this.getTimestamp().getTime());
		return json;
	}

	@Override
	public String toString() {
		return "ChannelViewerStat [id=" + id + ", user_id=" + user_id + ", viewerAmount=" + viewerAmount
				+ ", timestamp=" + timestamp + "]";
	}
	
}
