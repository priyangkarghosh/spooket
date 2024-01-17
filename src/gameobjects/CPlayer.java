package gameobjects;

import framework.common.GameObject;
import framework.components.HealthBarRenderer;
import framework.components.Renderer;
import framework.components.SpriteRenderer;
import framework.components.TrailRenderer;
import framework.math.Vector;
import game.PlayerPrefs;
import networking.client.CClient;

/**
 * The Class CPlayer (client-side player object).
 * 
 * @author priyangkar ghosh
 */
public class CPlayer extends GameObject {
	/** The Constant WEAPON_OFFSET. */
	private static final double WEAPON_OFFSET = 8;
	
	/** The Constant HEALTH_INCREMENT. */
	private static final double HEALTH_INCREMENT = 100 / 4;

	/** The id and username of the player. */
	private int id;	
	private String name;
	
	/** If the client is shooting. */
	private boolean isShooting;

	/**  The number of kills the player has, and their health. */
	private int kills;		
	private double health;

	/** The player's renderers. */
	private SpriteRenderer player = new SpriteRenderer(this, SpriteRenderer.PRELOADS.get("player"));		
	private SpriteRenderer weapon = new SpriteRenderer(this, SpriteRenderer.PRELOADS.get("weapon"));		

	private TrailRenderer trail;	
	private HealthBarRenderer healthBar;

	/**
	 * Instantiates a new player.
	 *
	 * @param {int} id - the id
	 * @param {String} name - the name
	 */
	public CPlayer(int id, String name) {
		super();
		
		// initializes the id and name
		this.id = id;
		this.name = name;
		
		// adds the health bar and trail components
		this.trail = new TrailRenderer(this, 6, 30, -10, -8);
		this.healthBar = new HealthBarRenderer(this, name, 100, -3);
		
		// makes sure that the weapon isn't animated
		this.weapon.looping = this.player.looping = false;
		
		// sets the corresponding glow
		if (this.id == CClient.instance().id)
			new SpriteRenderer(this, SpriteRenderer.PRELOADS.get("local_glow"));
		else
			new SpriteRenderer(this, SpriteRenderer.PRELOADS.get("enemy_glow"));
	}

	/**
	 * Updates the player.
	 */
	public void update() {
		// updates the ship so it corresponds to the health of the player
		this.player.setFrame((int) (4 - Math.ceil(health / HEALTH_INCREMENT)));
		
		// sets the weapon to animating if it is shooting
		this.weapon.looping = this.isShooting;
		this.weapon.setOffset(this.transform.right().scale(WEAPON_OFFSET));
		
		// sets the health of the health bar
		this.healthBar.setHealth(this.health);
		
		if (this.id != CClient.instance().id) return;
		
		// screen shakes if the client weapon is shooting
		if (this.weapon.getFrame() < 4 && this.isShooting) 
			Renderer.setTargetOffset(Vector.random().scale(30));
		else Renderer.setTargetOffset(Vector.zero());
		
		// checks if the player hits a new highscore
		if (this.kills > PlayerPrefs.get("killHS", 0))
			PlayerPrefs.set("killHS", this.kills);
	}
	
	/**
	 * Sets the health.
	 *
	 * @param {double} health - the new health
	 */
	public void setHealth(double health) {
		this.health = health;
	}
	
	/**
	 * Sets if the player is shooting.
	 *
	 * @param {boolean} isShooting - the new shooting
	 */
	public void setShooting(boolean isShooting) {
		this.isShooting = isShooting;
	}
	
	/**
	 * Adds a kill to the player.
	 *
	 */
	public void addKill() {
		this.kills += 1;
	}

	/**
	 * Gets the health of the player.
	 *
	 * @return {double} the health
	 */
	public double getHealth() {
		return health;
	}
	
	/**
	 * Sets the kills.
	 *
	 * @param {int} kills - the new kills
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Gets the kills.
	 *
	 * @return {int} the kills this player has
	 */
	public int getKills() {
		return this.kills;
	}

	/**
	 * Gets the name.
	 *
	 * @return {String} the name
	 */
	public String getName() {
		return this.name;
	}
}
