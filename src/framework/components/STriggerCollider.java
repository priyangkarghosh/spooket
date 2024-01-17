package framework.components;

import java.util.ArrayList;

import framework.common.GameObject;
import framework.math.AABB;
import framework.math.Vector;
import networking.server.ServerManager;

/**
 * The Class STriggerCollider.
 *
 * @author priyangkar ghosh
 */
public class STriggerCollider extends Component {
	
	/** The aabb. */
	public AABB aabb = new AABB();
	
	/** The trigger. */
	private Rigidbody trigger;

	/**
	 * Instantiates a new server trigger collider.
	 *
	 * @param {GameObject} host - the host
	 * @param size {Vector} - the size
	 */
	public STriggerCollider(GameObject host, Vector size) {
		super(host, Component.Type.TRIGGER_COLLIDER);
		
		// sets the aabb for this collider
		Vector halfSize = size.scale(0.5d);
		this.aabb.set(
				new Vector(host.transform.position.x - halfSize.x, host.transform.position.x + halfSize.y),
				new Vector(host.transform.position.x + halfSize.x, host.transform.position.x - halfSize.y)
		);
	}

	/**
	 * Update.
	 */
	@Override
	public void update() {
		// sets the aabb to the correct position
		this.aabb.set(this.host.transform.position);
		
		// finds what it overlaps with
		ArrayList<AABB> overlaps = ServerManager.world.getTree().overlaps(this.aabb);
		
		// finds the first trigger / collision with a rigidbody
		this.trigger = null;
		for (AABB triggered : overlaps) {
			Rigidbody body = (Rigidbody) triggered.getComponent();
			if (body == null) continue;
			else if (body.collision(this.aabb)) {
				this.trigger = body;
				break;
			}
		}
	}

	/**
	 * Gets the trigger.
	 *
	 * @return the rigidbody
	 */
	public Rigidbody get() {
		return trigger;
	}

	/**
	 * Close.
	 */
	@Override
	public void close() {
		super.close();

		this.aabb = null;
		this.trigger = null;
	}
}
