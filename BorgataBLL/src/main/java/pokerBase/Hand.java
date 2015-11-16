package pokerBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import domain.HandDomainModel;
import domain.CardDomainModel;
import enums.eCardNo;
import enums.eHandStrength;
import enums.eRank;

public class Hand extends HandDomainModel {

	private static Deck dNonWildDeck = new Deck();
	
	public Hand() {

	}
	
	private Hand(ArrayList<Card> Cards)
	{
		ArrayList<CardDomainModel> cDM = new ArrayList<CardDomainModel>();
		
		for (Card c: Cards)
		{
			cDM.add(new Card(c.getSuit(), c.getRank(), c.getWild(), c.getCardNbr()));
		}
		this.setCardsInHand(cDM);
		
	}

	public ArrayList<Card> getCardsInHand() {		
		ArrayList<Card> CardsInHand = new ArrayList<Card>();
		
		for (CardDomainModel cDM: this.getCards())
		{
			Card c = new Card(cDM.getSuit(), cDM.getRank(), cDM.getWild(), cDM.getCardNbr());
			CardsInHand.add(c);
		}				
		return CardsInHand;
	}
	

	
	public void AddCardToHand(Card c) {
		if (this.getCards() == null) {
			setCardsInHand(new ArrayList<CardDomainModel>());
		}
		this.getCards().add(c);
	}

	public Card GetCardFromHand(int location) {
		return (Card) getCards().get(location);
	}

	public Hand(Deck d) {
		ArrayList<CardDomainModel> Import = new ArrayList<CardDomainModel>();
		for (int x = 0; x < 5; x++) {
			Import.add(d.drawFromDeck());
		}
		setCardsInHand(Import);
	}

	public static Hand EvalHand(ArrayList<CardDomainModel> SeededHand) {

		Deck d = new Deck();
		Hand h = new Hand(d);
		h.setCardsInHand(SeededHand);

		return h;
	}

	/**
	 * Takes in a hand and analyzes each card. If the card is a Joker or wild
	 * card, it sets the hand as unnatural. Then, it calls SubstituteHand on the
	 * hand. Ultimately, what is returned is an ArrayList of Hand ranging from 1
	 * hand (if there were not Jokers/wild cards to millions of hands.
	 * 
	 * @param h
	 * @return
	 */
	private static ArrayList<Hand> ExplodeHands(Hand h) {
		ArrayList<Hand> HandsToReturn = new ArrayList<Hand>();
		HandsToReturn.add(h);

		for (int a = 0; a < h.getCards().size(); a++) {
			if (h.getCards().get(a).getRank().getRank() == eRank.JOKER.getRank()
					|| h.getCards().get(a).getWild() == true) {
				h.setbNatural(0); // Hand is not natural
			}
		}

		for (int a = 0; a < h.getCards().size(); a++)

		{
			HandsToReturn = SubstituteHand(HandsToReturn, a);
		}

		return HandsToReturn; // ArrayList of all possible Hands

	}

	/**
	 * Determines number of Jokers/Wilds in Hand and substitutes each for every
	 * other card value in a natural Deck. Each Hand combination is then stored
	 * in an ArrayList of all possible Hands and returned to the caller.
	 * 
	 * @param inHands
	 * @param SubCardNo
	 * @return
	 */
	private static ArrayList<Hand> SubstituteHand(ArrayList<Hand> inHands, int SubCardNo) {

		ArrayList<Hand> SubHands = new ArrayList<Hand>();

		for (Hand h : inHands) {
			ArrayList<Card> c = h.getCardsInHand();
			if (c.get(SubCardNo).getRank().getRank() == eRank.JOKER.getRank() || c.get(SubCardNo).getWild() == true) {

				for (CardDomainModel JokerSub : dNonWildDeck.getCards()) {
					ArrayList<Card> SubCards = new ArrayList<Card>();
					SubCards.add((Card) JokerSub); // Adds substitute card for
											// Joker/wild. Will iterate through
											// entire naural deck

					for (int a = 0; a < 5; a++) { // Adds original cards that
													// are not wild/Jokers
						if (SubCardNo != a) {
							SubCards.add((Card) h.getCards().get(a));
						}
					}
					Hand subHand = new Hand(SubCards); // Creating new subHand
					SubHands.add(subHand);
				}
			} else {
				SubHands.add(h); // If no wilds/Jokers, subHands is an ArrayList
									// of 1 element, the original Hand
			}
		}
		return SubHands;
	}

	
	public static Hand EvalHand(Hand h) {
		ArrayList<Hand> EvalHands = ExplodeHands(h);

		for (Hand EvalHand : EvalHands) {
			EvalHand.EvalHand();
		}

		Collections.sort(EvalHands, Hand.HandRank);

		return EvalHands.get(0);

	}
	
	
	private void EvalHand() {
		// Evaluates if the hand is a flush and/or straight then figures out
		// the hand's strength attributes

		ArrayList<CardDomainModel> remainingCards = new ArrayList<CardDomainModel>();

		// Sort the cards!
		Collections.sort(getCardsInHand(), CardDomainModel.CardRank);

		Collections.sort(getCards(), CardDomainModel.CardRank);
		
		// Ace Evaluation
		if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == eRank.ACE) {
			setAce(true);
		}

