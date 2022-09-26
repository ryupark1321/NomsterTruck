/*
 * Truck.java
 *
 * This is a passive model, and this model does very little by itself.
 * All of its work is done by the CollisionController or the
 * GameplayController.
 *
 * This separation is very important for this class because it has a lot
 * of interactions with other classes.  When a ship fires, it creates
 * bullets. If did not move that behavior to the GameplayController,
 * then we would have to have a reference to the GameEngine in this
 * class. Tight coupling with the GameEngine is a very bad idea, so
 * we have separated this out.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import edu.cornell.gdiac.optimize.*;
import edu.cornell.gdiac.optimize.kitchen.Food;
import edu.cornell.gdiac.optimize.map.Board;
import edu.cornell.gdiac.optimize.map.HexTile;
import edu.cornell.gdiac.util.*;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.Array;

/**
 * Model class for the player ship.
 */
public class Truck extends GameObject {
    /// CONSTANTS
    /** Max time the player can spend on a tile */
    public static final int TILE_TIMER = 1300;

    /** The forward movement of the player this turn */
    private boolean forward;
    /** The backward movement of the player this turn */
    private boolean backward;
    /** The left movement of the player this turn */
    private boolean left;
    /** The right movement of the player this turn */
    private boolean right;
    /** The amount to scale the image */
    private float imgScale;

    /** Number of movements allowed */
    private int moveNum;
    /** Player's position in board coordinate system */
    private Vector2 boardPosition;
    /** Previous tile the player visited in board coordinate system */
//    private Vector2 prevTile;
    private boolean prevMove;
    /** Time left for the player to be on the current tile */
    private int tileTimer;
    /** Whether player was on a tile for too long */
    private boolean exceedTileTimer;
    /** Whether exceedTileTimer was triggered previously */
    private boolean prevTileTimerTrigger;

    /** Reference to truck's sprite for drawing */
    private FilmStrip truckSprite;


    private FilmStrip truckSpriteN;
    private FilmStrip truckSpriteNE;
    private FilmStrip truckSpriteSE;
    private FilmStrip truckSpriteS;
    private FilmStrip truckSpriteSW;
    private FilmStrip truckSpriteNW;

    private boolean isMoving;
    private float xOffset;
    private float yOffset;
    private int driveTimer;

    private static final int TRUCK_FRAME_SIZE = 5;
    private static final int TRUCK_STATIC_FRAME = 0;
    private static final int DRIVING_TIME = 30;
    private static final float TRUCK_SCALE = 0.1f;

    public static final int DIRECTION_N= 0;
    public static final int DIRECTION_NE = 1;
    public static final int DIRECTION_SE = 2;
    public static final int DIRECTION_S = 3;
    public static final int DIRECTION_SW = 4;
    public static final int DIRECTION_NW = 5;

    private boolean isThrowing;
    private float throwableFoodPosX;
    private float throwableFoodPosY;
    private float throwableFoodOffsetX;
    private float throwableFoodOffsetY;
    private int throwTimer;
    private float throwWidth;
    private float throwHeight;
    private float throwWidthFinal;
    private float throwHeightFinal;
    private float tileHeight;

    private static final int THROW_TIME = 60;
    private static final float[] THROW_OFFSETS = new float[] {0.605f,0.8f,0.96f,-0.605f,-0.8f,-0.96f};

    private int throwRadius = 3;
    private boolean isMoveable;
    private boolean canShoot;

    private int turnNum;
    private boolean madeMove;
    private boolean madeTurn;
    private Food throwFood;

    private Texture texture;
    private Animation<Texture> animation;

    private static Texture northTruck;
    private static Texture northwestTruck;
    private static Texture southwestTruck;
    private static Texture southTruck;
    private static Texture southeastTruck;
    private static Texture northeastTruck;

    private static Animation<Texture> northAnimation;
    private static Animation<Texture> northwestAnimation;
    private static Animation<Texture> southwestAnimation;
    private static Animation<Texture> southAnimation;
    private static Animation<Texture> southeastAnimation;
    private static Animation<Texture> northeastAnimation;

    private static final float NORTH_ANG = 0.0f;
    private static final float NORTHWEST_ANG = 60.0f;
    private static final float SOUTHWEST_ANG = 120.0f;
    private static final float SOUTH_ANG = 180.0f;
    private static final float SOUTHEAST_ANG = 240.0f;
    private static final float NORTHEAST_ANG = 300.0f;

