package com.dev.melosz.melodroid.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by marek.kozina on 10/13/2015.
 * Custom DepthPageTransformer so the login/registration fragments are side-by-side but rather
 * stacked on top of eachother.
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {

    /**
     * Transforms the login/registration fragments so they slide in or out as if they were
     * stacked on top of each other to begin with, with a gradual fade.  The visibility needs
     * to be explicitly changed from View.VISIBLE to View.GONE (or vice versa) or the UI
     * elements from the first page will be visible, but the interaction will occur on the page
     * behind it due to the alpha being set to 0.
     *
     * @param view View the fragment (page) to transform to/from
     * @param position float the position of the page relative to the viewport
     */
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
            view.setVisibility(View.GONE);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);

            if (view.getVisibility()!=View.VISIBLE)
                view.setVisibility(View.VISIBLE);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1 - position);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            //
            if (position==1) {
                view.setVisibility(View.GONE);
            } else {
                if (view.getVisibility()!=View.VISIBLE)
                    view.setVisibility(View.VISIBLE);
            }

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
            view.setVisibility(View.GONE);
        }
    }
}