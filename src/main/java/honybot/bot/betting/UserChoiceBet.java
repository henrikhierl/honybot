package honybot.bot.betting;

/**
 * Created by Thomas on 15.10.2017.
 */
public class UserChoiceBet {

    protected String username;
    protected String option;
    protected int amount;
    protected boolean result;

    public UserChoiceBet(String username, String option, int amount) {
        this.username = username;
        this.option = option;
        this.amount = amount;
        result = false;
    }

    public String getUsername() {
        return username;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    
    public String print() {
    	String optionShort = getOption().substring(0, 1).toUpperCase();
    	return getUsername() + "(" + getAmount() + "|" + optionShort + ")";
    }
}
