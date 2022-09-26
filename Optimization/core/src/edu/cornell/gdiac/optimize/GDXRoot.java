/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * This time we shown how to use Game with player modes.  The player modes are 
 * implemented by screens.  Player modes handle their own rendering (instead of the
 * root class calling render for them).  When a player mode is ready to quit, it
 * notifies the root class through the ScreenListener interface.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.optimize.playmode.GameMode;
import edu.cornell.gdiac.optimize.playmode.LoadingMode;
import edu.cornell.gdiac.optimize.playmode.MenuMode;
import edu.cornell.gdiac.util.*;

import com.badlogic.gdx.*;
import edu.cornell.gdiac.assets.*;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Screen between levels */
	private LoadingMode interlevelscreen;
	private LoadingMode starsavingscreen;

	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private GameMode playing;
	/** Player mode for starting game*/
	private MenuMode starting;
	/** Player mode for level selection */
	private MenuMode levelSelecting;

	private MenuMode settings;
	private MenuMode credits;
	private MenuMode control;

	private float musicvolume;
	private float soundvolume;
	private boolean playToSetting = false;

	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {}

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
//		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json",canvas,1);
		loading.setTimer(-500);
		playing = new GameMode(canvas);
		playing.updateVolume(1,1);
//		starting = new MenuMode(canvas, "startScreen.json", 1, 3, 1);
//		Vector2 startPos = new Vector2(canvas.getWidth()/7f, canvas.getHeight() - canvas.getHeight()/3f); // - bTexture
//		Vector2 endPos =  new Vector2(canvas.getWidth() - canvas.getWidth()/7f, canvas.getHeight()/3f);

		levelSelecting = new MenuMode(canvas, "levels.json", "keys.json",0, 5, 4); // level selecting exit code = 4
		playing.setLastLevel(levelSelecting.getLastUnlockedLevel());
		settings = new MenuMode(canvas, "settings.json",-1,-1,-1);
		credits = new MenuMode(canvas,"credits.json",-2);
		control = new MenuMode(canvas,"control.json",-3);
		settings.populate();
		credits.populateCredits();
		control.populateControl();

		interlevelscreen = new LoadingMode(canvas, 90,"loading.json");
		starsavingscreen = new LoadingMode(canvas, 120);

		// find first level button position
		Vector2 levStartPos = new Vector2(canvas.getWidth()/7f, canvas.getHeight() - canvas.getHeight()/3f); // - bTexture
		Vector2 levEndPos =  new Vector2(canvas.getWidth() - canvas.getWidth()/7f, canvas.getHeight()/3f);
		levelSelecting.setButton(levStartPos, levEndPos, 170, 170 * 250 / 279f);
		levelSelecting.populateStars();
		levelSelecting.populateLevelButtons();
		levelSelecting.populateFlipButtons();

		musicvolume = 1.0f;
		soundvolume = 1.0f;
    
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		Screen screen = getScreen();
		setScreen(null);
		screen.dispose();
		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		if (directory != null) {
//			directory.unloadAssets();
//			directory.dispose();
//			directory = null;
		}
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	public float getMusicvolume(){
		return musicvolume;
	}

	public float getSoundvolume(){
		return soundvolume;
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
//		if (exitCode != 0 && exitCode != 2) {
//			Gdx.app.error("GDXRoot", "Exit with error code "+exitCode, new RuntimeException());
//			Gdx.app.exit();
//		} else
		if (exitCode == -2 && screen == settings){
			musicvolume = settings.getMusicVolume();
			soundvolume = settings.getSoundVolume();
			settings.setScreenListener(this);
			setScreen(settings);
		} else if (screen == loading && exitCode == 0) {
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();
			levelSelecting.setMusicVolume(musicvolume);
			levelSelecting.setSoundVolume(soundvolume);
			levelSelecting.setScreenListener(this);
			setScreen(levelSelecting);
		} else if ((screen == loading && exitCode == 1) || (screen == playing && exitCode == 5)) {
			if (screen == loading){
				loading.resetTimer();
			}
			if (screen == playing) {
//				playing.dispose();
//				canvas = new GameCanvas();
//				playing = new GameMode(canvas);
				playToSetting = true;
			}
			settings.setScreenListener(this);
			setScreen(settings);
		} else if (exitCode == 9){
			credits.setScreenListener(this);
			setScreen(credits);
		} else if (exitCode == 11){
			control.setScreenListener(this);
			setScreen(control);
		}else if (exitCode == 6) {
			musicvolume = settings.getMusicVolume();
			soundvolume = settings.getSoundVolume();
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();

			if (playToSetting){
				playing.updateVolume(musicvolume, soundvolume);
				playing.setScreenListener(this);
				setScreen(playing);
				playToSetting = false;
			} else {
				loading.setScreenListener(this);
				setScreen(loading);
			}
		} else if (screen == levelSelecting && exitCode == 0) {
			playing.setLevel(levelSelecting.getSelectedButtonContent());
			playing.updateVolume(musicvolume,soundvolume);
			playing.setGameplayController();
			playing.setScreenListener(this);
			directory = loading.getAssets();
			playing.populate(directory);
			setScreen(playing);
		} else if(screen == levelSelecting && exitCode == 7){
			levelSelecting.nextPage();
			levelSelecting.resetSelectedButton();
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();
		} else if(screen == levelSelecting && exitCode == 8){
			levelSelecting.backPage();
			levelSelecting.resetSelectedButton();
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();
		} else if (screen == playing && exitCode == 2){ //level selector
			levelSelecting.resetSelectedButton();
			if (playing.keysExist()){
				playing.setWinKeySaved(true);
				if (levelSelecting.getLevelKeyNum(playing.getCurrLev()-1) < playing.getKeyNum()) {
					levelSelecting.writeKeysToFile(playing.getCurrLev() - 1, playing.getKeyNum());
					levelSelecting.clearButtons();
					levelSelecting.reloadKeys("keys.json");
				}
			}
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();
			playing.dispose();
			canvas = new GameCanvas();
			playing = new GameMode(canvas);
			playing.setLastLevel(levelSelecting.getLastUnlockedLevel());


//			levelSelecting.clearButtons();
//			levelSelecting.reloadKeys("keys.json");
//			levelSelecting.populateLevelButtons();
//			levelSelecting.populateFlipButtons();
			levelSelecting.resume();
			levelSelecting.setScreenListener(this);
			setScreen(levelSelecting);
		} else if(exitCode == 4){ //resume
			levelSelecting.populateLevelButtons();
			levelSelecting.populateFlipButtons();
			playing.resume();
		} else if (exitCode == 3) { // retry
			String currLev = playing.getCurrLev() + ".json";
			playing.dispose();
			canvas = new GameCanvas();
			playing = new GameMode(canvas);
			playing.setLastLevel(levelSelecting.getLastUnlockedLevel());
			playing.setLevel(currLev);
			playing.updateVolume(musicvolume,soundvolume);
			playing.setGameplayController();
			playing.setScreenListener(this);
			directory = loading.getAssets();
			playing.populate(directory);
			setScreen(playing);
		} else if (exitCode == 1 && screen == playing) {
			interlevelscreen.setScreenListener(this);
			setScreen(interlevelscreen);
		} else if (exitCode == 1 && screen == interlevelscreen) { // next level
			interlevelscreen.resetTimer();
			int nextLev = Math.min(playing.getCurrLev()+1, levelSelecting.getLevels().size());
			String nextLevFile = nextLev+".json";
			playing.dispose();
			canvas = new GameCanvas();
			playing = new GameMode(canvas);
			playing.setLastLevel(levelSelecting.getLastUnlockedLevel());
			playing.setLevel(nextLevFile);
			playing.updateVolume(musicvolume,soundvolume);
			playing.setGameplayController();
			playing.setScreenListener(this);
			directory = loading.getAssets();
			playing.populate(directory);
			setScreen(playing);
		} else if (screen == playing && exitCode == 100) { // save keys (win screen)
			if (playing.keysExist()){
				playing.setWinKeySaved(true);
				if (levelSelecting.getLevelKeyNum(playing.getCurrLev()-1) < playing.getKeyNum()) {
					levelSelecting.writeKeysToFile(playing.getCurrLev() - 1, playing.getKeyNum());
					levelSelecting.clearButtons();
					levelSelecting.reloadKeys("keys.json");
					levelSelecting.populateLevelButtons();
					levelSelecting.populateFlipButtons();
					playing.setLastLevel(levelSelecting.getLastUnlockedLevel());
				}
			}
		} else if (exitCode == 10) {
			loading.dispose();
			loading = null;
			// We quit the main application
			Gdx.app.exit();
		} else {
			loading.dispose();
			loading = null;
			// We quit the main application
			Gdx.app.exit();
		}
//		else if (screen == starsavingscreen && exitCode == 1) {
//			playing.setScreenListener(this);
//			setScreen(playing);
//			starsavingscreen.resetTimer();
//		}
	}

}
