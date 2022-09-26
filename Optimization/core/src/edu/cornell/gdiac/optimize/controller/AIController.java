package edu.cornell.gdiac.optimize.controller;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.entity.Enemy;
import edu.cornell.gdiac.optimize.controller.AIController;
import edu.cornell.gdiac.optimize.map.Board;
import edu.cornell.gdiac.optimize.map.HexTile;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.HashMap;
import java.util.Random;

public class AIController {
    /** The list of currently active enemies' board coordinates */
    private Array<Enemy> enemies;
    /** The list of potential "will be chasing sth" enemies */
    private Array<Enemy> enemiesInFoodRadius;

    public static final int DIRECTION_N= 0;
    public static final int DIRECTION_NE = 1;
    public static final int DIRECTION_SE = 2;
    public static final int DIRECTION_S = 3;
    public static final int DIRECTION_SW = 4;
    public static final int DIRECTION_NW = 5;

    private static final int HOP_COLS = 4;
    private static final int HOP_ROWS = 3;
    private static final int HOP_SIZE = 12;

    private Texture hopTextureN;
    private Texture hopTextureNE;
    private Texture hopTextureSE;
    private Texture hopTextureS;
    private Texture hopTextureSW;
    private Texture hopTextureNW;
    private Texture moveIndicator;

    public AIController(Array<Enemy> e){
        enemies = e;
        enemiesInFoodRadius = null;
    }

    /**
     * Populates this mode from the given the directory.
     *
     * The asset directory is a dictionary that maps string keys to assets.
     * Assets can include images, sounds, and fonts (and more). This
     * method delegates to the gameplay controller
     *
     * @param directory 	Reference to the asset directory.
     */
    public void populate(AssetDirectory directory) {
        hopTextureN = directory.getEntry("enemyHopN",Texture.class);
        hopTextureNE = directory.getEntry("enemyHopNE",Texture.class);
        hopTextureSE = directory.getEntry("enemyHopSE",Texture.class);
        hopTextureS = directory.getEntry("enemyHopS",Texture.class);
        hopTextureSW = directory.getEntry("enemyHopSW",Texture.class);
        hopTextureNW = directory.getEntry("enemyHopNW",Texture.class);
        moveIndicator = directory.getEntry("moveIndicator",Texture.class);
    }


    public Array<Enemy> getEnemies(){
        return enemies;
    }

    public Array<Enemy> getEnemiesInFoodRadius(){
        return enemiesInFoodRadius;
    }

    public void setEnemies(Array<Enemy> enemies){
        this.enemies = enemies;
    }

    public void setEnemiesInFoodRadius(Array<Enemy> enemies){
        enemiesInFoodRadius = enemies;
    }

    public void addEnemies(Enemy enemy, Board board){
        enemies.add(enemy);
        for (Enemy o : enemies) {
            o.setHopSprites(new FilmStrip[]{new FilmStrip(hopTextureN,HOP_ROWS,HOP_COLS,HOP_SIZE),
                    new FilmStrip(hopTextureNE,HOP_ROWS,HOP_COLS,HOP_SIZE),
                    new FilmStrip(hopTextureSE,HOP_ROWS,HOP_COLS,HOP_SIZE),
                    new FilmStrip(hopTextureS,HOP_ROWS,HOP_COLS,HOP_SIZE),
                    new FilmStrip(hopTextureSW,HOP_ROWS,HOP_COLS,HOP_SIZE),
                    new FilmStrip(hopTextureNW,HOP_ROWS,HOP_COLS,HOP_SIZE)}, board);
            o.setFilmStrip();
            o.setIndicatorSprite(new FilmStrip(moveIndicator, 2,7,11),board);
        }
    }

    public void updateFoodChaseState(HexTile foodTile){
        for (Enemy e : enemiesInFoodRadius){
            if (!e.getFoodChasing()){
                e.setFoodChasing(true);
                e.setTargetFoodTile(foodTile);
            }
        }
    }

