package honybot.login;

public class User {

	private int id;
	private String username;
	private String twitch_auth;
	private boolean enabled;
	private int sc2Session;
	
	public User(int id, String username, String twitch_auth, boolean enabled, int sc2Session) {
		super();
		this.id = id;
		this.username = username;
		this.twitch_auth = twitch_auth;
		this.enabled = enabled;
		this.sc2Session = sc2Session;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTwitch_auth() {
		return twitch_auth;
	}

	public void setTwitch_auth(String twitch_auth) {
		this.twitch_auth = twitch_auth;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getSc2Session(){
		return sc2Session;
	}
	
}
