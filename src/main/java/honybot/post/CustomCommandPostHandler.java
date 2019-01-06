package honybot.post;

import org.json.simple.JSONObject;

import honybot.bot.BotHandler;
import honybot.bot.dao.impl.SQLCustomCommandsDao;
import honybot.login.AuthenticationManager;
import honybot.login.User;
import spark.Request;

public class CustomCommandPostHandler {
	
	public static JSONObject handlePost(Request req){
		
		User user = AuthenticationManager.getAuthenticatedUser(req);
		if(user != null){
			String username = user.getUsername();
			String action = req.queryParams("action");
			switch (action){
			case "add": 
				return addCommand(req, username);
			case "remove":
				return removeCommand(req, username);
			}
		}else{
			return JSONResponseHelper.createError("403 permission denied");
		}
		return JSONResponseHelper.createError("Invalid action!");
	}
	

	public static JSONObject addCommand(Request req, String username){
		String command ="";
		String response ="";
		int cost = 0;
		int permission = 0;
		int vip = 0;
		boolean whisper = false;
		boolean input = false;
		
		command =  req.queryParams("command");
		response =  req.queryParams("response");
		
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
				vip = Integer.parseInt(permissionString);
			}
		}catch(Exception e){
			return JSONResponseHelper.createError("Invalid input, expected number but got Text");
		}
		if(req.queryParams("whisper") == null){
			return JSONResponseHelper.createError("There was an error creating the command. Whisper not specified");
		}
		if(req.queryParams("whisper").equalsIgnoreCase("as whisper")){
			whisper = true;
		}
		if(req.queryParams("input") == null){
			return JSONResponseHelper.createError("There was an error creating the command. User Input definition not specified");
		}
		if(req.queryParams("input").equalsIgnoreCase("user input")){
			input = true;
		}
		
		if((command == null) || command.equals("")){
			return JSONResponseHelper.createError("Command can't be empty!");
		}
		if(command.contains(" ")){
			return JSONResponseHelper.createError("Command must not contain any spaces!");
		}
		if((response == null) || response.equals("")){
			return JSONResponseHelper.createError("Response can't be empty!");
		}
		
		if(SQLCustomCommandsDao.addCommand(username, command, response, cost, whisper, input, permission, vip)){			
			if(BotHandler.hasBot(username)){
				BotHandler.getBot(username).getCommandListener().updateSettings();
			}
			return JSONResponseHelper.createSuccess("Successfully created/updated command: " + command);
		}else{
			System.out.println("command not added");
			return JSONResponseHelper.createError("There was an error while adding the command to the database.");
		}

	}
	
	private static JSONObject removeCommand(Request req, String username) {
		String command =  req.queryParams("command");
		if(command != null && !command.equals("")){
			if(SQLCustomCommandsDao.removeCommand(username, command)){
				return JSONResponseHelper.createSuccess("Command removed successfully!");
			}
		}
		return JSONResponseHelper.createError("There was an error removing the command.");
	}	
	
}
