package honybot.bot.listener;

import honybot.bot.MessageHandler;
import honybot.bot.Event.ChannelMessageEvent;
import honybot.bot.settings.Sc2Settings;
import honybot.login.SQLUserDao;
import honybot.login.User;
import honybot.sc2.SQLPlayerDao;
import honybot.sc2.Sc2ChatInfo;
import honybot.sc2.Sc2GameDao;
import honybot.sc2.Sc2Stats;

public class StarcraftCommandListener implements ChatEventListener {

	private MessageHandler messageHandler;
	String username;
	
	public StarcraftCommandListener(String username, MessageHandler messageHandler){
		this.messageHandler = messageHandler;
		this.username = username;
	}
	
	@Override
	public void onMessage(ChannelMessageEvent event) {
		if(!event.getBot().getSettings().getSc2Settings().isEnabled()){
			return;
		}
		String command = event.getMessageByIndex(0);
		if(command.equalsIgnoreCase("!rank")){
			Sc2Settings sc2Settings = event.getBot().getSettings().getSc2Settings();
			String id = sc2Settings.getId();
			String name = sc2Settings.getName();
			String race  = sc2Settings.getRace();
			String region = sc2Settings.getRegion();
			Sc2ChatInfo info = SQLPlayerDao.getPlayerForChat(id, name, race, region);
			if(info != null){
				String leagueWithTier = "";
				if(info.getLeague().equalsIgnoreCase("GM")){
					leagueWithTier = info.getLeague();
				}else{
					leagueWithTier = info.getLeague() + "-" + info.getTier(); 
				}
				String output = "["+info.getClantag()+"]" + name + "(" + race.substring(0, 1).toUpperCase() + ") - "
						+info.getMMR()+" MMR ("+info.getPercentile()+"th percentile) - "+leagueWithTier+" - "
						+info.getWins()+"/"+info.getLosses();
				int winstreak = info.getWinstreakcurrent();
				if(winstreak > 0){
					output  = output +" - " + info.getWinstreakcurrent() + " game winstreak";
				}
				System.out.println("info:" + output);
				messageHandler.sendMessage(false, event.getBot(), event.getChannel(), output);
			}
		}else if(command.equalsIgnoreCase("!matchup")){
			//Is implemented in betting already
		}else if(command.equalsIgnoreCase("!stats")){
			User user = SQLUserDao.getUserByName(username);
			Sc2Stats stats = Sc2GameDao.getStatsBySession(user.getId(), user.getSc2Session());
			String vsP = stats.getpWon() + ":" + stats.getpLost();
			String vsR = stats.getrWon() + ":" + stats.getrLost();
			String vsT = stats.gettWon() + ":" + stats.gettLost();
			String vsZ = stats.getzWon() + ":" + stats.getzLost();
			//String sc2Name = event.getBot().getSettings().getSc2Settings().getName();
			String race = event.getBot().getSettings().getSc2Settings().getRace().substring(0, 1).toUpperCase();
			String response = "Todays stats - "+race+"vP: "+vsP+", "+race+"vR: "+vsR+", "+race+"vT: "+vsT+". "+race+"vZ: "+vsZ;
			messageHandler.sendMessage(false, event.getBot(), event.getChannel(), response);
		}
		
	}

	@Override
	public void updateSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub

	}

}
