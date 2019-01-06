package honybot.web;

public class LoginResult {
	
	private String error;
	
	private WebUser user;
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public WebUser getWebUser() {
		return user;
	}
	
	public void setWebUser(WebUser user) {
		this.user = user;
	}

}