package edu.cornell.gdiac.optimize.controller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.kitchen.*;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.*;

public class KitchenController {
//    /** Variable to keep track of ingredient object that is being dragged */
//    private Ingredient dragIngredient;

    public static float HEIGHT_OFFSET;

    /** Texture for kitchen */
    private Texture kitchenTexture;
    /** Texture for kitchen */
    private Texture kitchenBoard;
    /** Texture for kitchen */
    private Texture kitchenDrawers;
    /** Texture for kitchen */
    private Texture kitchenPanels;

    /** The food image for the game */
    private Texture foodTexture;

    private Texture numberCircle;
//    "bonesRamen" : "images/bones_ramen.png",
//            "basicRamen" : "images/basic_ramen.png",
//            "eyeballRamen" : "images/eyeball_ramen.png",


    private Array<Array<Texture>> processedTextures;

    /** Texture for target select */
    private Texture selectTexture;
    private Texture recipeIngTexture;
    private Texture recipeOneTexture;
    private Texture recipeTwoTexture;
    private Texture recipeThreeTexture;
    private Texture crossRecipeTexture;
    private Texture wrongFoodTexture;
    private Texture rewardForTexture;
    private Texture rewardBackTexture;
    private Texture rewardTurnTexture;
    private Texture rewardThrowTexture;

    private Texture key1;
    private Texture key2;
    private Texture keyT;
    private Texture keyQ;
    private Texture spacebar;
    private Texture[] recipeTextures;
    // Textures for rewards
    private Texture forwardTexture;
    private Texture backwardTexture;
    private Texture turnTexture;
    private Texture throwTexture;
    private Texture closedSignTexture;
    private Texture checkmarkTexture;

    // statusBar is a "texture atlas." Break it up into parts.
    /** Left cap to the status background (grey region) */
    private TextureRegion statusBkgLeft;
    /** Middle portion of the status background (grey region) */
    private TextureRegion statusBkgMiddle;
    /** Right cap to the status background (grey region) */
    private TextureRegion statusBkgRight;
    /** Left cap to the status forground (colored region) */
    private TextureRegion statusFrgLeft;
    /** Middle portion of the status forground (colored region) */
    private TextureRegion statusFrgMiddle;
    /** Right cap to the status forground (colored region) */
    private TextureRegion statusFrgRight;
    /** Width of the rounded cap on left or right */
    private static int PROGRESS_CAP  = 15;

    /** Variable to keep the default recipe */
    private Food defaultRecipe;

    private Appliance choppingBoard;
    private Appliance stove;
    private Appliance fryer;
    private Appliance trashcan;
    private Appliance plate;
    private ArrayList<Appliance> appliances;
    public static ArrayList<Appliance> appliancesList;
    public static String[] basicAppliances = {"chop", "pot", "fry", "plate", "trash"};
    Array<String> rawIngTypesString; // List of ingredients name String
    private ArrayList<Ingredient> ingredients;
    private int selectedIng;
    private ArrayList<Ingredient> rawIngredients;

    // Variable to keep track of the food that is fed! May be erased but here in case we need storing?
    private Food currentFedFood;
    private JsonValue recipesJSON;
    private int currentSelectedCookedIngredient;
    private ArrayList<Food> recipes;
    /** List of indices of the recipes displayed on screen */
    private ArrayList<Integer> displayedRecipesInd;
    private Random rand;
    private float PROGRESSBAR_HEIGHT;

    public static float ING_WIDTH;
    private static float KITCHEN_HEIGHT;
    private static final int RECIPE_OFFSET = 40;
    private static final float RECIPECARD_COLUMN_OFFSET = 11/96f;
    private static float RECIPE_ING_SIZE;

    private Array<Ingredient> doneIngredients;

    private static final int CLICK_OFFSET = 5;

    private static final int NO_SELECT = -1;

    private int trashState;

    private static final int RECIPE_NUM = 3;
    private static final int WRONG_ASSEMBLE_OFF = 0;
    private static final int TRASH_STATE_ON = 8;
    private static final int TRASH_STATE_OFF = 0;
    private static final int PREV_FED_OFF = -1;

    private Texture chopSpriteTexture;
    private Texture potSpriteTexture;
    private Texture fryerSpriteTexture;
    private Texture plateSpriteTexture;

    private static final float ING_Y_RATIO = 1/14f;
    private static final float ING1_X_RATIO = 13/384f;
    private static final float ING2_X_RATIO = 7/32f;
    private static final float ING3_X_RATIO = 23/48f;
    private static final float ING4_X_RATIO = 35/48f;
    private static final float ING5_X_RATIO = 179/192f;
    private static final float[] INGS_X_RATIOS = {ING3_X_RATIO,ING2_X_RATIO,ING4_X_RATIO,ING1_X_RATIO,ING5_X_RATIO};
    private static final Vector2 DRAWER1_RANGE = new Vector2(0,43/384f);

    private static final Vector2 DRAWER2_RANGE = new Vector2(53/384f,23/64f);

    private static final Vector2 DRAWER3_RANGE = new Vector2(25/64f,235/384f);

    private static final Vector2 DRAWER4_RANGE = new Vector2(61/96f,55/64f);

    private static final Vector2 DRAWER5_RATIO = new Vector2(167/192f,1f);


    private int width;
    private int height;

