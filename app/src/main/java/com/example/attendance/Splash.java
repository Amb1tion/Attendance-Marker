package com.example.attendance;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.view.Menu;

public class Splash extends Activity {
    private static final String TAG = "SPLASH";
    private final int  SPLASH_DISPLAY_LENGTH=1000;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.attendance";
    Intent mainIntent;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                mPreferences=Splash.this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
                boolean hasSections = mPreferences.contains("Sections");
                if (hasSections){mainIntent = new Intent(Splash.this, course_activity.class);}
                else{mainIntent = new Intent(Splash.this, Activity2.class);}
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
