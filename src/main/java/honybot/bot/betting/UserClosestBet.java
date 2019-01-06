package honybot.bot.betting;

public class UserClosestBet {


    protected String username;
    protected float option;
    protected int amount;
    protected boolean result;

    public UserClosestBet(String username, float option, int amount) {
        this.username = username;
        this.option = option;
        this.amount = amount;
        result = false;
    }

    public String getUsername() {
        return username;
    }

    public float getOption() {
        return option;
    }

    public void setOption(float option) {
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
    	return getUsername() + "(" + getAmount() + "|" + getOption() + ")";
    }
}
