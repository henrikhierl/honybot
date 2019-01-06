package honybot.error;

public class Error {
	
	//TODO:ts use enum instead of String for level
	private String level;
	private String message;
	
	public Error(String level, String message){
		this.level = level;
		this.message = message;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
