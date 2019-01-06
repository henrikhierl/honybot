package honybot.bot.betting;

import java.util.List;

/**
 * Created by Thomas on 15.10.2017.
 */
public class BetHandler {

    protected BetInterface currentBet;
    protected boolean running = false;
    protected String channelName;
    protected String description;

    public BetHandler(String channelName) {
        this.channelName = channelName;
    }

    public boolean hasBet(){
        return currentBet != null;
    }
    
    public BetInterface getBet() {
    	return this.currentBet;
    }

    public boolean startBet(BetInterface bet, String description) {
        if(hasBet()) {
            return false;
        }
        if(bet == null) {
        	return false;
        }
        this.currentBet = bet;
        this.description = description;
        this.running = true;
        return true;
    }

    public void stopBet() {
        running = false;
    }

    public boolean cancelBet() {
        if(!hasBet()) {
            return false;
        }
        List<UserPointsChange> pointsChangeList = currentBet.cancelBet();
        BetInterface.changePoints(pointsChangeList, channelName);

        currentBet = null;
        running = false;

        return true;
    }

    public boolean addUserBet(String username, String[] arguments) {
        if(!hasBet()) {
            return false;
        }
        if(!running) {
        	return false;
        }
        System.out.println("bethandler has bet, arguments have " + arguments.length);
        return currentBet.addUserBet(username, arguments);
    }
    
    public boolean addUserBet(String username, String option, int amount){
    	if(!hasBet()) {
    		return false;
    	}
        if(!running) {
        	return false;
        }
    	return currentBet.addUserBet(username, option, amount);
    }

    public boolean updateUserBet(String[] arguments) {
        return false;
    }

    public boolean canResolve(String winningOption) {
    	if (!hasBet()) {
    		return false;
    	}
    	return currentBet.canResolve(winningOption);
    }
    
    public List<UserPointsChange> resolveBet(String winningOption) {
        if(!hasBet()) {
            return null;
        }

        if(!currentBet.canResolve(winningOption)) {
            return null;
        }

        List<UserPointsChange> pointsChangeList = currentBet.resolveBet(winningOption);
        if(pointsChangeList != null) {
        	BetInterface.changePoints(pointsChangeList, channelName);
        }
        
        currentBet = null;
        running = false;

        return pointsChangeList;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

	public String getBettersText() {
		if(hasBet()) {
			return currentBet.getBettersText();
		}
		return "";
	}
	
	public String getResolvingText(String winningOption) {
		if(!hasBet()) {
			return "";
		}
		return currentBet.getResolvingText(winningOption);
	}


}
