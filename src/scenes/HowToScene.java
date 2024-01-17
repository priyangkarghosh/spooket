package scenes;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import framework.common.GameObject;
import framework.components.SpriteRenderer;
import game.Game;

/**
 * The How to play scene for the user.
 * 
 * @author priyangkar ghosh
 */
public class HowToScene extends Scene {
	/**  The leave button. */
	private JButton leave = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\leave.png")
	);
	
	/**
	 * Start.
	 */
	@Override
	public void start() {
		// adds the leave button
		Game.getContentPane().addButton(this.leave, 1232, 12, 24, 24);
		
		// displays the "how to play" image
		GameObject center = new GameObject();
		center.transform.position.translate(Game.CENTER);
		
		new SpriteRenderer(center, SpriteRenderer.PRELOADS.get("how_to"));
	}
	
	/**
	 * Action performed.
	 *
	 * @param {ActionEvent} e - the action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.leave) 
			Scene.load(new MainScene());
	}
	
	//** UNUSED METHODS */
	
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
