package honybot.bot.betting;

/**
 * Created by Thomas on 15.10.2017.
 */
public class BetChoice {

    protected String option;
    protected String description;
    protected float quota;

    public BetChoice(String option, String description, float quota) {
        this.option = option;
        this.description = description;
        this.quota = quota;
    }

    public String getOption() {
        return option;
    }

    public String getDescription() {
        return description;
    }

    public float getQuota() {
        return quota;
    }

    public String getOptionAndDescription(){
    	if((description == null) || description.equalsIgnoreCase("")){
    		return option;
    	}
        return option + " (" + description + ")";
    }

    public String printChoice(){
    	if((description == null) || description.equalsIgnoreCase("")){
    		return option+", quota: " + quota;
    	}
        return option+" - (" + description + "), quota: " + quota;
    }

}
