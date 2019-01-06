package honybot.bot.settings;

import java.util.Arrays;

import org.json.simple.JSONObject;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class VipSettings {
	
	private boolean enabled;
	private boolean autoExtend;
	private boolean doesStack;
	private int bonus;
	private Expression costFunction;	//maybe expression instead of string?
	private String costFunctionString;
	private String costCommand;
	private String vipCommand;
	private String[] levels;
	private String suffix;
	
	public VipSettings(boolean enabled, boolean autoExtend, boolean doesStack, int bonus, String cost,
			String costCommand, String vipCommand, String[] levels, String suffix) {
		super();
		this.enabled = enabled;
		this.autoExtend = autoExtend;
		this.doesStack = doesStack;
		this.bonus = bonus;
		this.costFunction = new ExpressionBuilder(cost)
				.variables("x")
				.build();
		this.costFunctionString = cost;
		this.costCommand = costCommand;
		this.vipCommand = vipCommand;
		this.levels = levels;
		this.suffix = suffix;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean doesAutoExtend() {
		return autoExtend;
	}

	public boolean doesStack() {
		return doesStack;
	}

	public int getBonus() {
		return bonus;
	}

	public Expression getCostFunction() {
		return costFunction;
	}
	
	public String getCostFunctionString () {
		return costFunctionString;
	}

	public String getCostCommand() {
		return costCommand;
	}

	public String getVipCommand() {
		return vipCommand;
	}

	public String[] getLevels() {
		return levels;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		return "VipSettings [enabled=" + enabled + ", autoExtend=" + autoExtend + ", doesStack=" + doesStack
				+ ", bonus=" + bonus + ", costFunction=" + costFunction + ", costCommand=" + costCommand
				+ ", vipCommand=" + vipCommand + ", levels=" + Arrays.toString(levels) + ", suffix=" + suffix + "]";
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("enabled", this.isEnabled());
		jsonObject.put("command", this.getVipCommand());
		jsonObject.put("cosCommand", this.getCostCommand());
		jsonObject.put("cost", this.getCostFunctionString());
		jsonObject.put("bonus", this.getBonus());
		jsonObject.put("stacking", this.doesStack());
		jsonObject.put("levels", String.join(",", this.getLevels()));
		jsonObject.put("suffix", this.getSuffix());
		jsonObject.put("extensions", this.doesAutoExtend());
		
		return jsonObject;
	}
	
}
