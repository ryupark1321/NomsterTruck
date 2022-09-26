package edu.cornell.gdiac.optimize.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.GameObject;
import edu.cornell.gdiac.optimize.controller.RandomController;
import edu.cornell.gdiac.optimize.entity.Enemy;
import edu.cornell.gdiac.optimize.kitchen.Food;
import edu.cornell.gdiac.optimize.playmode.GameMode;

import java.util.HashMap;
import java.util.Random;

public class Board {

    // Instance attributes
    /** The board width (in number of tiles) */
    private float width;
    /** The board height (in number of tiles) */
    private float height;
    /** The obstacle indices */
    private Array<Vector2> foodTiles;
    /** Constants to find neighbors of a tile*/
    private final int[][] neighbors = {{1,0},{0,1},{-1,0},{0,-1},{1,-1},{-1,1}};
    /** Selected (red) tiles*/
    private Array<HexTile> selectedPath;
    /** Array to store the gameboard*/
    private HexTile[][] gameBoard;
    /** Keeps track of width */
    private float tileWidth;
    /** Board position of the exit tile in board coordinate system */
    private Vector2 exitTile;
    /** Keeps track of height */
    private float tileHeight;
    /** Keeps track of number of rows */
    private int numRows;
    /** Keeps track of number of cols */
    private int numCols;
    /** Middle portion of the status background (grey region) */
    private TextureRegion statusBkgMiddle;
    /** Middle portion of the status forground (colored region) */
    private TextureRegion statusFrgMiddle;

    /**
     * Creates a new hexagonal tile board of the given size with exit position e
     *
     * @param nCols number of columns
     * @param nRows number of rows
     * @param regionWidth width of region to fill
     * @param regionHeight height of region to fill
     * @param e exit position
     */
    public Board(int nCols, int nRows, float regionWidth, float regionHeight, Vector2 e) {
        if (nCols % 0.5 != 0 || nRows % 0.5 != 0){
            Gdx.app.error("GameBoard","Invalid number of rows / columns", new IllegalArgumentException());
        }
        gameBoard = new HexTile[nCols][nRows];
//        float defaultHeight = regionHeight/5f;
//        float defaultWidth = defaultHeight*393/155f;
        float defaultWidth = regionWidth / ((3f*12 + 1)/4f + 1/2f);
        float defaultHeight = defaultWidth*155/393f;
        float xOffset;
        float yOffset;

        if (nCols <= 12 && nRows < 5){
            tileWidth = defaultWidth;
            tileHeight = defaultHeight;
            xOffset = (regionWidth - tileWidth*(nCols*3 + 1)/4f)/2f + tileWidth/2f;
            yOffset = regionHeight*(10/3f*8/18f) + tileHeight/2f;
        } else if (nCols > 12) {
            tileWidth = regionWidth / ((3f*nCols + 1)/4f + 1/2f);
            tileHeight = regionHeight/nRows;
            xOffset = tileWidth*3/4f;
            yOffset = regionHeight*(10/3f*8/18f) + tileHeight/2f;
        } else {
            tileHeight = regionHeight/nRows;
            tileWidth = tileHeight*393/155f;
            xOffset = (regionWidth-tileWidth*nCols*0.75f - tileWidth/4f)/2f;
            yOffset = regionHeight*(10/3f*8/18f) + tileHeight/2f;
        }
        int tileColor;
        for (int i = 0; i < nCols; i++){
            for (int j = 0; j < nRows; j++){
                tileColor = RandomController.rollInt(0, 100) % 3;
                gameBoard[i][j] = new HexTile(i, j - (int) Math.floor(i/2), tileWidth, tileHeight, xOffset, yOffset, tileColor);
            }
        }
        numCols = nCols;
        numRows = nRows;
        width = regionWidth;
        height = regionHeight;
        selectedPath = new Array<>();
        exitTile = e;
        foodTiles = new Array<>();
        Vector2 exitArrayCoord = boardToArray((int) e.x, (int) e.y);
        gameBoard[(int) exitArrayCoord.x][(int) exitArrayCoord.y].setIsExit(true);
    }

    /**
     * Get the exit tile coordinate in board coordinate system.
     *
     * @return exit tile coordinate.
     */
    public Vector2 getExitTile() {
        return exitTile;
    }

