package honybot.post;

import org.json.simple.JSONObject;

import honybot.bot.BotHandler;
import honybot.bot.IRCBot;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.listener.BetCommandListener;
import honybot.bot.listener.PointCommandListener;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.login.AuthenticationManager;
import honybot.login.User;
import spark.Request;

public class ControlCenterPostHandler {
	
	//maybe return map to input in reponse?
	public static JSONObject handlePost(Request req){
		User user = AuthenticationManager.getAuthenticatedUser(req);
		if(user == null){
			return null;
		}
		
		System.out.println(user.toString());
		System.out.println(req.queryParams());
		String username = user.getUsername();
		String action = req.queryParams("type");
		
		switch (action){
		case "bot-start": 
			return createAndStartBot(username, username);
		case "bot-restart": 
			return restartBot(username, username);
		case "bot-stop": 
			return destroyAndStopBot(username);
		case "bet-start": 
			return startBet(username);
		case "bet-cancel": 
			return cancelBet(username);
		case "bet-win": 
			return resolveBet(username, "win");
		case "bet-lose": 
			return resolveBet(username, "lose");		
		case "points-to-user":
			return givePointsToUser(username, req);	
		case "points-to-all": 
			return givePointsToAll(username, req);
		case "community":
			return setCommunityOptions(username, req);
		}
		
		return new JSONObject();

	}

	public static JSONObject createAndStartBot(String name, String channel){
		if(BotHandler.createAndStartBot(name, channel)){
			return JSONResponseHelper.createSuccess("The bot has been started successfully!");
		}else{
			return JSONResponseHelper.createError("Bot could not be started, is it already running?");	
		}
	}

	public static JSONObject destroyAndStopBot(String name){
		if(BotHandler.destroyAndStopBot(name)){
			return JSONResponseHelper.createSuccess("Bot successfully stopped!");
		}else{
			return JSONResponseHelper.createError("There was an error stopping the Bot!");	
		}
	}
	
	//TODO: specify if there was an error in stopping or starting the bot!
	public static JSONObject restartBot(String name, String channel){
		destroyAndStopBot(name);
		JSONObject start = createAndStartBot(name, channel);
		if(start.get("type").toString().equalsIgnoreCase("success")){
			return JSONResponseHelper.createSuccess("Bot has been restarted!");
		}else{
			return JSONResponseHelper.createError("There was an error restarting the bot!");
		}
	}
	
	public static JSONObject startBet(String username){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				if(betListener.startSC2Bet(username + " has started a game!", username)){
					return JSONResponseHelper.createSuccess("Bet has been started!");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be started!");
	}

	public static JSONObject cancelBet(String username){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				if(betListener.cancelBet(username)){
					return JSONResponseHelper.createSuccess("Bet successfully cancelled");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be cancelled!");
	}
	
	public static JSONObject resolveBet(String username, String option){
		try {
			IRCBot bot = BotHandler.getBot(username);
			if(bot != null){
				BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
				if(betListener != null){
					if(betListener.resolveBet(username, option)){
						return JSONResponseHelper.createSuccess("Bet has been resolved successfully!");
					}
				}
				return JSONResponseHelper.createError("Bet could not be resolved");
			} else {
				return JSONResponseHelper.createError("Bot is not in the channel");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return JSONResponseHelper.createError("Bet could not be resolved");
		
	}
	
	public static JSONObject givePointsToUser(String username, Request req){
		try{
			IRCBot bot = BotHandler.getBot(username);
			if(bot != null){
				PointCommandListener pointListener = (PointCommandListener)bot.getPointsListener();
				if(pointListener != null){
					String receiver = req.queryParams("user");
					int amount = Integer.parseInt(req.queryParams("amount"));
					String[] message = {"!give", receiver, Integer.toString(amount)};
					ChannelMessageEvent event = new ChannelMessageEvent(bot, "#" + username, username, message, true);
					if(pointListener.modGiveUser(event)){
						return JSONResponseHelper.createSuccess("Successfully gave " + amount + " points to " + receiver);
					}
				}
			} else {
				return JSONResponseHelper.createError("Bot is not in the channel");
			}
		}catch(Exception e){
		}	
		return JSONResponseHelper.createError("There was an error giving Points to the user");
	}

	public static JSONObject givePointsToAll(String username, Request req){
		try{
			IRCBot bot = BotHandler.getBot(username);
			if(bot != null){
				PointCommandListener pointListener = (PointCommandListener)bot.getPointsListener();
				if(pointListener != null){
					int amount = Integer.parseInt(req.queryParams("amount"));
					String[] message = {"!giveall", Integer.toString(amount)};
					ChannelMessageEvent event = new ChannelMessageEvent(bot, "#" + username, username, message, true);
					if(pointListener.giveAll(event)){
						return JSONResponseHelper.createSuccess("Successfully gave " + amount + " points to all users!");
					}
				}
			} else {
				return JSONResponseHelper.createError("Bot is not in the channel");
			}
			
		}catch(Exception e){
		}	
		return JSONResponseHelper.createError("There was an error giving points to all users");
	}
	

	private static JSONObject setCommunityOptions(String username, Request req) {
		String value = req.queryParams("value");
		System.out.println("Setting community option to: " + value);
		if(value.equalsIgnoreCase("true")){
			value = "on";
		}else{
			value = "off";
		}
		if(SQLSettingsDao.setSetting(username, "auto-community", value)){
			return JSONResponseHelper.createSuccess("Automatically joining Honybot-Community is now " + value);
		}
		return JSONResponseHelper.createError("Could not change community settings");
	}
}
