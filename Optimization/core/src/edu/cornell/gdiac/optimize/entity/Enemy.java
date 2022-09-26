package edu.cornell.gdiac.optimize.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.optimize.map.Board;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.GameObject;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.optimize.map.HexTile;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.HashMap;
import java.util.Random;

public class Enemy extends GameObject {
    // Constants
    public static final int COOL_TIME = 90;
    private static final int DEFAULT_COOL_TIME = 120;
    public static int ENEMY_TIMER = 120 * 6;
    private int ABOUT_TO_TRUCK_CHASE;
    private static int UNSET_TRUCK_CHASE = -1;
    private static final float[] HOP_OFFSETS = new float[] {0,0,0,0,0,0,0.605f,0.8f,0.96f,-0.605f,-0.8f,-0.96f};
    private static final int[] HOP_FRAMES = new int[] {0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,0,1,2,3,4,5,6,7,8,9,10,11};
    private int currFrame;
    /** Scale of the enemy image */
    private float scaleSize;
    /** Scale of the indicator image */
    private float indicatorScaleSize;

    /** Path of the enemy in direction */
    private Queue<Vector2> path;
    /** Time to wait until enemy moves to next tile */
    private int coolTime;
    /** Time left for the enemy to stay on the current tile */
    private int timer;
    /** Half of the tile height for offsetting enemy drawing location */
    private float tileHOffset;
    private int cooltimeScale;
    /** Position of the enemy in board coordinate system*/
    private Vector2 tilePosition;

    /** Reference to enemy's sprite for drawing */
    private FilmStrip hopSprite;

    private FilmStrip hopSpriteN;
    private FilmStrip hopSpriteNE;
    private FilmStrip hopSpriteSE;
    private FilmStrip hopSpriteS;
    private FilmStrip hopSpriteSW;
    private FilmStrip hopSpriteNW;
    private FilmStrip moveIndicator;

    /** Tile with food placed that enemy wants to move to */
    private HexTile targetFoodTile;
    /** Whether the enemy is moving to the tile with food placed */
    private boolean isFoodChasing = false;
    /** Whether the enemy is chasing the player */
    private boolean isChasingTruck = false;
    /** Time left until the enemy moves in one tile closer to the player that's triggered when about to move */
    private int aboutToChaseTruckTimer;
    /** Color to tint the enemy when drawing */
    private Color color = Color.WHITE;

    public static final int STAND_FRAME = 0;
    public static final int HOPPING_TIME = 222;
    private static final float MOVING_FRAMES = 36;

    private boolean isMoving;
    private boolean hasMoved;
    private boolean needsRemoval;
    private float xOffset;
    private float yOffset;
    private int hopTimer;

    public static final int DIRECTION_N= 0;
    public static final int DIRECTION_NE = 1;
    public static final int DIRECTION_SE = 2;
    public static final int DIRECTION_S = 3;
    public static final int DIRECTION_SW = 4;
    public static final int DIRECTION_NW = 5;

    public Enemy(Vector2 startPos, Board board) {
        coolTime = DEFAULT_COOL_TIME;
        cooltimeScale = 0;
        resetTimer();
        tilePosition = startPos;
        setPosition(tilePosition, board);
        path = new Queue<>();
        path.addLast(new Vector2(1, 0));
        path.addLast(new Vector2(-1, 1));
        path.addLast(new Vector2(0, -1));
        targetFoodTile = null;
        aboutToChaseTruckTimer = UNSET_TRUCK_CHASE;
    }

    public Enemy(int time, Vector2 startPos, Board board) {
        coolTime = time;
        cooltimeScale = 0;
        resetTimer();
        tilePosition = startPos;
        setPosition(tilePosition, board);
        path = new Queue<>();
        path.addLast(new Vector2(1, 0));
        path.addLast(new Vector2(-1, 1));
        path.addLast(new Vector2(0, -1));
        targetFoodTile = null;
        aboutToChaseTruckTimer = UNSET_TRUCK_CHASE;
    }

