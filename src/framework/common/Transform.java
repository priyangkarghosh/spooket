package framework.common;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import framework.math.MathExt;
import framework.math.Vector;

/**
 * Transform class. 
 * Used to store position, rotation, and scale of game objects.
 * 
 * @author priyangkar ghosh
 */
public class Transform {

	/** The position. */
	public Vector position = Vector.zero();

	/** The rotation. */
	public double rotation = 0;

	/** The scale. */
	public Vector scale = Vector.one();

	/**
	 * Instantiates a new transform.
	 */
	public Transform() {

	}

	/**
	 * Instantiates a new transform.
	 *
	 * @param {Vector} position - the position
	 * @param {double} rotation - the rotation
	 * @param {Vector} scale - the scale
	 */
	public Transform(Vector position, double rotation, Vector scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	/**
	 * Up.
	 *
	 * @return {Vector} the up vector of this transform
	 */
	public Vector up() {
		return MathExt.toVector(this.rotation);
	}

	/**
	 * Down.
	 *
	 * @return {Vector} the down vector of this transform
	 */
	public Vector down() {
		return MathExt.toVector(this.rotation + Math.PI);
	}

	/**
	 * Left.
	 *
	 * @return {Vector} the left vector of this transform
	 */
	public Vector left() {
		return MathExt.toVector(this.rotation + MathExt.HALF_PI);
	}

	/**
	 * Right.
	 *
	 * @return {Vector} the right vector of this transform
	 */
	public Vector right() {
		return MathExt.toVector(this.rotation - MathExt.HALF_PI);
	}

	/**
	 * Rotate.
	 *
	 * @param {Vector} point - the point
	 * @param {Vector} pivot - the pivot
	 * @param {double} angle - the angle
	 * @return {Vector} the rotated vector
	 */
	public static Vector rotate(Vector point, Vector pivot, double angle) {
		Point2D result = new Point2D.Double();
	    AffineTransform rotation = new AffineTransform();

	    rotation.rotate(angle, pivot.x, pivot.y);
	    rotation.transform(new Point2D.Double(point.x, point.y), result);

	    return new Vector(result.getX(), result.getY());
	}
}
