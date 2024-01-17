/**
 * 
 */
package framework.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import framework.common.GameObject;
import framework.math.Vector;
import game.Game;

/**
 * The Class StarRenderer.
 * 
 * @author priyangkar ghosh
 */
public class StarRenderer extends Renderer {
	
	/** The Constant NUM_OF_STARS. */
	private static final int NUM_OF_STARS = 600;
	
	/** The Constant SCROLL_SPEED. */
	private static final int SCROLL_SPEED = 2;
	
	/** The instance. */
	private static StarRenderer instance;
	
	/** The offset. */
	private static double offset = 0;
	
	/**
	 * Gets the instance of StarRenderer.
	 *
	 * @return {StarRenderer} the instance of StarRenderer
	 */
	public static StarRenderer getInstance() {
		if (instance == null) instance = new StarRenderer();
		return instance;
	}
	
	/**
	 * Instantiate a new star renderer.
	 */
	public StarRenderer() {
		super(new GameObject(), -10);
		this.enabled = false;
	}

	/**
	 * Draw.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {Vector} center - the center
	 * @param {Vector} topLeft - the top left
	 * @param {double} rotation - the rotation
	 * @param {Vector} size - the size
	 */
	@Override
	public void draw(Graphics2D g2d, Vector center, Vector topLeft, double rotation, Vector size) {
		// creates a new random with arbitrary seed
		Random rng = new Random(3);
		
		for (int i = 0; i < NUM_OF_STARS; i++) {
			// sets the opacity randomly
			g2d.setColor(new Color(255, 255, 255, (int)(rng.nextDouble() * 200)));
			
			// calculates the star pos
			Vector starPos = new Vector((rng.nextDouble() * 1280 + offset) % 1280, rng.nextDouble() * 720);
			starPos.translate(Vector.random().scale(0.5));
			
			// draws the star
			int starSize = (int)((rng.nextDouble() + Math.random()) / 2 * 5);			
			g2d.fillOval((int)starPos.x, (int)starPos.y, starSize, starSize);
		}
		// scrolls the star map
		offset += Game.FIXED_TIMESTEP * SCROLL_SPEED;
	}
	
	/**
	 * Resets the star renderer.
	*/
	@Override
	public void close() { 
		super.close();
		instance = null;
	}
}
