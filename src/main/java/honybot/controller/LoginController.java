package honybot.controller;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import honybot.config.ConfigManager;
import honybot.login.AuthenticationManager;
import honybot.login.SQLUserDao;
import honybot.web.dao.impl.SettingsDaoSQL;
import honybot.login.User;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class LoginController {

	private static final String adminPassword = ConfigManager.getAdminPassword();
	
	private static final String client_id = ConfigManager.getTwitchClientId();
	private static final String client_secret = ConfigManager.getTwitchClientSecret();
	private static final String grant_type = ConfigManager.getTwitchgranttype();
	private static final String redirect_uri = ConfigManager.getTwitchLoginRedirectURI();
	private static final String twitch_login_url = ConfigManager.getTwitchLoginURL();
	
	public static void loadRoutes(){

		get("/login", (req, res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			if(authUser == null){
				res.redirect(twitch_login_url);
			}else if(!authUser.isEnabled()){
				res.redirect("/disabled");
			}else{
				res.redirect("/panel");
			}
        	return null;
        }, new HandlebarsTemplateEngine());
		
		get("/disabled", (req, res) -> {
			return new ModelAndView(null, "/disabled.hbs");
        }, new HandlebarsTemplateEngine());
		
		
		/* Answer from twitch after user "logged in" */
		get("/auth", (req, res) -> {
			String code = req.queryParams("code");
			/* send post to twitch to get auth token */
			if(code != null){
				JSONObject response = postToTwitch(code);
				if(response.containsKey("error")){
					halt(400);
					return null;
				}else if(response.containsKey("access_token")){
					//get name
					String token = (String) response.get("access_token");
					String username = getUserNameViaAPI(token);
					if(username != null){
						//check if user/name already exists
						User user = SQLUserDao.getUserByName(username);
						if(user == null){
							//if not exists: create
							user = new User(
									0, //id
									username,
									token,
									true,
									0
									);
							SQLUserDao.registerUser(user);
							AuthenticationManager.addAuthenticatedUser(req, user);
							//create default Settings
							SettingsDaoSQL.createDefaultSettings(username);
							res.redirect("/panel");
							halt();
						}else{
							//if exists:
							//set new token
							user.setTwitch_auth(token);
							SQLUserDao.updateTwitchAuth(user);
							//set session
							AuthenticationManager.addAuthenticatedUser(req, user);
							res.redirect("/panel");
							halt();
						}
					}else{
						//username == null
						//TODO: return error page
						halt(400);
						return null;
					}
				}else{
					//if no access_token and no error
					//TODO: return error page
					halt(400);
					return null;
				}
			}else{
				//if code param wasnt sent in url
				res.redirect("/login");
				halt();
			}
			//redirect if login was successful
        	return new ModelAndView(null, "page_404.hbs");
        }, new HandlebarsTemplateEngine());
				
		get("/logout", (req, res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			if(authUser != null){
				AuthenticationManager.removeAuthenticatedUser(req);
			}
			res.redirect("/");
        	return null;
        }, new HandlebarsTemplateEngine());
		
		get("/*/logout", (req, res) -> {
			User authUser = AuthenticationManager.getAuthenticatedUser(req);
			if(authUser != null){
				AuthenticationManager.removeAuthenticatedUser(req);
			}
			res.redirect("/");
        	return null;
        });
		
		
		get("/admin", (req, res) -> {
        	return new ModelAndView(null, "admin/login.hbs");
        }, new HandlebarsTemplateEngine());
		
		post("/admin", (req, res) -> {
			String username = req.queryParams("login-username");
			String password = req.queryParams("login-password");
			if(password.equals(adminPassword)){
				User user = SQLUserDao.getUserByName(username);
				if(user == null){
					return new ModelAndView(null, "page_404.hbs");
				}
				AuthenticationManager.addAuthenticatedUser(req, user);
				System.out.println("Admin logged in as " + username);
				res.redirect("/panel");
				halt();
			}else{
				res.redirect("/admin");
				halt();
			}			
			return new ModelAndView(null, "page_404.hbs");
        }, new HandlebarsTemplateEngine());
	}
	
	public static JSONObject postToTwitch(String code){				
		try {
			Document doc = Jsoup.connect("https://api.twitch.tv/kraken/oauth2/token")
					  .data("client_id", client_id)
					  .data("client_secret", client_secret)
					  .data("grant_type", grant_type)
					  .data("redirect_uri", redirect_uri)
					  .data("code", code)
					  .ignoreHttpErrors(true)
					  .ignoreContentType(true)
				  .post();
			String response = doc.body().text();
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(response);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getUserNameViaAPI(String token){
		try {
			Document doc = Jsoup.connect("https://api.twitch.tv/kraken?oauth_token="+token+"&client_id="+client_id)
					  .ignoreHttpErrors(true)
					  .ignoreContentType(true)
				  .get();
			String response = doc.body().text();
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(response);
			JSONObject token_obj = (JSONObject) obj.get("token");
			return (String) token_obj.get("user_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
}
