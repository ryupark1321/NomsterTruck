package edu.cornell.gdiac.optimize.kitchen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.optimize.*;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.ArrayList;

public class Appliance extends GameObject {

    /** The constants for the appliances */
    // Chopping Board
    private static final float CHOPPINGBOARD_WIDTH_RATIO = 53/384f;
    private String type;
    private float CHOPPINGBOARD_WIDTH;
    private static final float CHOPPINGBOARD_HEIGHT_RATIO = 1/5f;
    private float CHOPPINGBOARD_HEIGHT;
    private static final float CHOPPINGBOARD_X_RATIO = 427/1245f;
    private float CHOPPINGBOARD_X;
    private static final float CHOPPINGBOARD_Y_RATIO = 1/5f;
    private float CHOPPINGBOARD_Y;

    // Stove
    private static final float STOVE_WIDTH_RATIO = 11/96f;
    private float STOVE_WIDTH;
    private static final float STOVE_HEIGHT_RATIO = 1/4f;
    private float STOVE_HEIGHT;
    private static final float STOVE_X_RATIO = 371/768f;
    private float STOVE_X;
    private static final float STOVE_Y_RATIO = 1/5f;
    private float STOVE_Y;

    // Fryer
    private static final float FRYER_WIDTH_RATIO = 103/768f;
    private float FRYER_WIDTH;
    private static final float FRYER_HEIGHT_RATIO = 23/108f;
    private float FRYER_HEIGHT;
    private static final float FRYER_X_RATIO = 29/48f;
    private float FRYER_X;
    private static final float FRYER_Y_RATIO = 1/5f;
    private float FRYER_Y;

    // Plate
    private static final float PLATE_WIDTH_RATIO = 7/64f;
    private float PLATE_WIDTH;
    private static final float PLATE_HEIGHT_RATIO = 19/108f;
    private float PLATE_HEIGHT;
    private static final float PLATE_X_RATIO = 3/4f;
    private float PLATE_X;
    private static final float PLATE_Y_RATIO = 49/216f;
    private float PLATE_Y;

    // Trash
    private static final float TRASH_WIDTH_RATIO = 95/768f;
    private float TRASH_WIDTH;
    private static final float TRASH_HEIGHT_RATIO = 97/432f;
    private float TRASH_HEIGHT;
    private static final float TRASH_X_RATIO = 335/384f;
    private float TRASH_X;
    private static final float TRASH_Y_RATIO = 49/216f;
    private float TRASH_Y;


    // MAGIC NUMBERS
    public static final int CHOPPINGBOARD = 0; //click event
    public static final int POT = 1; //progress bar
    public static final int FRYER = 2; //progress bar
    public static final int TRASH = 4;
    public static final int PLATE = 3;

    public static final float CHOP_CLICK = 4f;
    public static final float STOVE_PROGRESS = 40f;// 180;
    public static final float FRYER_PROGRESS = 40f; //240;
    public static final int POT_TIME = 60;
    public static final int FRYER_TIME = 60;
    public static final int CHOP_TIME = 20;
    public static final int PLATE_TIME = 32;
    public static final int FRAME_TIME = 3;
    public int cookingTimer;
    public int totalTimer;

    public static final int CHOP_SIZE = 10;
    public static final int POT_SIZE = 6;
    public static final int FRYER_SIZE = 6;
    public static final int PLATE_SIZE = 4;

    private static final int NOT_COOKING = -1;
    private static final int POTATO_ING = 0;
    private static final int CARROT_ING = 1;

    public static float APPLIANCE_DIAMETER;
    public static Vector2 choppingPos;
    public static Vector2 stovePos;
    public static Vector2 fryerPos;
    public static Vector2 platePos;

    private int doneProcessed;

    /** In order of: WidthRatio, HeightRatio, XRation, YRation, progressEvent (0-notClick, 1-click), progressAmount
     * Convert progressEvent and progressAmount to int when in setting.
     *
     */
    private static float[] chopSetupInfo = {CHOPPINGBOARD_WIDTH_RATIO, CHOPPINGBOARD_HEIGHT_RATIO, CHOPPINGBOARD_X_RATIO, CHOPPINGBOARD_Y_RATIO, 1f, CHOP_CLICK};
    private static float[] potSetupInfo = {STOVE_WIDTH_RATIO, STOVE_HEIGHT_RATIO, STOVE_X_RATIO, STOVE_Y_RATIO, 0f, STOVE_PROGRESS};
    private static float[] frySetupInfo = {FRYER_WIDTH_RATIO, FRYER_HEIGHT_RATIO, FRYER_X_RATIO, FRYER_Y_RATIO, 0f, FRYER_PROGRESS};

