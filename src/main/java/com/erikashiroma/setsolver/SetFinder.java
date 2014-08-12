package com.erikashiroma.setsolver;

import android.widget.TextView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Created by Erika on 8/10/2014.
 */
public class SetFinder {
    public Map<String, Integer> drawables;   // maps a card to it's drawable
    private TextView consoleView;
    private Stack<SetCard> currentCards;        // by this I mean all cards currenty in play...
    private Set<Set<SetCard>> result;
    private Stack<SetCard> discard;
    // GAME OPTIONS
    public boolean reviewDiscard;
    public boolean reviewCurrent;
    public boolean autoNextTurn;

    public static final int SET_SIZE  = 3;  // must be greater than 1
    public static final int POSSIBLE_CARDS = 12; // must be greater than cards per set  RENAME THIS??
    public static final int TOTAL_CARDS 	= 81; // 3xcount, 3xcolor, 3xshade, 3xshape
    public Scanner console;

    // Creates a new SetFinder object (default: reviews current, but not discard)
    // Default settings: review current cards (but not discard pile); ask to go on to another turn.
    public SetFinder(TextView consoleView, Map<String, Integer> drawables) { this(consoleView, drawables, true, false, false);}
    public SetFinder(TextView consoleView, Map<String, Integer> drawables, boolean reviewCurrent, boolean reviewDiscard, boolean autoNextTurn) {
        this.drawables = drawables;
        this.consoleView = consoleView;
        currentCards = new Stack<SetCard>();
        result = new HashSet<Set<SetCard>>();
        discard = new Stack<SetCard>();
        this.reviewCurrent = reviewCurrent;
        this.reviewDiscard = reviewDiscard;
        this.autoNextTurn = autoNextTurn;
        console = new Scanner(System.in);
    }

    // PUBLIC CLIENT STUFF
    public int setSize() 	 { return SET_SIZE;}
    public int possibleCards()  { return POSSIBLE_CARDS;}
    public int totalCards()  	 { return TOTAL_CARDS;}
    public int currentCount()	 { return currentCards.size();}
    public int quantityPlayed() { return currentCards.size() + discard.size();}
    public int resultSize()		 { return result.size();}
    // uh....yeah do this for now
    public Stack<SetCard> getCurrentCards() { return currentCards; }
    public Stack<SetCard> getDiscardPile() { return discard; }
    public Set<Set<SetCard>> getMatches() { return result; }

    public void printCurrent() {
        System.out.println("\nAll " + currentCards.size() + " current possible cards are: ");
        printStackLines(currentCards);
    }
    public void printResults() {
        if (result.size() == 0) { System.out.println("\nThere are no current possible sets");
        } else if (result.size() == 1) { System.out.println("\nThe only possible set is:");
        } else {	System.out.println("\nThe " + result.size() + " possible sets are:");}
        printSets(result);
    }
    public void printDiscard() {
        System.out.println("\nThe " + discard.size() + " cards that have already been played and removed are:");
        printStackLines(discard);
    }

    /**
     * Adds the given set of cards to the current in-play card stack.
     * (we need this because it's PUBLIC for the MainActivity to use)
     * @throws IllegalArgumentException if given set is null or does not have SET_SIZE cards
     */
    public void addCards(Set<SetCard> toAdd) {
        if (toAdd == null || toAdd.size() != SET_SIZE) {
            throw new IllegalArgumentException("Invalid set of cards to remove");
        }
        for (SetCard card : toAdd) {
            currentCards.add(card);
        }
    }

    /**
     * Removes the given set of cards from the current in-play card stack.
     * (we need this because it's PUBLIC for the MainActivity to use)
     * @throws IllegalArgumentException if given set is null or does not have SET_SIZE cards
     */
    public void removeCards(Set<SetCard> toRemove) {
        if (toRemove == null || toRemove.size() != SET_SIZE) {
            throw new IllegalArgumentException("Invalid set of cards to remove");
        }
        for (SetCard card : toRemove) {
            currentCards.remove(card);
            discard.add(card);
        }
    }
    // plays a turn with the user (addCards must have been called first)
    /*public void playNextTurn() {
        findMatches();						// check to see if a set can be formed (alter result stack)
        if (!result.isEmpty()) {		// set/s formed: done; call choose/remove method and exit
            printResults();
            removeCards(null);  // TODO: fix
        } else if (currentCards.size() >= POSSIBLE_CARDS) {	// no sets possible: add more cards and recurse
            // shouldn't recurse off the deep end (ends recursive adding when the normal amount of cards has a set)
            System.out.println("\nThere are no possible sets with those cards!");
            System.out.println("So for this turn, we'll add in an extra " + SET_SIZE + " cards.");
            addCards(SET_SIZE);
            playNextTurn();		// recurse again; keeps adding cards until a set can be formed
            //System.out.println("(now we will go back to playing with " + currentCards.size() + " cards)");
        }
    }*/


    // also may call addImpossibleCards() and addTestCards() and giveIntro();
    /////////////////////////////////////////////////////////////////////////////////

    // PRIVATE STUFF

