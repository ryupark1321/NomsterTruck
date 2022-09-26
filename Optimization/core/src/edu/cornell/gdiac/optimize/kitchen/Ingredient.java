package edu.cornell.gdiac.optimize.kitchen;

import edu.cornell.gdiac.optimize.*;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.util.*;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;

public class Ingredient extends GameObject {

    // MAGIC NUMBERS
    public static final int RECTANGLE_INGREDIENT = 0;
    public static final int CIRCLE_INGREDIENT = 1;
    public static final float ING_DIAMETER = 70f;

    private Texture normalTexture;
    private Texture highlightTexture;
    private Texture texture;

    // ATTRIBUTES
//    boolean isAssembled;
    private int ingredientType;
    private boolean isValid;
    private boolean isCooked;
    private boolean isTrashable;
    private float width;
    private float height;
    private boolean isAssembled;
    private boolean isIncCard;
    private Texture ingCardTexture;
    private String cook;
    private String ingName; // name to refer to when getting texture

    /**
     * Initialize an ingredient with trivial starting position.
     *
//     * @param i Type of the ingredient
     */
    public Ingredient(String name, String cook, float width, float height) {
        ingName = name;
//        if (cook != "") {
//            ingName = name + cook.substring(0, 1).toUpperCase() + cook.substring(1);
//        }
        this.cook = cook;
        setStatus(cook);
        this.width = width;
        this.height = height;
        isAssembled = false;
        isIncCard = false;
    }

    public String getIngName() {
        return ingName;
    }

    public String getCook() {
        return cook;
    }

    /**
     *
     * Set is assembled
     *
     * @param value whether this ingredient is moved to the assemble plate
     */
    public void setAssembled(boolean value) {
        isAssembled = value;
    }

    /**
     *
     * Get is assembled
     *
     * @return whether this ingredient is moved to the assemble plate
     */
    public boolean getAssembled() {
        return isAssembled;
    }

    public float getWidth() {return width;}

    public float getHeight() {return height;}
    /**
     * Returns the type of this object.
     *
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    public ObjectType getType() {
        return ObjectType.Ingredient;
    }

    /**
     * Returns the type of this ingredient.
     *
     * We use this instead of runtime-typing for performance reasons.
     *
     * @return the type of this object.
     */
    public int getIngredientType() {
        return ingredientType;
    }


    /**
     * Covert the type of the ingredient.
     *
     * @param i integer type of the ingredient
     */
    public void convertIngredientType(int i) {
//        if (i == POTATO_ING) {
//            ingredientType = POTATO_ING;
//        } else if (i == CARROT_ING){
//            ingredientType = CARROT_ING;
//        } else if (i == POTATO_CHOP) {
//            ingredientType = POTATO_CHOP;
//        } else if (i == CARROT_CHOP) {
//            ingredientType = CARROT_CHOP;
//        } else if (i == POTATO_STOVE) {
//            ingredientType = POTATO_STOVE;
//        } else if (i == CARROT_STOVE) {
//            ingredientType = CARROT_STOVE;
//        } else if (i == POTATO_FRY) {
//            ingredientType = POTATO_FRY;
//        } else if (i == CARROT_FRY) {
//            ingredientType = CARROT_FRY;
//        }
    }

    /**
     * Set the cook and the initial valid status of the ingredient.
     */
    public void setStatus(String cook) {
        if (cook == "") {
            isCooked = false;
            isValid = true;
            isTrashable = false;
        } else {
            isCooked = true;
            isValid = false;
            isTrashable = true;
        }
    }

    public boolean isValid() { return isValid; }

    public void setValid(boolean value) {isValid = value;}

    public void setTextures(Texture t, Texture th){
        normalTexture = t;
        highlightTexture = th;
        this.texture = normalTexture;
//        setTexture(normalTexture);
    }

    public Ingredient clone(){
        Ingredient ig = new Ingredient(ingName, cook,width, height);
        ig.setX(getX());
        ig.setY(getY());
        ig.setTextures(normalTexture,highlightTexture);
        return ig;
    }

    public void setHighlight(){
        texture = highlightTexture;
    }

    public void setNormal(){
        texture = normalTexture;
    }

    public boolean isCooked() {return isCooked;}

    public void setIsIncCard() {
        isIncCard = true;
        setTexture(ingCardTexture);
    }

    public void setIncCardTexture(Texture tIC) {
        ingCardTexture = tIC;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public boolean equals(Object obj) {
        Ingredient ingredient = (Ingredient) obj;
        return ingredient.ingName.equals(ingName);
    }

    /**
     * Updates the age and angle of this star.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(float delta) {
        // Call superclass's update
        super.update(delta);
        // update isAssembled
//        // if moved to assemble plate, destroy
//        if (isAssembled) {
//            destroyed = true;
//        }

    }


    /**
     * Draws this object to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
//    public void draw(GameCanvas canvas) {
//        float x = animator.getRegionWidth()/2.0f;
//        float y = animator.getRegionHeight()/2.0f;
//        canvas.draw(animator, Color.WHITE, x, y, position.x, position.y, 0.0f, 0.25f, 0.25f);
//    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, position.x-width/2, position.y-height/2, width, height);
    }

    public void drawSize(GameCanvas canvas, float px, float py, float sw, float sh) {
        canvas.draw(texture, px , py, sw, sh);
    }


    /**
     * Draws this object to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void drawScale(GameCanvas canvas, float px, float py) {
        float x = animator.getRegionWidth()/2.0f;
        float y = animator.getRegionHeight()/2.0f;
        canvas.draw(animator, Color.WHITE, x, y, px, py, 0.0f, 0.15f, 0.15f);
    }

}
