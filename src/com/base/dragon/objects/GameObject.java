package com.base.dragon.objects;

import android.graphics.Canvas;
import com.base.dragon.main.GameView;

/**
 * The base class of game objects
 */
public abstract class GameObject {

    public String id;

    /**
     * Position
     */
    protected int x, y;

    /**
     * GameView
     */
    protected GameView view;

    protected Canvas canvas;

    /**
     * If the object is Clickable
     */
    protected boolean isClickable;

    /*public GameObject(GameView view){
        this.view = view;
        init();
    }*/

    public void init(GameView view, Canvas canvas, int x, int y){
        this.view = view;
        this.canvas = canvas;
        this.x = x;
        this.y = y;
    }

    /**
     * Set Canvas
     * @param canvas
     */
    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
    }

    abstract void draw();
}
