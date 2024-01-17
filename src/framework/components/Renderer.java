package framework.components;

import java.awt.Graphics2D;

import adt.Tree;
import adt.TreeNode;
import framework.common.GameObject;
import framework.math.MathExt;
import framework.math.Vector;
import game.Game;

/**
 * The Class Renderer.
 * Used for rendering objects in a specific order using BST and depth values
 *
 * @author priyangkar ghosh
 */
public abstract class Renderer extends Component implements Comparable<Renderer> {
	
	/** The Constant TELEPORT_MAGNITUDE. */
	private static final double TELEPORT_MAGNITUDE = 225;
	
	/** The Constant CAMERA_LERP_CONSTANT. */
	private static final double CAMERA_LERP_CONSTANT = 0.05;
	
	/** The target and current offset. */
	private static Vector targetOffset = Vector.zero();	
	private static Vector currentOffset = Vector.zero();
	
	/** The render tree. */
	public static Tree<Renderer> tree = new Tree<>();

	/** The render position and rotation. */
	protected Vector renderPosition;		
	protected double renderRotation;

	/** stores the two used sizes of what is rendered. */
	protected Vector size, halfSize;

	/**  offset amount, if any. */
	protected Vector offset;

	/**  if it should be rendered. */
	public boolean enabled = true;

	/** The depth.  */
	protected int depth;

	/**
	 * Instantiates a new renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {int} depth - the depth
	 */
	public Renderer(GameObject host, int depth) {
		super(host, Component.Type.RENDERER);

		// initializes position and rotation
		this.renderPosition = host.transform.position;
		this.renderRotation = host.transform.rotation;

		// initializes depth, offset and size
		this.setSize(Vector.zero());
		this.setOffset(Vector.zero());
		this.depth = depth;

		// inserts the renderer to the render tree
		if (host != Game.getPrefabObject())
			tree.insert(this);
	}

	/**
	 * Instantiates a new renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {Vector} size - the size
	 * @param {Vector} offset - the offset
	 * @param {int} depth - the depth
	 */
	public Renderer(GameObject host, Vector size, Vector offset, int depth) {
		super(host, Component.Type.RENDERER);

		// initializes position and rotation
		this.renderPosition = host.transform.position;
		this.renderRotation = host.transform.rotation;

		// initializes depth, offset and size
		this.setSize(size);
		this.setOffset(offset);
		this.depth = depth;

		// inserts the renderer to the render tree if it isn't a prefab
		if (host != Game.getPrefabObject())
			tree.insert(this);
	}

	/**
	 * Sets the size and half size.
	 *
	 * @param {Vector} size - the new size
	 */
	public void setSize(Vector size) {
		this.size = size;
		this.halfSize = size.scale(0.5d);
	}

	/**
	 * Updates the renderer.
	 */
	@Override
	public void update() {
		if (!tree.contains(this)) tree.insert(this);
	}

	/**
	 * Abstract draw function.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {Vector} center - the center
	 * @param {Vector} topLeft - the top left
	 * @param {double} rotation - the rotation
	 * @param {Vector} size - the size
	 */
	public abstract void draw(Graphics2D g2d, Vector center, Vector topLeft, double rotation, Vector size);

	/**
	 * Resets the render tree.
	 */
	public static void reset() {
		tree.clear();
	}

	/**
	 * Renders all the renderers objects in the tree.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public static void render(Graphics2D g2d) {
		currentOffset = MathExt.lerp(currentOffset, targetOffset, CAMERA_LERP_CONSTANT);
		render(g2d, tree.getRoot());
	}

	/**
	 * Render the tree using in-order traversal.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {TreeNode<Renderer>} node - the node
	 */
	private static void render(Graphics2D g2d, TreeNode<Renderer> node) {
		// the node is null, return
		if (node == null) return;

		// renders the left subtree
		render(g2d, node.left);

		// renders the current node
		Renderer sprite = node.value;
		GameObject host = sprite.host;

		if (sprite != null && sprite.enabled && host != null && host.transform != null) {
			// center of the sprite, and calculates the render position using
			// linear interpolation
			Vector center = sprite.renderPosition =
					MathExt.lerp(sprite.renderPosition, host.transform.position, Game.LERP_CONSTANT);
			if (host.transform.position.subtract(center).sqrMagnitude > TELEPORT_MAGNITUDE)
				center = sprite.renderPosition = host.transform.position;
			center = center.add(sprite.offset).add(Renderer.currentOffset);

			// rotation of the sprite, and calculates the render rotation using
			// linear interpolation
			double rotation = sprite.renderRotation =
					MathExt.lerp(sprite.renderRotation, host.transform.rotation, Game.LERP_CONSTANT);
			if (host.transform.rotation - rotation > 0.4)
				rotation = sprite.renderRotation = host.transform.rotation;

			Vector size = sprite.size.scale(host.transform.scale);
			Vector halfSize = sprite.halfSize.scale(host.transform.scale);

			// calculates the top left position
			Vector topLeft = center.subtract(halfSize);

		    // draws the renderer object to the screen
		    sprite.draw(g2d, center, topLeft, rotation, size);
		}

	    // render the right subtree
		render(g2d, node.right);
	}

	/**
	 * Compare two renderers using depth.
	 *
	 * @param {Renderer} other - the other
	 * @return {int} corresponding int from -1, 0, 1
	 */
	@Override
	public int compareTo(Renderer other) {
		if (this.depth < other.depth) return -1;
		if (this.depth > other.depth) return 1;
		return 0;
	}

	/**
	 * Close.
	 */
	@Override
	public void close() {
		super.close();
		this.renderPosition = this.size = this.halfSize = null;
		tree.remove(this);
	}

	/**
	 * Sets the offset.
	 *
	 * @param {Vector} offset - the new offset
	 */
	public void setOffset(Vector offset) {
		this.offset = offset;
	}

	/**
	 * Sets the target offset.
	 *
	 * @param {Vector} targetOffset - the new target offset
	 */
	public static void setTargetOffset(Vector targetOffset) {
		Renderer.targetOffset = targetOffset;
	}
}