    public Enemy(int time, Vector2 startPos, Board board, Queue<Vector2> p) {
        coolTime = time;
        cooltimeScale = 0;
        resetTimer();
        path = p;
        tilePosition = startPos;
        setPosition(tilePosition, board);
        targetFoodTile = null;
        aboutToChaseTruckTimer = UNSET_TRUCK_CHASE;
        isMoving = false;
        xOffset = 0;
        yOffset = 0;
        hopTimer = HOPPING_TIME;
        tileHOffset = board.getTileHeight()*2/3;
        currFrame = 0;
    }


    public void setHopSprites (FilmStrip[] sprites, Board board) {
        hopSpriteN = sprites[DIRECTION_N];
        hopSpriteNE = sprites[DIRECTION_NE];
        hopSpriteSE = sprites[DIRECTION_SE];
        hopSpriteS = sprites[DIRECTION_S];
        hopSpriteSW = sprites[DIRECTION_SW];
        hopSpriteNW = sprites[DIRECTION_NW];
        scaleSize = board.getTileHeight()*2f / hopSpriteN.getRegionHeight();
    }

    public void setIndicatorSprite(FilmStrip indicator, Board board) {
        moveIndicator = indicator;
        indicatorScaleSize = board.getTileHeight()/ moveIndicator.getRegionHeight();
    }


    /**
     * Sets the image texture for this enemy
     *
     * param value the image texture for this enemy
     */
    public void setFilmStrip() {
        hopSprite = hopSpriteS;
        hopSprite.setFrame(STAND_FRAME);
    }

    public boolean isMoving() {return isMoving;}

    public void setIsMoving(boolean b) {isMoving = b;}


    public void startMoving(Vector2 tilePos, Board board) {
        Vector2 screenCoord = board.boardToScreen((int) tilePos.x, (int) tilePos.y);
        Vector2 currArrCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
        Vector2 arrCoord = board.boardToArray((int) tilePos.x, (int) tilePos.y);
        HexTile currTile = board.getTileArrayCoord((int) currArrCoord.x, (int) currArrCoord.y);
        HexTile tile = board.getTileArrayCoord((int) arrCoord.x, (int) arrCoord.y);
        hasMoved = false;

        xOffset = screenCoord.x - position.x;
        yOffset = (screenCoord.y - board.getTileHeight()/4f) - position.y;
        isMoving = true;
        if (xOffset < 1 && xOffset > -1){
            if (yOffset <0) {
                hopSprite = hopSpriteS;
            }
            else if (yOffset >0) {
                hopSprite = hopSpriteN;
            }
        }
        else if (xOffset < 0) {
            if (yOffset <0) {
                hopSprite = hopSpriteSW;
            }
            else if (yOffset >0) {
                hopSprite = hopSpriteNW;
            }
        }
        else if (xOffset > 0) {
            if (yOffset <0) {
                hopSprite = hopSpriteSE;
            }
            else if (yOffset >0) {
                hopSprite = hopSpriteNE;
            }
        }
    }

    public void updateMoving(Vector2 nextPos, Board board) {
        if (hopTimer != 0) {
            if (hopTimer % (HOPPING_TIME/HOP_FRAMES.length) == 0 && hopTimer!= HOPPING_TIME) {
                currFrame++;
            }
            hopSprite.setFrame(HOP_FRAMES[currFrame]);
            hopTimer--;

            if(hopSprite.getFrame()>= 6 && hopSprite.getFrame()<= 11) {
                position.x += xOffset/MOVING_FRAMES;
                position.y += yOffset/MOVING_FRAMES + HOP_OFFSETS[hopSprite.getFrame()]*board.getTileHeight()*0.05f;
            }
            if (!hasMoved && hopTimer== 9){
                hasMoved = true;
                needsRemoval = true;
                Vector2 currArrCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
                HexTile currTile = board.getTileArrayCoord((int) currArrCoord.x, (int) currArrCoord.y);
                Vector2 newArrCoord = board.boardToArray((int) nextPos.x, (int) nextPos.y);
                HexTile newTile = board.getTileArrayCoord((int) newArrCoord.x, (int) newArrCoord.y);
                if (currTile.hasEnemy() && currTile.getEnemies().contains(this, true)){
                    currTile.removeEnemies(this);
                }
                setTilePosition(new Vector2(nextPos.x, nextPos.y));
                newTile.addEnemy(this);
            }
        }
        else {
//            currTilePos.setEnemy(null);
//            nextTilePos.setEnemy(this);
//            setTilePosition(nextPos);
            isMoving = false;
            hasMoved = false;
            hopSprite.setFrame(STAND_FRAME);
            xOffset = 0;
            yOffset = 0;
            currFrame = 0;
            hopTimer = HOPPING_TIME;
        }
    }

