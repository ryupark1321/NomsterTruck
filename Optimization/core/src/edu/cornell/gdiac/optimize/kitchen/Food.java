package edu.cornell.gdiac.optimize.kitchen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.optimize.*;
import edu.cornell.gdiac.optimize.GameCanvas;

import java.util.ArrayList;

/**
 * Model class for recipe
 */
public class Food extends GameObject{

    /** Order in which the ingredients are put in the recipe */
    private ArrayList<Ingredient> assembleIngredients;
    private ArrayList<String> assembleAppliances;
    /** Boolean list that keeps track of which ingredients are cleared */
    private ArrayList<Boolean> checkedIngredients;
    /** Whether this food is a recipe or not */
    private boolean isRecipe;
    /** Whether this food is throwable or not */
    private boolean isThrowable;
    /** Number of movement the recipe has */
    private int movementNum;
    /** Number of turns the recipe has */
    private int turnNum;

    /** Texture of this food */
    private Texture texture;

    private ArrayList<Ingredient> processingIngredients;

    /**
     * Initialize assembled food with ingredients
     */
    public Food(ArrayList<Ingredient> ingredients, ArrayList<String> apps){
        //isThrowable = false;
        //isRecipe = false;
        assembleIngredients = new ArrayList<>(ingredients.size());
        assembleAppliances = new ArrayList<>(ingredients.size());
        //checkedIngredients = new ArrayList<>(ingredients.length);
        for (Ingredient ig : ingredients){
            assembleIngredients.add(ig);
            //checkedIngredients.add(false);
        }
        for (String app : apps) {
            assembleAppliances.add(app);
        }
    }

    /**
     * Initialize recipe with ingredients
     */
    public Food(ArrayList<Ingredient> ings, ArrayList<String> apps, int movementNum, int turnNum, boolean isThrowable){
        //this.isRecipe = isRecipe;
        this.movementNum = movementNum;
        this.turnNum = turnNum;
        this.isThrowable = isThrowable;
        assembleIngredients = new ArrayList<>(ings.size());
        assembleAppliances = new ArrayList<>(ings.size());
        //checkedIngredients = new ArrayList<>(ingredients.length);
        for (Ingredient ig : ings){
            assembleIngredients.add(ig);
        }
        for (String app : apps) {
            assembleAppliances.add(app);
        }
    }

    public void addIngredients(ArrayList<Ingredient> igList){
        for (Ingredient ig : igList){
            assembleIngredients.add(ig);
        }
    }

    public void addAppliances(ArrayList<String> apList){
        for (String ap : apList){
            assembleAppliances.add(ap);
        }
    }

    /**
     * Returns whether this food is recipe or not
     *
     * @return true if this food is a recipe
     */
    public boolean isRecipe(){
        return isRecipe;
    }

    public int getTotalActions() {
        if (!isThrowable) {return movementNum + turnNum;}
        else {return movementNum + turnNum + 1;}
    }

    public int getMovementNum() {return movementNum;}

    public int getTurnNum() {return turnNum;}

    /**
     * Returns whether this food is throwable or not
     *
     * @return true if this food is throwable
     */
    public boolean isThrowable(){
        return isThrowable;
    }

    public ArrayList<Ingredient> getAssembleIngredients() {return assembleIngredients;}
    public ArrayList<String> getAssembleAppliances() {return assembleAppliances;}

//    /**
//     * Randomizes the ingredients
//     */
//    public void randomize(Ingredient[] ingredients, int isThrowable){
//        assemblyOrder.clear();
//        flush();
//        for (Ingredient ig : ingredients){
//            assemblyOrder.add(ig);
//        }
//        this.isThrowable = (isThrowable == 1);
//    }

    /**
     * Returns the length of the recipe
     *
     * @return the length of the recipe.
     */
    public int getSize(){
        return assembleIngredients.size();
    }

    /**
     * Gets texture of this food
     */
    public Texture getTexture(){
        return texture;
    }


    /**
     * Returns the type of the object
     *
     * @return the type of the object
     */
    @Override
    public ObjectType getType() {
        if (isRecipe) {
            return ObjectType.Recipe;
        }
        return ObjectType.Food;
    }

    public Food clone() {
        Food temp = new Food(assembleIngredients, assembleAppliances, movementNum, turnNum,isThrowable);
        temp.setTexture(texture);
        temp.setX(getX());
        temp.setY(getY());
        return temp;
    }

//    /**
//     * Checks the ingredient at given index to true.
//     *
//     * @return the index that needs to be cleared next
//     */
//    public int setIndexToTrue(int idx){
//        checkedIngredients.set(idx,true);
//        return idx+1;
//    }

    /**
     * Sets texture of this food
     */
    public void setTexture(Texture texture){
        this.texture = texture;
    }

//    /**
//     * Unchecks all the ingredients in the recipe
//     */
//    public void flush() {
//        int idx = 0;
//        while (idx < assemblyOrder.size() && checkedIngredients.get(idx)) {
//            checkedIngredients.set(idx,false);
//            idx++;
//        }
//    }

//    /**
//     * Compares the ingredient at given index in recipe with input ingredient.
//     *
//     * @return whether the ingredient matches the ingredient in the recipe at given index
//     */
//    public boolean compareRecipe(Ingredient ingredient, int idx){
//        if (idx >= assemblyOrder.size()){
//            return false;
//        }
//        return ingredient.equals(assemblyOrder.get(idx));
//    }

    public boolean isEqual(Food food) {
        if (getSize() != food.getSize()) {
            return false;
        }
        for (Ingredient igCur : assembleIngredients) {
            for (String appCur : assembleAppliances){
                boolean result = false;
                for (Ingredient igGiven : food.getAssembleIngredients()) {
                    for (String appGiven : food.getAssembleAppliances()){
                        if (igCur.equals(igGiven) && appCur.equals(appGiven)) {
                            result = true;
                        }
                    }
                }
                if (result == false) {
                    return false;
                }
            }
        }
        return true;
    }

//    /**
//     * Draws this object to the canvas
//     *
//     * There is only one drawing pass in this application, so you can draw the objects
//     * in any order.
//     *
//     * @param canvas The drawing context
//     */
//    public void draw(GameCanvas canvas) {
//        for (int i = 0; i < assemblyOrder.size(); i++){
//            if (!checkedIngredients.get(i)){
//                assemblyOrder.get(i).drawScale(canvas);
//            }
//        }
//    }
}
