package framework.physics;

import framework.components.Rigidbody;
import framework.math.Vector;

/**
 * The Class Manifold, is a data storage class.
 * 
 * @author priyangkar ghosh
 */
public class Manifold {

	/** The edge. */
	public Edge edge = null;

	/** The vertex. */
	public Vertex vertex = null;

	/** The normal. */
	public Vector normal = null;

	/** The overlap. */
	public double overlap = Double.MAX_VALUE;

	/** The colliding. */
	public boolean colliding = false;

	/** The two rigidbodies. */
	public Rigidbody rb1, rb2;

	/**
	 * Instantiates a new manifold.
	 */
	public Manifold() { }
}
