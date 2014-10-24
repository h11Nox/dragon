package com.base.dragon;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameView extends View {

    // Game Field size
    public static final int size = 4;
    // Game level
    private int level = 1, selected = -1;
    // Levels data
    private int[][] data = {
        {0,5,1,4,2,3,1,5,4,1,4,3,2,0,4,6},
        {0,5,1,4,2,3,1,5,4,1,4,3,2,2,3,6},
        {0,4,2,3,5,2,1,5,3,1,3,4,2,1,3,6},
        {0,4,5,3,3,1,4,2,5,2,2,1,2,1,4,6}
    };
    // Game size properties
    private int fieldSize, cellSize, lineWidth=5, cellFullSize, unitSize = 150, fieldXPosition = 0, fieldYPosition = 100;
    // Units
    private Bitmap units, backBtn;
    private int move = 0, moves = 0, width;

    private Context context;
    private List<GameField> fields = new ArrayList<GameField>();

    /**
     * Constructor
     * @param context
     */
    public GameView(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Game init
     */
    public void init(int w, int h){
        initSize(w,h);
        initLevels();
    }

    /**
     * On Size Change method
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        init(w,h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLevel(canvas);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        if(currentX > lineWidth + fieldXPosition && currentX < fieldSize - lineWidth + fieldXPosition
                && currentY < fieldSize - lineWidth + fieldYPosition && currentY > lineWidth + fieldYPosition) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (GameField field : fields) {
                        if(field.hasPoint(currentX, currentY)){
                            select(field);
                            break;
                        }
                    }

                    break;
                // ToDo: swipe elements
            }
        }

        return true;
    }

    /**
     * Loading resources
     */
    private void loadResources(){
        try{
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("units.png");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            units = BitmapFactory
                    .decodeStream(inputStream, null, options);
            inputStream.close();

            InputStream backBtnStream = assetManager.open("backBtn.png");
            BitmapFactory.Options btnOptions = new BitmapFactory.Options();
            btnOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
            backBtn = BitmapFactory
                    .decodeStream(backBtnStream, null, btnOptions);
            backBtnStream.close();


        } catch (IOException e) {
            System.out.println("Can't load the file.");
        } finally {
            // we should really close our input streams here.
        }
    }

    /**
     * Calculating sizes
     */
    private void initSize(int w, int h){
        float width = (w - (2 + 2*size) * lineWidth)/size;
        cellSize = (int)width;
        cellFullSize = cellSize + 2 * lineWidth;
        fieldSize = size * cellFullSize + 2 * lineWidth;
    }

    /**
     * Init levels
     */
    protected void initLevels(){
        int[] currentData = data[level-1];
        int i,j;
        for(i=0; i<size;i++){
            for(j=0; j<size;j++){
                int unit = currentData[j + i * size];
                int x = fieldXPosition + lineWidth + j * cellFullSize;
                int y = fieldYPosition + lineWidth + i * cellFullSize;
                int endX = x + cellSize;
                int endY = y + cellSize;

                GameField field = new GameField(j, i, unit, this);
                field.setCoordinates(x, y, endX, endY);

                fields.add(field);
            }
        }
    }

    /**
     * Drawing level
     * @param canvas
     */
    public void drawLevel(Canvas canvas){
        this.loadResources();

        // Show Level
        Paint levelText = new Paint();
        levelText.setColor(this.getResources().getColor(R.color.level));
        levelText.setTextSize(52);
        canvas.drawText("Level: "+String.valueOf(this.level), 25, 70, levelText);

        // Show Move
        if(move > 0){
            Paint moveText = new Paint();
            moveText.setColor(this.getResources().getColor(R.color.move));
            moveText.setTextSize(52);
            moveText.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(this.move)+" "+(this.move == 1 ? "move" : "moves"), width-25, 70, moveText);
        }

        // Get Line Paint
        Paint border = new Paint();
        border.setColor(this.getResources().getColor(R.color.line));

        // Get Selected Line Paint
        Paint borderSelected = new Paint();
        borderSelected.setColor(this.getResources().getColor(R.color.line_selected));

        for (GameField field : fields) {
            canvas.drawRect(
                    field.getRect(), field.isSelected() ? borderSelected : border);

            canvas.drawBitmap(units, field.getSourceRect(unitSize), field.getDestRect(cellSize), null);
        }

        // Draw Back Button
        // canvas.drawBitmap(backBtn, 30, fieldSize + fieldYPosition + 30, null);

        units.recycle();
        backBtn.recycle();
    }

    /**
     * Select game field
     * @param field
     */
    protected void select(GameField field){
        if(selected != -1){
            if(!field.isSelected()){
                int index = field.getIndex();
                if(field.swap(fields.get(selected))){
                    Collections.swap(fields, selected, index);
                    move++;
                    if(checkIfCompleted()){
                        this.deselect();
                        return;
                    }
                }
                else{
                    // Show message about incorrect move
                    Toast toast = Toast.makeText(context, "Incorrect move", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            field.deselect();
            this.deselect();
        }
        else{
            if(field.select()){
                selected = field.getIndex();
                invalidate();
            }
        }
    }

    /**
     * Deselect field
     */
    protected void deselect(){
        if(selected != -1){
            GameField field = fields.get(selected);
            field.deselect();
        }
        selected = -1;
        invalidate();
    }

    /**
     * Checks if the level is done
     * @return boolean
     */
    protected boolean checkIfCompleted(){
        boolean result = false;
        if(fields.get(size * size - 2).type == GameField.TYPE_DRAGON){
            moves += move;
            move = 0;
            if(this.data.length > this.level){
                this.level++;
                fields.clear();
                this.initLevels();
                invalidate();
            }
            else{

            }
            result = true;
        }
        return result;
    }
}
