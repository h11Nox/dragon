package com.base.dragon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.base.dragon.R;

public class BaseActivity extends GameBaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void onAbout(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("Dragon")
                .setMessage("The game was created by Sergiy Radykhovsky.  If you have any suggestions you can email to radykhovsky@gmail.com")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
