package image_char_matching;


/**
 * {@code RoundingMode} is an enumeration of rounding strategies used
 * to convert a double precision brightness value to an integer index.
 *
 * <p>Each constant implements its own version of {@link #apply(double)},
 * allowing flexible control over how a floating-point brightness score is mapped
 * to a character index during ASCII art generation.</p>
 * @author Eyal and Dana
 */
public enum RoundingMode {
	/**
	 * Rounds the value up to the nearest integer (ceiling).
	 * Example: 2.1 → 3
	 */
	UP {
		@Override
		public int apply(double value) {
			return (int) Math.ceil(value);
		}
	},

	/**
	 * Rounds the value down to the nearest integer (floor).
	 * Example: 2.9 → 2
	 */
	DOWN {
		@Override
		public int apply(double value) {
			return (int) Math.floor(value);
		}
	},
	/**
	 * Rounds the value to the nearest integer using standard rounding.
	 * Example: 2.5 → 3, 2.4 → 2
	 */
	NEAREST {
		@Override
		public int apply(double value) {
			return (int) Math.round(value);
		}
	};

	/**
	 * Applies the rounding mode to the given double value.
	 *
	 * @param value the value to round
	 * @return the rounded integer result
	 */
	public abstract int apply(double value);
}