package pokerBase;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.eHandStrength;
import enums.eRank;
import enums.eSuit;

public class Joker_Test {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void FiveOfAKind() {
		Deck d = new Deck();
		Hand h = new Hand();
		h.AddCardToHand(new Card(eSuit.CLUBS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.CLUBS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.CLUBS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.CLUBS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h = Hand.EvalHand(h);
		
		assertTrue(h.getHandStrength() == eHandStrength.FiveOfAKind.getHandStrength());
		
	}
	
	@Test
	public void RoyalFlush() {
		Deck d = new Deck();
		Hand h = new Hand();
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.JACK,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.QUEEN,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h = Hand.EvalHand(h);
		
		assertTrue(h.getHandStrength() == eHandStrength.RoyalFlush.getHandStrength());
		
	}
	
	@Test
	public void RoyalFlush2() {
		Deck d = new Deck();
		Hand h = new Hand();
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.QUEEN,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h = Hand.EvalHand(h);
		
		assertTrue(h.getHandStrength() == eHandStrength.RoyalFlush.getHandStrength());
		
	}
	
	@Test
	public void FourOfAKind() {
		Deck d = new Deck();
		Hand h = new Hand();
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.FIVE,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h = Hand.EvalHand(h);
		
		assertTrue(h.getHandStrength() == eHandStrength.FourOfAKind.getHandStrength());
		
	}
	
	public void FourOfAKind2() {
		Deck d = new Deck();
		Hand h = new Hand();
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TWO,true,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.TEN,0));
		h.AddCardToHand(new Card(eSuit.HEARTS,eRank.FIVE,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h.AddCardToHand(new Card(eSuit.JOKER,eRank.JOKER,0));
		h = Hand.EvalHand(h);
		
		assertTrue(h.getHandStrength() == eHandStrength.FourOfAKind.getHandStrength());
		
	}
}