    private static float[] trashSetupInfo = {TRASH_WIDTH_RATIO, TRASH_HEIGHT_RATIO, TRASH_X_RATIO, TRASH_Y_RATIO};
    private static float[] plateSetupInfo = {PLATE_WIDTH_RATIO, PLATE_HEIGHT_RATIO, PLATE_X_RATIO, PLATE_Y_RATIO};
    private static float[][] applianceSetupInfo = {chopSetupInfo, potSetupInfo, frySetupInfo, plateSetupInfo, trashSetupInfo};
    // ATTRIBUTES

//    /** The list of ingredient int type that the appliance can accept */
//    private ArrayList<Integer> ingredientTypes;
    /** Whether this appliance can cook */
    private boolean isCook;
    /** The index type of the appliance */
//    private int applianceType;
    /** Whether the appliance is working */
    private boolean isWorking = false;
    /** the total progress of appliance */
    private int totalProgress;
    /** the current progress of appliance */
    private int currProgress;
    /** whether the appliance has a click event (progress event) */
    private boolean isClick;
    /** The int type of the ingredient when cooking */
    private int cookingIng = NOT_COOKING;
    /** Type of this appliance */
    private int appType;
    private Texture texture;
    private Texture normalTexture;
    private Texture highlightTexture;
    private boolean containsFood = false;

    private Texture ingCardTexture;
    private boolean  isIncCard;
    private ArrayList<Texture> activeTextures;

    private float width;
    private float height;

    private float scaleSize;

    private boolean isAnimating;

    private FilmStrip workingSprite;
    private int frameTime;

    public static ArrayList<Vector2> getapplianceConstants(){
        ArrayList<Vector2> tbReturned = new ArrayList<>();
        tbReturned.add(new Vector2(CHOPPINGBOARD_X_RATIO,CHOPPINGBOARD_Y_RATIO));
        tbReturned.add(new Vector2(CHOPPINGBOARD_WIDTH_RATIO,CHOPPINGBOARD_HEIGHT_RATIO));
        tbReturned.add(new Vector2(STOVE_X_RATIO,STOVE_Y_RATIO));
        tbReturned.add(new Vector2(STOVE_WIDTH_RATIO,STOVE_HEIGHT_RATIO));
        tbReturned.add(new Vector2(FRYER_X_RATIO,FRYER_Y_RATIO));
        tbReturned.add(new Vector2(FRYER_WIDTH_RATIO,FRYER_HEIGHT_RATIO));
        tbReturned.add(new Vector2(PLATE_X_RATIO,PLATE_Y_RATIO));
        tbReturned.add(new Vector2(PLATE_WIDTH_RATIO,PLATE_HEIGHT_RATIO));
        tbReturned.add(new Vector2(TRASH_X_RATIO,TRASH_Y_RATIO));
        tbReturned.add(new Vector2(TRASH_WIDTH_RATIO,TRASH_HEIGHT_RATIO));
        return tbReturned;
    }

    public void setSprite(FilmStrip[] sprites) {
        if (appType == CHOPPINGBOARD){
            workingSprite = sprites[0];
            scaleSize = height/(workingSprite.getRegionHeight()*3);
            scaleSize = (133*height)/(160*workingSprite.getRegionHeight());
        }
        else if (appType == POT) {
            workingSprite = sprites[1];
            scaleSize = (3*height)/(2*workingSprite.getRegionHeight());
        }
        else if (appType == FRYER) {
            workingSprite = sprites[2];
            scaleSize = (3*height)/(2*workingSprite.getRegionHeight());
        }
        else if (appType == PLATE) {
            workingSprite = sprites[3];
            scaleSize = (width)/(workingSprite.getRegionWidth());
        }
        if (appType != TRASH) {
            workingSprite.setFrame(0);
        }
    }

    /**
     * Returns the type of this object.
     *
     * @return the type of this object.
     */
    public ObjectType getType() {
        return ObjectType.Appliance;
    }

    public boolean getContainsFood() { return containsFood; }
    public void setContainsFood(boolean b) { containsFood = b; }

