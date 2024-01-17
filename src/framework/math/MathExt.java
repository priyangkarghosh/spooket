package framework.math;

/**
 * Math extensions class.
 *
 * @author priyangkar ghosh
 */
public class MathExt {
	
	/** The Constant TWO_PI. */
	public static final double TWO_PI = Math.PI * 2;
	
	/** The Constant HALF_PI. */
	public static final double HALF_PI = Math.PI / 2;

	/**
	 * Clamps a value between two values.
	 *
	 * @param {double} value - the value
	 * @param {double} floor - the floor
	 * @param {double} ceiling - the ceiling
	 * @return {double} the clamped value
	 */
	public static double clamp(double value, double floor, double ceiling) {
		if (floor > ceiling) return clamp(value, ceiling, floor);
		else return Math.min(ceiling, Math.max(floor, value));
	}

	/**
	 * Linearly interpolates between two doubles after clamping t.
	 *
	 * @param {double} start - the start
	 * @param {double} end - the end
	 * @param {double} t - the t
	 * @return {double} the lerped value
	 */
	public static double lerp(double start, double end, double t) {
		t = MathExt.clamp(t, 0, 1);
		return lerpUnclamped(start, end, t);
	}

	/**
	 * Linearly interpolates between two doubles after clamping t.
	 *
	 * @param {Vector} start - the start
	 * @param {Vector} end - the end
	 * @param {double} t - the t
	 * @return {Vector} the lerped vector
	 */
	public static Vector lerp(Vector start, Vector end, double t) {
		t = MathExt.clamp(t, 0, 1);
		return lerpUnclamped(start, end, t);
	}

	/**
	 * Linearly interpolates between two doubles without clamping t.
	 *
	 * @param {double} start - the start
	 * @param {double} end - the end
	 * @param {double} t - the t
	 * @return {double} the lerped value
	 */
	public static double lerpUnclamped(double start, double end, double t) {
		return start + (end - start) * t;
	}

	/**
	 * Linearly interpolates between two vectors without clamping t.
	 *
	 * @param {Vector} start - the start
	 * @param {Vector} end - the end
	 * @param {double} t - the t
	 * @return {Vector} the lerped vector
	 */
	public static Vector lerpUnclamped(Vector start, Vector end, double t) {
		return start.add(end.subtract(start).scale(t));
	}

	/**
	 * angle to vector.
	 *
	 * @param {double} angle - the angle
	 * @return {Vector} the vector
	 */
	public static Vector toVector(double angle) {
		return new Vector(Math.cos(angle), Math.sin(angle));
	}
}
