package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import framework.audio.AudioManager;
import framework.components.Renderer;
import game.Game;
import scenes.Scene;

/**
 * The Class GamePanel.
 * 
 * @author priyangkar ghosh
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	//* TRANSPARENT AND SEMI TRANSPARENT COLOUR CONSTANTS */
	private static final Color TRANSPARENT = new Color(255, 255, 255, 0);
	private static final Color SEMI_TRANSPARENT = new Color(255, 255, 255, 30);

	/**
	 * Instantiates a new game panel.
	 */
	public GamePanel() {
		this.addKeyListener(this);
		this.addMouseListener(this);

		this.setFocusable(true);
		this.requestFocusInWindow();

		this.setBackground(new Color(41, 32, 41));
		this.setLayout(null);		
	}

	/**
	 * Action performed method.
	 *
	 * @param {ActionEvent} e - the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// passes the event to the current scene
			Scene.get().actionPerformed(e);
		}

		catch (NullPointerException ex) { }
	}

	/**
	 * Key pressed.
	 *
	 * @param {KeyEvent} key event - the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		try {
			// passes the event to the current scene
			Scene.get().keyPressed(e);
		}

		catch (NullPointerException ex) { }
	}

	/**
	 * Key released.
	 *
	 * @param {KeyEvent} key event - the key event
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		try {
			// passes the event to the current scene
			Scene.get().keyReleased(e);
		}

		catch (NullPointerException ex) { }
	}

	/**
	 * Mouse pressed.
	 *
	 * @param {MouseEvent} mouse event - the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		try {
			// passes the event to the current scene
			Scene.get().mousePressed(e);
		}

		catch (NullPointerException ex) { }
	}

	/**
	 * Mouse released.
	 *
	 * @param {MouseEvent} mouse event - the mouse event
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			// passes the event to the current scene
			Scene.get().mouseReleased(e);
		}

		catch (NullPointerException ex) { }
	}

	/**
	 * Paint component.
	 *
	 * @param {Graphics} g - the graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		Renderer.render(g2d);
		g2d.dispose();
		

		try { Thread.sleep(Game.RENDER_TIMESTEP); }
		catch (InterruptedException e) { }
	}
	
	/**
	 * Adds a button.
	 *
	 * @param {JButton} button - the button
	 * @param {int} x - the x
	 * @param {int} y - the y
	 * @param {int} width - the width
	 * @param {int} height - the height
	 */
	public void addButton(JButton button, int x, int y, int width, int height) {
		// sets the bounds of the button
		button.setBounds(x, y, width, height);
		
		// turns off the button
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setBackground(TRANSPARENT);
		
		// add listener so the button is highlighted when hovered over
		button.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(SEMI_TRANSPARENT);
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(TRANSPARENT);
		    }
		});
		
		// add listener so the button plays a sound when clicked
		button.addActionListener(
				e -> AudioManager.play("press", false)
		);
		
		// adds action listener, and adds component to the content pane
		button.addActionListener(Game.getActionListener());
		Game.getContentPane().add(button);
	}

	// UNUSED METHOD OVERRIDES THAT MUST BE DEFINED

	/**
	 * Mouse clicked.
	 *
	 * @param {MouseEvent} e - the e
	 */
	@Override
	public void mouseClicked(MouseEvent e) { }

	/**
	 * Mouse entered.
	 *
	 * @param {MouseEvent} e - the e
	 */
	@Override
	public void mouseEntered(MouseEvent e) { }

	/**
	 * Mouse exited.
	 *
	 * @param {MouseEvent} e - the e
	 */
	@Override
	public void mouseExited(MouseEvent e) { }

	/**
	 * Key typed.
	 *
	 * @param {KeyEvent} e - the e
	 */
	@Override
	public void keyTyped(KeyEvent e) { }
}
