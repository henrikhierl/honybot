package honybot.bot.betting;

import java.util.List;

import honybot.bot.dao.impl.SQLViewerUserDao;

public interface BetInterface {

    public boolean canResolve(String result);

    public List<UserPointsChange> resolveBet(String result);

    public List<UserPointsChange> cancelBet();

    public boolean addUserBet(String username, String[] arguments);
    
    public boolean addUserBet(String username, String option, int amount);

    public boolean updateUserBet(String username, String[] arguments);
    
    public String getInfoText(String pointsName);
    
    public String getBettersText();
    
    public String getResolvingText(String winningOption);

    public String getQuotaText();

    // public void getWinners();
    // public void getLosers();
    // public void getUserBets();


    public static void changePoints(List<UserPointsChange> userPointsChangeList, String channelName) {
    	if(userPointsChangeList == null) {
    		return;
    	}
        userPointsChangeList.forEach((user) -> {
            int amount = user.getPointsAmount();
            String name = user.getUsername();
            SQLViewerUserDao.changePoints(channelName, name, amount);
        });
    }
    
    public static void writeStats() {
    	
    }
    
}
