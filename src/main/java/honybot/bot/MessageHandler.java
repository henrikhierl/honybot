package honybot.bot;

import java.util.Arrays;

import honybot.bot.stats.ChatStats;

public class MessageHandler {
	
	//stores timestamps of the last 'rateLimit' messages sent
	int rateLimit = 20;
	private long messageSentTimes[] = new long[rateLimit];
	//points to the time of the oldest message
	private int timePointer = 0;
	private String channelName;
	
	public MessageHandler(String channelName){
		Arrays.fill(messageSentTimes, 0);
		if(channelName.startsWith("#")){
			this.channelName = channelName;
		}else{
			this.channelName = "#"+channelName;
		}
	}
	
	//Maybe use priority queue?
	public void sendMessage(boolean whisper, IRCBot bot, String receiver, String message){
		//TODO: Check if user is on "blacklist" -> that means, if he does not want to get whispers
		//when oldest message is more then 30 seconds old
		long timeOfOldestMessage = messageSentTimes[timePointer];
		long difference = System.currentTimeMillis() - timeOfOldestMessage;		//time of oldest message minus now
		// TODO: replace with library?
		if(difference > 30000){		//oldest message older than 30 seconds -> new message can be sent, else throw away
			if(whisper){
				//bot.sendWhisper(receiver, message);
				ChatStats.addWhisperSent(bot.getUsername());
				//TODO change!
				bot.sendMessage(channelName, message);
			}else{
				ChatStats.addLineSent(bot.getUsername());
				bot.sendMessage(receiver, message);
			}
			messageSentTimes[timePointer] = System.currentTimeMillis();	//sets time of newest message
			timePointer = ((timePointer + 1) % rateLimit); 				//makes pointer circle
		}
	}
}
