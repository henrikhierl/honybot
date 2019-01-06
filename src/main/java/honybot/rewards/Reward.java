package honybot.rewards;

import org.json.simple.JSONObject;

public class Reward {
	
	private int id;
	private String username;
	private String command;
	private String title;
	private String response;
	private String description;
	private String image_url;
	private int cost;
	private int permission;
	private int vip;
	private boolean enabled;
	
	public Reward(int id, String username, String command, String title, String response, String description,
			String image_url, int cost, int permission, int vip, boolean enabled) {
		super();
		this.id = id;
		this.username = username;
		this.command = command;
		this.title = title;
		this.response = response;
		this.description = description;
		this.image_url = image_url;
		this.cost = cost;
		this.permission = permission;
		this.vip = vip;
		this.enabled = enabled;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getCommand() {
		return command;
	}

	public String getTitle() {
		return title;
	}

	public String getResponse() {
		return response;
	}

	public String getDescription() {
		return description;
	}

	public String getImage_url() {
		return image_url;
	}

	public int getCost() {
		return cost;
	}

	public int getPermission() {
		return permission;
	}

	public int getVip() {
		return vip;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean getHasImage() {
		return (image_url != null && !image_url.equalsIgnoreCase(""));
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("username", getUsername());
		json.put("command", getCommand());
		json.put("title", getTitle());
		json.put("response", getResponse());
		json.put("description", getDescription());
		json.put("imageUrl", getImage_url());
		json.put("cost", getCost());
		json.put("permission", getPermission());
		json.put("vip", getVip());
		json.put("enabled", isEnabled());
		return json;
	}

}
