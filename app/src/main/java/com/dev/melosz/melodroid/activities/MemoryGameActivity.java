package com.dev.melosz.melodroid.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.classes.AppUser;
import com.dev.melosz.melodroid.database.UserDAO;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;
import com.dev.melosz.melodroid.views.CardLayout;

import java.util.Random;

/**
 * Created by Marek Kozina 09/28/2015
 * Activity which runs a simple 6x4 or 8x3 Memory game depending on orientation.  Plans for future
 * implementation are polishing the logo "frontCard" image so it's not so bland, new game with
 * different card count (4x4, 8x6, 12x4, etc.), and new graphic icons in which are selected randomly
 * depending on the card count.
 *
 */
public class MemoryGameActivity extends Activity implements
        ViewTreeObserver.OnGlobalLayoutListener,
        CardLayout.OnCardTouchListener, PopupMenu.OnMenuItemClickListener{
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = MemoryGameActivity.class.getSimpleName();
    private static final boolean DEBUG = true;

    // Helper utility class
    AppUtil appUtil = new AppUtil();

    // Helper handler for UI interaction delays
    private Handler handler = new Handler();

    // Array to hold the CardLayouts
    private CardLayout[] cards;

    // The game GridLayout
    private GridLayout mGridLayout;

    // Variables which will draw the columns and rows
    private int numColumns;
    private int numRows;
    private int numCards;

    // holder cards to check matching and keep track of currently selected card
    private CardLayout currentCard;
    private CardLayout cardToMatch;

    // Cases to check if the deck needs to be initially built or to simply redraw with rebuild True
    private boolean cardsBuilt = false;
    private boolean rebuild = false;

    // This int ensures only 2 cards MAX are selected at a time
    private int accumulator;

    // Activity and score variables
    private Context CTX;
    private UserDAO uDAO;
    private SharedPreferences prefs;
    private AppUser mUser;
    private int mHighScore;
    private int score;

    // High & Current Score TextViews
    private TextView mAppHighScoreTV;
    private TextView mCurrentScoreTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        // Get the Activity Context and SharedPreferences
        CTX = this;
        prefs = CTX.getSharedPreferences(getString(R.string.preference_file_key),
                                         Context.MODE_PRIVATE);
        uDAO = new UserDAO(CTX);
        uDAO.open();

        TextView titleTV = (TextView) findViewById(R.id.title_bar);
        appUtil.setTitleFont(getAssets(), titleTV);

        // Get the stored user & high score from the SharedPreferences
        String activeUser = prefs.getString(getString(R.string.preference_stored_user), null);
        mHighScore = prefs.getInt(getString(R.string.memory_high_score), 0);
        if(activeUser != null) {
            mUser = uDAO.getUserByName(activeUser);
            if(DEBUG) log.i(TAG, "Current user for this Game: [" + mUser.getUserName() +
                       "] with a High Score: [" + mUser.getScore() + "].");
        }

        // Get the GridLayout container and apply a global listener to obtain WxH
        mGridLayout = (GridLayout) findViewById(R.id.card_grid_container);
        mGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);

        // Build the game variables
        numColumns = mGridLayout.getColumnCount();
        numRows = mGridLayout.getRowCount();
        numCards = numColumns * numRows;
        accumulator = 0;
        score = 0;

        mAppHighScoreTV = (TextView) findViewById(R.id.app_high);
        mCurrentScoreTV = (TextView) findViewById(R.id.user_high);
        updateScore();

        ImageView optionsIV = (ImageView) findViewById(R.id.options_button);
        optionsIV.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                popupMenu(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_game, menu);
        return true;
    }

    /**
     * Custom menu to override the ActionBar menu
     * @param v View the optionsIV ImageView
     */
    public void popupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_memory_game, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.memory_new_game) {
            String message =
                    "Are you sure you want to start a new game? Any progress will be lost.";
            buildConfirmDialog(this, message);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the views are drawn/re-drawn
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onGlobalLayout(){
        if(!cardsBuilt) {
            buildCards();
        }

        // Must be called for the screens to render properly.  Handles deprecated pre-JellyBean
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            mGridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        else
            mGridLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * Called when the user changes screen orientation. Wipes the gridlayout and determines the new
     * column and row count depending on the orientation
     * @param newConfig Configuration the new screen configuration
     */
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        System.out.println("ORIENTATION CHANGE: " + newConfig);
        if(mGridLayout != null) {
            // tear down current cards for re-draw
            mGridLayout.removeAllViews();

            // determine column and row count depending on orientation and set them back
            mGridLayout.setColumnCount(
                    newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? 4 : 8);
            mGridLayout.setRowCount(
                    newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? 6 : 3);
            numColumns = mGridLayout.getColumnCount();
            numRows = mGridLayout.getRowCount();

            // Delay the rebuilding of the cards so the views can update.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rebuild = true;
                    buildCards();
                }
            }, 250);

        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * This method listens for the clicks of the cards from the CardLayout class
     * @param card CardLayout the wrapper which holds the front and back cards
     * @param selected whether or not this view has been toggled
     */
    @Override
    public void OnCardSelected(CardLayout card, boolean selected) {
            if(DEBUG) {
                int tag = (int) card.getBackCard().getTag();
                String rName = getResources().getResourceName(tag);
                //get the id string
                String idString = " \nCard ID: [" + card.getId() + "]\n" +
                        "Card Tag: [" + tag + "]\n" +
                        "BackCard ID: [" + card.getBackCard().getId() + "]\n" +
                        "Selected: [" + card.isSelected() + "]\n" +
                        "Matched: [" + card.isMatched() + "]\n" +
                        "Resource Name : [" + rName + "]";
                log.i(TAG, idString);
            }
            if(!card.isSelected() && !card.isMatched() && accumulator != 2) {
                // initial selection
                if (currentCard == null || accumulator == 0) {
                    currentCard = card;
                    flipCard(card, false);
                }
                // first time second card is chosen
                else if (cardToMatch == null || accumulator == 1) {
                    cardToMatch = currentCard;
                    currentCard = card;
                    flipCard(card, true);
                }
            }
    }

    private void updateScore() {
        String preAppHigh = getString(R.string.high_score_label);
        String preAppCurrent = getString(R.string.current_score_label);

        mAppHighScoreTV.setText(preAppHigh + " [" + mHighScore + "]");
        mCurrentScoreTV.setText(preAppCurrent + " [" +  score + "]");
    }
    /**
     * This method performs the animation when a card is clicked
     * @param card the CardLayout card
     */
    public void flipCard (CardLayout card, boolean check) {
        // New instances of AnimationSets so they don't run into each other
        AnimatorSet setL = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_left_out);
        AnimatorSet setR = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_right_in);

        final boolean runCheck = check;
        accumulator++;

        // Build and start the animation for the card flip
        setL.setTarget(card.getFrontCard());
        setR.setTarget(card.getBackCard());
        setL.start();
        setR.start();

        // Set selected flag on the current card
        card.setSelected(true);

        // Delay the next check until the flip has finished.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (runCheck) {
                    checkMatch();
                }
            }
        }, 500);
    }

    /**
     * Flips the selected cards back when they do not match
     * @param card1 CardLayout the currently selected card
     * @param card2 CardLayout the card that was match-checked
     */
    public void flipCardBack(CardLayout card1, CardLayout card2){
        // New instances of AnimationSets so they don't run into each other
        AnimatorSet setR1 = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_right_out);
        AnimatorSet setR2 = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_right_out);
        AnimatorSet setL1 = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_left_in);
        AnimatorSet setL2 = (AnimatorSet) AnimatorInflater.loadAnimator(
                this, R.animator.flip_left_in);

        // set the targets for the two flip animations
        setR1.setTarget(card1.getBackCard());
        setL1.setTarget(card1.getFrontCard());
        setR2.setTarget(card2.getBackCard());
        setL2.setTarget(card2.getFrontCard());

        // start the animations
        setR1.start();
        setL1.start();
        setR2.start();
        setL2.start();

        // reset the selected flag
        card1.setSelected(false);
        card2.setSelected(false);

        // This insures only 2 cards max are selected at a time
        if (accumulator == 2) {
            accumulator = 0;
        }
    }

    /**
     * Checks if the two selected cards are a match by the tag assigned in CardLayout
     */
    private void checkMatch() {
        String message;
        int[] matcher = new int[]{
                (int) currentCard.getBackCard().getTag(),
                (int) cardToMatch.getBackCard().getTag()
        };
        // Its a match, and check if the game is completed
        if(matcher[0] == matcher[1]){
            // Add 50 points for ever match
            score += 50;

            if(DEBUG) {
                message = "MATCH WITH TAGS: \ncurrentCard: [" + matcher[0] + "] \ncardToMatch: ["
                        + matcher[1]  + "] \nScore: [" + score + "]";
                log.i(TAG, message);
            }

            // set the match flag
            currentCard.setMatched(true);
            cardToMatch.setMatched(true);
            // This insures only 2 cards max are selected at a time
            if (accumulator == 2) {
                accumulator = 0;
            }

            // check if all cards have been matched
            gameComplete();
        }
        else {
            // Deduct 10 points from the score on every non-match
            score -= 10;

            if(DEBUG) {
                message = "NO MATCH WITH TAGS \ncurrentCard: [" + matcher[0] + "] \ncardToMatch: ["
                        + matcher[1] + "] \nScore: [" + score + "]";
                log.i(TAG, message);
            }

            // flip both cards back to their original state
            flipCardBack(currentCard, cardToMatch);
        }
        updateScore();
    }

    /**
     * Method to check if all cards have been matched. Called upon each positive match.
     */
    private void gameComplete(){
        boolean complete = false;

        // Check if any cards are not matched, and break the loop if so.
        for(CardLayout card : cards) {
            if(!card.isMatched()){
                complete = false;
                break;
            }
            else
                complete = true;
        }
        if(complete) {
            int userScore = mUser.getScore();
            String userName = mUser.getUserName();
            String oldHSName =
                    prefs.getString(getString(R.string.memory_user_high_score), "MeloDroid");
            String message;

            // New user high score
            if(score > userScore){
                message = userName + " You have beat your old High Score of ["
                        + userScore + "] with a new personal best High Score of ["
                        + score + "]! Congratulations!";

                // Update user best score
                mUser.setScore(score);
                uDAO.updateUser(mUser);

                // New app-wide high score
                if(score > mHighScore){
                    message = userName + " You have beat " + oldHSName + "'s High Score of ["
                            + mHighScore + "] with a new App High Score of ["
                            + score + "]! Congratulations!";

                    // Add score & userName to preferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(getString(R.string.memory_high_score), score);
                    editor.putString(getString(R.string.memory_user_high_score), userName);
                    editor.apply();

                    // Set the App High Score as current for new games
                    mHighScore = score;
                }
            }
            else {
                message =
                    "You have completed the game with a score of: [" + score + "] Congratulations!";
            }

            // Display the toast with the previously constructed message
            Toast.makeText(CTX, message, Toast.LENGTH_LONG).show();

            // Wait 3 seconds, then ask the user if they would like to start a new game
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String message = "Would you like to start a new game?";
                    buildConfirmDialog(MemoryGameActivity.this, message);
                }
            }, 3000);
        }
    }

    /**
     *
     */
    private void buildCards() {
        // obtain the WxH of the current view
        int gridHeight = mGridLayout.getHeight();
        int gridWidth = mGridLayout.getWidth();

        // get the WxH for each CardLayout so they are evenly spaced
        int cardWidth = gridWidth / numColumns;
        int cardHeight = gridHeight / numRows;

        CardLayout.LayoutParams params = new CardLayout.LayoutParams(cardWidth, cardHeight);

        // Only run on initialization
        if (!cardsBuilt) {
            cards = new CardLayout[numCards];
            for (int i = 0; i < cards.length; i++) {
                CardLayout newCard = new CardLayout(this, params);
                newCard.setOnCardTouchListener(this);
                cards[i] = newCard;
                mGridLayout.addView(newCard);
            }
            setBackCards(params);
            cardsBuilt = true;
        }
        // Run each time screen orientation is changed
        else if(rebuild) {
            for (CardLayout card : cards) {
                card.setLayoutParams(params);
                card.setCardDimensions(params);
                mGridLayout.addView(card);
            }
        }
    }

    /**
     * Sets the hidden backCards for each CardLayout.
     * @param params LayoutParams to set on the new backCard
     */
    public void setBackCards(CardLayout.LayoutParams params) {
        int[] imgVals = getImgVals();
        int i = 0;
        for(CardLayout card : cards) {
            ImageView newBackCard = new ImageView(this);
            newBackCard.setLayoutParams(params);
            card.setBackCard(newBackCard, imgVals[i]);
            i++;
        }
    }

    /**
     * Assigns images in an array and shuffles them
     * @return imgVals the shuffled array
     */
    public int[] getImgVals(){
        // Store all of the images. For now, only 24 supported.
        int[] imgVals = new int[]{
                R.mipmap.clock96, // Designed by Freepik
                R.mipmap.clock96, // Designed by Freepik
                R.mipmap.locked59, // Designed by Freepik
                R.mipmap.locked59, // Designed by Freepik
                R.mipmap.hotdrink2, // Designed by Freepik
                R.mipmap.hotdrink2, // Designed by Freepik
                R.mipmap.icon1,
                R.mipmap.icon1,
                R.mipmap.linkedin,
                R.mipmap.linkedin,
                R.mipmap.logo,
                R.mipmap.logo,
                R.mipmap.magnifier13, // Designed by Freepik
                R.mipmap.magnifier13, // Designed by Freepik
                R.mipmap.plan,
                R.mipmap.plan,
                R.mipmap.three115, // Designed by Freepik
                R.mipmap.three115, // Designed by Freepik
                R.mipmap.phone325, // Designed by Freepik
                R.mipmap.phone325, // Designed by Freepik
                R.mipmap.x_circle,
                R.mipmap.x_circle,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher
        };

        Random rand = new Random();

        // Shuffle the cards to randomize them
        for(int i = 0; i < imgVals.length; i++) {
            int randomPosition = rand.nextInt(imgVals.length);
            int temp = imgVals[i];
            imgVals[i] = imgVals[randomPosition];
            imgVals[randomPosition] = temp;
        }
        return imgVals;
    }

    /**
     * reset all globals and rebuild the cards
     */
    public void reset() {
        mGridLayout.removeAllViews();
        score = 0;
        accumulator = 0;
        currentCard = null;
        cardToMatch = null;
        cards = null;
        cardsBuilt = false;
        updateScore();
        buildCards();
    }

    /**
     * Builds a confirmation Dialog box with Yes or No as choices
     *
     * @param context the Activity Context
     * @param message String the message to display in the dialog
     */
    private void buildConfirmDialog(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        reset();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}