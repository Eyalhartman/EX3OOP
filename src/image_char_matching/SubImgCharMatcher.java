package image_char_matching;

import java.util.*;

/**
 * The {@code SubImgCharMatcher} class maps characters to their normalized brightness
 * values based on a binary (black-and-white) image representation.
 * <p>
 * It is used to select the most appropriate ASCII character to represent a
 * sub-image region in ASCII art generation, according to its average brightness.
 * </p>
 *
 * <p>
 * Internally, it computes the brightness of each character in the given set,
 * normalizes the values to [0,1], and allows retrieving the closest character
 * for a given brightness level using a configurable rounding mode.
 * </p>
 *
 * <p>
 * Supports dynamically adding and removing characters, and automatically
 * recomputes brightness normalization when the set changes.
 *
 * @author Eyal and Dana
 */

public class SubImgCharMatcher {
	private static final int NUM_SIZE_BOOL_ARR = 16 * 16;
	private static final int MIN_VAL_ZERO = 0;
	private static final int MIN_VALUE_ONE = 1;
	private static final int ADD_SUB_ONE = 1;

	private RoundingMode roundingMode;
	private final double minBrightness;
	private final double maxBrightness;
	private Map<Character, Double> brightnessMap = new HashMap<>();
	private final List<Character> charset = new ArrayList<>();

	/**
	 * Constructs a SubImgCharMatcher from a given array of characters.
	 * Brightness values are computed and normalized to the range [0,1].
	 *
	 * @param charset the array of characters to include in the mapping.
	 */
	public SubImgCharMatcher(char[] charset) {
		for (char c : charset) {
			this.charset.add(c);
			this.brightnessMap.put(c, calcBrightness(c));
		}
		normalizingBrightness();
		this.minBrightness = Collections.min(this.brightnessMap.values());
		this.maxBrightness = Collections.max(this.brightnessMap.values());
		this.charset.sort(
				Comparator.comparingDouble(brightnessMap::get)
		);
	}

	/**
	 * Sets the rounding mode to use when mapping brightness to character index.
	 *
	 * @param mode the rounding mode (e.g., RoundingMode.HALF_UP)
	 */
	public void setRoundingMode(RoundingMode mode) {
		this.roundingMode = mode;
	}

	/**
	 * Returns the currently set rounding mode.
	 *
	 * @return the rounding mode in use
	 */
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}


	/**
	 * Returns the character from the current set whose normalized brightness
	 * most closely matches the provided brightness value, according to the
	 * configured rounding mode.
	 * If multiple characters have the same brightness, the one with the lowest
	 * ASCII value is returned.
	 *
	 * @param brightness the brightness value of the sub-image (usually in [0,1])
	 * @return the best-matching character
	 */
	public char getCharByImageBrightness(double brightness) {
		// check if brightness is in range
		double normalized = (brightness - minBrightness) / (maxBrightness - minBrightness);
		normalized = Math.max(MIN_VAL_ZERO, Math.min(MIN_VALUE_ONE, normalized));
		// scale to the range of charset size
		double scaled = normalized * (charset.size() - ADD_SUB_ONE);
		// round to the nearest index

		int idx = roundingMode.apply(scaled);
		// ensure index is within bounds

		idx = Math.max(MIN_VAL_ZERO, Math.min(idx, charset.size() - ADD_SUB_ONE));
		// return the character at the calculated index
		return charset.get(idx);
	}


	/**
	 * Adds a character to the set and updates the brightness map and normalization.
	 *
	 * @param c the character to add
	 */
	public void addChar(char c) {
		this.charset.add(c);
		this.brightnessMap.put(c, calcBrightness(c));
		normalizingBrightness();
	}


	/**
	 * Removes a character from the set and updates the brightness map and normalization.
	 *
	 * @param c the character to remove
	 */
	public void removeChar(char c) {
		this.charset.remove(c);
		this.brightnessMap.remove(c);
		normalizingBrightness();
	}

	/**
	 * Calculates the brightness of a character by converting it to a boolean matrix
	 * and counting the number of pixels set to {@code true}.
	 *
	 * @param c the character to compute brightness for
	 * @return the normalized brightness value in [0,1]
	 */
	private double calcBrightness(char c) {
		boolean[][] charToBool = CharConverter.convertToBoolArray(c);
		int trueCounter = MIN_VAL_ZERO;
		for (int i = 0; i < charToBool.length; i++) {
			for (int j = 0; j < charToBool[i].length; j++) {
				if (charToBool[i][j]) {
					trueCounter++;
				}
			}
		}
		return (double) trueCounter / NUM_SIZE_BOOL_ARR;
	}

	/**
	 * Normalizes the brightness values of all characters in the set
	 * so that they fall within the range [0,1].
	 * This method is called after adding or removing characters.
	 */
	private void normalizingBrightness() {
		double minVal = Collections.min(this.brightnessMap.values());
		double maxVal = Collections.max(this.brightnessMap.values());
		double maxMinusMin = maxVal - minVal;

		TreeMap<Character, Double> normalizedMap = new TreeMap<>();

		for (Map.Entry<Character, Double> entry : brightnessMap.entrySet()) {
			double newVal = (entry.getValue() - minVal) / maxMinusMin;
			normalizedMap.put(entry.getKey(), newVal);
		}
		this.brightnessMap = normalizedMap;
	}
}
