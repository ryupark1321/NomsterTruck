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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.optimize.Button;
import edu.cornell.gdiac.optimize.controller.GameplayController;
import edu.cornell.gdiac.optimize.controller.InputController;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.util.Controllers;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.XBoxController;

import java.util.ArrayList;

/**
 * The primary controller class for the game.
 *
 * While GDXRoot is the root class, it delegates all of the work to the player mode
 * classes. This is the player mode class for running the game. In initializes all 
 * of the other classes in the game and hooks them together.  It also provides the
 * basic game loop (update-f).
 */
public class GameMode implements Screen, InputProcessor {


	/**
	 * Track the current state of the game for the update loop.
	 */
	public enum GameState {
		/** Before the game has started */
		INTRO,
		/** While we are playing the game */
		PLAY,
		/** When the ships is dead (but shells still work) */
		OVER,
		PAUSE,

	}

	// Loaded assets
	/** The menu selection Screen for the game */
	private Texture mainMenu;
	/** The font for giving messages to the player */
	private BitmapFont displayFont;
	private BitmapFont westSacFont;
	private BitmapFont westSac45Font;
	/** Set when new number of keys are collected */
	private int keyNum;

	/// CONSTANTS
	/** Factor used to compute where we are in scrolling process */
	private static final float TIME_MODIFIER    = 0.06f;
	/** Offset for the shell counter message on the screen */
	private static final float COUNTER_OFFSET   = 5.0f;
	/** Offset for the game over message on the screen */
	private static final float GAME_OVER_OFFSET = 40.0f;
	/** Offset for the game over message on the screen */
	private static final int NO_CLICK_TIME = 50;
	private static final int SHIP_DAMAGE_TIME = 50;
	private static final int TILE_SIZE = 50;

	private static final int TILE_X_OFFSET = 15;
	private static final int TILE_Y_OFFSET = 325;
	private int pressState;
	private int lastLevel;

	/** Reference to drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;

	/** Constructs the game models and handle basic gameplay (CONTROLLER CLASS) */
	private GameplayController gameplayController;
	/** Reads input from keyboard or game pad (CONTROLLER CLASS) */
	private InputController inputController;

	/** Variable to track the game state (SIMPLE FIELDS) */
	private GameState gameState;
	/** Variable to track total time played in milliseconds (SIMPLE FIELDS) */
	private float totalTime = 0;
	/** Whether or not this player mode is still active */
	private boolean active;
	/** The amount of time after damage font initiates */
	private int clickResumeTime = NO_CLICK_TIME;

	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;
	/** Listener that will update the player mode when we are done */
	private ClickListener clickListener;

	private boolean gameResult;

	public static final Vector2 EXIT_POS = new Vector2(7,0);
	private static final int TIMER = 20;
	private Texture pauseButton;
	private Texture pauseScreen;
	private Texture keyTexture;
	private Texture timerPlank;
	private Texture background;
	private Texture endScreen;
	private Texture mapReturn;
	private Texture settingsReturn;
	private Texture retryReturn;
	private Texture nextLevelReturn;
	private Texture resumeReturn;
	private Texture emptyKeyTexture;
	private Texture questionMark;
	private Texture gameControl;
	private Texture black;

	private String level;
	private ArrayList<Button> buttons;
	private Button selectedButton;
	private int currLev;

	private float musicvolume;
	private float soundvolume;
	private boolean winKeySaved;
	private int pausedState;

	private Sound bgm;
	private Sound bgm2;
	private long bgmID;


	public void setWinKeySaved(boolean b) {
		winKeySaved = b;
	}

	public void setLastLevel(int i) {
		lastLevel = i;
	}

	public int getCurrLev() {
		return currLev;
	}

	public int getKeyNum() {
		return keyNum;
	}