    private Sound revving;
    private float soundVolume;

    //#endregion
    /**
     * Returns the type of this object.
     *
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    public ObjectType getType() {
        return ObjectType.Truck;
    }

    @Override
    /**
     * Returns the texture of this object.
     *
     * @return the texture of this object.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Gets whether the truck can throw food or not
     */
    public boolean getCanShoot() { return canShoot;}

    /**
     * Gets whether the truck can throw food or not
     */
    public Food getThrowFood() { return throwFood;}

    /**
     * Gets whether the truck can throw food or not
     */
    public void setThrowFood(Food f) { throwFood = f;}

    /**
     * Gets throwing radius of the truck
     */
    public int getThrowRadius() { return throwRadius;}

    public boolean getMadeTurn() {return madeTurn;}

    public void resetMadeTurn(){
        madeTurn = false;
    }

    /**
     * Gets throwing radius of the truck
     */
    public void setThrowRadius(int radius) { throwRadius = radius;}

    /**
     * Gets number of movements left for truck
     */
    public int getMoveNum() { return moveNum;}

    /**
     * Resets whether the truck moved previously
     */
    public void resetPrevMove() {
        prevMove = false;
    }

    /**
     * Returns whether the truck moved previously
     */
    public boolean getPrevMove() {
        return prevMove;
    }

    /**
     * Sets whether the player can turn.
     */
    public int getTurnNum() {return turnNum; }


    /**
     * Gets throwing radius of the truck
     */
    public void setMoveNum(int v) { moveNum = v;}

    /**
     * Sets the current player forward movement input.
     *
     * @param value the current player forward input.
     */
    public void setForward(boolean value) {forward = value; }

    /**
     * Sets the current player backward movement input.
     *
     * @param value the current player backward input.
     */
    public void setBackward(boolean value) {backward = value; }

    /**
     * Sets the current player left movement input.
     *
     * @param value the current player left input.
     */
    public void setLeft(boolean value) {left = value; }

    /**
     * Sets the current player right movement input.
     *
     * @param value the current right forward input.
     */
    public void setRight(boolean value) {right = value; }

    /**
     * Sets whether the player can move the truck or not.
     *
     * @param value the new movable state.
     */
    public void setIsMoveable(boolean value) {isMoveable = value; }

    /**
     * Sets whether the player can throw a food or not.
     *
     * @param value the new throwable state.
     */
    public void setCanShoot(boolean value) {canShoot = value; }

    /**
     * Sets whether the player can turn.
     *
     * @param value the turn state.
     */
    public void setTurnNum(int value) {turnNum = value; }



    /**
     * Initialize a truck.
     */
    public Truck() {
        forward = false;
        backward = false;
        left = false;
        right = false;
        madeTurn = false;
        isMoveable = false;
        madeMove = false;
        exceedTileTimer = false;
        prevTileTimerTrigger = false;
        tileTimer = TILE_TIMER;
        moveNum = 0;
        throwFood = null;
        isMoving = false;
        xOffset = 0;
        yOffset = 0;
        driveTimer = DRIVING_TIME;
        prevMove = false;
        isThrowing = false;
        throwTimer = THROW_TIME;
    }

    public float getThrowWidth() {return throwWidth;}
    public float getThrowHeight() {return throwHeight;}
    public float getThrowableFoodPosX() {return throwableFoodPosX;}
    public float getThrowableFoodPosY() {return throwableFoodPosY;}

    public boolean isThrowing() {return isThrowing;}

    public void startThrowing(Vector2 finalPos, float boardWidth, float boardHeight) {
        isThrowing = true;
        throwableFoodPosX = position.x;
        throwableFoodPosY = position.y;
        throwableFoodOffsetX = -throwableFoodPosX + finalPos.x;
        throwableFoodOffsetY = -throwableFoodPosY + finalPos.y;
        throwWidth = 0;
        throwHeight = 0;
        throwWidthFinal = boardWidth/2;
        throwHeightFinal = boardHeight;
    }

    public void setRevSound(Sound s){
        revving = s;
    }

    public void setSoundVolume(float f){
        soundVolume = f;
    }

