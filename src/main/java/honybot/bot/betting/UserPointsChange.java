package honybot.bot.betting;

/**
 * Created by Thomas on 15.10.2017.
 */
public class UserPointsChange {

    protected String username;
    protected int pointsAmount;

    public UserPointsChange(String username, int pointsAmount) {
        this.username = username;
        this.pointsAmount = pointsAmount;
    }

    public String getUsername() {
        return username;
    }

    public int getPointsAmount() {
        return pointsAmount;
    }
}
