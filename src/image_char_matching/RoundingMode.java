package image_char_matching;


/**
 * {@code RoundingMode} is an enumeration of rounding strategies used
 * to convert a double precision brightness value to an integer index.
 *
 * <p>Each constant implements its own version of rounding logic,
 * allowing flexible control over how a floating-point brightness score is mapped
 * to a character index during ASCII art generation.</p>
 *
 * @author Eyal and Dana
 */
public enum RoundingMode {
	/**
	 * Rounds the value to the nearest integer using standard rounding.
	 * Example: 2.5 → 3, 2.4 → 2
	 */
	UP {},
	/**
	 * Rounds the value to the nearest integer using standard rounding.
	 * Example: 2.5 → 3, 2.4 → 2
	 */
	DOWN {},
	/**
	 * Rounds the value to the nearest integer using standard rounding.
	 * Example: 2.5 → 3, 2.4 → 2
	 */
	NEAREST {};
}