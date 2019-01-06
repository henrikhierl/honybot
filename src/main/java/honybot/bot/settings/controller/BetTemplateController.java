package honybot.bot.settings.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import honybot.bot.betting.BetInterface;
import honybot.bot.betting.BetTemplate;
import honybot.bot.betting.BetTemplateDao;
import honybot.bot.betting.ChoiceBet;
import honybot.bot.betting.ClosestBet;
import honybot.login.User;
import honybot.post.JSONResponseHelper;
import spark.Request;

public class BetTemplateController {


	public static JSONObject handlePost(User user, Request req){
		
		String type = req.queryParams("type");
		switch(type){
			case("save"):
				return saveBetTemplate(user, req);
			case("delete"):
				return deleteBetTemplate(user, req);
		}
		
		return JSONResponseHelper.createError("invalid action");
		
	}
	
	protected static JSONObject saveBetTemplate(User user, Request req) {
		JSONParser parser = new JSONParser();
		JSONObject template;
		try {
			String template_string = req.queryParams("template");
			System.out.println(template_string);
			template = (JSONObject) parser.parse(template_string);
		}catch(Exception ex){
			return JSONResponseHelper.createError("invalid request format");
		}
		
		// parse id
		int id = -1;
		try {
			Long long_id = (Long) template.get("id");
			id = long_id.intValue();
		}catch(Exception ex){
			// do nothing (no id -> create, id -> update)
		}
		
		String type = "";
		try{
			type = (String) template.get("type");
		}catch(Exception ex){
			return JSONResponseHelper.createError("no type specified");
		}
		if(!type.equalsIgnoreCase("choice") && !type.equalsIgnoreCase("closest")){
			return JSONResponseHelper.createError("invalid type");
		}
		
		String name = "";
		try{
			name = (String) template.get("name");
		}catch(Exception ex){
			return JSONResponseHelper.createError("no name specified");
		}
		
		String description = "";
		try{
			description = (String) template.get("description");
		}catch(Exception ex){
			// don't care
		}
		
		String arguments = "";
		try{
			arguments = (String) template.get("arguments");
		}catch(Exception ex){
			return JSONResponseHelper.createError("arguments missing");
		}
		
		// try to create bet
		BetInterface bet = null;
		if(type.equalsIgnoreCase("choice")){
			bet = ChoiceBet.create(arguments);
		}

		if(type.equalsIgnoreCase("closest")){
			bet = ClosestBet.create(arguments);
		}
		
		if(bet == null){
			return JSONResponseHelper.createError("Template is not valid");
		}
		BetTemplate existingTemplate = BetTemplateDao.getTemplateById(id);
		if(existingTemplate != null){
			if(existingTemplate.getUserId() != user.getId()) {
				return JSONResponseHelper.createError("permission denied");
			}
			existingTemplate.setName(name);	//TODO: check for double names
			existingTemplate.setBetType(type);
			existingTemplate.setArguments(arguments);
			existingTemplate.setDescription(description);
			
			if(BetTemplateDao.updateTemplate(existingTemplate)){
				return JSONResponseHelper.createSuccess("Template updated!");
			}else{
				JSONResponseHelper.createError("Error updating tempalte");
			}
		}else{
			BetTemplate newTemplate = new BetTemplate(-1, user.getId(), name, arguments, description, type);
			int new_id = BetTemplateDao.addTemplate(newTemplate);
			newTemplate.setId(new_id);
			JSONObject obj = new JSONObject();
			obj.put("type", "success");
			obj.put("success", true);
			obj.put("title", "Success!");
			obj.put("text", "Template created!");	
			
			JSONObject data = new JSONObject();
			data.put("id", new_id);
			data.put("user_id", user.getId());
			obj.put("data", data);		
			return obj;
		}
		return JSONResponseHelper.createError("Error creating template!");
	}
	
	protected static JSONObject deleteBetTemplate(User user, Request req) {
		
		int id = -1;
		try {
			String id_string = req.queryParams("id");
			id = Integer.parseInt(id_string);
		}catch(Exception ex){
			return JSONResponseHelper.createError("Invalid ID!");
		}
		if(id == -1){
			return JSONResponseHelper.createError("Invalid ID!");
		}
		
		BetTemplate existingTemplate = BetTemplateDao.getTemplateById(id);
		if(existingTemplate.getUserId() != user.getId()) {
			return JSONResponseHelper.createError("Permission denied!");
		}
		
		if(BetTemplateDao.removeTemplateById(id)){
			return JSONResponseHelper.createSuccess("Template deleted!");
		}else{
			return JSONResponseHelper.createError("Template could note be deleted!");
		}
	}
	
}
