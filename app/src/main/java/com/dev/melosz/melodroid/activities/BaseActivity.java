package com.dev.melosz.melodroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.dev.melosz.melodroid.R;

/**
 * Created on 09/26/2015 for the purpose of developing a general miscellaneous app with various
 * Activities to include database functionality, graphics, games, tools and utilities. The purpose
 * of this app is to get a presence on the Google Play Store.
 */
public class BaseActivity extends AppCompatActivity {

    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mLayout = (LinearLayout) findViewById(R.id.base_activity_layout);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        System.out.println("Height: [" + mLayout.getHeight() + "] Width: [" + mLayout.getWidth() + "]");
        int height = mLayout.getHeight();

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
            height -= getResources().getDimensionPixelSize(resourceId);
        System.out.println("Height minus menu bar: " + height);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
