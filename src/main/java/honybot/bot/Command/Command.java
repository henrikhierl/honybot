package honybot.bot.Command;

public class Command {
	
	private String command;
	private String text;
	private int cost;
	private boolean whisper;
	private boolean takesUserInput;
	private int permission;
	private int vip;
	
	public Command(String command, String text, int cost, boolean whisper, boolean takesUserInput, int permission, int vip) {
		super();
		this.command = command;
		this.text = text;
		this.cost = cost;
		this.whisper = whisper;
		this.takesUserInput = takesUserInput;
		this.permission = permission;
		this.vip = vip;
	}

	public String getCommand() {
		return command;
	}

	public String getText() {
		return text;
	}

	public int getCost() {
		return cost;
	}
	
	public boolean isWhisper(){
		return whisper;
	}

	public boolean takesUserInput() {
		return takesUserInput;
	}
	
	public int getPermission(){
		return permission;
	}
	
	public int getVip(){
		return vip;
	}
}
