package scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import framework.common.GameObject;
import framework.components.SpriteRenderer;
import framework.math.Vector;
import game.Game;
import game.PlayerPrefs;
import networking.client.CClient;
import networking.server.Server;

/**
 * The Class PlayScene, where the host can choose how to join or leave a game.
 * 
 * @author priyangkar ghosh
 */
public class PlayScene extends Scene {
	
	/** The Constant MAX_NAME_CHARS. */
	private static final int MAX_NAME_CHARS = 9;
	
	/** The play button. */
	private JButton host = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\host.png")
	);
	
	/** The settings. */
	private JButton join = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\join.png")
	);
	
	/**  The leave button. */
	private JButton leave = new JButton(
			new ImageIcon("assets\\images\\gui\\buttons\\leave.png")
	);

	/** The name. */
	private JLabel nameLabel = new JLabel("Enter username (MAX " + MAX_NAME_CHARS + " CHARS): ");
	private JLabel hsLabel = new JLabel("Kill High Score: " + PlayerPrefs.get("killHS", 0));

	/** The name. */
	private JTextField name = new JTextField(PlayerPrefs.get("name", ""));
	
	/** The back. */
	// game objects with sprite renderer components
	private GameObject back = new GameObject();
	
	/**
	 * Start.
	 */
	@Override
	public void start() {
		// adds a background renderer
		new SpriteRenderer(this.back, SpriteRenderer.PRELOADS.get("back"));	
		this.back.transform.position.translate(Game.CENTER);
		
		// adds the three buttons
		Game.getContentPane().addButton(this.host, 483, 273, 314, 94);
		Game.getContentPane().addButton(this.join, 483, 391, 314, 94);
		Game.getContentPane().addButton(this.leave, 1232, 12, 24, 24);
		
		// initializes some fonts
		Font current, newFont;
		
		// sets the first font
		current = this.nameLabel.getFont();
		newFont = current.deriveFont(current.getSize() * 1.4f);
		
		// adds a label above the name for clarification
		this.nameLabel.setFont(newFont);
		this.nameLabel.setForeground(Color.WHITE);
		this.nameLabel.setBounds(493, 175, 294, 20);
		Game.getContentPane().add(this.nameLabel);
		
		// displays the users highest kills
		this.hsLabel.setFont(newFont);
		this.hsLabel.setForeground(Color.WHITE);
		this.hsLabel.setBounds(12, 12, 294, 20);
		Game.getContentPane().add(this.hsLabel);
		
		// sets the second font
		current = this.name.getFont();
		newFont = current.deriveFont(current.getSize() * 2f);	
		
		// sets the font of the name input field
		this.name.setFont(newFont);
		this.name.setBounds(493, 195, 294, 54);
		this.name.addActionListener(Game.getActionListener());
		Game.getContentPane().add(name);
	}

	/**
	 * Fixed update.
	 */
	@Override
	public void fixedUpdate() {
		long time = System.currentTimeMillis();
		Vector cosSin = new Vector(Math.cos(time), Math.sin(time)).scale(Game.FIXED_TIMESTEP);
		
		// moves the background around a little bit so it is more dynamic
		this.back.transform.position.translate(cosSin.scale(50 * Math.random()));
	}

	/**
	 * Action performed.
	 *
	 * @param {ActionEvent} e - the action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		// goes back to main menu
		if (source == this.leave) 
			Scene.load(new MainScene());
		
		// updates the username
		else if (source == this.name) {
			String text = this.name.getText();
			// limits the number of characters of the text
			this.name.setText(text.substring(0, Math.min(MAX_NAME_CHARS, text.length())));
			// sets the name
			PlayerPrefs.set("name", this.name.getText());
		}
		
		// if the user wants to host a new game
		else if (source == this.host) {
			// loads the game scene
			Scene.load(new GameScene(true));
			
			// starts a new server
			Server.instance().start();
			try {
				// client connects to this new server
				CClient.instance().connect("localhost", Server.instance().getPort());
			} 
			
			catch (IOException ex) {
				// disconnects the client and stops the server
				Scene.load(new PlayScene());
				CClient.instance().disconnect();
				Server.instance().stop();
				
				// if the server couldn't be started properly, or if the client couldn't connect
				// it errors, and then moves the user back to the previous scene
				JOptionPane.showMessageDialog(null, "Could not start the server.");
			}
		}
		
		// if the user wants to join a game
		else if (source == this.join){			
			String code = JOptionPane.showInputDialog("Game Code:");
			
			// checks if the user entered a code
			if (code != null) {
				// if they did, it starts loading the game scene
				Scene.load(new GameScene(false));
				
				try {
					// the program tries to connect to the specified code
					CClient.instance().connect(code);
				} 
				
				// if it can't then it goes back to the play scene
				catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
					// if the code was formatted wrong
					JOptionPane.showMessageDialog(null, "Invalid Game Code!");
					Scene.load(new PlayScene());
				}	
				
				catch (IOException ex) {
					// if the client could not connect to the server
					JOptionPane.showMessageDialog(null, "Could not connect to server.");			
					Scene.load(new PlayScene());
					CClient.instance().disconnect();
				}
			}
		}
	}
	
	//** UNUSED METHODS *//
	
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
