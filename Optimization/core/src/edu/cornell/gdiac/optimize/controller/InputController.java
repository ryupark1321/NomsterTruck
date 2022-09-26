/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * This class is a singleton for this application, but we have not designed
 * it as one.  That is to give you some extra functionality should you want
 * to add multiple ships.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize.controller;

import com.badlogic.gdx.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.optimize.kitchen.Appliance;
import edu.cornell.gdiac.optimize.kitchen.Ingredient;

import edu.cornell.gdiac.util.*;

import java.util.ArrayList;

/**
 * Class for reading player input. 
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only 
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class InputController {
	// Fields to manage game state
	/** Whether the reset button was pressed. */
	protected boolean resetPressed;
	protected boolean prevResetPressed;
	/** Mouse position */
	private Vector2 mousePos;
	/** Whether the flood button was pressed. */
	protected boolean floodPressed;
	/** Whether the exit button was pressed. */
	protected boolean exitPressed;
	/** Whether the fire button was pressed. */
	private boolean firePressed;
	/** Whether the player should move forward. */
	private boolean moveForward;
	/** Whether the player should move backward. */
	private boolean moveBackward;
	/** Whether the player should turn left. */
	private boolean turnLeft;
	/** Whether the player should turn right. */
	private boolean turnRight;
	/** XBox Controller support */
	private XBoxController xbox;
	/** Previous click ingredient */
	private int prevIngredientPressed;
	/** Previous ingredient input */
	private int prevIngredientInput;

	/** Whether the player press reset in the last turn. */
	private boolean resetLast;
	/** Whether the player should move forward in the last turn. */
	private boolean moveForwardLast;
	/** Whether the player should move backward in the last turn. */
	private boolean moveBackwardLast;
	/** Whether the player should turn left in the last turn. */
	private boolean turnLeftLast;
	/** Whether the player should turn right in the last turn. */
	private boolean turnRightLast;
	/** Whether the ingredient to add is a potato */
	private boolean addPotato;
	/** Whether the ingredient to add is a carrot */
	private boolean addCarrot;
