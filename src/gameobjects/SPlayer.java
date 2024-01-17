package gameobjects;

import framework.common.GameObject;
import framework.components.Rigidbody;
import framework.components.SCharacterController;
import framework.math.MathExt;
import framework.math.Vector;
import framework.physics.Material;
import framework.physics.Physics.ForceMode;
import framework.physics.Physics.RigidbodyMode;
import framework.physics.Vertex;
import game.Game;
import networking.server.ServerManager;
import networking.server.ServerSend;

/**
 * The Class SPlayer (server-side player).
 * 
 * @author priyangkar ghosh
 */
public class SPlayer extends GameObject {	
	/** The Constant MAX_HEALTH. */
	public static final int MAX_HEALTH = 100;
	
	/** The Constant REGEN_AMOUNT. */
	public static final double REGEN_AMOUNT = 5 * Game.FIXED_TIMESTEP;

	/** The Constant MOVE_FORCE. */
	public static final Vector MOVE_FORCE = new Vector(25, 25).scale(Game.METER_TO_PIXEL);
	
	/** The Constant SHOTS_PER_SECOND. */
	public static final double SHOTS_PER_SECOND = 4 * Game.FIXED_TIMESTEP; // shots per second
	
	/** The Constant RECOIL. */
	public static final double RECOIL = 5;

	/** The id and name of the player. */
	private int id;		
	private String name;

	/** The kills and health of the player. */
	public int kills;	
	private double health;

	/** The rigidbody of this player. */
	public Rigidbody body = new Rigidbody(
			this, RigidbodyMode.KINEMATIC, 1,
			new Vertex[] {
					new Vertex(new Vector(10, 0)), // is a trapezoid
					new Vertex(new Vector(22, 0)),
					new Vertex(new Vector(32, 30)),
					new Vertex(new Vector(0, 30)),
			}, new Material(1, 0)
	);

	/** The controller. */
	public SCharacterController controller;

	/**
	 * Instantiates a new SPlayer.
	 *
	 * @param {int} id - the id
	 * @param {String} name - the name
	 */
	public SPlayer(int id, String name) {
		this.id = id;
		this.name = name;
		
		// adds a character controller component to this player
		this.controller = new SCharacterController(
				this, id, MOVE_FORCE, SHOTS_PER_SECOND, RECOIL
		);
		
		// adds the body to the physics world and spawns the player
		ServerManager.world.addBody(this.body);
		this.spawn();
	}

	/**
	 * Update.
	 */
	public void update() {
		// regenerates the player hp, making sure it doesn't exceed the max health
		this.health = MathExt.clamp(this.health + REGEN_AMOUNT, 0, MAX_HEALTH);		
	}

	/**
	 * Spawns/Respawns the player.
	 */
	public void spawn() {
		this.health = MAX_HEALTH;
		
		// randomly chooses where to spawn the player
		Vector spawnPosition = new Vector(1260 * Math.random(), 680 * Math.random());
		Vector translation = spawnPosition.subtract(this.transform.position);

		// set the velocity of the body to 0, and move it to the spawn position
		body.addForce(Vector.zero(), ForceMode.SET);
		body.translate(translation, true);
	}

	/**
	 * Damages the player.
	 *
	 * @param {double} amount - the amount
	 * @param {int} from - the from
	 */
	public void damage(double amount, int from) {
		// removes the damage from the health
		this.health -= amount;
		if (this.health > 0) return;
		
		// if the health goes lower than 0, then the player dies
		ServerManager.addKill(from);
		ServerSend.killedPlayer(from);
		this.spawn();
	}

	/**
	 * Gets the current health.
	 *
	 * @return {double} the health
	 */
	public double getHealth() {
		return this.health;
	}

	/**
	 * Gets how many kills this player has.
	 *
	 * @return {int} the kills
	 */
	public int getKills() {
		return this.kills;
	}
}