	/**
	 * Creates a new game with the given drawing context.
	 *
	 * This constructor initializes the models and controllers for the game.  The
	 * view has already been initialized by the root class.
	 */
	public GameMode(GameCanvas canvas) {
		this.canvas = canvas;
		active = false;
		winKeySaved = false;

//		level = "levels/1.json";

		// Null out all pointers, 0 out all ints, etc.
		gameState = GameState.INTRO;
		// Create the controllers.
		inputController = new InputController();
		// Level file should be passed in from level selecting screen??
//		gameplayController = new GameplayController(canvas.getWidth(),canvas.getHeight(), level);

	}

	public void updateVolume(float m, float s){
		musicvolume = m;
		soundvolume = s;
	}

	public void setLevel(String s) {
		level = "levels/" + s;
		currLev = Integer.valueOf(s.split(".json")[0]);
	}

	public void setGameplayController() {
		gameplayController = new GameplayController(canvas.getWidth(), canvas.getHeight(), level);
		gameplayController.notifyLevel(currLev);
		gameplayController.loadConstantsToInputController(inputController);
		gameplayController.setVolumes(musicvolume,soundvolume);
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		gameplayController = null;
		canvas = null;
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
		background = directory.getEntry("background",Texture.class);
		mapReturn = directory.getEntry("mapReturnButton",Texture.class);
		settingsReturn = directory.getEntry("settingsReturnButton",Texture.class);
		retryReturn = directory.getEntry("retryReturnButton",Texture.class);
		nextLevelReturn = directory.getEntry("nextLevelReturnButton",Texture.class);
		displayFont = directory.getEntry("times",BitmapFont.class);
		westSacFont = directory.getEntry("westSac",BitmapFont.class);
		westSac45Font = directory.getEntry("westSac45",BitmapFont.class);
		pauseButton = directory.getEntry("pause", Texture.class);
		pauseScreen = directory.getEntry("pauseScreen", Texture.class);
		gameplayController.populate(directory);
		mainMenu = directory.getEntry("mainmenu", Texture.class);
		resumeReturn = directory.getEntry("resumeReturnButton", Texture.class);
		keyTexture = directory.getEntry("star", Texture.class);
		emptyKeyTexture = directory.getEntry("star2", Texture.class);
		endScreen = directory.getEntry("endScreen", Texture.class);
		gameControl = directory.getEntry("gameControl", Texture.class);
		questionMark = directory.getEntry("questionMark", Texture.class);
		black = directory.getEntry("black", Texture.class);
		bgm = directory.getEntry("bgm", Sound.class);
		bgm2 = directory.getEntry("bgm2", Sound.class);
	}

