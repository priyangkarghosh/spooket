/**
 *
 */
package framework.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Objects;

import framework.common.GameObject;
import framework.math.Vector;
import gameobjects.CPlayer;
import networking.client.ClientManager;

/**
 * The Scoreboard singleton.
 *
 * @author priyangkar ghosh
 */
public class Scoreboard extends Renderer {	
	/** The instance. */
	private static Scoreboard instance;
	
	/**
	 * Gets the instance of Scoreboard.
	 *
	 * @return {Scoreboard} the instance of Scoreboard
	 */
	public static Scoreboard getInstance() {
		if (instance == null) instance = new Scoreboard();
		return instance;
	}

	/**
	 * Instantiates a new scoreboard.
	 */
	private Scoreboard() {
		super(new GameObject(), 20);
		this.enabled = false;
	}

	/**
	 * Draws the scoreboard.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {Vector} center - the center
	 * @param {Vector} topLeft - the top left
	 * @param {double} rotation - the rotation
	 * @param {Vector} size - the size
	 */
	@Override
	public void draw(Graphics2D g2d, Vector center, Vector topLeft, double rotation, Vector size) {
		// removes all the null options
		CPlayer[] sortedPlayers = Arrays.stream(ClientManager.players).filter(Objects::nonNull).toArray(CPlayer[]::new);
		
		// sorts the players
		sortPlayers(sortedPlayers);
		
		// draws the background for the scoreboard
		g2d.setColor(new Color(217, 249, 242, 70));
		g2d.fillRoundRect(427, 120, 426, 480, 10, 10);
		
		// sets the current font
		g2d.setColor(Color.WHITE);
		Font currentFont = g2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.7f);
		g2d.setFont(newFont);
		
		// draws the player scores and ranks
		String format = "%d: %s (%d)";
		Vector current = new Vector(450, 200);
		for (int i = 0; i < sortedPlayers.length; i++) {
			CPlayer player = sortedPlayers[i];
			
			// draws the string
			g2d.drawString(
					String.format(format, i + 1, player.getName(), player.getKills()),
					(int) current.x, (int) current.y
			);
			current.translate(new Vector(0, 50));
		}
		
		// sets the title font
		currentFont = g2d.getFont();
		newFont = currentFont.deriveFont(currentFont.getSize() * 1.6f);
		g2d.setFont(newFont);
		
		// draws the title
		g2d.drawString("SCOREBOARD", 523, 169);
	}

	/**
	 * Sort the players using insertion sort.
	 *
	 * @param {CPlayer[]} players - the players
	 */
	public void sortPlayers(CPlayer[] players) {
		int n = players.length;
		for (int i = 1; i < n; i++) {
			int j = i - 1;
			CPlayer key = players[i];
			
			// sorts by the kills
			while (j >= 0 && players[j].getKills() < key.getKills()) {
				players[j + 1] = players[j];
				j--;
			}
			players[j + 1] = key;
		}
	}
	
	/**
	 * Resets the scoreboard.
	*/
	@Override
	public void close() { 
		super.close();
		instance = null;
	}
}
