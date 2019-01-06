package honybot.helpers;

import honybot.bot.settings.VipSettings;

public class VipHelper {
	
	public static String getVipLevelName(VipSettings settings, int level){
		if(level <= 0 || settings == null){
			return "-";
		}
		String[] vipLevels = settings.getLevels();
		String vipSuffix = settings.getSuffix();
		String levelSuffix = " (" + level + ")";
		if(level <= vipLevels.length){
			//Potato-VIP (2)
			return vipLevels[level-1] + vipSuffix + levelSuffix;
		} else if(level == vipLevels.length + 1){
			//Super Potato-VIP+ (7)
			return vipLevels[vipLevels.length-1] + vipSuffix + "+" + levelSuffix;
		} else{
			//Super Potato-VIP+3 (9)
			return vipLevels[vipLevels.length-1] + vipSuffix + "+" + (level - vipLevels.length) + levelSuffix;
		}
	}	
}
