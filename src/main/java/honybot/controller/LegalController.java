package honybot.controller;

import static spark.Spark.get;

import org.json.simple.JSONObject;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class LegalController {
	
	public static void loadRoutes(){
		
		get("/disclaimer", (req, res) -> {
			JSONObject model = new JSONObject();
			model.put("title", "Disclaimer");
			return new ModelAndView(model, "/frontend/disclaimer.hbs");
        }, new HandlebarsTemplateEngine());
		
		get("/terms", (req, res) -> {
			JSONObject model = new JSONObject();
			model.put("title", "Terms of Services");
			return new ModelAndView(model, "/frontend/terms.hbs");
        }, new HandlebarsTemplateEngine());
		
		get("/privacy", (req, res) -> {
			JSONObject model = new JSONObject();
			model.put("title", "Privacy Policy");
			return new ModelAndView(model, "/frontend/privacy.hbs");
        }, new HandlebarsTemplateEngine());
	}
	
}