    /**
     * Returns an array of all neighbors adjacent to the tile at (q,r).
     *
     * @param q q coordinate of tile
     * @param r r coordinate of tile
     *
     * @return Array of size 0..6 containing all adjacent tiles.
     */
    public Array<HexTile> getNeighbors(int q, int r){
        int x = q;
        int y = r - (int) Math.floor(q/2);
        Array<HexTile> result = new Array<>();
        for (int[] offsets : neighbors){
            int oq = q + offsets[0];
            int or = r + offsets[1];
            int ox = oq;
            int oy = or + (int) Math.floor(oq/2);
            if (ox >= 0
                    && ox < gameBoard.length
                    && oy >= 0
                    && oy < gameBoard[0].length){
                if (gameBoard[ox][oy] != null){
                    result.add(gameBoard[ox][oy]);
                }
            }
        }
        return result;
    }

    public void setProgressTextures(TextureRegion bkg, TextureRegion frg) {
        statusBkgMiddle = bkg;
        statusFrgMiddle = frg;
    }

    /**
     * Returns an array of all neighbors adjacent to the tile t.
     *
     * @param t tile
     *
     * @return Array of size 0..6 containing all adjacent tiles.
     */
    public Array<HexTile> getNeighbors(HexTile t){
        return getNeighbors(t.getQ(), t.getR());
    }

    /**
     * Returns the number of tiles horizontally across the board.
     *
     * @return the number of tiles horizontally across the board.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Returns the number of tiles horizontally across the board.
     *
     * @return the number of tiles horizontally across the board.
     */
    public HexTile[][] getBoard() {
        return gameBoard;
    }

    /**
     * Returns the number of tiles vertically across the board.
     *
     * @return the number of tiles vertically across the board.
     */
    public int getNumRows() {
        return numRows;
    }

    /** Method to get tile  */
    public HexTile getTileArrayCoord(int c, int r){
        return gameBoard[c][r];
    }


    /** Method to get tile width */
    public float getTileWidth(){
        return gameBoard[0][0].getWidth();
    }

    /** Method to get tile height  */
    public float getTileHeight(){
        return gameBoard[0][0].getHeight();
    }

    /**
     * Returns the size of the tile texture.
     *
     * @return the size of the tile texture.
     */
    public Vector2 getTileSize() {
        return new Vector2(tileWidth, tileHeight);
    }

    /**
     * Returns the array of food tiles on the board
     *
     * @return the array of food tiles on the board
     */
    public Array<Vector2> getFoodTiles() {return foodTiles; }

    /**
     * Sets tile texture to {texture}
     *
     * @param texture the tile texture
     */
    public void setTileTexture(Texture texture, Texture obsTexture){
        for (HexTile[] col : gameBoard){
            for (HexTile tile : col){
                if (tile != null){
                    if (tile.isObstacle()){
                        tile.setTexture(obsTexture);
                    } else {
                        tile.setTexture(texture);
                    }
                }
            }
        }
    }

    /**
     * Sets tile font to {font}
     *
     * @param font the tile font
     */
    public void setTileFont(BitmapFont font){
        for (HexTile[] col : gameBoard){
            for (HexTile tile : col){
                if (tile != null){
                    tile.setFont(font);
                }
            }
        }
    }

    /**
     * Creates an obstacle on tile that is nearest to screen position (x,y)
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     * @param playerTile the tile on which the player is
     */
    public void setObstacle(float x, float y, Vector2 playerTile) {
        HexTile selectTile = findNearestTile(x, Gdx.graphics.getHeight() - y);

        // not player tile
        if (playerTile.x != selectTile.getQ() || playerTile.y != selectTile.getR()) {
            if (!selectTile.getIsExit()) {
                selectTile.setObstacle(true);
            }
        }
    }

    /**
     * Creates an obstacle on tile that is nearest to screen position (x,y)
     *
     * @param q board coordinate q
     * @param r board coordinate r
     * @param playerTile the tile on which the player is
     */
    public void setObstacle(int q, int r, Vector2 playerTile, Texture t) {
        Vector2 arrayCoord = boardToArray(q, r);
        HexTile selectTile = getTileArrayCoord((int) arrayCoord.x, (int) arrayCoord.y);
        selectTile.setObstacle(true);
        if (t == null){
            selectTile.setTexture(null);
        }
    }

