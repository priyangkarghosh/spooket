package game;

import java.awt.event.ActionListener;

import framework.audio.AudioManager;
import framework.common.GameObject;
import framework.components.Component;
import framework.math.Vector;
import gui.GamePanel;
import gui.Window;
import scenes.CoverScene;
import scenes.Scene;

/**
 * The Class Game, also the main/driver class.
 * 
 * @author priyangkar ghosh
 */
public class Game {
	/** The Constant NAME. */
	private static final String NAME = "spooket";

	/** The Constant WINDOW_RES. */
	public static final Vector WINDOW_RES = new Vector(1280, 720);
	
	/** The Constant CENTER. */
	public static final Vector CENTER = WINDOW_RES.scale(0.5d);

	/** The Constant CONTENT_PANE. */
	private static final GamePanel CONTENT_PANE = new GamePanel();

	/** The Prefab Constant. */
	private static final GameObject PREFAB_OBJECT = new GameObject();

	/** The Constant MAX_FPS. */
	private static final int MAX_FPS = 400;

	/** The Constant METER_TO_PIXEL. */
	public static final int METER_TO_PIXEL = 32;

	/** The Constant FIXED_TIMESTEP. */
	public static final double FIXED_TIMESTEP = 0.0222;

	/** The Constant RENDER_TIMESTEP. */
	public static final int RENDER_TIMESTEP = 1000 / MAX_FPS;

	/** The Constant LERP_CONSTANT, controls how quickly things linearly interpolate. */
	public static final double LERP_CONSTANT = 0.5;
	
	/**  If the game is running. */
	public static volatile boolean running = true;
	
	/**
	 * The main method.
	 *
	 * @param {String[]} args - the arguments
	 */
	public static void main(String[] args) {
		Window window = new Window(NAME, false, Window.getDisplayCenter(), WINDOW_RES);
		window.setVisible(true);
		
		// initializes the player prefs
		PlayerPrefs.init();
		AudioManager.play("music", true);
		
		// initializes the time steps
		long renderNanoStep = 1000000 * RENDER_TIMESTEP;
		double fixedNanoStep = 1000000000 * FIXED_TIMESTEP;
		
		// loads the initial scene
		Scene.load(new CoverScene());
		
		// starts the game loop thread
		Thread gameLoop = new Thread(() -> {
			double delta = 0;
			long start = System.nanoTime();
			
			// runs the loop until game is stopped
			while (running) {
				long now = System.nanoTime();
				delta += (now - start) / fixedNanoStep;
				start = now;
				
				// updates if it's time for the fixed timestep
				while (delta >= 1) {
					Component.refresh();
					Scene.get().fixedUpdate();
					delta--;
				}
				
				// updates if it's time for a render step
				Scene.get().update();
				CONTENT_PANE.repaint();
				while (now - start < renderNanoStep) {
					Thread.yield();

					// this stops the app from consuming all your CPU
					try { Thread.sleep(1); }
					catch (Exception e) {  }

					now = System.nanoTime();
				}
			}
		});
		
		// runs the loop
		gameLoop.run();
	}

	/**
	 * Gets the content pane.
	 *
	 * @return {GamePanel} the content pane
	 */
	public static GamePanel getContentPane() {
		return CONTENT_PANE;
	}

	/**
	 * Gets the action listener.
	 *
	 * @return {ActionListener} the action listener
	 */
	public static ActionListener getActionListener() {
		return CONTENT_PANE;
	}

	/**
	 * Gets the prefab object.
	 *
	 * @return {GameObject} the prefab object
	 */
	public static GameObject getPrefabObject() {
		return PREFAB_OBJECT;
	}
}