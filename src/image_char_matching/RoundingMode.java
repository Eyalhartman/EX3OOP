package image_char_matching;

/**
 * Enum representing different rounding modes.
 * Each mode defines how to round a double value to an integer.
 *
 * @author Eyal and Dana
 */
public enum RoundingMode {
	UP {
		@Override
		public int apply(double value) {
			return (int) Math.ceil(value);
		}
	},
	DOWN {
		@Override
		public int apply(double value) {
			return (int) Math.floor(value);
		}
	},
	NEAREST {
		@Override
		public int apply(double value) {
			return (int) Math.round(value);
		}
	};

	public abstract int apply(double value);
}