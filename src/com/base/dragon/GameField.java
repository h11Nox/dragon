package com.base.dragon;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class GameField {

    /*
    Unit types
     */
    public final static int TYPE_DRAGON = 0;
    public final static int TYPE_MAGE = 1;
    public final static int TYPE_GNOME = 2;
    public final static int TYPE_KNIGHT = 3;
    public final static int TYPE_RIDER = 4;
    public final static int TYPE_WALL = 5;
    public final static int TYPE_CAVE = 6;

    private final static double ANIMATION_SPEED = 0.0005;
    protected double positionX = 0, positionY = 0;

    // Unit type
    public int type;
    // Is unit selected
    private boolean selected = false, isMoving = false;
    // Unit index
    private int index, padding = 5;
    // Unit coordinates
    private int i,j;
    private int x, y, endX, endY;
    // View
    private GameView view;

    /**
     * Constructor
     * @param i - x position on game field
     * @param j - y position on game field
     * @param type - type of unit
     * @param view - view
     */
    public GameField(int i, int j, int type, GameView view) {
        this.i = i;
        this.j = j;
        this.index = GameView.size * j + i;
        this.type = type;
        this.view = view;
    }

    /**
     * Set field coordinates
     * @param x - X position
     * @param y - Y position
     * @param endX - X end position
     * @param endY - Y end position
     */
    public void setCoordinates(int x, int y, int endX, int endY){
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Set field coordinates
     * @param data - Coordinates
     */
    public void setCoordinates(int[] data){
        x = data[0];
        y = data[1];
        endX = data[2];
        endY = data[3];
    }

    /**
     * Get Coordinates
     * @return int[]
     */
    public int[] getCoordinates(){
        int[] data = {x, y, endX, endY};
        return data;
    }

    /**
     * Get unit type
     * @return int
     */
    public int getUnit(){
        return this.type;
    }

    /**
     * Get field index
     * @return int
     */
    public int getIndex(){
        return this.index;
    }

    /**
     * Set field index
     * @param index - Index
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * Get i
     * @return int
     */
    public int getI(){
        return i;
    }

    /**
     * Get X
     * @return int
     */
    public int getX(){
        return x;
    }

    /**
     * Get Y
     * @return int
     */
    public int getY(){
        return y;
    }

    /**
     * Get j
     * @return int
     */
    public int getJ(){
        return j;
    }

    /**
     * Set position
     * @param i - i
     * @param j - j
     */
    public void setPosition(int i, int j){
        this.index = GameView.size * j + i;
        this.i = i;
        this.j = j;
    }

    /**
     * Check if point is in field
     * @param xPoint - X position
     * @param yPoint - Y position
     * @return boolean
     */
    public boolean hasPoint(float xPoint, float yPoint){
        return xPoint >= x && xPoint <= endX && yPoint >= y && yPoint <= endY;
    }

    /**
     * Check if unit is selected
     * @return boolean
     */
    public boolean isSelected(){
        return selected;
    }

    /**
     * Select unit
     * @return boolean
     */
    public boolean select(){
        boolean result = false;
        if(!isBlocked()){
            result = true;
            selected = true;
        }
        return result;
    }

    /**
     * Check if field is blocked
     * @return boolean
     */
    public boolean isBlocked(){
        return type == GameField.TYPE_WALL || type == GameField.TYPE_CAVE;
    }

    /**
     * Deselect unit
     */
    public void deselect(){
        if(selected){
            selected = false;
        }
    }

    /**
     * Get field rectangle
     * @return Rect
     */
    public Rect getRect(){
        Rect rect = new Rect();
        rect.set(x, y, endX, endY);
        return rect;
    }

    /**
     * Get units sprite field rectangle
     * @param unitSize - Unit Size px
     * @return Rect
     */
    public Rect getSourceRect(int unitSize){
        Rect sourceRect = new Rect();
        sourceRect.set(type * unitSize, 0, (type +1)*unitSize, unitSize);
        return sourceRect;
    }

    /**
     * Get destination rectangle
     * @param cellSize - Game Cell size px
     * @return Rect
     */
    public Rect getDestRect(int cellSize){
        Rect destRect = new Rect();
        destRect.set(x + padding + (int)positionX, y + padding + (int)positionY, x + cellSize - padding + (int)positionX, y + cellSize - padding + (int)positionY);

        return destRect;
    }

    /**
     * Check if field is next to current one
     * @param i - i
     * @param j - j
     * @return boolean
     */
    public boolean isNext(int i, int j){
        return (this.i == i && Math.abs(this.j - j) == 1) || (this.j == j && Math.abs(this.i - i) == 1);
    }

    /**
     * Check if units can be swapped
     * @param field - Game Field
     * @return boolean
     */
    public boolean check(GameField field){
        boolean result = false;
        if(!isBlocked() && !field.isBlocked()){
            boolean isAllowed = (this.type != GameField.TYPE_DRAGON && field.type != GameField.TYPE_DRAGON && Math.abs(type - field.type) == 1 )
                    || ((this.type == GameField.TYPE_DRAGON ^ field.type == GameField.TYPE_DRAGON) && (this.type == GameField.TYPE_RIDER ^ field.type == GameField.TYPE_RIDER));
            if(isAllowed && field.isNext(i, j)){
                result = true;
            }
        }

        return result;
    }

    /**
     * Set is moving
     * @param moving
     */
    public void setIsMoving(boolean moving){
        this.isMoving = moving;
    }

    /**
     * If field is moving
     * @return
     */
    public boolean isMoving(){
        return isMoving;
    }

    /**
     * Start animation
     * @param field - Field
     */
    public void startMove(GameField field){

        boolean isFieldMoving = true;
        field.setIsMoving(true);
        setIsMoving(true);

        while(isFieldMoving){
            field.move(this);
            if(!move(field)){
                isFieldMoving = false;
                field.finishMove();
                finishMove();

                endMove(field);
            }
        }
    }

    /**
     * Make a move
     * @param field
     * @return
     */
    public boolean move(GameField field){
        boolean result = true;
        int i = field.getI(), j = field.getJ(), finishX = field.getX(), finishY = field.getY();
        if(i != this.i){
            int direction = i - this.i;
            positionX += direction*ANIMATION_SPEED;
            if((int)(x + positionX) == finishX){
                result = false;
            }
        }
        else{
            int direction = j - this.j;
            positionY += direction*ANIMATION_SPEED;
            if((int)(y + positionY) == finishY){
                result = false;
            }
        }

        /*try {
            view.renderThread.sleep(10);
        } catch (Exception e) {}*/

        return result;
    }

    /**
     * Finish move
     */
    public void finishMove(){
        isMoving = false;
        positionX = 0;
        positionY = 0;
    }

    /**
     * End Move
     * @param field - Field
     */
    public void endMove(GameField field){
        int cI = i, cJ = j;
        int[] coordinates = getCoordinates();
        this.setPosition(field.getI(), field.getJ());
        this.setCoordinates(field.getCoordinates());
        this.deselect();

        field.setPosition(cI, cJ);
        field.setCoordinates(coordinates);
        field.deselect();

        view.move(getIndex(), field.getIndex());
    }

    /**
     * Swapping fields
     * @param field - Game Field
     */
    public void swap(GameField field){
        startMove(field);
    }
}