	/**
	 * Update the game state.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
		// Process the game input
		if (gameState != GameState.PAUSE && gameState != GameState.OVER){
			gameplayController.update(inputController);
		} else if (gameState == GameState.PAUSE) {
			inputController.readControl();
			if (pausedState == 0){
				gameplayController.checkTutorialStates(inputController);
				gameplayController.handleTutorials(inputController);
				if (inputController.updatePause() || inputController.didControl()) {
					pausedState = 1;
				}
			}
		}
		// Test whether to reset the game.
		switch (gameState) {
			case INTRO:
				gameState = GameState.PLAY;
				gameplayController.init();
				inputController.resetPrevIngredientInput();
				inputController.resetPrevIngredientPressed();
				bgmID = bgm.loop(musicvolume);
				break;
			case OVER:
//				if (inputController.didReset()) {
//					restartUpdate();
//				}
//				else {
//					play(delta);
//				}

				break;
			case PLAY:
//				if (inputController.didReset()) {
//					restartUpdate();
//				}
				play(delta);
				break;
			case PAUSE:
				if (inputController.updateReset()) {
					restartUpdate();
				}
				pause();
			default:
				break;
		}
	}

	public void restartUpdate() {
		gameState = GameState.PLAY;
		winKeySaved = false;
		gameplayController.restartUpdate();
		inputController.resetSelectedTargetIngredient();
	}


	/**
	 * This method processes a single step in the game loop.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	protected void play(float delta) {
		// if no player is alive, declare game over
		if (gameState != GameState.OVER){
			if (gameplayController.reachExit() && !gameplayController.isCollectingKey()) {
				gameResult = true;
				gameState = GameState.OVER;
				return;
			}

			if (gameplayController.getWorldTimerDisplay() == 0 || gameplayController.playerMetEnemy()) {
				gameResult = false;
				gameState = GameState.OVER;
				System.out.println("here");
				return;
			}

			if(!gameplayController.boardExists() && gameplayController.completedAllRecipes()){
				gameResult = true;
				gameState = GameState.OVER;
				return;
			}

			gameplayController.play(delta, inputController);

			if(gameplayController.isPaused()) {
				gameState = GameState.PAUSE;
				pausedState = 0;
			}
			// Check for collisions
			totalTime += (delta * 1000); // Seconds to milliseconds
			if (inputController.didPause() || inputController.didControl()) {
				gameState = GameState.PAUSE;
				pausedState = 1;
			}
		}
	}

	private void drawOver(){
		if (gameState == GameState.OVER) {
			displayFont.setColor(Color.WHITE);
//				canvas.draw(background, canvas.getWidth()/2 - background.getWidth()/2, (canvas.getHeight()/2 - background.getHeight()/2), background.getWidth()/2, background.getHeight()/2);
			float winScreenWidth = canvas.getWidth()/2f; //canvas.getWidth()/2 - 325
			float winScreenHeight = canvas.getHeight()/2f;// canvas.getHeight()/2 - 200
			String text = "Game Over";
			if (gameResult) {
				text = "Level Completed";
			}
			if (gameplayController.keysExist()) {
				canvas.draw(endScreen, 0, 0, canvas.getWidth(), canvas.getHeight());
				westSacFont.getData().setScale(canvas.getHeight()/1440f);
				canvas.drawTextCentered(text,westSacFont, canvas.getHeight()/7f, Color.WHITE);
				keyNum = gameplayController.getKeyNum();
				int keysLeft = 3 - keyNum;
				float keySize = canvas.getHeight() / 10f;
				float keyY = (canvas.getHeight() - keySize / 2f) / 2f;
				float keyOffset = keySize / 6f;
				for (int i = 0; i < keysLeft; i++) {
					canvas.draw(emptyKeyTexture, canvas.getWidth() / 1.67f - keySize * (i + 1) - keyOffset * (i + 1), keyY, keySize, keySize);
				}
				for (int j = keysLeft; j < 3; j++) {
					canvas.draw(keyTexture, canvas.getWidth() / 1.67f - keySize * (j + 1) - keyOffset * (j + 1), keyY, keySize, keySize, Color.WHITE);
				}
			} else {
				canvas.draw(pauseScreen, 0, 0, canvas.getWidth(), canvas.getHeight());
				westSacFont.getData().setScale(canvas.getHeight()/1440f);
				canvas.drawTextCentered(text, westSacFont, 50, Color.WHITE);
			}
			resetFontScale();
//				canvas.drawTextCentered("Press 0 to Restart", displayFont, -30);
			createButtons(gameResult,new Vector2(winScreenWidth / 2, winScreenHeight / 2), new Vector2(winScreenWidth, winScreenHeight));
			for (Button b : buttons) {
				b.draw(canvas);
			}
		}
	}

	private void resetFontScale(){
		westSacFont.getData().setScale(1);
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();
		if (gameState == GameState.INTRO){

		} else {
			gameplayController.draw(canvas);
			Color tint = (inputController.didPause()? Color.GRAY : Color.WHITE);
			canvas.draw(pauseButton, 0, canvas.getHeight() - pauseButton.getHeight()/2f,
					pauseButton.getWidth() /2f, pauseButton.getHeight()/2f, tint);

			Color tintQ = (inputController.didControl()? Color.GRAY : Color.WHITE);
			canvas.draw(questionMark, pauseButton.getWidth()*3/4f, canvas.getHeight() - pauseButton.getHeight()/2f,
					pauseButton.getWidth() /2f, pauseButton.getHeight()/2f, tintQ);
			drawOver();
		}
		// Flush information to the graphic buffer.
		canvas.end();
	}

	private void createButtons(boolean gS, Vector2 winSPos, Vector2 winSSize) {
		westSacFont.getData().setScale(canvas.getHeight()/3600f);
		buttons = new ArrayList<>();
		String[] basicButtons = {"settingsReturn", "mapReturn"};
		Texture[] basicButtonsTexture = {settingsReturn, mapReturn};
		String[] basicButtonsText = {"Settings", "Return to Map"};
		String[] basicButtonsExit = {"5", "2"};

		float screenStart = winSPos.x - winSSize.x/2;
		float screenEnd = winSPos.x + winSSize.x/2;
		float bWidthPadding = winSSize.x / 12f;
		float bWidth = bWidthPadding * 2.5f;
		float bWidthOffset = (winSSize.x - bWidth*3 - bWidthPadding*2) / 3;
		float bHeight = winSSize.y / 7f;
		Vector2 pos;
		if(!gameplayController.keysExist()) {
			pos = new Vector2(winSPos.x + bWidthPadding, winSPos.y + bHeight * 2.25f);
		}else{
			pos = new Vector2(winSPos.x + bWidthPadding, winSPos.y + bHeight * 1.15f);
		}
		if (gS && currLev < lastLevel) {
			buttons.add(new Button(bWidth, bHeight, false, "Next Level", westSacFont, "1", pos,50, nextLevelReturn, Color.WHITE));
		} else {
			buttons.add(new Button(bWidth, bHeight, false, "Retry", westSacFont, "3", pos,50, retryReturn, Color.WHITE));
		}
		pos = new Vector2(pos.x + bWidthOffset + bWidth, pos.y);
		for (int i = 0; i < basicButtons.length; i++) {
			if (i == 1) {
				bWidth *= 1.3f;
			}
			buttons.add(new Button(bWidth, bHeight, false, basicButtonsText[i], westSacFont, basicButtonsExit[i], pos, 50, basicButtonsTexture[i], Color.WHITE));
			pos = new Vector2(pos.x + (bWidthOffset + bWidth)*(i+1), pos.y);
		}
		Gdx.input.setInputProcessor( this );
		// Let ANY connected controller start the game.
//		for (XBoxController controller : Controllers.get().getXBoxControllers()) {
//			controller.addListener( this );
//		}

	}

	public boolean keysExist(){
		return gameplayController.keysExist();
	}

	private void createPauseButtons(Vector2 winSPos, Vector2 winSSize){
		westSacFont.getData().setScale(canvas.getHeight()/3600f);
		buttons = new ArrayList<>();
		String[] basicButtons = {"resumeReturn", "settingsReturn", "mapReturn"};
		Texture[] basicButtonsTexture = {resumeReturn, settingsReturn, mapReturn};
		String[] basicButtonsText = {"Resume", "Settings", "Return to Map"};
		String[] basicButtonsExit = {"4", "5", "2"};
		float bWidthPadding = winSSize.x / 12f;
		float bWidth = bWidthPadding * 2.5f;
		float bWidthOffset = (winSSize.x - bWidth*3 - bWidthPadding*2) / 3;
		float bHeight = winSSize.y / 7f;

		Vector2 pos = new Vector2(winSPos.x + bWidthPadding,  winSPos.y + bHeight*2.25f);
		for(int i = 0; i < basicButtons.length; i++){
			if(i == 2){
				bWidth *= 1.3f;
			}
			buttons.add(new Button(bWidth, bHeight, false, basicButtonsText[i], westSacFont, basicButtonsExit[i], pos, 100, basicButtonsTexture[i], Color.WHITE));
			pos = new Vector2(pos.x + (bWidthOffset + bWidth), pos.y);
		}
		Gdx.input.setInputProcessor(this);
	}

	private void drawPauseScreen(){
		canvas.begin();
		if (pausedState == 1){
			if (!inputController.didPause()){
				canvas.draw(pauseScreen, 0, 0, canvas.getWidth(), canvas.getHeight());
				canvas.draw(black, 0,Gdx.graphics.getHeight()/2f-Gdx.graphics.getWidth()*0.365f/2f,
						Gdx.graphics.getWidth(),Gdx.graphics.getWidth()*0.365f);
				canvas.draw(gameControl, 0,Gdx.graphics.getHeight()/2f-Gdx.graphics.getWidth()*0.365f/2f,
						Gdx.graphics.getWidth(),Gdx.graphics.getWidth()*0.365f);
			}
			else {
				canvas.draw(pauseScreen, 0, 0, canvas.getWidth(), canvas.getHeight());
				displayFont.setColor(Color.WHITE);
				westSacFont.getData().setScale(canvas.getHeight() / 1440f);
				canvas.drawTextCentered("PAUSED", westSacFont, canvas.getHeight() / 12f, Color.WHITE);
				int dim = 70;
				float winScreenWidth = canvas.getWidth() / 4f; //canvas.getWidth()/2 - 325
				float winScreenHeight = canvas.getHeight() / 4f;// canvas.getHeight()/2 - 200
				createPauseButtons(new Vector2(winScreenWidth, winScreenHeight), new Vector2(winScreenWidth * 2, winScreenHeight * 2));
				for (Button b : buttons) {
					b.draw(canvas);
				}
		}

		}

		canvas.end();
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			draw();
			if (gameState == GameState.OVER && gameResult && gameplayController.keysExist() && !winKeySaved) {
				listener.exitScreen(this, 100);
				return;
			}

			if (selectedButton != null) {
				if(selectedButton.getConnectedFile() == "1"){
					bgm.stop();
				}else{
					bgm.pause();
				}
				String val = selectedButton.getConnectedFile();
				selectedButton = null;
				buttons = null;
				listener.exitScreen(this, Integer.valueOf(val));
				return;
			}

			if(gameState == GameState.PAUSE && pausedState == 1 && !inputController.didControl() && !inputController.didPause()) {
				listener.exitScreen(this, 4);
				return;
			}

			if(gameState == GameState.PAUSE && pausedState == 0 && !gameplayController.isPaused()) {
				listener.exitScreen(this, 4);
				return;
			}

			updateVolume(musicvolume,soundvolume);
			update(delta);
			if(gameState == GameState.PAUSE){
				drawPauseScreen();
			}
			if (inputController.didExit() && listener != null) {
				listener.exitScreen(this, 0);
			}
		}
	}

	/**
	 * Called when the Screen is paused.
	 *
	 * This is usually when it's not active or visible on screen. An Application is
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub
		bgm.pause();
		drawPauseScreen();
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
		gameState = GameState.PLAY;
		bgm.setVolume(bgmID, musicvolume);
		bgm.resume();
		gameplayController.setVolumes(musicvolume,soundvolume);
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the ClickListener for this mode
	 *
	 * The ClickListener will respond to requests to quit.
	 */
	public void setClickListener(ClickListener listener) {
		this.clickListener = listener;
	}

	// input processor methods
	/**
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the click
	 * is in the bounds of the play button.  If so, it signals the that the button
	 * has been pressed and is currently down. Any mouse button is accepted.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (buttons != null) {
			screenY = canvas.getHeight() - screenY;
			for (Button b : buttons) {
				float xAreaLow = b.getPosition().x - b.getWidth() ;
				float xAreaHigh = b.getPosition().x + b.getWidth();
				float yAreaLow = b.getPosition().y - b.getHeight();
				float yAreaHigh = b.getPosition().y + b.getHeight();

				if (screenX >= xAreaLow && screenX <= xAreaHigh && screenY >= yAreaLow && screenY <= yAreaHigh) {
					selectedButton = b;

					pressState = 1;
					return true;
				}

			}
		}

		if(gameState == GameState.PLAY || gameState == GameState.PAUSE){
			if(pressState == 2){
				return true;
			}
		}
		if(gameState == GameState.PLAY){
			if(screenX < pauseButton.getWidth() * 0.8 && screenY < canvas.getHeight() - pauseButton.getHeight() * 0.8f){
				pressState = 1;
			}
		}
		return false;
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
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pressState == 1){
			pressState = 2;
			return false;
		}
		return true;
	}

	//ignore for now
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}