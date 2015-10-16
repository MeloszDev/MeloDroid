package com.dev.melosz.melodroid.views;

import android.R.drawable;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dev.melosz.melodroid.R;
import com.dev.melosz.melodroid.utils.AppUtil;

/**
 * Created by marek.kozina on 9/28/2015.
 * Custom subclass of a Relative Layout which contains two image views to represent the front and
 * back cards for the memory game.
 *
 *    Date           Name                  Description of Changes
 * ---------   -------------    --------------------------------------------------------------------
 * 10 Oct 15   M. Kozina        1. Added header & updated resource image
 *
 */
public class CardLayout extends RelativeLayout {
    // Debugging controls
    private static final String TAG = CardLayout.class.getSimpleName();
    private static final boolean DEBUG = false;

    // Helper utility class
    private AppUtil appUtil = new AppUtil();

    // These will store the images and matching params to check in the memory game
    private ImageView frontCard;
    private ImageView backCard;

    public boolean matched;
    public boolean selected;

    private OnCardTouchListener cardTouchListener;

    /**
     * Generic constructor
     * @param context Context the Activity Context
     */
    public CardLayout (Context context){
        super(context);
    }
    /**
     * Constructor which also builds the WxH of the CardLayout
     * @param context Context the Activity Context
     * @param params LayoutParams the WxH
     */
    public CardLayout(Context context, CardLayout.LayoutParams params) {
        super(context);

        setLayoutParams(params);
        setId(appUtil.generateViewId());

        // Build the generic frontCard
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(params);
        iv.setId(appUtil.generateViewId());
        iv.setImageResource(R.mipmap.meto_card_test);
        iv.setBackgroundResource(drawable.dialog_holo_light_frame);
        setFrontCard(iv);

        addView(iv);
        selected = false;
        matched = false;
    }

    /**
     * Custom Listener to listen for touch events on each card
     */
    public interface OnCardTouchListener {
        void OnCardSelected(CardLayout card, boolean selected);
    }

    /**
     * Sets the dimensions of the CardLayouts Card Image Views
     * @param params LayoutParams the Width x Height
     */
    public void setCardDimensions(CardLayout.LayoutParams params) {
        frontCard.setLayoutParams(params);

        if(backCard != null)
            backCard.setLayoutParams(params);
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ImageView getFrontCard() {
        return frontCard;
    }

    public void setFrontCard(ImageView frontCard) {
        this.frontCard = frontCard;
    }

    public ImageView getBackCard() {
        return backCard;
    }

    public void setBackCard(ImageView backCard, int imgResource) {
        // initially make this invisible
        backCard.setAlpha(0f);
        backCard.setId(appUtil.generateViewId());
        backCard.setImageResource(imgResource);
        backCard.setBackgroundResource(drawable.dialog_holo_light_frame);
        backCard.setTag(imgResource);
        this.backCard = backCard;
        addView(this.backCard);
    }

    /**
     *
     * @param event MotionEvent the user's motion event UI interaction
     * @return boolean whether or not a specific event has occurred
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        super.onTouchEvent(event);
        if(DEBUG) Log.i(TAG, "Card id: [" + this.getId() + "] Selected.");

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //                invalidate();

                if(cardTouchListener != null)
                    cardTouchListener.OnCardSelected(this, selected);
                return true;
        }
        return false;
    }

    /**
     * Assigns the onTouchListener to this specific card
     * @param listener the OnCardTouchListener
     */
    public void setOnCardTouchListener(OnCardTouchListener listener){
        cardTouchListener = listener;
    }
}