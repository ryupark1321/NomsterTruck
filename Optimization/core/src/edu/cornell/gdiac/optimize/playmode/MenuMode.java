/*
 * GameMode.java
 *
 * This is the primary class file for running the game.  You should study this file for
 * ideas on how to structure your own root class. This class follows a
 * model-view-controller pattern fairly strictly.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize.playmode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import edu.cornell.gdiac.optimize.Button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.util.Controllers;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.XBoxController;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class MenuMode implements Screen, InputProcessor, ControllerListener {
    /** Constants */
    private static final int UNSET_LEVEL = -1;
    private int PADDING_VER = 200;
    private int PADDING_HORI = 300;
    private int LEVELS_PER_PAGE = 10;


    /** Internal assets for this menu screen */
    private AssetDirectory internal;
    /** Reference to GameCanvas created by the root */
    private GameCanvas canvas;
    private Texture background;
    private Texture bTexture;
    private Texture bTexture0;
    private Texture bTexture1;
    private Texture bTexture2;
    private Texture bTexture3;
    private BitmapFont bFont;
    private Texture filter;
    private Texture bar;
    private Texture blackfilter;
    private Texture gameControl;
    /** Whether this screen mode is still active */
    private boolean active;
    /** Listener that will update the player mode when we are done */
    private  ScreenListener listener;
    /** List of level file names */
    private ArrayList<String> levels;
    /** List of collected keys number for all levels */
    private ArrayList<Integer> keys;
    /** Level selected by the player */
    private int selectedLevelInd = UNSET_LEVEL;
    private int exitCode;
    private Texture white;
    private Texture back;
    private Texture backText;

    private Texture musicicon;
    private Texture soundicon;
    private BitmapFont westsac;
    private BitmapFont westsacCr;
    private Texture button;
    private ArrayList<Button> buttons;
    private Button pressedButton;
    private int pressState;
    /** Number of buttons horizontally */
    private int numHori;
    /** Number of buttons vertically */
    private int numVer;
    private Vector2 musicfilterPos;
    private Vector2 soundfilterPos;
    private Vector2 barSize;
    private float[] barWidths;
    private boolean[] volumePressed;
    /** Starting position of the first button */
    private Vector2 startButtonPos;
    /** end position of the first button */
    private Vector2 endButtonPos;
    private  Vector2 buttonSize;
    private ArrayList<Boolean> unlocked;
    private HashMap<Integer, Integer> unlockCondition;

    Json json = new Json();