    public int appIntTypeConverter(String type) {
        if (type == "chop") {
            return 0;
        } else if (type == "pot") {
            return 1;
        } else if (type == "fry") {
            return 2;
        } else if (type == "plate") {
            return 3;
        } else if (type == "trash") {
            return 4;
        }
        return -1; // should never reach this
    }

    public boolean isCookingApp() {
        return appType < 3;
    }

    /**
     * Initialize an ingredient with trivial starting position.
     *
//     * @param i integer type of the appliance
//     * @param types The list of ingredient int type that the appliance can accept
     */
    public Appliance(String type,  float canvasWidth, float canvasHeight) {
        appType = appIntTypeConverter(type);
        float[] appInfo = applianceSetupInfo[appType];
        this.width = canvasWidth * appInfo[0];
        this.height = canvasHeight * appInfo[1];
        this.type = type;
        position.x = canvasWidth * appInfo[2];
        position.y = canvasHeight * appInfo[3]; // -canvasHeight/18f; //-HEIGHT_OFFSET
        isCook = isCookingApp();
        isIncCard = false;

        doneProcessed = NOT_COOKING;
        if (isCook) {
            isClick = (appInfo[4] == 1f);
            totalProgress = (int) appInfo[5];
            currProgress = totalProgress;
        }
        setTimer();
        workingSprite = null;
        isAnimating = false;
//        ingredientTypes = types;
        isWorking = false; // cookable
        cookingIng = NOT_COOKING;
        setProgress(); // cookable
        currProgress = totalProgress; // cookable
    }

    public void setTimer() {
        if (appType == CHOPPINGBOARD){
            cookingTimer = CHOP_TIME;
            frameTime = FRAME_TIME;
        }
        else if (appType == POT) {
            cookingTimer = POT_TIME;
            frameTime = 5;
        }
        else if (appType == FRYER) {
            cookingTimer = FRYER_TIME;
            frameTime = 5;
        }
        else if (appType == PLATE) {
            cookingTimer = PLATE_TIME;
            frameTime = 8;
        }
        else {
            frameTime = 1;
        }
        totalTimer = cookingTimer;
    }

    public void setIsIncCard() {
        isIncCard = true;
        setTexture(ingCardTexture);
    }

    public float getWidth() {return width;}

    public float getHeight() {return height;}

    public static void setApplianceDiameter(float d){
        APPLIANCE_DIAMETER = d;
    }

    public static void setStovePosition(float x, float y){
        stovePos = new Vector2(x, y);
    }

    public static void setChoppingPosition(float x, float y){
        choppingPos = new Vector2(x, y);
    }

    public static void setFryerPosition(float x, float y){
        fryerPos = new Vector2(x, y);
    }

    public static void setPlatePosition(float x, float y){
        platePos = new Vector2(x, y);
    }

    /**
     * Returns the int type of appliance
     *
     * @return the int type of appliance
     */
    public int getApplianceType() {return appType;}

    public String getApplianceStringType(){
        return type;
    }

    /**
     * Returns the current progress of appliance
     *
     * @return the current progress of appliance
     */
    public int getCurrProgress() {return currProgress;}

    public void resetCurrProgress() {currProgress = 0;}

    /**
     * Returns the total progress of appliance
     *
     * @return the total progress of appliance
     */
    public int getTotalProgress() {return totalProgress;}

    /**
     * Returns whether the appliance is working
     *
     * @return whether the appliance is working
     */
    public boolean isWorking() {return isWorking;}

    /**
     * Returns whether the appliance has a click event
     *
     * @return whether the appliance has a click event
     */
    public boolean isClickable() {return isClick;}


    public void setClickProgress() {
        currProgress = currProgress -1;
        if (isWorking) {
            isAnimating = true;
        }
    }

    /**
     * Set the appliance to working status
     *
     * @param i integer type of the ingredient
     */
    public void setWorking(int i) {
        isWorking = true;
        cookingIng = i;
        isAnimating = true;
        texture = activeTextures.get(i);
    }


    public void setNotCooking(){
        isWorking = false;
        cookingIng = NOT_COOKING;
        currProgress = totalProgress;
        texture = normalTexture;
    }

    public void setTextures(Texture t, Texture th, Texture tIC){
        normalTexture = t;
        highlightTexture = th;
        ingCardTexture = tIC;
        texture = normalTexture;
    }

    public void setActiveTexture(Texture[] at){
        activeTextures = new ArrayList<>();
        for (int i = 0; i < at.length; i++)
            activeTextures.add(at[i]);
    }

