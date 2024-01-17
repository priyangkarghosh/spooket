package networking.server;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

import framework.common.GameObject;
import framework.common.InputManager.Controls;
import framework.components.Rigidbody;
import framework.math.MathExt;
import framework.math.Vector;
import framework.physics.Material;
import framework.physics.Physics;
import framework.physics.Physics.RigidbodyMode;
import framework.physics.Vertex;
import gameobjects.SShot;
import scenes.GameScene;

/**
 * The Class ServerManager.
 * 
 * @author priyangkar ghosh
 */
public class ServerManager {

	/** The world. */
	public static Physics world = new Physics(Vector.zero());

	/** The bullets in the world right now. */
	private static HashSet<SShot> shots = new HashSet<>();
	
	/** The asteroids in the world right now. */
	private static GameObject[] asteroids = new GameObject[GameScene.NUM_OF_ASTEROIDS];
	
	/**
	 * Initializes the server manager.
	 */
	public static void init() {
		world.init();
		
		// adds the static ship to the world
		world.addBody(
				new Rigidbody(new GameObject(), RigidbodyMode.STATIC, 0, 
						
				new Vertex[] {
					new Vertex(968, 182),
					new Vertex(1001, 132),
					new Vertex(1061, 132),
					new Vertex(1113, 223),
					new Vertex(1078, 268),
					new Vertex(1018, 275),
				}, 
				
				new Material(1, 0))
		);
		
		// adds the asteroids to the world
		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++) {
			asteroids[i] = new GameObject();
			
			// chooses a random spawn location
			Vector spawnPosition = new Vector(
					1280 * MathExt.clamp(Math.random(), 0.3, 0.7), 
					720 * MathExt.clamp(Math.random(), 0.3, 0.7)
			);
			
			// adds the body to the world
			world.addBody(
					new Rigidbody(asteroids[i], RigidbodyMode.DYNAMIC, 1, 
							
					new Vertex[] {
						new Vertex(spawnPosition.x + 26, spawnPosition.y + 0),
						new Vertex(spawnPosition.x + 57, spawnPosition.y + 24),
						new Vertex(spawnPosition.x + 32, spawnPosition.y + 50),
						new Vertex(spawnPosition.x + 0, spawnPosition.y + 32),
						new Vertex(spawnPosition.x + 0, spawnPosition.y + 14),
					}, 
					
					new Material(0.5, 0))
			);
		}
	}

	/**
	 * Shoot.
	 *
	 * @param {int} id - the id
	 * @param {Vector} position - the position
	 * @param {Vector} direction - the direction
	 */
	public static void shoot(int id, Vector position, Vector direction) {
		SShot shot = new SShot(id, position, direction);
		ServerSend.shotAdded(shot.getId(), shot.transform);
		shots.add(shot);
	}
	
	/**
	 * Add kill to specified player.
	 *
	 * @param {int} id - the id of the player who got the kill
	 */
	public static void addKill(int id) {
		SClient sc = Server.instance().getClient(id);
		sc.player.kills++;
	}

	/**
	 * Tick.
	 */
	public static void tick() {
		world.step();
		
		// loops through each player and updates them (and sends their information to other clients)
		for (int i = 0; i < Server.MAX_PLAYERS; i++) {
			try {
				SClient sc = Server.instance().getClient(i);
				if (sc != null && sc.player != null) {
					sc.player.update();
					ServerSend.playerInformation(
							sc.getId(), sc.player.getHealth(),
							sc.player.controller.inputManager.getInput(Controls.SHOOT),
							sc.player.transform
					);
				}
			}
			
			catch (NullPointerException e) { }
		}
		
		// loops through each shot and sents their information
		try {
			for (Iterator<SShot> i = shots.iterator(); i.hasNext();) {
			    SShot shot = i.next();
			    
			    // makes sure the shot is still alive
			    if (shot.update()) {
			    	// if it is then it sends it's information
					ServerSend.shotInformation(shot.getId(), shot.transform);
					continue;
			    }
			    
			    // otherwise it removes the shot and destroys its game object
			    ServerSend.shotRemoved(shot.getId());
			    shot.destroy();
			    i.remove();
			}
		}

		catch (ConcurrentModificationException e) { }
		
		// sends the asteroid information
		ServerSend.asteroidInformation(asteroids);
	}

	/**
	 * Resets the server manager.
	 */
	public static void reset() {
		// destroys all the asteroids
		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++) {
			asteroids[i].destroy();
			asteroids[i] = null;
		}
		
		// destroys all the remaining shots
		for (SShot shot : shots) shot.destroy();
		
		// clears the collections
		shots.clear();
		world.clear();
	}
}
