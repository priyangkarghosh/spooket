package framework.math;

import java.util.Objects;

/**
 * The Class MinMax.
 * 
 * @author priyangkar ghosh
 */
public class MinMax {

	/** The min and max. */
	private double min, max;

	/**
	 * Instantiates a new min max.
	 */
	public MinMax() {
		reset();
	}

	/**
	 * Instantiates a new min max.
	 *
	 * @param {double} min - the min
	 * @param {double} max - the max
	 */
	public MinMax(double min, double max) {
		set(min, max);
	}

	/**
	 * Instantiates a new min max.
	 *
	 * @param {MinMax} minMax - the min max
	 */
	public MinMax(MinMax minMax) {
		set(minMax.min, minMax.max);
	}

	/**
	 * Gets the max.
	 *
	 * @return {double} the max
	 */
	public double getMax() {
		return this.max;
	}

	/**
	 * Gets the min.
	 *
	 * @return {double} the min
	 */
	public double getMin() {
		return this.min;
	}

	/**
	 * Resets the object.
	 */
	public void reset() {
		this.min = Double.MAX_VALUE;
		this.max = Double.MIN_VALUE;
	}

	/**
	 * Sets the min and max.
	 *
	 * @param {double} min - the min
	 * @param {double} max - the max
	 */
	public void set(double min, double max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Updates the min max.
	 *
	 * @param {double} value - the value
	 */
	public void update(double value) {
		this.min = Math.min(this.min, value);
		this.max = Math.max(this.max, value);
	}

	/**
	 * Hash code.
	 *
	 * @return {int} the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(max, min);
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return {boolean} true, if equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MinMax))
			return false;
		MinMax other = (MinMax) obj;
		return Double.doubleToLongBits(this.max) == Double.doubleToLongBits(other.max)
				&& Double.doubleToLongBits(this.min) == Double.doubleToLongBits(other.min);
	}
}
