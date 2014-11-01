package com.base.dragon.resourses;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

public class Assets {

    public static SoundPool soundPool;

    /** Sound Variables **/
    public static Sound swap;
    public static Sound click;
    public static Sound win;
    public static Sound wrong;

    protected static  AssetManager assets;

    public static void load(AssetManager assetsManager){
        assets = assetsManager;
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

        click = newSound("click.ogg");
        swap = newSound("swap.ogg");
        win = newSound("win.ogg");
        wrong = newSound("wrong.ogg");
    }

    public static Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new Sound(soundPool, soundId);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load sound '" + filename + "'");
        }
    }
}
