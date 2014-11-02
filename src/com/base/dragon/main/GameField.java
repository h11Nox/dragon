package com.base.dragon.main;

import android.graphics.Rect;

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

    private final static double ANIMATION_SPEED = 20;
    protected double positionX = 0, positionY = 0;
    protected boolean isSingleMove = false;

    // Unit type
    public int type;
    // Is unit selected
    private boolean selected = false, isMoving = false;
    // Unit index
    private int index, padding = 5;
    // Unit coordinates
    private int i,j;
    private int x, y, endX, endY, currentX, currentY;
    // View
    private GameView view;

    public GameField swapField;
    public boolean needSwap = false;

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

        currentX = x;
        currentY = y;
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

        currentX = x;
        currentY = y;
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
        destRect.set(currentX + padding, currentY + padding, currentX + cellSize - padding, currentY + cellSize - padding);

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
        if((!isBlocked() && !field.isBlocked() )|| (field.type == GameField.TYPE_DRAGON && this.type == TYPE_CAVE) ){
            boolean isAllowed = (this.type != GameField.TYPE_DRAGON && field.type != GameField.TYPE_DRAGON && Math.abs(type - field.type) == 1 )
                    || ((this.type == GameField.TYPE_DRAGON ^ field.type == GameField.TYPE_DRAGON) && (this.type == GameField.TYPE_RIDER ^ field.type == GameField.TYPE_RIDER))
                    || ((field.type == GameField.TYPE_DRAGON && this.type == TYPE_CAVE));
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
     * Make a move
     * @return
     */
    public void move(){
        if(!isMoving){
            return;
        }
        if(positionX > 0 && currentX != positionX){
            currentX += positionX > currentX ? ANIMATION_SPEED : -ANIMATION_SPEED;
            if(Math.abs(currentX - positionX) <= ANIMATION_SPEED){
                finishMove();
            }
        }
        if(positionY > 0 && currentY != positionY){
            currentY += positionY > currentY ? ANIMATION_SPEED : -ANIMATION_SPEED;
            if(Math.abs(currentY - positionY) <= ANIMATION_SPEED){
                finishMove();
            }
        }
    }

    /**
     * Finish move
     */
    public void finishMove(){
        if(needSwap){

            clearMoveData();
            swapField.clearMoveData();

            endMove(swapField);
            needSwap = false;
            isSingleMove = false;
        }
    }

    /**
     * Clear Move Data
     */
    public void clearMoveData(){
        isMoving = false;
        positionX = 0;
        positionY = 0;
    }

    /**
     * End Move
     * @param field - Field
     */
    public void endMove(GameField field){
        int cI = i, cJ = j, index = getIndex();
        int[] coordinates = getCoordinates();
        this.setPosition(field.getI(), field.getJ());
        this.setCoordinates(field.getCoordinates());
        this.deselect();

        field.setPosition(cI, cJ);
        if(!isSingleMove){
            field.setCoordinates(coordinates);
        }
        field.deselect();

        view.move(getIndex(), index);
    }

    /**
     * Set Move Position
     * @param field - Field
     */
    public void setMovePosition(GameField field){
        positionX = field.getX();
        positionY = field.getY();
    }

    /**
     * Swapping fields
     * @param field - Game Field
     */
    public void swap(GameField field){
        boolean isLastMove = field.type == TYPE_CAVE;

        if(!isLastMove){
            field.setIsMoving(true);
            field.setMovePosition(this);
        }

        // Set the program need to swap fields at the end
        setIsMoving(true);
        setMovePosition(field);

        // To fix animation bug have to check if the field will be rendered after need to set like the main field another one
        if(getIndex() > field.getIndex() || isLastMove){
            needSwap = true;
            swapField = field;

            if(isLastMove){
                isSingleMove = true;
            }
        }
        else{
            field.needSwap = true;
            field.swapField = this;
        }
    }
}
