package framework.math;

import java.util.Objects;

/**
 * The Vector class for 2d vector math.
 * 
 * @author priyangkar ghosh
 */
public class Vector {	
	/** The constant slack. */
	public static final double slack = 1e-5;

	/**
	 * Crosses a double and a vector.
	 *
	 * @param {double} a - the a
	 * @param {Vector} b - the b
	 * @return the crossed vector
	 */
	public static Vector cross(double a, Vector b) {
		return new Vector(-a * b.y, a * b.x);
	}

	/**
	 * Crosses a double and a vector.
	 *
	 * @param {Vector} a - the a
	 * @param {double} b - the b
	 * @return the crossed vector
	 */
	public static Vector cross(Vector a, double b) {
		return new Vector(a.y * b, a.x * b);
	}

	/**
	 * Down vector. (0, 1)
	 *
	 * @return {Vector} the down vector
	 */
	public static Vector down() {
		return new Vector(0, 1);
	}

	/**
	 * Left vector. (-1, 0)
	 *
	 * @return {Vector} the left vector
	 */
	public static Vector left() {
		return new Vector(-1, 0);
	}

	/**
	 * One vector. (1, 0)
	 *
	 * @return {Vector} the right vector
	 */
	public static Vector right() {
		return new Vector(1, 0);
	}

	/**
	 * Up vector. (0, -1)
	 *
	 * @return {Vector} the up vector
	 */
	public static Vector up() {
		return new Vector(0, -1);
	}

	/**
	 * Zero. (0, 0)
	 *
	 * @return {Vector} the zero vector
	 */
	public static Vector zero() {
		return new Vector(0, 0);
	}

	/**
	 * One vector. (1, 1)
	 *
	 * @return {Vector} the one vector
	 */
	public static Vector one() {
		return new Vector(1, 1);
	}
	