    public void setHighlight(){
        texture = highlightTexture;
    }

    public void setNormal(){
        texture = normalTexture;
    }

    /**
     * Returns The int type of the ingredient
     *
     * @return The int type of the ingredient when cooking
     */
    public int getCookingIng() {return cookingIng;}

//    /**
//     * Returns whether the ingredient can be cooked in the appliance
//     *
//     * @param i the ingredient to be put in the appliance
//     * @return whether the ingredient can be cooked in the appliance
//     */
//    public boolean isAcceptable(int i) {
//        for (int type : ingredientTypes) {
//            if (type == i) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Returns whether this appliance is a cooking appliance
     *
     * @return whether this appliance is a cooking appliance
     */
    public boolean getIsCook() {
        return isCook;
    }

    public int getDoneProcessed(){
        return doneProcessed;
    }

    public void resetDoneProcessed(){
        doneProcessed = NOT_COOKING;
    }

    /**
     * Initialize the total progress for each appliance.
     */
    public void setProgress() {
//        if (applianceType == CHOPPINGBOARD) {
//            isClick = true;
//            totalProgress = CHOP_CLICK;
//        }
//        else if (applianceType == STOVE) {
//            isClick = false;
//            totalProgress = STOVE_PROGRESS;
//        }
//        else if (applianceType == FRYER) {
//            isClick = false;
//            totalProgress = FRYER_PROGRESS;
//        }
    }

    /**
     * Updates the age and angle of this star.
     */
    public void update() {
        if (isWorking) {
            if (currProgress > 0 && !isClick) {
                currProgress --;
            }
            else if (currProgress == 0){
                isWorking = false;
                doneProcessed = cookingIng;
                cookingIng = NOT_COOKING;
                currProgress = totalProgress;
            }
        }
        if (isAnimating) {
            updateAnimation();
        }
    }

    public void updateAnimation() {
        if (cookingTimer !=0 && isAnimating && (isWorking || appType == PLATE)) {
            if (cookingTimer % frameTime == 0 && cookingTimer!= totalTimer) {
                if (workingSprite!= null && workingSprite.getFrame() == workingSprite.getSize()-1) {
                    workingSprite.setFrame(0);
                }
                else if (workingSprite!= null){
                    workingSprite.setFrame(workingSprite.getFrame()+1);
                }
            }
            cookingTimer--;
        }
        else{
            setTimer();
            isAnimating = false;
            if (appType != TRASH) {
                workingSprite.setFrame(0);
            }
        }
    }

    public boolean isAnimating() {return isAnimating;}
    public void setAnimating() {
        isAnimating = true;
    }


    public void updatePlateAnimation() {

    }

    public Appliance clone(){
        Appliance ap = new Appliance(type,width,height);
        ap.setTextures(normalTexture,highlightTexture,ingCardTexture);
        ap.setActiveTexture(activeTextures.toArray(new Texture[]{}));
        return ap;
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, position.x, position.y, width, height);
    }

    public void draw(GameCanvas canvas, Color color) {
        canvas.draw(texture, position.x, position.y, width, height, color);
    }

    public void drawSize(GameCanvas canvas, float px, float py, float sw) {
        canvas.draw(getTexture(), px , py, sw, sw * getTexture().getHeight() / getTexture().getWidth());
    }

    public void drawSize(GameCanvas canvas, float px, float py, float sw, float sh) {
        canvas.draw(texture, px , py, sw, sh);
    }

    public void drawObj(GameCanvas canvas, Color color) {
        float x = animator.getRegionWidth()/2.0f;
        float y = animator.getRegionHeight()/2.0f;
        canvas.draw(animator, color, x, y, position.x, position.y, 0.0f, 1f, 1f);
    }

    public void drawAnimation(GameCanvas canvas) {
        if (workingSprite == null) {
            return;
        }
        float x = workingSprite.getRegionWidth()/2.0f;
        float y = workingSprite.getRegionWidth()/2.0f;
        if (appType == CHOPPINGBOARD){
            canvas.draw(workingSprite, Color.WHITE, x, y, position.x+width/2, position.y+height , 0.0f, scaleSize, scaleSize);

        }
        else {
            canvas.draw(workingSprite, Color.WHITE, x, y, position.x+width/2, position.y+height/2 , 0.0f, scaleSize, scaleSize);
        }
    }
}
