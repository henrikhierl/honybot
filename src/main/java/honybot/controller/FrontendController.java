package honybot.controller;

import static spark.Spark.get;

import org.json.simple.JSONObject;

import honybot.content.FrontendContentHandler;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class FrontendController {
	
	public static void loadRoutes(){
		get("/", (req, res) -> {
			JSONObject model = FrontendContentHandler.getIndexContent();
			return new ModelAndView(model, "/frontend/index.hbs");
        }, new HandlebarsTemplateEngine());
		
		get("/changelog", (req, res) -> {
			JSONObject model = FrontendContentHandler.getTwitchLoginUrl();
			model.put("title", "Changes");
			return new ModelAndView(model, "/frontend/changes.hbs");
        }, new HandlebarsTemplateEngine());
		
		get("/blog", (req, res) -> {
			JSONObject model = FrontendContentHandler.getTwitchLoginUrl();
			model.put("title", "Blog");
			return new ModelAndView(model, "/frontend/blog.hbs");
        }, new HandlebarsTemplateEngine());
	}
	
}
