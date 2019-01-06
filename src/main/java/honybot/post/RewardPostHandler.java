package honybot.post;

import org.json.simple.JSONObject;

import honybot.login.AuthenticationManager;
import honybot.login.User;
import honybot.rewards.Reward;
import honybot.rewards.RewardFromRequestResponse;
import honybot.rewards.RewardsDao;
import spark.Request;

public class RewardPostHandler {
	
	public static JSONObject handlePost(Request req){
		
		User user = AuthenticationManager.getAuthenticatedUser(req);
		if(user == null){
			return JSONResponseHelper.createError("403 permission denied");
		}
		// check action
		String action = req.queryParams("action");
		switch (action){
			case "add": 
				return addReward(req, user);
			case "remove":
				return removeReward(req, user);
			case "update":
				return updateReward(req, user);
		}
		
		return JSONResponseHelper.createError("Invalid action!");
	}
		
	public static JSONObject addReward(Request req, User user) {
		RewardFromRequestResponse resp = createRewardFromRequest(req, user.getUsername());
		if(!resp.isSuccess()) {
			return JSONResponseHelper.createError(resp.getMessage());
		}
		Reward existing_reward = RewardsDao.getRewardByCommandAndUsername(resp.getReward().getCommand(), user.getUsername());
		if(existing_reward != null) {
			return JSONResponseHelper.createError("A reward with this command already exists.");
		}
		int generatedId = RewardsDao.addReward(resp.getReward());
		if(generatedId >= 0) {
			return JSONResponseHelper.createSuccess("Reward added successfully", Integer.toString(generatedId));
		} else {
			return JSONResponseHelper.createError("Error adding reward");
		}
	}
	
	public static JSONObject removeReward(Request req, User user) {
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
		Reward reward = RewardsDao.getRewardById(id);
		if(!reward.getUsername().equalsIgnoreCase(user.getUsername())){
			return JSONResponseHelper.createSuccess("Access Denied");
		}
		// check if reward has redeems
		if(RewardsDao.hasRedeems(reward)) {
			return JSONResponseHelper.createError("Redemptions of this reward still exist. Please delete them before deleting this reward.");
		}
		
		if(RewardsDao.removeRewardById(reward.getId())) {
			return JSONResponseHelper.createSuccess("Reward deleted successfully");
		} else {
			return JSONResponseHelper.createError("Error deleting reward");
		}
	}
	
	public static JSONObject updateReward(Request req, User user) {
		// try to create reward from request
		RewardFromRequestResponse resp = createRewardFromRequest(req, user.getUsername());
		if(!resp.isSuccess()) {
			return JSONResponseHelper.createError(resp.getMessage());
		}
		//check if user is owner of reward
		Reward rewardToUpdate = resp.getReward();
		Reward existingReward = RewardsDao.getRewardByCommandUsernameAndNotCommandID(rewardToUpdate.getCommand(), user.getUsername(), rewardToUpdate.getId());
		if(existingReward != null) {
			return JSONResponseHelper.createError("Another reward with this command already exists");
		}
		if(!rewardToUpdate.getUsername().equalsIgnoreCase(user.getUsername())) {
			return JSONResponseHelper.createError("Access Denied");
		}
		//try to update reward
		if(RewardsDao.updateReward(rewardToUpdate)){
			return JSONResponseHelper.createSuccess("Reward updated successfully");
		} else {
			return JSONResponseHelper.createError("Error updating reward");
		}
	}
	
	public static RewardFromRequestResponse createRewardFromRequest(Request req, String username) {
		
		int id = -1;
		String command = "";
		String title = "";
		String response = "";
		String description = "";
		String image_url = "";
		int cost = 0;
		int permission = 0;
		int vip = 0;
		boolean enabled = true;
		
		String id_string = req.queryParams("ID");
		if(id_string != null && !id_string.equalsIgnoreCase("")){
			try {
				id = Integer.parseInt(id_string);
			} catch (Exception e) {
				return new RewardFromRequestResponse(false, "Invalid ID", null);
			}
		}
		
		// COMMAND
		command =  req.queryParams("command");
		command = command.trim();
		if(command == null || command.equalsIgnoreCase("")) {
			return new RewardFromRequestResponse(false, "command cannot be empty", null);
		}
		if(command.contains(" ")) {
			return new RewardFromRequestResponse(false, "Command cannot contain spaces", null);
		}
		
		// TITLE
		title = req.queryParams("title");
		title = title.trim();
		if(title == null || title.equalsIgnoreCase("")) {
			return new RewardFromRequestResponse(false, "title cannot be empty", null);
		}
		if(title.length() > 255) {
			return new RewardFromRequestResponse(false, "Title can be 255 characters max.", null);
		}
		
		// Response
		response = req.queryParams("response");
		if(response == null){
			response = "";
		}
		
		// Description
		description = req.queryParams("description");
		if(description == null){
			description = "";
		}
		
		// Description
		image_url = req.queryParams("image_url");
		if(image_url == null){
			image_url = "";
		}
		
		try{
			String costString = req.queryParams("cost");
			if((costString == null) || (costString.equals(""))){
				cost = 0;
			}else{
				cost = Integer.parseInt(costString);
			}
			
			String permissionString =  req.queryParams("permission");			
			if((permissionString == null) || (permissionString.equals(""))){
				permission = 0;
			}else{
				permission = Integer.parseInt(permissionString);
			}
			
			String vipString = req.queryParams("vip");
			if((vipString == null) || (vipString.equals(""))){
				vip = 0;
			}else{
				vip = Integer.parseInt(vipString);
			}
		}catch(Exception e){
			return new RewardFromRequestResponse(false, "Invalid input, expected number but got Text", null);
		}
		
		Reward rewardToAdd = new Reward(
			id,
			username,
			command,
			title,
			response,
			description,
			image_url,
			cost,
			permission,
			vip,
			enabled
		);
		
		return new RewardFromRequestResponse(true, "Error adding reward", rewardToAdd);
	}
	
}
