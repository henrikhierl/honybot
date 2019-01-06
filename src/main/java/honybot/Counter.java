package honybot;

import org.json.simple.JSONObject;

public class Counter {
	
	private String name;
	private int value;
	private int initialValue;
	private String path;

	public Counter(String name, int value, int initialValue, String path) {
		super();
		this.name = name;
		this.value = value;
		this.initialValue = initialValue;
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public int getValue() {
		return value;
	}
	public int getInitialValue() {
		return initialValue;
	}
	public String getPath() {
		return path;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		String name = this.getName();
		json.put("counter-name", name);
		json.put("value", getValue());
		json.put("default", getInitialValue());
		json.put("path", "/counters/" + getPath());
		json.put("commands", "!" + name + ", !dec" + name + ", !reset" + name);
		return json;
	}
	
}
