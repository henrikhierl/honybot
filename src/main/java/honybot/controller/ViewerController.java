package honybot.controller;

import static spark.Spark.get;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import honybot.bot.ViewerUser;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.login.SQLUserDao;
import honybot.login.User;
import honybot.rewards.Reward;
import honybot.rewards.RewardsDao;
import honybot.sc2.SQLPlayerDao;
import honybot.sc2.Sc2ChatInfo;
import honybot.sc2.Sc2GameDao;
import honybot.twitch.ViewerHandler;
import honybot.web.ContentHandler;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class ViewerController {
	
	@SuppressWarnings("unchecked")
	public static void loadRoutes(){
		
        get("/streamers/:name", (req, res) -> {
			String username = req.params(":name");
			User user = SQLUserDao.getUserByName(username);
			if(user == null){
				return new ModelAndView(false, "page_404.hbs");
			}
			JSONObject obj = new JSONObject();
			obj.put("page_home", true);
			obj.put("title", username);
			JSONObject channel = ViewerHandler.getChannel(username);
			//TODO: check first if channel exists in honybot db
			if(channel == null){
				// Not sure whats up with this if and the to do above. I better keep it ...
			}
			obj.put("logo", (String) channel.get("logo"));
			obj.put("banner", (String) channel.get("profile_banner"));			
			obj.put("name", channel.get("display_name"));
			obj.put("pointsname", SQLSettingsDao.getPointsname(username));

			String viewerName = req.queryParams("q");
        	if(viewerName != null){
    			ViewerUser viewer = SQLViewerUserDao.getViewerByName(username, viewerName);
    			if(viewer != null){
    				obj.put("viewer", ContentHandler.getViewer(username, viewer));
    			}        		
        	}
			obj.put("total", ContentHandler.getStats(username));
			Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
			obj.put("settings", settings);
        	return new ModelAndView(obj, "/viewer/Home.hbs");
        	
        }, new HandlebarsTemplateEngine());
		
		get("/streamers/:name/leaderboard", (req, res) -> {
			String username = req.params(":name");
			User user = SQLUserDao.getUserByName(username);
			if(user == null){
				return new ModelAndView(false, "page_404.hbs");
			}
			JSONObject obj = new JSONObject();
			obj.put("page_leaderboard", true);
			obj.put("title", username + " Leaderboard");
			JSONObject channel = ViewerHandler.getChannel(username);
			//TODO: check first if channel exists in honybot db
			if(channel == null){
				// Not sure whats up with this if and the to do above. I better keep it ...
				//TODO: 404 not found
			}
			obj.put("logo", (String) channel.get("logo"));
			obj.put("banner", (String) channel.get("profile_banner"));
			obj.put("name", username);
			obj.put("pointsname", SQLSettingsDao.getPointsname(username));
			obj.put("total", ContentHandler.getStats(username));
			String viewer_name = req.queryParams("q");			
			if(viewer_name != null){
        		ViewerUser viewer = SQLViewerUserDao.getViewerByName(username, viewer_name);
        		if(viewer != null){
        			JSONObject user_obj = ContentHandler.getViewer(username, viewer);
        			JSONArray users = new JSONArray();
        			users.add(user_obj);
        			obj.put("users", users);
        			return new ModelAndView(obj, "/viewer/Leaderboard.hbs");
        		}    			
			}
			String page = req.queryParams("p");
			int perPage = 50;
			int viewers = SQLViewerUserDao.getViewerAmount(username);
        	if(page == null){
        		obj.put("users", ContentHandler.getUsersPaginated(username, 0)); 
    			obj.put("pagination", ContentHandler.getPagination(viewers, 1, perPage));       		
        	}else{
        		try{
        			int pageNumber = Integer.parseInt(page);
        			int offset = (pageNumber-1)*perPage;
        			obj.put("users", ContentHandler.getUsersPaginated(username, offset));
        			obj.put("pagination", ContentHandler.getPagination(viewers, pageNumber, perPage));
        		}catch(Exception e){
        			obj.put("users", ContentHandler.getUsersPaginated(username, 0)); 
        			obj.put("pagination", ContentHandler.getPagination(viewers, 1, perPage));   
        		}
        	}
        	Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
        	obj.put("settings", settings);
        	return new ModelAndView(obj, "/viewer/Leaderboard.hbs");
        }, new HandlebarsTemplateEngine());
		
		get("/streamers/:name/vip", (req, res) -> {
			String username = req.params(":name");
			User user = SQLUserDao.getUserByName(username);
			if(user == null){
				return new ModelAndView(false, "page_404.hbs");
			}
			JSONObject obj = new JSONObject();
			obj.put("page_vip", true);
			obj.put("title", username + " VIPs");
			JSONObject channel = ViewerHandler.getChannel(username);
			//TODO: check first if channel exists in honybot db
			if(channel == null){
				// Not sure whats up with this if and the to do above. I better keep it ...
				//TODO: 404 not found
			}
			obj.put("logo", (String) channel.get("logo"));
			obj.put("banner", (String) channel.get("profile_banner"));
			obj.put("name", username);
			obj.put("pointsname", SQLSettingsDao.getPointsname(username));
			obj.put("vip", ContentHandler.getVipTableAndStats(username));
			obj.put("total", ContentHandler.getStats(username));

			String viewer_name = req.queryParams("q");		
			if(viewer_name != null){
        		ViewerUser viewer = SQLViewerUserDao.getViewerByName(username, viewer_name);
        		if(viewer != null){
        			JSONObject user_obj = ContentHandler.getVip(username, viewer);
        			JSONArray users = new JSONArray();
        			users.add(user_obj);
        			obj.put("users", users);
        			return new ModelAndView(obj, "/viewer/vip.hbs");
        		}    			
			}
			String page = req.queryParams("p");
			int perPage = 50;
			int viewers = SQLViewerUserDao.getVipAmount(username);

        	if(page == null){
        		obj.put("users", ContentHandler.getVipsPaginated(username, 0)); 
    			obj.put("pagination", ContentHandler.getPagination(viewers, 1, perPage));       		
        	}else{
        		try{
        			int pageNumber = Integer.parseInt(page);
        			int offset = (pageNumber-1)*perPage;
        			obj.put("users", ContentHandler.getVipsPaginated(username, offset));
        			obj.put("pagination", ContentHandler.getPagination(viewers, pageNumber, perPage));
        		}catch(Exception e){
        			obj.put("users", ContentHandler.getVipsPaginated(username, 0)); 
        			obj.put("pagination", ContentHandler.getPagination(viewers, 1, perPage));   
        		}
        	}
        	Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
        	obj.put("settings", settings);
        	return new ModelAndView(obj, "/viewer/vip.hbs");
        }, new HandlebarsTemplateEngine());

		get("/streamers/:name/sc2", (req, res) -> {
			String username = req.params(":name");
			User user = SQLUserDao.getUserByName(username);
			if(user == null){
				return new ModelAndView(false, "page_404.hbs");
			}
			Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
			if(settings == null || !settings.isEnabled() || !settings.isShowGamesEnabled()){
				return new ModelAndView(null, "/page_404.hbs");
			}

			JSONObject obj = new JSONObject();
			obj.put("settings", settings);
			obj.put("page_sc", true);
			obj.put("title", username + " Starcraft 2");
			
			JSONObject channel = ViewerHandler.getChannel(username);
			//TODO: check first if channel exists in honybot db (it should because we checked if user exists)
			if(channel == null){
				//TODO: 404 not found
			}
			obj.put("logo", (String) channel.get("logo"));
			obj.put("banner", (String) channel.get("profile_banner"));
			obj.put("name", username);
			Sc2ChatInfo info = SQLPlayerDao.getPlayerForChat(settings.getId(), settings.getName(), settings.getRace(), settings.getRegion());
			obj.put("mmr", info.getMMR());
			obj.put("league", info.getLeague()+" "+info.getTier());
			obj.put("games-amount", "48:45");
			obj.put("vZ", "12:6");
			obj.put("vT", "12:12");
			obj.put("vP", "6:9");
			
			String page = req.queryParams("p");
			int perPage = 50;
			int user_id = user.getId();
			int total_games = Sc2GameDao.getGameAmount(user_id);
			int offset = 0;
			int pageNumber = 1;
			
			if(page != null){
				try{
        			pageNumber = Integer.parseInt(page);
        		}catch(Exception e){
        			e.printStackTrace();
        		}
			}
			
			offset = (pageNumber-1)*perPage;
			obj.put("games", ContentHandler.getGamesPaginated(user_id, offset));
			obj.put("stats", SQLPlayerDao.getGameStats(user.getId()));
			obj.put("pagination", ContentHandler.getPagination(total_games, pageNumber, perPage));

        	return new ModelAndView(obj, "/viewer/sc2.hbs");
        }, new HandlebarsTemplateEngine());
		

        get("/streamers/:name/rewards", (req, res) -> {
			String username = req.params(":name");
			User user = SQLUserDao.getUserByName(username);
			if(user == null){
				return new ModelAndView(false, "page_404.hbs");
			}
			JSONObject obj = new JSONObject();
			obj.put("page_rewards", true);
			obj.put("title", username + " rewards");
			JSONObject channel = ViewerHandler.getChannel(username);
			//TODO: check first if channel exists in honybot db
			if(channel == null){
				//TODO: 404 not found
			}
			obj.put("logo", (String) channel.get("logo"));
			obj.put("banner", (String) channel.get("profile_banner"));			
			obj.put("name", channel.get("display_name"));
			obj.put("pointsname", SQLSettingsDao.getPointsname(username));

        	Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
        	obj.put("settings", settings);
			
			LinkedList<Reward> rewards = RewardsDao.getRewardsByChannelAsList(username);
			obj.put("rewards", rewards);
			
        	return new ModelAndView(obj, "/viewer/rewards.hbs");
        	
        }, new HandlebarsTemplateEngine());
		
	}
	
}
