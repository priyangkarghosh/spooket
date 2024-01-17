package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import framework.math.Vector;
import game.Game;
import game.PlayerPrefs;
import networking.client.CClient;
import networking.server.Server;

/**
 * The Class Window.
 * 
 * @author priyangkar ghohs
 */
public class Window extends JFrame {
	/**
	 * Gets the display center.
	 *
	 * @return {Vector} - the display center
	 */
	public static Vector getDisplayCenter() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Vector(screenSize.getWidth() / 2d, screenSize.getHeight() / 2d);
	}

	/**
	 * Instantiates a new window.
	 *
	 * @param {String} title - the title
	 * @param {boolean} resizable - the resizable
	 * @param {Vector} center - the center
	 * @param {Vector} size - the size
	 */
	public Window(String title, boolean resizable, Vector center, Vector size) {		
		// initializes the window
		this.setTitle(title);
		this.setIconImage(
				Toolkit.getDefaultToolkit().getImage("assets\\images\\gui\\icon.png")
		);

		this.setResizable(resizable);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		// sets the bounds of the window
		Vector bounds = center.subtract(size.scale(0.5d));
		this.setBounds((int) bounds.x, (int) bounds.y, (int) size.x, (int) size.y);
		
		// sets the content pane
		this.setContentPane(Game.getContentPane());
		
		// adds a window listener
		this.addWindowListener(new WindowAdapter() {
			// what should happen on close
			@Override
			public void windowClosing(WindowEvent e) {
				// stops running the game
				Game.running = false;
				
				// disconnects the client and server
				CClient.instance().disconnect();
				Server.instance().stop();
				
				// saves player preferences
				PlayerPrefs.save();
				
				// stops running the program
				System.exit(0);
			}
		});
	}
}
