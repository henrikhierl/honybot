package honybot.bot.settings;

import org.json.simple.JSONObject;

public class Sc2Settings {
	
	private boolean enabled;
	private String id;
	private String name;
	private String region;
	private String race;
	private boolean titleEnabled;
	private String ingameTitle;
	private String menuTitle;
	private boolean showGamesEnabled;
	
	public Sc2Settings(boolean enabled, String id, String name, String region, String race, boolean titleEnabled, String ingameTitle, String menuTitle, boolean showGamesEnabled) {
		super();
		this.enabled = enabled;
		this.id = id;
		this.name = name;
		this.region = region;
		this.race = race;
		this.titleEnabled = titleEnabled;
		this.ingameTitle = ingameTitle;
		this.menuTitle = menuTitle;
		this.showGamesEnabled = showGamesEnabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRegion() {
		return region;
	}

	public String getRace() {
		return race;
	}

	public boolean isTitleEnabled() {
		return titleEnabled;
	}

	public String getIngameTitle() {
		return ingameTitle;
	}

	public String getMenuTitle() {
		return menuTitle;
	}

	public boolean isShowGamesEnabled() {
		return showGamesEnabled;
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("enabled", this.isEnabled());
		jsonObject.put("id", this.getId());
		jsonObject.put("name", this.getName());
		jsonObject.put("region", this.getRegion());
		jsonObject.put("race", this.getRace());
		jsonObject.put("titleEnabled", this.isTitleEnabled());
		jsonObject.put("titleIngame", this.getIngameTitle());
		jsonObject.put("titleMenu", this.getMenuTitle());
		jsonObject.put("showGames", this.isShowGamesEnabled());
		
		return jsonObject;
	}
	
}
