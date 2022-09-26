package edu.cornell.gdiac.optimize.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import edu.cornell.gdiac.optimize.GDXRoot;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		config.width = (int) genv.getMaximumWindowBounds().getWidth();
		config.height = (int) genv.getMaximumWindowBounds().getHeight();
//		config.width  = 800;
//		config.height = 800;
//		config.width  = 1400;
//		config.height = 860;
//		config.width  = 1520;
//		config.height = 860;
//		config.width  = 1280;
//		config.height = 720;
//		config.width  = 1920;
//		config.height = 1080;

//		config.fullscreen = true;
//		config.resizable = false;
		config.foregroundFPS = 60;

		config.title = "Nomster Truck";
		new LwjglApplication(new GDXRoot(), config);
	}
}