	/**
	 * Random vector. (0 - 1, 0 - 1)
	 *
	 * @return {Vector} a random vector
	 */
	public static Vector random() {
		return new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1);
	}

	/**  The properties of the vector. */
	public double x, y, sqrMagnitude;

	/**
	 * Instantiates a new vector.
	 *
	 * @param {double} x - the x
	 * @param {double} y - the y
	 */
	public Vector(double x, double y) {
		this.set(x, y);
	}

	/**
	 * Instantiates a new vector.
	 *
	 * @param {Vector} v - the v
	 */
	public Vector(Vector v) {
		this.set(v);
	}

	/**
	 * Sets the vector to be equal specified vector.
	 *
	 * @param {Vector} v - the v
	 */
	public void set(Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.sqrMagnitude = this.dot(this);
	}

	/**
	 * Sets the vector to specified x and y.
	 *
	 * @param {double} x - the x
	 * @param {double} y - the y
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
		this.sqrMagnitude = this.dot(this);
	}

	/**
	 * Translates the vector by specified vector.
	 *
	 * @param {Vector} v - the v
	 */
	public void translate(Vector v) {
		this.x = x + v.x;
		this.y = y + v.y;
		this.sqrMagnitude = this.dot(this);
	}

	/**
	 * Abs value of the vector.
	 *
	 * @return {Vector} absolute value of the vector
	 */
	public Vector abs() {
		return new Vector(Math.abs(this.x), Math.abs(this.y));
	}

	/**
	 * Inverse of the vector.
	 *
	 * @return {Vector} inverse of the vector
	 */
	public Vector inverse() {
		return new Vector(1 / this.x, 1 / this.y);
	}

	/**
	 * Magnitude of the vector.
	 *
	 * @return {double} the magnitude of the vector
	 */
	public double magnitude() {
		return Math.sqrt(sqrMagnitude);
	}

	/**
	 * Negated version of the vector.
	 *
	 * @return {Vector} the negated vector
	 */
	public Vector negated() {
		return new Vector(-this.x, -this.y);
	}

	/**
	 * Calculates the normal of the vector.
	 *
	 * @return {Vector} returns the normal vector
	 */
	public Vector normal() {
		return new Vector(this.y, -this.x);
	}

	/**
	 * Returns the normalized vector (vector with magnitude of 1).
	 *
	 * @return {Vector} returns the normalized vector
	 */
	public Vector normalized() {
		double factor = 1 / this.magnitude();
		return this.scale(factor);
	}

	/**
	 * Adds the two vectors.
	 *
	 * @param {Vector} other - the other
	 * @return {Vector} the sum of the two vectors
	 */
	public Vector add(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y);
	}

	/**
	 * Subtracts the two vectors.
	 *
	 * @param {Vector} other - the other
	 * @return {Vector} the difference between the two vectors
	 */
	public Vector subtract(Vector other) {
		return new Vector(this.x - other.x, this.y - other.y);
	}

	/**
	 * Scales the vector by a double.
	 *
	 * @param {double} num - the num
	 * @return {Vector} the scaled vector
	 */
	public Vector scale(double num) {
		return new Vector(this.x * num, this.y * num);
	}

	/**
	 * Scales the vector by a vector.
	 *
	 * @param {Vector} other - the other
	 * @return {Vector} the scaled vector
	 */
	public Vector scale(Vector other) {
		return new Vector(this.x * other.x, this.y * other.y);
	}

	/**
	 * Angle between two vectors. (0° to 180° / 0 rad to π rad)
	 *
	 * @param {Vector} other - the other
	 * @return {double} angle in radians
	 */
	public double angle(Vector other) {
		return Math.acos(MathExt.clamp(this.normalized().dot(other.normalized()), -1, 1)) % MathExt.TWO_PI;
	}

	/**
	 * Signed angle between two vectors. (-180° to 180° / -π rad to π rad)
	 * from: https://stackoverflow.com/questions/68843016/why-is-my-angle-of-2-vectors-function-return-nan-even-though-i-follow-the-formul
	 *
	 * @param {Vector} other - the other
	 * @return {double} angle in radians
	 */
	public double signedAngle(Vector other) {
		return this.angle(other) * Math.signum(other.cross(this));
	}

	/**
	 * Complete angle between two vectors. (0° to 360° / 0 rad to 2π rad)
	 *
	 * @param {Vector} other - the other
	 * @return {double} angle in radians
	 */
	public double completeAngle(Vector other) {
		double angle = this.signedAngle(other);
		return (angle < 0) ? angle + MathExt.TWO_PI : angle;
	}

	/**
	 * Cross two vectors.
	 *
	 * @param {Vector} other - the other
	 * @return {double} the crossed value
	 */
	public double cross(Vector other) {
		return this.x * other.y - this.y * other.x;
	}

	/**
	 * Dot two vectors.
	 *
	 * @param {Vector} other - the other
	 * @return {double} the value obtained from dotting
	 */
	public double dot(Vector other) {
		return this.x * other.x + this.y * other.y;
	}

	/**
	 * Negate the vector.
	 */
	public void negate() {
		this.x = -this.x;
		this.y = -this.y;
	}

	/**
	 * Normalize the vector.
	 */
	public void normalize() {
		double mag = this.magnitude();
		this.sqrMagnitude = 1;

		this.x /= mag;
		this.y /= mag;
	}

	/**
	 * Project this vector onto another.
	 *
	 * @param {Vector} other - the other
	 * @return {Vector} returns the projected vector
	 */
	public Vector project(Vector other) {
		return other.scale(this.dot(other) / other.sqrMagnitude);
	}

	/**
	 * To string.
	 *
	 * @return {String} the string
	 */
	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(sqrMagnitude);
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return true, if successful
	 */
	// checks if two vectors are collinear
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Vector)) return false;
		Vector other = (Vector) obj;
		return Math.abs(this.cross(other)) < slack;
	}
}
