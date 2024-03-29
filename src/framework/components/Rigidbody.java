package framework.components;

import java.awt.Color;
import java.awt.Graphics2D;

import framework.common.GameObject;
import framework.common.Transform;
import framework.math.AABB;
import framework.math.MinMax;
import framework.math.Vector;
import framework.physics.Edge;
import framework.physics.Manifold;
import framework.physics.Material;
import framework.physics.Physics.ForceMode;
import framework.physics.Physics.RigidbodyMode;
import framework.physics.Vertex;

/**
 * The Class Rigidbody.
 * 
 * @author priyangkar ghosh
 */
public class Rigidbody extends Component {
	/** The vertices. */
	private Vertex[] vertices;

	/** The edges. */
	private Edge[] edges;

	/** The anchor. */
	private Vector anchor;

	/** The inverse mass (1 / mass). */
	private double inv_mass;

	/** The physics material. */
	private Material mat;

	/** The forces at this timestep. */
	private Vector forces = Vector.zero();

	/** The aabb of the body. */
	private AABB aabb = new AABB(this);

	/** The mode. (dynamic, kinematic, static) */
	public RigidbodyMode mode;

	/**
	 * Instantiates a new rigidbody.
	 *
	 * @param {GameObject} host - the host
	 * @param {RigidbodyMode} mode - the mode
	 * @param {double} mass - the mass
	 * @param {Vertex[]} vertices - the vertices
	 * @param {Material} mat - the mat
	 */
	public Rigidbody(GameObject host, RigidbodyMode mode, double mass, Vertex[] vertices, Material mat) {
		super(host, Component.Type.RIGIDBODY);
		
		// initializes the variables
		this.mat = mat;
		this.mode = mode;
		this.vertices = vertices;

		this.updateVertices();

		// calculates all the edges
		int l = this.vertices.length;
		this.edges = new Edge[(l * l - l) / 2];
		for (int i = 0; i < this.edges.length; i++) {
			this.edges[i] = new Edge(this.vertices[i % l], this.vertices[(i + ((i < l) ? 1 : (l / 2))) % l], this);
		}

		this.updateEdges();

		this.inv_mass = 1 / mass;
		this.anchor = vertices[0].position.subtract(
				this.host.transform.position
		);
	}

	/**
	 * Update.
	 */
	@Override
	public void update() {
		// updates the rigidbody		
		this.aabb.reset();
		this.updateVertices();
		this.updateEdges();
		this.forces = Vector.zero();
	}

	/**
	 * Update the edges and transform.
	 */
	public void updateEdges() {
		Vector midpoint = Vector.zero();
		for (int i = 0; i < this.edges.length; i++) {
			this.edges[i].update();

			if (i < this.vertices.length) {
				Vector v = this.edges[i].getVertex(0).position;
				midpoint = midpoint.add(v);
				aabb.update(v);
			}
		}
		midpoint = midpoint.scale(1d / this.vertices.length);
		updateTransform(midpoint);
	}

	/**
	 * Updates transform.
	 *
	 * @param {Vector} midpoint - the midpoint
	 */
	private void updateTransform(Vector midpoint) {
		this.host.transform.position = midpoint;

		if (this.anchor != null) {
			// calculates rotation
			double rotation =
					this.vertices[0].position.subtract(midpoint).completeAngle(this.anchor);
			this.host.transform.rotation = rotation;
		}
	}

	/**
	 * Updates vertices.
	 */
	private void updateVertices() {
		for (Vertex v : this.vertices)
			v.update(this.forces, this.mat);
	}

	/**
	 * Checks for collision with another Rigidbody.
	 *
	 * @param {Rigidbody} other - the other
	 * @return {Manifold} the manifold generated by the method
	 */
	public Manifold collision(Rigidbody other) {
		// two variables which point to the two bodies involved
		Rigidbody refA = this;
		Rigidbody refB = other;

		// creates a new manifold to return
		Manifold m = new Manifold();

		// loops through the exterior edges
		for (int i = 0; i < refA.vertices.length + refB.vertices.length; i++) {
			// finds the index of the edge, regardless of rigidbody
			int k = (i >= refA.vertices.length) ? i - refA.vertices.length : i;

			// finds corresponding edge which acts as an axis
			Vector axis = (k == i) ? refA.edges[k].normal : refB.edges[k].normal;

			// stores the min and max projections for each vertex
			MinMax scalarA = new MinMax(), scalarB = new MinMax();

			// loops through each vertex and projects it onto the axis
			// updates the scalar with the projected vector
			for (int j = 0; j < Math.max(refA.vertices.length, refB.vertices.length); j++) {
				if (j < refA.vertices.length)
					scalarA.update(axis.dot(refA.vertices[j].position.project(axis)));
				if (j < refB.vertices.length)
					scalarB.update(axis.dot(refB.vertices[j].position.project(axis)));
			}

			// calculates the amount the two rigidbodies overlap on this axis
			double overlap = Math.min(scalarA.getMax(), scalarB.getMax());
			overlap -= Math.max(scalarA.getMin(), scalarB.getMin());

			// if there is no overlap, stop checking
			if (overlap < 0) return m;

			// if this is the smallest overlap found so far, update the manifold
			if (overlap < m.overlap) {
				m.normal = axis; m.overlap = overlap;
				m.edge = (k == i) ? this.edges[k] : other.edges[k];
			}
		}

		// makes sure the parent of the edge is refB
		// otherwise swaps the two pointers
		if (m.edge.getParent() != refB) {
			refA = other; refB = this;
		}

		m.rb1 = refA;
		m.rb2 = refB;

		// negates the normal if it doesnt point from a to b
		if (refA.host.transform.position.subtract(
				refB.host.transform.position).dot(m.normal) < 0)
			m.normal.negate();

		// finds the vertex closest to the second rigidbody
		Double min = Double.MAX_VALUE;
		for (Vertex v : refA.vertices) {
			double distance = v.position.subtract(refB.host.transform.position).dot(m.normal);
			if ((min = Math.min(min, distance)) == distance) m.vertex = v;
		}

		// if the method makes it this far in the code,
		// the two rigidbodies are colliding
		m.colliding = true;
		return m;
	}

