package honybot.controller;

import static spark.Spark.get;

import org.json.simple.JSONObject;

import honybot.Counter;
import honybot.bot.dao.impl.SQLCounterDao;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class CounterController {

	public static void loadRoutes () {
		 get("/counters/:path", (req, res) -> {
			String path = req.params(":path");
			Counter counter = SQLCounterDao.getCounterByPath(path);
			if(counter != null){
				int value = counter.getValue();
				String name = counter.getName();
				
				JSONObject obj = new JSONObject();
				obj.put("value", value);
				obj.put("name", name);
				obj.put("path", path);
	        	return new ModelAndView(obj, "/source/counter.hbs");
			}else{
				return new ModelAndView(null, "page_404.hbs");
			}
        }, new HandlebarsTemplateEngine());
        
		
		get("/counters/:path/get", (req, res) -> {
			res.type("application/json"); 
			String path = req.params(":path");
			Counter counter = SQLCounterDao.getCounterByPath(path);
			JSONObject obj = new JSONObject();
			if(counter != null){
				obj.put("value", counter.getValue());
				return obj;
			}else{
				obj.put("value", "404 not found");
				return obj;
			}
        });
	}
	
}
