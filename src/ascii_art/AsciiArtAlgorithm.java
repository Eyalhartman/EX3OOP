package ascii_art;

import image.Image;
import image_char_matching.SubImgCharMatcher;
import image.ImageProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code AsciiArtAlgorithm} class generates ASCII art from a given image
 * by dividing it into equally sized blocks, calculating the brightness of each block,
 * and mapping that brightness to a character using a {@link SubImgCharMatcher}.
 *
 * <p>
 * The algorithm ensures the input image is padded to dimensions that are powers of two,
 * splits it into a grid according to a specified resolution, and then creates a
 * 2D character array representing the ASCII-art version of the image.
 * </p>
 *
 * <p>
 * To optimize performance, brightness values are cached per block using a computed index.
 *
 * @author Eyal and Dana
 */
public class AsciiArtAlgorithm {
	private Image image;
	private final int res;
	private final SubImgCharMatcher matcher;
	private final Map<Integer, Double> brightnessCache = new HashMap<>();


	/**
	 * Constructs a new AsciiArtAlgorithm instance.
	 *
	 * @param image   the image to convert into ASCII art
	 * @param res     the resolution (number of blocks per row) used to divide the image
	 * @param matcher a matcher that maps brightness values to characters
	 */
	public AsciiArtAlgorithm(Image image, int res, SubImgCharMatcher matcher) {
		this.image = image;
		this.res = res;
		this.matcher = matcher;
	}

	/**
	 * Runs the ASCII art conversion algorithm.
	 * <p>
	 * This method:
	 * <ul>
	 *     <li>Pads the image to dimensions that are powers of two</li>
	 *     <li>Splits the image into square sub-images (tiles)</li>
	 *     <li>Computes the average brightness for each tile</li>
	 *     <li>Maps each brightness to a character via the matcher</li>
	 * </ul>
	 *
	 * @return a 2D character array representing the ASCII-art image
	 */
	public char[][] run() {
		image = ImageProcessor.padToPowerOfTwo(this.image);
		Image[][] images = ImageProcessor.splitImage(this.image, this.res);
		int rows = images.length;
		int cols = images[0].length;
		char[][] brightness = new char[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int key = i * cols + j;
				final Image subImg = images[i][j];
				double bri = brightnessCache.computeIfAbsent(key,
						k -> ImageProcessor.computeAverageBrightness(subImg));
				brightness[i][j] = matcher.getCharByImageBrightness(bri);

			}
		}
		return brightness;
	}
}
