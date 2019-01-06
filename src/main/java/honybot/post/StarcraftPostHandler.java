package honybot.post;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import honybot.bot.BotHandler;
import honybot.bot.IRCBot;
import honybot.bot.listener.BetCommandListener;
import honybot.bot.settings.Sc2Settings;
import honybot.bot.settings.controller.SC2SettingsController;
import honybot.bot.settings.dao.SQLSettingsDao;
import honybot.login.SQLUserDao;
import honybot.login.User;
import honybot.sc2.SQLPlayerDao;
import honybot.sc2.Sc2ChatInfo;
import honybot.sc2.Sc2Game;
import honybot.sc2.Sc2GameDao;
import honybot.sc2.Sc2Player;
import honybot.twitch.Channel;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import spark.Request;

public class StarcraftPostHandler {
	
	private static String quotaFunction = "e^(x/750)+0.8";
	
	public static JSONObject handleBet(Request req){
		System.out.println(req.queryParams());
		System.out.println(req.toString());
		
		//get username by auth
		String auth = req.queryParams("auth");
		String username = SC2SettingsController.getUsernameByAuth(auth);
		
		if(username == null){
			return null;
		}
		String action = req.queryParams("bet");
		System.out.println("action: " + action);
		
		if(action != null){
			System.out.println("action != null");
			if(action.equalsIgnoreCase("customBet")){
				startCustomBet(req, username);
			}else if(action.equalsIgnoreCase("start")){
				startBet(req, username);
			}else if(action.equalsIgnoreCase("resolve")){
				resolveBet(username, req.queryParams("winningOption"));
			}else if(action.equalsIgnoreCase("cancel")){
				cancelBet(username);
			}
		}
		return null;
	}
	
