/**
 *
 */
package gameobjects;

import framework.common.GameObject;
import framework.common.Transform;
import framework.components.SpriteRenderer;

/**
 * The Class CShot.
 *
 * @author priyangkar ghosh
 */
public class CShot extends GameObject {
	
	/**
	 * Instantiates a new c shot.
	 *
	 * @param {Transform} transform - the transform
	 */
	public CShot(Transform transform) {
		super(transform);
		
		// adds a sprite renderer component to this game object
		new SpriteRenderer(this, SpriteRenderer.PRELOADS.get("shot"));
	}
}
