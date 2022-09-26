package edu.cornell.gdiac.optimize.map;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.controller.RandomController;
import edu.cornell.gdiac.optimize.entity.Enemy;
import edu.cornell.gdiac.optimize.entity.Truck;
import edu.cornell.gdiac.optimize.kitchen.Appliance;
import edu.cornell.gdiac.optimize.kitchen.Food;

public class HexTile {
    /** The q(diagonal) coordinate for tile */
    private int q;
    /** The r(vertical) coordinate for tile */
    private int r;
    /** The x coordinate for tile */
    private float x;
    /** The y coordinate for tile */
    private float y;
    /** The width of the tile (Not exact) */
    private float width;
    /** The height of the tile (Not exact) */
    private float height;
    /** The display text on a tile */
    private String textDisplay;
    /** The tile texture */
    private Texture texture;
    /** Font */
    private BitmapFont font;
    /** Whether the tile is highlighted or not */
    private boolean isHighlighted;
    /** Whether the tile is visited or not */
    private boolean visited = false;
    /** Whether the tile is on path or not */
    private boolean onPath = false;
    /** Whether the tile is obstacle or not */
    private boolean obstacle;
    /** Whether the tile is exit tile or not */
    private boolean isExit;
    /** Whether the tile contains an enemy or not */
    private Array<Enemy> enemies;
    /** The tile contains food */
    private Food food;
    /** The tile contains truck */
    private Truck truck;
    /** Whether the tile contains food or not */
    private boolean hasFood;
    /** How long it has been since the food was on the tile */
    private int foodOnTileTime;

    private Color[] tileColors = {new Color(110f/255, 196f/255, 146f/255, 1), new Color(78f/255, 166f/255, 114f/255, 1), new Color(59f/255, 125f/255, 86f/255, 1)};

    private Color color;
    private Color originalColor;

    /**
     * Creates a tile
     *
     * @param q Q coordinate
     * @param r R coordinate
     * @param width Width of this tile
     * @param height Height of this tile
     * @param xOffset offset in x axis of this tile
     * @param yOffset offset in y axis of this tile
     */
    public HexTile(int q, int r, float width, float height, float xOffset, float yOffset, int c){
        this.q = q;
        this.r = r;
        this.width = width;
        this.height = height;
        this.x = width*q*3/4 + xOffset;
        if (q%2 == 0) {
            this.y = height*(q/2+r)+yOffset;
        } else {
            this.y = height*(q/2+r)+height/2+yOffset;
        }
        this.textDisplay = (int) q + ", " + (int) r;
        enemies = new Array<>();
        obstacle = false;
        color = tileColors[c];
        originalColor = color;
    }

    /**
     * Returns the x coordinate of this tile
     *
     * @return x coordinate of this tile
     */
    public float getX(){
        return x;
    }

    /**
     * Returns the y coordinate of this tile
     *
     * @return y coordinate of this tile
     * */
    public float getY(){
        return y;
    }

    /**
     * Returns the x coordinate of this tile
     *
     * @return x coordinate of this tile
     */
    public float getAX(){
        return q;
    }

    /**
     * Returns the y coordinate of this tile
     *
     * @return y coordinate of this tile
     * */
    public float getAY(){
        return r + (int) Math.floor(q/2);
    }

    /**
     * Returns the width of this tile
     *
     * @return width of the tile(not exact)
     */
    public float getWidth(){
        return width;
    }

    /**
     * Returns the height of this tile
     *
     * @return height of the tile(not exact)
     */
    public float getHeight(){
        return height;
    }

    /**
     * Returns whether this tile is the exit
     *
     * @return whether this tile is exit or not
     */
    public boolean getIsExit(){
        return isExit;
    }

    /**
     * Returns the font
     *
     * @return the tile font
     */
    public BitmapFont getFont(){
        return font;
    }
    /**
     * Returns the textDisplay
     *
     * @return the display text
     */
    public String getTextDisplay(){
        return textDisplay;
    }

    /**
     * Returns the enemy on the tile
     *
     * @return enemy
     */
    public Array<Enemy> getEnemies(){
        return enemies;
    }

    /**
     * Returns the texture of the tile
     *
     * @return texture
     */
    public Texture getTexture(){
        return texture;
    }

    /**
     * Returns the obstacle on the tile
     *
     * @return obstacle
     */
    public boolean getObstacle(){
        return obstacle;
    }

    /**
     * Returns the q coordinate of this tile
     *
     * @return q coordinate of this tile
     */
    public int getQ(){
        return q;
    }

    /**
     * Returns the r coordinate of this tile
     *
     * @return r coordinate of this tile
     */
    public int getR(){
        return r;
    }

    /**
     * Add the tile to contain the enemy
     *
     * @param e Enemy on the tile
     */
    public void addEnemy(Enemy e){
        enemies.add(e);
    }

