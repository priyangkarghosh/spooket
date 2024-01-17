package framework.physics;

import java.awt.Graphics2D;

import framework.math.MathExt;
import framework.math.Vector;
import game.Game;

/**
 * The Class Vertex.
 * 
 * @author priyangkar ghosh
 */
public class Vertex {

	/** The previous position and current position. */
	public Vector position, previousPosition;

	/**
	 * Instantiates a new vertex.
	 *
	 * @param {double} x - the x
	 * @param {double} y - the y
	 */
	public Vertex(double x, double y) {
		this.position = previousPosition = new Vector(x, y);
	}

	/**
	 * Instantiates a new vertex.
	 *
	 * @param {Vector} position - the position
	 */
	public Vertex(Vector position) {
		this.position = previousPosition = new Vector(position);
	}

	/**
	 * Render.
	 *
	 * @param {Graphics2D} g2d the graphics object
	 */
	public void render(Graphics2D g2d) {
		g2d.fillOval((int) position.x, (int) position.y, 3, 3);
	}

	/**
	 * Updates the vertex using verlet integration.
	 *
	 * @param {Vector} force - the force
	 * @param {Material} mat - the mat
	 */
	public void update(Vector force, Material mat) {
		// makes sure the vertex can't go out of the screen
		this.position.x = MathExt.clamp(this.position.x, 0, 1260);
		this.position.y = MathExt.clamp(this.position.y, 0, 680);
		
		// gets this positions velocity
		Vector velocity = getVelocity();
		
		// dampens the velocity
		this.previousPosition = this.position.add(velocity.scale(mat.drag() * Game.FIXED_TIMESTEP));
		
		// calculates the new position
		this.position = this.position.add(velocity).add(force.scale(Game.FIXED_TIMESTEP * Game.FIXED_TIMESTEP));
	}

	/**
	 * Gets the velocity.
	 *
	 * @return {Vector} the velocity
	 */
	public Vector getVelocity() {
		return this.position.subtract(this.previousPosition);
	}
}
