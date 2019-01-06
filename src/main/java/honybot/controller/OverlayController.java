package honybot.controller;

import static spark.Spark.get;

import org.json.simple.JSONObject;

import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.controller.SC2SettingsController;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.sc2.LeagueRange;
import honybot.sc2.SQLPlayerDao;
import honybot.sc2.SQLSc2Dao;
import honybot.sc2.Sc2ChatInfo;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class OverlayController {
	public static void loadRoutes(){
		
		/* Start Counter Pages */
        get("/mmr/:path", (req, res) -> {
        	// :path is honyhelper token
			String path = req.params(":path");
			//get username
			String username = SC2SettingsController.getUsernameByAuth(path);
			//get sc2 settings
			Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
			if(settings != null){
				//get sc2chatinfo using settings
				Sc2ChatInfo info = SQLPlayerDao.getPlayerForChat(settings.getId(), settings.getName(), settings.getRace(), settings.getRegion());
				if(info != null){
					JSONObject params = getParameters(settings.getRegion(), info);
					JSONObject obj = new JSONObject();
					obj.put("name", username);
					obj.put("path", path);
					obj.putAll(params);
		        	return new ModelAndView(obj, "/source/mmr.hbs");
				}
			}
			return new ModelAndView(null, "page_404.hbs");
        }, new HandlebarsTemplateEngine());
		/* End Viewer Pages */
        
		
		get("/mmr/:path/get", (req, res) -> {
			res.type("application/json"); 			
			//path is auth, is hony helper token
			String path = req.params(":path");
			//get username
			String username = SC2SettingsController.getUsernameByAuth(path);
			//get sc2 settings
			Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
			if(settings != null){
				//get sc2chatinfo using settings
				Sc2ChatInfo info = SQLPlayerDao.getPlayerForChat(settings.getId(), settings.getName(), settings.getRace(), settings.getRegion());
				if(info != null){

					JSONObject params = getParameters(settings.getRegion(), info);
		        	return params;
				}
			}
			
			JSONObject obj = new JSONObject();
			obj.put("value", "404 not found");
			return obj;
        });
		
		
	}
	public static JSONObject getParameters(String region, Sc2ChatInfo info){
		
		LeagueRange leagueRange = SQLSc2Dao.getLeagueRange(region, info.getMMR());
		int lower = leagueRange.getLower();
		int upper = leagueRange.getUpper();
		int mmrRange = upper - lower;
		int relativeMmr = info.getMMR() - lower;
		int progress = (relativeMmr * 100) / mmrRange;
		String leagueTier = leagueRange.getLeagueName() + "-" + (leagueRange.getTier()+1);
		if(leagueRange.getLeagueName().equalsIgnoreCase("Grandmaster")){
			leagueTier = "Grandmaster";
		}		
		
		JSONObject obj = new JSONObject();
		obj.put("lower", lower);
		obj.put("upper", upper);
		obj.put("mmrRange", mmrRange);
		obj.put("relativeMmr", relativeMmr);
		obj.put("progress", progress);
		obj.put("mmr", info.getMMR());
		obj.put("league", leagueRange.getLeagueName());
		obj.put("league_tier", leagueTier);
		obj.put("league_img", getLeagueImageURL(leagueRange.getLeagueName()));
		obj.put("tier", leagueRange.getTier() + 1);
		
	
		return obj;
	}
	
	public static String getLeagueImageURL(String league){
		
		switch(league){
		case "Bronze":
			return "http://images.honybot.com/sc/leagues/mmr/bronze.png";	//#ffc500
		case "Silver":
			return "http://images.honybot.com/sc/leagues/mmr/silver.png";	//standard red
		case "Gold":
			return "http://images.honybot.com/sc/leagues/mmr/gold.png";		//#ff7300
		case "Platinum":
			return "http://images.honybot.com/sc/leagues/mmr/platinum.png";	//standard red
		case "Diamond":
			return "http://images.honybot.com/sc/leagues/mmr/diamond.png";	//standard red
		case "Master":
			return "http://images.honybot.com/sc/leagues/mmr/master.png";	//standard red
		case "Grandmaster":
			return "http://images.honybot.com/sc/leagues/mmr/grandmaster.png";	//#ffc500
		}
		return "";
		
	}
}











