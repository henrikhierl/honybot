package honybot.bot.betting;


import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClosestBet implements BetInterface {

    protected float amount;
    protected float baseQuota;
    protected float quotaIncrease;

    protected HashMap<String, UserClosestBet> userBets;


    private ClosestBet(float amount, float baseQuota, float quotaIncrease) {
        userBets = new HashMap<>();
        this.amount = amount;
        this.baseQuota = baseQuota;
        this.quotaIncrease = quotaIncrease;

    }

    public static ClosestBet create(String arguments) {

        // parse String to Bet
        // String to float; >= 1 -> absolut, < 1, prozentual

        // 3;1.8 -> besten 3 bekommen quote 1.8
        // 0.33;1.8 -> besten 33% bekommen quote 1.8

        // 3;1.8;0.1 -> besten 3, schlechtester bekommt quote 1.8, 2. bekommt quote 1.9, bester bekommt quote 2.0
        // 0.33;1.8;0.1 -> besten 33%, schlechtester bekommt quote 1.8, jeder weitere bekommt um je 0.1 besser

        String[] parameters = arguments.split(";");
        if(parameters.length < 2) {
            return null;
        }
        String amountString = parameters[0];
        String baseQuotaString = parameters[1];
        String quotaIncreaseString = "0";
        if(parameters.length >= 3) {
            quotaIncreaseString = parameters[2];
        }

        //parse Strings to float
        try {
            float amount = Float.parseFloat(amountString);
            if(amount <= 0) {
                return null;
            }
            float baseQuota = Float.parseFloat(baseQuotaString);
            float quotaIncrease = Float.parseFloat(quotaIncreaseString);
            return new ClosestBet(amount, baseQuota, quotaIncrease);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public boolean canResolve(String result) {
        try {
            Float.parseFloat(result);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public List<UserPointsChange> resolveBet(String result) {
        if(!canResolve(result)) {
            return null;
        }
        if(userBets.size() == 0) {
            return null;
        }

        // TODO: in the future, if I want to cancel the bet when only 1 person placed a bet, do it here
        /*
        if(userBets.size() <= 1) {
            return null;
        }
        */

        // calculate how many users win the bet
        int absolutWinnerAmount = (int) amount;
        if(amount < 1) {
            absolutWinnerAmount = (int) (amount * userBets.size());
        }
        if(absolutWinnerAmount <= 0) {
            absolutWinnerAmount = 1;
        }
        try {
        	float winningResult = Float.parseFloat(result);
            return getWinners(absolutWinnerAmount, winningResult);
        } catch (Exception ex){
        	return null;
        }
    }

    protected List<UserPointsChange> getWinners(int absolutWinnerAmount, float winningOption) {
        if(amount < 0) {
            return null;
        }
        if(absolutWinnerAmount < 0) {
        	return null;
        }
        List<UserPointsChange> winners = new LinkedList<>();
        if(userBets.size() == 0) {
        	return winners;
        }
        List<UserClosestBet> userBetList = new LinkedList<>(userBets.values());
        List<ClosestBetDistance> distances = new LinkedList<>();
        // get Distances
        for(UserClosestBet userClosestBet : userBetList) {
        	float distance = Math.abs(userClosestBet.getOption() - winningOption);
        	distances.add(new ClosestBetDistance(distance, userClosestBet));
        }
        
        Collections.sort(distances);
        
        List<UserClosestBet> sortedUserBetList = new LinkedList<>();
        for(ClosestBetDistance distance : distances) {
        	sortedUserBetList.add(distance.getUserBet());
        }
        
        absolutWinnerAmount = Math.min(absolutWinnerAmount, distances.size());
        List<ClosestBetDistance> winningDistances = distances.subList(0, absolutWinnerAmount);
        Collections.reverse(winningDistances);
        
        if(winningDistances.size() == 0) {		// should not happen
        	return null;
        }
        
        int index = 0;
        float previousDistance = winningDistances.get(0).getDistance();
        
        for(ClosestBetDistance winningDistance : winningDistances) {
        	
        	// make sure that users with same distance get same quota
            float currentDistance = winningDistance.getDistance();
            if(previousDistance != currentDistance) {
            	index++;
                previousDistance = currentDistance;
            }
        	
            String username = winningDistance.getUserBet().getUsername();
            int betAmount = winningDistance.getUserBet().getAmount();
            float quota = baseQuota + (index * quotaIncrease);
            int amountToAdd = (int) (betAmount * quota);
            winners.add(new UserPointsChange(username, amountToAdd));
        }

        return winners;
    }
    
    public BetResultList getBetResultList (int absolutWinnerAmount, float winningOption) {
    	LinkedList<UserChoiceBet> winners = new LinkedList<>();
    	LinkedList<UserChoiceBet> losers = new LinkedList<>();
    	// TODO: sort
        return new BetResultList(winners, losers);
    }

    @Override
    public List<UserPointsChange> cancelBet() {
        List<UserPointsChange> pointsChangeList = new LinkedList<>();

        for(UserClosestBet userClosestBet : userBets.values()) {
            String username = userClosestBet.getUsername();
            int amount = userClosestBet.getAmount();
            pointsChangeList.add(new UserPointsChange(username, amount));
        }

        return pointsChangeList;
    }

    @Override
    public boolean addUserBet(String username, String[] arguments) {
        if(userBets.containsKey(username)) {
            return updateUserBet(username, arguments);
        }

        if(arguments.length < 2) {
            return false;
        }
        String amountString = arguments[0];
        String optionString = arguments[1];

        int betAmount = 0;
        float option;
        try {
            betAmount = Integer.parseInt(amountString);
            option = Float.parseFloat(optionString);
        } catch (Exception ex) {
            return false;
        }

        if(betAmount <= 0) {
            return false;
        }
        UserClosestBet userBet = new UserClosestBet(username, option, betAmount);
        userBets.put(username, userBet);

        return true;
    }
    
    public boolean addUserBet(String username, String optionString, int betAmount) {
    	if(userBets.containsKey(username)) {
            return false;
        }

        float option;
        try {
            option = Float.parseFloat(optionString);
        } catch (Exception ex) {
            return false;
        }

        if(betAmount <= 0) {
            return false;
        }
        UserClosestBet userBet = new UserClosestBet(username, option, betAmount);
        userBets.put(username, userBet);

        return true;
    }

    @Override
    public boolean updateUserBet(String username, String[] arguments) {
        return false;
    }

    public String getInfoText(String pointsName) {
    	String top = Integer.toString((int)amount);
    	if(amount < 1) {
    		top = Float.toString(amount * 100) + "%";
    	}
    	return "Place a bet on the final placement with \"!bet x y\", where x is the amount of "+pointsName+" and y is the placement e.g. \"!bet 200 1\". The closest " 
    	+ top + " betters will win. Quota is " + baseQuota + " increasing by " + quotaIncrease;
    }
    

    public String getBettersText() {
    	if(userBets.size() == 0) {
    		return "No bets were placed.";
    	}
    	return "Placed bets: " + userBets.values().stream().map(UserClosestBet::print).collect(Collectors.joining("; "));
    }
    
    public String getResolvingText(String winningOption) {
    	return "The winning option is " + winningOption;
    }

    @Override
    public String getQuotaText() {
        String quotaText;
        if (amount < 1) {
            quotaText = "The closest " + Float.toString(amount * 100) + "% of betters will win. ";
        } else {
            quotaText = "The closest " + ((int) amount) + " betters will win. ";
        }
        if (quotaIncrease != 0) {
            quotaText += "Quota is " + baseQuota + ", increasing by " + quotaIncrease;
        } else {
            quotaText += "Quota is " + baseQuota;
        }
        return quotaText;
    }
}
