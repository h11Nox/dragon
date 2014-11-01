package com.base.dragon.activities;

import android.os.Bundle;
import android.view.Window;
import com.base.dragon.main.GameView;
import com.base.dragon.resourses.AndroidFileIO;
import com.base.dragon.resourses.Assets;
import com.base.dragon.resourses.Settings;

import java.util.Map;

public class GameActivity extends GameBaseActivity {

    GameView gameView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameView = new GameView(this);
        setContentView(gameView);

        Settings.load(new AndroidFileIO(getApplicationContext()));
        Assets.load(getAssets());
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Map<String, Integer> data = gameView.getCurrentData();
        savedInstanceState.putInt("game_level", data.get("level"));
        savedInstanceState.putInt("game_move", data.get("move"));
        savedInstanceState.putInt("game_moves", data.get("moves"));

        savedInstanceState.putIntArray("game_data", gameView.getUnits());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int level, move, moves;
        level = savedInstanceState.getInt("game_level");
        move = savedInstanceState.getInt("game_move");
        moves = savedInstanceState.getInt("game_moves");

        gameView.load(level, move, moves, savedInstanceState.getIntArray("game_data"));
        super.onRestoreInstanceState(savedInstanceState);
    }
}