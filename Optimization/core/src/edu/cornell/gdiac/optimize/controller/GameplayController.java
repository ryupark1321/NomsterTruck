/*
 * GameplayController.java
 *
 * For many of you, this class will seem like the most unusual one in the entire project.
 * It implements a lot of functionality that looks like it should go into the various
 * GameObject subclasses. However, a lot of this functionality involves the creation or
 * destruction of objects.  We cannot do this without a lot of cyclic dependencies,
 * which are bad.
 *
 * You will notice that gameplay-wise, most of the features in this class are
 * interactions, not actions. This demonstrates why a software developer needs to
 * understand the difference between these two.
 *
 * You will definitely need to modify this file in Part 2 of the lab. However, you are
 * free to modify any file you want.  You are also free to add new classes and assets.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.optimize.GameCanvas;
import edu.cornell.gdiac.optimize.GameObject;
import edu.cornell.gdiac.optimize.entity.*;
import edu.cornell.gdiac.optimize.kitchen.*;
import edu.cornell.gdiac.optimize.map.*;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.ArrayList;

/**
 * Controller to handle gameplay interactions.
 * </summary>
 * <remarks>
 * This controller also acts as the root class for all the models.
 */
public class GameplayController {
	// =========================== Gameplay ===========================
	/**
	 * Reference to player (need to change to allow multiple players)
	 */
	private Truck player;
	/**
	 * Handles all kitchen related stuff (CONTROLLER CLASS)
	 */
	private KitchenController kitchenController;
	/**
	 * Handles all enemy related stuff (CONTROLLER CLASS)
	 */
	private AIController aiController;
	/**
	 * The board on which the game is played
	 */
	private Board board;
	/**
	 * The currently active object
	 */
	private Array<GameObject> objects;
	private int width;
	private int height;
	private float musicvolume;
	private float soundvolume;

	private int levelNum;
	private int tutorialState;
	private static final int[] LEVEL_INFO_3 = new int[] {0,120,240,42,600,600};

	// JSON related variables
	/**
	 * Json Reader for populating level from level data file
	 */
	JsonReader json = new JsonReader();
	/**
	 * Json value of the current level data file
	 */
	JsonValue level;
	/**
	 * Available moves for the player
	 */
	private Array<Integer> currentMove;

	// =========================== Textures ===========================

	// Truck
	/**
	 * Texture for truck facing north
	 */
	private Texture truckTextureN;
	/**
	 * Texture for truck facing northeast
	 */
	private Texture truckTextureNE;
	/**
	 * Texture for truck facing southeast
	 */
	private Texture truckTextureSE;
	/**
	 * Texture for truck facing south
	 */
	private Texture truckTextureS;
	/**
	 * Texture for truck facing southwest
	 */
	private Texture truckTextureSW;
	/**
	 * Texture for truck facing northwest
	 */
	private Texture truckTextureNW;

	// Obstacles
	/**
	 * Texture for placeholder obstacle tile
	 */
	private Texture obstacleTexture;
	/**
	 * Texture for first type of obstacle
	 */
	private Texture obs1;
	/**
	 * Texture for second type of obstacle
	 */
	private Texture obs2;
	/**
	 * Texture for third type of obstacle
	 */
	private Texture obs3;

	// Board and Background
	/**
	 * Texture for tiles on the board
	 */
	private Texture tileTexture;
	/**
	 * Texture for the background
	 */
	private Texture background;
	/**
	 * Texture for the fireflies filter
	 */
	private Texture filter;
	/**
	 * Texture for the exit sign
	 */
	private Texture exitSign;


	// Rewards and Movements
	/**
	 * Texture for the reward plank
	 */
	private Texture rewardPlank;
	/**
	 * Texture for forward movement icon
	 */
	private Texture forwardTexture;
	/**
	 * Texture for turn movement icon
	 */
	private Texture turnTexture;
	/**
	 * Texture for throw icon
	 */
	private Texture throwTexture;
	/**
	 * Texture for number on the icon
	 */
	private Texture numberCircle;

	// Timer
	/**
	 * Texture for timer plank
	 */
	private Texture timerPlank;
	/**
	 * Texture for colon
	 */
	private Texture colon;
	/**
	 * Texture for 0
	 */
	private Texture num0;
	/**
	 * Texture for 1
	 */
	private Texture num1;
	/**
	 * Texture for 2
	 */
	private Texture num2;
	/**
	 * Texture for 3
	 */
	private Texture num3;
	/**
	 * Texture for 4
	 */
	private Texture num4;
	/**
	 * Texture for 5
	 */
	private Texture num5;
	/**
	 * Texture for 6
	 */
	private Texture num6;
	/**
	 * Texture for 7
	 */
	private Texture num7;
	/**
	 * Texture for 8
	 */
	private Texture num8;
	/**
	 * Texture for 9
	 */
	private Texture num9;
	/**
	 * Middle portion of the status background (grey region)
	 */
	private TextureRegion statusBkgMiddle;
	/**
	 * Middle portion of the status forground (colored region)
	 */
	private TextureRegion statusFrgMiddle;
	/**
	 * Texture for key
	 */
	private Texture keyTexture;
	private Texture emptyKeyTexture;
	private Texture nextButton;
	private Texture tutorialDownRightArrow;

	private Texture cardRewardForward;
	private Texture cardRewardThrow;
	private Texture cardRewardTurn;

	// =========================== Constants ===========================

	/**
	 * Absolute value is the opacity of the filter
	 */
	private float opacity = -1.0f;
	/**
	 * Board position of the exit
	 */
	private Vector2 exitPos;
	/**
	 * Screen position of the exit sign
	 */
	private Vector2 exitSignPos;
	/**
	 * Number of frames in a row in truck animation strip
	 */
	private static final int TRUCK_COLS = 5;
	/**
	 * Number of rows in truck animation strip
	 */
	private static final int TRUCK_ROWS = 1;
	/**
	 * Number of frames in truck animation strip
	 */
	private static final int TRUCK_FRAME_SIZE = 5;
	/**
	 * Total time in world
	 */
	private static int WORLD_TIMER;
	/**
	 * Time in seconds left in world timer
	 */
	private int worldTimerDisplay;
	/**
	 * Time in frames left in world timer
	 */
	private int worldTimerF;
	/**
	 * Whether board should be created
	 */
	private boolean noBoard;

	// Graphics assets for the entities

	private Texture enemyTexture;

	private Texture truckTexture;

	private BitmapFont displayFont;
	private BitmapFont westSac;
	private BitmapFont westSac45;

	private Texture throwableFoodTexture;

	private boolean isPaused;


	/**
	 * The horizontal position of Rectangle ingredients
	 */
	public static float BOARD_X_POS;
	/**
	 * The vertical position of ingredients
	 */
	public static float ING_POS_Y;

	private boolean threw;
	private Vector2 mouseTileArrayCoord;

	private Sound chop;
	private Sound revving;
	private Sound bgm;
	private Sound star;

	/**
	 * The list contains all the keys on the board
	 */
	private Array<Key> keys;

	private int tutorialTimer;
	private BitmapFont grande;

	/**
	 * Creates a new GameplayController with no active elements.
	 *
	 * @param canvasWidth  Width of the canvas
	 * @param canvasHeight Height of the canvas
	 */
	public GameplayController(int canvasWidth, int canvasHeight, String lev) {
		width = canvasWidth;
		height = canvasHeight;
		// Store json file
		level = json.parse(Gdx.files.internal(lev));

		// Set board size from data file
		int boardQ = Integer.parseInt(level.get("boardQ").toString().split(" ")[1]);
		int boardR = Integer.parseInt(level.get("boardR").toString().split(" ")[1]);
		noBoard = boardQ == 0;

		// Set world timer from data file
		WORLD_TIMER = Integer.parseInt(level.get("worldTimeLimit").toString().split(":")[1].trim());
		worldTimerDisplay = WORLD_TIMER;
		worldTimerF = WORLD_TIMER * 60;

		// Set board exit from data file
		if (!noBoard) {
			String[] ex = level.get("exitTilePos").toString().split(",");
			int exitQ = (int) Float.parseFloat(ex[0].substring(1).split(" ")[1].substring(1));
			int exitR = (int) Float.parseFloat(ex[1].substring(0, ex[1].length() - 2));
			Vector2 exit = new Vector2(exitQ, exitR);
			board = new Board(boardQ, boardR, width, height * 3 / 10f, exit);
		}

		// Initialize objects
		kitchenController = new KitchenController(width, height, levelNum);
		aiController = new AIController(new Array<Enemy>());
		player = null;
		objects = new Array<GameObject>();
		keys = new Array<>();
		isPaused = false;
		tutorialTimer = -1;
		tutorialState = 0;
	}