    public void updateThrowing() {
        if(throwTimer != 0) {
            throwableFoodPosX += throwableFoodOffsetX/(float)THROW_TIME;
            throwableFoodPosY += throwableFoodOffsetY/(float)THROW_TIME
                    + THROW_OFFSETS[(THROW_TIME-throwTimer)/(THROW_TIME/THROW_OFFSETS.length)] * tileHeight*0.05f;
            throwWidth += throwWidthFinal/(float)THROW_TIME;
            throwHeight += throwHeightFinal/(float)THROW_TIME;
            throwTimer --;
        } else {
            isThrowing = false;
            throwTimer = THROW_TIME;
            canShoot = false;
        }
    }

    public void setTexture(Array<Texture> textures) {
        northTruck = textures.get(0);
        northAnimation = new Animation<Texture>(5f, textures.get(0));
        northeastTruck = textures.get(1);
        northeastAnimation = new Animation<Texture>(5f, textures.get(1));
        southeastTruck = textures.get(2);
        southeastAnimation = new Animation<Texture>(5f, textures.get(2));
        southTruck = textures.get(3);
        southAnimation = new Animation<Texture>(5f, textures.get(3));
        southwestTruck = textures.get(4);
        southwestAnimation = new Animation<Texture>(5f, textures.get(4));
        northwestTruck = textures.get(5);
        northwestAnimation = new Animation<Texture>(5f, textures.get(5));
        texture = northeastTruck;
    }

    /**
     * Sets the image texture for truck
     *
     * param value the image texture for truck
     */
    public void setFilmStrip(int level) {
        truckSprite = truckSpriteSE;
        if (level < 2){
            truckSprite = truckSpriteNE;
        }
        truckSprite.setFrame(TRUCK_STATIC_FRAME);
    }

    public void setDriveSprites (FilmStrip[] sprites, Board board) {
        truckSpriteN = sprites[DIRECTION_N];
        truckSpriteNE = sprites[DIRECTION_NE];
        truckSpriteSE = sprites[DIRECTION_SE];
        truckSpriteS = sprites[DIRECTION_S];
        truckSpriteSW = sprites[DIRECTION_SW];
        truckSpriteNW = sprites[DIRECTION_NW];
        imgScale = board.getTileHeight()*3f / truckSpriteN.getRegionHeight();
        tileHeight = board.getTileHeight();
    }

    public void startMoving(float x, float y, Board board) {
        Vector2 arrayCoord = board.boardToArray((int) x, (int) y);
        HexTile hex = board.getTileArrayCoord((int) arrayCoord.x, (int) arrayCoord.y);
//        position.x = hex.getX() + board.getTileWidth()/2;
//        position.y = hex.getY() + board.getTileHeight()/2 + board.getTileHeight()/0.346f*0.654f;
        if(revving != null) {
            revving.play(soundVolume);
        }
        xOffset = -position.x + (hex.getX());
        yOffset = -position.y + (hex.getY());
        isMoving = true;
    }

    public boolean isMoving() {return isMoving;}


    public void updateMoving() {
        if (driveTimer != 0) {
            if (driveTimer % (DRIVING_TIME/TRUCK_FRAME_SIZE) == 0 && driveTimer!= DRIVING_TIME) {
                truckSprite.setFrame(truckSprite.getFrame()+1);
            }
            driveTimer--;
            float singleMoveX = xOffset/(float)DRIVING_TIME;
            float singleMoveY = yOffset/(float)DRIVING_TIME;
            if (truckSprite.getFrame() == 0 || truckSprite.getFrame() == 4){
                position.x += 0.5f* singleMoveX;
                position.y += 0.5f* singleMoveY;
            }
            else if (truckSprite.getFrame() == 1 || truckSprite.getFrame() == 3) {
                position.x += singleMoveX;
                position.y += singleMoveY;
            }
            else {
                position.x += 2* singleMoveX;
                position.y += 2* singleMoveY;
            }
        }
        else{
            isMoving = false;
            truckSprite.setFrame(TRUCK_STATIC_FRAME);
            xOffset = 0;
            yOffset = 0;
            driveTimer = DRIVING_TIME;
        }
    }

    /**
     * Sets whether the tile timer exceeded.
     */
    public void resetExceedTileTimer() {
        exceedTileTimer = false;
        prevTileTimerTrigger = true;
//        tileTimer = TILE_TIMER;
    }

