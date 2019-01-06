package honybot.web;

import honybot.web.dao.UserDao;
import honybot.web.dao.impl.UserDaoSQL;

public class WebUserService {
	
	private UserDao userDao;
	private boolean isConnected;
	
	public WebUserService(){
		userDao = new UserDaoSQL();
		isConnected = userDao.connect();
	}
	
	public boolean isConnected(){
		return isConnected;
	}
	
	public LoginResult checkUser(WebUser user) {
		LoginResult result = new LoginResult();
		WebUser userFound = userDao.getUserbyUsername(user.getUsername());
		if(userFound == null) {
			result.setError("Invalid username");
		} else if(!PasswordUtil.verifyPassword(user.getPassword(), userFound.getPassword())) {
			result.setError("Invalid password");
		} else {
			result.setWebUser(userFound);
		}
		return result;
	}
	
	public boolean registerUser(WebUser user) {
		user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
		return userDao.registerUser(user);
	}
	
	public WebUser getUserbyUsername(String username) {
		return userDao.getUserbyUsername(username);
	}
	
}