	public static JSONObject handleGame(Request req){
		System.out.println(req.queryParams());
		System.out.println(req.toString());
		
		String auth = req.queryParams("auth");
		String username = SC2SettingsController.getUsernameByAuth(auth);
		if(username == null){
			return null;
		}
		User user = SQLUserDao.getUserByName(username);
		if(user == null){
			return null;
		}
		try {
			int length = (int)Double.parseDouble(req.queryParams("length"));
			Timestamp playedDate = new Timestamp(System.currentTimeMillis());
			Sc2Game game = new Sc2Game(
						user.getId(),
						req.queryParams("player"),
						req.queryParams("opponent"),
						req.queryParams("player_race"),
						req.queryParams("opponent_race"),
						req.queryParams("result"),
						length,
						req.queryParams("region"),
						user.getSc2Session(),
						playedDate
					);
			Sc2GameDao.addGame(game);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

	public static JSONObject getSession(Request req){
		System.out.println(req.queryParams());
		System.out.println(req.toString());

		String auth = req.queryParams("token");
		String username = SC2SettingsController.getUsernameByAuth(auth);
		if(username == null){
			return null;
		}
		User user = SQLUserDao.getUserByName(username);
		if(user == null){
			return null;
		}
		JSONObject response = new JSONObject();
		response.put("value", user.getSc2Session());
		return response;
	}
	
	public static JSONObject increaseSession(Request req){
		System.out.println(req.queryParams());
		System.out.println(req.toString());

		String auth = req.queryParams("token");
		String username = SC2SettingsController.getUsernameByAuth(auth);
		if(username == null){
			return null;
		}
		User user = SQLUserDao.getUserByName(username);
		if(user == null){
			return null;
		}
		int session = user.getSc2Session();
		if(SQLUserDao.increaseSession(user)){
			session++;
		}

		JSONObject response = new JSONObject();
		response.put("value", session);
		return response;
		
	}

	// TODO: this looks like it could be improved a lot
	public static JSONObject startBet(Request req, String username){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				String player1 = req.queryParams("player1");
				String player2 = req.queryParams("player2");
				String race1 = req.queryParams("race1");
				String race2 = req.queryParams("race2");
				String text = player1 + " vs " + player2 + " (" + race1 + "v" + race2 + ").";
				if(!bot.getSettings().getBetSettings().isMmrQuotaEnabled()){
					if(betListener.startSC2Bet(text, username)){
						return JSONResponseHelper.createSuccess("Bet has been started!");
					}else{
						return JSONResponseHelper.createError("There was an error starting the bet");
					}
				}
				//get region
				Sc2Settings settings = bot.getSettings().getSc2Settings();
				String region = settings.getRegion();
				//get opponent name and race
				String streamerName = settings.getName();
					//check if streamer is part of the game
				if(player1.equalsIgnoreCase(streamerName) || player2.equalsIgnoreCase(streamerName)){
					String opponentName = player1;
					String opponentRace = getRaceFromChar(race1);
					if(player1.equalsIgnoreCase(streamerName)){
						opponentName = player2;
						opponentRace = getRaceFromChar(race2);
					}
					//get opponents
					ArrayList<Sc2Player> opponents = SQLPlayerDao.getPossibleOpponents(region, opponentRace, opponentName);
					//check if opponent is empty or if there are multiple opponents
					if(opponents.size() != 0){
						Sc2Player player = SQLPlayerDao.getPlayer(settings.getId(), settings.getName(), settings.getRace(), settings.getRegion());
						Sc2Player opponent = getOpponentByMinMmrDifference(player.getMmr(), opponents);
						//check if difference in mmr is 1000 or less
						int mmrDiff =  opponent.getMmr() - player.getMmr();
						if(Math.abs(mmrDiff) <= 1000){
							//calculate quota
							Expression func = new ExpressionBuilder(quotaFunction)
									.variable("x")
									.build();
							float winQuota = (float) func.setVariable("x", mmrDiff).evaluate();
							winQuota = roundToTwoPlaces(winQuota);
							float loseQuota = (float) func.setVariable("x", (-1)*mmrDiff).evaluate();
							loseQuota = roundToTwoPlaces(loseQuota);
							//create bonus text
							text += " " + mmrDiff + " MMR difference. Quotas: win=" + winQuota + ", lose=" + loseQuota;
							boolean success = betListener.startSC2Bet(streamerName+" wins the game", streamerName+" loses the game", winQuota, loseQuota, username, text);
							if(success) {
								return JSONResponseHelper.createSuccess("Bet has been started");
							}else{
								return JSONResponseHelper.createSuccess("Bet coult not be");
							}							
						}
					}
				}
				if(betListener.startSC2Bet(text, username)){
					return JSONResponseHelper.createSuccess("Bet has been started!");
				}else{
					return JSONResponseHelper.createError("There was an error starting the bet");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be started. Is the Bot running?");
	}
	
	public static JSONObject startCustomBet(Request req, String username){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				String player1 = req.queryParams("player1");
				String player2 = req.queryParams("player2");
				String race1 = req.queryParams("race1");
				String race2 = req.queryParams("race2");
				String text = player1 + " vs " + player2 + " (" + race1 + "v" + race2 + ").";
				String option1 = req.queryParams("option1");
				String desc1 = req.queryParams("description1");
				String option2 = req.queryParams("option2");
				String desc2 = req.queryParams("description2");
				
				float quota1 = bot.getSettings().getBetSettings().getWinQuota();
				float quota2 = bot.getSettings().getBetSettings().getWinQuota();
				
				boolean success = betListener.startCustomSC2Bet(option1, option2, desc1, desc2, quota1, quota2, username, text);

				if(success){
					return JSONResponseHelper.createSuccess("Bet has been started!");
				}else{
					return JSONResponseHelper.createError("There was an error starting the bet");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be started");
	}

	public static JSONObject cancelBet(String username){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				if(betListener.cancelBet(username)){
					return JSONResponseHelper.createSuccess("Bet successfully cancelled");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be cancelled!");
	}
	
	public static JSONObject resolveBet(String username, String option){
		IRCBot bot = BotHandler.getBot(username);
		if(bot != null){
			BetCommandListener betListener = (BetCommandListener)bot.getBetListener();
			if(betListener != null){
				if(betListener.resolveBet(username, option)){
					return JSONResponseHelper.createSuccess("Bet has been resolved successfully!");
				}
			}
		}
		return JSONResponseHelper.createError("Bet could not be resolved!");
	}
	
	
	public static void handleStatusUpdate(Request req){
		String token = req.queryParams("token");
		String username = SC2SettingsController.getUsernameByAuth(token);
		if(username == null){
			return;
		}
		Sc2Settings settings = SQLSettingsDao.getSC2Settings(username);
		//check if user has auto updating enabled
		if(settings == null || !settings.isTitleEnabled()){
			return;
		}

		String gameType = req.queryParams("game-type");
		String title = "";
		if(gameType.equalsIgnoreCase("menu")){
			title = settings.getMenuTitle();
		}else{
			title = settings.getIngameTitle();
			
			String opponent = req.queryParams("opponent-name");
			String opponentRace = req.queryParams("opponent-race");
			String playerRace = req.queryParams("player-race");
			String timeString = req.queryParams("time");

			String matchup = playerRace + "v" + opponentRace;
			int minutes = (int) (Double.parseDouble(timeString) / 60);
			
			title = title.replaceAll("\\$mu", matchup);
			title = title.replaceAll("\\$opp", opponent);
			title = title.replaceAll("\\$or", opponentRace);
			title = title.replaceAll("\\$OR", getRaceFromChar(opponentRace));
			title = title.replaceAll("\\$pr", playerRace);
			title = title.replaceAll("\\$PR", getRaceFromChar(playerRace));
			title = title.replaceAll("\\$time", Integer.toString(minutes));
		}
		Sc2ChatInfo sc2Info = SQLPlayerDao.getPlayerForChat(settings.getId(), 
				settings.getName(), 
				settings.getRace(), 
				settings.getRegion());
		if(sc2Info != null){
			
			String league = sc2Info.getLeague();
			String leagueShort = league.substring(0, 1).toUpperCase();
			if(leagueShort.equalsIgnoreCase("G")){
				leagueShort = "GM";
			}
			int tier = sc2Info.getTier();
			int mmr = sc2Info.getMMR();
			
			title = title.replaceAll("\\$l", leagueShort);
			title = title.replaceAll("\\$L", league);
			title = title.replaceAll("\\$tier", Integer.toString(tier));
			title = title.replaceAll("\\$mmr", Integer.toString(mmr));
		}
		
		title = title.replaceAll("\\$status", gameType);
		if(gameType.equalsIgnoreCase("ingame")){
			title = title.replaceAll("\\$game", "ingame");
		}else{
			title = title.replaceAll("\\$game", "");
		}
		if(gameType.equalsIgnoreCase("menu")){
			title = title.replaceAll("\\$menu", "menu");
		}else{
			title = title.replaceAll("\\$menu", "");
		}
		if(gameType.equalsIgnoreCase("replay")){
			title = title.replaceAll("\\$replay", "replay");
		}else{
			title = title.replaceAll("\\$replay", "");
		}
		Channel.setTitle(username, title);
	}
	
	public static String getRaceFromChar(String race){
		if(race.equalsIgnoreCase("p")){
			return "Protoss";
		}else if(race.equalsIgnoreCase("t")){
			return "Terran";
		}else if(race.equalsIgnoreCase("z")){
			return "Zerg";
		}else if(race.equalsIgnoreCase("r")){
			return "Random";
		}
		return null;
	}	
	
	public static Sc2Player getOpponentByMinMmrDifference(int playerMMR, ArrayList<Sc2Player> opponents){
		if(opponents.size() == 1){
			return opponents.get(0);
		}
		Sc2Player bestOpponent = opponents.get(0);
		int minDiff = Math.abs(playerMMR - bestOpponent.getMmr()); 
		for(Sc2Player currentOpponent : opponents){
			int currentDiff = Math.abs(playerMMR - currentOpponent.getMmr());
			if( currentDiff < minDiff){
				bestOpponent = currentOpponent;
				minDiff = currentDiff;
			}
		}
		return bestOpponent;
	}

	private static float roundToTwoPlaces(float number){
		return (float) (Math.round(number*100.0)/100.0);
	}	
}
	
