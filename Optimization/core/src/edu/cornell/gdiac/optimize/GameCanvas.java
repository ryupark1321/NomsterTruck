/*
 * GameCanvas.java
 *
 * To properly follow the model-view-controller separation, we should not have
 * any specific drawing code in GameMode. All of that code goes here.  As
 * with GameEngine, this is a class that you are going to want to copy for
 * your own projects.
 *
 * An important part of this canvas design is that it is loosely coupled with
 * the model classes. All of the drawing methods are abstracted enough that
 * it does not require knowledge of the interfaces of the model classes.  This
 * important, as the model classes are likely to change often.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
package edu.cornell.gdiac.optimize;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;


/**
 * Primary view class for the game, abstracting the basic graphics calls.
 * 
 * This version of GameCanvas only supports (rectangular) Sprite drawing.
 * support for polygonal textures and drawing primitives will be present
 * in future labs.
 */
public class GameCanvas {
	/** While we are not drawing polygons (yet), this spritebatch is more reliable */
	private SpriteBatch spriteBatch;
	
	/** Track whether or not we are active (for error checking) */
	private boolean active;
	
	/** The current color blending mode */
	private BlendState blend;
	
	/** Value to cache window width (if we are currently full screen) */
	int width;
	/** Value to cache window height (if we are currently full screen) */
	int height;

	// For managing the camera and perspective
	/** Orthographic camera for the SpriteBatch layer */
	private OrthographicCamera spriteCam;
	/** Target for Perspective FOV */
	private Vector3 target;
	/** Eye for Perspective FOV */
	private Vector3 eye;

	// CACHE OBJECTS
	/** Affine cache for current sprite to draw */
	private Affine2 local;
	/** Cache object to unify everything under a master draw method */
	private TextureRegion holder;

	/** Projection Matrix */
	private Matrix4 proj;
	/** View Matrix */
	private Matrix4 view;
	/** World Matrix */
	private Matrix4 world;
	/** Temporary Matrix (for Calculations) */
	private Matrix4 tmpMat;

	/** Temporary Vectors */
	private Vector3 tmp0;
	private Vector3 tmp1;
	private Vector2 tmp2d;


	/**
	 * Creates a new GameCanvas determined by the application configuration.
	 * 
	 * Width, height, and fullscreen are taken from the LWGJApplicationConfig
	 * object used to start the application.  This constructor initializes all
	 * of the necessary graphics objects.
	 */
	public GameCanvas() {
		active = false;
		spriteBatch = new SpriteBatch();

		//spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteCam = new OrthographicCamera(getWidth(),getHeight());
		spriteCam.setToOrtho(false);
		spriteBatch.setProjectionMatrix(spriteCam.combined);

		// Set the projection matrix (for proper scaling)
//		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());

		// Initialize the perspective camera objects
		eye = new Vector3();
		target = new Vector3();
		world = new Matrix4();
		view  = new Matrix4();
		proj  = new Matrix4();

		// Initialize the cache objects
		tmpMat = new Matrix4();
		tmp0  = new Vector3();
		tmp1  = new Vector3();
		tmp2d = new Vector2();


		// Initialize the cache objects
		holder = new TextureRegion();
		local  = new Affine2();
	}
		
    /**
     * Eliminate any resources that should be garbage collected manually.
     */
    public void dispose() {
		if (active) {
			Gdx.app.error("GameCanvas", "Cannot dispose while drawing active", new IllegalStateException());
			return;
		}
		spriteBatch.dispose();
    	spriteBatch = null;
    	local  = null;
    	holder = null;
    }

	/**
	 * Returns the width of this canvas
	 *
	 * This currently gets its value from Gdx.graphics.getWidth()
	 *
	 * @return the width of this canvas
	 */
	public int getWidth() {
		return Gdx.graphics.getWidth();
	}
	