	/**
	 * Loads JSONdata to kitchen
	 * <p>
	 * The JSON data contains information about recipes, ingredients that are
	 * available on the level.
	 */
	public void loadJSONdata() {
		JsonValue kitchenData = level.get("kitchen");
		kitchenController.loadJSONKitchenData(kitchenData);
	}

	/**
	 * Loads constants to inputController
	 * <p>
	 * Loads appliance position constants from appliance and then passes it
	 * onto inputController
	 *
	 * @param ic
	 */
	public void loadConstantsToInputController(InputController ic) {
		ic.setApplianceConstants(Appliance.getapplianceConstants());
		ic.setIngredientConstants(kitchenController.getIngredientConstants());
	}

	public boolean keysExist() {
		return keys.size > 0;
	}

	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Populates this mode from the given the directory.
	 * <p>
	 * The asset directory is a dictionary that maps string keys to assets.
	 * Assets can include images, sounds, and fonts (and more). This
	 * method delegates to the gameplay controller
	 *
	 * @param directory Reference to the asset directory.
	 */
	public void populate(AssetDirectory directory) {
		loadJSONdata();
		kitchenController.populate(directory);
		background = directory.getEntry("background", Texture.class);
//		truckTexture = directory.getEntry("truck", Texture.class);
//		enemyTexture = directory.getEntry("enemy", Texture.class);
		aiController.populate(directory);
		obstacleTexture = directory.getEntry("obstacle", Texture.class);
//		displayFont = directory.getEntry("times",BitmapFont.class);
		westSac = directory.getEntry("westSac", BitmapFont.class);
		westSac45 = directory.getEntry("westSac45", BitmapFont.class);
		numberCircle = directory.getEntry("numberCircle", Texture.class);

//		truckTexture = directory.getEntry("northeasttruck",Texture.class);
		rewardPlank = directory.getEntry("rewardPlank", Texture.class);

		tileTexture = directory.getEntry("hexTile", Texture.class);

		aiController.populate(directory);
		background = directory.getEntry("background", Texture.class);
		obstacleTexture = directory.getEntry("obstacle", Texture.class);
		filter = directory.getEntry("filter", Texture.class);
		rewardPlank = directory.getEntry("rewardPlank", Texture.class);
		tileTexture = directory.getEntry("hexTile", Texture.class);
		forwardTexture = directory.getEntry("rewardForward", Texture.class);
		turnTexture = directory.getEntry("rewardTurn", Texture.class);
		throwTexture = directory.getEntry("rewardThrow", Texture.class);
		timerPlank = directory.getEntry("timerPlank", Texture.class);
		tutorialDownRightArrow = directory.getEntry("downRightArrow", Texture.class);

		// obstacle textures
		obs1 = directory.getEntry("obstacle1", Texture.class);
		obs2 = directory.getEntry("obstacle2", Texture.class);
		obs3 = directory.getEntry("obstacle3", Texture.class);

		truckTextureN = directory.getEntry("northtruck", Texture.class);
		truckTextureNE = directory.getEntry("northeasttruck", Texture.class);
		truckTextureSE = directory.getEntry("southeasttruck", Texture.class);
		truckTextureS = directory.getEntry("southtruck", Texture.class);
		truckTextureSW = directory.getEntry("southwesttruck", Texture.class);
		truckTextureNW = directory.getEntry("northwesttruck", Texture.class);
		exitSign = directory.getEntry("exitSign", Texture.class);

		cardRewardForward = directory.getEntry("cardRewardForward", Texture.class);
		cardRewardTurn = directory.getEntry("cardRewardTurn", Texture.class);
		cardRewardThrow = directory.getEntry("cardRewardThrow", Texture.class);

		// Timer textures
		colon = directory.getEntry("colonNumber", Texture.class);
		num0 = directory.getEntry("Number0", Texture.class);
		num1 = directory.getEntry("Number1", Texture.class);
		num2 = directory.getEntry("Number2", Texture.class);
		num3 = directory.getEntry("Number3", Texture.class);
		num4 = directory.getEntry("Number4", Texture.class);
		num5 = directory.getEntry("Number5", Texture.class);
		num6 = directory.getEntry("Number6", Texture.class);
		num7 = directory.getEntry("Number7", Texture.class);
		num8 = directory.getEntry("Number8", Texture.class);
		num9 = directory.getEntry("Number9", Texture.class);
		throwableFoodTexture = directory.getEntry("throwableFood", Texture.class);
		nextButton = directory.getEntry("nextButton", Texture.class);

		grande = directory.getEntry("grande", BitmapFont.class);

		chop = directory.getEntry("chop", Sound.class);
		revving = directory.getEntry("revving", Sound.class);
		bgm = directory.getEntry("bgm", Sound.class);
		star = directory.getEntry("star", Sound.class);

		// Break up the status bar texture into regions
		statusBkgMiddle = directory.getEntry("progress.background", TextureRegion.class);
		statusFrgMiddle = directory.getEntry("progress.foreground", TextureRegion.class);
		keyTexture = directory.getEntry("star", Texture.class);
		emptyKeyTexture = directory.getEntry("star2", Texture.class);
		if (!noBoard) {
			board.setTileTexture(tileTexture, obstacleTexture);
			board.setTileFont(new BitmapFont());
			exitPos = board.boardToScreen((int) board.getExitTile().x, (int) board.getExitTile().y);
			exitSignPos = new Vector2(exitPos.x - board.getTileWidth() / 4, exitPos.y);
			board.setProgressTextures(statusBkgMiddle, statusFrgMiddle);
		}
	}

	// ============================ Getter Functions ===========================

	/**
	 * Returns the corresponding obstacle texture
	 *
	 * @param name texture name of the obstacle
	 */
	private Texture getObstacleTexture(String name) {
		if (name.equals("images/obstacle1.png")) {
			return obs1;
		} else if (name.equals("images/obstacle2.png")) {
			return obs2;
		} else if (name.equals("images/obstacle3.png")) {
			return obs3;
		}
		return null;
	}

	/**
	 * Returns the list of the currently active (not destroyed) game objects
	 * <p>
	 * As this method returns a reference and Lists are mutable, other classes can
	 * technical modify this list.  That is a very bad idea.  Other classes should
	 * only mark objects as destroyed and leave list management to this class.
	 *
	 * @return the list of the currently active (not destroyed) game objects
	 */
	public Array<GameObject> getObjects() {
		return objects;
	}

	/**
	 * Returns a reference to the current board
	 *
	 * @return a reference to the current board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Returns a reference to the currently active player.
	 * <p>
	 * This property needs to be modified if you want multiple players.
	 *
	 * @return a reference to the currently active player.
	 */
	public Truck getPlayer() {
		return player;
	}

	/**
	 * Gets world timer display value
	 */
	public int getWorldTimerDisplay() {
		return worldTimerDisplay;
	}

	// ============================ Functions for GameMode ===========================

	/**
	 * Returns true if the currently active player reached the exit tile.
	 *
	 * @return true if the currently active player reached the exit tile.
	 */
	public boolean reachExit() {
		return !noBoard && player.getBoardPosition().equals(board.getExitTile()) && !player.isMoving();
	}

	/**
	 * Returns true if the currently active player is on the same tile as an enemy.
	 *
	 * @return true if the currently active player is on the same tile as an enemy.
	 */
	public boolean playerMetEnemy() {
		if (noBoard) {
			return false;
		}
		Vector2 playerBoardCoord = player.getBoardPosition();
		Vector2 playerArrayCoord = board.boardToArray((int) playerBoardCoord.x, (int) playerBoardCoord.y);
		HexTile playerTile = board.getTileArrayCoord((int) playerArrayCoord.x, (int) playerArrayCoord.y);
		boolean res = playerTile.hasEnemy();
		return res && !player.isMoving();
	}

