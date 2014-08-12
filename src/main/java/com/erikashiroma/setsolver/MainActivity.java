package com.erikashiroma.setsolver;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity {
    private static final int COLUMNS = 3;
    private static final int ROWS = 4;
    private SetFinder mFinder;
    private Map<String, Integer> mDrawables;    // maps the toString to a Drawable image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpData();
    }

    public void setUpData() {
        mDrawables = initializeMap();
        mFinder = new SetFinder((TextView) findViewById(R.id.console), mDrawables, false, false, true);

        ((TextView) findViewById(R.id.console)).setText(R.string.intro_instructions);
        mFinder.addTestCards();
        onRefreshCardViewClick(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefreshCardViewClick(View unused) {
        // find view manually later if needed, just use the passed one for now
        ViewGroup cardWrapper = (ViewGroup) findViewById(R.id.card_container);
        drawCardGrid(cardWrapper, mFinder.getCurrentCards());
    }

    public void onFindMatchesClick(View unused) {
//        Set<Set<SetCard>> matches = mFinder.findMatches();
        ViewGroup matchesWrapper = (ViewGroup) findViewById(R.id.matches_container);
        drawCardSets(matchesWrapper, mFinder.findMatches());
        matchesWrapper.setVisibility(View.VISIBLE);
    }

    /**
     * Draws the current set of cards in the standard grid size.
     * (for a set of X cards to be split into rows of COLUMN cards)
     * @param wrapper to contain the grid
     */
    public void drawCardGrid(ViewGroup wrapper, Collection<SetCard> set) {
        wrapper.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup cardRow = (ViewGroup) inflater.inflate(R.layout.card_row, wrapper, false);
        int i = 1;
        for (SetCard card : set) {
            ImageView image = (ImageView) inflater.inflate(R.layout.card, cardRow, false);
            image.setImageResource(mDrawables.get(card.toString()));
            cardRow.addView(image);
            if ((i % COLUMNS) == 0) {
                wrapper.addView(cardRow);
                cardRow = (ViewGroup) inflater.inflate(R.layout.card_row, wrapper, false); // next row
            }
            i++;
            System.out.println(i);
        }
        if ((i % COLUMNS) == 1) {
            wrapper.addView(cardRow);
        }
    }

    /**
     * Draws each set of cards in the set as a row, creating a grid.
     * (for a set of set of cards, each set to be its own row)
     * @param wrapper to contain the grid
     */
    public void drawCardSets(ViewGroup wrapper, Set<Set<SetCard>> setOfSets) {
        wrapper.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup cardRow = (ViewGroup) inflater.inflate(R.layout.card_row, wrapper, false);
        for (Set<SetCard> set : setOfSets) {
            for (SetCard card : set) {
                ImageView image = (ImageView) inflater.inflate(R.layout.card, cardRow, false);
                image.setImageResource(mDrawables.get(card.toString()));


                cardRow.addView(image);
            }

            // long clicking on this set will remove it
            final Set<SetCard> setToRemove = set;
            /*cardRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFinder.removeCards(setToRemove);
                    onRefreshCardViewClick(null);
                }
            });*/
            cardRow.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    mFinder.removeCards(setToRemove);
                    onRefreshCardViewClick(null);
                    drawCardSets((ViewGroup) findViewById(R.id.matches_container), mFinder.getMatches());
                    findViewById(R.id.matches_container).setVisibility(View.GONE);
                    return true;
                }
            });

            wrapper.addView(cardRow);
            cardRow = (ViewGroup) inflater.inflate(R.layout.card_row, wrapper, false);
        }
    }

    /**
     * Creates and returns the map between cards (toString) and their drawable ID
     * @return map between cards and drawable ID
     */
    public Map<String, Integer> initializeMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("ONE RED SOLID SQUIGGLE", R.drawable.c01);
        map.put("TWO RED SOLID SQUIGGLE", R.drawable.c02);
        map.put("THREE RED SOLID SQUIGGLE", R.drawable.c03);
        map.put("ONE PURPLE SOLID SQUIGGLE", R.drawable.c04);
        map.put("TWO PURPLE SOLID SQUIGGLE", R.drawable.c05);
        map.put("THREE PURPLE SOLID SQUIGGLE", R.drawable.c06);
        map.put("ONE GREEN SOLID SQUIGGLE", R.drawable.c07);
        map.put("TWO GREEN SOLID SQUIGGLE", R.drawable.c08);
        map.put("THREE GREEN SOLID SQUIGGLE", R.drawable.c09);
        map.put("ONE RED SOLID DIAMOND", R.drawable.c10);
        map.put("TWO RED SOLID DIAMOND", R.drawable.c11);
        map.put("THREE RED SOLID DIAMOND", R.drawable.c12);
        map.put("ONE PURPLE SOLID DIAMOND", R.drawable.c13);
        map.put("TWO PURPLE SOLID DIAMOND", R.drawable.c14);
        map.put("THREE PURPLE SOLID DIAMOND", R.drawable.c15);
        map.put("ONE GREEN SOLID DIAMOND", R.drawable.c16);
        map.put("TWO GREEN SOLID DIAMOND", R.drawable.c17);
        map.put("THREE GREEN SOLID DIAMOND", R.drawable.c18);
        map.put("ONE RED SOLID OVAL", R.drawable.c19);
        map.put("TWO RED SOLID OVAL", R.drawable.c20);
        map.put("THREE RED SOLID OVAL", R.drawable.c21);
        map.put("ONE PURPLE SOLID OVAL", R.drawable.c22);
        map.put("TWO PURPLE SOLID OVAL", R.drawable.c23);
        map.put("THREE PURPLE SOLID OVAL", R.drawable.c24);
        map.put("ONE GREEN SOLID OVAL", R.drawable.c25);
        map.put("TWO GREEN SOLID OVAL", R.drawable.c26);
        map.put("THREE GREEN SOLID OVAL", R.drawable.c27);

        map.put("ONE RED STRIPED SQUIGGLE", R.drawable.c28);
        map.put("TWO RED STRIPED SQUIGGLE", R.drawable.c29);
        map.put("THREE RED STRIPED SQUIGGLE", R.drawable.c30);
        map.put("ONE PURPLE STRIPED SQUIGGLE", R.drawable.c31);
        map.put("TWO PURPLE STRIPED SQUIGGLE", R.drawable.c32);
        map.put("THREE PURPLE STRIPED SQUIGGLE", R.drawable.c33);
        map.put("ONE GREEN STRIPED SQUIGGLE", R.drawable.c34);
        map.put("TWO GREEN STRIPED SQUIGGLE", R.drawable.c35);
        map.put("THREE GREEN STRIPED SQUIGGLE", R.drawable.c36);
        map.put("ONE RED STRIPED DIAMOND", R.drawable.c37);
        map.put("TWO RED STRIPED DIAMOND", R.drawable.c38);
        map.put("THREE RED STRIPED DIAMOND", R.drawable.c39);
        map.put("ONE PURPLE STRIPED DIAMOND", R.drawable.c40);
        map.put("TWO PURPLE STRIPED DIAMOND", R.drawable.c41);
        map.put("THREE PURPLE STRIPED DIAMOND", R.drawable.c42);
        map.put("ONE GREEN STRIPED DIAMOND", R.drawable.c43);
        map.put("TWO GREEN STRIPED DIAMOND", R.drawable.c44);
        map.put("THREE GREEN STRIPED DIAMOND", R.drawable.c45);
        map.put("ONE RED STRIPED OVAL", R.drawable.c46);
        map.put("TWO RED STRIPED OVAL", R.drawable.c47);
        map.put("THREE RED STRIPED OVAL", R.drawable.c48);
        map.put("ONE PURPLE STRIPED OVAL", R.drawable.c49);
        map.put("TWO PURPLE STRIPED OVAL", R.drawable.c50);
        map.put("THREE PURPLE STRIPED OVAL", R.drawable.c51);
        map.put("ONE GREEN STRIPED OVAL", R.drawable.c52);
        map.put("TWO GREEN STRIPED OVAL", R.drawable.c53);
        map.put("THREE GREEN STRIPED OVAL", R.drawable.c54);

        map.put("ONE RED OUTLINED SQUIGGLE", R.drawable.c55);
        map.put("TWO RED OUTLINED SQUIGGLE", R.drawable.c56);
        map.put("THREE RED OUTLINED SQUIGGLE", R.drawable.c57);
        map.put("ONE PURPLE OUTLINED SQUIGGLE", R.drawable.c58);
        map.put("TWO PURPLE OUTLINED SQUIGGLE", R.drawable.c59);
        map.put("THREE PURPLE OUTLINED SQUIGGLE", R.drawable.c60);
        map.put("ONE GREEN OUTLINED SQUIGGLE", R.drawable.c61);
        map.put("TWO GREEN OUTLINED SQUIGGLE", R.drawable.c62);
        map.put("THREE GREEN OUTLINED SQUIGGLE", R.drawable.c63);
        map.put("ONE RED OUTLINED DIAMOND", R.drawable.c64);
        map.put("TWO RED OUTLINED DIAMOND", R.drawable.c65);
        map.put("THREE RED OUTLINED DIAMOND", R.drawable.c66);
        map.put("ONE PURPLE OUTLINED DIAMOND", R.drawable.c67);
        map.put("TWO PURPLE OUTLINED DIAMOND", R.drawable.c68);
        map.put("THREE PURPLE OUTLINED DIAMOND", R.drawable.c69);
        map.put("ONE GREEN OUTLINED DIAMOND", R.drawable.c70);
        map.put("TWO GREEN OUTLINED DIAMOND", R.drawable.c71);
        map.put("THREE GREEN OUTLINED DIAMOND", R.drawable.c72);
        map.put("ONE RED OUTLINED OVAL", R.drawable.c73);
        map.put("TWO RED OUTLINED OVAL", R.drawable.c74);
        map.put("THREE RED OUTLINED OVAL", R.drawable.c75);
        map.put("ONE PURPLE OUTLINED OVAL", R.drawable.c76);
        map.put("TWO PURPLE OUTLINED OVAL", R.drawable.c77);
        map.put("THREE PURPLE OUTLINED OVAL", R.drawable.c78);
        map.put("ONE GREEN OUTLINED OVAL", R.drawable.c79);
        map.put("TWO GREEN OUTLINED OVAL", R.drawable.c80);
        map.put("THREE GREEN OUTLINED OVAL", R.drawable.c81);

        return map;
    }
}
