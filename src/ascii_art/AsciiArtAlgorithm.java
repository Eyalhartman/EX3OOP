package ascii_art;

import image.Image;
import image_char_matching.SubImgCharMatcher;
import image.ImageProcessor;

import java.util.HashMap;
import java.util.Map;


public class AsciiArtAlgorithm {
	private Image image;
	private final int res;
	private final SubImgCharMatcher matcher;
	private final Map<Integer, Double> brightnessCache = new HashMap<>();



	public AsciiArtAlgorithm(Image image, int res, SubImgCharMatcher matcher) {
		this.image = image;
		this.res = res;
		this.matcher = matcher;
	}


	public char[][] run() {
		image = ImageProcessor.padToPowerOfTwo(this.image);
		Image[][] images = ImageProcessor.splitImage(this.image, this.res);
		int rows = images.length;
		int cols = images[0].length;
		char[][] brightness = new char[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
//				brightness[i][j] =
//						matcher.getCharByImageBrightness
//								(ImageProcessor.computeAverageBrightness(images[i][j]));
				int key = i * cols + j;
				final Image subImg = images[i][j];
				double bri = brightnessCache.computeIfAbsent(
						key,
						k -> ImageProcessor.computeAverageBrightness(subImg)
				);
				brightness[i][j] = matcher.getCharByImageBrightness(bri);

			}
		}
		return brightness;

	}
}
