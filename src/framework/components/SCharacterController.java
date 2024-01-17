package framework.components;

import framework.common.GameObject;
import framework.common.InputManager;
import framework.common.InputManager.Controls;
import framework.math.Vector;
import framework.physics.Physics.ForceMode;
import gameobjects.SPlayer;
import gameobjects.SShot;
import networking.server.ServerManager;

/**
 * CharacterController component.
 * 
 * @author priyangkar ghosh
 */
public class SCharacterController extends Component {
	
	/** The id. */
	private int id;

	/** The body and input manager. */
	public Rigidbody body;	
	public InputManager inputManager;

	/** The shot prefab. */
	public SShot shotPrefab;

	/** The move speed. */
	private Vector moveForce;
	
	/** The recoil magnitude. */
	private double recoilMagnitude;

	/** The shoot speed (shots per second) and if time until player can shoot. */
	private double sps;	
	private double shotTimer = 0;

	/**
	 * Instantiates a new character controller.
	 *
	 * @param {GameObject} host - the host
	 * @param {int} id - the id
	 * @param {Vector} moveForce - the move force
	 * @param {double} shotsPerSecond - the shots per second
	 * @param {double} recoilMagnitude - the recoil magnitude
	 */
	public SCharacterController(GameObject host, int id, Vector moveForce, double shotsPerSecond, double recoilMagnitude) {
		super(host, Component.Type.CHARACTER_CONTROLLER);
		
		// removes the component if it isnt attached to a player
		if (!(host instanceof SPlayer)) { host.removeComponent(this); return; }
		
		// initializes variables
		this.id = id;

		this.sps = shotsPerSecond;
		
		this.moveForce = moveForce;
		this.recoilMagnitude = recoilMagnitude;

		this.inputManager = new InputManager();
		this.body = (Rigidbody) host.getComponent(Component.Type.RIGIDBODY);

	}

	/**
	 * Updates the character.
	 */
	@Override
	public void update() {
		Vector mouseDir = this.inputManager.getMouseDirection(this.host.transform.position);
		double rotateAngle = mouseDir.completeAngle(this.host.transform.right());
		
		// rotates the body towards the mouse, and moves it towards it too
		this.body.rotate(rotateAngle, true);
		this.body.addForce(mouseDir.scale(this.moveForce), ForceMode.FORCE);
		
		// lowers the shot timer
		this.shotTimer = Math.max(this.shotTimer - sps, 0);
		
		// checks if the player can shoot
		if (this.inputManager.getInput(Controls.SHOOT) && this.shotTimer == 0) {
			// shoots if the player can shoot
			ServerManager.shoot(this.id, this.host.transform.position, mouseDir);
			this.body.addForce(mouseDir.scale(recoilMagnitude), ForceMode.IMPULSE);
			this.shotTimer = 1;
		}
	}

	/**
	 * Closes this component.
	 */
	@Override
	public void close() {
		super.close();

		this.body = null;
		this.inputManager = null;
	}
}