    /**
     * Sets the tile to exit
     *
     * @param b whether the tile is an exit
     */
    public void setIsExit(Boolean b){
        isExit = b;
    }

    /**
     * Highlights the tile
     *
     * @param b whether the tile is highlighted or not
     */
    public void setIsHighlighted(Boolean b){
        isHighlighted = b && !obstacle ;
    }

    /**
     * Marks a tile as on the current BFS path
     *
     * @param v true if this tile is on path
     */
    public void setOnPath(boolean v){
        onPath = v;
    }

    /**
     * Sets a tile as visited for a BFS search
     *
     * @param v true if this tile is visited in bfs
     */
    public void setVisited(boolean v){
        visited = v;
    }

    /**
     * Sets the texture for this tile
     *
     * @param texture texture of the tile
     */
    public void setTexture(Texture texture){
        this.texture = texture;
    }

    /**
     * Sets the food to given boolean
     *
     * @param b whether this tile has food or not
     */
    public void setHasFood(boolean b){
        hasFood = b;
    }

    /**
     * Sets the food to given boolean
     *
     * @param tileFood whether this tile has food or not
     */
    public void setFood(Food tileFood){
        if (tileFood != null){
            foodOnTileTime = Enemy.ENEMY_TIMER;
            hasFood = tileFood != null;
            food = tileFood;
        }
    }

    /**
     * Sets the text font for this tile
     *
     * @param font font for this tile
     */
    public void setFont(BitmapFont font){
        font.setColor(Color.BLACK);
        this.font = font;
    }

    /**
     * Returns whether an enemy is on the tile
     *
     * @return true if an enemy is on tile
     */
    public boolean hasEnemy(){
        return !enemies.isEmpty();
    }

    /**
     * Returns whether food is on the tile
     *
     * @return true if food is on tile
     */
    public boolean hasFood(){
        return hasFood;
    }

    /**
     * Returns whether this tile is marked as on the current BFS path
     *
     * @return true if this tile is marked as on the current BFS path
     */
    public boolean isOnPath(){
        return onPath;
    }

    /**
     * Returns whether this tile is marked as visited for a BFS search
     *
     * @return true if this tile is marked as visited for a BFS search
     */
    public boolean isVisited(){
        return visited;
    }

    /**
     * Returns whether this tile is currently an obstacle
     *
     * @return true if this tile is currently an obstacle
     */
    public boolean isObstacle(){
        return obstacle;
    }

    /**
     * Returns whether this tile is at this position
     *
     * @param pos position
     *
     * @return a boolean on whether this tile is at this position
     */
    public boolean isAt(Vector2 pos) {
        return x == pos.x && y == pos.y;
    }

    /** Removes enemy from the tile */
    public void removeEnemies(Enemy e){
        enemies.removeValue(e, false);
    }

    /** Removes enemy from the tile */
    public void resetEnemies(){
        enemies = new Array<>();
    }

    /** Toggles a tile as an obstacle */
    public void setObstacle(boolean b){
        obstacle = b;
    }


    /**
     * Return the squared distance from (x,y) to the center of this tile
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     *
     * @return the squared distance from (x,y) to the center of this tile
     * */
    public float distance2ToCenter(float x, float y){
        return (float) (Math.pow(((this.x + width/2) - x), 2) + Math.pow(((this.y + height/2 + height/0.346f * 0.654f) - y), 2));
    }

    /**
      * Draws a tile, choosing color based on attributes of the tile.
      *
      * @param canvas Game canvas
      */
    public void draw(GameCanvas canvas){
        if (isExit) {
            color = new Color(61f/255, 97f/255, 155f/255, 1);
        }
        if (isHighlighted && !isExit){
            color = Color.LIME;
        }
        else if(!isHighlighted && !isExit){
            color = originalColor;
        }

        if (hasFood) {
            Texture foodTexture = food.getTexture();
            canvas.draw(texture,color,width/2f,height/2f + height/4f, x,y,width,height/4*5f);
            canvas.draw(foodTexture,Color.WHITE,0,0, x-width/4f,y - height/4f,width/2f,height/2f);
        } else if (!obstacle || texture != null){
            canvas.draw(texture,color,width/2f,height/2f + height/4f, x,y,width,height/4*5f);
        }
    }

    public int getFoodOnTileTime() {return foodOnTileTime;}

    /**
     * Returns whether the given object is this tile
     *
     * @param obj the object of interest
     *
     * @return whether the object is the tile or not
     */
    @Override
    public boolean equals(Object obj){
        if (obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        HexTile objT = (HexTile) obj;
        return (getQ() == objT.getQ() && getR() == objT.getR());
    }

    /**
     * Sets the food to given boolean
     */
    public boolean update(){
        foodOnTileTime--;
        if (foodOnTileTime == 0){
            hasFood = false;
            food = null;
            return true;
        }
        return false;
    }
}