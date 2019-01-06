package honybot.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import honybot.bot.ViewerUser;
import honybot.bot.Command.Command;
import honybot.bot.dao.impl.SQLCounterDao;
import honybot.bot.dao.impl.SQLCustomCommandsDao;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.PointSettings;
import honybot.bot.settings.VipSettings;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.helpers.VipHelper;
import honybot.login.User;
import honybot.post.StarcraftPostHandler;
import honybot.sc2.Sc2Game;
import honybot.sc2.Sc2GameDao;
import honybot.twitch.ViewerHandler;
import net.objecthunter.exp4j.Expression;

public class ContentHandler {
	
	public static HashMap<String, String> getModel(User user){
		HashMap<String, String> model = new HashMap<>();
		model.put("username", user.getUsername());
		return model;
	}
	
	public HashMap<String, String> getDashboardData(HashMap<String, String> model){
		//get stats from StatsDao and insert them into model
		return model;
	}
	
	public HashMap<String, String> getSettingsData(HashMap<String, String> model){
		//get stats from SettingsDao and insert them into model
		return model;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getCustomCommands(String username){
		JSONArray arr = new JSONArray();
		
		HashMap<String, Command> commands =  (HashMap<String,Command>) SQLCustomCommandsDao.getCommands(username);
		commands.forEach((key, command) -> {
			JSONObject tempObject = new JSONObject();
			tempObject.put("command", command.getCommand());
			tempObject.put("response", command.getText());
			tempObject.put("cost", command.getCost());
			tempObject.put("whisper", command.isWhisper());
			tempObject.put("input", command.takesUserInput());
			tempObject.put("permission", command.getPermission());
			tempObject.put("vip", command.getVip());
			
			arr.add(tempObject);
		});
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getCounters(String username){
		JSONArray arr = new JSONArray();
		SQLCounterDao.getCounters(username).forEach(counter -> arr.add(counter.toJSON()));
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getLeaderboard(String username){
		
		JSONArray arr = new JSONArray();
		
		//get all users
		ArrayList<ViewerUser> users = SQLViewerUserDao.getAllViewers(username);
		
		users.forEach((user) -> {
			JSONObject tempObject = new JSONObject();
			tempObject.put("username", user.getName());
			tempObject.put("points", user.getPoints());

			double watchTimeHrs = Math.round(100.0 * (user.getTime()/60.0)) / 100.0;
			tempObject.put("time", watchTimeHrs);
			
			int vip = user.getVip();
			tempObject.put("vip", vip);
			
			arr.add(tempObject);
		});
		return arr;
	}
	

	@SuppressWarnings("unchecked")
	public static JSONObject getViewer(String userName, ViewerUser viewer) {
		JSONObject obj = new JSONObject();
		if (viewer == null) {
			return obj;
		}

		JSONObject channel = ViewerHandler.getChannel(viewer.getName());
		String channelLogoLink = channel != null ? (String) channel.get("logo") : "";
		obj.put("logo", channelLogoLink);
		obj.put("username", viewer.getName());
		obj.put("points", viewer.getPoints());

		double watchTimeHrs = Math.round(100.0 * (viewer.getTime()/60.0)) / 100.0;
		obj.put("time", watchTimeHrs);

		PointSettings pointSettings = SQLSettingsDao.getPointSettings(userName);
		int points_get = pointSettings != null ? pointSettings.getPointsGet() : 0;
		int points_time = pointSettings != null ? pointSettings.getPointsTime() : 0;

		int vip = viewer.getVip();
		obj.put("vip", vip);

		if(vip > 0){
			obj.put("bets", viewer.getBetswon()+"/"+viewer.getBets());
			obj.put("points-won", viewer.getPointswon());
			obj.put("points-lost", viewer.getPointslost());
			obj.put("points-bet", viewer.getTotalbetvalue());

			VipSettings vipSettings = SQLSettingsDao.getVipSettings(userName);
			obj.put("vip_name", VipHelper.getVipLevelName(vipSettings, vip));
			if (vipSettings != null) {
				if(vipSettings.doesStack()){
					points_get += (vipSettings.getBonus() * vip);
				}else{
					points_get += vipSettings.getBonus();
				}
			}
		}

		obj.put("points-get", points_get);
		obj.put("points-time", points_time);
		return obj;
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public static JSONObject getVip(String username, ViewerUser viewer) {
		JSONObject tempObject = new JSONObject();
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		Expression exp = vipSettings.getCostFunction();
		String[] vipLevels = vipSettings.getLevels();
		String suffix = vipSettings.getSuffix();
	
		int vip = viewer.getVip();
		tempObject.put("level", vip);
		tempObject.put("username", viewer.getName());
		tempObject.put("vip_name", VipHelper.getVipLevelName(vipSettings, vip));

		int cost = (int) exp.setVariable("x", vip).evaluate();
		tempObject.put("cost", cost);
		int points = viewer.getPoints();
		tempObject.put("points", points);
		double progress = ((double) points/cost)*100.0;
		if(progress > 100){
			progress = 100;
		}
		String bar_color;
		if(progress >= 100){
			bar_color = "success";
		}else if(progress >= 66){
			bar_color = "info";
		}else if(progress >= 33){
			bar_color = "warning";
		}else{
			bar_color = "danger";
		}
		tempObject.put("bar_color", bar_color);
		tempObject.put("progress", progress);
		
		return tempObject;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getVIPs(String username){
		
		JSONArray arr = new JSONArray();
		
		//get all users
		LinkedList<ViewerUser> users = SQLViewerUserDao.getAllVips(username, 1);
		
		users.forEach((user) -> {
			JSONObject tempObject = new JSONObject();
			tempObject.put("username", user.getName());
			tempObject.put("points", user.getPoints());
			tempObject.put("bets", user.getBetswon()+"/"+user.getBets());
			tempObject.put("points-won", user.getPointswon());
			tempObject.put("points-lost", user.getPointslost());
			tempObject.put("bet-total", user.getTotalbetvalue());

			double watchTimeHrs = Math.round(100.0 * (user.getTime()/60.0)) / 100.0;
			tempObject.put("time", watchTimeHrs);
			
			int vip = user.getVip();
			tempObject.put("vip", vip);
			
			arr.add(tempObject);
		});
		return arr;
	}
	

	@SuppressWarnings("unchecked")
	public static JSONArray getUsersPaginated(String username, int offset){
		JSONArray arr = new JSONArray();
		int perPage = 50;
		
		//get all users
		ArrayList<ViewerUser> users = SQLViewerUserDao.getViewersPaginated(username, offset, perPage);
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		String[] vipLevels = vipSettings.getLevels();
		String suffix = vipSettings.getSuffix();
		
		int index = offset+1;
		for(ViewerUser user : users){
			JSONObject tempObject = new JSONObject();
			tempObject.put("username", user.getName());
			tempObject.put("points", user.getPoints());
			tempObject.put("index", index++);
			tempObject.put("vip_name", VipHelper.getVipLevelName(vipSettings, user.getVip()));

			arr.add(tempObject);
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getStats(String username){
		ViewerUser stats = SQLViewerUserDao.getGlobalBetStats(username);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("points", stats.getPoints());
		jsonObj.put("bets", stats.getBetswon()+"/"+stats.getBets());
		jsonObj.put("points-won", stats.getPointswon());
		jsonObj.put("points-lost", stats.getPointslost());
		jsonObj.put("points-bet", stats.getTotalbetvalue());
		jsonObj.put("viewers", SQLViewerUserDao.getViewerAmount(username));

		double watchTimeHrs = Math.round(100.0 * (stats.getTime()/60.0)) / 100.0;
		jsonObj.put("time", watchTimeHrs);
		
		int vip = stats.getVip();
		jsonObj.put("vip", vip);
		
		return jsonObj;
	}
	

	@SuppressWarnings("unchecked")
	public static JSONObject getVipStats(String username){
		ViewerUser stats = SQLViewerUserDao.getGlobalBetStats(username);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("points", stats.getPoints());
		jsonObj.put("bets", stats.getBetswon()+"/"+stats.getBets());
		jsonObj.put("points-won", stats.getPointswon());
		jsonObj.put("points-lost", stats.getPointslost());
		jsonObj.put("points-bet", stats.getTotalbetvalue());
		jsonObj.put("viewers", SQLViewerUserDao.getViewerAmount(username));

		double watchTimeHrs = Math.round(100.0 * (stats.getTime()/60.0)) / 100.0;
		jsonObj.put("time", watchTimeHrs);
		
		int vip = stats.getVip();
		jsonObj.put("vip", vip);
		
		return jsonObj;
	}
	

	@SuppressWarnings("unchecked")
	public static JSONObject getVipTableAndStats(String username){
		JSONObject obj = new JSONObject();
		JSONObject stats_obj = new JSONObject();
		JSONArray arr = new JSONArray();
		
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		Expression exp = vipSettings.getCostFunction();
		obj.put("function", SQLViewerUserDao.getVipFunction(username));
		String[] vipLevels = vipSettings.getLevels();
		String suffix = vipSettings.getSuffix();
		
		int total_vips = 0;
		int total_vip_levels = 0;
		int points_spent_for_vip = 0;
		if(exp != null){
			int maxVIP = SQLViewerUserDao.getMaxVIP(username);
			
			maxVIP = Math.max(maxVIP+1, 10);
			
			for(int vip = 1; vip <= maxVIP; vip++){
				JSONObject tempObject = new JSONObject();
				tempObject.put("level", vip);
				
				int cost = (int) exp.setVariable("x", vip).evaluate();
				tempObject.put("cost", cost);
				tempObject.put("vip_name", VipHelper.getVipLevelName(vipSettings, vip));

				int vip_amount = SQLViewerUserDao.getVipsWithLevelAmount(username, vip);
				tempObject.put("amount", vip_amount);
				total_vips += vip_amount;
				total_vip_levels += (vip_amount * vip);
				points_spent_for_vip += cost * (vip_amount);
				arr.add(tempObject);
			}
		}else{
			//TODO: handle this
			// JSONObject error_object = new JSONObject();
			// error_object.put("level", "invalid function");
			// error_object.put("cost", "err");
			// arr.add(error_object);
		}	
		stats_obj.put("vips-total", total_vips);
		stats_obj.put("levels-total", total_vip_levels);
		stats_obj.put("points-spent", points_spent_for_vip);
		obj.put("table", arr);
		obj.put("stats", stats_obj);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getVipsPaginated(String username, int offset){
		JSONArray arr = new JSONArray();
		int perPage = 50;
		
		//get all users
		LinkedList<ViewerUser> users = SQLViewerUserDao.getAllVipsPaginated(username, offset, perPage);
		VipSettings vipSettings = SQLSettingsDao.getVipSettings(username);
		Expression exp = vipSettings.getCostFunction();
		String[] vipLevels = vipSettings.getLevels();
		String suffix = vipSettings.getSuffix();
		
		for(ViewerUser user : users){
			JSONObject tempObject = new JSONObject();
			int vip = user.getVip();
			tempObject.put("level", vip);
			tempObject.put("username", user.getName());
			tempObject.put("vip_name", VipHelper.getVipLevelName(vipSettings, vip));
			int cost = (int) exp.setVariable("x", vip+1).evaluate();
			tempObject.put("cost", cost);
			int points = user.getPoints();
			tempObject.put("points", points);
			double progress = ((double) points/cost)*100.0;
			if(progress > 100){
				progress = 100;
			}
			tempObject.put("progress", progress);
			String bar_color;
			if(progress >= 100){
				bar_color = "success";
			}else if(progress >= 66){
				bar_color = "info";
			}else if(progress >= 33){
				bar_color = "warning";
			}else{
				bar_color = "danger";
			}
			tempObject.put("bar_color", bar_color);
			
			arr.add(tempObject);
		}
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getGamesPaginated(int user_id, int offset){
		JSONArray arr = new JSONArray();
		int perPage = 50;
		
		LinkedList<Sc2Game> games = Sc2GameDao.getAllGamesPaginated(user_id, offset, perPage);
		int index = offset+1;
		for(Sc2Game game : games){
			JSONObject tempObject = new JSONObject();
			tempObject.put("index",  index++);
			tempObject.put("opponent", game.getOpponent());
			tempObject.put("opponent-race", StarcraftPostHandler.getRaceFromChar(game.getOpponentRace()));
			boolean victory = false;
			if(game.getResult().equalsIgnoreCase("victory")){
				victory = true;
			}
			tempObject.put("victory", victory);
			tempObject.put("length", secondsToMinutesSeconds(game.getLength()));
			tempObject.put("region", game.getRegion());
			tempObject.put("played", game.formattedDate());
			arr.add(tempObject);
		}
		return arr;
	}

	
	@SuppressWarnings("unchecked")
	public static JSONObject getPagination(int items, int current, int perPage){
		JSONObject pagination = new JSONObject();
		//int viewers = SQLViewerUserDao.getVipAmount(username);
		int pages = (int)Math.ceil((double)items/perPage);
		if(current > pages){
			current = pages;
		}
		//int pages = (perPage + viewers - 1)/perPage;
		JSONObject back = new JSONObject();
		if(current > 1){
			back.put("enabled", true);
			back.put("url", current - 1);
		}else{
			back.put("enabled", false);
			back.put("url", "/");
		}
		pagination.put("back", back);
		JSONObject next = new JSONObject();
		if(current < pages){
			next.put("enabled", true);
			next.put("url", current + 1);
		}else{
			next.put("enabled", false);
			next.put("url", "/");
		}
		pagination.put("next", next);
		JSONArray arr = new JSONArray();
		//show all pages
		if(pages <= 4){
			for(int i = 1; i <= pages; i++){
				JSONObject pageNumber = new JSONObject();
				if(i == current){
					pageNumber.put("active", true);
					pageNumber.put("enabled", true);
				}else{
					pageNumber.put("active", false);
					pageNumber.put("enabled", true);
				}
				pageNumber.put("value", i);
				arr.add(pageNumber);
			}
		}else{
			//add first page
			JSONObject firstPage = new JSONObject();
			if(1 == current){
				firstPage.put("active", true);
			}else{
				firstPage.put("active", false);
			}
			firstPage.put("enabled", true);
			firstPage.put("value", 1);
			arr.add(firstPage);
			//add "..."
			if(current > 3){
				if(current == 4){
					JSONObject notDots = new JSONObject(); 
					notDots.put("enabled", true);
					notDots.put("active", false);
					notDots.put("value", current-2);
					arr.add(notDots);
				}else{
					JSONObject dots1 = new JSONObject(); 
					dots1.put("enabled", false);
					dots1.put("active", false);
					dots1.put("value", "...");
					arr.add(dots1);
				}
			}
			//add pages before current
			if(current > 2){
				JSONObject before = new JSONObject();
				before.put("enabled", true);
				before.put("active", false);
				before.put("value", current-1);
				arr.add(before);
			}
			//add current page
			if(current != 1 && current != pages){
				JSONObject currentPage = new JSONObject();
				currentPage.put("enabled", true);
				currentPage.put("active", true);
				currentPage.put("value", current);
				arr.add(currentPage);
			}
			
			//add pages after current
			if(current < (pages-1)){
				JSONObject after = new JSONObject();
				after.put("enabled", true);
				after.put("active", false);
				after.put("value", current+1);
				arr.add(after);
			}
			//add "..."
			if(current < (pages-2)){
				if(current == pages-3){
					JSONObject notDots = new JSONObject(); 
					notDots.put("enabled", true);
					notDots.put("active", false);
					notDots.put("value", current+2);
					arr.add(notDots);
				}else{
					JSONObject dots2 = new JSONObject(); 
					dots2.put("enabled", false);
					dots2.put("active", false);
					dots2.put("value", "...");
					arr.add(dots2);
				}
			}
			//add last page
			JSONObject lastPage = new JSONObject();
			if(pages == current){
				lastPage.put("enabled", false);
			}else{
				lastPage.put("enabled", true);
			}
			lastPage.put("value", pages);
			arr.add(lastPage);
		}
		pagination.put("pages", arr);
		return pagination;
	}
	
	public static String secondsToMinutesSeconds(int seconds){
		int m = seconds / 60;
		int s = seconds % 60;
		return String.format("%d:%02d", m, s);
	}


}