    /** Updates the board ??Is this doc right
     *
     * @param currentLoc current location to find enemies in range from
     * @param board current game board
     * @param dst number of tiles in the radius of the range
     * @return The enemy list with which the enemies are in dst number of tiles apart */
    public Array<Enemy> findEnemies(Vector2 currentLoc, Board board, int dst){
        Queue<Vector2> queue = new Queue<>();
        Array<Enemy> res = new Array<>();
        HashMap<Vector2, Boolean> visited = new HashMap<>();
        HashMap<Vector2, Integer> distanceKeeper = new HashMap<>();
        visited.put(currentLoc,true);
        distanceKeeper.put(currentLoc,0);
        for (Vector2 tile : board.adjTiles((int) currentLoc.x, (int) currentLoc.y)) {
            queue.addLast(tile);
            distanceKeeper.put(tile,1);
        }
        while (!queue.isEmpty()){
            Vector2 node = queue.removeFirst();
            if (!visited.containsKey(node)) {
                if (dst > distanceKeeper.get(node)) {
                    if (node.x < board.getNumCols() && node.y < board.getNumRows()) { // Why do we need this condition?
                        Array<Enemy> targets = board.getBoard()[(int) node.x][(int) node.y].getEnemies();
                        if (!targets.isEmpty() && !targets.get(0).getFoodChasing()) {
                            res.addAll(targets);
                        }
                        for (Vector2 tile : board.adjTiles((int) node.x, (int) node.y)) {
                            queue.addLast(tile);
                            if (!distanceKeeper.containsKey(tile) || distanceKeeper.get(tile) > distanceKeeper.get(node) + 1) {
                                distanceKeeper.put(tile, distanceKeeper.get(node) + 1);
                            }
                        }
                    }
                } else if (dst == distanceKeeper.get(node)) {
                    Array<Enemy> targets = board.getBoard()[(int) node.x][(int) node.y].getEnemies();
                    if (!targets.isEmpty() && !targets.get(0).getFoodChasing()) {
                        res.addAll(targets);
                    }
                }
                visited.put(node, true);
            }
        }
        return res;
    }

    public void updateFoodChaseStateToFalse(){
        for (Enemy e : enemies){
            if (e.getFoodChasing() && !e.getTargetFoodTile().hasFood()){
                e.setFoodChasing(false);
                e.setTargetFoodTile(null);
            }
        }
    }

    public void setEnemiesAboutToChaseTruck(int secLeft){
        for (Enemy e : enemies){
            if (!e.getFoodChasing()){
                e.setAboutToTruckChase(secLeft);
            }
        }
    }

    public void unsetEnemiesAboutToChaseTruck(){
        for (Enemy e : enemies){
            e.unsetAboutToTruckChase();
        }
    }


    /**
     * Updates the position and path of enemies when the tile timer is up for the player
     *
     * @param board The current game board
     * @param playerLoc The current location of the player in board coordinate
     */
    public void updateTileTimerEnemyMoveIn(Board board, Vector2 playerLoc) {
        Vector2 playerTileArrayCoord = board.boardToArray((int) playerLoc.x, (int) playerLoc.y);
        HexTile playerTile = board.getTileArrayCoord((int) playerTileArrayCoord.x, (int) playerTileArrayCoord.y);

        for (Enemy e : enemies) {
            if (!e.isMoving() && !e.getFoodChasing()){// find path to player and move one time closer
                Vector2 enemyTileArrayCoord = board.boardToArray((int) e.getTilePosition().x, (int) e.getTilePosition().y);
                HexTile enemyTile = board.getTileArrayCoord((int) enemyTileArrayCoord.x, (int) enemyTileArrayCoord.y);
                Array<HexTile> path = board.findBestPathBFS(enemyTile, playerTile);

                // update enemy position when there exists a path to the player
                if (path.size > 1) {
                    e.startMoving(new Vector2(path.get(1).getQ(), path.get(1).getR()), board);
                    // update patrolling path if necessary
                    if (e.haveObstacleInPath(board)) {
                        Queue<Vector2> newPath;
                        Random rand = new Random();
                        int pathSizeLimit = rand.nextInt(4);
//                        if (path.size > 2 + pathSizeLimit && !path.get(1).isObstacle() && !path.get(1).hasEnemy()) {
                        newPath = e.findNewSafePath(board, path.get(1));
//                            System.out.println("Me1");
//                        } else {
//                            newPath = e.findNewSafePath(board);
////                            System.out.println("Me2");
//                        }
//                        System.out.println("------");
//                        for (Vector2 v : newPath){
//                            System.out.println(v);
//                        }
//                        System.out.println("------");
                        e.setPath(newPath);
                    }
                }
            }
        }

    }

    public boolean allEnemiesNotMoving() {
        for (Enemy e : enemies) {
            if (e.isMoving()) {
                return false;
            }
        }
        return true;
    }
}
