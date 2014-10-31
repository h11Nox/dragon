package com.base.dragon.objects;


import android.graphics.Canvas;
import android.graphics.Paint;
import com.base.dragon.R;
import com.base.dragon.main.GameView;

public class TextObject extends GameObject{

    protected String text;
    protected int fontSize = 52;
    protected Paint.Align align = Paint.Align.LEFT;
    protected Paint paint = new Paint();

    public TextObject(GameView view, Canvas canvas, int x, int y, String text){
        super.init(view, canvas, x, y);
        this.text = text;

        paint.setColor(view.getResources().getColor(R.color.level));
        paint.setTextSize(fontSize);
    }

    /**
     * Drawing element
     */
    public void draw(){
        paint.setTextAlign(align);
        canvas.drawText(text, x, y, paint);
    }

    /**
     * Set text align
     * @param align - Text Align
     */
    public void setAlign(Paint.Align align){
        this.align = align;
    }

    /**
     * Set paint
     * @param paint
     */
    public void setPaint(Paint paint){
        this.paint = paint;
    }
}
