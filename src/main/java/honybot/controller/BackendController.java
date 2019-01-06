package honybot.controller;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.MultipartConfigElement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opencsv.CSVReader;

import honybot.bot.BotHandler;
import honybot.bot.ViewerUser;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.BetSettings;
import honybot.bot.settings.PointSettings;
import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.VipSettings;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.login.AuthenticationManager;
import honybot.login.User;
import honybot.post.ControlCenterPostHandler;
import honybot.post.CounterPostHandler;
import honybot.post.CustomCommandPostHandler;
import honybot.post.RedeemPostHandler;
import honybot.post.RewardPostHandler;
import honybot.rewards.Reward;
import honybot.rewards.RedeemedReward;
import honybot.rewards.RewardsDao;
import honybot.web.ContentHandler;
import honybot.bot.stats.*;
import java.sql.Timestamp;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class BackendController {
	
	public static void loadRoutes(){
		
		/* new react app */
		get("/panel", (req, res) -> {
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject content = getBaseContent(user);
			String username = user.getUsername();
			boolean botStarted = BotHandler.hasBot(username);
			content.put("bot-started", botStarted);
			content.put("pointsname", SQLSettingsDao.getPointsname(username));
			content.put("username", username);
			String community_setting = SQLSettingsDao.getSetting(username, "auto-community");
			if(community_setting.equalsIgnoreCase("on")){
				content.put("auto-community", true);
			}else{
				content.put("auto-community", false);
			}
			return new ModelAndView(content, "/backend/panel.hbs");
        }, new HandlebarsTemplateEngine());

		
		post("/panel/control_center", (req, res) -> {
			res.type("application/json"); 
        	return ControlCenterPostHandler.handlePost(req);
        });

		get("/panel/dashboard/viewerStats", (req, res) -> {
			res.type("application/json");

			User user = AuthenticationManager.getAuthenticatedUser(req);
			if (user == null) {
				JSONObject responseObject = new JSONObject();
				responseObject.put("status", 403);
				responseObject.put("message", "Permission Denied");
				return responseObject;
			}
			Timestamp now = new Timestamp(System.currentTimeMillis());
			Timestamp oneWeekAgo = new Timestamp(System.currentTimeMillis() - (604800 * 1000)); 	// -1 week
			List<ChannelViewerStat> stats = SQLStatsDao.getChannelViewerStats(user, oneWeekAgo, now);
			
			JSONArray statsJSON = new JSONArray();
			for (ChannelViewerStat stat : stats) {
				statsJSON.add(new JSONObject(stat.toJSON()));
			}
			
			JSONObject result = new JSONObject();
			result.put("status", 200);
			result.put("message", "success");
			result.put("stats", statsJSON);
			
			return result;
        });

		get("/panel/getCommands", (req, res) -> {
			res.type("application/json");
			
			User user = AuthenticationManager.getAuthenticatedUser(req);
			String username = user.getUsername();
			
			PointSettings pointSettings = SQLSettingsDao.getPointSettings(username);
			VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
			
			JSONObject commands = new JSONObject();
			commands.put("pointsName", pointSettings.getPointsName());
			commands.put("points", pointSettings.getCommand());
			commands.put("getvip", vipSettings.getVipCommand());
			commands.put("vipcost", vipSettings.getCostCommand());
			
			return commands;
        });
		
		get("/panel/getCounters", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject jsonResponse = new JSONObject();
			if(user == null){
				jsonResponse.put("success", false);
				jsonResponse.put("message", "Access Denied");
				return jsonResponse;
			}
			String username = user.getUsername();
			jsonResponse.put("counters", ContentHandler.getCounters(username));
        	return jsonResponse;
        });		
		
		post("/panel/updateCounters", (req, res) -> {
			res.type("application/json");
			return CounterPostHandler.handlePost(req);
        });
		
		get("/panel/commands/custom", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject jsonResponse = new JSONObject();
			if(user == null){
				jsonResponse.put("success", false);
				jsonResponse.put("message", "Access Denied");
				return jsonResponse;
			}
			String username = user.getUsername();
			jsonResponse.put("commands", ContentHandler.getCustomCommands(username));
        	return jsonResponse;
        });
		
		post("/panel/commands/custom", (req, res) -> {
			res.type("application/json");
			return CustomCommandPostHandler.handlePost(req);
        });

		post("/panel/import", (req, res) -> {
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject content = getBaseContent(user);
			String message = "File has been imported successfully";
			
			long maxFileSize = 10000000;       // the maximum size allowed for uploaded files
			long maxRequestSize = 10000000;    // the maximum size allowed for multipart/form-data requests
			int fileSizeThreshold = 10000000;       // the size threshold after which files will be written to disk

			boolean success = true;
			
			//check if max file size = 10MB;
			try{
				MultipartConfigElement configElement = new MultipartConfigElement("/temp", maxFileSize, maxRequestSize, fileSizeThreshold);
				req.attribute("org.eclipse.jetty.multipartConfig", configElement);
				
				try (InputStream input = req.raw().getPart("uploaded_file").getInputStream(); // getPart needs to use same "name" as input field in form
						CSVReader reader = new CSVReader(new InputStreamReader(input));) { 
					ArrayList<ViewerUser> users = new ArrayList<>();
            	    String [] nextLine;
            	    nextLine = reader.readNext();		//skip first line
            	    while ((nextLine = reader.readNext()) != null) {
            	    	try{
            	    		//get name
                	    	String name = nextLine[0];
                	    	//get points
                	    	int points = 0;
                	    	try{
                	    		points = Integer.parseInt(nextLine[2]);
                	    	}catch(Exception ex){
                	    		// do nothing
                	    	}
                	    	System.out.println("Importing user: " + name + ":" + points + " to channel " + user.getUsername());
                	    	users.add(new ViewerUser(name, points));
            	    	}catch(Exception ex){
            	    		// do nothing
            	    	}
            	    }
        	    	// add viewers to db
        	    	if(!SQLViewerUserDao.importViewers(user.getUsername(), users)){
        	    		message = "Error importing users into database";
            	    	success = false;
        	    	}else{
        	    		message = "Successfully imported " + users.size() + " users";
            	    	success = true;
        	    	}
        	        reader.close();
	            }catch (Exception ex){
	            	message = "Error reading file";
	            	success = false;
	            }
			}catch(Exception ex){
				message = "Error uploading file";
				success = false;
			}
            content.put("message", message);
            content.put("success", success);
        	return new ModelAndView(content, "/backend/import.hbs");
        }, new HandlebarsTemplateEngine());
		
		
	
		get("/panel/getRewards", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject jsonResponse = new JSONObject();
			LinkedList<Reward> rewards = RewardsDao.getRewardsByChannelAsList(user.getUsername());
			JSONArray rewardsJSON = new JSONArray();
			for(Reward reward : rewards) {
				rewardsJSON.add(reward.toJSON());
			}
			jsonResponse.put("rewards", rewardsJSON);
        	return jsonResponse;
        });
		
		post("/panel/rewards", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			if(user == null){
				return new ModelAndView("", "/page_404.hbs");
			}
			JSONObject notification = RewardPostHandler.handlePost(req);
        	return notification;
        });

		get("/panel/getRedemptions", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			JSONObject jsonResponse = new JSONObject();
			//TODDO: load redeems
			LinkedList<RedeemedReward> redeems = RewardsDao.getRedeemedRewardsByChannel(user.getUsername());
			JSONArray redeemsJSON = new JSONArray();
			for (RedeemedReward redeem : redeems) {
				redeemsJSON.add(redeem.toJSON());
			}
			jsonResponse.put("redemptions", redeemsJSON);
        	return jsonResponse;
        });
		
		post("/panel/redeems", (req, res) -> {
			res.type("application/json");
			User user = AuthenticationManager.getAuthenticatedUser(req);
			if(user == null){
				return new ModelAndView("", "/page_404.hbs");
			}
			System.out.println("user is not null");
			JSONObject notification = RedeemPostHandler.handlePost(req);
        	return notification;
        });
		
	}
	
	public static JSONObject getBaseContent(User user){
		JSONObject content = new JSONObject();
		String username = user.getUsername();
		content.put("name", username);
		PointSettings pointSettings = SQLSettingsDao.getPointSettings(username);
		BetSettings betSettings = SQLSettingsDao.getBetSettings(username);
		Sc2Settings sc2Settings = SQLSettingsDao.getSC2Settings(username);
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		content.put("pointSettings", pointSettings);
		content.put("betSettings", betSettings);
		content.put("sc2Settings", sc2Settings);
		content.put("vipSettings", vipSettings);
		return content;
	}
	
	public static JSONObject getBaseContentJSON(User user){
		JSONObject content = new JSONObject();
		String username = user.getUsername();
		content.put("name", username);
		PointSettings pointSettings = SQLSettingsDao.getPointSettings(username);
		BetSettings betSettings = SQLSettingsDao.getBetSettings(username);
		Sc2Settings sc2Settings = SQLSettingsDao.getSC2Settings(username);
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		content.put("pointSettings", pointSettings.toJSON());
		content.put("betSettings", betSettings.toJSON());
		content.put("sc2Settings", sc2Settings.toJSON());
		content.put("vipSettings", vipSettings.toJSON());
		return content;
	}
	
}
