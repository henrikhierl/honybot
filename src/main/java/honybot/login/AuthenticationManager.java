package honybot.login;

import spark.Request;

public class AuthenticationManager {

	public static final String USER_SESSION_ID = "user";
	
	
	public static void addAuthenticatedUser(Request request, User user) {
		request.session(true);
		request.session().attribute(USER_SESSION_ID, user);		
	}

	public static void removeAuthenticatedUser(Request request) {
		request.session().removeAttribute(USER_SESSION_ID);
		
	}

	public static User getAuthenticatedUser(Request request) {
		return request.session().attribute(USER_SESSION_ID);
	}
}
