package networking.client;

import java.util.HashMap;

import framework.common.GameObject;
import framework.common.InputManager;
import framework.common.Transform;
import framework.components.SpriteRenderer;
import gameobjects.CPlayer;
import gameobjects.CShot;
import networking.server.Server;
import scenes.GameScene;

/**
 * The Class GameManager.
 * 
 * @author priyangkar ghosh
 */
public class ClientManager {

	/** The players. */
	public static CPlayer[] players = new CPlayer[Server.MAX_PLAYERS];
	
	/** The shots. */
	private static HashMap<String, CShot> shots = new HashMap<>();
	
	/** The asteroids. */
	public static GameObject[] asteroids = new GameObject[GameScene.NUM_OF_ASTEROIDS];
	
	/**
	 * Initializes the client manager.
	 */
	public static void init() {
		// creates all the asteroids in the world
		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++) {
			asteroids[i] = new GameObject();
			
			// adds a sprite renderer component to them
			new SpriteRenderer(asteroids[i], SpriteRenderer.PRELOADS.get("asteroid"));
		}
	}
	
	/**
	 * Adds the player.
	 *
	 * @param {int} id - the id
	 * @param {String} name - the name
	 * @param {int} kills - the kills
	 * @param {double} health - the health
	 * @param {Transform} transform - the transform
	 */
	public static void addPlayer(int id, String name, int kills, double health, Transform transform) {
		// instantiates a new client side player
		players[id] = new CPlayer(id, name);
		
		// sets the kills and health
		players[id].setKills(kills);
		players[id].setHealth(health);
		
		// sets the transform
		players[id].transform = transform;
		
		// if this is the local player, then set the client to the player
		// and make a new input manager
		if (id == CClient.instance().id) {
			CClient.instance().player = players[id];
			CClient.instance().input = new InputManager();
		}
	}

	/**
	 * Removes the player.
	 *
	 * @param {int} id - the id
	 */
	public static void removePlayer(int id) {
		try {
			// destroys the game object, and sets it to null
			players[id].destroy();
			players[id] = null;
		}

		catch (NullPointerException | ArrayIndexOutOfBoundsException e) { }
	}

	/**
	 * Update player.
	 *
	 * @param {int} id - the id
	 * @param {double} health - the health
	 * @param {boolean} isShooting - the is shooting
	 * @param {Transform} transform - the transform
	 */
	public static void updatePlayer(int id, double health, boolean isShooting, Transform transform) {
		try {
			// updates the players health, shooting, and transform, then updates it
			players[id].setHealth(health);
			players[id].setShooting(isShooting);
			players[id].transform = transform;
			players[id].update();
		}

		catch (NullPointerException e) { }
	}
	
	/**
	 * Killed player.
	 *
	 * @param {int} id - the id
	 */
	public static void killedPlayer(int id) {
		// adds a kill to that player
		players[id].addKill();
	}

	/**
	 * Adds a shot to the scene.
	 *
	 * @param {String} id - the id
	 * @param {Transform} transform - the transform
	 */
	public static void addShot(String id, Transform transform) {
		// adds it to the shots hashmap
		shots.put(id, new CShot(transform));
	}

	/**
	 * Removes a shot.
	 *
	 * @param {String} id - the id
	 */
	public static void removeShot(String id) {
		CShot shot = shots.remove(id);
		if (shot == null) return;
		shot.destroy();
	}

	/**
	 * Updates a shot.
	 *
	 * @param {String} id - the id
	 * @param {Transform} transform - the transform
	 */
	public static void updateShot(String id, Transform transform) {
		// gets the shot
		CShot shot = shots.get(id);
		if (shot == null) return;
		
		// updates the transform if it exists
		shot.transform = transform;
	}
	
	/**
	 * Updates an asteroid.
	 *
	 * @param {int} index - the index
	 * @param {Transform} transform - the transform
	 */
	public static void updateAsteroid(int index, Transform transform) {
		if (asteroids[index] == null) return;
		asteroids[index].transform = transform;
	}

	/**
	 * Resets the client manager.
	 */
	public static void reset() {
		for (int i = 0; i < GameScene.NUM_OF_ASTEROIDS; i++) {
			asteroids[i].destroy();
			asteroids[i] = null;
		}
		
		// loops through all the players
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) continue;
			
			// destroys it if it exists
			players[i].destroy();
			players[i] = null;
		}
		
		// destroys every shot in the hash map
		for (CShot shot : shots.values())
			shot.destroy();
		
		// clears the hash map
		shots.clear();
	}
}