	/**
	 * This method handles the key collection.
	 */
	public void playerMetKey() {
		Vector2 playerBoardCoord = player.getBoardPosition();
		int currKeyNum = getKeyNum();
		for (Key key : keys) {
			if (playerBoardCoord.equals(key.getTileBoardPos()) && !player.isMoving() && !key.isCollecting()) {
				if(!key.isCollected()) {
					star.play(soundvolume);
				}
				key.setCollected(true, currKeyNum);
			}
		}
	}

	public boolean isCollectingKey() {
		for (Key key : keys) {
			if (key.isCollecting() && !key.isCollected()) {
				return true;

			}
		}
		return false;
	}

	/**
	 * Returns the number of keys which have been collected by the player.
	 *
	 * @return the number of keys which have been collected by the player.
	 */
	public int getKeyNum() {
		int result = 0;
		for (Key key : keys) {
			if (key.isCollected()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Sets the volumes of music and sound
	 *
	 * @param mv music volume
     * @param sv sound volume
	 */
	public void setVolumes(float mv, float sv){
		musicvolume = mv;
		soundvolume = sv;
		kitchenController.setVolume(soundvolume);
	}

	// ============================ Start Function and Helpers ===========================

	/**
	 * Initializes and adds the player to the game
	 * <p>
	 * This method creates a single player, but does nothing else.
	 * This method is a helper function for start.
	 *
	 * @param x Starting x-position for the player
	 * @param y Starting y-position for the player
	 */
	private void initPlayer(float x, float y) {
		player = new Truck();
		player.setDriveSprites(new FilmStrip[]{new FilmStrip(truckTextureN, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),
				new FilmStrip(truckTextureNE, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),
				new FilmStrip(truckTextureSE, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),
				new FilmStrip(truckTextureS, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),
				new FilmStrip(truckTextureSW, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),
				new FilmStrip(truckTextureNW, TRUCK_ROWS, TRUCK_COLS, TRUCK_FRAME_SIZE),}, board);
		player.setFilmStrip(levelNum);
		player.getPosition().set(x, y);
		player.setBoardPosition(board);
		if(levelNum > 8) {
			player.setRevSound(revving);
			player.setSoundVolume(soundvolume);
		}
		objects.add(player);
	}

	/**
	 * Initializes and adds obstacles to the game based on JSON data
	 * <p>
	 * This method creates obstacles from JSON data
	 * This method is a helper function for start.
	 */
	private void initObstacles() {
		for (JsonValue o : level.get("objects").get("obstacles").iterator()) {
			String[] start = o.get("position").toString().split(",");
			int startQ = (int) Float.parseFloat(start[0].split(": ")[1].substring(1));
			int startR = (int) Float.parseFloat(start[1].substring(0, start[1].length() - 2));
			Texture t = getObstacleTexture(o.get("texture").toString().split(": ")[1]);
			board.setObstacle(startQ, startR, player.getBoardPosition(), t);
			if (t != null) {
				Vector2 arrCoord = board.boardToArray(startQ, startR);
				HexTile obTile = board.getBoard()[(int) arrCoord.x][(int) arrCoord.y];
				GameObject temp = new Obstacle(obTile.getX(), obTile.getY(), obTile.getWidth(), obTile.getHeight());
				temp.setTexture(t);
				objects.add(temp);
			}
		}
	}

	/**
	 * Initializes and adds enemies to the game based on JSON data
	 * <p>
	 * This method creates enemies from JSON data
	 * This method is a helper function for start.
	 */
	private void initEnemy() {
		for (JsonValue e : level.get("objects").get("enemies").iterator()) {
			int coolTime = Integer.parseInt(e.get("coolTime").toString().split(": ")[1]);
			String[] start = e.get("startPosition").toString().split(",");
			float startQ = Float.parseFloat(start[0].split(": ")[1].substring(1));
			float startR = Float.parseFloat(start[1].substring(0, start[1].length() - 2));
			Queue path = new Queue();
			for (JsonValue p : e.get("path").iterator()) {
				String[] dir = p.toString().split(",");
				float dirQ = Float.parseFloat(dir[0].substring(1));
				float dirR = Float.parseFloat(dir[1].substring(0, dir[1].length() - 2));
				path.addLast(new Vector2(dirQ, dirR));
			}
			Enemy enemy = new Enemy(coolTime, new Vector2(startQ, startR), board, path);
			aiController.addEnemies(enemy, board);
			objects.add(enemy);
		}
	}

	/**
	 * Initializes and adds keys to the game based on JSON data
	 * <p>
	 * This method creates keys from JSON data
	 * This method is a helper function for start.
	 */
	private void initKeys() {
		for (JsonValue e : level.get("objects").get("keys").iterator()) {
			String[] key = e.toString().split(",");
			float keyQ = Float.parseFloat(key[0].substring(1));
			float keyR = Float.parseFloat(key[1].substring(0, key[1].length() - 2));
			Key newKey = new Key(board, width, height, new Vector2(keyQ, keyR));
			newKey.setTexture(keyTexture);
			keys.add(newKey);
			objects.add(newKey);
		}
	}

	/**
	 * Starts a new game.
	 * <p>
	 * This method creates a single player, but does nothing else.
	 *
	 * @param x Starting x-position for the player
	 * @param y Starting y-position for the player
	 */
	public void start(float x, float y, Board board) {
		// Create the player's ship
		initPlayer(x, y);
		// add obstacles from data file
		initObstacles();
		// add enemies from data file
		initEnemy();
		// add keys from data file
		initKeys();
	}

	// ============================ Reset Function ===========================

	/**
	 * Resets the game, deleting all objects.
	 */
	private void reset() {
		bgm.stop();
		tutorialState = 0;
		player = null;
		keys.clear();
		objects.clear();
		board.reset();
		if (currentMove != null){
			for (int i = 0; i < currentMove.size; i++){
				currentMove.set(i,0);
			}
		}
		aiController.setEnemies(new Array<Enemy>());
		kitchenController.reset();
	}

	/**
	 * Resets the game, deleting all objects.
	 */
	public void init() {
		bgm.stop();
		if (!noBoard) {
			String[] ex = level.get("startPos").toString().split(",");
			int startQ = (int) Float.parseFloat(ex[0].substring(1).split(" ")[1].substring(1));
			int startR = (int) Float.parseFloat(ex[1].substring(0, ex[1].length() - 2));

			Vector2 startPos = board.arrayToScreen(startQ, startR);
			start(startPos.x, startPos.y, board);
		}
	}

	/**
	 * Resets the game, deleting all objects.
	 */
	public void restartUpdate() {
		reset();
		if (!noBoard) {
			init();
		}
		worldTimerDisplay = WORLD_TIMER;
		worldTimerF = WORLD_TIMER * 60;
	}

	// ============================ Play Function and Helpers ===========================

	/**
	 * Update the progress after shooting food
	 * <p>
	 * Updates the board, the player, and the enemy if the input was valid.
	 * This method is a helper function resolvePlayer.
	 *
	 * @param input inputcontroller to detect change from
	 */
	private void updateThrow(InputController input) {
		player.updateThrowing();
		if (!player.isThrowing()) {
			threw = true;
			board.addFood(mouseTileArrayCoord, player.getThrowFood());
			input.setFoodShootingClick(false);
			aiController.setEnemiesInFoodRadius(aiController.findEnemies(mouseTileArrayCoord, board, 2));
			aiController.updateFoodChaseState(board.getTileArrayCoord((int) mouseTileArrayCoord.x, (int) mouseTileArrayCoord.y));
		}
	}

	/**
	 * Process the effect from shooting the food
	 * <p>
	 * Updates the board, the player, and the enemy if the input was valid.
	 * This method is a helper function resolvePlayer.
	 *
	 * @param input inputcontroller to detect change from
	 */
	private void handleThrow(InputController input) {
		Vector2 screenCoord = input.getFoodShootingScreenCoordinate(); // need to check if valid board coord
		if (board.findNearestTile(screenCoord.x, height - screenCoord.y) != null) {
			mouseTileArrayCoord = board.screenToArray(screenCoord.x, height - screenCoord.y);
			HexTile selectedTile = board.getTileArrayCoord((int) mouseTileArrayCoord.x, (int) mouseTileArrayCoord.y);
			if (mouseTileArrayCoord.dst(player.getArrayPosition(board)) > 0 &&
					player.getThrowRadius() >= board.dst2Tile(player.getArrayPosition(board), mouseTileArrayCoord)
					&& !selectedTile.isObstacle() && !selectedTile.hasFood() && !selectedTile.hasEnemy()) {
				Vector2 screenPos = board.arrayToScreen((int) mouseTileArrayCoord.x, (int) mouseTileArrayCoord.y);
				player.startThrowing(new Vector2(screenPos.x, screenPos.y + board.getTileHeight() / 4f),
						board.getTileWidth(), board.getTileHeight());
				if (levelNum != 4) {
					currentMove.set(3, 0);
					currentMove.set(0, currentMove.get(0) - 1);
				}
			}
		}
	}

	/**
	 * Updates enemy timer and process actions if timer is up
	 * <p>
	 * When tile timer goes off, update enemy state.
	 * Updates the board, the player, and the enemy if the input was valid.
	 * This method is a helper function resolvePlayer.
	 */
	private void updateEnemyTimer() {
		for (Vector2 arrC : board.getFoodTiles()) {
			aiController.setEnemiesInFoodRadius(aiController.findEnemies(arrC, board, 2));
			aiController.updateFoodChaseState(board.getTileArrayCoord((int) arrC.x, (int) arrC.y));
		}
		// if player tile timer is up, move-in enemies
		if (player.getExceedTileTimer()) {
			aiController.updateTileTimerEnemyMoveIn(board, player.getBoardPosition());
			player.resetExceedTileTimer();
		} else if (player.getTileTimer() == Truck.TILE_TIMER / 5) {
			aiController.setEnemiesAboutToChaseTruck(player.getTileTimer());
		}
		if (player.getPrevMove()) {
			aiController.unsetEnemiesAboutToChaseTruck();
			player.resetPrevMove();
		}
	}

	/**
	 * Updates reward after action is taken.
	 * <p>
	 * This method is a helper function resolvePlayer.
	 */
	private void updateReward() {
		if (player.getMadeMove()) {
			currentMove.set(1, currentMove.get(1) - 1);
			currentMove.set(0, currentMove.get(0) - 1);
			player.resetMadeMove();
		} else if (player.getMadeTurn()) {
			currentMove.set(2, currentMove.get(2) - 1);
			currentMove.set(0, currentMove.get(0) - 1);
			player.resetMadeTurn();
		}
	}

	/**
	 * Process the player's actions.
	 * <p>
	 * This method is a helper function for resolveActions
	 *
	 * @param input Reference to the input controller
	 * @param delta Number of seconds since last animation frame
	 */
	public void resolvePlayer(InputController input, float delta, Board board) {
		if (aiController.allEnemiesNotMoving() && player.getPrevTileTimerTrigger() && player.getTileTimer() < 0) {
			// reset truck's tile timer after enemies finished jumping
			player.resetTileTimer();
		}
		if (kitchenController.recipesExist()) {
			player.setIsMoveable(currentMove != null);
			if (currentMove != null) {
				player.setForward(input.isMoveForward() && currentMove.get(1) > 0);
				player.setBackward(input.isMoveBackward() && currentMove.get(1) > 0);
				player.setLeft(input.isTurnLeft() && currentMove.get(2) > 0);
				player.setRight(input.isTurnRight() && currentMove.get(2) > 0);
			}
			if (levelNum ==9){
				checkTutorialStates(input);
				handleTutorials(input);
			}
		} else if (levelNum == 1 || levelNum == 3 || levelNum == 5 || levelNum ==4) {
			checkTutorialStates(input);
			handleTutorials(input);
		} else {
			player.setIsMoveable(true);
			player.setForward(input.isMoveForward());
			player.setBackward(input.isMoveBackward());
			player.setLeft(input.isTurnLeft());
			player.setRight(input.isTurnRight());
			player.resetMadeMove();
			player.resetMadeTurn();
		}

		threw = false;
		if (player.isThrowing()) {
			updateThrow(input);
		} else if (input.getFoodShootingClick() && player.getCanShoot()) {
			handleThrow(input);
		} else {
			updateEnemyTimer();
		}
		player.update(delta, board, threw);
		if (kitchenController.recipesExist()) {
			updateReward();
		}
		playerMetEnemy();
		playerMetKey();
	}

	/**
	 * Processes the actions of player and enemies
	 * <p>
	 * This method is a helper function for play
	 *
	 * @param input Reference to the input controller
	 * @param delta Number of seconds since last animation frame
	 */
	public void resolveActions(InputController input, float delta, Board board) {
		// Process the player
		if (player != null) {
			resolvePlayer(input, delta, board);
		}
		for (GameObject o : objects) {
			if (o.getType() != GameObject.ObjectType.Enemy) {
				o.update(delta);
			} else {
				((Enemy) o).updateEnemy(delta, board);
			}
		}
	}


	/**
	 * This method processes a single step in the game loop.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void play(float delta, InputController inputController) {
		if (!noBoard) {
			board.unhighlightTiles();
			aiController.updateFoodChaseStateToFalse();

			if (player != null && player.getCanShoot()) {
				board.highlightTiles(player.getArrayPosition(board), player.getThrowRadius());
			}
			// Update objects.
			resolveActions(inputController, delta, board);
		} else if (levelNum == 6){
			checkKitchenStates(inputController);
		}
	}

	// ============================ update Function and Helpers ===========================

	/**
	 * Updates world timer
	 * <p>
	 * This method is a helper function for update
	 */
	public void timerUpdate() {
		if (worldTimerF > 0 && levelNum != 1 && levelNum != 3 && (levelNum > 8 || levelNum < 6)){
			worldTimerF--;
			if (worldTimerF % 60 == 0) {
				worldTimerDisplay--;
			}
		}
	}

	/**
	 * Process all keyboard actions from inputcontroller and pass them to kitchencontroller
	 * <p>
	 * This method is a helper function for update
	 *
	 * @param inputController Reference to the input controller
	 */
	private void handleKeyBoardAction(InputController inputController) {
		// Handle Trash
		if (inputController.getTrashPressed() && inputController.getisHoveringOnAppliance()
				&& inputController.getHoveringOnwhichAppliance() < 4) {
			if (levelNum != 6 || checkTutorialAppliance(1,inputController)) {
				kitchenController.setTrash(inputController.getHoveringOnwhichAppliance());
				inputController.resetTrashAssemble();
			}
		}
		// Handle Assemble
		if (inputController.getAssemblePressed()) {
			if (levelNum != 6 || checkTutorialAppliance(2,inputController)) {
				kitchenController.setAssemble(false, inputController.getHoveringOnwhichAppliance());
				inputController.resetAssembledPressed();
				inputController.resetAwakeAppliance();
			}
		}
		// Handle Feed
		if (inputController.getFeedPressed()) {
			if (levelNum != 6 || checkTutorialAppliance(3,inputController)) {
				kitchenController.setFeed();
				inputController.resetFeed();
			}
		}
	}

	public boolean checkTutorialAppliance(int i,InputController inputController) {
		//0:working ; 1:trash ; 2:assemble ; 3:feed
		if(i==0){
			if((inputController.getAwakeAppliance()==0 && tutorialState ==1)||(inputController.getAwakeAppliance()==1 && tutorialState ==7)
					||(inputController.getAwakeAppliance()==2 && tutorialState ==5)){
				return true;
			} else {
				return false;
			}
		}else if(i==1){
			if (tutorialState==6){
				return true;
			} else {
				return false;
			}
		}else if(i==2){
			if (tutorialState==3 || tutorialState==5 || tutorialState==7){
				return true;
			} else {
				return false;
			}
		}else if(i==3){
			if (tutorialState==4 || tutorialState==7){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Process all click actions and pass them to kitchencontroller
	 * <p>
	 * This method is a helper function for update
	 *
	 * @param inputController Reference to the input controller
	 */
	private void handleClick(InputController inputController) {
		if (inputController.getSelectedTargetIngredient() > -1) {
			kitchenController.updateHighlightIngredients(inputController.getSelectedTargetIngredient());
			if (inputController.getAwakeAppliance() > -1) {
				if (levelNum!=6 || checkTutorialAppliance(0,inputController)){
					kitchenController.activateAppliance(inputController.getAwakeAppliance(), inputController.getSelectedTargetIngredient());
					inputController.resetSelectedTargetIngredient();
					inputController.resetAwakeAppliance();
				}
			}
		} else {
			if (kitchenController.getCurrentSelectedCookedIngredient() == -1 && inputController.getAwakeAppliance() > -1) {
				if (kitchenController.applianceHasDoneIngredient(inputController.getAwakeAppliance())) {
					kitchenController.setCurrentSelectedCookedIngredient(inputController.getAwakeAppliance());
				} else {
					if (inputController.getAwakeAppliance() == 0 && kitchenController.getApplianceIndex(0).isWorking()) {
						kitchenController.getApplianceIndex(0).setClickProgress();
						chop.play(soundvolume);
					}
					inputController.resetAwakeAppliance();
				}
			} else {
				if (inputController.getAwakeAppliance() == -1) {
					kitchenController.resetCurrentSelectedCookedIngredient();
					return;
				}
				kitchenController.highlightDoneIngredient();
				if (inputController.getAwakeAppliance() != kitchenController.getCurrentSelectedCookedIngredient()
						&& inputController.getAwakeAppliance() > 2) {
					if (inputController.getAwakeAppliance() == 3) {
						kitchenController.setAssemble(true, kitchenController.getCurrentSelectedCookedIngredient());
					} else {
						kitchenController.setTrash(kitchenController.getCurrentSelectedCookedIngredient());
					}
					kitchenController.resetCurrentSelectedCookedIngredient();
					inputController.resetAwakeAppliance();
				}
			}
		}
	}

	/**
	 * Process all hover actions and pass them to kitchencontroller
	 * <p>
	 * This method is a helper function for update
	 *
	 * @param inputController Reference to the input controller
	 */
	private void handleHover(InputController inputController) {
		if (inputController.getisHoveringOnAppliance()) {
			if (kitchenController.applianceHasDoneIngredient(inputController.getHoveringOnwhichAppliance())) {
				// Handle highlighting the done ingredient
				kitchenController.highlightDoneIngredient();
			} else {
				kitchenController.updateHighlightAppliance(inputController.getHoveringOnwhichAppliance());
			}
			inputController.setisHoveringOnAppliance(false);
			inputController.setHoveringOnwhichAppliance(-1);
		} else if (inputController.getisHoveringOnIngredient()) {
			kitchenController.updateHighlightIngredients(inputController.getHoveringOnwhichIngredient());
			inputController.setisHoveringOnIngredient(false);
			inputController.setHoveringOnwhichIngredient(-1);
		}
	}

	/**
	 * Process reward after feeding success and passes it onto the player
	 * <p>
	 * This method is a helper function for update
	 */
	private void handleReward() {
		if (kitchenController.getFedSuccess()) {
			if (currentMove == null) {
				currentMove = new Array<>();
			} else {
				currentMove.clear();
			}
//			for (int i = 0; i < kitchenController.getFedReward().get("size"); i++) {
//				currentMove.add(kitchenController.getFedReward().get(i));
//			}
			currentMove.add(kitchenController.getFedReward().get("size"));
			currentMove.add(kitchenController.getFedReward().get("move"));
			currentMove.add(kitchenController.getFedReward().get("turn"));
			currentMove.add(kitchenController.getFedReward().get("throw"));
			player.setMoveNum(kitchenController.getFedReward().get("move"));
			player.setCanShoot(kitchenController.getFedReward().get("throw") > 0);
			player.setTurnNum(kitchenController.getFedReward().get("turn"));
			player.setThrowFood(kitchenController.getFedFood());
			kitchenController.resetFedSuccess();
			kitchenController.resetFedReward();
		}
	}


	/**
	 * Update opacity
	 * <p>
	 * This method is a helper function for update
	 */
	private void updateOpacity() {
		opacity += 0.02;
		if (opacity >= 1) {
			opacity = -1.0f;
		}
	}

	/**
	 * Update the gameplay .
	 * <p>
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param inputController inputController to detect inputs
	 */
	public void update(InputController inputController) {
		timerUpdate();
		// Process the game input
		inputController.readInput();

		if (kitchenController.recipesExist()) {
			handleKeyBoardAction(inputController);
			kitchenController.update();
			kitchenController.resetApplianceHighlight();
			kitchenController.resetIngredientHighlight();
			kitchenController.resetHighlightDoneIngredient();

			handleClick(inputController);
			handleHover(inputController);
			if (!noBoard) {
				handleReward();
			}
		}
		updateOpacity();
		if (!noBoard) {
			board.clearVisited();
			board.update();

		}
	}


	// ============================ Draw Function and Helpers ===========================

	/**
	 * Matches the integer to the corresponding font texture
	 * <p>
	 * This method is a helper function for update
	 *
	 * @param j the input number
	 */
	private Texture matcher(int j) {
		switch (j) {
			case 0:
				return num0;
			case 1:
				return num1;
			case 2:
				return num2;
			case 3:
				return num3;
			case 4:
				return num4;
			case 5:
				return num5;
			case 6:
				return num6;
			case 7:
				return num7;
			case 8:
				return num8;
			default:
				return num9;
		}
	}

	/**
	 * Format current remaining time into min:sec format
	 *
	 * @param timer current timer value
	 */
	private Texture[] timerFormat(int timer) {
		int sec = timer % 60;
		int min = timer / 60;
		Texture[] toBeReturned = new Texture[5];
		toBeReturned[0] = matcher(min / 10);
		toBeReturned[1] = matcher(min % 10);
		toBeReturned[2] = matcher(sec / 10);
		toBeReturned[3] = matcher(sec % 10);
		return toBeReturned;
	}

	/**
	 * Draw timer plank
	 * <p>
	 * This method is a helper function for draw
	 *
	 * @param canvas canvas to draw on
	 */
	private void drawTimer(GameCanvas canvas) {
		if (worldTimerDisplay > 0 && levelNum != 1 && levelNum != 3 && (levelNum > 8 || levelNum < 6)){
			canvas.draw(timerPlank, canvas.getWidth() / 2 - canvas.getWidth() * 65 / 576f, canvas.getHeight() * 9 / 10f
					, 65 / 288f * canvas.getWidth(), canvas.getHeight() / 10f);
			canvas.draw(colon, canvas.getWidth() * (1 - 65f / (576f * 9)) / 2, canvas.getHeight() * 37 / 40f,
					canvas.getWidth() * 65f / (576f * 9), canvas.getHeight() / 20f);
			Texture[] tArr = timerFormat(worldTimerDisplay);
			canvas.draw(tArr[0], canvas.getWidth() * (1 - 65f / 576f) / 2, canvas.getHeight() * 37 / 40f,
					canvas.getWidth() * 65f * 2 / (576f * 9), canvas.getHeight() / 20f);
			canvas.draw(tArr[1], canvas.getWidth() * (1 - 65f * 5 / (576f * 9)) / 2, canvas.getHeight() * 37 / 40f,
					canvas.getWidth() * 65f * 2 / (576f * 9), canvas.getHeight() / 20f);
			canvas.draw(tArr[2], canvas.getWidth() * (1 + 65f / (576f * 9)) / 2, canvas.getHeight() * 37 / 40f,
					canvas.getWidth() * 65f * 2 / (576f * 9), canvas.getHeight() / 20f);
			canvas.draw(tArr[3], canvas.getWidth() * (1 + 65f * 5 / (576f * 9)) / 2, canvas.getHeight() * 37 / 40f,
					canvas.getWidth() * 65f * 2 / (576f * 9), canvas.getHeight() / 20f);
		}
	}


	private void drawRewardHelper(GameCanvas canvas) {
		String toPrint;
		GlyphLayout layout;
		float textWidth;
		int[] xPosArr = new int[]{-3, -1, 1};
		float rewardHeight = height * 12 / 180f;
		float rewardyPos = height * 593 / 720f;
		Texture[] textureArr = new Texture[]{cardRewardForward, cardRewardTurn, cardRewardThrow};
		for (int i = 0; i < 3; i++) {
			if (currentMove.get(i + 1) != 0) {
				toPrint = currentMove.get(i + 1).toString();
				layout = new GlyphLayout(westSac, toPrint);
				textWidth = layout.width;
				// Draw Rewards
				canvas.draw(textureArr[i], width / 2f + width * 64 * xPosArr[i] / (288f * 8),
						rewardyPos, 65 / (288f * 4) * width,
						rewardHeight);
				canvas.drawTextBottommAligned(toPrint, grande,
						width / 2f + width * 64 * xPosArr[i] / (288f * 8) + 65 / (288f * 4) * width - textureArr[i].getWidth()/4,
						rewardyPos + grande.getCapHeight()/3f, Color.WHITE);

			}
		}
	}

	private void drawKeys(GameCanvas canvas) {
		int keyLeft = 3 - getKeyNum();
		float keySize = canvas.getHeight() / 10f;
		float keyOffset = keySize / 6f;
		float keyY = canvas.getHeight() * 9 / 10f;
		for (int i = 0; i < keyLeft; i++) {
			canvas.draw(emptyKeyTexture, canvas.getWidth() - keySize * (i + 1) - keyOffset * i, keyY, keySize, keySize);
		}
		for (int i = keyLeft; i < 3; i++) {
			canvas.draw(keyTexture, canvas.getWidth() - keySize * (i + 1) - keyOffset * i, keyY, keySize, keySize);
		}
	}

	/**
	 * Draw reward
	 * <p>
	 * This method is a helper function for draw
	 *
	 * @param canvas canvas to draw on
	 */
	private void drawReward(GameCanvas canvas) {
		if (currentMove != null && currentMove.get(1) + currentMove.get(2) + currentMove.get(3) > 0) {
			canvas.draw(rewardPlank, width * (1 / 2f - 65 / 576f), height * 97 / 120f,
					65 / 288f * width, height * 11 / 120f);
			drawRewardHelper(canvas);
		}
	}

	/**
	 * Draws the whole game to the canvas
	 *
	 * @param canvas The drawing context
	 */
	public void draw(GameCanvas canvas) {
		canvas.draw(background, 0, canvas.getHeight() / 18, canvas.getWidth(), canvas.getHeight());
		canvas.draw(filter, 0, canvas.getHeight() * 3 / 7, canvas.getWidth(), canvas.getHeight() * 4 / 7,
				new Color(1, 1, 1, Math.abs(opacity)));
		if (!noBoard) {
			board.draw(canvas);
		}
		kitchenController.draw(canvas);

		if (!noBoard) {
			Array<GameObject> arrangedObj = new Array<>();
			for (GameObject o : objects) {
				if (arrangedObj.size == 0) {
					arrangedObj.add(o);
				} else {
					boolean contains = false;
					for (int i = 0; i < arrangedObj.size; i++) {
						if (!contains && o.getY() >= arrangedObj.get(i).getY()) {
							arrangedObj.insert(i, o);
							contains = true;
						}
					}
					if (!contains) {
						arrangedObj.add(o);
					}
				}
			}
			boolean throwableFoodDrew = false;
			boolean exitDrew = false;
			for (int i = 0; i < arrangedObj.size; i++) {
				if (!exitDrew && exitSignPos.y >= arrangedObj.get(i).getY()) {
					exitDrew = true;
					canvas.draw(exitSign, Color.WHITE, board.getTileWidth() * 27 / 40f,
							0, exitSignPos.x + board.getTileWidth() / 4, exitSignPos.y, board.getTileWidth(), board.getTileWidth());
				}
				if (player.isThrowing() && !throwableFoodDrew && player.getThrowableFoodPosY() >= arrangedObj.get(i).getY()) {
					throwableFoodDrew = true;
					canvas.draw(throwableFoodTexture, player.getThrowableFoodPosX() - player.getThrowWidth() / 2,
							player.getThrowableFoodPosY() - player.getThrowHeight() / 2,
							player.getThrowWidth(), player.getThrowHeight());
				}
				arrangedObj.get(i).draw(canvas);
			}
			if (!exitDrew) {
				canvas.draw(exitSign, Color.WHITE, board.getTileWidth() * 27 / 40f,
						0, exitSignPos.x + board.getTileWidth() / 4, exitSignPos.y, board.getTileWidth(), board.getTileWidth());
			}
			if (player.isThrowing() && !throwableFoodDrew) {
				canvas.draw(throwableFoodTexture, player.getThrowableFoodPosX() - player.getThrowWidth() / 2,
						player.getThrowableFoodPosY() - player.getThrowHeight() / 2,
						player.getThrowWidth(), player.getThrowHeight());
			}
			drawTimer(canvas);
			drawReward(canvas);
			if (keys.size != 0) {
				drawKeys(canvas);
			}

			drawTutorialText(canvas);
		} else {
			drawKitchenTutorialText(canvas);
		}
	}


	public void notifyLevel(int currLev) {
			levelNum = currLev;
			if (levelNum < 8){
				tutorialState = 0;
			}
			kitchenController.setTutorialLevel(levelNum);
		}

	public boolean completedAllRecipes(){
		return kitchenController.getTutorial() && kitchenController.completedAll();
	}

	public boolean boardExists(){
		return !noBoard;
	}

	public void checkKitchenStates(InputController input) {
		if (levelNum == 6){
			if (tutorialState == 0){
				if(input.getSelectedTargetIngredient()==0){
					tutorialState++;
				}
			} else if (tutorialState == 1){
				if(kitchenController.getChoppingBoard().isWorking()){
					tutorialState++;
				}
			} else if (tutorialState == 2){
				if(kitchenController.applianceHasDoneIngredient(0)){
					tutorialState++;
				}
			} else if (tutorialState == 3){
				if(input.didAssemble()){
					tutorialState++;
				}
			} else if (tutorialState == 4){
				if(input.didFeed()){
					tutorialState++;
				}
			} else if (tutorialState == 5){
				if(kitchenController.applianceHasDoneIngredient(2)){
					tutorialState++;
				}
			} else if (tutorialState == 6){
				if(input.getTrashPressed() && !kitchenController.applianceHasDoneIngredient(2)){
					tutorialState++;
				}
			}
		}
	}


	public void checkNextHelper(int i, InputController input) {
		if (tutorialTimer == -1) {
			tutorialTimer = LEVEL_INFO_3[i];
		} else if (tutorialTimer != 0) {
			tutorialTimer --;
		} else if (input.readNext()) {
			tutorialTimer = -1;
			tutorialState++;
		}
	}

	public boolean checkEnemyChase() {
		for (Enemy e : aiController.getEnemies()) {
			if (e.getFoodChasing()) {
				return true;
			}
		}
		return false;
	}

	public void checkTutorialStates(InputController input) {
		if (levelNum == 1) {
			if (tutorialState == 0) {
				if (player.getBoardPosition().equals(new Vector2((float)1.0,(float)0.0))) {
					tutorialState ++;
				}
			} else if (tutorialState == 1) {
				if (player.getDirection() == 0) {
					tutorialState ++;
				}
			} else if (tutorialState == 2) {
				if (player.getBoardPosition().equals(new Vector2((float)1.0,(float)1.0))) {
					tutorialState ++;
				}
			} else if (tutorialState == 3) {
				if (player.getDirection() == 1) {
					tutorialState ++;
				}
			} else if (tutorialState == 4) {
				if (player.getBoardPosition().equals(new Vector2((float)2.0,(float)1.0))) {
					tutorialState ++;
				}
			} else if (tutorialState == 5) {
				if (player.getBoardPosition().equals(new Vector2((float)1.0,(float)1.0))) {
					tutorialState ++;
				}
			}
		} else if (levelNum == 3) {
			if (tutorialState == 0) {
				if (input.readNext()) {
					tutorialState++;
				}
			} else if (tutorialState >= 1 && tutorialState <= 5) {
				checkNextHelper(tutorialState,input);
			}
		} else if (levelNum == 5) {
			if (tutorialState == 0) {
				if (getKeyNum() == 1) {
					tutorialState++;
				}
			}
		} else if (levelNum == 4) {
			if (tutorialState ==0) {
				if (checkEnemyChase()) {
					tutorialState++;
				}
			}
		} else if (levelNum == 9) {
			if (input.readNext() && tutorialState<2) {
				tutorialState++;
			}
		}
	}

	public void handleNextHelper() {
		player.setIsMoveable(false);
		if (tutorialTimer != 0) {
			isPaused = false;
		} else {
			isPaused = true;
		}
	}

	public void handleTutorials(InputController input) {
		if (levelNum == 1) {
			if (tutorialState == 0) {
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(false);
				player.setLeft(false);
				player.setRight(false);
				player.resetMadeMove();
				player.resetMadeTurn();
			}
			else if (tutorialState == 1) {
				player.setIsMoveable(true);
				player.setForward(false);
				player.setBackward(false);
				player.setLeft(input.isTurnLeft());
				player.setRight(false);
				player.resetMadeMove();
				player.resetMadeTurn();
			} else if (tutorialState == 2) {
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(false);
				player.setLeft(false);
				player.setRight(false);
				player.resetMadeMove();
				player.resetMadeTurn();
			} else if (tutorialState == 3) {
				player.setIsMoveable(true);
				player.setForward(false);
				player.setBackward(false);
				player.setLeft(false);
				player.setRight(input.isTurnRight());
				player.resetMadeMove();
				player.resetMadeTurn();
			} else if (tutorialState == 4) {
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(false);
				player.setLeft(false);
				player.setRight(false);
				player.resetMadeMove();
				player.resetMadeTurn();
			} else if (tutorialState == 5) {
				player.setIsMoveable(true);
				player.setForward(false);
				player.setBackward(input.isMoveBackward());
				player.setLeft(false);
				player.setRight(false);
				player.resetMadeMove();
				player.resetMadeTurn();
			} else if (tutorialState == 6) {
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(input.isMoveBackward());
				player.setLeft(input.isTurnLeft());
				player.setRight(input.isTurnRight());
				player.resetMadeMove();
				player.resetMadeTurn();
			}
		} else if (levelNum == 3) {
			if (tutorialState == 0) {
				isPaused = true;
				player.setIsMoveable(false);
			} else if (tutorialState >= 1 && tutorialState <= 5) {
				handleNextHelper();
			} else if (tutorialState == 6) {
				isPaused = false;
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(input.isMoveBackward());
				player.setLeft(input.isTurnLeft());
				player.setRight(input.isTurnRight());
				player.resetMadeMove();
				player.resetMadeTurn();
			}
		} else if (levelNum == 5) {
			player.setIsMoveable(true);
			player.setForward(input.isMoveForward());
			player.setBackward(input.isMoveBackward());
			player.setLeft(input.isTurnLeft());
			player.setRight(input.isTurnRight());
			player.resetMadeMove();
			player.resetMadeTurn();
		} else if (levelNum == 4) {
			if (tutorialState == 0) {
				player.setIsMoveable(false);
			}
			if (tutorialState == 1) {
				player.setIsMoveable(true);
				player.setForward(input.isMoveForward());
				player.setBackward(input.isMoveBackward());
				player.setLeft(input.isTurnLeft());
				player.setRight(input.isTurnRight());
				player.resetMadeMove();
				player.resetMadeTurn();
			}
			if (!board.hasFoodOnBoard()) {
				player.setCanShoot(true);
				player.setThrowFood(kitchenController.getDefaultFoodTexture());
			}
		} else if (levelNum ==9){
			if (tutorialState<2){
				isPaused = true;
			} else {
				isPaused = false;
			}
		}
	}

	public void drawTutorialText(GameCanvas canvas) {
		BitmapFont tutorialFont = new BitmapFont(westSac.getData(), westSac.getRegion(), false);
		tutorialFont.getData().setScale(canvas.getHeight()*0.0004f);
		float tutorialTextXPos = canvas.getWidth()/24f;
		float tutorialTextYUpPos = canvas.getWidth()*3.2f/8f;
		float tutorialTextYDownPos = tutorialTextYUpPos - tutorialFont.getCapHeight()*1.9f;

		if (levelNum == 1) {
			if (tutorialState == 0) {
				canvas.drawText("Here is your Nomster Truck!",tutorialFont, tutorialTextXPos, tutorialTextYUpPos, Color.WHITE);
				canvas.drawText("Press W to move forward",tutorialFont, tutorialTextXPos, tutorialTextYDownPos, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  0f, tutorialDownRightArrow.getHeight(),
						tutorialTextXPos*4.7f, tutorialTextYDownPos - tutorialFont.getCapHeight()*1.6f,
						0f, 0.3f, 0.3f);
			}
			else if (tutorialState == 1) {
				canvas.drawText("Press A to turn left",tutorialFont, tutorialTextXPos*3.5f, tutorialTextYDownPos - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
			} else if (tutorialState == 2) {
				canvas.drawText("Move forward now!",tutorialFont,tutorialTextXPos*3.5f, tutorialTextYDownPos - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
			} else if (tutorialState == 3) {
				canvas.drawText("Press D to turn right",tutorialFont,tutorialTextXPos*3.5f, tutorialTextYDownPos - tutorialFont.getCapHeight()/2f, Color.WHITE);

			} else if (tutorialState == 4) {
				canvas.drawText("Move forward now!", tutorialFont,tutorialTextXPos*3.5f, tutorialTextYDownPos - tutorialFont.getCapHeight()/2f, Color.WHITE);
			} else if (tutorialState == 5) {
				canvas.drawText("Press S to move backward", tutorialFont,tutorialTextXPos*4f, tutorialTextYUpPos - tutorialFont.getCapHeight()/2f, Color.WHITE);
			} else if (tutorialState == 6) {
				canvas.drawText("Now you can drive the truck!",tutorialFont, tutorialTextXPos*2, tutorialTextYUpPos, Color.WHITE);
				canvas.drawText("Continue to reach the exit!",tutorialFont, tutorialTextXPos*2, tutorialTextYDownPos, Color.WHITE);
			}
		} else if (levelNum == 2) {
			canvas.drawText("Reach the exit before the time is up!",tutorialFont, tutorialTextXPos*3 + tutorialFont.getCapHeight(),
					canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*1.3f, Color.WHITE);
			canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), 0,
					canvas.getWidth()/2 + tutorialFont.getCapHeight(), canvas.getHeight()*14.5f/16f - tutorialFont.getCapHeight(),
					90f, 0.2f, 0.2f);

			canvas.drawText("You can't drive through the tree",tutorialFont, tutorialTextXPos*4,
					canvas.getHeight()*8/16f - tutorialFont.getCapHeight(), Color.WHITE);
			canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
					canvas.getWidth()/2f - tutorialTextXPos/2f, canvas.getHeight()/2f + tutorialFont.getCapHeight()/2f,
					90f, 0.2f, 0.2f);

		} else if (levelNum == 3) {
			Enemy e = aiController.getEnemies().get(0);
			if (tutorialState == 0) {
				canvas.drawText("This little creature may seem cute", tutorialFont, tutorialTextXPos - tutorialFont.getCapHeight(),
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("but be warned!",tutorialFont, tutorialTextXPos - tutorialFont.getCapHeight(),
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("Colliding with it will smash your truck",tutorialFont, tutorialTextXPos - tutorialFont.getCapHeight(),
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
						e.getX() - tutorialDownRightArrow.getWidth()*0.16f, e.getY() + tutorialDownRightArrow.getHeight()*0.2f*1.4f,
						90, -0.2f, 0.2f);
				canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
						canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
			} else if (tutorialState == 1) {
				canvas.drawText("It has its own timer to stay on a tile", tutorialFont, tutorialTextXPos + tutorialFont.getCapHeight()/2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*1.5f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
						e.getX() - tutorialDownRightArrow.getWidth()*0.16f, e.getY() + tutorialDownRightArrow.getHeight()*0.2f*1.4f,
						90, -0.2f, 0.2f);

				if (tutorialTimer == 0) {
					canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
							canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
				}
			} else if (tutorialState == 2) {
				canvas.drawText("When the timer is up,", tutorialFont, tutorialTextXPos*3.2f,
						canvas.getHeight()*14.5f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("it will face to the next tile",tutorialFont, tutorialTextXPos*3.2f,
						canvas.getHeight()*14.5f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("and hop on the current tile",tutorialFont, tutorialTextXPos*3.2f,
						canvas.getHeight()*14.5f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
						e.getX() - tutorialDownRightArrow.getWidth()*0.16f, e.getY() + tutorialDownRightArrow.getHeight()*0.2f*1.4f,
						90, -0.2f, 0.2f);

				if (tutorialTimer == 0) {
					canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
							canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
				}
			} else if (tutorialState == 3) {
				canvas.drawText("Then it will jump to the next tile", tutorialFont,
						e.getX() - tutorialDownRightArrow.getWidth()*1.86f, e.getY() + tutorialDownRightArrow.getHeight()*0.52f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
						e.getX() - tutorialFont.getCapHeight()*2, e.getY() + tutorialDownRightArrow.getHeight()*0.2f*1.4f,
						90, -0.2f, 0.2f);

				if (tutorialTimer == 0) {
					canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
							canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
				}
			} else if (tutorialState == 4) {
				canvas.drawText("Each enemy will follow its own patrolling path", tutorialFont,
						e.getX() - tutorialDownRightArrow.getWidth()*2.55f, e.getY() + tutorialDownRightArrow.getHeight()*0.52f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  tutorialDownRightArrow.getWidth(), tutorialDownRightArrow.getHeight(),
						e.getX() - tutorialDownRightArrow.getWidth()*0.16f, e.getY() + tutorialDownRightArrow.getHeight()*0.2f*1.4f,
						90, -0.2f, 0.2f);

				if (tutorialTimer == 0) {
					canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
							canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
				}
			} else if (tutorialState == 5) {
				canvas.drawText("Staying on a tile for too long,", tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("the enemy will be alerted",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("and will move one tile closer!",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);

				if (tutorialTimer == 0) {
					canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
							canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
				}
			} else if (tutorialState == 6) {
				canvas.drawText("Now all moves are free", tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("Avoid the enemy",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("and move to the exit!",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);
			}
		} else if (levelNum == 5){
			if (tutorialState == 0){
				canvas.drawText("There are stars on the board", tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("To collect a star,",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("move to a tile with one!",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);

			} else if (tutorialState == 1){
				canvas.drawText("Now you get one star!", tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("Try to collect all of them,",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("before reaching the exit",tutorialFont, tutorialTextXPos*2,
						canvas.getHeight()*14f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);
			}
		} else if (levelNum == 4) {
			if (tutorialState == 0){
				canvas.drawText("Try throwing food", tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("by clicking a highlighted tile",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
				canvas.drawText("Enemy within two tile radius",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*4f, Color.WHITE);
				canvas.drawText("will be stunned",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*5.5f, Color.WHITE);

			} else if (tutorialState == 1){
				canvas.drawText("Enemy is successfully stunned!", tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight(), Color.WHITE);
				canvas.drawText("Reach the exit now",tutorialFont, tutorialTextXPos,
						canvas.getHeight()*12f/16f - tutorialFont.getCapHeight()*2.5f, Color.WHITE);
			}
		} else if (levelNum == 9) {
			if (tutorialState == 0){
				canvas.drawText("Welcome to the last tutorial level!", tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13f/16f, Color.WHITE);
				canvas.drawText("Let's enable both of the kitchen and the board",tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13f/16f - tutorialFont.getCapHeight()*2, Color.WHITE);

				canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
						canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
			} else if (tutorialState == 1){
				canvas.drawText("Completing a recipe and feed to the truck", tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13.6f/16f, Color.WHITE);
				canvas.drawText("You will get the corresponding rewards shown on the recipe",tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13.6f/16f - tutorialFont.getCapHeight()*2, Color.WHITE);
				canvas.drawText("There are 3 reward types:",tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13.6f/16f - tutorialFont.getCapHeight()*4, Color.WHITE);
				canvas.drawText("movements, turns, and throwable food",tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13.6f/16f - tutorialFont.getCapHeight()*6, Color.WHITE);

				canvas.draw(nextButton,canvas.getWidth()*2/3, canvas.getHeight()*4/9,
						canvas.getWidth()/5, ((float)nextButton.getHeight()/ nextButton.getWidth())*(canvas.getWidth()/5));
			} else if (tutorialState == 2){
				canvas.drawText("Now is time to try it out! Reach the exit before time is up!", tutorialFont, canvas.getWidth()*0.8f/2,
						canvas.getHeight()*13f/16f, Color.WHITE);
			}
		}
	}

	public void drawKitchenTutorialText(GameCanvas canvas){
		BitmapFont tutorialFont = new BitmapFont(westSac.getData(), westSac.getRegion(), false);
		tutorialFont.getData().setScale(canvas.getHeight()*0.0004f);
		if (levelNum == 6){
			if(tutorialState == 0){
				canvas.drawTextCentered("Welcome to the kitchen tutorial!",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
				canvas.drawTextCentered("Try clicking the raw noodle to select it",tutorialFont,-tutorialFont.getCapHeight(),Color.WHITE);
			}else if (tutorialState == 1){
				canvas.drawText("Click on the chopping board to place this ingredient",tutorialFont, canvas.getWidth()*0.92f/2,
						canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  0, 0,
						canvas.getWidth()*4.8f/12, canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f,
						90f, -0.3f, -0.3f);
			}else if (tutorialState == 2){
				canvas.drawTextCentered("Click the chopping board continuously to chop it",tutorialFont,0,Color.WHITE);
			}else if (tutorialState == 3){
				canvas.drawText("Press SPACE key to assemble your cooked ingredient in the plate", tutorialFont, canvas.getWidth()*0.24f/2,
						canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f, Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  0, 0,
						canvas.getWidth()*3.2f/4, canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f,
						90f, -0.3f, 0.3f);

			}else if (tutorialState == 4){
				canvas.drawTextCentered("Press Q or F key to feed your truck",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
			}else if (tutorialState == 5){
				canvas.drawText("Oops! The assembled food doesn't match the recipe", tutorialFont, canvas.getWidth()*0.24f/2,
						canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f, Color.WHITE);
				canvas.drawText("Now, try placing the noodle in the fryer", tutorialFont, canvas.getWidth()*0.24f/2,
						canvas.getHeight()/2 -tutorialFont.getCapHeight(), Color.WHITE);
				canvas.draw(tutorialDownRightArrow, Color.WHITE,  0, 0,
						canvas.getWidth()*2.7f/4, canvas.getHeight()/2 + tutorialFont.getCapHeight()*1.5f,
						90f, -0.3f, 0.3f);

			}else if (tutorialState == 6){
				canvas.drawTextCentered("The food still doesn't match the recipe",tutorialFont,tutorialFont.getCapHeight()*3.5f,Color.WHITE);
				canvas.drawTextCentered("but we can trash it instead of feeding the truck",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
				canvas.drawTextCentered("Hover over or Click on the cooked ingredient and Press T to trash it",tutorialFont,-tutorialFont.getCapHeight()*0.5f,Color.WHITE);
			}else if (tutorialState == 7){
				canvas.drawTextCentered("Now you already learned all the basic cooking skills!",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
				canvas.drawTextCentered("Let's try to make a correct recipe and feed the truck!",tutorialFont,-tutorialFont.getCapHeight()*0.5f,Color.WHITE);
			}
		} else if(levelNum == 7){
			canvas.drawTextCentered("Now, we have three different recipes!",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
			canvas.drawTextCentered("Complete each of them regardless of their order!",tutorialFont,-tutorialFont.getCapHeight()*0.5f,Color.WHITE);
		} else if(levelNum == 8){
			canvas.drawTextCentered("Now, there are multiple ingredient in the recipes",tutorialFont,tutorialFont.getCapHeight()*1.5f,Color.WHITE);
			canvas.drawTextCentered("Complete each of them!",tutorialFont,-tutorialFont.getCapHeight()*0.5f,Color.WHITE);
		}
	}
}