    // adds a new card to the collection of all cards using user data via the passed console
    // throws and IllegalArgumentException if the user gives an invalid property, or
    // if the card has already been entered before
    /*private void addNewCard() {
        System.out.print("    count: ");
        String count = console.nextLine();
        System.out.print("    color: ");
        String color = console.nextLine().trim().toLowerCase();
        System.out.print("    shade: ");
        String shade = console.nextLine().trim().toLowerCase();
        System.out.print("    shape: ");
        String shape = console.nextLine().trim().toLowerCase();

        SetCard newCard = new SetCard(count, color, shade, shape);
        if (stackContainsCard(currentCards, newCard) || stackContainsCard(discard, newCard)) {
            throw new IllegalArgumentException("Sorry, but that card has already been played. (no duplicates)");
        }
        currentCards.push(newCard);
    }*/


    /**
     * Finds and returns a set of all possible sets of cards
     * @return Set of Sets of Cards
     */
    public Set<Set<SetCard>> findMatches() {
        result.clear();		// get rid of old sets that are no longer applicable
        findMatches(new Stack<SetCard>());
        return result;      // maybe create a copy instead....ugh
    }

    private void findMatches(Stack<SetCard> chosen) {
        if (chosen.size() == SET_SIZE) {
            if (isSet(chosen)) {
                result.add(new TreeSet<SetCard>(chosen));
            }
        } else if (chosen.size() <= SET_SIZE && !currentCards.isEmpty()) {
            SetCard card = currentCards.pop();	// choose
            findMatches(chosen);					// recurse without the card
            chosen.push(card);
            findMatches(chosen);					// recurse with the card
            chosen.pop();
            currentCards.push(card);					// un-choose
        }
    }

    /**
     * Determines if the given set of cards is a valid set.
     * @param set of cards to check
     * @return true if the set is a valid set of cards
     */
    private boolean isSet(Stack<SetCard> set) {
        Set<SetCard.Count> count = new HashSet<SetCard.Count>();
        for (SetCard card : set) count.add(card.getCount());
        if (count.size() != 1 && count.size() != set.size()) return false;
        Set<SetCard.Shape> shape = new HashSet<SetCard.Shape>();
        for (SetCard card : set) shape.add(card.getShape());
        if (shape.size() != 1 && shape.size() != set.size()) return false;
        Set<SetCard.Shade> shade = new HashSet<SetCard.Shade>();
        for (SetCard card : set) shade.add(card.getShade());
        if (shade.size() != 1 && shade.size() != set.size()) return false;
        Set<SetCard.Color> color = new HashSet<SetCard.Color>();
        for (SetCard card : set) color.add(card.getColor());
        return color.size() == 1 || color.size() == set.size();
    }

    // prints each element in the given collection/stack on a separate line
    private void printSets(Set<Set<SetCard>> all) {
        for (Set<SetCard> set : all)
            System.out.println(set);
        System.out.println();
    }

    private void printStackLines(Stack<SetCard> stack) {
        Collections.sort(stack);
        for (SetCard card : stack)
            System.out.println(card);
        System.out.println();
    }

    // returns true if the given stack already contains a card with
    // the same properties as the given SetCard
    private boolean stackContainsCard(Stack<SetCard> stack, SetCard card) {
        for (SetCard temp : stack) {
            if (temp.equals(card))
                return true;
        }
        return false;
    }

    // returns true if the two sets have the same cards in them
    private boolean setsEqual(Stack<SetCard> set1, Stack<SetCard> set2) {
        if (set1.size() != set2.size()) return false;
        for (SetCard card : set1) {
            if (!set2.contains(card))
                return false;
        }
        return true;
    }
    // asks the user which set they chose and removes those cards from the pool of possible cards

    // adds 12 cards to the pile of all possibles (no possible set)
    // clears the set first if passed true
    public void addImpossibleCards() { addImpossibleCards(false, true);}
    public void addImpossibleCards(boolean clearFirst, boolean printOut) {
        if (clearFirst) { currentCards.clear(); }
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.PURPLE, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.PURPLE, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.PURPLE, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.PURPLE, SetCard.Shade.OUTLINED, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.PURPLE, SetCard.Shade.OUTLINED, SetCard.Shape.SQUIGGLE));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.RED, SetCard.Shade.STRIPED, SetCard.Shape.SQUIGGLE));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.RED, SetCard.Shade.STRIPED, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.RED, SetCard.Shade.OUTLINED, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.RED, SetCard.Shade.OUTLINED, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.GREEN, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.GREEN, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        System.out.println("The following arrangement of cards has no sets!");
        printStackLines(currentCards);
    }
    // adds 12 cards to the pile of all possibles (has possible sets)
    // clears the set first if passed true
    public void addTestCards() { addTestCards(false, true);}
    public void addTestCards(boolean clearFirst, boolean printOut) {
        if (clearFirst) {
            currentCards.clear();
        }
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.OVAL));
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.RED, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.GREEN, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.PURPLE, SetCard.Shade.SOLID, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.PURPLE, SetCard.Shade.OUTLINED, SetCard.Shape.DIAMOND));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.SQUIGGLE));
        currentCards.add(new SetCard(SetCard.Count.ONE, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.SQUIGGLE));
        currentCards.add(new SetCard(SetCard.Count.TWO, SetCard.Color.GREEN, SetCard.Shade.STRIPED, SetCard.Shape.SQUIGGLE));
        currentCards.add(new SetCard(SetCard.Count.THREE, SetCard.Color.GREEN, SetCard.Shade.SOLID, SetCard.Shape.SQUIGGLE));
//        System.out.println("The following pre-set arrangement has been added!");
//        printStackLines(currentCards);
    }
}
