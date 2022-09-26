/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize.playmode;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.assets.*;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.util.*;
import org.w3c.dom.Text;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LoadingMode implements Screen, InputProcessor, ControllerListener {
	// There are TWO asset managers.  One to load the loading screen.  The other to load the assets
	/** Internal assets for this loading screen */
	private AssetDirectory internal;
	/** The actual assets to be loaded */
	private AssetDirectory assets;
	
	/** Background texture for start-up */
	private Texture background;
	/** Texture atlas to support a progress bar */
	private Texture statusBar;
	/** Texture atlas to draw titleText */
	private Texture titleText;
	/** Texture atlas to draw play button */
	private Texture playbutton;
	/** Texture atlas to draw settingsbutton */
	private Texture settingsbutton;
	/** Texture atlas to draw credits button */
	private Texture creditsbutton;
	/** Texture atlas to draw control button */
	private Texture controlbutton;
	/** Texture atlas to draw exit button */
	private Texture exitbutton;

	/** Texture for interlveel screen stars */
	private Texture stars;
	/** Texture for interlveel screen truck */
	private Texture truck;
	/** Backgorund texture for interlevel screens */
	private Texture levelBackground;
	private Texture enemy;
	private Texture whitebar;



	
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

	/** Default budget for asset loader (do nothing but load 60 fps) */
	private static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	/** Ratio of the bar width to the screen */
	private static float BAR_WIDTH_RATIO  = 0.66f;
	/** Ration of the bar height to the screen */
	private static float BAR_HEIGHT_RATIO = 0.25f;	
	/** Height of the progress bar */
	private static int PROGRESS_HEIGHT = 30;
	/** Width of the rounded cap on left or right */
	private static int PROGRESS_CAP    = 15;
	/** Width of the middle portion in texture atlas */
	private static int PROGRESS_MIDDLE = 200;
	private static float BUTTON_SCALE  = 0.75f;
	
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The width of the progress bar */
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** Scaling factor for when the student changes the resolution. */
	private float scale;

	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** The current state of the play button */
	private int   pressState;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int   budget;
	/** Whether the loading animation is over or not */
	private boolean   isLoaded;

	/** Whether or not this player mode is still active */
	private boolean active;
	/** How long user will stay in this loading screen */
	private int timeLimit = -500;
	private int TIMELIMIT;

//	private int levelToLoad;

//	public int getLevelToLoad() {
//		return levelToLoad;
//	}

	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}
	
	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState > 5;
	}

	/**
	 * Returns the asset directory produced by this loading screen
	 *
	 * This asset loader is NOT owned by this loading scene, so it persists even
	 * after the scene is disposed.  It is your responsbility to unload the
	 * assets in this directory.
	 *
	 * @return the asset directory produced by this loading screen
	 */
	public AssetDirectory getAssets() {
		return assets;
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param canvas 	The game canvas to draw to
	 * @param timeLimit The amount of time to show this loading screen
	 */
	public LoadingMode(GameCanvas canvas, int timeLimit) {
		this.canvas = canvas;
		TIMELIMIT = timeLimit;
		this.timeLimit = timeLimit;
		internal = new AssetDirectory( "settings.json" );
		internal.loadAssets();
		internal.finishLoading();
		levelBackground = internal.getEntry("white", Texture.class);
		stars = internal.getEntry("stars", Texture.class);
		truck = internal.getEntry("truck", Texture.class);
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param canvas 	The game canvas to draw to
	 * @param timeLimit The amount of time to show this loading screen
	 *
	 */
	public LoadingMode(GameCanvas canvas, int timeLimit, String s) {
		this.canvas = canvas;
		TIMELIMIT = timeLimit;
		this.timeLimit = timeLimit;
		internal = new AssetDirectory( s );
		internal.loadAssets();
		internal.finishLoading();
		levelBackground = internal.getEntry("white", Texture.class);
		enemy = internal.getEntry("enemy", Texture.class);
		whitebar = internal.getEntry("whiteprogressbar", Texture.class);
	}

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param file  	The asset directory to load in the background
	 * @param canvas 	The game canvas to draw to
	 * @param millis The loading budget in milliseconds
	 */
	public LoadingMode(String file, GameCanvas canvas, int millis) {
		this.canvas  = canvas;
		budget = millis;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight()); //canvas.getHeight() * 2 / 3

		// We need these files loaded immediately
		internal = new AssetDirectory( "loading.json" );
		internal.loadAssets();
		internal.finishLoading();

		// Load the next two images immediately.
		isLoaded = false;
		background = internal.getEntry( "background", Texture.class );
		background.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		statusBar = internal.getEntry( "progress", Texture.class );

		// Load mainMenu screen
		titleText = internal.getEntry("titletext", Texture.class);
		titleText.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		playbutton = internal.getEntry("playbutton", Texture.class);
		playbutton.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		settingsbutton = internal.getEntry("settingsbutton", Texture.class);
		settingsbutton.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		controlbutton = internal.getEntry("controlbutton", Texture.class);
		controlbutton.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		creditsbutton = internal.getEntry("creditsbutton", Texture.class);
		creditsbutton.setFilter( TextureFilter.Linear, TextureFilter.Linear );
		exitbutton = internal.getEntry("exitbutton", Texture.class);
		exitbutton.setFilter( TextureFilter.Linear, TextureFilter.Linear );



		// Break up the status bar texture into regions
		statusBkgLeft = internal.getEntry( "progress.backleft", TextureRegion.class );
		statusBkgRight = internal.getEntry( "progress.backright", TextureRegion.class );
		statusBkgMiddle = internal.getEntry( "progress.background", TextureRegion.class );

		statusFrgLeft = internal.getEntry( "progress.foreleft", TextureRegion.class );
		statusFrgRight = internal.getEntry( "progress.foreright", TextureRegion.class );
		statusFrgMiddle = internal.getEntry( "progress.foreground", TextureRegion.class );

		// No progress so far.
		progress = 0;
		pressState = 0;

		Gdx.input.setInputProcessor( this );

		// Let ANY connected controller start the game.
		for (XBoxController controller : Controllers.get().getXBoxControllers()) {
			controller.addListener( this );
		}

		// Start loading the real assets
		assets = new AssetDirectory( file );
		assets.loadAssets();
		active = true;
	}

	public void setTimer(int i){
		TIMELIMIT = i;
		timeLimit = i;
	}

	public void resetTimer(){
		timeLimit = TIMELIMIT;
	}
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		internal.unloadAssets();
		internal.dispose();
	}
	
	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
		if (!isLoaded && timeLimit < -20) {
			assets.update(budget);
			this.progress = assets.getProgress();
			if (progress >= 1.0f) {
				this.progress = 1.0f;
				isLoaded = true;
			}
		}
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
		if (timeLimit < -20){
			canvas.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			if (assets != null && !isLoaded) {
				drawProgress(canvas);
			} else {
				canvas.draw(titleText, 0.088f*canvas.getWidth(), 0.186f*canvas.getHeight(),
					canvas.getWidth()*0.2875f, canvas.getHeight()*0.37f);
				canvas.draw(settingsbutton, centerX - canvas.getWidth()*0.104f, 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f,
						canvas.getWidth()*0.208f, canvas.getHeight()*0.0925f,
						(pressState == 2 ? Color.GRAY: Color.WHITE));
				canvas.draw(controlbutton, centerX - canvas.getWidth()*0.104f, (float) ((0.62-0.0925*2.5)*canvas.getHeight()),
						canvas.getWidth()*0.208f, canvas.getHeight()*0.0925f,
						(pressState == 3 ? Color.GRAY: Color.WHITE));
				canvas.draw(creditsbutton, centerX - canvas.getWidth()*0.104f, (float) ((0.62-0.0925*4)*canvas.getHeight()),
						canvas.getWidth()*0.208f, canvas.getHeight()*0.0925f,
						(pressState == 4 ? Color.GRAY: Color.WHITE));
				canvas.draw(exitbutton, centerX - canvas.getWidth()*0.104f, (float) ((0.62-0.0925*5.5)*canvas.getHeight()),
						canvas.getWidth()*0.208f, canvas.getHeight()*0.0925f,
						(pressState == 5 ? Color.GRAY: Color.WHITE));
				canvas.draw(playbutton, centerX - canvas.getWidth()*0.104f, (float) ((0.62+0.0925*0.5)*canvas.getHeight()),
						canvas.getWidth()*0.208f, canvas.getHeight()*0.0925f,
						(pressState == 1 ? Color.GRAY: Color.WHITE));
			}
		} else {
			timeLimit--;
			canvas.draw(levelBackground,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),Color.BLACK);
			float starRadius = Gdx.graphics.getHeight()*3/5f;
			if (truck != null){
				canvas.draw(stars,new Color(1,1,1,(120f-timeLimit)/120f), starRadius/2f,starRadius/2f,
						Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f, starRadius, starRadius);
				float truckWidth = Gdx.graphics.getWidth()/3f;
				float truckHeight = truckWidth*truck.getHeight()/truck.getWidth();
				canvas.draw(truck,Color.WHITE, truckWidth/2f,truckHeight/2f,
						Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f, truckWidth, truckHeight);
			} else {
				float enemyWidth = Gdx.graphics.getWidth()/15f;
				float enemyHeight = enemyWidth*enemy.getHeight()/enemy.getWidth();
				canvas.draw(enemy,Color.WHITE, enemyWidth/2f,enemyHeight/2f,
						Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f, enemyWidth, enemyHeight);
				canvas.draw(whitebar, new Color(146/255f,247/255f,62/85f,1f),
						5/32f*Gdx.graphics.getWidth(),5/144f*Gdx.graphics.getHeight(),
						Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/4f,
						5/16f*Gdx.graphics.getWidth(),5/72f*Gdx.graphics.getHeight());
				canvas.draw(whitebar, new Color(44/51f,103/255f,26/51f,1f),
						5/32f*Gdx.graphics.getWidth(),5/144f*Gdx.graphics.getHeight(),
						Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/4f,
						(float) ((1-Math.pow(timeLimit/90f,3))*5/16f*Gdx.graphics.getWidth()),5/72f*Gdx.graphics.getHeight());
			}

		}
		canvas.end();
	}

	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, 
	 * and a rectangle in a middle.  We adjust the size of the rectangle in
	 * the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */	
	private void drawProgress(GameCanvas canvas) {	
		canvas.draw(statusBkgLeft,   centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		canvas.draw(statusBkgRight,  centerX+width/2-scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		canvas.draw(statusBkgMiddle, centerX-width/2+scale*PROGRESS_CAP, centerY, width-2*scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);

		canvas.draw(statusFrgLeft,   centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		if (progress > 0) {
			float span = progress*(width-2*scale*PROGRESS_CAP)/2.0f;
			canvas.draw(statusFrgRight,  centerX-width/2+scale*PROGRESS_CAP+span, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
			canvas.draw(statusFrgMiddle, centerX-width/2+scale*PROGRESS_CAP, centerY, span, scale*PROGRESS_HEIGHT);
		} else {
			canvas.draw(statusFrgRight,  centerX-width/2+scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		}
	}

	// ADDITIONAL SCREEN METHODS
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
			if (assets != null) {
				update(delta);
			}
			draw();

			// We are are ready, notify our listener
			if (isReady() && listener != null) {
				pressState = 0;
				listener.exitScreen(this, 0);
			} else if (pressState == 2 || timeLimit == 0) {
				pressState = 0;
				listener.exitScreen(this, 1);
			} else if (pressState == 4) {
				pressState = 0;
				listener.exitScreen(this, 9);
			} else if (pressState == 5) {
				pressState = 0;
				listener.exitScreen(this, 10);
			} else if (pressState == 3) {
				pressState = 0;
				listener.exitScreen(this, 11);
			}
		}
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
		// Compute the drawing scale
		float sx = ((float)width)/STANDARD_WIDTH;
		float sy = ((float)height)/STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);
		
		this.width = (int)(BAR_WIDTH_RATIO*width);
		centerY = (int)(BAR_HEIGHT_RATIO*height);
		centerX = width/2;
		heightY = height;
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

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
		Gdx.input.setInputProcessor( this );
		this.listener = listener;
	}
	
	// PROCESSING PLAYER INPUT
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!isLoaded || pressState == 6) {
			return true;
		}

		// Flip to match graphics coordinates
		screenY = heightY-screenY;

		if (screenX >= centerX - canvas.getWidth()*0.104f && screenX <= centerX + canvas.getWidth()*0.104f){
			if (screenY >= 0.62f*canvas.getHeight() + canvas.getHeight()*0.0925f*0.5
					&& screenY <= 0.62f*canvas.getHeight() + canvas.getHeight()*0.0925f*1.5){
				pressState = 1;
			}
			else if (screenY >= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f && screenY <= 0.62f*canvas.getHeight()){
				// Play button
				pressState = 2;
			} else if (screenY >= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*2.5
					&& screenY <= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*1.5){
				// Settings Button
				pressState = 3;
			} else if (screenY >= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*4
					&& screenY <= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*3){
				// Controls Button
				pressState = 4;
			} else if (screenY >= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*5.5
					&& screenY <= 0.62f*canvas.getHeight() - canvas.getHeight()*0.0925f*4.5){
				// Credits Button
				pressState = 5;
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
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pressState !=0 && pressState <= 5 && pressState != 2) {
			pressState+= 5;
			return false;
		}
		return true;
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
	public boolean buttonDown (Controller controller, int buttonCode) {
		if (pressState == 0) {
			ControllerMapping mapping = controller.getMapping();
			if (mapping != null && buttonCode == mapping.buttonStart ) {
				pressState = 1;
				return false;
			}
		}
		return true;
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
	public boolean buttonUp (Controller controller, int buttonCode) {
		if (pressState == 1) {
			ControllerMapping mapping = controller.getMapping();
			if (mapping != null && buttonCode == mapping.buttonStart ) {
				pressState = 2;
				return false;
			}
		}
		return true;
	}
	
	// UNSUPPORTED METHODS FROM InputProcessor

	/** 
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode the key pressed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyDown(int keycode) { 
		return true; 
	}

	/** 
	 * Called when a key is typed (UNSUPPORTED)
	 *
	 * @param character the key typed
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean keyTyped(char character) { 
		return true; 
	}

	/** 
	 * Called when a key is released (UNSUPPORTED)
	 *
	 * @param keycode the key released
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean keyUp(int keycode) { 
		return true; 
	}
	
	/** 
	 * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners. 
	 */	
	public boolean mouseMoved(int screenX, int screenY) { 
		return true; 
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
		return true;
	}

	/** 
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 * @param pointer the button or touch finger number
	 * @return whether to hand the event to other listeners. 
	 */		
	public boolean touchDragged(int screenX, int screenY, int pointer) { 
		return true; 
	}
	
	// UNSUPPORTED METHODS FROM ControllerListener
	
	/**
	 * Called when a controller is connected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void connected (Controller controller) {}

	/**
	 * Called when a controller is disconnected. (UNSUPPORTED)
	 *
	 * @param controller The game controller
	 */
	public void disconnected (Controller controller) {}

	/** 
	 * Called when an axis on the Controller moved. (UNSUPPORTED) 
	 *
	 * The axisCode is controller specific. The axis value is in the range [-1, 1]. 
	 *
	 * @param controller The game controller
	 * @param axisCode 	The axis moved
	 * @param value 	The axis value, -1 to 1
	 * @return whether to hand the event to other listeners. 
	 */
	public boolean axisMoved (Controller controller, int axisCode, float value) {
		return true;
	}

}