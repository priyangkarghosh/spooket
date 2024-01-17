package framework.components;

import java.awt.Color;
import java.awt.Graphics2D;

import framework.common.GameObject;
import framework.math.Vector;

/**
 * The Class HealthBarRenderer, used to render health bars.
 *
 * @author priyangkar ghosh
 */
public class HealthBarRenderer extends Renderer {
	
	/** The Constant OFFSET. */
	public static final Vector OFFSET = new Vector(0, -36);
	
	/** The Constant BAR_SIZE. */
	public static final Vector BAR_SIZE = new Vector(56, 8);

	/** The Constant OUTLINE_THICKNESS. */
	public static final Vector OUTLINE_THICKNESS = new Vector(3, 3);
	
	/** The Constant NAME_OFFSET. */
	public static final Vector NAME_OFFSET = OUTLINE_THICKNESS.add(new Vector(0, 5)).negated();

	/** The name of this player. */
	private String name;
	
	/** The current and max health. */
	private double health, maxHealth;

	/**
	 * Instantiates a new health bar renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {String} name - the name
	 * @param {double} maxHealth - the max health
	 * @param {int} depth - the depth
	 */
	public HealthBarRenderer(GameObject host, String name, double maxHealth, int depth) {
		super(host, BAR_SIZE, OFFSET, depth);
		
		// sets the name and max health
		this.name = name;
		this.maxHealth = maxHealth;
	}

	/**
	 * Draws the health bar.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {Vector} center - the center
	 * @param {Vector} topLeft - the top left
	 * @param {double} rotation - the rotation
	 * @param {Vector} size - the size
	 */
	@Override
	public void draw(Graphics2D g2d, Vector center, Vector topLeft, double rotation, Vector size) {
		// draws the outline for the health bar
		g2d.setColor(Color.WHITE); 
		Vector outlinePos = topLeft.subtract(OUTLINE_THICKNESS);
		Vector outlineSize = size.add(OUTLINE_THICKNESS.scale(2));
		g2d.drawRoundRect((int)outlinePos.x, (int)outlinePos.y, (int)outlineSize.x, (int)outlineSize.y, 5, 5);
		
		// draws the current health
		g2d.setColor(Color.GREEN);
		double percentageHealth = health / maxHealth;
		g2d.fillRoundRect((int)topLeft.x, (int)topLeft.y, (int)(size.x * percentageHealth), (int)size.y, 5, 5);
		
		// writes the username
		g2d.setColor(Color.WHITE);
		Vector namePos = topLeft.add(NAME_OFFSET);
		g2d.drawString(this.name, (int)namePos.x, (int)namePos.y);
	}

	/**
	 * Sets the current health.
	 *
	 * @param {double} health - the new health
	 */
	public void setHealth(double health) {
		this.health = health;
	}
}