//	private boolean trash;
//	private boolean assemble;
	private boolean feed;
	private boolean inPot;
	private boolean onBoard;
	private boolean trashAssemble;
	private boolean onAssemble;
	private boolean inFryer;
	private boolean pause;
	private boolean play;
	private boolean control;
	private boolean next;

	private boolean isHoveringOnAppliance;
	// Variable for which appliance the hover is on: 0 chopping board 1 stove 2 fryer 3 plate 4 trash
	private int hoveringOnwhichAppliance;

	private boolean isHoveringOnIngredient;
	// Variable for which appliance the hover is on
	private int hoveringOnwhichIngredient;

	// x,y formatted
	private Vector2 choppingBoardPos;
	private Vector2 choppingBoardSize;
	private Vector2 stovePos;
	private Vector2 stoveSize;
	private Vector2 fryerPos;
	private Vector2 fryerSize;
	private Vector2 platePos;
	private Vector2 plateSize;
	private Vector2 trashPos;
	private Vector2 trashSize;
	private float ing1X;
	private float ing2X;
	private float ing3X;
	private float ing4X;
	private float ing5X;
	private float ingY;
	private float ingSize;

	private boolean foodShootingClick;
	private Vector2 foodShootingScreenCoordinate;

	/** Current state of the slider. */
	private int sliderState;

	/** Whether the click bar just pressed. */
	private boolean clickPressed;

	/** Awake the selected appliance to work. */
	private int awakeAppliance;

	/** The current selected ingredient */
	private int selectedTargetIngredient = -1;

	private static final int NOT_SELECT = -1;
	/** Whether the trash key is pressed */
	private boolean trashPressed;
	/** Whether the assemble key is pressed */
	private boolean assemblePressed;


	/**
	 * Creates a new input controller
	 *
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */
	public InputController() {
		// If we have a game-pad for id, then use it.
		Array<XBoxController> controllers = Controllers.get().getXBoxControllers();
		if (controllers.size > 0) {
			xbox = controllers.get(0);
		} else {
			xbox = null;
		}
		prevIngredientPressed = NOT_SELECT;
		foodShootingClick = false;
		sliderState = 2;
		selectedTargetIngredient = NOT_SELECT;
		clickPressed = false;
		awakeAppliance = NOT_SELECT;
		trashPressed = false;
		assemblePressed = false;
		trashAssemble = false;
		feed = false;
		next = false;
	}

	public boolean updatePause() {
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			pause = Gdx.input.getX() <= 60 && Gdx.input.getY() <= 60;
		}
		return pause;
	}

	public void setIngredientConstants(ArrayList<Float> constants){
		ing1X = constants.get(0);
		ing2X = constants.get(1);
		ing3X = constants.get(2);
		ing4X = constants.get(3);
		ing5X = constants.get(4);
		ingY = constants.get(5);
		ingSize = constants.get(6);
	}

	public void setApplianceConstants(ArrayList<Vector2> constants){
		choppingBoardPos = constants.get(0);
		choppingBoardSize = constants.get(1);
		stovePos = constants.get(2);
		stoveSize = constants.get(3);
		fryerPos = constants.get(4);
		fryerSize = constants.get(5);
		platePos = constants.get(6);
		plateSize = constants.get(7);
		trashPos = constants.get(8);
		trashSize = constants.get(9);
	}

	public boolean readNext() {
		next = false;
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			next = Gdx.input.getX() >= Gdx.graphics.getWidth()*2/3 &&  Gdx.input.getX() <= Gdx.graphics.getWidth()*2/3 + Gdx.graphics.getWidth()/5
					&& Gdx.input.getY() >= Gdx.graphics.getHeight()*4/9 && Gdx.input.getY() <= Gdx.graphics.getHeight()*4/9+Gdx.graphics.getWidth()/5*0.423f;
		}
		return next;
	}

	/**
	 * Returns the current state of the slider
	 * @return the current state of the slider
	 */
	public int getSliderState() {return sliderState;}

	public void setSliderState(int value) {sliderState = value;}


	public boolean getFoodShootingClick(){
		return foodShootingClick;
	}

	public boolean getTrashAssemble(){
		return trashAssemble;
	}

	public void setTrashAssemble(boolean b){
		trashAssemble = b;
	}

	public boolean getTrashPressed(){
		return trashPressed;
	}

	public boolean getOnBoard(){
		return onBoard;
	}

	public boolean getInFryer(){
		return inFryer;
	}

	public boolean getInPot(){
		return inPot;
	}

	public Vector2 getFoodShootingScreenCoordinate(){
		return foodShootingScreenCoordinate;
	}

	public void setFoodShootingClick(boolean v){
		foodShootingClick = v;
	}

	public int getSelectedTargetIngredient() {return selectedTargetIngredient;}


	public void resetSelectedTargetIngredient() { selectedTargetIngredient = NOT_SELECT;}

	public boolean getClickPressed() {return clickPressed;}

	public int getAwakeAppliance() {return awakeAppliance;}

	public void resetAwakeAppliance() {awakeAppliance = NOT_SELECT;}

	public boolean getisHoveringOnAppliance() {return isHoveringOnAppliance;}

	public boolean getisHoveringOnIngredient() {return isHoveringOnIngredient;}

	public int getHoveringOnwhichIngredient() {return hoveringOnwhichIngredient;}

	public int getHoveringOnwhichAppliance() {return hoveringOnwhichAppliance;}

	public void setisHoveringOnAppliance(boolean b) {isHoveringOnAppliance = b;}

	public void setisHoveringOnIngredient(boolean b) {isHoveringOnIngredient = b;}

	public void setHoveringOnwhichAppliance(int i) {hoveringOnwhichAppliance = i;}

	public void setHoveringOnwhichIngredient(int i) {hoveringOnwhichIngredient = i;}
	
	public boolean getAssemblePressed() {return assemblePressed;}

	public void resetAssembledPressed() {assemblePressed = false;}

	public boolean getFeedPressed() {return feed;}

	public void resetFeed() { feed = false;}

	public boolean didAssemble() {
		return Gdx.input.isKeyPressed(Input.Keys.SPACE);
	}

	public boolean didFeed() {
		return (Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.F));
	}


	/**
	 * Returns whether the player should move forward.
	 * @return whether the player should move forward.
	 */
	public boolean isMoveForward() { return moveForward;}

	/**
	 * Returns whether the player should move backward.
	 * @return whether the player should move backward.
	 */
	public boolean isMoveBackward() { return moveBackward; }

	/**
	 * Returns whether the player should turn left.
	 * @return whether the player should turn left.
	 */
	public boolean isTurnLeft() { return turnLeft; }

	/**
	 * Returns whether the player should turn right.
	 * @return whether the player should turn right.
	 */
	public boolean isTurnRight() { return turnRight; }

	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = none
	 *
	 * @return the amount of sideways movement.
	 */
	public int getPrevIngredientPressed() {
		return prevIngredientPressed;
	}

	/**
	 * Returns the previous input ingredient.
	 *
	 * -1 = none
	 *
	 * @return the previous input ingredient.
	 */
	public int getPrevIngredientInput() {
		return prevIngredientInput;
	}

	/**
	 * Returns the current mouse position
	 *
	 * @return current mouse position
	 */
	public Vector2 getMousePos() {
		return mousePos;
	}


	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = none
	 *
	 * @return the amount of sideways movement.
	 */
	public void resetPrevIngredientPressed() {
		 prevIngredientPressed = -1;
	}
	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = none
	 *
	 * @return the amount of sideways movement.
	 */
	public void resetPrevIngredientInput() {
		prevIngredientInput = -1;
	}


	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
		return resetPressed && !prevResetPressed;
	}

	public boolean updateReset() {
		prevResetPressed = resetPressed;
		resetPressed = resetPressed = (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) && !resetLast;
		return resetPressed && !prevResetPressed;
	}

	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed;
	}

	public boolean didAddPotato(){ return addPotato; }

	public boolean didAddCarrot(){ return addCarrot; }

	public boolean isOnBoard(){ return onBoard; }

	public boolean isInPot(){ return inPot; }

	public boolean isInFryer(){ return inFryer; }

	public boolean didPause(){ return pause; }

	public boolean didPlay(){ return play;}

	public boolean didControl(){return control;}


	private boolean onIngredient(float ingX){
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		return Gdx.input.getX() >= (ingX - ingSize / 3) * width && Gdx.input.getX() <= (ingX + ingSize / 3) * width
				&& (Gdx.input.getY() >= (1 - (ingY + ingSize * 4 / 3)) * height &&
				Gdx.input.getY() <= (1 - (ingY - ingSize / 3)) * height);
	}

	private boolean onAppliance(Vector2 pos, Vector2 size){
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		return (Gdx.input.getX() >= pos.x * width && Gdx.input.getX() <= (pos.x + size.x) * width)
				&& (Gdx.input.getY() >= (1 - (pos.y + size.y)) * height &&
				Gdx.input.getY() <= (1 - pos.y) * height);
	}

	public void readControl(){
		if (!pause) {
			control = (Gdx.input.getX() >= 75 && Gdx.input.getX() <= 135) && Gdx.input.getY() <= 60;
		}
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 */
	public void readInput() {
		foodShootingClick = false;
		foodShootingScreenCoordinate = null;
		prevResetPressed = resetPressed;
		clickPressed = false;
//		awakeAppliance = NOT_SELECT;
		mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		control = (Gdx.input.getX() >= 75 && Gdx.input.getX() <= 135) && Gdx.input.getY() <= 60;
		// Check to see if a GamePad is connected
		if (xbox != null && xbox.isConnected()) {
			readGamepad();
			readKeyboard(true); // Read as a back-up
		} else {
			readKeyboard(false);
			float[] ingXPosArr = new float[]{ing3X,ing2X,ing4X,ing1X,ing5X};
			Vector2[] appPosArr = new Vector2[]{choppingBoardPos, stovePos,fryerPos,platePos, trashPos};
			Vector2[] appSizeArr = new Vector2[]{choppingBoardSize, stoveSize,fryerSize,plateSize, trashSize};
			if (!Gdx.input.justTouched()) {
				for (int i = 0; i < appPosArr.length; i++){
					if (onAppliance(appPosArr[i], appSizeArr[i])) {
						isHoveringOnAppliance = true;
						hoveringOnwhichAppliance = i;
					}
				}
				for (int i = 0; i < ingXPosArr.length; i++){
					if (onIngredient(ingXPosArr[i])){
						isHoveringOnIngredient = true;
						hoveringOnwhichIngredient = i;
					}
				}
			}
			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				// Detect the selected ingredient
				boolean detected = false;
				for (int i = 0; i < ingXPosArr.length; i++){
					if (onIngredient(ingXPosArr[i])){
						selectedTargetIngredient = i;
						detected = true;
					}
				}
				// Detect the selected appliance
				for (int i = 0; i < appPosArr.length; i++){
					if (onAppliance(appPosArr[i], appSizeArr[i])) {
						awakeAppliance = i;
						detected = true;
					}
				}
				if (!detected) {
					resetAwakeAppliance();
					resetSelectedTargetIngredient();
				}

				pause = Gdx.input.getX() <= 60 && Gdx.input.getY() <= 60;
				play = Gdx.input.getX() <= Gdx.graphics.getWidth() / 2f + 35 && Gdx.input.getX() >= Gdx.graphics.getWidth() / 2f - 35;
				play &= Gdx.input.getY() <= Gdx.graphics.getHeight() / 2f + 35 && Gdx.input.getY() >= Gdx.graphics.getHeight() / 2f - 35;
				// Detect which tile is pressed for food throwing
				if (Gdx.input.getY() <= Gdx.graphics.getHeight() * 2 / 3f - 25f) {
					foodShootingClick = true;
					foodShootingScreenCoordinate = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				}
			}
		}
	}


		/**
		 * Reads input from an X-Box controller connected to this computer.
		 */
		private void readGamepad () {

			resetPressed = xbox.getA();
			floodPressed = xbox.getRBumper();
			exitPressed = xbox.getBack();

			// Increase animation frame, but only if trying to move
			firePressed = xbox.getRightTrigger() > 0.6f;
		}

		/**
		 * Reads input from the keyboard.
		 *
		 * This controller reads from the keyboard regardless of whether or not an X-Box
		 * controller is connected.  However, if a controller is connected, this method
		 * gives priority to the X-Box controller.
		 *
		 * @param secondary true if the keyboard should give priority to a gamepad
		 */
		private void readKeyboard ( boolean secondary){
			// Give priority to gamepad results
			resetPressed = (secondary && resetPressed) || ((Gdx.input.isKeyPressed(Input.Keys.NUM_0)) && !resetLast);
			//floodPressed = (secondary && floodPressed) || (Gdx.input.isKeyPressed(Input.Keys.F));
			exitPressed = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));

			turnLeft = Gdx.input.isKeyPressed(Input.Keys.A) && !turnLeftLast;
			turnRight = Gdx.input.isKeyPressed(Input.Keys.D) && !turnRightLast;
			moveForward = Gdx.input.isKeyPressed(Input.Keys.W) && !moveForwardLast;
			moveBackward = Gdx.input.isKeyPressed(Input.Keys.S) && !moveBackwardLast;

			trashPressed = Gdx.input.isKeyPressed(Input.Keys.T);
			assemblePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

			resetLast = Gdx.input.isKeyPressed(Input.Keys.NUM_0);
			turnLeftLast = Gdx.input.isKeyPressed(Input.Keys.A);
			turnRightLast = Gdx.input.isKeyPressed(Input.Keys.D);
			moveForwardLast = Gdx.input.isKeyPressed(Input.Keys.W);
			moveBackwardLast = Gdx.input.isKeyPressed(Input.Keys.S);

//		firePressed =  (secondary && firePressed) || Gdx.input.isKeyPressed(Input.Keys.SPACE);

			addPotato = Gdx.input.isKeyPressed(Input.Keys.NUM_1);
			addCarrot = Gdx.input.isKeyPressed(Input.Keys.NUM_2);
//		trash = Gdx.input.isKeyPressed(Input.Keys.T);
//		assemble = Gdx.input.isKeyPressed(Input.Keys.SPACE);

			feed = Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.F);
			pause = Gdx.input.isKeyPressed(Input.Keys.P);
			play = Gdx.input.isKeyPressed(Input.Keys.ENTER);
		}

		public void resetTrashAssemble () {
			trashAssemble = false;
		}
	}