	/**
	 * Checks for collision with an aabb.
	 *
	 * @param {AABB} other - the other
	 * @return {boolean} if the two are colliding
	 */
	public boolean collision(AABB other) {
		Vector[] aabbVertices = new Vector[] {
				other.topLeft(), other.topRight(),
				other.bottomRight(), other.bottomLeft()
		};

		// loops through the exterior edges
		for (int i = 0; i < this.vertices.length + 2; i++) {
			// finds the index of the edge
			int k = (i >= 2) ? i - 2 : i;

			// finds corresponding edge which acts as an axis
			Vector axis = (k == i) ? AABB.normals[k] : this.edges[k].normal;

			// stores the min and max projections for each vertex
			MinMax scalarA = new MinMax(), scalarB = new MinMax();

			// loops through each vertex and projects it onto the axis
			// updates the scalar with the projected vector
			for (int j = 0; j < Math.max(this.vertices.length, 4); j++) {
				if (j < 4)
					scalarA.update(axis.dot(aabbVertices[j].project(axis)));
				if (j < this.vertices.length)
					scalarB.update(axis.dot(this.vertices[j].position.project(axis)));
			}

			// calculates the amount the two rigidbodies overlap on this axis
			double overlap = Math.min(scalarA.getMax(), scalarB.getMax());
			overlap -= Math.max(scalarA.getMin(), scalarB.getMin());

			// if there is no overlap, stop checking
			if (overlap < 0) return false;
		}

		return true;
	}

	/**
	 * Adds the force to the rigidbody.
	 *
	 * @param {Vector} force - the force
	 * @param {ForceMonde} forceMode - the force mode
	 */
	public void addForce(Vector force, ForceMode forceMode) {
		if (this.mode.isStatic()) return;
		
		switch (forceMode) {
			case FORCE:
				// adds to the forces on the object
				this.forces = this.forces.add(force);
				break;

			case IMPULSE:
				// adds the force to the velocity
				for (Vertex v : this.vertices)
					v.previousPosition = v.previousPosition.add(
							v.getVelocity().add(force)
					);
				break;

			case SET:
				// sets the velocity
				for (Vertex v : this.vertices)
					v.previousPosition = v.position.subtract(force);
				break;

			default:
				break;
		}
	}

	/**
	 * Translate the rigidbody.
	 *
	 * @param {Vector} translation - the translation
	 * @param {boolean} conserveVelocity - the conserve velocity
	 */
	public void translate(Vector translation, boolean conserveVelocity) {
		for (Vertex v : this.vertices) {
			v.position.translate(translation);
			// changes previous position too if the velocity should be conserved
			if (conserveVelocity) v.previousPosition.translate(translation);
		}
		
		// updates the game object position
		this.host.transform.position.translate(translation);
	}

	/**
	 * Rotate the rigidbody.
	 *
	 * @param {double} angle - the angle
	 * @param {boolean} conserveVelocity - the conserve velocity
	 */
	public void rotate(double angle, boolean conserveVelocity) {
		Vector origin = this.host.transform.position;

		for (Vertex v : this.vertices) {
			// store temporary position
			Vector temp = v.position;

			// rotates the vertex
			v.position = Transform.rotate(v.position, origin, angle);

			// conserves the angular velocity
			if (conserveVelocity)
				v.previousPosition.translate(v.position.subtract(temp));
		}
	}

	/**
	 * Gets the aabb.
	 *
	 * @return {AABB} the aabb
	 */
	public AABB getAABB() {
		return aabb;
	}

	/**
	 * Gets the inverse mass.
	 *
	 * @return {double} the inverse mass
	 */
	public double getInverseMass() {
		return inv_mass;
	}

	/**
	 * Gets the mass.
	 *
	 * @return {double} the mass
	 */
	public double getMass() {
		return 1 / inv_mass;
	}

	/**
	 * Gets the material.
	 *
	 * @return {Material} the material
	 */
	public Material getMaterial() {
		return this.mat;
	}

	/**
	 * Renders the rigidbody.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public void render(Graphics2D g2d) {
		g2d.setColor(Color.white);
		for (Edge e : this.edges) e.render(g2d);
	}
}