	/**
	 * Changes the width of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param width the canvas width
	 */
	public void setWidth(int width) {
		if (active) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.width = width;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(width, getHeight());
		}
		resize();
	}
	
	/**
	 * Returns the height of this canvas
	 *
	 * This currently gets its value from Gdx.graphics.getHeight()
	 *
	 * @return the height of this canvas
	 */
	public int getHeight() {
		return Gdx.graphics.getHeight();
	}
	
	/**
	 * Changes the height of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param height the canvas height
	 */
	public void setHeight(int height) {
		if (active) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.height = height;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(getWidth(), height);	
		}
		resize();
	}
	
	/**
	 * Returns the dimensions of this canvas
	 *
	 * @return the dimensions of this canvas
	 */
	public Vector2 getSize() {
		return new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}
	
	/**
	 * Changes the width and height of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param width the canvas width
	 * @param height the canvas height
	 */
	public void setSize(int width, int height) {
		if (active) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.width = width;
		this.height = height;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(width, height);
		}
		resize();
	}
	
	/**
	 * Returns whether this canvas is currently fullscreen.
	 *
	 * @return whether this canvas is currently fullscreen.
	 */	 
	public boolean isFullscreen() {
		return Gdx.graphics.isFullscreen(); 
	}
	
	/**
	 * Sets whether or not this canvas should change to fullscreen.
	 *
	 * If desktop is true, it will use the current desktop resolution for
	 * fullscreen, and not the width and height set in the configuration
	 * object at the start of the application. This parameter has no effect
	 * if fullscreen is false.
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param fullscreen Whether this canvas should change to fullscreen.
	 * @param desktop 	 Whether to use the current desktop resolution
	 */	 
	public void setFullscreen(boolean value) {
		if (active) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		if (value) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(width, height);
		}
	}
	
	/**
	 * Resets the SpriteBatch camera when this canvas is resized.
	 *
	 * If you do not call this when the window is resized, you will get
	 * weird scaling issues.
	 */
	 public void resize() {
		 // Resizing screws up the spriteBatch projection matrix
		 spriteCam.setToOrtho(false,getWidth(),getHeight());
		 spriteBatch.setProjectionMatrix(spriteCam.combined);

		 // Resizing screws up the spriteBatch projection matrix
//		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
	}
	
	/**
	 * Returns the current color blending state for this canvas.
	 *
	 * Textures draw to this canvas will be composited according
	 * to the rules of this blend state.
	 *
	 * @return the current color blending state for this canvas
	 */
	public BlendState getBlendState() {
		return blend;
	}
	
	/**
	 * Sets the color blending state for this canvas.
	 *
	 * Any texture draw subsequent to this call will use the rules of this blend 
	 * state to composite with other textures.  Unlike the other setters, if it is 
	 * perfectly safe to use this setter while  drawing is active (e.g. in-between 
	 * a begin-end pair).  
	 *
	 * @param state the color blending rule
	 */
	public void setBlendState(BlendState state) {
		if (state == blend) {
			return;
		}
		switch (state) {
		case NO_PREMULT:
			spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
			break;
		case ALPHA_BLEND:
			spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
			break;
		case ADDITIVE:
			spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE);
			break;
		case OPAQUE:
			spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ZERO);
			break;
		}
		blend = state;
	}

	/**
	 * Start and active drawing sequence with the identity transform.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 */
    public void begin() {
    	spriteBatch.begin();
    	active = true;

		// Set the projection matrix (for proper scaling)
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());

		// Initialize the cache objects
		holder = new TextureRegion();
		local  = new Affine2();
//		Gdx.gl.glViewport( 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); ?
		//Set up camera with viewport in mind
//		draw( delta );
		//Set up camera again with other viewport in mind
