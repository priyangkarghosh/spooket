package framework.physics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import adt.AABBTree;
import framework.components.Component;
import framework.components.Rigidbody;
import framework.math.AABB;
import framework.math.Vector;
import game.Game;

/**
 * The Class Physics.
 * 
 * @author priyangkar ghosh
 */
public class Physics implements Runnable {

	/** The Constant COLLISION_ITERATIONS. */
	/** determines how many times to iterate through the collision checker */
	private static final int COLLISION_ITERATIONS = 5;

	/** The gravity of this world. */
	private Vector gravity = new Vector(0, 9.81).scale(Game.METER_TO_PIXEL);

	/** The rigidbodies. */
	private ArrayList<Rigidbody> rigidbodies = new ArrayList<>();

	/** The aabb tree. */
	private AABBTree aabbTree = new AABBTree(new Vector(2, 2));

	/** The steps. */
	private int steps = 0;

	/** If this world is active. */
	private volatile boolean active;

	/**
	 * Instantiates a new physics.
	 */
	public Physics() { }

	/**
	 * Instantiates a new physics with specified gravity.
	 *
	 * @param {Vector} gravity - the gravity
	 */
	public Physics(Vector gravity) {
		this.gravity = gravity.scale(Game.METER_TO_PIXEL);
	}

	/**
	 * Initializes the world.
	 */
	public void init() {
		new Thread(this).start();
	}

	/**
	 * Advances the physics world by one step.
	 */
	public void step() {
		this.steps++;
	}

	/**
	 * Physics loop.
	 */
	@Override
	public void run() {
		this.active = true;
		
		// runs as long as the world is active
		while (this.active) {
			// makes sure it doesn't run continuously
			Thread.yield();
			try { Thread.sleep(1); }
			catch (Exception e) {  }
			
			// if the world should update
			while (this.steps > 0) {
				try {
					// loops through each rigidbody and adds gravity if the body is dynamic
					for (Rigidbody rb : this.rigidbodies) {
						if (rb.mode.isDynamic())
							rb.addForce(this.gravity, ForceMode.FORCE);
					}
					
					// updates the aabb tree
					this.aabbTree.updateTree();
					
					// loops through each body
					for (Rigidbody rb : this.rigidbodies) {
						// if its static it doesn't do any collision checking
						if (rb.mode.isStatic()) continue;
						
						// broadphase check for what this aabb overlaps with
						ArrayList<AABB> overlaps = this.aabbTree.overlaps(rb.getAABB());
						
						// resolves collision x times
						for (int i = 0; i < Physics.COLLISION_ITERATIONS; i++)
							for (AABB other : overlaps) {
								// checks to see if the parent of this aabb is a component
								Component comp = other.getComponent();
								
								// resolves collision if it's an instance of a rigidbody
								if (comp instanceof Rigidbody)
									Physics.processCollision(
											rb.collision((Rigidbody)comp)
									);
							}
					}
					
					// decrements the steps
					this.steps--;
				}

				catch (ConcurrentModificationException e) {
					// if a rigidbody is added at the same time as iteration
				}
			}
		}
	}