    private Array<Array<Integer>> rewardsNSize;
    private ArrayList<Appliance> appIngCardTextures;
    private ArrayList<Ingredient> ingsIngCardTextures;
    private boolean trash;
    private int trashTarget;
    private int assembleTarget;
    private Array<Ingredient> plateIngredients;
    private boolean feed;
    private HashMap<String, Integer> fedReward;
    private boolean fedSuccess;
    private boolean failedFed;
    private boolean fed;
    private Food fedFood;
    private float soundvolume;

    private JsonValue storedJsonData;
    private int failedFedCountdown;
    private static final int FAILFEDTIME = 120;
    private BitmapFont westSacFont;
    private BitmapFont grande;

    private Sound sizzle;
    private Sound boiling;
    private Sound ding;
    private Sound error;

    /** Completed recipes */
    private ArrayList<Food> completedRecipes;
    private boolean tutorialLevel;

    public Appliance getPlate() {
        return plate;
    }

    public Appliance getStove() {
        return stove;
    }

    public Appliance getChoppingBoard() {
        return choppingBoard;
    }

    public Appliance getFryer() {
        return fryer;
    }

    public Appliance getTrashcan() {
        return trashcan;
    }

    public void setTrashState() {
        trashState = TRASH_STATE_ON;
    }

    public boolean recipesExist(){
        return recipes == null || recipes.size() != 0;
    }

    public KitchenController(int canvasWidth, int canvasHeight, int level){
        width = canvasWidth;
        height = canvasHeight;
        ING_WIDTH = canvasHeight/8f;
        KITCHEN_HEIGHT = canvasHeight * 3/8f;
//        HEIGHT_OFFSET =0;
        PROGRESSBAR_HEIGHT = 1/72f*height;

        appliancesList = appliances;
        selectedIng = NO_SELECT;
        trashState = TRASH_STATE_OFF;
        rawIngredients = new ArrayList<>();
        plateIngredients = new Array<>();
        doneIngredients = new Array<>();
        currentSelectedCookedIngredient = NO_SELECT;
        rand = new Random();
    }

    public void setTutorialLevel(int level) {
        tutorialLevel = level >= 6 && level <= 8;
        if (tutorialLevel) {
            completedRecipes = new ArrayList<>();
        }
    }

    public void loadJSONKitchenData(JsonValue data){
//        applianceNum = Integer.parseInt(data.get("applianceNum").toString().split(" ")[1]);
        rawIngTypesString = new Array<>();
        for (JsonValue ingT : data.get("ingredients").iterator()) {
            rawIngTypesString.add(ingT.toString());
        }
        createIngredients(rawIngTypesString);
//        setRecipeData(data.get("recipes"));
        recipesJSON = data.get("recipes");
    }

    public void setVolume(float sv){
        soundvolume = sv;
    }

    public JsonValue getRecipesJSON() { return recipesJSON; }

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
        RECIPE_ING_SIZE = (height/4 -RECIPE_OFFSET) /3;
        kitchenTexture = directory.getEntry("kitchen", Texture.class);
        kitchenPanels = directory.getEntry("kitchenPanels", Texture.class);
        kitchenDrawers = directory.getEntry("kitchenDrawers", Texture.class);
        kitchenBoard = directory.getEntry("kitchenBoard", Texture.class);
        foodTexture = directory.getEntry("food", Texture.class);
        selectTexture = directory.getEntry("selectTarget", Texture.class);
        numberCircle = directory.getEntry("numberCircle", Texture.class);
        checkmarkTexture = directory.getEntry("checkmark", Texture.class);

        rewardForTexture = directory.getEntry("rewardForward", Texture.class);
//        rewardBackTexture = directory.getEntry("rewardBackward", Texture.class);
        rewardTurnTexture = directory.getEntry("rewardTurn", Texture.class);
        rewardThrowTexture = directory.getEntry("rewardThrow", Texture.class);

        recipeIngTexture = directory.getEntry("recipeIng", Texture.class);
        recipeOneTexture = directory.getEntry("recipe1", Texture.class);
        recipeTwoTexture = directory.getEntry("recipe2", Texture.class);
        recipeThreeTexture = directory.getEntry("recipe3", Texture.class);
        crossRecipeTexture = directory.getEntry("crossRecipe", Texture.class);
        wrongFoodTexture = directory.getEntry("wrongFood", Texture.class);
        westSacFont = directory.getEntry("westSac",BitmapFont.class);
        grande = directory.getEntry("grande", BitmapFont.class);

        key1 = directory.getEntry("1key", Texture.class);
        key2 = directory.getEntry("2key", Texture.class);
        keyT = directory.getEntry("tkey", Texture.class);
        keyQ = directory.getEntry("qkey", Texture.class);
        spacebar = directory.getEntry("spacebar", Texture.class);

        // Break up the status bar texture into regions
        statusBkgLeft = directory.getEntry( "progress.backleft", TextureRegion.class );
        statusBkgRight = directory.getEntry( "progress.backright", TextureRegion.class );
        statusBkgMiddle = directory.getEntry( "progress.background", TextureRegion.class );

        statusFrgLeft = directory.getEntry( "progress.foreleft", TextureRegion.class );
        statusFrgRight = directory.getEntry( "progress.foreright", TextureRegion.class );
        statusFrgMiddle = directory.getEntry( "progress.foreground", TextureRegion.class );

        chopSpriteTexture = directory.getEntry("chopSprite", Texture.class);
        potSpriteTexture = directory.getEntry("potSprite", Texture.class);
        fryerSpriteTexture = directory.getEntry("fryerSprite", Texture.class);
        plateSpriteTexture = directory.getEntry("plateSprite", Texture.class);

