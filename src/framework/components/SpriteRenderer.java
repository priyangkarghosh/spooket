package framework.components;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import framework.common.GameObject;
import framework.math.Vector;
import game.Game;

/**
 * The SpriteRenderer component.
 * Used to render images/spritesheet animations.
 * 
 * @author priyangkar ghosh
 */
public class SpriteRenderer extends Renderer {
	
	/** The Constant PRELOADS. */
	// Constant to preload all the images so they don't have to constantly be loaded
	public static final HashMap<String, SpriteRenderer> PRELOADS = new HashMap<>(){
		{
			put("back", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\background\\back.png", 1, 9, 9, -5));
			put("stars", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\background\\stars.png", 1, 9, 9, -4));
			put("sparkle", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\background\\sparkle.png", 1, 9, 9, -3));
			put("title", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\gui\\title.png", -2));
			put("player", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\player\\base.png", 1, 4, 0, 1));
			put("weapon", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\player\\weapon.png", 1, 12, 48, 0));
			put("shot", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\player\\bullet.png", 1, 10, 10, 0));
			put("ship", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\environment\\ship.png", 1, 60, 10, 2));
			put("asteroid", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\environment\\asteroid.png", 1));
			put("local_glow", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\player\\local_glow.png", -6));
			put("enemy_glow", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\player\\enemy_glow.png", -6));
			put("how_to", new SpriteRenderer(Game.getPrefabObject(), "assets\\images\\gui\\how_to.png", -6));
		}
	};
	
	/** The image. */
	private BufferedImage[] frames;

	/**  The fps to run the animation at */
	private double fps;

	/** The current frame of the animation.  */
	private double current;

	/** If the animation should be looping. */
	public boolean looping = true;

	/**
	 * Instantiates a new sprite renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {String} spritePath - the sprite path
	 * @param {int} depth - the depth
	 */
	public SpriteRenderer(GameObject host, String spritePath, int depth) {
		super(host, depth);

		try {
			// gets the image from specified file path
			this.frames = new BufferedImage[] { ImageIO.read(new File(spritePath)) };
		}

		catch (IOException e) {
			System.out.println("Image not found. ");
		}

		this.setSize(new Vector(this.frames[0].getWidth(), this.frames[0].getHeight()));
	}

	/**
	 * Instantiates a new sprite renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {String} spritesheetPath - the spritesheet path
	 * @param {int} rows - the rows
	 * @param {int} columns - the columns
	 * @param {int} fps - the fps
	 * @param {int} depth - the depth
	 */
	public SpriteRenderer(GameObject host, String spritesheetPath, int rows, int columns, int fps, int depth) {
		super(host, depth);

		try {
			// gets the image from specified file path
			BufferedImage image = ImageIO.read(new File(spritesheetPath));
			this.frames = new BufferedImage[rows * columns];

			// loads spritesheet
			int width = image.getWidth() / columns;
			int height = image.getHeight() / rows;

			for (int row = 0; row < rows; row++)
				for (int column = 0; column < columns; column++) 
					this.frames[row * columns + column] = image.getSubimage(
							column * width, row * height, width, height
					);
			

			// initializes the fps for the animation to run at
			this.fps = fps * Game.FIXED_TIMESTEP;

			// sets the sizing to be correct
			this.setSize(new Vector(width, height));
		}

		catch (IOException e) {
			System.out.println("Image not found. ");
		}
	}

	/**
	 * Instantiates a new sprite renderer.
	 *
	 * @param {GameObject} host - the host
	 * @param {SpriteRenderer} sprite - the sprite
	 */
	public SpriteRenderer(GameObject host, SpriteRenderer sprite) {
		super(host, sprite.size, sprite.offset, sprite.depth);

		this.frames = sprite.frames;
		this.fps = sprite.fps;
	}

	/**
	 * Updates the sprite renderer.
	 */
	@Override
	public void update() {
		super.update();
		if (this.current > 0 || this.looping) this.current += this.fps;
		this.current = (this.current > this.frames.length) ? 0 : this.current;
	}
	
	/**
	 * Gets the frame.
	 *
	 * @return {int} frame - the current frame
	 */
	public int getFrame() {
		return ((int) this.current) + 1;
	}

	/**
	 * Sets the frame.
	 *
	 * @param {int} frame - the new frame
	 */
	public void setFrame(int frame) {
		this.current = Math.abs(frame % this.frames.length);
	}

	/**
	 * Sprite Renderer draw function.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 * @param {Vector} center - the center
	 * @param {Vector} topLeft - the top left
	 * @param {double} rotation - the rotation
	 * @param {Vector} size - the size
	 */
	@Override
	public void draw(Graphics2D g2d, Vector center, Vector topLeft, double rotation, Vector size) {
		if (this.frames == null) return;

		// rotates the graphics object by the sprite rotation
		// simulates drawing a rotated object
	    g2d.rotate(rotation, (int) center.x, (int) center.y);

		g2d.drawImage(
				this.frames[(int) this.current],
				(int) topLeft.x, (int) topLeft.y,
				(int) size.x, (int) size.y, null
		);

		// rotates the graphics object back
	    g2d.rotate(-rotation, (int) center.x, (int) center.y);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(current, fps, looping);
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SpriteRenderer))
			return false;
		SpriteRenderer other = (SpriteRenderer) obj;
		return Arrays.equals(this.frames, other.frames);
	}
}