    /**
     * Returns the type of this object.
     *
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    @Override
    public ObjectType getType() {
        return ObjectType.Enemy;
    }

    public Vector2 getTilePosition() { return  tilePosition; }

    public HexTile getTargetFoodTile(){
        return targetFoodTile;
    }

    public boolean getFoodChasing(){
        return isFoodChasing;
    }

    public void setFoodChasing(boolean chase){
        isFoodChasing = chase;
    }

    public Queue<Vector2> getPath(){
        return path;
    }

    public void setPath(Queue<Vector2> newPath){
        path = newPath;
        resetTimer();
    }

    public boolean getTruckChasing(){
        return isChasingTruck;
    }

    public void setTruckChasing(boolean b) {
        isChasingTruck = b;
    }

    public void setPosition(Vector2 tilePos, Board board) {
        Vector2 currArrCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
        HexTile currTile = board.getTileArrayCoord((int) currArrCoord.x, (int) currArrCoord.y);
        Vector2 newArrCoord = board.boardToArray((int) tilePos.x, (int) tilePos.y);
        HexTile newTile = board.getTileArrayCoord((int) newArrCoord.x, (int) newArrCoord.y);
        currTile.removeEnemies(this);
        newTile.addEnemy(this);
        setTilePosition(tilePos);
        position.x = newTile.getX();
        position.y = newTile.getY() - board.getTileHeight()/4;
    }

    public void setCooltimeScale(int i) {
        if (cooltimeScale != i) {
            cooltimeScale = i;
            resetTimer();
        }
    }

    public void setTilePosition(Vector2 v) {
        tilePosition = v;
    }

    public void setTargetFoodTile(HexTile v) {
        targetFoodTile = v;
    }

    public void setCoolTime(int value) {coolTime = value;}

    public void setAboutToTruckChase(int secLeft) {
        aboutToChaseTruckTimer = secLeft; //ABOUT_TO_TRUCK_CHASE;
        ABOUT_TO_TRUCK_CHASE = secLeft / 5;
        color = Color.RED;
    }

    public void unsetAboutToTruckChase() {
        aboutToChaseTruckTimer = UNSET_TRUCK_CHASE;
        color = Color.WHITE;
        moveIndicator.setFrame(0);
    }

    public void resetTimer() {
        timer = coolTime + (COOL_TIME * cooltimeScale);
        ENEMY_TIMER = timer * 6;
    }

    public Array<HexTile> filterPossiblePathTiles(Array<HexTile> tiles) {
        Array<HexTile> res = new Array<>();
        for (HexTile h : tiles) {
            // ??later: prevent enemies overlapping path (tile contain enemy that has the tile in its path)
            if (!h.isObstacle() && !h.hasEnemy() && !h.getIsExit() && !h.equals(targetFoodTile)) {
                res.add(h);
            }
        }
        return res;
    }

    public Array<HexTile> filterPossiblePathTiles(Array<HexTile> tiles, HexTile currEnemyTile) {
        Array<HexTile> res = new Array<>();
        for (HexTile h : tiles) {
            // ??later: prevent enemies overlapping path (tile contain enemy that has the tile in its path)
            if (h.isObstacle() || h.hasEnemy() || h.getIsExit() || h.equals(targetFoodTile) || h.equals(currEnemyTile)) {
                tiles.removeValue(h, true);
            } else {
                res.add(h);
            }
        }
        return res;
    }

    public boolean haveObstacleInPath(Board board) {
        Vector2 currPosArrayCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
        HexTile currPosTile = board.getTileArrayCoord((int) currPosArrayCoord.x, (int) currPosArrayCoord.y);
        for (Vector2 v : path) {
            if (currPosTile.isObstacle() || currPosTile.hasEnemy()) {
                return true;
            }
        }
        return false;
    }

    public Queue<Vector2> findNewSafePath(Board board) {
        Random random = new Random();

        Array<HexTile> adjTilesBoardCoord = board.getNeighbors((int) tilePosition.x, (int) tilePosition.y);
        adjTilesBoardCoord = filterPossiblePathTiles(adjTilesBoardCoord);

        // if no adjacent tiles available
        if (adjTilesBoardCoord.size < 1) {
            return null;
        }

        Queue<Vector2> newPath = new Queue<>();
        // add first direction
        HexTile fstTile = adjTilesBoardCoord.get(random.nextInt(adjTilesBoardCoord.size));
        Vector2 fstDir = new Vector2(fstTile.getQ() - tilePosition.x, fstTile.getR() - tilePosition.y);
        newPath.addLast(fstDir);

        Vector2 currEnemyBoardCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
        HexTile currEnemyTile = board.getTileArrayCoord((int) currEnemyBoardCoord.x, (int) currEnemyBoardCoord.y);
        Array<HexTile> sndAdjTilesBoardCoord = filterPossiblePathTiles(board.getNeighbors(fstTile), currEnemyTile);
        if (sndAdjTilesBoardCoord.size > 0){
            HexTile sndTile = sndAdjTilesBoardCoord.get(random.nextInt(sndAdjTilesBoardCoord.size));
            Vector2 sndDir = new Vector2(sndTile.getQ() - fstTile.getQ(), sndTile.getR() - fstTile.getR());
            newPath.addLast(sndDir);

            Vector2 revSndDir = new Vector2(-sndDir.x, -sndDir.y);
            newPath.addLast(revSndDir);
        }
        Vector2 revfstDir = new Vector2(-fstDir.x, -fstDir.y);
        newPath.addLast(revfstDir);

        return newPath;
    }

    public Queue<Vector2> findNewSafePath(Board board, HexTile next) {
        Random random = new Random();
        Queue<Vector2> newPath = new Queue<>();
        // add first direction
        Vector2 fstDir = new Vector2(next.getQ() - tilePosition.x, next.getR() - tilePosition.y);
        newPath.addLast(fstDir);

        Vector2 currEnemyBoardCoord = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
        HexTile currEnemyTile = board.getTileArrayCoord((int) currEnemyBoardCoord.x, (int) currEnemyBoardCoord.y);
        Array<HexTile> adjTilesBoardCoord = filterPossiblePathTiles(board.getNeighbors(next), currEnemyTile);
        if (adjTilesBoardCoord.size > 0){
            HexTile sndTile = adjTilesBoardCoord.get(random.nextInt(adjTilesBoardCoord.size));
            Vector2 sndDir = new Vector2(sndTile.getQ() - next.getQ(), sndTile.getR() - next.getR());
            newPath.addLast(sndDir);

            Vector2 revSndDir = new Vector2(-sndDir.x, -sndDir.y);
            newPath.addLast(revSndDir);
        }
        Vector2 revfstDir = new Vector2(-fstDir.x, -fstDir.y);
        newPath.addLast(revfstDir);

        return newPath;
    }


    public void updateEnemy(float delta, Board board) {
        super.update(delta);
        if (isMoving){
            Vector2 nextPos;
            if (path.size > 0){
                nextPos = new Vector2(tilePosition.x + path.get(0).x,tilePosition.y + path.get(0).y);
            } else {
                nextPos = new Vector2(tilePosition.x,tilePosition.y);
            }
            updateMoving(nextPos,board);
            if (hasMoved && needsRemoval){
                if (!(isChasingTruck || isFoodChasing)) {
                    path.addLast(path.get(0));
                }
                path.removeFirst();
                needsRemoval = false;
            }
            if (aboutToChaseTruckTimer >= 0) {
                aboutToChaseTruckTimer--;
            }
        }
        else {
            timer--;
            if (aboutToChaseTruckTimer >= 0) {
                aboutToChaseTruckTimer--;
            } else {
                color = Color.WHITE;
            }
            if (timer <= 0 && path.size > 0) {
                if (!isFoodChasing) {
                    if (aboutToChaseTruckTimer <= 0) { // jump to next tile in path when timer is up
                        Vector2 nextMove = path.get(0);
//                        path.removeFirst();
//                        Vector2 arrayPosition = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
//                        board.getTileArrayCoord((int) arrayPosition.x, (int) arrayPosition.y).removeEnemy();
                        Vector2 tilePos = new Vector2(tilePosition.x + nextMove.x, tilePosition.y + nextMove.y);
                        if (board.isSafeAtBoard(tilePos.x, tilePos.y)) {
                            startMoving(tilePos, board);
//                            Vector2 newArrayPosition = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
//                            HexTile currTile = board.getTileArrayCoord((int) newArrayPosition.x, (int) newArrayPosition.y);
//                            currTile.setEnemy(this);
//                        } else {
//                            tilePosition = new Vector2(tilePosition.x - nextMove.x, tilePosition.y - nextMove.y);
                        }
//                        path.addLast(nextMove);
                    }
                } else {
                    // When going back to patrolling, reset path if there are obstacles in its original path
                    if (haveObstacleInPath(board)){
                        path = findNewSafePath(board);
                    }
                    // update position to near the food
                    Vector2 currArrPos = board.boardToArray((int) tilePosition.x, (int) tilePosition.y);
                    Vector2 foodPos = new Vector2(targetFoodTile.getAX(), targetFoodTile.getAY());
                    if (!board.isAdjacent(currArrPos, foodPos)){
                        Array<HexTile> hpath = board.findBestPathBFSEnemyFree(board.getTileArrayCoord((int) currArrPos.x, (int) currArrPos.y),
                                board.getTileArrayCoord((int) foodPos.x, (int) foodPos.y));
                        if (hpath.size > 1 && board.isSafeAtBoard(hpath.get(1).getQ(), hpath.get(1).getR())) {
                            startMoving(new Vector2(hpath.get(1).getQ(), hpath.get(1).getR()), board);
                            path.addFirst(new Vector2(hpath.get(1).getQ()-tilePosition.x,
                                    hpath.get(1).getR()-tilePosition.y));
                        }
                    }
                }
                resetTimer();
            }
        }
    }

    public void draw(GameCanvas canvas) {
        if (hopSprite == null) {
            return;
        }
        if (aboutToChaseTruckTimer > 0 && (aboutToChaseTruckTimer / ABOUT_TO_TRUCK_CHASE) % 2 == 1) {
            color = Color.WHITE;
        } else if (aboutToChaseTruckTimer > 0 && (aboutToChaseTruckTimer / ABOUT_TO_TRUCK_CHASE) % 2 == 0) {
            color = Color.RED;
        }
        if (aboutToChaseTruckTimer > 0) {
            if (aboutToChaseTruckTimer % 3 == 0) {
                if (moveIndicator.getFrame() == moveIndicator.getSize()-1) {
                    moveIndicator.setFrame(0);
                }
                else {
                    moveIndicator.setFrame(moveIndicator.getFrame()+1);
                }
            }
            float ix = moveIndicator.getRegionWidth()/2.0f;
            float iy = moveIndicator.getRegionWidth()/10.0f;
            canvas.draw(moveIndicator, Color.WHITE, ix, iy, position.x, position.y+hopSprite.getRegionHeight()*scaleSize*0.75f,
                    0.0f, indicatorScaleSize, indicatorScaleSize);
        }
        float x = hopSprite.getRegionWidth()/2.0f;
        float y = hopSprite.getRegionWidth()/10.0f;
        canvas.draw(hopSprite, color, x, y, position.x, position.y , 0.0f, scaleSize, scaleSize);
    }


}
