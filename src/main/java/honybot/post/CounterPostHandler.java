package honybot.post;

import org.json.simple.JSONObject;

import com.mysql.jdbc.StringUtils;

import honybot.Counter;
import honybot.bot.BotHandler;
import honybot.bot.dao.impl.SQLCounterDao;
import honybot.login.AuthenticationManager;
import honybot.login.User;
import spark.Request;

public class CounterPostHandler {
	
	public static JSONObject handlePost(Request req){
		
		User user = AuthenticationManager.getAuthenticatedUser(req);
		if(user != null){
			String username = user.getUsername();
			String action = req.queryParams("action");
			System.out.println(req.queryParams().size());
			switch (action){
			case "add": 
				return addCounter(req, username);
			case "remove":
				return removeCounter(req, username);
			}
		}else{
			return JSONResponseHelper.createError("403 permission denied");
		}
		return JSONResponseHelper.createError("Invalid action!");
	}
	
	public static JSONObject addCounter(Request req, String username){
		
		String name;
		String init_val;
		int initial_value;
		try{
			name = req.queryParams("counter-name");
			name = name.trim();
			if(StringUtils.isNullOrEmpty(name)){
				return JSONResponseHelper.createError("Name can not be empty");
			}
			if(name.contains(" ")){
				return JSONResponseHelper.createError("Name must not contain spaces!");
			}
			init_val = req.queryParams("init_val");
			if(StringUtils.isNullOrEmpty(name)){
				initial_value = 0;
			}else{
				initial_value = Integer.parseInt(init_val);
			}
		}catch(Exception e){
			return JSONResponseHelper.createError("There was an error loading parameters.");
		}		
		if(SQLCounterDao.addCounter(username, name, initial_value)){
			if(BotHandler.hasBot(username)){
				BotHandler.getBot(username).getCounterCommandListener().updateSettings();
			}
			Counter counter = SQLCounterDao.getCounterByName(username, name);
			return JSONResponseHelper.createSuccess("Successfully created counter: " + name, counter.toJSON());
		}else{
			return JSONResponseHelper.createError("There was an error while adding the counter to the database.");
		}
	}

	public static JSONObject removeCounter(Request req, String username){
		String name;
		try{
			name =  req.queryParams("counter-name");
		}catch(Exception e){
			return JSONResponseHelper.createError("There was an error loading parameters.");
		}		
		if(SQLCounterDao.removeCounter(username, name)){
			if(BotHandler.hasBot(username)){
				BotHandler.getBot(username).getCounterCommandListener().updateSettings();
			}
			return JSONResponseHelper.createSuccess("Successfully deleted counter: " + name);
		}else{
			return JSONResponseHelper.createError("There was an error while deleting the counter from the database.");
		}
	}
}
