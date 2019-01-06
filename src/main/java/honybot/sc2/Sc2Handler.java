package honybot.sc2;

import java.util.ArrayList;

public class Sc2Handler {
	
	
	//TODO:ts not that good
	public static Sc2Player getBestFittingOpponent(Sc2Player player, ArrayList<Sc2Player> opponents, String opponentRace, String region){
		if(player != null && opponents != null){
			if(!opponents.isEmpty()){
				if(opponents.size() == 1){
					return opponents.get(0);
				}
				ArrayList<Sc2Player> opponents1 = new ArrayList<>();
				
				//filter by region
				opponents.forEach(opponent -> {
					if(opponent.getRegion() == player.getRegion()){
						opponents1.add(opponent);
					}					
				});
				if(opponents1.isEmpty()){
					return null;
				}else if(opponents1.size() == 1){
					return opponents.get(0);
				}else{	//Filter by race
					ArrayList<Sc2Player> opponents2 = new ArrayList<>();
					opponents1.forEach(opponent -> {
						if(opponent.getRace() == opponentRace){
							opponents2.add(opponent);
						}
					});
					if(opponents2.isEmpty()){
						return null;
					}else if(opponents2.size() == 1){
						return opponents.get(0);
					}else{		//get best fitting opponent by minimal mmr difference
						ArrayList<Sc2Player> opponents3 = new ArrayList<>();
						int minMMRDifference = 100000;
						Sc2Player bestFit = null;						
						for(Sc2Player opponent : opponents3){
							int difference = opponent.getMmr() - player.getMmr();
							if(difference < minMMRDifference){
								minMMRDifference = difference;
								bestFit = opponent;
							}
						}
						return bestFit;
					}
				}
			}
		}		
		return null;
	}
	
}