        sizzle = directory.getEntry("sizzle", Sound.class);
        boiling = directory.getEntry("boiling", Sound.class);
        ding = directory.getEntry("ding", Sound.class);
        error = directory.getEntry("error", Sound.class);
        closedSignTexture = directory.getEntry("closedSign", Texture.class);

        if (recipesExist()) {
            createAppliances(width, height);
            for (Appliance app : appliances) {
                app.setSprite(new FilmStrip[]{new FilmStrip(chopSpriteTexture, 3, 4, 10),
                        new FilmStrip(potSpriteTexture, 2, 4, 6),
                        new FilmStrip(fryerSpriteTexture, 2, 4, 6),
                        new FilmStrip(plateSpriteTexture, 1, 4, 4)});
            }

            processedTextures = new Array<>();
            for (int k = 0; k < 3; k++) {
                processedTextures.add(new Array<Texture>());
                String appName = basicAppliances[k];
                appName = appName.substring(0, 1).toUpperCase(Locale.ROOT) + appName.substring(1);
                for (int i = 0; i < rawIngTypesString.size; i++) {
                    processedTextures.get(k).add(directory.getEntry(rawIngTypesString.get(i) + appName, Texture.class));
                    processedTextures.get(k).add(directory.getEntry(rawIngTypesString.get(i) + appName + "Highlight", Texture.class));
                }
            }
            //        createIngredients();
            setApplianceTexture(directory);
            setIngredientTexture(directory);
            //        createRecipes();
            loadRecipesJSON(recipesJSON);
        }

    }

    public int getCurrentSelectedCookedIngredient(){
        return currentSelectedCookedIngredient;
    }

    public void setCurrentSelectedCookedIngredient(int i){
        currentSelectedCookedIngredient = i;
    }

    public void resetCurrentSelectedCookedIngredient(){
        currentSelectedCookedIngredient = NO_SELECT;
    }

    public void setRecipeData(JsonValue data) {
        storedJsonData = data;
        rewardsNSize = new Array<>();
        for (JsonValue recJson : data.iterator()) {
            Array<Integer> tmpReward = new Array<>(); // size, movements, throwable
            String sizeStr = recJson.get("rewards").get("size").toString().split(": ")[1];
            int size = Integer.parseInt(sizeStr);
            String movesNumStr = recJson.get("rewards").get("movements").toString().split(": ")[1];
            String turnsNumStr = recJson.get("rewards").get("turns").toString().split(": ")[1];
            int moves = Integer.parseInt(movesNumStr);
            int turns = Integer.parseInt(turnsNumStr);
            boolean throwableB = Boolean.valueOf(recJson.get("rewards").get("throwable").toString().split(": ")[1]);
            int throwable = throwableB ? 1 : 0;
            tmpReward.add(size); tmpReward.add(moves); tmpReward.add(turns); tmpReward.add(throwable);
            rewardsNSize.add(tmpReward);
        }
    }

    public Ingredient getIngFromIngLstByName(ArrayList<Ingredient> ingLst, String name) {
        Ingredient res = null;
        for (Ingredient ing : ingLst) {
            if (ing.getIngName().equals(name)) {
                res = ing;
            }
        }
        return res;
    }

    public Appliance getAppFromAppLstByName(ArrayList<Appliance> appLst, String name) {
        Appliance res = null;
        for (Appliance app : appLst) {
            if (app.getApplianceStringType() == name) {
                res = app;
            }
        }
        return res;
    }

    public Food makeFoodJSON(JsonValue recipeJSON, int moves, int turns, int throwable) {
        ArrayList<Ingredient> recipeIngList = new ArrayList<>();
        ArrayList<String> recipeAppList = new ArrayList<>();
        ArrayList<Ingredient> ingredientsList = new ArrayList<>(ingsIngCardTextures);
        ArrayList<Appliance> appliancesList = new ArrayList<>(appIngCardTextures);

        for (JsonValue ing : recipeJSON.iterator()) {
            String ingName = ing.get("name").toString().split(": ")[1];
            recipeIngList.add(getIngFromIngLstByName(ingredientsList, ingName).clone());
            String appName = ing.get("cookingMethod").toString().split(": ")[1];
            recipeAppList.add(appName);

        }

        boolean isThrowable = throwable == 1 ? true : false;
        return new Food(recipeIngList, recipeAppList, moves, turns, isThrowable);

    }

    public void loadRecipesJSON(JsonValue data){
        recipes = new ArrayList<>();
        for (JsonValue recipeJ : data.iterator()) {
            String movesNumStr = recipeJ.get("rewards").get("movements").toString().split(": ")[1];
            String turnsNumStr = recipeJ.get("rewards").get("turns").toString().split(": ")[1];
            int moves = Integer.parseInt(movesNumStr);
            int turns = Integer.parseInt(turnsNumStr);
            boolean throwableB = Boolean.valueOf(recipeJ.get("rewards").get("throwable").toString().split(": ")[1]);
            int throwable = throwableB ? 1 : 0;
            Food tmpFood = makeFoodJSON(recipeJ.get("ingredients"), moves, turns, throwable);
            recipes.add(tmpFood);
        }
        displayedRecipesInd = new ArrayList<>();
        if (recipes.size() > RECIPE_NUM){
            while (displayedRecipesInd.size() < RECIPE_NUM) {
                int newNum = rand.nextInt(recipes.size());
                if (!displayedRecipesInd.contains(newNum)){
                    displayedRecipesInd.add(newNum);
                }
            }
        } else {
            for (int j = 0; j < recipes.size(); j++) {
                displayedRecipesInd.add(j);
            }
        }
    }

    private void createAppliances(float canvasWidth, float canvasHeight) {
        appliances = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Appliance app = new Appliance(basicAppliances[i], canvasWidth, canvasHeight);
            appliances.add(app);
        }
    }

    public void setApplianceTexture(AssetDirectory directory) {
        for (int i = 0; i < 5; i++) {
            String appName = basicAppliances[i];
            Texture t = directory.getEntry(appName, Texture.class);
            Texture tH = directory.getEntry(appName+"Highlight", Texture.class);
            Texture tIC = directory.getEntry(appName+"IngCard", Texture.class);
            appliances.get(i).setTextures(t, tH, tIC);
            if (i < 3){
                Texture[] at = new Texture[rawIngTypesString.size];
                for (int j = 0; j < rawIngTypesString.size; j++){
                    String capIngType = rawIngTypesString.get(j);
                    capIngType = capIngType.substring(0, 1).toUpperCase(Locale.ROOT) + capIngType.substring(1);
                    at[j] = directory.getEntry(appName+capIngType,Texture.class);
                }
                appliances.get(i).setActiveTexture(at);
            }
        }
        choppingBoard = appliances.get(0);
        stove = appliances.get(1);
        fryer = appliances.get(2);
        plate = appliances.get(3);
        trashcan = appliances.get(4);
        appIngCardTextures = new ArrayList<>(appliances.subList(0,3));
        for (Appliance app : appIngCardTextures) {
            app.setIsIncCard();
        }
    }

    public void highlightDoneIngredient(){
        if (currentSelectedCookedIngredient < 3){
            for (Ingredient ig : doneIngredients){
                if (cookMatcher(ig.getCook()) == currentSelectedCookedIngredient){
                    ig.setHighlight();
                }
            }
        } else if (currentSelectedCookedIngredient == 3) {
            for (Ingredient ig : plateIngredients){
                ig.setHighlight();
            }
        }
    }

    public void setIngredientTexture(AssetDirectory directory) {
        for (Ingredient ing : ingredients) {
            String ingName = ing.getIngName();
            Texture t = directory.getEntry(ingName, Texture.class);
            Texture th = directory.getEntry(ingName+"Highlight", Texture.class);
            Texture tIC = directory.getEntry(ingName+"IngCard", Texture.class);
            ing.setTextures(t, th);
            ing.setTexture(t);
            ing.setIncCardTexture(tIC);
            if (!ing.isCooked()) {
                rawIngredients.add(ing);
            }
        }
        ingsIngCardTextures = new ArrayList<>(rawIngredients);
        for (Ingredient ing : ingsIngCardTextures) {
            ing.setIsIncCard();
        }
    }

    public void createIngredients(Array<String> rawIngTypes){
        ingredients = new ArrayList<>();
        ArrayList<String> apps = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(basicAppliances, 0, 3)));
        apps.add(0,"");
        int ptr = 0;
        for (String rawT : rawIngTypes) {
            for (String appT : apps) {
                Ingredient ing = new Ingredient(rawT, appT, ING_WIDTH, ING_WIDTH);
                if (appT == "") {
                    ing.setX(INGS_X_RATIOS[ptr]*width);
                    ing.setY(ING_Y_RATIO*height);
                    ptr++;
                }
                ingredients.add(ing);
            }
        }
    }

    public void createRecipes(){
        recipes = new ArrayList<>();
        for (int i =0; i<rewardsNSize.size; i++) {
            recipes.add(makeRecipe(rewardsNSize.get(i)));
        }
    }

    public Food makeRecipe(Array<Integer> recipeInfo) { // size, moves, throwable
        ArrayList<Ingredient> recipeIngList = new ArrayList<>();
        ArrayList<String> recipeAppList = new ArrayList<>();
        ArrayList<Ingredient> ingredientsList = new ArrayList<>(ingsIngCardTextures);
        ArrayList<Appliance> appliancesList = new ArrayList<>(appIngCardTextures);

        for (int i = 0; i < recipeInfo.get(0) ; i++) {
            int recipeIng = RandomController.rollInt(0,Math.min(ingredientsList.size()-1, 3));
            int recipeApp = RandomController.rollInt(0,Math.min(appliancesList.size()-1, 3));
            recipeIngList.add(ingredientsList.get(recipeIng).clone());
            recipeAppList.add(appliancesList.get(recipeApp).getApplianceStringType());
            ingredientsList.remove(recipeIng);
            appliancesList.remove(recipeApp);
            if (ingredientsList.size() == 0) {
                ingredientsList = new ArrayList<>(ingsIngCardTextures);
            }
            if (appliancesList.size() == 0) {
                appliancesList = new ArrayList<>(appIngCardTextures);
            }
        }
        boolean isThrowable = recipeInfo.get(3) == 1;
        return new Food(recipeIngList, recipeAppList, recipeInfo.get(1), recipeInfo.get(2),isThrowable);
    }

    public void reset() {
        if (recipesExist()) {
            selectedIng = NO_SELECT;
            trashState = TRASH_STATE_OFF;
            doneIngredients.clear();
            plateIngredients.clear();
            for (Appliance a : appliances) {
                a.resetDoneProcessed();
                a.setNotCooking();
                a.setContainsFood(false);
            }
            for (Ingredient ing : rawIngredients) {
                ing.setValid(false);
                ing.setAssembled(false);
            }
        }
//        setRecipeData(storedJsonData);
    }

    public Array<Integer> newRewards(){
        int moveNum = RandomController.rollInt(3,9)/3;
        int throwable;
        if (RandomController.rollFloat(0,1) > 0.3 && moveNum < 3) {
            throwable = 1;
        } else {
            throwable = 0;
        }
        return new Array<>(new Integer[]{moveNum + throwable, moveNum, throwable});
    }

    /** Sets drag ingredient */
    public Food getDefaultRecipe() {
        return defaultRecipe;
    }

    public Food getDefaultFoodTexture() {
        Food f = new Food(new ArrayList<Ingredient>(), new ArrayList<String>());
        f.setTexture(foodTexture);
        return f;
    }


    /** Sets default recipe */
    public void setDefaultRecipe(Food rp) {
        rp.setTexture(foodTexture);
        defaultRecipe = rp;
    }

    public void resetApplianceHighlight(){
        for (Appliance a : appliances){
            if (!a.isWorking()){
                a.setNormal();
            }
        }
    }

    public void updateHighlightAppliance(int i){
        if (!appliances.get(i).isWorking())
            appliances.get(i).setHighlight();
    }

    public void updateHighlightIngredients(int i){
        if (i < rawIngredients.size() && i >= 0)
            rawIngredients.get(i).setHighlight();
    }

    public boolean applianceHasDoneIngredient(int i){
        if (i < 3){
            for (Ingredient ig : doneIngredients){
                if (cookMatcher(ig.getCook()) == i){
                    return true;
                }
            }
        }
        if (i == 3){
            return true;
        }
        return false;
    }

    public void resetIngredientHighlight(){
        for (Ingredient ig : rawIngredients)
            ig.setNormal();
    }

    public void drawProgressBar(GameCanvas canvas, Appliance appliance) {
        float progressX = appliance.getX();
        float progressY = appliance.getY() + appliance.getHeight();

        float progressWidth = appliance.getWidth();
        float progressHeight = PROGRESSBAR_HEIGHT;
        canvas.draw(statusBkgMiddle, progressX, progressY, progressWidth, progressHeight);
        float currProgress = progressWidth * (float) appliance.getCurrProgress()/ (float) appliance.getTotalProgress();
        canvas.draw(statusFrgMiddle,progressX,progressY,progressWidth - currProgress,progressHeight);
    }


    public void drawClick(GameCanvas canvas, Appliance appliance) {
        float progressWidth = appliance.getWidth();
        float progressHeight = PROGRESSBAR_HEIGHT;
        int clickNum = appliance.getTotalProgress() - appliance.getCurrProgress();
        int clickTotal = appliance.getTotalProgress();
        float clickWidth = (progressWidth-CLICK_OFFSET*(clickTotal-1))/(float) clickTotal;
        float progressX = appliance.getX();
        float progressY = appliance.getY() + appliance.getHeight();

        canvas.draw(statusBkgMiddle, progressX, progressY, progressWidth, progressHeight);
            for (int i = 0; i < clickNum; i++) {
                canvas.draw(statusFrgMiddle, progressX + i * (clickWidth + CLICK_OFFSET), progressY, clickWidth, progressHeight);
            }

    }

    public void drawAppliances(GameCanvas canvas) {
        if (trashState > TRASH_STATE_OFF){
            trashcan.draw(canvas, Color.RED);
        }
        else {
            trashcan.draw(canvas, Color.WHITE);
        }
        choppingBoard.draw(canvas);
        stove.draw(canvas);
        fryer.draw(canvas);
        drawPlate(canvas);
    }

    public int matchRecipe(Food food) {
        for (int i : displayedRecipesInd) {
            if (recipes.get(i).isEqual(food)) {
                return i;
            }
        }
        return -1;
    }

    public void replaceRecipe(int matchedIdx) {
        // TODO: ROTATE ENABLE
        int matchedRecipeIdxInDisplayed = displayedRecipesInd.indexOf(matchedIdx);
        int newNum;
        while (true) {
            int tmpNum = rand.nextInt(recipes.size());
            if (!displayedRecipesInd.contains(tmpNum)) {
                newNum = tmpNum;
                break;
            }
        }
        displayedRecipesInd.add(matchedRecipeIdxInDisplayed, newNum);
        displayedRecipesInd.remove(matchedRecipeIdxInDisplayed+1);
//        boolean isReplaced = false;
//        while (!isReplaced) {
//            Food newRecipe = makeRecipe(rewardsNSize.get(i));
//            boolean hasSame = false;
//            for (Food ig: recipes) {
//                if (newRecipe.isEqual(ig)) {
//                    hasSame = true;
//                }
//            }
//            if (!hasSame) {
//                isReplaced = true;
//                recipes.set(i,newRecipe);
//            }
//        }
    }

    public Appliance getApplianceIndex(int idx){
        return appliances.get(idx);
    }

    public Appliance getApplianceName(String name){
        Appliance res = null;

        for (Appliance app : appliances) {
            if (app.getApplianceStringType().equals(name)) {
                 res = app;
            }
        }
        return res;
    }


    public void drawRecipes(GameCanvas canvas) {
        for (int i = 0; i < displayedRecipesInd.size(); i++) {
            ArrayList<Ingredient> curRecipeIng = recipes.get(displayedRecipesInd.get(i)).getAssembleIngredients();
            ArrayList<String> curRecipeApp = recipes.get(displayedRecipesInd.get(i)).getAssembleAppliances();
            float recipeIngX = width*(RECIPE_NUM - (i+1))/9f;
            recipeIngX = recipeIngX == 0 ? -15 : recipeIngX;
            float marginOffset = 10f;
            float yOffset = 4/122f*(width/9f - recipeIngX);
            String toPrint;
            GlyphLayout layout;
            float textWidth;

            float rewardIngY = (KITCHEN_HEIGHT - RECIPE_OFFSET/3) + yOffset;
            float rewardNOffX = i == 2 ? RECIPE_OFFSET*0.55f : 0;
            float rewardIngX = rewardNOffX + recipeIngX + RECIPE_ING_SIZE;
            float rewardNOffY = i == 2 ?-RECIPE_OFFSET*0.2f : 0;
            float rewardNumOffX = i == 0 ? RECIPE_OFFSET*1.5f/4 : RECIPE_OFFSET*1.4f/4;
            rewardNumOffX = i == 2 ? RECIPE_OFFSET*1.4f/4 : rewardNumOffX;
            float rewardNumOffY = i == 0 ? RECIPE_OFFSET*1.23f/4 : RECIPE_OFFSET*0.6f/4;
            rewardNumOffY = i == 2 ? 0 : rewardNumOffY;
            int count = 0; //how many rewards already drawn
            grande.getData().setScale(0.8f);
            if (recipes.get(displayedRecipesInd.get(i)).getMovementNum() != 0){
                toPrint = "" + recipes.get(displayedRecipesInd.get(i)).getMovementNum();
                layout = new GlyphLayout(grande, toPrint);
                textWidth = layout.width;
                canvas.draw(rewardForTexture, rewardIngX+RECIPE_OFFSET*count, rewardIngY+rewardNOffY, RECIPE_ING_SIZE, RECIPE_ING_SIZE);
                canvas.drawTextBottommAligned(toPrint, grande, rewardIngX+RECIPE_OFFSET*count+rewardNumOffX,
                        rewardIngY+rewardNOffY + yOffset+rewardNumOffY,Color.WHITE);
                count++;
            }

            if (recipes.get(displayedRecipesInd.get(i)).getTurnNum() != 0){
                toPrint = "" + recipes.get(displayedRecipesInd.get(i)).getTurnNum();
                layout = new GlyphLayout(westSacFont, toPrint);
                textWidth = layout.width;
                canvas.draw(rewardTurnTexture, rewardIngX + RECIPE_OFFSET*count,
                        rewardIngY+rewardNOffY, RECIPE_ING_SIZE, RECIPE_ING_SIZE);
                canvas.drawTextBottommAligned(toPrint, grande, rewardIngX + RECIPE_OFFSET*count+rewardNumOffX,
                        rewardIngY+rewardNOffY + yOffset+rewardNumOffY,Color.WHITE);
                count++;
            }

            if (recipes.get(displayedRecipesInd.get(i)).isThrowable()){
                toPrint = "1";
                layout = new GlyphLayout(westSacFont, toPrint);
                textWidth = layout.width;
                canvas.draw(rewardThrowTexture, rewardIngX + RECIPE_OFFSET*count,
                        rewardIngY+rewardNOffY, RECIPE_ING_SIZE, RECIPE_ING_SIZE);
                canvas.drawTextBottommAligned(toPrint, grande, rewardIngX + RECIPE_OFFSET*count+rewardNumOffX,
                        rewardIngY+rewardNOffY + yOffset+rewardNumOffY,Color.WHITE);
                count++;
            }
            count = 0;

            // draw ingredients in recipes
            for (int n = 0; n < recipes.get(displayedRecipesInd.get(i)).getSize() ; n++) {
                float l = i > 1 ? RECIPE_OFFSET/2 -(RECIPE_OFFSET*2/3f * n) : RECIPE_OFFSET/3 -(RECIPE_OFFSET*2/3f * n);
                l = i == 0 ? l + RECIPE_OFFSET/3 : l;
                float ingXPos = recipeIngX + l;
                float recipeIngY = (KITCHEN_HEIGHT - RECIPE_OFFSET*1.7f) -RECIPE_ING_SIZE*0.74f*n + yOffset;
                float nOff = n > 1 ? RECIPE_OFFSET/3f : 0;
                curRecipeIng.get(n).drawSize(canvas, ingXPos+RECIPE_OFFSET, recipeIngY, RECIPE_ING_SIZE*0.8f,RECIPE_ING_SIZE*0.8f);
                getApplianceName(curRecipeApp.get(n)).drawSize(canvas, ingXPos+(RECIPE_OFFSET*2.67f)+nOff, recipeIngY*1.04f, RECIPE_ING_SIZE*0.85f);
            }
            // Cross out assembled ingredients
            if (plateIngredients.size > 0) {
                ArrayList<Integer> crossedIngIdx = new ArrayList<>();
                for (Ingredient ig : plateIngredients) {
                    for (int n = 0; n < recipes.get(displayedRecipesInd.get(i)).getSize() ; n++){
                        if (ig.equals(curRecipeIng.get(n)) && ig.getCook().equals(curRecipeApp.get(n)) && !crossedIngIdx.contains(n)) {
                            float l = i > 1 ? RECIPE_OFFSET/2 -(RECIPE_OFFSET*2/3f * n) : RECIPE_OFFSET/3 -(RECIPE_OFFSET*2/3f * n);
                            l = i == 0 ? l + RECIPE_OFFSET/3 : l;
                            float recipeIngY = (KITCHEN_HEIGHT - RECIPE_OFFSET*2f) -RECIPE_ING_SIZE*0.74f*n + yOffset;

                            float ingXPos = recipeIngX + l;
                            canvas.draw(crossRecipeTexture, ingXPos + RECIPE_OFFSET,
                                    recipeIngY + RECIPE_ING_SIZE / 2, RECIPE_ING_SIZE * 2, RECIPE_ING_SIZE / 4);
                            crossedIngIdx.add(n);
                            break;
                        }
                    }
                }
            }

            // in tutorial, did complete recipe
            if (tutorialLevel) {
                for (int j = 0; j < completedRecipes.size(); j++) {
                    if (completedRecipes.get(j).isEqual(recipes.get(displayedRecipesInd.get(i)))) {
                        float checkXPos = recipeIngX - 2*marginOffset + RECIPE_OFFSET/2 -(RECIPE_OFFSET/2 * 1.5f);
                        float checkY = (KITCHEN_HEIGHT - RECIPE_OFFSET) -RECIPE_ING_SIZE*2 + yOffset;
                        canvas.draw(checkmarkTexture, checkXPos + RECIPE_OFFSET*2f,
                                checkY + RECIPE_ING_SIZE , RECIPE_ING_SIZE*1.5f, RECIPE_ING_SIZE*1.5f);

                    }
                }
            }

        }
    }

    public void drawPlate(GameCanvas canvas) {
        if (!plate.isAnimating()) {
            plate.draw(canvas,Color.WHITE);
        }
        if (failedFed) {
            canvas.draw(wrongFoodTexture, plate.getX(),plate.getY(),plate.getWidth(),plate.getHeight());
        }
    }

    private int cookMatcher(String cook){
        switch (cook) {
            case "chop":
                return 0;
            case "pot":
                return 1;
            case "fry":
                return 2;
            case "plate":
                return 3;
            default:
                return 4;
        }
    }

    private void handleTrash(){
        if (trash){
            if (trashTarget == 3){
                plateIngredients.clear();
                failedFed = false;
                failedFedCountdown = FAILFEDTIME;
            } else {
                for (int i = 0; i < doneIngredients.size; i++){
                    if (cookMatcher(doneIngredients.get(i).getCook()) == trashTarget){
                        doneIngredients.removeIndex(i);
                        break;
                    }
                }
            }
            appliances.get(trashTarget).setNotCooking();
            appliances.get(trashTarget).setContainsFood(false);
            resetTrash();
        }
    }

    private void handleAssemble(){
        if (assembleTarget > -2){
            if (assembleTarget == -1){
                for (Ingredient ig : doneIngredients){
                    ig.setX(plate.getX() + plate.getWidth()/2f);
                    ig.setY(plate.getY() + plate.getHeight()/2f);
                    plateIngredients.add(ig);
                }
                doneIngredients.clear();
                for (Appliance a: appliances){
                    a.setContainsFood(false);
                }
            } else {
                appliances.get(assembleTarget).setContainsFood(false);
                appliances.get(assembleTarget).resetDoneProcessed();
                for (int i = 0; i < doneIngredients.size; i++){
                    if (cookMatcher(doneIngredients.get(i).getCook()) == assembleTarget){
                        doneIngredients.get(i).setX(plate.getX() + plate.getWidth()/2f);
                        doneIngredients.get(i).setY(plate.getY() + plate.getHeight()/2f);
                        doneIngredients.get(i).setNormal();
                        plateIngredients.add(doneIngredients.get(i));
                        doneIngredients.removeIndex(i);
                        break;
                    }
                }
            }
            // Handle updating recipeCards
            resetAssemble();
        }
    }

    private int ingNameToIndMatcher(String s){
        // assumption: never -1 (string always in rawIngTypesString)
        int ind = rawIngTypesString.indexOf(s, false);
        return ind;
    }

    private HashMap<String, Integer> recipeRewardToDict(int recipe){
        HashMap<String, Integer> toBeReturned = new HashMap<>();
        Food reward = recipes.get(recipe);
        toBeReturned.put("size",reward.getSize());
        toBeReturned.put("move",reward.getMovementNum());
        toBeReturned.put("turn",reward.getTurnNum());
        int thr = reward.isThrowable() ? 1 : 0;
        toBeReturned.put("throw",thr);
        return toBeReturned;
    }

    private void handleFeed(){
        if (failedFed) {
            failedFedCountdown--;
            if(failedFedCountdown == 0) {
                failedFed = false;
                failedFedCountdown = FAILFEDTIME;
            }
        }
        if (feed) {
            // Feed the truck
            if (plateIngredients.size > 0){
                ArrayList<Ingredient> igList = new ArrayList<>();
                ArrayList<String> apList = new ArrayList<>();
                for (Ingredient plateIng : plateIngredients){
                    igList.add(rawIngredients.get(ingNameToIndMatcher(plateIng.getIngName())));
                    apList.add(appliances.get(cookMatcher(plateIng.getCook())).getApplianceStringType());
                }
                if (currentFedFood == null){
                    currentFedFood = new Food(igList,apList);
                    currentFedFood.setTexture(foodTexture);
                    int matchedRecipe = matchRecipe(currentFedFood);
                    if (matchedRecipe > -1){
                        if (tutorialLevel) {
                            completedRecipes.add(currentFedFood);
                        }
                        // Handle recipe reward
                        fedSuccess = true;
                        ding.play(soundvolume);
                        fedReward = recipeRewardToDict(matchedRecipe);
                        fedFood = currentFedFood.clone();
                        // Handle replacement
                        if (recipes.size() > RECIPE_NUM){
                            replaceRecipe(matchedRecipe);
                        }
                    } else {
                        error.play(soundvolume);
                        failedFed = true;
                        failedFedCountdown = FAILFEDTIME;
                    }
                    currentFedFood = null;
                    plateIngredients.clear();
                    if (!failedFed) {
                        plate.setAnimating();
                    }
                }
            }
            resetFeed();
        }
    }

    public void update() {
            handleTrash();
            handleAssemble();
            handleFeed();
            for (Appliance app : appliances) {
                app.update();
                if (app.getDoneProcessed() != -1) {
                    int igType = app.getDoneProcessed();
                    Ingredient ig = new Ingredient(rawIngredients.get(igType).getIngName(),
                            app.getApplianceStringType(), app.getWidth() / 2, app.getHeight() / 3);
                    ig.setX(app.getX() + app.getWidth() / 2);
                    ig.setY(app.getY() + app.getHeight() / 2);
                    ig.setTextures(processedTextures.get(app.getApplianceType()).get(igType * 2),
                            processedTextures.get(app.getApplianceType()).get(igType * 2 + 1));
                    doneIngredients.add(ig);
                    app.setContainsFood(true);
                    app.resetDoneProcessed();
                }
            }
    }

    /**
     * Draws the kitchen to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     */
    public void drawRawIngredients(ArrayList<Ingredient> rawIngredients){
        for (Ingredient ig : rawIngredients){
            // TO DO: Fill up drawer space

        }
    }

    /**
     * Draws the kitchen to the canvas
     *
     * There is only one drawing pass in this application, so you can draw the objects
     * in any order.
     *
     * @param canvas The drawing context
     */
    public void draw(GameCanvas canvas) {
        // Draw kitchen to scale
//        canvas.draw(kitchenTexture, 0, 0, canvas.getWidth(), canvas.getHeight() * 415 / 530f);
        canvas.draw(kitchenDrawers, 0, 0, canvas.getWidth(), canvas.getHeight() * 415 / 530f);

//        drawRawIngredients(rawIngredients);

        for (Ingredient ig : rawIngredients) {
            ig.draw(canvas);
        }

        canvas.draw(kitchenBoard, 0, 0, canvas.getWidth(), canvas.getHeight() * 415 / 530f);

        if (recipesExist()) {
            drawRecipes(canvas);
            drawAppliances(canvas);

            if (doneIngredients.size > 0) {
                for (Ingredient ig : doneIngredients) {
                    ig.draw(canvas);
                }
            }

            if (plateIngredients.size > 0) {
                for (Ingredient ig : plateIngredients) {
                    ig.draw(canvas);
                }
            }

            for (Appliance app : appliances) {
                if (app.isWorking() && !app.isClickable() || app.getDoneProcessed() > -1) {
                    drawProgressBar(canvas, app);
                } else if (app.isWorking() && app.isClickable()) {
                    drawClick(canvas, app);
                }
                if (app.isAnimating()) {
                    app.drawAnimation(canvas);
                }
            }
        }

        if (rawIngredients.size() == 0) {
            canvas.draw(closedSignTexture, width/2f-width/10f,height/4f,width/5f,((float)closedSignTexture.getHeight()/(float)closedSignTexture.getWidth())*(width/5f));
        }
    }


    public ArrayList<Float> getIngredientConstants() {
        ArrayList<Float> tobeReturned = new ArrayList<>();
        tobeReturned.add(ING1_X_RATIO);
        tobeReturned.add(ING2_X_RATIO);
        tobeReturned.add(ING3_X_RATIO);
        tobeReturned.add(ING4_X_RATIO);
        tobeReturned.add(ING5_X_RATIO);
        tobeReturned.add(ING_Y_RATIO);
        tobeReturned.add(ING_WIDTH/height);
        return tobeReturned;
    }

    public void activateAppliance(int awakeAppliance, int ingredient) {
        if (awakeAppliance > 2)
            return;
        if (appliances.get(awakeAppliance).getContainsFood() || appliances.get(awakeAppliance).isWorking()) {
            return;
        }
        switch (awakeAppliance){
            case 1:
                boiling.play(soundvolume);
                break;
            case 2:
                sizzle.play(soundvolume);
                break;
        }
        appliances.get(awakeAppliance).setWorking(ingredient);
    }

    public Array<Ingredient> getDoneIngredients() {
        return doneIngredients;
    }

    public void setTrash(int applianceNum) {
        trash = true;
        trashTarget = applianceNum;

        switch (applianceNum){
            case 1:
                boiling.stop();
            case 2:
                sizzle.stop();
        }
    }

    public void resetTrash() {
        trash = false;
        trashTarget = -1;
    }

    public void setAssemble(boolean getisHoveringOnAppliance, int hoveringOnwhichAppliance) {
        if (getisHoveringOnAppliance) {
            assembleTarget = hoveringOnwhichAppliance;
        } else {
            assembleTarget = -1;
        }
    }

    public void resetAssemble() {
        assembleTarget = -2;
    }

    public void setFeed() {
        feed = true;
    }

    public void resetFeed() {
        feed = false;
    }

    public boolean getFedSuccess() {
        return fedSuccess;
    }

    public void resetFedSuccess() {
        fedSuccess = false;
    }

    public HashMap<String, Integer> getFedReward() {
        return fedReward;
    }

    public Food getFedFood() {
        return fedFood;
    }

    public void resetFedFood() {
        fedFood = null;
    }

    public void resetFedReward() {
        fedReward = null;
    }

    public void resetHighlightDoneIngredient() {
        for (Ingredient ig : doneIngredients){
            ig.setNormal();
        }
        for (Ingredient ig : plateIngredients){
            ig.setNormal();
        }
    }

    public boolean getTutorial() {
        return tutorialLevel;
    }

    public boolean completedAll(){
        return completedRecipes.size() == recipes.size();
    }
}
