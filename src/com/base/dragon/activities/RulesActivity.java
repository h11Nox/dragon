package com.base.dragon.activities;

import android.os.Bundle;
import android.view.Window;
import com.base.dragon.R;

public class RulesActivity extends GameBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rules_layout);
    }
}