    /**
     * Helper function for findNearestTile
     */
    private boolean inTile(HexTile tile, float x, float y){
        boolean intile = x >= tile.getX() - tileWidth/2f && x < tile.getX() + tileWidth/2f
                && y < tile.getY() + tileHeight/2f && y >= tile.getY() - tileHeight/2f;
        y -= (tile.getY() - tileHeight/2f);
        boolean bleft = (x >= tile.getX() - tileWidth/2f && x < tile.getX() - tileWidth/4f)
                && (y > tileHeight/2f + 2*tileHeight/tileWidth*(x - tile.getX() + tileWidth/2f)
                || y < tileHeight/2f - 2*tileHeight/tileWidth*(x - tile.getX() + tileWidth/2f));
        boolean bright = (x >= tile.getX() + tileWidth/4f && x < tile.getX() + tileWidth/2f)
                && (y > tileHeight/2f - 2*tileHeight/tileWidth*(x - tile.getX() - tileWidth/2f)
                || y < tileHeight/2f + 2*tileHeight/tileWidth*(x - tile.getX() - tileWidth/2f));
        return intile && !bleft && !bright;
    }

    /**
     * Finds the nearest tile to screen coordinates (x,y)
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     *
     * @return Nearest tile to (x, y)
     */
    public HexTile findNearestTile(float x, float y){
        HexTile bestTile = null;
        for (HexTile[] col : gameBoard){
            for (HexTile tile : col){
                if (inTile(tile,x,y)) {
                    bestTile = tile;
                    return bestTile;
                }
            }
        }
        return bestTile;
    }

    // Region Conversion
    /**
     * Finds the board coordinates from given array coordinates.
     *
     * @return board coordinates
     */
    public Vector2 arrayToBoard(int x, int y){
        return new Vector2(x,y - (int) Math.floor(x/2));
    }

    /**
     * Finds the array coordinates from given board coordinates.
     *
     * @return array coordinates
     */
    public Vector2 boardToArray(int q, int r){
        return new Vector2(q,r + (int) Math.floor(q/2));
    }

    /**
     * Finds the array coordinates from screen coordinates.
     *
     * @return array coordinates
     */
    public Vector2 screenToArray(float x, float y){
        HexTile target = findNearestTile(x,y); //Gdx.graphics.getHeight() - y - Y_OFFSET
        return boardToArray(target.getQ(),target.getR());
    }

    /**
     * Finds the screen coordinates from array coordinates.
     *
     * @return screen coordinates
     */
    public Vector2 arrayToScreen(int x, int y){
        HexTile targetTile = gameBoard[x][y];
        return new Vector2(targetTile.getX(), targetTile.getY());
    }

    /**
     * Finds the board coordinates from screen coordinates.
     *
     * @return board coordinates
     */
    public Vector2 screenToBoard(float x, float y){
        HexTile target = findNearestTile(x,y); //Gdx.graphics.getHeight() - y - Y_OFFSET
        return new Vector2(target.getQ(), target.getR());
    }

    /**
     * Finds the screen coordinates from board coordinates.
     *
     * @return board coordinates
     */
    public Vector2 boardToScreen(int x, int y){
        Vector2 tilePos = boardToArray(x,y);
        HexTile targetTile = gameBoard[(int)tilePos.x][(int)tilePos.y];
        return new Vector2(targetTile.getX(), targetTile.getY());
    }
    // #EndRegion

    /** Marks all tiles as unvisited for next update loop */
    public void clearVisited(){
        for (HexTile[] col : gameBoard){
            for (HexTile tile : col){
                if (tile != null){
                    tile.setVisited(false);
                }
            }
        }
    }

