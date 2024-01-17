package framework.physics;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import framework.components.Rigidbody;
import framework.math.Vector;

/**
 * The Class Edge.
 * 
 * @author priyangkar ghosh
 */
public class Edge {

	/** The normal. */
	public Vector normal = Vector.zero();

	/** The two vertices of this edge. */
	private Vertex a, b;

	/** The distance between the two vertices squared. */
	private double distSqr;

	/** The parent. */
	private Rigidbody parent;

	/**
	 * Instantiates a new edge.
	 *
	 * @param {Vertex} a - the a
	 * @param {Vertex} b - the b
	 * @param {Rigidbody} parent - the parent
	 */
	public Edge(Vertex a, Vertex b, Rigidbody parent) {
		// sets the point
		this.a = a; this.b = b;
		
		// calculates the distance squared
		this.distSqr = b.position.subtract(a.position).sqrMagnitude;
		
		// sets the parent
		this.parent = parent;
	}

	/**
	 * Renders this edge.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public void render(Graphics2D g2d) {
		// renders the vertices
		a.render(g2d);
		b.render(g2d);
		
		// renders the edge
		Line2D.Double l = new Line2D.Double();
		l.x1 = a.position.x;
		l.x2 = b.position.x;

		l.y1 = a.position.y;
		l.y2 = b.position.y;
		
		// renders the normal
		Vector half = (a.position.add(b.position)).scale(1d / 2);
		g2d.drawLine((int) half.x, (int) half.y, (int) (half.x + this.normal.x * 5),
				(int) (half.y + this.normal.y * 5));
		g2d.draw(l);
	}

	/**
	 * Updates this edge.
	 */
	public void update() {
		// calculates tangent
		Vector tangent = this.b.position.subtract(this.a.position);
		
		// sets normal from tangent
		this.normal = tangent.normal().normalized();
		
		// scales tangent to change the vertex positions
		tangent = tangent.scale(this.distSqr / (tangent.sqrMagnitude + this.distSqr) - 0.5);
		
		// changes vertex positions so the distance between them doesn't change
		this.a.position = this.a.position.subtract(tangent);
		this.b.position = this.b.position.add(tangent);
	}

	/**
	 * Gets the parent.
	 *
	 * @return {Rigidbody} the parent
	 */
	public Rigidbody getParent() {
		return this.parent;
	}

	/**
	 * Gets the vertex.
	 *
	 * @param {int} i - the i
	 * @return {Vertex} the vertex
	 */
	public Vertex getVertex(int i) {
		return (i == 0) ? a : b;
	}
}