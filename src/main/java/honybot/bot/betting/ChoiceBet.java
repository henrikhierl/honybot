package honybot.bot.betting;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChoiceBet implements BetInterface {

    protected HashMap<String, BetChoice> choices;
    protected HashMap<String, UserChoiceBet> userBets;

    private ChoiceBet(String arguments, HashMap<String, BetChoice> choices) {

        this.choices = choices;
        userBets = new HashMap<>();
    }
    
    public static ChoiceBet create(String arguments) {
    	
    	HashMap<String, BetChoice> choices = new HashMap<>();

        // parse String to choices
        // arguments = "win,Holly gewinnt,1.8;lose, Holly verliert,1.8"
        String[] optionsAsString = arguments.split(";");

        for(String optionString : optionsAsString) {
            String[] optionParts = optionString.split(",");
            if(optionParts.length < 3) {
                continue;
            }
            BetChoice choice = createBetChoice(optionParts);
            if(choice == null) {
                continue;
            }
            choices.put(choice.getOption(), choice);
        }
        if(choices.size() == 0) {
        	return null;
        }
        return new ChoiceBet(arguments, choices);
    }

    public boolean canResolve(String result) {
        return choices.containsKey(result);
    }

    @Override
    public List<UserPointsChange> resolveBet(String result) {
        if(!canResolve(result)) {
            return null;
        }
        float quota = choices.get(result).getQuota();
        
        BetResultList results = getBetResultList(result);

        List<UserPointsChange> pointsChanges = new LinkedList<>();

        for(UserChoiceBet winner : results.getWinners()) {
            int amountWon = (int)(winner.getAmount() * quota);
            UserPointsChange userPointsChange = new UserPointsChange(winner.getUsername(), amountWon);
            pointsChanges.add(userPointsChange);
        }
        
        return pointsChanges;
    }

    @Override
    public List<UserPointsChange> cancelBet() {
        List<UserPointsChange> pointsChanges = new LinkedList<>();

        for(UserChoiceBet userBet : userBets.values()) {
            String name = userBet.getUsername();
            int amount = userBet.getAmount();
            UserPointsChange userPointsChange = new UserPointsChange(name, amount);
            pointsChanges.add(userPointsChange);
        }

        return pointsChanges;
    }

    @Override
    public boolean addUserBet(String username, String[] arguments) {
        // arguments[0] = option
        // arguments[1] = amount
        if(arguments.length < 2) {
            return false;
        }

        String amountString = arguments[0];
        String option = arguments[1];
        int amount = 0;
        
        if(choices.containsKey(option)) {
    		return false;
    	}

        System.out.println("option: " + option + " amount: " + amountString);
        try {
            amount = Integer.parseInt(amountString);
        } catch (Exception ex) {
            return false;
        }

        if(amount <= 0) {
            return false;
        }

        UserChoiceBet userBet = new UserChoiceBet(username, option, amount);
        if(userBets.containsKey(username)) {
            return updateUserBet(username, arguments);
        }

        // TODO: check and deduct points
        userBets.put(username, userBet);

        return true;
    }

    @Override
    public boolean addUserBet(String username, String option, int amount) {
    	if(!choices.containsKey(option)) {
    		return false;
    	}

        if(amount <= 0) {
            return false;
        }

        UserChoiceBet userBet = new UserChoiceBet(username, option, amount);
        if(userBets.containsKey(username)) {
            return false;
        }

        // TODO: check and deduct points
        userBets.put(username, userBet);

        return true;
    }

    @Override
    public boolean updateUserBet(String username, String[] arguments) {
        return false;
    }

    public BetResultList getBetResultList (String winningOptions) {
    	
    	List<UserChoiceBet> bets = new LinkedList<>(userBets.values());
    	LinkedList<UserChoiceBet> winners = new LinkedList<>();
    	LinkedList<UserChoiceBet> losers = new LinkedList<>();
    	for(UserChoiceBet bet : bets) {
    		if(bet.getOption().equalsIgnoreCase(winningOptions)) {
    			bet.setResult(true);
    			winners.add(bet);
    		} else {
    			bet.setResult(false);
    			losers.add(bet);
    		}
    	}
    	// TODO: sort
        return new BetResultList(winners, losers);
    }

    public static BetChoice createBetChoice(String[] choiceParts) {
        if(choiceParts == null) {
            return null;
        }
        if(choiceParts.length < 3) {
            return null;
        }

        String option = choiceParts[0];
        option = option.replaceAll("\\s+","");
        String description = choiceParts[1];
        String quotaString = choiceParts[2];
        
        try {
            float quota = Float.parseFloat(quotaString);
            return new BetChoice(option, description, quota);
        } catch (Exception ex) {
            return null;
        }

    }
    
    public String getInfoText(String pointsName) {
    	String explenation = "Type \"!bet x option\", where x is the amount of "+pointsName+" you want to set. Available options: ";
    	System.out.println("available options:" + choices.size());
    	String joinedOptions = choices.values().stream().map(BetChoice::printChoice).collect(Collectors.joining("; "));			
		
    	return explenation + joinedOptions;
    }
    
    public String getBettersText() {
    	if(userBets.size() == 0) {
    		return "No bets were placed.";
    	}
    	return "Placed bets: " + userBets.values().stream().map(UserChoiceBet::print).collect(Collectors.joining("; "));
    }
    
    public String getResolvingText(String winningOption) {
    	BetChoice winningChoice = choices.get(winningOption);
    	if(winningChoice == null) {
    		return "";
    	}
    	String winningOptionString = winningChoice.getOptionAndDescription();
    	return "The winning option is " + winningOptionString;
    }

    @Override
    public String getQuotaText() {
        List<String> choiceStrings = choices.values()
                .stream()
                .map(BetChoice::printChoice)
                .collect(Collectors.toList());

        return String.join("; ", choiceStrings);
    }

}