    /**
     * Returns the best path between tiles start and end
     *
     * @param start start tile
     * @param end end tile
     *
     * @return the array of tiles on the shortest path from start to end
     */
    public Array<HexTile> findBestPathBFS(HexTile start, HexTile end){
        Queue<Array<HexTile>> potentialPaths = new Queue<>();
        clearVisited();

        start.setVisited(true);

        Array<HexTile> path = new Array<>();
        path.add(start);
        potentialPaths.addLast(path);

        while (!potentialPaths.isEmpty()){
            path = potentialPaths.removeFirst();
            HexTile node = path.peek();
            if (node.equals(end)){
                return path;
            }
            for (HexTile t : getNeighbors(node.getQ(), node.getR())){
                if (!t.isVisited() && !t.isObstacle()){
                    t.setVisited(true);
                    Array<HexTile> newPath = new Array<HexTile>(path);
                    newPath.add(t);
                    potentialPaths.addLast(newPath);
                }
            }
        }
        return new Array<>();
    }


    /**
     * Returns the best path between tiles start and end enemy free adjacent to target
     *
     * @param start start tile
     * @param end end tile
     *
     * @return the array of tiles on the shortest path from start to end
     */
    public Array<HexTile> findBestPathBFSEnemyFree(HexTile start, HexTile end){
        Queue<Array<HexTile>> potentialPaths = new Queue<>();
        clearVisited();

        int idx = 0;

        start.setVisited(true);

        Array<HexTile> path = new Array<>();
        path.add(start);
        potentialPaths.addLast(path);

        while (!potentialPaths.isEmpty()){
            path = potentialPaths.removeFirst();
            HexTile node = path.peek();
            if (node == end){
                return path;
            }
            for (HexTile t : getNeighbors(node.getQ(), node.getR())){
                if (!t.isVisited() && !t.isObstacle() && (!t.hasEnemy() || idx > 0)){
                    t.setVisited(true);
                    Array<HexTile> newPath = new Array<HexTile>(path);
                    newPath.add(t);
                    potentialPaths.addLast(newPath);
                }
            }
            idx++;
        }
        return new Array<>();
    }

    /**
     * Returns the best path between tiles start and end enemy free adjacent to target
     *
     * @param start start tile
     * @param end end tile
     *
     * @return the array of tiles on the shortest path from start to end
     */
    public Array<HexTile> BFSPath(HexTile start, HexTile end){
        Queue<Array<HexTile>> potentialPaths = new Queue<>();

//        int idx = 0;

        start.setVisited(true);

        Array<HexTile> path = new Array<>();
        path.add(start);
        potentialPaths.addLast(path);

        while (!potentialPaths.isEmpty()){
            path = potentialPaths.removeFirst();
            HexTile node = path.peek();
            if (node == end){
                return path;
            }
            for (HexTile t : getNeighbors(node.getQ(), node.getR())){
                if (!t.isVisited() && !t.isObstacle() && !t.hasEnemy()){
                    t.setVisited(true);
                    Array<HexTile> newPath = new Array<HexTile>(path);
                    newPath.add(t);
                    potentialPaths.addLast(newPath);
                }
            }
        }
        return new Array<>();
    }

    /**
     * Returns the array with all adjacent tiles of the tile on the board in index form.
     *
     * @param x x array coordinate
     * @param y y array coordinate
     *
     * @return the array of tiles that are adjacent to tile on (x,y)
     */
    public Array<Vector2> adjTiles(int x, int y) {
        Array<Vector2> result = new Array<>();
        HexTile tile = gameBoard[x][y];
        Array<HexTile> adjTiles = getNeighbors(tile.getQ(),tile.getR());
        for (HexTile target : adjTiles){
            result.add(boardToArray(target.getQ(),target.getR()));
        }
        return result;
    }

    /**
     * Returns if the two array coordinates are adjacent to one another
     *
     * @param a first array coordinate
     * @param b second array coordinate
     *
     * @return true iff first and second are adjacent
     */
    public boolean isAdjacent(Vector2 a, Vector2 b){
        Vector2 boardACoord = arrayToBoard((int) a.x, (int) a.y);
        Array<HexTile> neighborsA = getNeighbors((int) boardACoord.x, (int) boardACoord.y);
        for (HexTile t : neighborsA){
            if (t.equals(gameBoard[(int) b.x][(int) b.y])){
                return true;
            }
        }
        return false;
    }

