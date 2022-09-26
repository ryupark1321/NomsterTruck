package edu.cornell.gdiac.optimize.map;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.GameObject;
import com.badlogic.gdx.graphics.*;

public class Key extends GameObject {

    /** Texture of the key */
    private Texture keyTexture;
    /** Whether the key has been collected by the player */
    private boolean isCollected;
    /** Size of the key image */
    private float size;
    /** Offset of the key moving in each frame */
    private float offset;
    /** Current frame of the key */
    private int frame;
    /** Timer since the key has been created */
    private int timer;
    /** Board position of the tile which the key is on */
    private Vector2 tileBoardPos;
    /** Whether the key is in collecting process */
    private boolean isCollecting;
    /** Timer for collecting process */
    private int collectingTimer;
    /** alpha value of the key (used in collecting process) */
    private float alpha;
    /** Time for collecting process */
    private static final int COLLECT_TIME = 60;

    private float keyDisplayY;
    private float keyDisplaySize;
    private float keyDisplayOffset;
    private float width;
    private float height;

    private float keyDisplayX0;
    private float keyDisplayX1;
    private float keyDisplayX2;

    private float collectingOffsetX;
    private float collectingOffsetY;
    private float sizeOffset;

    @Override
    public ObjectType getType() {
        return ObjectType.Key;
    }

    /**
     * Initialize a key.
     */
    public Key(Board board, float canvasWidth, float canvasHeight, Vector2 pos) {
        isCollected = false;
        isCollecting = false;
        tileBoardPos = pos;
        Vector2 screenPos = board.boardToScreen((int)pos.x,(int)pos.y);
        position = new Vector2(screenPos.x - board.getTileWidth()/6,screenPos.y);
        size = board.getTileWidth()/3;
        offset = board.getTileWidth()/50;
        frame = 0;
        timer = 0;
        collectingTimer = COLLECT_TIME;
        alpha = 1;
        width = canvasWidth;
        height = canvasHeight;

        keyDisplayY = height*9/10f;
        keyDisplaySize = height/10f;
        keyDisplayOffset = keyDisplaySize/6f;

        keyDisplayX0 = width - 3*keyDisplaySize - 2*keyDisplayOffset;
        keyDisplayX1 = width - 2*keyDisplaySize - keyDisplayOffset;
        keyDisplayX2 = width - keyDisplaySize;
        sizeOffset = keyDisplaySize - size;
    }

    public void setTexture(Texture texture) {keyTexture = texture;}

    public boolean isCollected() {return isCollected;}

    public boolean isCollecting() {return  isCollecting;}

    public void setCollected(boolean b,int i) {
        isCollecting = b;
        collectingOffsetY = keyDisplayY - position.y;
        if (i == 0) {
            collectingOffsetX = keyDisplayX0 - position.x;
        }
        else if (i == 1) {
            collectingOffsetX = keyDisplayX1 - position.x;
        }
        else if (i == 2) {
            collectingOffsetX = keyDisplayX2 - position.x;
        }
    }

    public void updateCollecting(GameCanvas canvas){
        if (collectingTimer != 0) {
            collectingTimer--;
            position.x += collectingOffsetX/COLLECT_TIME;
            position.y += collectingOffsetY/COLLECT_TIME;
            size += sizeOffset/COLLECT_TIME;
            alpha -= 1f/COLLECT_TIME;
            canvas.draw(keyTexture, position.x,position.y,size,size);
        }
        else {
            isCollecting = false;
            isCollected = true;
        }
    }

    public Vector2 getTileBoardPos() {return tileBoardPos;}

    public void draw(GameCanvas canvas) {
        if(!isCollected) {
            if (!isCollecting) {
                timer++;
                if (timer % 6 == 0) {
                    if (frame == 9) {frame = 0;}
                    else  {frame++;}
                    if (frame < 5){position.y -= offset;}
                    else {position.y += offset;}
                }
                canvas.draw(keyTexture, position.x,position.y,size,size);
            }
            else {
                updateCollecting(canvas);
            }
        }
    }
}
