package honybot.post;

import org.json.simple.JSONObject;

import honybot.login.AuthenticationManager;
import honybot.login.User;
import honybot.rewards.RedeemedReward;
import honybot.rewards.Reward;
import honybot.rewards.RewardsDao;
import spark.Request;

public class RedeemPostHandler {
	
	public static JSONObject handlePost(Request req){
		
		User user = AuthenticationManager.getAuthenticatedUser(req);
		if(user == null){
			return JSONResponseHelper.createError("403 permission denied");
		}
		// check action
		String action = req.queryParams("action");
		switch (action){
			case "remove":
				return removeRedeem(req, user);
			default:
				break;
		}
		
		return JSONResponseHelper.createError("Invalid action!");
	}
	
	public static JSONObject removeRedeem(Request req, User user) {
		int id = 0;
		String id_string = req.queryParams("ID");
		if(id_string == null || id_string.equalsIgnoreCase("")){
			return JSONResponseHelper.createError("Invalid ID");
		}
		try {
			id = Integer.parseInt(id_string);
		} catch (Exception e) {
			return JSONResponseHelper.createError("Invalid ID");
		}
		// get existing reward by id
		RedeemedReward redeem = RewardsDao.getRedeemedRewardById(id);
		if(redeem == null) {
			return JSONResponseHelper.createError("Invalid ID");
		}
		Reward reward = redeem.getReward();
		if(!reward.getUsername().equalsIgnoreCase(user.getUsername())){
			return JSONResponseHelper.createSuccess("Access Denied");
		}
		
		if(RewardsDao.removeRedeemedRewardById(id)) {
			return JSONResponseHelper.createSuccess("Redemption deleted successfully");
		} else {
			return JSONResponseHelper.createError("Error deleting reward");
		}
	}
	
}