		// Flush Evaluation
		if (getCards().get(eCardNo.FirstCard.getCardNo()).getSuit() == getCards().get(eCardNo.SecondCard.getCardNo())
				.getSuit()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getSuit() == getCards()
						.get(eCardNo.ThirdCard.getCardNo()).getSuit()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getSuit() == getCards()
						.get(eCardNo.FourthCard.getCardNo()).getSuit()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getSuit() == getCards()
						.get(eCardNo.FifthCard.getCardNo()).getSuit()) {
			setFlush(true);
		} else {
			setFlush(false);
		}

		// Straight Evaluation
		if (isAce()) {
			// Looks for Ace, King, Queen, Jack, 10
			if (getCards().get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.KING
					&& getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.QUEEN
					&& getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == eRank.JACK
					&& getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN) {
				setStraight(true);
				// Looks for Ace, 2, 3, 4, 5
			} else if (getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TWO
					&& getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == eRank.THREE
					&& getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.FOUR
					&& getCards().get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.FIVE) {
				setStraight(true);
			} else {
				setStraight(false);
			}
			// Looks for straight without Ace
		} else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank()
				.getRank() == getCards().get(eCardNo.SecondCard.getCardNo()).getRank().getRank() + 1
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == getCards().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank() + 2
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == getCards().get(eCardNo.FourthCard.getCardNo()).getRank().getRank() + 3
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank()
						.getRank() == getCards().get(eCardNo.FifthCard.getCardNo()).getRank().getRank() + 4) {
			setStraight(true);
		} else {
			setStraight(false);
		}

		// Evaluates the hand type
		if (isStraight() == true && isFlush() == true
				&& getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN && isAce()) {
			ScoreHand(eHandStrength.RoyalFlush, 0, 0, null);
		}

		// Straight Flush
		else if (isStraight() == true && isFlush() == true) {
			remainingCards = null;
			ScoreHand(eHandStrength.StraightFlush, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// five of a Kind

		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FiveOfAKind, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// Four of a Kind

		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FourthCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.FourOfAKind, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		else if (getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FifthCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FourthCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			ScoreHand(eHandStrength.FourOfAKind, getCards().get(eCardNo.FifthCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// Full House
		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FullHouse, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(),
					getCards().get(eCardNo.FourthCard.getCardNo()).getRank().getRank(), remainingCards);
		}

		else if (getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FifthCard.getCardNo()).getRank()
				&& getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.SecondCard.getCardNo()).getRank()) {
			remainingCards = null;
			ScoreHand(eHandStrength.FullHouse, getCards().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(),
					getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), remainingCards);
		}

		// Flush
		else if (isFlush()) {
			remainingCards = null;
			ScoreHand(eHandStrength.Flush, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// Straight
		else if (isStraight()) {
			remainingCards = null;
			ScoreHand(eHandStrength.Straight, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// Three of a Kind
		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.ThirdCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.ThreeOfAKind, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		else if (getCards().get(eCardNo.SecondCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));

			ScoreHand(eHandStrength.ThreeOfAKind, getCards().get(eCardNo.SecondCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		} else if (getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.SecondCard.getCardNo()));
			ScoreHand(eHandStrength.ThreeOfAKind, getCards().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		// Two Pair
		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FourthCard.getCardNo()).getRank())) {

			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));

			ScoreHand(eHandStrength.TwoPair, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(),
					getCards().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), remainingCards);
		} else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FifthCard.getCardNo()).getRank())) {

			remainingCards.add((Card) getCards().get(eCardNo.ThirdCard.getCardNo()));

			ScoreHand(eHandStrength.TwoPair, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(),
					getCards().get(eCardNo.FourthCard.getCardNo()).getRank().getRank(), remainingCards);
		} else if (getCards().get(eCardNo.SecondCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& (getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == getCards()
						.get(eCardNo.FifthCard.getCardNo()).getRank())) {

			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			ScoreHand(eHandStrength.TwoPair, getCards().get(eCardNo.SecondCard.getCardNo()).getRank().getRank(),
					getCards().get(eCardNo.FourthCard.getCardNo()).getRank().getRank(), remainingCards);
		}

		// Pair
		else if (getCards().get(eCardNo.FirstCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.SecondCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.ThirdCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.Pair, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		} else if (getCards().get(eCardNo.SecondCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));
			ScoreHand(eHandStrength.Pair, getCards().get(eCardNo.SecondCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		} else if (getCards().get(eCardNo.ThirdCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FourthCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));

			ScoreHand(eHandStrength.Pair, getCards().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		} else if (getCards().get(eCardNo.FourthCard.getCardNo()).getRank() == getCards()
				.get(eCardNo.FifthCard.getCardNo()).getRank()) {

			remainingCards.add((Card) getCards().get(eCardNo.FirstCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.ThirdCard.getCardNo()));

			ScoreHand(eHandStrength.Pair, getCards().get(eCardNo.FourthCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}

		else {
			remainingCards.add((Card) getCards().get(eCardNo.SecondCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.ThirdCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FourthCard.getCardNo()));
			remainingCards.add((Card) getCards().get(eCardNo.FifthCard.getCardNo()));

			ScoreHand(eHandStrength.HighCard, getCards().get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0,
					remainingCards);
		}
	}

	private void ScoreHand(eHandStrength hST, int HiHand, int LoHand, ArrayList<CardDomainModel> kickers) {
		this.setHandStrength(hST.getHandStrength());
		this.setHiHand(HiHand);
		this.setLoHand(LoHand);
		this.setKickers(kickers);
		this.setbScored(true);

	}

	/**
	 * Custom sort to figure the best hand in an array of hands
	 */
	
	public static Comparator<Hand> HandRank = new Comparator<Hand>() {

		public int compare(Hand h1, Hand h2) {

			int result = 0;

			result = h2.getHandStrength() - h1.getHandStrength();

			if (result != 0) {
				return result;
			}

			result = h2.getHiHand() - h1.getHiHand();
			if (result != 0) {
				return result;
			}

			result = h2.getLoHand() - h1.getLoHand();
			if (result != 0) {
				return result;
			}

			
			if ((h2.getKicker() == null) || (h1.getKicker() == null))
			{
				return 0;
			}
			

			try
			{
				if (h2.getKicker().size() >= eCardNo.FirstCard.getCardNo() +1 )
				{
					if (h1.getKicker().size() >= eCardNo.FirstCard.getCardNo() +1)
					{
						result = h2.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank();
					}
					if (result != 0)
					{
						return result;
					}
				}				
			}
			catch (Exception e)
			{				
				System.out.println(e.getMessage());
				throw new RuntimeException(e);
			}			

			
			try
			{
				if (h2.getKicker().size() >= eCardNo.SecondCard.getCardNo() +1 )
				{
					if (h1.getKicker().size() >= eCardNo.SecondCard.getCardNo() +1)
					{
						result = h2.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank();
					}
					if (result != 0)
					{
						return result;
					}
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				throw new RuntimeException(e);				
			}			
			
			try
			{
				if (h2.getKicker().size() >= eCardNo.ThirdCard.getCardNo() +1 )
				{
					if (h1.getKicker().size() >= eCardNo.ThirdCard.getCardNo() +1)
					{
						result = h2.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank();
					}
					if (result != 0)
					{
						return result;
					}
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				throw new RuntimeException(e);
			}
			
			try
			{
				if (h2.getKicker().size() >= eCardNo.FourthCard.getCardNo() +1 )
				{
					if (h1.getKicker().size() >= eCardNo.FourthCard.getCardNo() +1)
					{
						result = h2.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank() - h1.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank();
					}
					if (result != 0)
					{
						return result;
					}
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				throw new RuntimeException(e);				
			}
			
			return 0;
		}
	};
	
	/*
	public static Comparator<Hand> HandRank = new Comparator<Hand>() {

		public int compare(Hand h1, Hand h2) {

			int result = 0;

			result = h2.getHandStrength() - h1.getHandStrength();

			if (result != 0) {
				return result;
			}

			result = h2.getHiHand() - h1.getHiHand();
			if (result != 0) {
				return result;
			}

			result = h2.getLoHand() - h1.getLoHand();
			if (result != 0) {
				return result;
			}

			if (h2.getKicker().get(eCardNo.FirstCard.getCardNo()) != null) {
				if (h1.getKicker().get(eCardNo.FirstCard.getCardNo()) != null) {
					result = h2.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank()
							- h1.getKicker().get(eCardNo.FirstCard.getCardNo()).getRank().getRank();
				}
				if (result != 0) {
					return result;
				}
			}

			if (h2.getKicker().get(eCardNo.SecondCard.getCardNo()) != null) {
				if (h1.getKicker().get(eCardNo.SecondCard.getCardNo()) != null) {
					result = h2.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank()
							- h1.getKicker().get(eCardNo.SecondCard.getCardNo()).getRank().getRank();
				}
				if (result != 0) {
					return result;
				}
			}
			if (h2.getKicker().get(eCardNo.ThirdCard.getCardNo()) != null) {
				if (h1.getKicker().get(eCardNo.ThirdCard.getCardNo()) != null) {
					result = h2.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank()
							- h1.getKicker().get(eCardNo.ThirdCard.getCardNo()).getRank().getRank();
				}
				if (result != 0) {
					return result;
				}
			}

			if (h2.getKicker().get(eCardNo.FourthCard.getCardNo()) != null) {
				if (h1.getKicker().get(eCardNo.FourthCard.getCardNo()) != null) {
					result = h2.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank()
							- h1.getKicker().get(eCardNo.FourthCard.getCardNo()).getRank().getRank();
				}
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
	};
	*/
}
