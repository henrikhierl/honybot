package honybot.bot.settings;

import org.json.simple.JSONObject;

public class BetSettings {

	private boolean enabled;
	private int timeToBet;
	private int notification;
	private float winQuota;
	private float loseQuota;
	private boolean mmrQuota;
	
	public BetSettings(boolean enabled, int timeToBet, int notification, float winQuota, float loseQuota, boolean mmrQuota) {
		super();
		this.enabled = enabled;
		this.timeToBet = timeToBet;
		this.notification = notification;
		this.winQuota = winQuota;
		this.loseQuota = loseQuota;
		this.mmrQuota = mmrQuota;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getTimeToBet() {
		return timeToBet;
	}

	public int getNotification() {
		return notification;
	}

	public float getWinQuota() {
		return winQuota;
	}

	public float getLoseQuota() {
		return loseQuota;
	}
	
	public boolean isMmrQuotaEnabled(){
		return mmrQuota;
	}

	@Override
	public String toString() {
		return "BetSettings [enabled=" + enabled + ", timeToBet=" + timeToBet + ", notification=" + notification
				+ ", winQuota=" + winQuota + ", loseQuota=" + loseQuota + ", mmrQuota=" + mmrQuota + "]";
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("enabled", this.isEnabled());
		jsonObject.put("timeToBet", this.getTimeToBet());
		jsonObject.put("notification", this.getNotification());
		jsonObject.put("winQuota", this.getWinQuota());
		jsonObject.put("loseQuota", this.getLoseQuota());
		jsonObject.put("mmrQuotaEnabled", this.isMmrQuotaEnabled());
		
		return jsonObject;
	}
	
	
}
