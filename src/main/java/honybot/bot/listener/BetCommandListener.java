package honybot.bot.listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import honybot.bot.IRCBot;
import honybot.bot.MessageHandler;
import honybot.bot.TimerHandler;
import honybot.bot.ViewerUser;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.betting.BetHandler;
import honybot.bot.betting.BetInterface;
import honybot.bot.betting.BetTemplate;
import honybot.bot.betting.BetTemplateDao;
import honybot.bot.betting.ChoiceBet;
import honybot.bot.betting.ClosestBet;
import honybot.bot.betting.UserPointsChange;
import honybot.bot.dao.impl.SQLViewerUserDao;
import honybot.bot.settings.BetSettings;
import honybot.login.SQLUserDao;
import honybot.login.User;

public class BetCommandListener implements ChatEventListener {

	// TODO create templates for bets in webinterface
	private String channelname;
	private MessageHandler messageHandler;
	private IRCBot bot;
	private BetHandler betHandler;
	private Map<String, BetTemplate> templates;
	
	private ScheduledFuture betFuture;
	private ScheduledFuture notificationFuture;

	public BetCommandListener(String username, MessageHandler messageHandler, IRCBot bot) {
		this.channelname = username;
		this.messageHandler = messageHandler;
		this.bot = bot;
		// settings = SQLSettingsDao.getBetSettings(username);
		betHandler = new BetHandler(username);
		
		User user = SQLUserDao.getUserByName(username);
		templates = BetTemplateDao.getBetTemplatesByChannel(user.getId());
		
		betFuture = null;
		notificationFuture = null;
	}