    /**
     * Sets whether the tile timer exceeded.
     */
    public void resetTileTimer() {
        tileTimer = TILE_TIMER;
        prevTileTimerTrigger = false;
    }

    /**
     * Returns whether the tile timer exceeded.
     *
     * @return whether the tile timer exceeded
     */
    public boolean getExceedTileTimer() {
        return exceedTileTimer;
    }

    /**
     * Returns the time (number of frames) left to spend on the current tile
     *
     * @return the time (number of frames) left to spend on the current tile
     */
    public int getTileTimer() {
        return tileTimer;
    }

//    /**
//     * Sets the tile the player most previously visited
//     *
//     * @param v the mostly previously visited tile's board coordinate
//     */
//    public void setPrevTile(Vector2 v) {
//        prevTile = v;
//    }

    public boolean getIsMoveable() {
        return isMoveable;
    }

    public boolean getForward(){
        return forward;
    }

    public boolean getPrevTileTimerTrigger() { return prevTileTimerTrigger; }

    public int getDirection() {
        if (truckSprite.equals(truckSpriteN)){
            return 0;
        }
        else if (truckSprite.equals(truckSpriteNE)) {
            return 1;
        }
        else if (truckSprite.equals(truckSpriteSE)) {
            return 2;
        }
        else if (truckSprite.equals(truckSpriteS)) {
            return 3;
        }
        else if (truckSprite.equals(truckSpriteSW)) {
            return 4;
        }
        else {
            return 5;
        }
    }

