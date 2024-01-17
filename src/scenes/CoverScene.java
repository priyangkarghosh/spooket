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
import framework.components.SpriteRenderer;
import framework.components.StarRenderer;
import framework.math.Vector;
import game.Game;

/**
 * The Class CoverScene for the cover page.
 *
 * @author priyangkar ghosh
 */
public class CoverScene extends Scene {
	
	/** The load game button. */
	private JButton load = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\load.png")
	);

	/**
	 * Start.
	 */
	@Override
	public void start() {
		// adds a renderer to draw the cover page
		GameObject pageHolder = new GameObject();
		pageHolder.transform.position.translate(new Vector(640, 360));
		new SpriteRenderer(pageHolder, "assets\\images\\gui\\cover_page.png", 5);
		
		// adds a button for the user to move on to the next scene
		Game.getContentPane().addButton(this.load, 944, 580, 314, 94);
		
		// renders stars in the background
		StarRenderer.getInstance().enabled = true;
	}

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// if the load button is clicked, it progresses to the main menu
		if (e.getSource() == this.load) Scene.load(new MainScene());
	}
	
	//* SCENE DOES NOTHING IN THE FOLLOWING UPDATES */
	
	/**
	 * Update.
	 */
	@Override
	public void update() { }	

	/**
	 * Fixed update.
	 */
	@Override
	public void fixedUpdate() { }

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
