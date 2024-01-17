package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import framework.common.GameObject;
import framework.common.InputManager;
import framework.components.Scoreboard;
import framework.components.SpriteRenderer;
import framework.components.StarRenderer;
import framework.math.Vector;
import game.Game;
import networking.client.CClient;
import networking.server.Server;

/**
 * The Class GameScene for when the player is actually in game.
 * 
 * @author priyangkar ghosh
 */
public class GameScene extends Scene {	
	/** The Constant NUM_OF_ASTEROIDS. */
	public static final int NUM_OF_ASTEROIDS = 15;
	
	/** The leave and copy code button (used to copy the game code). */
	private JButton leave = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\leave.png")
	);
	
	private JButton copy = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\code.png")
	);	
	
	/** The ship static object. */
	private GameObject ship = new GameObject();
	
	/** if this scene was started as a host */
	private boolean isServerHost;
	
	public GameScene(boolean isServerHost) {
		super();
		
		// sets the variable
		this.isServerHost = isServerHost;
	}

	/**
	 * Start.
	 */
	@Override
	public void start() {
		// adds the leave button
		Game.getContentPane().addButton(this.leave, 1232, 12, 24, 24);
		
		// enables stars in the background
		StarRenderer.getInstance().enabled = true;
		
		// sets initial position and rotation
		this.ship.transform.position.translate(new Vector(1040, 200));
		this.ship.transform.rotation = Math.toRadians(-30);
		new SpriteRenderer(ship, SpriteRenderer.PRELOADS.get("ship"));
		
		// adds the copy game code button
		if (this.isServerHost) {
			Font current = this.copy.getFont();
			Font newFont = current.deriveFont(current.getSize() * 0.8f);
			
			// sets the button text
			JLabel copyLabel = new JLabel("COPY CODE");
			copyLabel.setFont(newFont);
			copyLabel.setForeground(Color.BLACK);
			copyLabel.setBounds(18, 643, 74, 36);
			
			Game.getContentPane().add(copyLabel);
			Game.getContentPane().addButton(this.copy, 8, 648, 74, 26);
		}
	}

	/**
	 * Update.
	 */
	@Override
	public void update() {
		double currentTimeScaled = System.currentTimeMillis() / 100;
		Vector cosSin = new Vector(Math.cos(currentTimeScaled), Math.sin(currentTimeScaled)).scale(Game.FIXED_TIMESTEP);
		
		// moves the ship around in a small circle for it to be a bit more dynamic
		this.ship.transform.position.translate(cosSin.scale(3 * Math.random()));
		this.ship.transform.rotation += cosSin.x * Math.random() * 0.02;
	}

	/**
	 * Fixed update.
	 */
	@Override
	public void fixedUpdate() {
		// ticks both the server and client
		// if either are not running, it won't update
		CClient.instance().tick();
		Server.instance().tick();
	}

	/**
	 * Action performed.
	 *
	 * @param {ActionEvent} e - the action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// gets the action source
		Object source = e.getSource();
		
		// if the user wants to leave the game
		if (source == this.leave) {
			// disconnects the client and stops the server
			CClient.instance().disconnect();
			Server.instance().stop();
			
			// loads the player back into the previous scene
			Scene.load(new PlayScene());
		}
		
		// if the user wants to copy the game code
		else if (source == this.copy) {
			// copies the game code to their clipboard
			StringSelection stringSelection = new StringSelection(Server.instance().getCode());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	}

	/**
	 * Key pressed.
	 *
	 * @param {KeyEvent} e - the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// enables the scoreboard if its key was pressed
		if (InputManager.Controls.VIEW_SCOREBOARD.equals(e.getKeyCode()))
			Scoreboard.getInstance().enabled = true;
	}

	/**
	 * Key released.
	 *
	 * @param {KeyEvent} e - the key event
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// disables the scoreboard if its key stopped being pressed
		if (InputManager.Controls.VIEW_SCOREBOARD.equals(e.getKeyCode()))
			Scoreboard.getInstance().enabled = false;
	}

	/**
	 * Mouse pressed.
	 *
	 * @param {MouseEvent} e - the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// processes this input in the client input manager
		CClient.instance().input.process(e, true);
	}

	/**
	 * Mouse released.
	 *
	 * @param {MouseEvent} e - the mouse event
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// processes this input in the client input manager
		CClient.instance().input.process(e, false);
	}
}
