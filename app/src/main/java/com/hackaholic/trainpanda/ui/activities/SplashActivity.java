package com.hackaholic.trainpanda.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.hackaholic.trainpanda.R;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        sharedPreferences = getSharedPreferences("TrainPanda",MODE_PRIVATE);

      new CountDownTimer(2500,1000){

          @Override
          public void onTick(long l) {

          }

          @Override
          public void onFinish() {


              String cust_id = sharedPreferences.getString("customer_id", "").trim();


              if(cust_id.equalsIgnoreCase("")){
                  Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  startActivity(intent);
                  finish();
              }else{
                  Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  startActivity(intent);
                  finish();
              }



          }
      }.start();


/*    Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);
        */
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
