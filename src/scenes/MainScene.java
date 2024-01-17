/**
 *
 */
package scenes;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import framework.common.GameObject;
import framework.common.InputManager;
import framework.components.SpriteRenderer;
import framework.math.Vector;
import game.Game;

/**
 * The Class MainMenu.
 *
 * @author priyangkar ghosh
 */
public class MainScene extends Scene {	
	/** The Constant TITLE_POS. */
	private static final Vector TITLE_POS = new Vector(664, 150);
	
	/** The Constant MOUSE_OFFSET. */
	private static final double MOUSE_OFFSET = 5;

	/** The back. */
	// game objects with sprite renderer components
	private GameObject back = new GameObject();
	
	/** The stars. */
	private GameObject stars = new GameObject();
	
	/** The sparkle. */
	private GameObject sparkle = new GameObject();
	
	/** The title. */
	private GameObject title = new GameObject();
	
	/** The input manager. */
	private InputManager inputManager = new InputManager();

	/** The play button. */
	private JButton play = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\play.png")
	);
	
	/** The how to play button. */
	private JButton howTo = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\how_to.png")
	);

	/**
	 * Start.
	 */
	@Override
	public void start() {
		// adds the sprite renderer components
		new SpriteRenderer(this.back, SpriteRenderer.PRELOADS.get("back"));
		new SpriteRenderer(this.stars, SpriteRenderer.PRELOADS.get("stars"));
		new SpriteRenderer(this.sparkle, SpriteRenderer.PRELOADS.get("sparkle"));
		new SpriteRenderer(this.title, SpriteRenderer.PRELOADS.get("title"));
		
		// translates the components to the correct position
		this.back.transform.position.translate(Game.CENTER);
		this.stars.transform.position.translate(Game.CENTER);
		this.sparkle.transform.position.translate(Game.CENTER);
		this.title.transform.position.translate(TITLE_POS);
		
		// adds the buttons
		Game.getContentPane().addButton(this.play, 483, 273, 314, 94);
		Game.getContentPane().addButton(this.howTo, 483, 391, 314, 94);
	}

	/**
	 * Fixed update.
	 */
	@Override
	public void fixedUpdate() {
		this.inputManager.updateMousePosition();

		long time = System.currentTimeMillis();
		Vector cosSin = new Vector(Math.cos(time), Math.sin(time)).scale(Game.FIXED_TIMESTEP);

		this.back.transform.position.translate(cosSin.scale(5 * Math.random()));
		this.stars.transform.position.translate(cosSin.scale(25 * Math.random()));
		this.sparkle.transform.position.translate(cosSin.scale(50 * Math.random()));

		this.title.transform.position = TITLE_POS.add(
				this.inputManager.getMouseDirection(TITLE_POS).scale(MOUSE_OFFSET)
		);
	}

	/**
	 * Action performed.
	 *
	 * @param {ActionEvent} e - the action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// loads into the next scene depending on the button pressed
		if (e.getSource() == this.play)
			Scene.load(new PlayScene());
		if (e.getSource() == this.howTo)
			Scene.load(new HowToScene());
	}
	
	//** UNUSED METHODS */
	
	/**
	 * Update.
	 */
	@Override
	public void update() { }

	/**
	 * Key pressed.
	 *
	 * @param {KeyEvent} key event - the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) { }

	/**
	 * Key released.
	 *
	 * @param {KeyEvent} key event - the key event
	 */
	@Override
	public void keyReleased(KeyEvent e) { }

	/**
	 * Mouse pressed.
	 *
	 * @param {MouseEvent} mouse event - the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e) { }

	/**
	 * Mouse released.
	 *
	 * @param {MouseEvent} mouse event - the mouse event
	 */
	@Override
	public void mouseReleased(MouseEvent e) { }
}
