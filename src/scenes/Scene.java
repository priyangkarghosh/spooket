package scenes;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import framework.components.Component;
import framework.components.Renderer;
import game.Game;

/**
 * The abstract Scene class.
 * 
 * @author priyangkar ghosh
 */
public abstract class Scene {
	/** The current scene. */
	private static Scene current;

	/**
	 * Start.
	 */
	public abstract void start();

	/**
	 * Update.
	 */
	public abstract void update();

	/**
	 * Fixed update.
	 */
	public abstract void fixedUpdate();

	/**
	 * Action performed.
	 *
	 * @param {ActionEvent} e - the action event
	 */
	//events
	public abstract void actionPerformed(ActionEvent e);

	/**
	 * Key pressed.
	 *
	 * @param {KeyEvent} e - the key event
	 */
	public abstract void keyPressed(KeyEvent e);

	/**
	 * Key released.
	 *
	 * @param {KeyEvent} e - the key event
	 */
	public abstract void keyReleased(KeyEvent e);

	/**
	 * Mouse pressed.
	 *
	 * @param {MouseEvent} e - the mouse event
	 */
	public abstract void mousePressed(MouseEvent e);

	/**
	 * Mouse released.
	 *
	 * @param {MouseEvent} e - the mouse event
	 */
	public abstract void mouseReleased(MouseEvent e);

	/**
	 * Gets the current scene.
	 *
	 * @return {Scene} the current scene
	 */
	public static Scene get() {
		return current;
	}

	/**
	 * Loads a new scene.
	 *
	 * @param {Scene} scene - the new scene
	 */
	public static void load(Scene scene) {
		// removes all the components from the old scene
		Game.getContentPane().removeAll();		
		
		// resets both the component and renderer classes
		Component.reset();
		Renderer.reset();
		
		// sets the new scene, then starts it
		Scene.current = scene;
		scene.start();
	}
}
