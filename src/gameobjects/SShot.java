package gameobjects;

import java.util.UUID;

import framework.common.GameObject;
import framework.components.Rigidbody;
import framework.components.STriggerCollider;
import framework.math.Vector;
import framework.physics.Physics.ForceMode;
import game.Game;

/**
 * The Class SShot (server-side shot).
 *
 * @author priyangkar ghosh
 */
public class SShot extends GameObject {
	
	/** The Constant LIFESPAN. */
	private static final double LIFESPAN = 3;
	
	/** The Constant SPAWN_OFFSET. */
	private static final double SPAWN_OFFSET = 30;
	
	/** The Constant DAMAGE. */
	private static final int DAMAGE = 35;

	/** The Constant MOVE_SPEED. */
	private static final double MOVE_SPEED = 15 * Game.METER_TO_PIXEL * Game.FIXED_TIMESTEP;
	
	/** The Constant RECOIL_MAGNITUDE. */
	private static final double RECOIL_MAGNITUDE = 8;
	
	/** The Constant PIXEL_SIZE. */
	private static final Vector PIXEL_SIZE = new Vector(12, 12);

	/** from. */
	// id of client which shot the bullet
	private int from;

	/** The id. */
	// id used to identify the shot over the network
	private String id;

	/** The move vector. */
	private Vector moveVector, recoilVector;
	
	/** The current lifespan. */
	private double currentLifespan;

	/** The trigger. */
	private STriggerCollider trigger;

	/**
	 * Instantiates a new server shot.
	 *
	 * @param {int} from - the from
	 * @param {Vector} position - the position
	 * @param {Vector} shotDir - the shot dir
	 */
	public SShot(int from, Vector position, Vector shotDir) {
		super();

		this.from = from;
		
		// set transform
		this.transform.position = position.add(shotDir.scale(SPAWN_OFFSET));
		this.transform.rotation = Math.atan2(shotDir.y, shotDir.x);
		this.transform.scale = Vector.one();
		
		// set lifespan
		this.currentLifespan = LIFESPAN;
		
		// set the move vector
		this.recoilVector = this.transform.up();
		this.moveVector = recoilVector.scale(MOVE_SPEED);
		
		// set how the player is affected
		this.recoilVector = this.recoilVector.scale(RECOIL_MAGNITUDE);
		this.recoilVector.negate();
		
		// sets the shots id, uses UUID for unique ids
		this.id = UUID.randomUUID().toString();
		this.trigger = new STriggerCollider(this, PIXEL_SIZE);
	}

	/**
	 * Update.
	 *
	 * @return {boolean} true, if the bullet is still alive after the update
	 */
	public boolean update() {
		if (this.currentLifespan < 0) return false;
		
		// moves the shot
		this.transform.position.translate(moveVector);
		this.currentLifespan -= Game.FIXED_TIMESTEP;
		
		// checks if the trigger collider collided with a rigidbody
		Rigidbody bodyTriggered;
		if ((bodyTriggered = this.trigger.get()) != null) {
			// checks if the body was kinematic
			if (bodyTriggered.mode.isKinematic()) {
				GameObject object = bodyTriggered.getHost();
				
				// checks if the host is an instance of server player
				if (object instanceof SPlayer) {
					SPlayer player = (SPlayer) object;
					player.damage(DAMAGE, this.from);
				}
			}
			
			// adds recoil to the body
			bodyTriggered.addForce(this.recoilVector, ForceMode.IMPULSE);
			return false;
		}

		return true;
	}

	/**
	 * Gets the id.
	 *
	 * @return {id} the id of this shot
	 */
	public String getId() {
		return id;
	}
}
