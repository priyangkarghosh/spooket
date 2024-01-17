package framework.math;

import java.awt.Graphics2D;

import framework.components.Component;

/**
 * The AABB class (axis aligned bounding box).
 * 
 * @author priyangkar ghosh
 */
public class AABB {	
	/** The normals. */
	public static Vector[] normals = new Vector[] { Vector.up(), Vector.right() };

	/**  Stores the min and max x and y values. */
	private MinMax x, y;

	/** Stores the parent Rigidbody, if it has one. */
	private Component host;

	/**
	 * Instantiates a new aabb.
	 */
	public AABB() {
		this.x = new MinMax();
		this.y = new MinMax();
	}

	/**
	 * Instantiates a new aabb from a pre-existing one.
	 *
	 * @param {AABB} aabb - the aabb
	 */
	public AABB(AABB aabb) {
		this.x = new MinMax(aabb.x);
		this.y = new MinMax(aabb.y);
	}

	/**
	 * Instantiates a new aabb from a position and size.
	 *
	 * @param {Vector} pos - the pos
	 * @param {Vector} size - the size
	 */
	public AABB(Vector pos, Vector size) {
		this.x = new MinMax(pos.x, pos.x + size.x);
		this.y = new MinMax(pos.y, pos.y + size.y);
	}

	/**
	 * Instantiates a new aabb with a corresponding body.
	 *
	 * @param {Component} component - the component
	 */
	public AABB(Component component) {
		this.x = new MinMax();
		this.y = new MinMax();
		this.host = component;
	}

	/**
	 * Update the min and max values with a vertex;.
	 *
	 * @param {Vector} vertex - the vertex
	 */
	public void update(Vector vertex) {
		this.x.update(vertex.x);
		this.y.update(vertex.y);
	}

	/**
	 * Sets the min max x and y values through the center vertex.
	 *
	 * @param {Vector} center - the center
	 */
	public void set(Vector center) {
		Vector size = bottomRight().subtract(topLeft()).scale(1/2d);
		x.set(center.x - size.x, center.x + size.x);
		y.set(center.y - size.y, center.y + size.y);
	}

	/**
	 * Sets the min and max x and y through lower and upper vectors.
	 *
	 * @param {Vector} lower - the lower
	 * @param {Vector} upper - the upper
	 */
	public void set(Vector lower, Vector upper) {
		this.x.set(lower.x, upper.x);
		this.y.set(upper.y, lower.y);
	}

	/**
	 * Resets the mins and maxes.
	 */
	public void reset() {
		this.x.reset(); this.y.reset();
	}

	/**
	 * Checks if two aabbs overlap.
	 *
	 * @param {AABB} other - the other
	 * @return {boolean} true, if the two aabbs overlap
	 */
	public boolean overlap(AABB other) {
		return (this.x.getMin() < other.x.getMax()) &&
			   (other.x.getMin() < this.x.getMax()) &&
			   (this.y.getMin() < other.y.getMax()) &&
			   (other.y.getMin() < this.y.getMax());
	}

	/**
	 * If this aabb contains another aabb.
	 *
	 * @param {AABB} other - the other
	 * @param {Vector} fat - the fat
	 * @return true, if this aabb contains the other
	 */
	public boolean contains(AABB other, Vector fat) {
		return (this.x.getMin() - fat.x <= other.x.getMin()) &&
			   (this.x.getMax() + fat.x >= other.x.getMax()) &&
		       (this.y.getMin() - fat.y <= other.y.getMin()) &&
			   (this.y.getMax() + fat.y >= other.y.getMax());
	}

	/**
	 * Area of the aabb.
	 *
	 * @return {double} area of the aabb
	 */
	public double area() {
		Vector d = bottomRight().subtract(topLeft());
		return 2 * d.x * d.y;
	}

	/**
	 * Finds the union of two AABB's.
	 *
	 * @param {AABB} other - the other
	 * @return {AABB} the merged aabb
	 */
	public AABB union(AABB other) {
		// creates a new aabb
		AABB c = new AABB();

		// calculates the lower vector, which is the min x, and max y
		// of the two aabbs
		Vector lower = new Vector(Math.min(this.x.getMin(), other.x.getMin()),
				Math.max(this.y.getMax(), other.y.getMax()));

		// calculates the upper vector, which is the max x, and min y
		// of the two aabbs
		Vector upper = new Vector(Math.max(this.x.getMax(), other.x.getMax()),
				Math.min(this.y.getMin(), other.y.getMin()));

		// sets the bounds of the instantiated aabb
		c.set(lower, upper);
		return c;
	}

	/**
	 * Bottom left.
	 *
	 * @return {Vector} returns the bottom left vector of the aabb
	 */
	public Vector bottomLeft() {
		return new Vector(this.x.getMin(), this.y.getMax());
	}

	/**
	 * Bottom right.
	 *
	 * @return {Vector} returns the bottom right vector of the aabb
	 */
	public Vector bottomRight() {
		return new Vector(this.x.getMax(), this.y.getMax());
	}

	/**
	 * Top left.
	 *
	 * @return {Vector} returns the top left vector of the aabb
	 */
	public Vector topLeft() {
		return new Vector(this.x.getMin(), this.y.getMin());
	}

	/**
	 * Top right.
	 *
	 * @return {Vector} returns the top right vector of the aabb
	 */
	public Vector topRight() {
		return new Vector(this.x.getMax(), this.y.getMin());
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public Component getComponent() {
		return this.host;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "{ " + bottomLeft() + ", " + topRight() + " }";
	}

	/**
	 * Render.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public void render(Graphics2D g2d) {
		Vector tl = this.topLeft();
		Vector size = tl.subtract(this.bottomRight()).abs();
		g2d.fillRect((int) tl.x, (int) tl.y, (int) size.x, (int) size.y);
	}
}