//		draw( delta );

    }

	/**
	 * Ends a drawing sequence, flushing textures to the graphics card.
	 */
    public void end() {
    	spriteBatch.end();
    	active = false;
    }
    
	/**
     * Draw the seamless background image.
     *
     * The background image is drawn (with NO SCALING) at position x, y.  Width-wise, 
     * the image is seamlessly scrolled; when we reach the image we draw a second copy.  
     *
     * To work properly, the image should be wide and high enough to fill the screen.
     * 
     * @param image  Texture to draw as an overlay
	 * @param x      The x-coordinate of the bottom left corner
	 * @param y 	 The y-coordinate of the bottom left corner
	 */
    public void drawBackground(Texture image, float x, float y) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		float w = image.getWidth();
        // Have to draw the background twice for continuous scrolling.
        spriteBatch.draw(image, x,   y);
        spriteBatch.draw(image, x+w, y);
    }

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */
	public void draw(Texture image, float x, float y) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
		// Unlike Lab 1, we can shortcut without a master drawing method
    	spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(image, x,  y);
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */
	public void draw(Texture image, float x, float y, Color color) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		spriteBatch.setColor(color);
		spriteBatch.draw(image, x,  y);
	}



	public void draw(Texture image, float x, float y, float width, float height) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
    	spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(image, x,  y, width, height);
	}

	public void draw(Texture image, float x, float y, float width, float height, Color color) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		spriteBatch.setColor(color);
		spriteBatch.draw(image, x,  y, width, height);
		spriteBatch.setColor(Color.WHITE);
	}

	/**
	 * Draws the tinted texture with the given transformations
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin
	 * @param y 	The y-coordinate of the texture origin
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */	
	public void draw(Texture image, Color tint, float ox, float oy, 
					float x, float y, float angle, float sx, float sy) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
		// Call the master drawing method (we have to for transforms)
		holder.setRegion(image);
		draw(holder,tint,ox,oy,x,y,angle,sx,sy);
	}
	
	/**
	 * Draws the tinted texture region (filmstrip) at the given position.
	 *
	 * A texture region is a single texture file that can hold one or more textures.
	 * It is used for filmstrip animation.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */
	public void draw(TextureRegion region, float x, float y) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
		// Unlike Lab 1, we can shortcut without a master drawing method
    	spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(region, x,  y);
	}
	
	public void draw(TextureRegion region, float x, float y, float width, float height) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
		// Unlike Lab 1, we can shortcut without a master drawing method
    	spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(region, x,  y, width, height);
	}

	public void draw(Texture texture, Color tint, float ox, float oy,
					 float x, float y, float width, float height){
		TextureRegion tr = new TextureRegion(texture);
		tr.setRegion(texture);
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		local.setToTranslation(x,y);
		local.translate(-ox,-oy);
		spriteBatch.setColor(tint);
		spriteBatch.draw(tr,width,height,local);
	}

	public void draw(TextureRegion textureregion, Color tint, float ox, float oy,
					 float x, float y, float width, float height){
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		local.setToTranslation(x,y);
		local.translate(-ox,-oy);
		spriteBatch.setColor(tint);
		spriteBatch.draw(textureregion,width,height,local);
	}
	
	/**
	 * Draws the tinted texture region (filmstrip) with the given transformations
	 *
	 * THIS IS THE MASTER DRAW METHOD (Modify for exercise 4)
	 *
	 * A texture region is a single texture file that can hold one or more textures.
	 * It is used for filmstrip animation.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin
	 * @param y 	The y-coordinate of the texture origin
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */	
	public void draw(TextureRegion region, Color tint, float ox, float oy, 
					 float x, float y, float angle, float sx, float sy) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		
		computeTransform(ox,oy,x,y,angle,sx,sy);
		spriteBatch.setColor(tint);
		spriteBatch.draw(region,region.getRegionWidth(),region.getRegionHeight(),local);
	}

	public void draw(Texture texture, float ox, float oy,
					 float x, float y, float width, float height, float angle, Color tint){
		TextureRegion tr = new TextureRegion(texture);
		tr.setRegion(texture);
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		computeTransform(ox,oy,x,y,angle,1,1);
		spriteBatch.setColor(tint);
		spriteBatch.draw(tr,width,height,local);
	}

	/**
	 * Compute the affine transform (and store it in local) for this image.
	 * 
	 * This helper is meant to simplify all of the math in the above draw method
	 * so that you do not need to worry about it when working on Exercise 4.
	 *
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin
	 * @param y 	The y-coordinate of the texture origin
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */
	private void computeTransform(float ox, float oy, float x, float y, float angle, float sx, float sy) {
		local.setToTranslation(x,y);
		local.rotate(angle);
		local.scale(sx,sy);
		local.translate(-ox,-oy);
	}

    /**
     * Draws text on the screen.
     *
     * @param text The string to draw
     * @param font The font to use
     * @param x The x-coordinate of the lower-left corner
     * @param y The y-coordinate of the lower-left corner
	 * @param c The color to tint the font
     */
    public void drawText(String text, BitmapFont font, float x, float y, Color c) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		font.setColor(c);
		GlyphLayout layout = new GlyphLayout(font,text);
		font.draw(spriteBatch, layout, x, y);
    }

	public void drawTextBottommAligned(String text, BitmapFont font, float x, float y, Color c) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		font.setColor(c);
		GlyphLayout layout = new GlyphLayout(font,text);
		font.draw(spriteBatch, layout, x, y+font.getCapHeight());
	}

    /**
     * Draws text centered on the screen.
     *
     * @param text The string to draw
     * @param font The font to use
     * @param offset The y-value offset from the center of the screen.
	 * @param c The color to tint the font
     */
    public void drawTextCentered(String text, BitmapFont font, float offset,  Color c) {
		if (!active) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		font.setColor(c);
		GlyphLayout layout = new GlyphLayout(font,text);
		float x = (getWidth()  - layout.width) / 2.0f;
		float y = (getHeight() + layout.height) / 2.0f;
//		font.setColor(Color.WHITE);
		font.draw(spriteBatch, layout, x, y+offset);
    }

	/**
	 * Enumeration of supported BlendStates.
	 *
	 * For reasons of convenience, we do not allow user-defined blend functions.
	 * 99% of the time, we find that the following blend modes are sufficient
	 * (particularly with 2D games).
	 */
	public enum BlendState {
		/** Alpha blending on, assuming the colors have pre-multipled alpha (DEFAULT) */
		ALPHA_BLEND,
		/** Alpha blending on, assuming the colors have no pre-multipled alpha */
		NO_PREMULT,
		/** Color values are added together, causing a white-out effect */
		ADDITIVE,
		/** Color values are draw on top of one another with no transparency support */
		OPAQUE
	}	
}