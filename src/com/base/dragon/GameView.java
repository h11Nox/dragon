package com.base.dragon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GameView extends SurfaceView{
    public GameThread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

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
    private int fieldSize, cellSize, lineWidth=5, cellFullSize, unitSize = 150, fieldXPosition = 0, fieldYPosition = 0;
    // Units
    private Bitmap units;
    private int move = 0, moves = 0;

    private Context context;
    private List<GameField> fields = new ArrayList<GameField>();
    private int[] currentData;
    private int canvasWidth, canvasHeight;
    private boolean isPortrait = true;

    public GameView(Context context) {
        super(context);
        this.context = context;
        loadData();

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
                renderThread.setRunning(false);
                boolean retry = true;
                while (retry) {
                    try {
                        running = false;
                        holder.lockCanvas();
                        renderThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                init();
                renderThread.setRunning(true);
                renderThread.start();
                running = true;
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

        loadResources();
    }

    public void resume() {
        running = true;

        renderThread.start();
        run();
    }

    public void run() {
        while(running) {
            if(!holder.getSurface().isValid())
                continue;

            Canvas canvas = holder.lockCanvas();
            canvas.drawRGB(255, 255, 0);
            holder.unlockCanvasAndPost(canvas);
        }
    }
    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                // retry
            }
        }
    }

    /**
    * Game init
    */
    public void init(){
        initSize();
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
        renderThread = new GameThread(this);
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if(isPortrait){
            fieldYPosition = 100;
        }
        canvasWidth = w;
        canvasHeight = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLevel(canvas);
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


        } catch (IOException e) {
            System.out.println("Can't load the file.");
        } finally {
            // we should really close our input streams here.
        }
    }

    /**
     * Calculating sizes
     */
    private void initSize(){
        float cell = (Math.min(canvasWidth, canvasHeight) - (2 + 2*size) * lineWidth)/size;
        cellSize = (int)cell;
        cellFullSize = cellSize + 2 * lineWidth;
        fieldSize = size * cellFullSize + 2 * lineWidth;
        System.out.print(canvasWidth);
        System.out.print(canvasHeight);
    }

    /**
     * Init levels
     */
    protected void initLevels(){
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
        if(!holder.getSurface().isValid() || !running)
            return;

        canvas.drawColor(Color.BLACK);
        // Show Level
        int levelTextX = 25, levelTextY = 70;
        if(!isPortrait){
            levelTextX = fieldSize + levelTextX;
        }

        Paint levelText = new Paint();
        levelText.setColor(this.getResources().getColor(R.color.level));
        levelText.setTextSize(52);
        canvas.drawText("Level: " + String.valueOf(this.level), levelTextX, levelTextY, levelText);

        // Show Move
        int moveTextX = canvasWidth - 25;
        int moveTextY = 70;
        if (!isPortrait){
            moveTextX = fieldSize + 25;
            moveTextY = 122;
        }

        if (move > 0) {
            Paint moveText = new Paint();
            moveText.setColor(this.getResources().getColor(R.color.move));
            moveText.setTextSize(52);
            moveText.setTextAlign(isPortrait ? Paint.Align.RIGHT : Paint.Align.LEFT);

            canvas.drawText(String.valueOf(this.move) + " " + (this.move == 1 ? "move" : "moves"), moveTextX, moveTextY, moveText);
        }

        // Get Line Paint
        Paint border = new Paint();
        border.setColor(this.getResources().getColor(R.color.line));

        // Get Selected Line Paint
        Paint borderSelected = new Paint();
        borderSelected.setColor(this.getResources().getColor(R.color.line_selected));

        for (GameField field : fields) {

            field.move();

            Paint unitPaint = new Paint();
            if(field.isMoving()){
                unitPaint.setAlpha(100);
            }
            else{
                canvas.drawRect(
                        field.getRect(), field.isSelected() ? borderSelected : border);
            }

            canvas.drawBitmap(units, field.getSourceRect(unitSize), field.getDestRect(cellSize), unitPaint);
        }
    }

    /**
     * Select game field
     * @param field
     */
    protected void select(GameField field){
        if(selected != -1){
            if(!field.isSelected()){
                // int index = field.getIndex();
                GameField currentField = fields.get(selected);
                if(field.check(currentField)){
                    currentField.swap(field);
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
                //invalidate();
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
        //invalidate();
    }

    /**
     * Checks if the level is done
     * @return boolean
     */
    protected boolean checkIfCompleted(){
        boolean result = false;
        if(fields.get(size * size - 1).type == GameField.TYPE_DRAGON){
            moves += move;
            move = 0;
            if(this.data.length > this.level){
                this.level++;
                loadData();
                fields.clear();
                this.initLevels();

                // ToDo - Do level changing animation
                renderThread.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Dragon")
                                .setMessage("Level complete! Press Ok button to continue.")
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
                });

                // invalidate();
            }
            else{
                // ToDo - Show win message
            }
            result = true;
        }
        return result;
    }

    /**
     * Load level data
     */
    protected void loadData(){
        currentData = data[level-1];
    }

    /**
     * Get Current State Data
     * @return
     */
    public Map getCurrentData(){
        Map data = new HashMap<String, Integer>();
        data.put("level", level);
        data.put("move", move);
        data.put("moves", moves);

        return data;
    }

    /**
     * Get Game Fields
     * @return
     */
    public int[] getUnits(){
        int[] data = new int[size*size];
        int index=0;
        for (GameField field : fields) {
            data[index] = field.getUnit();
            index++;
        }
        return data;
    }

    /**
     * Load Level
     * @param level     - Level
     * @param move      - Current Move
     * @param moves     - Total moves count
     * @param data      - Units
     */
    public void load(int level, int move, int moves, int[] data){
        this.level = level;
        this.move = move;
        this.moves = moves;
        this.currentData = data;
    }

    /**
     * Make a move
     * @param index1 - Index
     * @param index2 - Index
     */
    public void move(int index1, int index2){
        Collections.swap(fields, index1, index2);
        move++;
        if(checkIfCompleted()){
            this.deselect();
        }
    }
}