//    FileHandle fileHandle = Gdx.files.local("keys.json");
    FileHandle fileHandle;

    private int exitCode2;
    private int page = 0;
    private int exitCode3;
    private int numLevels;
    private boolean displayLockText = false;
    private int framesLeft = 30;

    private Sound bgm2;
    private long bgm2ID;

    public MenuMode(GameCanvas canvas, String internalF, int exitC) {
        this.canvas = canvas;
        active = false;
        numHori = -1;
        numVer = -1;
        exitCode = exitC;
        // We need these files loaded immediately
        internal = new AssetDirectory( internalF );
        internal.loadAssets();
        internal.finishLoading();

        pressState = 0;
    }

    public MenuMode(GameCanvas canvas, String internalF,String keyF, int exitC, int numCol, int numRow) {
        this.canvas = canvas;
        active = false;
        JsonReader jsonR = new JsonReader();

        fileHandle = Gdx.files.external("NomsterTruck/" +keyF);

        // Compute the dimensions from the canvas
//        resize(canvas.getWidth(),canvas.getHeight());
        numHori = numCol;
        numVer = numRow;

        exitCode = exitC;
        // We need these files loaded immediately
        internal = new AssetDirectory( internalF );
        internal.loadAssets();
        internal.finishLoading();

        background = internal.getEntry( "background", Texture.class );
        white = internal.getEntry("white", Texture.class);
        bgm2 = internal.getEntry("bgm2", Sound.class);
        bgm2ID = bgm2.loop();
        background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        JsonValue levelsJson = jsonR.parse(Gdx.files.internal(internalF)).get("levels");
        musicfilterPos = new Vector2(canvas.getWidth()*5/196f,713/1080f*canvas.getHeight());
        soundfilterPos = new Vector2(canvas.getWidth()*5/196f,19/40f*canvas.getHeight());
        barSize = new Vector2(canvas.getWidth()*147/160f,canvas.getHeight()*5/54f);
        barWidths = new float[]{1,1};
        volumePressed = new boolean[]{false, false};
        levels = new ArrayList<String>();
        numLevels = 0;
        for (JsonValue s : levelsJson.iterator()) {
            levels.add(s.toString());
            numLevels++;
        }
        unlocked = new ArrayList<>((int) Math.ceil(numLevels/5d));
        unlockCondition = new HashMap<Integer, Integer>();
        unlockCondition.put(0,0);
        unlockCondition.put(1,0);
        unlockCondition.put(2,0);
        unlockCondition.put(3,0);
        unlockCondition.put(4,0);
        for (int i = 0; i < Math.ceil(numLevels/5d); i++){
            unlocked.add(false);
        }
        unlocked.set(0,true);
        keys = new ArrayList<>();
        if (!fileHandle.exists()) {
            JsonValue keysJson = jsonR.parse(Gdx.files.local(keyF));
            for (JsonValue s : keysJson.iterator()) {
                keys.add(s.asInt());
            }

            fileHandle.parent().mkdirs();

            json.setOutputType(JsonWriter.OutputType.json);
            KeysData kData = new KeysData();
            fileHandle.writeString(json.prettyPrint(kData), false);
        } else {
            JsonValue keysJson = jsonR.parse(Gdx.files.external("NomsterTruck/"+keyF));
            for (JsonValue s : keysJson.iterator()) {
                keys.add(s.asInt());
            }
        }
//        int idx = 0;
//        boolean locked = false;
//        while (idx < numLevels/5 && !locked){
//            int numKeys = 0;
//            for (int i = idx*5; i < 5*(idx+1); i++) {
//                if (keys.get(i) > -1){
//                    numKeys += keys.get(i);
//                }
//            }
//            if (numKeys >= unlockCondition.get(idx)){
//                unlocked.set(idx + 1,true);
//            } else {
//                locked = true;
//            }
//            idx++;
//        }
        pressState = 0;
    }

    public MenuMode(GameCanvas canvas, String internalF, int exitC, int numCol, int numRow) {
        this.canvas = canvas;
        active = false;

        // Compute the dimensions from the canvas
//        resize(canvas.getWidth(),canvas.getHeight());
        numHori = numCol;
        numVer = numRow;

        exitCode = exitC;
        // We need these files loaded immediately
        internal = new AssetDirectory( internalF );
        internal.loadAssets();
        internal.finishLoading();

        white = internal.getEntry("white", Texture.class);
        white.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        filter = internal.getEntry("filter", Texture.class);
        bgm2 = internal.getEntry("bgm2", Sound.class);
//        bgm2ID = bgm2.loop();
        filter.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        musicfilterPos = new Vector2(canvas.getWidth()*5/196f,713/1080f*canvas.getHeight());
        soundfilterPos = new Vector2(canvas.getWidth()*5/196f,19/40f*canvas.getHeight());
        barSize = new Vector2(canvas.getWidth()*147/160f,canvas.getHeight()*5/54f);
        barWidths = new float[]{1,1};
        volumePressed = new boolean[]{false, false};
        pressState = 0;
    }

    public void populateControl() {
        buttons = new ArrayList<>();
        white = internal.getEntry("white", Texture.class);
        white.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        back = internal.getEntry("back", Texture.class);
        back.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        button = internal.getEntry("button", Texture.class);
        button.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        westsac = internal.getEntry("westSac",BitmapFont.class);
        gameControl = internal.getEntry("gameControl", Texture.class);
        gameControl.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        filter = internal.getEntry("filter", Texture.class);
        filter.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


        addButton(Gdx.graphics.getWidth()*5/24f, Gdx.graphics.getHeight()*5/54f, false,
                "",westsac,Integer.toString(4),
                new Vector2(Gdx.graphics.getWidth()*11/240f, Gdx.graphics.getHeight()*31/540f),64,back);
    }


    public int getLastUnlockedLevel(){
        updateKeyLocks();
        int idx = 0;
        while (idx < unlocked.size() && unlocked.get(idx)){
            idx++;
        }
        return idx*5;
    }

    public ArrayList<String> getLevels() {
        return levels;
    }

    public String getLevel(int i) {
        return levels.get(i);
    }

    public void connected(Controller controller) {

    }

    public void disconnected(Controller controller) {

    }

    /**
     * Called when a button on the Controller was pressed.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */

    public boolean buttonDown(Controller controller, int buttonCode) {
        // TODO: what is this used for?
        if (pressState == 0) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 1;
                return false;
            }
        }
        return false;
    }

    /**
     * Called when a button on the Controller was released.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */

    public boolean buttonUp(Controller controller, int buttonCode) {
        if (pressState == 1) {
            ControllerMapping mapping = controller.getMapping();
            if (mapping != null && buttonCode == mapping.buttonStart ) {
                pressState = 2;
                return false;
            }
        }
        return true;
    }

    public boolean axisMoved(Controller controller, int i, float v) {
        return false;
    }

    public void addButton(float w, float h, boolean lB, String t, BitmapFont f, String cF, Vector2 pos, int fontS, Texture texture) {
        Button b = new Button(w, h, lB, t, f, cF, pos, fontS, texture);
        buttons.add(b);
    }

    public void addButton(float w, float h, boolean lB, String t, BitmapFont f, String cF, Vector2 pos, int fontS, Texture texture, Color tint) {
        Button b = new Button(w, h, lB, t, f, cF, pos, fontS, texture, tint);
        buttons.add(b);
    }

    public void nextPage(){
        page++;
    }

    public void backPage(){
        page--;
    }

    public void resetPage(){
        page = 0;
    }

    public void populateLevelButtons() {
        buttons = new ArrayList<>();
        // calculate button positions
        float horiOffset = canvas.getWidth() - startButtonPos.x * 2;

        horiOffset = (horiOffset - buttonSize.x * 0.75f * numHori) / numHori;//(horiOffset / bTexture.getWidth()) / numHori;
        float verOffset = canvas.getHeight() - (canvas.getHeight() - startButtonPos.y);

        verOffset = (verOffset - buttonSize.y * 0.75f * numVer) / numVer;// (verOffset /bTexture.getHeight()) / numVer;

        int colHoriPtr = 0;
        int rowVerPtr = 0;
        int numKeys = 0;
        Vector2 lPos = new Vector2();
        Vector2 pos = new Vector2();
        float blackFilterWidth = (horiOffset + buttonSize.x)*4*0.8f + buttonSize.x*1.5f;
        float highYPos = startButtonPos.y * 0.9f;
        float lowYPos = startButtonPos.y - (verOffset + buttonSize.y) * 2 * 0.9f;
        for (int i = LEVELS_PER_PAGE * page; i < LEVELS_PER_PAGE * page + LEVELS_PER_PAGE && i < levels.size(); i++) {
            String name = levels.get(i).split(".json")[0];
            // increment drawn position of buttons
            float newX = startButtonPos.x + (horiOffset + buttonSize.x) * colHoriPtr;
            float newY = startButtonPos.y - (verOffset + buttonSize.y) * rowVerPtr * 2;
            pos = new Vector2(newX * 0.8f, newY * 0.9f);
            int keyNum = keys.get(i);
            if (keyNum == -1){
                addButton(buttonSize.x, buttonSize.y, true, name, bFont, levels.get(i), pos, 20, bTexture);
            }
            else if (keyNum == 0){
                addButton(buttonSize.x, buttonSize.y, true, name, bFont, levels.get(i), pos, 20, bTexture0);
            }
            else if (keyNum == 1){
                addButton(buttonSize.x, buttonSize.y, true, name, bFont, levels.get(i), pos, 20, bTexture1);
            }
            else if (keyNum == 2){
                addButton(buttonSize.x, buttonSize.y, true, name, bFont, levels.get(i), pos, 20, bTexture2);
            }
            else if (keyNum == 3) {
                addButton(buttonSize.x, buttonSize.y, true, name, bFont, levels.get(i), pos, 20, bTexture3);
            }
            if (keyNum != -1){
                numKeys += keyNum;
            }
            if (i%5 == 0){
                lPos.x = pos.x - buttonSize.x/4f;
                lPos.y = pos.y;
            }
            if (i%5 == 4){
                if (numKeys >= unlockCondition.get(i/5)){
                    if (unlocked.size() < i/5 + 1){
                        unlocked.set(i/5 + 1, true);
                    }
                }
                if (!unlocked.get(i/5) ) {
                    addButton(pos.x-lPos.x + buttonSize.x*1.5f, buttonSize.y*1.5f, false, "Locked",
                            bFont, "", new Vector2(lPos), 0, white, new Color(0,0,0,0.9f));
                }
                numKeys = 0;
            }
            colHoriPtr++;
            if (colHoriPtr >= numHori) {
                colHoriPtr = 0;
                rowVerPtr++;
            }
        }
        if (levels.size()%5 != 4 && page == Math.ceil((levels.size()-1)/10f)){
            if (numKeys >= unlockCondition.get(levels.size()/5)){
                unlocked.set(levels.size()/5 + 1, true);
            }
            if (!unlocked.get(levels.size()/5)) {
                addButton(blackFilterWidth, buttonSize.y*1.5f, false, "Locked",
                        bFont, "", new Vector2(lPos.x, (levels.size()/5)%2 == 0 ? highYPos : lowYPos ),
                        0, white, new Color(0,0,0,0.9f));
            }
        }
    }

    public void updateKeyLocks(){
//        for (int i = 0; i < unlocked.size();i++){
//            unlocked.set(i,true);
//        }
        int numKeys = 0;
        for (int i = 0; i < levels.size(); i++){
            int keyNum = keys.get(i);
            if (keyNum != -1){
                numKeys += keyNum;
            }
            if (i%5 == 4 && i/5 + 1 < unlocked.size()){
                if (numKeys >= unlockCondition.get(i/5)){
                    unlocked.set(i/5 + 1, true);
                } else {
                    unlocked.set(i/5 + 1, false);
                }
                numKeys = 0;
            }
        }
    }

    public void populateStars() {
        bTexture = internal.getEntry("buttonTexture",Texture.class);
        bTexture0 = internal.getEntry("buttonTexture0",Texture.class);
        bTexture1 = internal.getEntry("buttonTexture1",Texture.class);
        bTexture2 = internal.getEntry("buttonTexture2",Texture.class);
        bTexture3 = internal.getEntry("buttonTexture3",Texture.class);
        bFont =  internal.getEntry("buttonFont",BitmapFont.class);
    }

    public void clearButtons() {
        buttons = null;
    }

    public int getLevelKeyNum(int levelIdx) {
        return keys.get(levelIdx);
    }

    public int getNumLevels(){
        return numLevels;
    }

    public void writeKeysToFile(int levelIdx, int newStarNum) {
        keys.remove(levelIdx);
        keys.add(levelIdx, newStarNum);
        json.setOutputType(JsonWriter.OutputType.json);
        KeysData kData = new KeysData();
        fileHandle.writeString(json.prettyPrint(kData), false);
    }

    private class KeysData implements Json.Serializable {

        private KeysData() {

        }

        @Override
        public void write(Json json) {
            for (int i = 0; i < levels.size(); i++) {
                json.writeValue(levels.get(i), keys.get(i));
            }
        }

        @Override
        public void read(Json json, JsonValue jsonValue) {

        }
    }

    public void reloadKeys(String keyF) {
        JsonReader jsonR = new JsonReader();

        keys = new ArrayList<Integer>();
        JsonValue keysJson = jsonR.parse(Gdx.files.external("NomsterTruck/" +keyF));
        for (JsonValue s : keysJson.iterator()) {
            keys.add(s.asInt());
        }
    }

    public void populateCredits() {
        buttons = new ArrayList<>();
        background = internal.getEntry( "background", Texture.class );
        background.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        back = internal.getEntry("back", Texture.class);
        back.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        button = internal.getEntry("button", Texture.class);
        button.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        westsac = internal.getEntry("westSac",BitmapFont.class);
        westsacCr = internal.getEntry("westSacCr",BitmapFont.class);

        addButton(Gdx.graphics.getWidth()*5/24f, Gdx.graphics.getHeight()*5/54f, false,
                "",westsac,Integer.toString(4),
                new Vector2(Gdx.graphics.getWidth()*11/240f, Gdx.graphics.getHeight()*31/540f),64,back);
    }
 
    public void populate() {
        buttons = new ArrayList<>();
        bar = internal.getEntry("bar", Texture.class);
        back = internal.getEntry("back", Texture.class);
        back.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        button = internal.getEntry("button", Texture.class);
        button.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        blackfilter = internal.getEntry("blackfilter",Texture.class);
        westsac = internal.getEntry("westSac",BitmapFont.class);
        musicicon = internal.getEntry("musicicon",Texture.class);
        soundicon = internal.getEntry("soundicon",Texture.class);
        addButton(Gdx.graphics.getWidth()*5/24f, Gdx.graphics.getHeight()*5/54f, false,
                "",westsac,Integer.toString(4),
                new Vector2(Gdx.graphics.getWidth()*11/240f, Gdx.graphics.getHeight()*31/540f),64,back);
    }

    public void populateFlipButtons(){
        Texture nextTexture = internal.getEntry("nextButton", Texture.class);
        Texture backText = internal.getEntry("backText", Texture.class);
        Texture backTexture = internal.getEntry("backButton", Texture.class);
        BitmapFont bFont = internal.getEntry("buttonFont", BitmapFont.class);
        float w = 100f * 317 / 498 ;
        float h = 100f;
        exitCode2 = 7;
        exitCode3 = 8;
        Vector2 pos = new Vector2(canvas.getWidth() - w * 1.5f, canvas.getHeight() / 2f - h / 2f);
        Vector2 pos2 = new Vector2(w * 0.5f, canvas.getHeight() / 2f - h / 2f);
        if (page < java.lang.Math.ceil(levels.size()/LEVELS_PER_PAGE)) {
            buttons.add(new Button(w, h, false, "", bFont, "6", pos, 0, nextTexture, Color.WHITE));
        }
        if (page > 0) {
            buttons.add(new Button(w, h, false, "", bFont, "5", pos2, 0, backTexture, Color.WHITE));
        }
        addButton(Gdx.graphics.getWidth()*5/24f, Gdx.graphics.getHeight()*5/54f, false,
                "",new BitmapFont(),Integer.toString(4),
                new Vector2(Gdx.graphics.getWidth()*11/240f, Gdx.graphics.getHeight()*31/540f),64,backText);
    }

    public void setButton(Vector2 startPos, Vector2 endPos, float width, float height) {
        startButtonPos = startPos;
        endButtonPos = endPos;
        buttonSize = new Vector2(width * canvas.getHeight()/720f, height* canvas.getHeight()/720f);
    }

    public String getSelectedButtonContent() {
        return pressedButton.getConnectedFile();
    }

    public void setSelectedButtonContent(String s) {
        for (Button b : buttons) {
            if (b.getConnectedFile() == s) {
                pressedButton = b;
            }
        }
    }

    public void resetSelectedButton() {
        pressedButton = null;
    }

    public Button getPressedButton() {
        return pressedButton;
    }

    public void draw() {
        canvas.begin();
        if (exitCode == 0) { //Level selections
            if (active) {
                canvas.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                if (buttons != null){
//                    int idx = 0;
//                    float xPos = 0;
//                    float xPos3 = 0;
//                    float bHeight = 0f;
//                    float bWidth = 0f;
//                    Vector2 bPos = new Vector2(0,0);
                    int idx = 0;
                    for (Button b : buttons) {
                        bFont.getData().setScale(canvas.getHeight()/720f);
                        b.draw(canvas);
                        idx++;
                    }
                }
                if (displayLockText){
                    framesLeft--;
                    String s = "Earn stars to unlock more levels!";
                    GlyphLayout layout = new GlyphLayout(bFont,s);
                    float width = layout.width;
                    canvas.drawText(s,bFont,
                            (canvas.getWidth()-width)/2f,canvas.getHeight()*1.05f/2f,Color.WHITE);
                    if (framesLeft == 0){
                        displayLockText = false;
                    }
                }
            }
        } else if (exitCode == -1){ //Settings
            if (active) {
                canvas.draw(white, Color.BLACK,0,0,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                canvas.draw(filter, 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                if (buttons != null){
                    for (Button b : buttons) {
                        b.draw(canvas);
                    }
                }
                canvas.draw(button, Color.WHITE, 0,0,19/48f*Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight()*231/270f, Gdx.graphics.getWidth()*5/24f,
                        Gdx.graphics.getHeight()*5/54f);
                String toPrint = "Settings";
                GlyphLayout layout = new GlyphLayout(westsac, toPrint);
                float textWidth = layout.width;
                canvas.drawText(toPrint,westsac,(Gdx.graphics.getWidth()-textWidth)/2f,
                        Gdx.graphics.getHeight()*231/270f + westsac.getLineHeight(),Color.BLACK);

                canvas.draw(bar, Color.WHITE,0,0, musicfilterPos.x, musicfilterPos.y,barSize.x,barSize.y);
                canvas.draw(blackfilter, new Color(0,0,0,0.25f),0,0,musicfilterPos.x,
                        musicfilterPos.y,barWidths[0] * barSize.x,barSize.y);
                canvas.drawText("  Music ("+Integer.toString((int) (barWidths[0]*100))+"%)",westsac,
                        musicfilterPos.x, musicfilterPos.y + westsac.getLineHeight(), Color.WHITE);
                canvas.draw(musicicon, Color.WHITE,barSize.y*0.3f,barSize.y*0.3f,
                        canvas.getWidth()*8/9f,musicfilterPos.y + barSize.y/2f,
                        barSize.y*0.6f,barSize.y*0.6f);

                canvas.draw(bar, Color.WHITE,0,0, soundfilterPos.x, soundfilterPos.y,barSize.x,barSize.y);
                canvas.draw(blackfilter, new Color(0,0,0,0.25f),0,0,soundfilterPos.x,
                        soundfilterPos.y,barWidths[1] * barSize.x,barSize.y);
                canvas.drawText("  Sound ("+Integer.toString((int) (barWidths[1]*100))+"%)",westsac,
                        soundfilterPos.x, soundfilterPos.y+ westsac.getLineHeight(),Color.WHITE);
                canvas.draw(soundicon, Color.WHITE,barSize.y*0.3f,barSize.y*0.3f,
                        canvas.getWidth()*8/9f,soundfilterPos.y + barSize.y/2f,
                        barSize.y*0.6f,barSize.y*0.6f);
            }
        } else if (exitCode == -2){ //Credits
            if (active) {
                canvas.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                if (buttons != null){
                    for (Button b : buttons) {
                        b.draw(canvas);
                    }
                    canvas.draw(button, Color.WHITE, 0,0,19/48f*Gdx.graphics.getWidth(),
                            Gdx.graphics.getHeight()*231/270f, Gdx.graphics.getWidth()*5/24f,
                            Gdx.graphics.getHeight()*5/54f);
                    String toPrint = "Credits";
                    GlyphLayout layout = new GlyphLayout(westsac, toPrint);
                    float textWidth = layout.width;
                    canvas.drawText(toPrint,westsac,(Gdx.graphics.getWidth()-textWidth)/2f,
                            Gdx.graphics.getHeight()*231/270f + westsac.getLineHeight(),Color.BLACK);

                    canvas.drawTextCentered("Developed by:",westsac,Gdx.graphics.getHeight()/5, Color.WHITE);
                    canvas.drawTextCentered("Christy Song",westsacCr,Gdx.graphics.getHeight()/5-Gdx.graphics.getHeight()/13, Color.WHITE);
                    canvas.drawTextCentered("Chloe Chu",westsacCr,Gdx.graphics.getHeight()/5-Gdx.graphics.getHeight()/13-Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Crystal Jin",westsacCr,Gdx.graphics.getHeight()/5-Gdx.graphics.getHeight()/13-2*Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Soon Jae Park",westsacCr,Gdx.graphics.getHeight()/5-Gdx.graphics.getHeight()/13-3*Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Jacob Asimow",westsacCr,Gdx.graphics.getHeight()/5-Gdx.graphics.getHeight()/13-4*Gdx.graphics.getHeight()/16, Color.WHITE);

                    canvas.drawTextCentered("Designed by:",westsac,Gdx.graphics.getHeight()/5-2*Gdx.graphics.getHeight()/13-4*Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Hank Lin",westsacCr,Gdx.graphics.getHeight()/5-3*Gdx.graphics.getHeight()/13-4*Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Carl Chen",westsacCr,Gdx.graphics.getHeight()/5-3*Gdx.graphics.getHeight()/13-5*Gdx.graphics.getHeight()/16, Color.WHITE);
                    canvas.drawTextCentered("Roxanne Tanenbaum",westsacCr,Gdx.graphics.getHeight()/5-3*Gdx.graphics.getHeight()/13-6*Gdx.graphics.getHeight()/16, Color.WHITE);
                }
            }
        } else if (exitCode == -3){ //Control
            canvas.draw(white, Color.BLACK,0,0,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            canvas.draw(filter, 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            if (buttons != null){
                for (Button b : buttons) {
                    b.draw(canvas);
                }
            }
            canvas.draw(button, Color.WHITE, 0,0,19/48f*Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()*231/270f, Gdx.graphics.getWidth()*5/24f,
                    Gdx.graphics.getHeight()*5/54f);
            String toPrint = "Control";
            GlyphLayout layout = new GlyphLayout(westsac, toPrint);
            float textWidth = layout.width;
            canvas.drawText(toPrint,westsac,(Gdx.graphics.getWidth()-textWidth)/2f,
                    Gdx.graphics.getHeight()*231/270f + westsac.getLineHeight(),Color.BLACK);
            canvas.draw(gameControl, 0,Gdx.graphics.getHeight()*31/540f+Gdx.graphics.getHeight()*5/54f*1.5f,
                    Gdx.graphics.getWidth(),Gdx.graphics.getWidth()*0.365f);
        }
        canvas.end();
    }

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */

    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when a key is released (UNSUPPORTED)
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */

    public boolean keyUp(int keycode) {
        return false;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @param character the key typed
     * @return whether to hand the event to other listeners.
     */

    public boolean keyTyped(char character) {
        return false;
    }

    private void handleBarPress(int screenX, int screenY) {
        if (exitCode != -1) {
            return;
        }
        Vector2[] temp = new Vector2[]{musicfilterPos, soundfilterPos};
        for (int i = 0; i < temp.length; i++){
            if (screenX >= temp[i].x && screenX < temp[i].x + barSize.x
            && screenY >= temp[i].y && screenY < temp[i].y + barSize.y){
                volumePressed[i] = true;
                barWidths[i] = ((float)(screenX - temp[i].x))/(barSize.x);
            }
        }
    }

    public float getMusicVolume(){
        return barWidths[0];
    }
    public float getSoundVolume(){
        return barWidths[1];
    }

    public void setMusicVolume(float m){
        barWidths[0] = m;
    }

    public void setSoundVolume(float s){
        barWidths[1] = s;
    }

    /**
     * Called when the screen was touched or a mouse button was pressed.
     *
     * This method checks to see if a button is available and if the click
     * is in the bounds of the button.  If so, it signals that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (active) { // flip to match graphics coordinates
            screenY = canvas.getHeight() - screenY;
            if (buttons != null) {
                for (Button b : buttons) {
                    float xAreaLow = b.getPosition().x;
                    float xAreaHigh = b.getPosition().x + b.getWidth();
                    float yAreaLow = b.getPosition().y;
                    float yAreaHigh = b.getPosition().y + b.getHeight();
                    if (screenX >= xAreaLow && screenX <= xAreaHigh && screenY >= yAreaLow && screenY <= yAreaHigh) {
                        if (b.getText() == "Locked" || b.getText() == "" || unlocked.get((Integer.parseInt(b.getText()) - 1)/5)) {
                            pressedButton = b;
                            pressState = 1;
                            return true;
                        }
                    }
                }
            }
            handleBarPress(screenX,screenY);
        }

        return false;
    }


    private void handleBarRelease(int screenX, boolean released){
        if (exitCode != -1) {
            return;
        }
            Vector2[] temp = new Vector2[]{musicfilterPos, soundfilterPos};
        for (int i = 0; i < 2; i++){
            if (volumePressed[i]) {
                if (screenX <= temp[i].x){
                    barWidths[i] = 0;
                } else if (screenX >= temp[i].x + barSize.x){
                    barWidths[i] = 1;
                } else {
                    barWidths[i] = ((float)(screenX - temp[i].x))/(barSize.x);
                }
                if (released){
                    volumePressed[i] = false;
                    bgm2.setVolume(bgm2ID, getMusicVolume());
                }
            }
        }
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     *
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        handleBarRelease(screenX, true);
        if (pressedButton != null && pressState == 1) {
            if (exitCode != 0 && pressedButton.getConnectedFile() != ""){
                exitCode = Integer.parseInt(pressedButton.getConnectedFile()) + 2;
            } else if (pressedButton.getConnectedFile() == "" && pressedButton.getText() == "Locked"){
                displayLockText = true;
                framesLeft = 90;
            }
            pressState = 2;
            return false;
        }
        return true;
    }

    public boolean touchDragged(int i, int i1, int i2) {
        handleBarRelease(i, false);
        return false;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param dx the amount of horizontal scroll
     * @param dy the amount of vertical scroll
     *
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(float dx, float dy) {
        return false;
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     *
     * Useless if called in outside animation loop
     */
    public void show() {
        active = true;
    }

    public boolean isReady() {
        if (selectedLevelInd !=  UNSET_LEVEL || pressedButton != null && pressedButton.isLevelButton()) {
            return pressState >= 2;
        }
        return false;
    }

    public boolean pressedNext(){
        if(pressedButton != null && !pressedButton.isLevelButton() && pressedButton.getConnectedFile() == "6"){
            return pressState >= 2;
        }
        return false;
    }

    public boolean pressedBack(){
        if(pressedButton != null && !pressedButton.isLevelButton() && pressedButton.getConnectedFile() == "5"){
            return pressState >= 2;
        }
        return false;
    }

    public void render(float v) {
        if (active) {
            draw();
//
            if (pressedButton != null && pressedButton.getConnectedFile().equals("4")) { // when level selected
                listener.exitScreen(this,6);
                pressState = 0;
                pressedButton = null;
            } else if (isReady() && listener != null) { // when level selected
                pressState = 0;
                bgm2.pause();
                listener.exitScreen(this, exitCode);
            } else if(pressedNext() && listener != null){
                pressState = 0;
                listener.exitScreen(this, exitCode2);
            } else if(pressedBack() && listener != null){
                pressState = 0;
                listener.exitScreen(this, exitCode3);
            } else if (exitCode == -1) {
                listener.exitScreen(this, -2);
            }
        }
    }

    public void resize(int width, int height) {

    }

    public void pause() {
//        bgm2.pause();
    }

    public void resume() {
        bgm2.setVolume(bgm2ID, getMusicVolume());
        bgm2.resume();
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     *
     * Useless if called in outside animation loop
     */
    public void hide() {
        active = false;
    }

    public void dispose() {
        internal.unloadAssets();
        internal.dispose();
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
        Gdx.input.setInputProcessor( this );
        // Let ANY connected controller start the game.
        for (XBoxController controller : Controllers.get().getXBoxControllers()) {
            controller.addListener( this );
        }
    }
}
