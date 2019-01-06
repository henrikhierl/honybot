package honybot.bot.settings;

import org.json.simple.JSONObject;

public class PointSettings {
	
	private boolean enabled;
	private String pointsName;
	private String command;
	private boolean whisper;
	private String reply;
	private int pointsGet;
	private int pointsTime;
	private int initialPoints;
	
	public PointSettings(boolean enabled, String pointsName, String command, boolean whisper, String reply, int pointsGet,
			int pointsTime, int initialPoints) {
		super();
		this.enabled = enabled;
		this.pointsName = pointsName;
		this.command = command;
		this.whisper = whisper;
		this.reply = reply;
		this.pointsGet = pointsGet;
		this.pointsTime = pointsTime;
		this.initialPoints = initialPoints;
	}
	
	public PointSettings(){
		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getPointsName() {
		return pointsName;
	}

	public String getCommand() {
		return command;
	}

	public boolean isWhisper() {
		return whisper;
	}

	public String getReply() {
		return reply;
	}

	public int getPointsGet() {
		return pointsGet;
	}

	public int getPointsTime() {
		return pointsTime;
	}

	public int getInitialPoints() {
		return initialPoints;
	}

	@Override
	public String toString() {
		return "PointSettings [enabled=" + enabled + ", pointsName=" + pointsName + ", command=" + command
				+ ", whisper=" + whisper + ", reply=" + reply + ", pointsGet=" + pointsGet + ", pointsTime="
				+ pointsTime + "]";
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("enabled", this.isEnabled());
		jsonObject.put("pointsName", this.getPointsName());
		jsonObject.put("command", this.getCommand());
		jsonObject.put("whsiper", this.isWhisper());
		jsonObject.put("reply", this.getReply());
		jsonObject.put("pointsPerInterval", this.getPointsGet());
		jsonObject.put("interval", this.getPointsTime());
		jsonObject.put("initial", this.getInitialPoints());
		
		return jsonObject;
	}
	
	
	
	
}
