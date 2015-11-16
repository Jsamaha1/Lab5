package pokerBase;

import java.util.ArrayList;
import java.util.Collections;

import pokerBase.Hand;


public class Lab5_Classes {
	public ArrayList<Hand> bestHands = new ArrayList<Hand>();
	public void scoreHands(ArrayList<Hand> hands, ArrayList<Card> middle, GamePlay gme) {
		// If there are community cards, add them to all the players hands for scoring
		if(!middle.isEmpty()) {
			for (int i = 0; i < hands.size(); i++) {
				for (int j = 0; j < 5; j ++) {
					hands.get(i).AddCardToHand(middle.get(j));
				}
			}
		}
	
		// Get the best possible hand for all of the different possible hands
		for(int i = 0; i < hands.size(); i++) {
			bestHands.add(Hand.EvalHand(hands.get(i)));
		}
		
		// The best hand will be the first hand
		Collections.sort(bestHands, Hand.HandRank);
		
	}
}