	@Override
	public void onMessage(ChannelMessageEvent event) {
		
		if (!bot.getSettings().getBetSettings().isEnabled() || !bot.getSettings().getPointSettings().isEnabled()) {
			return;
		}

		String command = event.getMessageByIndex(0);
		String[] messageArray = event.getMessage();
		
		
		if(!command.startsWith("!")) {
			return;
		}
		
		if (command.equalsIgnoreCase("!startbet")) {
			if (event.isOp()) {
				startBet(messageArray, event.getChannel());
			}
		} else if (command.equalsIgnoreCase("!cancelbet")) {
			if (event.isOp()) {
				cancelBet(event.getChannel());
			}

		} else if (command.equalsIgnoreCase("!win") && messageArray.length == 1) {
			if (event.isOp()) {
				resolveBet(event.getChannel(), "win");
			}

		} else if (isLoss(command) && messageArray.length == 1) {
			if (event.isOp()) {
				resolveBet(event.getChannel(), "lose");
			}
		}else if(command.equalsIgnoreCase("!endbet") && messageArray.length == 2){
			String option = messageArray[1];
			this.resolveBet(event.getChannel(), option);
			//!bet win 100 !bet lose 100 etc
		} else if ((betHandler.hasBet()) && command.equalsIgnoreCase("!bet") && (messageArray.length == 3)) {
			// get user from database
			ViewerUser user = SQLViewerUserDao.getViewerByName(channelname, event.getUser());
			if (user == null) {
				return;
			}
			// get amount from message
			String amountString = messageArray[1];
			int amount = 0;
			boolean validAmount = false;
			if(amountString.equalsIgnoreCase("all")) {
				amount = user.getPoints();
				validAmount = true;
			} else {
				try {
					amount = Integer.parseInt(amountString);
					validAmount = true;
				} catch (Exception ex) {
				}
			}
			if(!validAmount || amount <= 0) {
				return;
			}
			String pointsName = bot.getSettings().getPointSettings().getPointsName();
			// check if user has enough points
			if(amount > user.getPoints()) {
				messageHandler.sendMessage(true, bot, event.getUser(), "You dont have enough " + pointsName);
				return;
			}
			// try to add wager
			boolean success = betHandler.addUserBet(user.getName(), messageArray[2], amount);
			if(success) {
				SQLViewerUserDao.changePoints(channelname, user.getName(), (-1) * amount);
				messageHandler.sendMessage(true, bot, event.getUser(),
						user.getName() + " placed " + amount + " " + pointsName + " on " + messageArray[2]);
			}
		} else if (command.equalsIgnoreCase("!quota")) {
			String message = betHandler.getBet().getQuotaText();
			messageHandler.sendMessage(false, bot, event.getChannel(), message);
		} else if (command.equalsIgnoreCase("!stats")) {
			/*
			 * ViewerUser user = SQLViewerUserDao.getViewerByName(channelname,
			 * event.getUser()); ViewerUser stats =
			 * SQLViewerUserDao.getGlobalBetStats(channelname); if(user != null
			 * && stats != null){
			 * if(bot.getSettings().getVipSettings().isEnabled()){
			 * if(user.getVip() < 1){ return; } } String pointsName =
			 * bot.getSettings().getPointSettings().getPointsName(); String
			 * message = "Viewers have " + stats.getPoints() + " " + pointsName
			 * + ", viewing for " + stats.getTime() + " minutes. " +
			 * stats.getVip() + " VIP-Levels were bought. " + stats.getBetswon()
			 * + "/" + stats.getBets() + " bets won. " + stats.getPointswon() +
			 * " " + pointsName + " won and " + stats.getPointslost() + " " +
			 * pointsName + " lost. " + "A total of " + stats.getTotalbetvalue()
			 * + " " + pointsName + " were wagered.";
			 * messageHandler.sendMessage(false, bot, event.getChannel(),
			 * message); }
			 */
			
			/*
			messageHandler.sendMessage(false, bot, event.getChannel(),
					"All stats can now be viewed on honybot.com/streamers/" + channelname);
			*/
		} else if (command.equalsIgnoreCase("!mystats")) {
			/*
			 * ViewerUser user = SQLViewerUserDao.getViewerByName(channelname,
			 * event.getUser()); if(user != null){
			 * if(bot.getSettings().getVipSettings().isEnabled()){
			 * if(user.getVip() < 1){ return; } } String pointsName =
			 * bot.getSettings().getPointSettings().getPointsName(); String
			 * message = user.getName() + ": " + user.getBetswon() + "/" +
			 * user.getBets() + " bets won. " + user.getPointswon() + " " +
			 * pointsName + " won and " + user.getPointslost() + " " +
			 * pointsName + " lost. " + "A total of " + user.getTotalbetvalue()
			 * + " " + pointsName + " were wagered.";
			 * messageHandler.sendMessage(false, bot, event.getChannel(),
			 * message); }
			 */
			messageHandler.sendMessage(false, bot, event.getChannel(),
					"All stats can now be viewed on honybot.com/streamers/" + channelname + "?q=" + event.getUser());
		} else if (command.equalsIgnoreCase("!topbetters") || command.equalsIgnoreCase("!top")) {
			ViewerUser user = SQLViewerUserDao.getViewerByName(channelname, event.getUser());
			if (user != null) {
				if (bot.getSettings().getVipSettings().isEnabled()) {
					if (user.getVip() < 1) {
						return;
					}
				}
				String[][] stats = SQLViewerUserDao.getTopBetters(channelname);
				if (stats != null) {
					String pointsName = bot.getSettings().getPointSettings().getPointsName();
					String message = "";
					message += "Most bets: " + stats[0][0] + " [" + stats[0][1] + "], ";
					message += "Most bets won: " + stats[1][0] + " [" + stats[1][1] + "], ";
					message += "Most bets lost: " + stats[2][0] + " [" + stats[2][1] + "], ";
					message += "Most " + pointsName + " won: " + stats[3][0] + " [" + stats[3][1] + "], ";
					message += "Most " + pointsName + " lost: " + stats[4][0] + " [" + stats[4][1] + "], ";
					message += "Most " + pointsName + " wagered: " + stats[5][0] + " [" + stats[5][1] + "], ";
					message += "Most " + pointsName + ": " + stats[6][0] + " [" + stats[6][1] + "], ";
					message += "Most viewtime in minutes: " + stats[7][0] +  "[" + stats[7][1] + "], ";
					messageHandler.sendMessage(false, bot, event.getChannel(), message);
				}
			}
		} else if (command.equalsIgnoreCase("!matchup")) {
			String info = betHandler.getDescription();
			if (!info.equals("")) {
				messageHandler.sendMessage(false, bot, event.getChannel(), "Current match: " + info);
			}
		}

	}

	private boolean isLoss(String command) {
		if (command.equalsIgnoreCase("!lose") || command.equalsIgnoreCase("!loss")
				|| command.equalsIgnoreCase("!loose")) {
			return true;
		}
		return false;
	}
	
	private boolean isBetLoss(String command) {
		if (command.equalsIgnoreCase("lose") || command.equalsIgnoreCase("loss")
				|| command.equalsIgnoreCase("loose")) {
			return true;
		}
		return false;
	}

