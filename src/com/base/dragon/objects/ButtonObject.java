package com.base.dragon.objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.base.dragon.R;
import com.base.dragon.main.GameView;

public class ButtonObject extends TextObject{

    protected boolean isClickable = true;
    protected Paint.Align align = Paint.Align.CENTER;
    protected int sizeX = 100, sizeY = 40, border = 1, endX, endY;
    protected boolean isRight = false;

    public ButtonObject(GameView view, Canvas canvas, int x, int y, String text){
        super(view, canvas, x, y, text);
        calculate();
    }

    /**
     * Calculate
     */
    protected void calculate(){
        int startX, startY, endX, endY;
        if(!isRight){
            this.endX = x + sizeX;
            this.endY = y + sizeY;
        }
        else{
            startX = x - sizeX;
            startY = y;
            endX = x;
            endY = y + sizeY;

            this.x = startX;
            this.y = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    /**
     * Set right floating
     */
    public void setRightFloat(){
        isRight = true;
        calculate();
    }

    /**
     * Draw the element
     */
    public void draw(){

        Paint buttonBorder = new Paint();
        buttonBorder.setColor(view.getResources().getColor(R.color.base));
        canvas.drawRect(this.x, this.y, this.endX, this.endY, buttonBorder);

        Paint buttonPaint = new Paint();
        buttonPaint.setColor(view.getResources().getColor(R.color.line_selected));
        canvas.drawRect(this.x + border, this.y + border, this.endX - border, this.endY - border, buttonPaint);

        TextObject textObject = new TextObject(view, canvas, this.x + sizeX / 2, this.y + (int)(sizeY * 0.7), text);
        textObject.setAlign(Paint.Align.CENTER);

        Paint textPaint = new Paint();
        textPaint.setColor(view.getResources().getColor(R.color.base));
        textPaint.setTextSize(22);
        textObject.setPaint(textPaint);
        textObject.draw();
    }

    /**
     * If the point in the button
     * @param x
     * @param y
     * @return
     */
    public boolean isBounds(float x, float y){
        return this.x <= x && this.endX >= x && this.y <= y && this.endY >= y;
    }

    /**
     * Set Width
     * @param width - Width
     */
    public void setWidth(int width){
        this.sizeX = width;
        calculate();
    }

    /**
     * Set text
     * @param text  - Text
     */
    public void setText(String text){
        this.text = text;
    }
}
