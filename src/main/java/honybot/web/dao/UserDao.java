package honybot.web.dao;

import honybot.web.WebUser;

public interface UserDao {
	
	WebUser getUserbyUsername(String username);
	
	boolean registerUser(WebUser user);
	
	boolean connect();

	boolean isConnected();
	
}
