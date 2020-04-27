package com.biocoda.miov10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = new Intent(this, LoginActivity.class);
        final Handler splashHandler = new Handler();

        splashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // code to run when timer expires
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}



