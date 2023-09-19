package com.example.firebasetest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button mRegisterBtn, mLoginBtn, mwifi, mchangelan;
 String URL_TO_LOAD = "http://192.168.1.13";
    private static final String APP_PACKAGE = "com.gopro.smarty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mLoginBtn = findViewById(R.id.login_btn);
        mRegisterBtn = findViewById(R.id.register_btn);
        mwifi = findViewById(R.id.wifi_btn);




        mwifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                openAppOrPlayStore(MainActivity.this);





            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
    private void openAppOrPlayStore(Context context) {
        boolean isAppInstalled = isAppInstalled(context, APP_PACKAGE);

        if (isAppInstalled) {
            // App is installed, open it
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE);
            startActivity(launchIntent);
        } else {
            // App is not installed, open Play Store
            openPlayStore(context);
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openPlayStore(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Play Store app is not available, open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PACKAGE));
            startActivity(intent);
        }
    }
}