    /**
     * Updates the animation frame and position of this truck.
     *
     * Notice how little this method does.  It does not actively fire the weapon.  It
     * only manages the cooldown and indicates whether the weapon is currently firing.
     * The result of weapon fire is managed by the GameplayController.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta, Board board, boolean didThrow) {
        // Call superclass's update
        super.update(delta);
        if (isMoving) {
            updateMoving();
        }
        else if (isMoveable && !madeMove && !madeTurn) {
            Vector2 pos = getBoardPosition();
            if (left) {
                // reset ismoveable after making one move
                madeTurn = true;
                if (truckSprite.equals(truckSpriteN)){
                    truckSprite = truckSpriteNW;
                }
                else if (truckSprite.equals(truckSpriteNE)) {
                    truckSprite = truckSpriteN;
                }
                else if (truckSprite.equals(truckSpriteSE)) {
                    truckSprite = truckSpriteNE;
                }
                else if (truckSprite.equals(truckSpriteS)) {
                    truckSprite = truckSpriteSE;
                }
                else if (truckSprite.equals(truckSpriteSW)) {
                    truckSprite = truckSpriteS;
                }
                else if (truckSprite.equals(truckSpriteNW)) {
                    truckSprite = truckSpriteSW;
                }
            }

            if (right) {
                // reset ismoveable after making one move
                madeTurn = true;
                if (truckSprite.equals(truckSpriteN)){
                    truckSprite = truckSpriteNE;
                }
                else if (truckSprite.equals(truckSpriteNE)) {
                    truckSprite = truckSpriteSE;
                }
                else if (truckSprite.equals(truckSpriteSE)) {
                    truckSprite = truckSpriteS;
                }
                else if (truckSprite.equals(truckSpriteS)) {
                    truckSprite = truckSpriteSW;
                }
                else if (truckSprite.equals(truckSpriteSW)) {
                    truckSprite = truckSpriteNW;
                }
                else if (truckSprite.equals(truckSpriteNW)) {
                    truckSprite = truckSpriteN;
                }
            }
            if (forward) {
                prevMove = true;
                if (truckSprite.equals(truckSpriteN)) {
                    if (board.isObstacleFree((int) pos.x, (int) pos.y + 1)) {
                        startMoving(pos.x, pos.y + 1, board);
                        boardPosition.x = pos.x;
                        boardPosition.y = pos.y + 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteNW)) {
                    if (board.isObstacleFree((int) pos.x - 1, (int) pos.y + 1)) {
                        startMoving(pos.x - 1, pos.y + 1, board);
                        boardPosition.x = pos.x - 1;
                        boardPosition.y = pos.y + 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteSW)) {
                    if (board.isObstacleFree((int) pos.x - 1, (int) pos.y )) {
                        startMoving(pos.x - 1, pos.y, board);
                        boardPosition.x = pos.x - 1;
                        boardPosition.y = pos.y;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteS)) {
                    if (board.isObstacleFree((int) pos.x, (int) pos.y - 1)) {
                        startMoving(pos.x, pos.y - 1, board);
                        boardPosition.x = pos.x;
                        boardPosition.y = pos.y - 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteSE)) {
                    if (board.isObstacleFree((int) pos.x + 1, (int) pos.y - 1)) {
                        startMoving(pos.x + 1, pos.y - 1, board);
                        boardPosition.x = pos.x + 1;
                        boardPosition.y = pos.y - 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteNE)) {
                    if (board.isObstacleFree((int) pos.x + 1, (int) pos.y)) {
                        startMoving(pos.x + 1, pos.y, board);
                        boardPosition.x = pos.x + 1;
                        boardPosition.y = pos.y;
                        madeMove = true;
                        moveNum--;
                    }
                }
            }
            if (backward) {
                prevMove = true;
                if (truckSprite.equals(truckSpriteN)) {
                    if (board.isObstacleFree((int) pos.x, (int) pos.y - 1)) {
                        startMoving(pos.x, pos.y - 1, board);
                        boardPosition.x = pos.x;
                        boardPosition.y = pos.y - 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteNW)) {
                    if (board.isObstacleFree((int) pos.x + 1, (int) pos.y - 1)) {
                        startMoving(pos.x + 1, pos.y - 1, board);
                        boardPosition.x = pos.x + 1;
                        boardPosition.y = pos.y - 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteSW)) {
                    if (board.isObstacleFree((int) pos.x + 1, (int) pos.y)) {
                        startMoving(pos.x + 1, pos.y, board);
                        boardPosition.x = pos.x + 1;
                        boardPosition.y = pos.y;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteS)) {
                    if (board.isObstacleFree((int) pos.x, (int) pos.y + 1)) {
                        startMoving(pos.x, pos.y + 1, board);
                        boardPosition.x = pos.x;
                        boardPosition.y = pos.y + 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteSE)) {
                    if (board.isObstacleFree((int) pos.x - 1, (int) pos.y + 1)) {
                        startMoving(pos.x - 1, pos.y + 1, board);
                        boardPosition.x = pos.x - 1;
                        boardPosition.y = pos.y + 1;
                        madeMove = true;
                        moveNum--;
                    }
                } else if (truckSprite.equals(truckSpriteNE)){
                    if (board.isObstacleFree((int) pos.x - 1, (int) pos.y)) {
                        startMoving(pos.x - 1, pos.y, board);
                        boardPosition.x = pos.x - 1;
                        boardPosition.y = pos.y;
                        madeMove = true;
                        moveNum--;
                    }
                }
            }
        }

        // update timer
//        if (!prevTile.equals(boardPosition)) {
//            prevTile = boardPosition;
        if (prevMove) {
            tileTimer = TILE_TIMER;
        } else {
            tileTimer--;
        }

        if (tileTimer < 0 && !prevTileTimerTrigger) {
            exceedTileTimer = true;
        }

        if (moveNum <= 0) {
            isMoveable = false;
        }

        // throw and reset
        if (didThrow){
            canShoot = false;
        }
    }


    /**
     * Draws this truck to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void draw(GameCanvas canvas) {
        float x = truckSprite.getRegionWidth()/2.0f;
        float y = truckSprite.getRegionHeight()/3.0f;
        canvas.draw(truckSprite, Color.WHITE, x, y, position.x, position.y, 0, imgScale, imgScale);
    }

    public void setBoardPosition(Board board) {
        Vector2 boardPos = board.screenToArray(position.x, position.y);
        boardPosition = board.arrayToBoard((int) boardPos.x, (int) boardPos.y);
    }

    public Vector2 getBoardPosition() {
        return boardPosition;
    }

    public Vector2 getArrayPosition(Board board) {
        return board.boardToArray((int) boardPosition.x, (int) boardPosition.y);
    }

    public void setPosition(float x, float y, Board board) {
        Vector2 arrayCoord = board.boardToArray((int) x, (int) y);
        HexTile hex = board.getTileArrayCoord((int) arrayCoord.x, (int) arrayCoord.y);
        position.x = hex.getX();
        position.y = hex.getY();
    }

    public boolean getMadeMove() {
        return madeMove;
    }

    public void resetMadeMove() {
        madeMove = false;
    }
}