    // Drawing information
    /**
     * Returns true if a screen location is safe (i.e. there is a tile there)
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a screen location is safe
     */
    public boolean isSafeAtScreen(float x, float y, float canvasH) {
//        Vector2 boardCoord = screenToArray(x,y);
//        int bx = (int) boardCoord.x;
//        int by = (int) boardCoord.y;
//        return bx >= 0 && by >= 0
//                && bx < width
//                && by < height;
        y = canvasH - y;
        boolean res = x >= 0 && x <= width;
        res &= y >= height*1.45 && y <= canvasH;
        return res;
    }

    /**
     * Returns true if a board location is safe (i.e. there is a tile there)
     *
     * @param q The q value in board coordinates
     * @param r The r value in board coordinates
     *
     * @return true if a board location is safe
     */
    public boolean isSafeAtBoard(float q, float r) {
        Vector2 arrayCoord = boardToArray((int) q, (int) r);
        float x = arrayCoord.x;
        float y = arrayCoord.y;
        return x >= 0 && y >= 0 && x < numCols && y < numRows;
    }

    /** Removes all obstacles */
    public void clearObstacles(){
        for (HexTile[] hexRow : gameBoard){
            for (HexTile hex : hexRow){
                if (hex.isObstacle()){
                    hex.setObstacle(false);
                }
            }
        }
    }

    /** Adds food to array index in aCoord */
    public void addFood(Vector2 aCoord, GameObject recipe){
        HexTile tile = gameBoard[(int) aCoord.x][(int) aCoord.y];
        tile.setFood((Food) recipe);
        foodTiles.add(aCoord);
    }

