package framework.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import adt.Node;
import adt.Queue;
import framework.common.GameObject;
import framework.math.Vector;

/**
 * The Class TrailRenderer.
 *
 * @author priyangkar ghosh
 */
public class TrailRenderer extends Renderer {
	
	/** The trail parameters. */
	private int particleSize, length, offsetMagnitude;
	private Vector halfParticleSize = Vector.zero();
	
	/** The trail. */
	private Queue<Vector> trail = new Queue<Vector>();
	
	/**
	 * Instantiates a new trail renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {int} size - the size
	 * @param {int} length - the length
	 * @param {int} offset - the offset
	 * @param {int} depth - the depth
	 */
	public TrailRenderer(GameObject host, int size, int length, int offset, int depth) {
		super(host, depth);
		
		// initializes the variables
		this.particleSize = size;
		this.halfParticleSize = new Vector(size / 2, size / 2);
		
		this.length = length;
		this.offsetMagnitude = offset;
	}
	
	/**
	 * Update.
	 */
	@Override
	public void update() {
		// sets this trails offset
		this.setOffset(host.transform.right().scale(this.offsetMagnitude));
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
		// queues the trail
		this.trail.queue(
				this.renderPosition.add(this.offset).subtract(this.halfParticleSize)
		); if (this.trail.size() > this.length) this.trail.dequeue();
		
		// if the trail has no positions, don't draw
		Node<Vector> temp = trail.front;
		if (temp == null) return;
		
		// loop through the queue and draw an oval at each position/node
		for (double i = 0; i < 1 && temp.link != null; i += (1d / trail.size())) {
			// make the trail less transparent as it gets to more recent positions
			g2d.setColor(new Color(255, 255, 255, (int)(i * 255)));
			
			// draw an oval at the position
			g2d.fillOval((int)temp.value.x, (int)temp.value.y, this.particleSize, this.particleSize);
			
			// move on to next position
			temp = temp.link;
		}
	}
	
	/**
	 * Close.
	 */
	@Override
	public void close() {
		this.trail.clear();
		super.close();
	}

	/**
	 * Hash code.
	 *
	 * @return {int} the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(length, trail);
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return {boolean} true, if the two are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TrailRenderer))
			return false;
		TrailRenderer other = (TrailRenderer) obj;
		return this.length == other.length && Objects.equals(this.trail, other.trail);
	}
}
