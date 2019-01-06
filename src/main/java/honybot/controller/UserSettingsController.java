package honybot.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import honybot.bot.settings.controller.BetSettingsController;
import honybot.bot.settings.controller.BetTemplateController;
import honybot.bot.settings.controller.PointSettingsController;
import honybot.bot.settings.controller.SC2SettingsController;
import honybot.bot.settings.controller.VIPSettingsController;
import honybot.bot.betting.BetTemplate;
import honybot.bot.betting.BetTemplateDao;
import honybot.login.AuthenticationManager;
import honybot.login.User;

public class UserSettingsController {
	
	public static void loadRoutes(){
		/** start modules **/
		get("/panel/getBetSettings", (req, res) -> {
			res.type("application/json"); 
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject current_settings = BetSettingsController.getSettings(channelname);
			current_settings.put("name", channelname);
	    	return current_settings;
	    });

		post("/panel/betting", (req, res) -> {
			System.out.println(req.queryParams());
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject response = BetSettingsController.handleSettings(channelname, req);
	    	return response;
	    });

		get("/panel/betting/templates", (req, res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject response = new JSONObject();
			if(authUser == null) {
				response.put("success", false);
				return response;
			}
			int user_id = authUser.getId();
			
			List<BetTemplate> templates = new LinkedList<>(BetTemplateDao.getBetTemplatesByChannel(user_id).values());
			
			JSONArray templateArray = new JSONArray();
			for(BetTemplate template : templates) {
				// create template object
				JSONObject newTemplate = new JSONObject();
				newTemplate.put("id", template.getId());
				newTemplate.put("user_id", user_id);
				newTemplate.put("name", template.getName());
				newTemplate.put("arguments", template.getArguments());
				newTemplate.put("description", template.getDescription());
				newTemplate.put("type", template.getBetType());
				// add template object to array
				templateArray.add(newTemplate);
			}
			
			response.put("success", true);
			response.put("data", templateArray);
	    	return response;
	    });

		post("/panel/betting/templates", (req, res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject response = BetTemplateController.handlePost(authUser, req);
	    	return response;
	    });
		
		get("/panel/getPointsModuleData", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject current_settings = PointSettingsController.getSettings(channelname);
			current_settings.put("name", channelname);
	    	return current_settings;
	    });

		post("/panel/points", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject response = PointSettingsController.handleSettings(channelname, req);
	    	return response;
	    });
		
		get("/panel/getVIPModuleData", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject current_settings = VIPSettingsController.getSettings(channelname);
			current_settings.put("name", channelname);
	    	return current_settings;
	    });

		post("/panel/vip", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject response = VIPSettingsController.handleSettings(channelname, req);
	    	return response;
	    });
		
		get("/panel/getSC2SettingsData", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject current_settings = SC2SettingsController.getSettings(channelname);
			current_settings.put("name", channelname);
			//check if sc2 profile is valid
			boolean valid_profile = SC2SettingsController.testPlayerProfile(channelname);
			current_settings.put("valid_profile", valid_profile);	
	    	return current_settings;
	    });

		post("/panel/sc2", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject response = SC2SettingsController.handleSettings(channelname, req);
	    	return response;
	    });

		post("/panel/sc2/test", (req, res) -> {
			res.type("application/json");
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			String channelname = authUser.getUsername();
			JSONObject response = SC2SettingsController.testSettings(channelname, req);
	    	return response;
	    });
		/** end modules **/
	}
	

}
