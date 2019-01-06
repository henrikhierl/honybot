package honybot.bot.Command;

public class CounterCommand {
	
	private int counter_id;
	private String command;
	private String output;
	private int cost;
	private int permission;
	private int vip;
	private String type;
	
	public CounterCommand(int counter_id, String command, String output, int cost, int permission, int vip,
			String type) {
		super();
		this.counter_id = counter_id;
		this.command = command;
		this.output = output;
		this.cost = cost;
		this.permission = permission;
		this.vip = vip;
		this.type = type;
	}

	public int getCounter_id() {
		return counter_id;
	}

	public String getCommand() {
		return command;
	}

	public String getOutput() {
		return output;
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

	public String getType() {
		return type;
	}
	
	
	
}
