package com.base.dragon.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import com.base.dragon.R;

public class BaseActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
    }

    public void onPlay(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onRules(View view){
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    public void onAbout(View view){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onAppExit(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you wanna exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