	@Override
	public void updateSettings() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub

	}
	
	public boolean startBet(String[] messageArray, String channel){
		// command is messageArray[0]
		String name = "sc2";
		String description = "";
		String[] argumentsArray;
		
		if(messageArray.length == 1) {
			name = "sc2";
			argumentsArray = new String[0];
		} else if(messageArray.length >= 2) {
			name = messageArray[1];
			argumentsArray = new String[messageArray.length - 2];
		} else {
			argumentsArray = new String[0];
		}
		String arugments = "";
		// build arguments string
		for(int i = 2; i < messageArray.length; i++) {
			argumentsArray[i-2] = messageArray[i];		// this is horrible
		}
		String arguments = String.join(" ", argumentsArray);
		BetInterface bet = null;
		
		//create bet by name
		name = name.toLowerCase();
		if(templates.containsKey(name)) {
			//create custom bet by template
			BetTemplate template = templates.get(name);
			arguments = template.getArguments();
			description = template.getDescription();
			String type = template.getBetType();
			
			//create bet by type
			if(type.equalsIgnoreCase("choice")) {
				System.out.println(arguments);
				bet = ChoiceBet.create(arguments);
			}else if(type.equalsIgnoreCase("closest")) {
				bet = ClosestBet.create(arguments);
			}
			System.out.println("custom bet created");
		}else if(name.equalsIgnoreCase("sc2")) {
			description = channel + " has started a game!";
			bet = createSC2Bet(channel);
			
		}else if(name.equalsIgnoreCase("pubg") || name.equalsIgnoreCase("battlegrounds")) {
			bet = createPubgBet();
			description = channel + " has entered the Battle Royal! ";
			
		}else if(name.equalsIgnoreCase("win-lose")) {
			bet = createSC2Bet(channel);
			description = channel + " has started a game!";
			// TODO
		}else if(name.equalsIgnoreCase("choice")){
			bet = createChoiceBet(arguments);
		}else if(name.equalsIgnoreCase("closest")){
			bet = createClosestBet(arguments);
		}
		if(bet == null) {
			System.out.println("bet null");
			return false;
		}
		System.out.println("bet not null");
		return startBet(bet, description, channel);
		
	}
	
	public boolean startBet(BetInterface bet, String description, String channel) {
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		if(bet == null) {
			return false;
		}
		
		if(betHandler.hasBet()) {
			return false;
		}
		// start bet
		boolean didStart = betHandler.startBet(bet, description);
		if(!didStart) {
			System.out.println("did not start");
			return false;
		}
		
		String pointsName = bot.getSettings().getPointSettings().getPointsName();
		int timeToBet = bot.getSettings().getBetSettings().getTimeToBet();
		String message = description + " Bet is open for " + timeToBet + " seconds. " + betHandler.getBet().getInfoText(pointsName);
		
		messageHandler.sendMessage(false, bot, channel, message);
		startBetTimer(channel, timeToBet);
		return true;
	}
	
	
	public BetInterface createSC2Bet(String channel) {
		String username = channel;
		if(username.startsWith("#")){
			username = username.substring(1);
		}
		BetSettings settings = bot.getSettings().getBetSettings();
		
		String arguments = "win," + username + " wins the game," + settings.getWinQuota();
		arguments += ";lose," + username + " loses the game," + settings.getLoseQuota();
		
		return ChoiceBet.create(arguments);
	}
	
	public boolean startSC2Bet(String description, String channel) {
		BetInterface bet = createSC2Bet(channel);
		if(bet == null) {
			return false;
		}
		return startBet(bet, description, channel);
	}
	
	public boolean startSC2Bet(String descWin, String descLose, float quota1, float quota2, String channel, String description) {
		String username = channel;
		if(username.startsWith("#")){
			username = username.substring(1);
		}
		
		String arguments = "win,"+descWin+"," + quota1;
		arguments += ";lose,"+descLose+"," + quota2;
		
		BetInterface bet = ChoiceBet.create(arguments);
		if(bet == null) {
			return false;
		}
		return startBet(bet, description, channel);
	}
	
	public boolean startCustomSC2Bet(String option1, String option2, String descTeam1, String descTeam2, float quota1, float quota2, String channel, String description) {
		String username = channel;
		if(username.startsWith("#")){
			username = username.substring(1);
		}
		if(option1 == null || option1.equalsIgnoreCase("")) {
			return false;
		}
		if(option2 == null || option2.equalsIgnoreCase("")) {
			return false;
		}
		
		String arguments = option1+","+descTeam1+"," + quota1;
		arguments += ";"+option2+","+descTeam2+"," + quota2;
		
		//BetInterface bet = new ChoiceBet(arguments);
		BetInterface bet = ChoiceBet.create(arguments);
		if(bet == null) {
			return false;
		}
		return startBet(bet, description, channel);
	}
	
	public BetInterface createPubgBet() {
		
		String arguments = "0.33;1.8;0.1";
		return ClosestBet.create(arguments);
	}
	
	public BetInterface createChoiceBet(String arguments) {
		return ChoiceBet.create(arguments);
	}
	
	public BetInterface createClosestBet(String arguments) {
		return ClosestBet.create(arguments);
	}

	public boolean cancelBet(String channel) {
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		if (betHandler.cancelBet()) {
			String pointsName = bot.getSettings().getPointSettings().getPointsName();
			//Cancel Timers if they are still running
			cancelTimers();
			messageHandler.sendMessage(false, bot, channel,
					"Bet has been cancelled. Everyone got back their " + pointsName);
			return true;
		}
		return false;
	}

	public boolean resolveBet(String channel, String winningOption) {
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		if(!betHandler.canResolve(winningOption)) {
			return false;
		}
		String resolvingMessage = betHandler.getResolvingText(winningOption);
		List<UserPointsChange> winners = betHandler.resolveBet(winningOption);
		
		String winningBetters = "";
		int totalPointsWon = 0;
		if(winners != null){
			for(UserPointsChange winner : winners){
				int amount = (int) Math.round(winner.getPointsAmount());
				totalPointsWon += amount;
				winningBetters = winningBetters + winner.getUsername() + " (" + amount + ")" + ", ";
			}
		}
		
		if (winningBetters.equalsIgnoreCase("")) {
			winningBetters = "Sadly nobody placed a winning bet :(";
		} else {
			int totalWinners = winners.size();
			String pointsName = bot.getSettings().getPointSettings().getPointsName();
			winningBetters = totalWinners + " won " + totalPointsWon + " " + pointsName + ": " + winningBetters.substring(0, winningBetters.length() - 2);
		}
		messageHandler.sendMessage(false, bot, channel, resolvingMessage + ". " + winningBetters);
		cancelTimers();
		return true;
	}

	public String getRaceFromSymbol(String symbol) {
		if (symbol.equalsIgnoreCase("z")) {
			return "Zerg";
		}
		if (symbol.equalsIgnoreCase("t")) {
			return "Terran";
		}
		if (symbol.equalsIgnoreCase("p")) {
			return "Protoss";
		}
		if (symbol.equalsIgnoreCase("r")) {
			return "Random";
		}
		return "";
	}

	public BetHandler getBetHandler() {
		return betHandler;
	}
	
	
	public void startBetTimer(String channel, int timeToBet) {
		BettingTimer bettingTimer = new BettingTimer(bot, channel);
		// name of timer is just for command line, has no other function
		String bettingTimerName = channel + "-betting-timer";
		betFuture = TimerHandler.addOneShotTimer(bettingTimerName, bettingTimer, timeToBet, TimeUnit.SECONDS);
		int notificationTime = bot.getSettings().getBetSettings().getNotification();
		if ((notificationTime > 0) && (notificationTime < timeToBet)) {
			int notificationDelay = timeToBet - notificationTime;
			BettingReminder bettingReminder = new BettingReminder(bot, channel);
			String notificationTimerName = channel + "-betting-notification";
			notificationFuture = TimerHandler.addOneShotTimer(notificationTimerName, bettingReminder, notificationDelay, TimeUnit.SECONDS);
		}
	}

	public class BettingReminder implements Runnable {
		private IRCBot ircBot;
		private String channel;

		public BettingReminder(IRCBot ircBot, String channel) {
			this.ircBot = ircBot;
			this.channel = channel;
		}

		@Override
		public void run() {
			if (betHandler.hasBet()) {
				messageHandler.sendMessage(false, ircBot, channel,
						"Bet will close in " + bot.getSettings().getBetSettings().getNotification() + " seconds!");
				notificationFuture = null;
			}
		}
	}

	public class BettingTimer implements Runnable {
		private IRCBot ircBot;
		private String channel;

		public BettingTimer(IRCBot ircBot, String channel) {
			this.ircBot = ircBot;
			this.channel = channel;
		}

		@Override
		public void run() {
			betHandler.stopBet();
			
			String bettersText = betHandler.getBettersText();
			
			messageHandler.sendMessage(false, ircBot, channel, "Bet is now closed! " + bettersText);
			betFuture = null;
		}
	}
	
	public void cancelTimers(){
		//Cancel Timers if they are still running
		if(betFuture != null){
			betFuture.cancel(true);
			betFuture = null;
		}
		if(notificationFuture != null){
			notificationFuture.cancel(true);
			notificationFuture = null;
		}
	}

}
