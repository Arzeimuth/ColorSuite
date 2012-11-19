package buxburt.anddev.colorharmonizer.helper;

import android.util.Log;

/**
 * A math class that extends itself to more comprehensive calculations
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class MathExt {

	private static MathExt ref;

	private MathExt() {
		// TODO Auto-generated constructor stub
	}

	public static synchronized MathExt getMathExt() {
		if (ref == null)
			ref = new MathExt();
		return ref;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Get value from 0 to 1 dependent on a domain of min and max
	 * 
	 * @param min
	 * @param max
	 * @param val
	 * @return A value from 0.0 to 1.0 if max > min
	 */
	public static float interpolate(float min, float max, float val) {
		if (min != max) {
			val = (val - min) * (1 / (max - min));
		}

		else {
			Log.e("Divide by Zero",
					"Max cannot equal min. Division by zero error.");
		}

		return val;
	}

	/**
	 * Get value from range newMin to newMax dependent on a domain of oldMin and
	 * oldMax
	 * 
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 * @param val
	 * @return A value from newMin to newMax if max>min
	 */
	public static float linear_interpolate(float oldMin, float oldMax,
			float newMin, float newMax, float val) {
		if (newMin != newMax && oldMin != oldMax) {
			val = newMin + (val - oldMin)
					* ((newMax - newMin) / (oldMax - oldMin));
		}

		else {
			Log.e("Divide by Zero",
					"Max cannot equal min. Division by zero error.");
		}
		return val;
	}

	/**
	 * Find the largest number out of a tuplet
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return The minimum of the three values
	 */
	public static float min_extended(float x, float y, float z) {
		return Math.min(x, Math.min(y, z));
	}

	/**
	 * Find the largest number out of a tuplet
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return The maximum of the three values
	 */
	public static float max_extended(float x, float y, float z) {
		return Math.max(x, Math.max(y, z));
	}
}
