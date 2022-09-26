package edu.cornell.gdiac.optimize.map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.GameObject;

public class Obstacle extends GameObject {
    private float width;
    private float height;
    private Texture texture;

    /**
     * Constructor fo obstacle
     */
    public Obstacle(float x, float y, float width, float height){
        position = new Vector2(x,y);
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the type of this object.
     *
     * @return the type of this object.
     */
    public ObjectType getType() {
        return ObjectType.Obstacle;
    }

    /**
     * Returns the width of this object.
     *
     * @return the width of this object.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the height of this object.
     *
     * @return the height of this object.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns the texture of this object.
     *
     * @return the texture of this object.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture of this object
     *
     * @param tex texture of this object
     */
    public void setTexture(Texture tex) {
        texture = tex;
    }

    /**
     * Sets the width of this object.
     *
     * @param w width of this object
     */
    public void setWidth(float w) {
        width = w;
    }

    /**
     * Sets the height of this object.
     *
     * @param h height of this object
     */
    public void setHeight(float h) {
        height = h;
    }

    /**
     * Draws this object to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void draw(GameCanvas canvas){
        canvas.draw(texture, Color.WHITE, width/2f,height, position.x,position.y,width,height*2);
    }
}
