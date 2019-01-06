package honybot.sc2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sc2Game {
	
	private int user_id;
	private String player;
	private String opponent;
	private String playerRace;
	private String opponentRace;
	private String result;
	private int length;
	private String region;
	private int session;
	private Timestamp playedDate;
	
	public Sc2Game(int user_id, String player, String opponent, String playerRace, String opponentRace, String result,
			int length, String region, int session, Timestamp playedDate) {
		super();
		this.user_id = user_id;
		this.player = player;
		this.opponent = opponent;
		this.playerRace = playerRace;
		this.opponentRace = opponentRace;
		this.result = result;
		this.length = length;
		this.region = region;
		this.session = session;
		this.playedDate = playedDate;
	}

	public int getUser_id() {
		return user_id;
	}

	public String getPlayer() {
		return player;
	}

	public String getOpponent() {
		return opponent;
	}

	public String getPlayerRace() {
		return playerRace;
	}

	public String getOpponentRace() {
		return opponentRace;
	}

	public String getResult() {
		return result;
	}

	public int getLength() {
		return length;
	}

	public String getRegion() {
		return region;
	}

	public int getSession() {
		return session;
	}

	public Timestamp getPlayedDate() {
		return playedDate;
	}
	
	public String formattedDate () {
		return new SimpleDateFormat("dd. MMM yyyy - HH:mm", Locale.US).format(new Date(playedDate.getTime()));
	}
}
