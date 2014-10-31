package com.base.dragon.main;

import android.graphics.Canvas;
import android.os.Handler;

public class GameThread extends Thread {
    public Handler handler = new Handler();
    static final long FPS = 30;
    private GameView view;
    private boolean running = false;

    public GameThread(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean running){
        this.running = running;
    }


    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }

            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}