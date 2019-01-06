package honybot;
import static spark.Spark.*;

import java.util.Set;

import honybot.bot.BotHandler;
import honybot.bot.StopRunningBotsThread;
import honybot.controller.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;


import honybot.config.ConfigManager;
import honybot.login.AuthenticationManager;
import honybot.login.User;
import honybot.web.WebUserService;
import honybot.web.dao.impl.SettingsDaoSQL;


public class Main {

	public static final String USER_SESSION_ID = "user";
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new StopRunningBotsThread()));
		
		exception(Exception.class, (exception, request, response) -> {
		    exception.printStackTrace();
		});
		
		port(ConfigManager.getPort());
		staticFileLocation("/public");
		
		WebUserService service = new WebUserService();
		SettingsDaoSQL settingsDao = new SettingsDaoSQL();
		settingsDao.connect();

		// /panel is secured area which can only be accessed when you are logged in
		before("/panel/*", (req,res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			if(authUser == null){
				res.redirect("/login");
			}else if(!authUser.isEnabled()){
				res.redirect("/disabled");
			}
		});

		before("/panel", (req,res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			if(authUser == null){
				res.redirect("/login");
			}else if(!authUser.isEnabled()){
				res.redirect("/disabled");
			}
		});
				
		UserSettingsController.loadRoutes();
		LoginController.loadRoutes();
		ViewerController.loadRoutes();
		FrontendController.loadRoutes();
		LegalController.loadRoutes();
		BackendController.loadRoutes();
		OverlayController.loadRoutes();
		CounterController.loadRoutes();
		Sc2Controller.loadRoutes();

		BotHandler.restartBots();
        
        get("*", (req, res) -> {
        	return new ModelAndView(null, "page_404.hbs");
        }, new HandlebarsTemplateEngine());
        
        post("*", (req, res) -> {
        	
        	System.out.println("got unexpected post request at " + req.url());
        	Set<String> params = req.queryParams();
        	params.stream().forEach(param -> System.out.print(param + ": " + req.queryParams(param) + ", "));
        	System.out.print("\n");
        	
        	halt();
        	return null;
        }, new HandlebarsTemplateEngine());
        
        (new Thread(new CommandLineController())).start();
    }
	
}