	/**
	 * Process a collision.
	 *
	 * @param {Manifold} m - the m
	 */
	private static void processCollision(Manifold m) {
		if (!m.colliding) return;

		Vector mtv = m.normal.scale(m.overlap);

		Vertex edgeA = m.edge.getVertex(0);
		Vertex edgeB = m.edge.getVertex(1);

		double t;
		Vector dist = edgeB.position.subtract(edgeA.position);
		if (Math.abs(dist.x) > Math.abs(dist.y))
			t = Math.abs((m.vertex.position.x - mtv.x - edgeA.position.x) / dist.x);
		else
			t = Math.abs((m.vertex.position.y - mtv.y - edgeA.position.y) / dist.y);

		double lambda = 1 / (t * t + (1 - t) * (1 - t));

		switch (m.rb1.mode) {
			case DYNAMIC:
				switch (m.rb2.mode) {
					case DYNAMIC:
						m.vertex.position.translate(mtv.scale(0.5));

						edgeA.position.translate(mtv.scale(t * lambda * -0.5));
						edgeB.position.translate(mtv.scale((1 - t) * lambda * -0.5));
						break;

					case STATIC:
					case KINEMATIC:
						m.rb1.translate(mtv, false);
						break;
				}
				break;

			case KINEMATIC:
				switch (m.rb2.mode) {
					case KINEMATIC:
						m.rb1.translate(mtv.scale(0.5), false);
						m.rb2.translate(mtv.scale(-0.5), false);
						break;

					case DYNAMIC:
						edgeA.position.translate(mtv.scale(t * lambda * -1));
						edgeB.position.translate(mtv.scale((1 - t) * lambda * -1));
						break;

					case STATIC:
						m.rb1.translate(mtv.scale(1), false);
						break;
				}
				break;

			case STATIC:
				switch (m.rb2.mode) {
					case DYNAMIC:
					case KINEMATIC:
						m.rb2.translate(mtv.scale(-1), false);
						break;
					case STATIC:
						break;
				}
				break;
		}

		m.rb1.updateEdges();
		m.rb2.updateEdges();
	}

	/**
	 * Renders the physics world.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public void render(Graphics2D g2d) {
		try {
			// renders every rigidbody
			for (Rigidbody b : this.rigidbodies) b.render(g2d);
			
			// renders the aabb tree
			this.aabbTree.render(g2d);
		}
		
		// if a rigidbody is added at the same time as rendering
		catch (ConcurrentModificationException e) { }
	}

	/**
	 * Adds the body.
	 *
	 * @param {Rigidbody} body - the body
	 */
	public void addBody(Rigidbody body) {
		// adds the body and inserts it to the tree
		this.rigidbodies.add(body);
		this.aabbTree.insertLeaf(body.getAABB());
	}

	/**
	 * Removes the body.
	 *
	 * @param {Rigidbody} body - the body
	 */
	public void removeBody(Rigidbody body) {
		// removes from tree and list
		this.rigidbodies.remove(body);
		this.aabbTree.removeLeaf(body.getAABB());
	}

	/**
	 * Gets the tree.
	 *
	 * @return {AABBTree} the tree
	 */
	public AABBTree getTree() {
		return this.aabbTree;
	}

	/**
	 * Gets the bodies.
	 *
	 * @return {ArrayList<Rigidbody>} the bodies
	 */
	public ArrayList<Rigidbody> getBodies() {
		return this.rigidbodies;
	}

	/**
	 * Gets the gravity vector.
	 *
	 * @return {Vector} the gravity vector
	 */
	public Vector getGravityVector() {
		return this.gravity;
	}

	/**
	 * The Enum RigidbodyMode.
	 */
	public enum RigidbodyMode {

		/** The dynamic. */
		// affected by all forces and collisions
		DYNAMIC,

		/** The kinematic. */
		// controlled by script
		KINEMATIC,

		/** The static. */
		// can't move
		STATIC;

		/**
		 * Checks if is dynamic.
		 *
		 * @return {boolean} true, if is dynamic
		 */
		public boolean isDynamic() {
			return this == DYNAMIC;
		}

		/**
		 * Checks if is kinematic.
		 *
		 * @return {boolean} true, if is kinematic
		 */
		public boolean isKinematic() {
			return this == KINEMATIC;
		}

		/**
		 * Checks if is static.
		 *
		 * @return {boolean} true, if is static
		 */
		public boolean isStatic() {
			return this == STATIC;
		}
	}

	/**
	 * The Enum ForceMode.
	 */
	public enum ForceMode {

		/** The force. */
		FORCE,

		/** The impulse. */
		IMPULSE,

		/** The set. */
		SET
	}

	/**
	 * Clear.
	 */
	public void clear() {
		// stops the thread from running
		this.active = false;
		
		// closes each body in the world
		for (Rigidbody body : this.rigidbodies) body.close();
		
		// clears the tree and bodies
		this.rigidbodies.clear();
		this.aabbTree.clear();
	}
}