    /** Removes all enemies */
    public void clearTiles(){
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                gameBoard[j][i].resetEnemies();
                gameBoard[j][i].setHasFood(false);
                gameBoard[j][i].setFood(null);
                gameBoard[j][i].setOnPath(false);
                gameBoard[j][i].setVisited(false);
                gameBoard[j][i].setIsHighlighted(false);
            }
        }
    }

    /** Removes all elements from the board */
    public void reset(){
        clearTiles();
        foodTiles.clear();
        selectedPath.clear();
    }


    /** Unmarks all tiles */
    public void unhighlightTiles(){
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                gameBoard[j][i].setIsHighlighted(false);
            }
        }
    }


    /**
     * Returns whether an obstacle present at position (x, y)
     *
     * @param q x array coordinate of tile
     * @param r y array coordinate of tile
     *
     * @return whether there is an obstacle at position (x, y)
     */
    public boolean isObstacleFree(int q, int r){
        Vector2 arrayCoord = boardToArray(q,r);
        return isSafeAtBoard(q,r) && !gameBoard[(int) arrayCoord.x][(int) arrayCoord.y].isObstacle();
    }

    public void drawProgressBar(GameCanvas canvas, HexTile tile) {
        canvas.draw(statusBkgMiddle, tile.getX()-tileWidth/4, tile.getY() + tileHeight/4f, tileWidth/2, tileHeight/5);
        float currProgress = (float)tile.getFoodOnTileTime() / Enemy.ENEMY_TIMER;
        canvas.draw(statusFrgMiddle,tile.getX()-tileWidth/4,tile.getY() + tileHeight/4f,(tileWidth*currProgress)/2,tileHeight/5);
    }

    public boolean hasFoodOnBoard() {
        if (foodTiles.size != 0){
            return true;
        }
        return false;
    }

    /**
     * Draws the board to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void draw(GameCanvas canvas) {
        Array<HexTile> tilesArranged = new Array<>();
        for (HexTile[] col : gameBoard) {
            for (HexTile tile : col) {
                if (tile != null) {
                    if (tilesArranged.size == 0) {
                        tilesArranged.add(tile);
                    } else {
                        boolean containsTile = false;
                        int idx = 0;
                        while (!containsTile && idx < tilesArranged.size){
                            if (tile.getY() > tilesArranged.get(idx).getY()){
                                tilesArranged.insert(idx,tile);
                                containsTile = true;
                            }
                            idx++;
                        }
//                        for(int i = 0; i<tilesArranged.size; i++) {
//                            if (!containsTile && tile.getY() >= tilesArranged.get(i).getY()) {
//                                tilesArranged.insert(i,tile);
//                                containsTile = true;
//                            }
//                        }
                        if (!containsTile) {
                            tilesArranged.add(tile);
                        }
                    }
                }
            }
        }
        for (HexTile tile : tilesArranged) {
            tile.draw(canvas);
            if(tile.hasFood()) {
                drawProgressBar(canvas,tile);
            }
        }
    }

    /**
     * Marks all tiles within throw radius of the truck
     *
     * @param a array coordinate of the truck
     * @param dst throw radius
     */
    public void highlightTiles(Vector2 a, int dst){
        Queue<Vector2> queue = new Queue<>();
        HashMap<Vector2, Boolean> visited = new HashMap<>();
        HashMap<Vector2, Integer> distanceKeeper = new HashMap<>();
        distanceKeeper.put(a,0);
        queue.addLast(a);
        while (!queue.isEmpty()){
            Vector2 node = queue.removeFirst();
            if (!visited.containsKey(node)){
                if (dst > distanceKeeper.get(node) && node.x < numCols && node.y < numRows) {
                    HexTile target = gameBoard[(int) node.x][(int) node.y];
                    if (!target.isObstacle() && !target.hasEnemy() && !target.hasFood() && node != a){
                        target.setIsHighlighted(true);
                    }
                    for (Vector2 tile : adjTiles((int) node.x, (int) node.y)) {
                        HexTile neiTile = gameBoard[(int) tile.x][(int) tile.y];
                        if (!neiTile.isObstacle() || neiTile.getTexture() == null){
                            queue.addLast(tile);
                            if (!distanceKeeper.containsKey(tile) || distanceKeeper.get(tile) > distanceKeeper.get(node) + 1){
                                distanceKeeper.put(tile,distanceKeeper.get(node) + 1);
                            }
                        }
                    }
                } else if (dst == distanceKeeper.get(node)){
                    HexTile target = gameBoard[(int) node.x][(int) node.y];
                    if (!target.isObstacle() && !target.hasEnemy() && !target.hasFood()){
                        target.setIsHighlighted(true);
                    }
                }
                visited.put(node, true);
            }
        }
        int[] temp = {0,1,1,0,-1,-1};
        Vector2 pos;
        for (int i = 0; i < 6; i++){
            pos = a;
            int yOff;
            boolean isEven;
            int d = 0;
            boolean hasObstacle = false;
            while (pos.x >= 0 && pos.y >= 0 && pos.x < numCols && pos.y < numRows && d <= dst){
                if (gameBoard[(int) pos.x][(int) pos.y].isObstacle() && gameBoard[(int) pos.x][(int) pos.y].getTexture() != null ){
                    hasObstacle = true;
                    gameBoard[(int) pos.x][(int) pos.y].setIsHighlighted(false);
                } else {
                    if (hasObstacle){
                        gameBoard[(int) pos.x][(int) pos.y].setIsHighlighted(false);
                    }
                }
                isEven = pos.x%2 == 0;
                switch (i) {
                    case 0:
                        yOff = 1;
                        break;
                    case 1:
                        yOff = isEven ? 0 : 1;
                        break;
                    case 2:
                        yOff = isEven ? -1 : 0;
                        break;
                    case 3:
                        yOff = -1;
                        break;
                    case 4:
                        yOff = isEven ? -1 : 0;
                        break;
                    default:
                        yOff = isEven ? 0 : 1;
                        break;
                }
                pos = new Vector2(temp[i] + pos.x, yOff + pos.y);
                d++;
            }
        }
    }


    /**
     * Returns the distance between two tiles
     *
     * @param a array coordinate of first tile
     * @param b array coordinate of second tile
     */
    public int dst2Tile(Vector2 a, Vector2 b){
        Array<HexTile> path = findBestPathBFS(gameBoard[(int) a.x][(int) a.y],gameBoard[(int) b.x][(int) b.y]);
        return path.size - 1;
    }

    /**
     * Updates the board
     */
    public void update(){
        for (int i = 0; i < foodTiles.size; i++){
            Vector2 food = foodTiles.get(i);
            if (gameBoard[(int) food.x][(int) food.y].update()){
                foodTiles.removeValue(food, false);
            }
        }
    }
}
