package image;

import java.awt.*;

/**
 * Utility class for image processing operations such as padding, splitting, brightness calculation, etc.
 *
 * @author Eyal and Dana
 */
public class ImageProcessor {
	final static int MAX_RGB = 255;
	final static double RED = 0.2126;
	final static double GREEN = 0.7152;
	final static double BLUE = 0.0722;
	private static final int HALF_DIVISOR = 2;
	private static final int INITIAL_POWER = 1;
	private static final int POWER_OF_TWO_BASE = 2;


	// Private constructor to prevent instantiation
	private ImageProcessor() {
	}

	/**
	 * Returns a new Image padded symmetrically to the next power-of-two dimensions.
	 * If the source image already has width and height as powers of two, returns the original.
	 *
	 * @param sourceImage the source Image
	 * @return a new, padded Image instance or the original if no padding is needed
	 */
	public static Image padToPowerOfTwo(Image sourceImage) {
		int originalWidth = sourceImage.getWidth();
		int originalHeight = sourceImage.getHeight();
		int paddedWidth = nextPowerOfTwo(originalWidth);
		int paddedHeight = nextPowerOfTwo(originalHeight);
		if (paddedWidth == originalWidth && paddedHeight == originalHeight) {
			return sourceImage;
		}
		// Create a white canvas of target size
		Color[][] paddedPixels = new Color[paddedHeight][paddedWidth];
		for (int row = 0; row < paddedHeight; row++) {
			for (int col = 0; col < paddedWidth; col++) {
				paddedPixels[row][col] = Color.WHITE;
			}
		}

		// Calculate offsets to center the original image
		int horizontalOffset = (paddedWidth - originalWidth) / HALF_DIVISOR;
		int verticalOffset = (paddedHeight - originalHeight) / HALF_DIVISOR;

		// Copy original pixels into centered position
		for (int row = 0; row < originalHeight; row++) {
			for (int col = 0; col < originalWidth; col++) {
				paddedPixels[row + verticalOffset][col + horizontalOffset] =
						sourceImage.getPixel(row, col);
			}
		}

		return new Image(paddedPixels, paddedWidth, paddedHeight);
	}

	/**
	 * Splits the image into a grid of square sub-images based on the specified number of sub-images per row.
	 * Each sub-image is a square block of pixels.
	 *
	 * @param sourceImage     the source Image to split
	 * @param subImagesPerRow the number of sub-images per row (grid width)
	 * @return a 2D array of sub-images [numberOfRows][subImagesPerRow]
	 */
	public static Image[][] splitImage(Image sourceImage, int subImagesPerRow) {
		int fullWidth = sourceImage.getWidth();
		int fullHeight = sourceImage.getHeight();
		int squareSize = fullWidth / subImagesPerRow;
		int numberOfRows = fullHeight / squareSize;

		Image[][] grid = new Image[numberOfRows][subImagesPerRow];

		for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
			for (int colIndex = 0; colIndex < subImagesPerRow; colIndex++) {
				Color[][] cellPixels = new Color[squareSize][squareSize];
				// Fill the cellPixels array with the corresponding pixels from the source image
				for (int pixelRow = 0; pixelRow < squareSize; pixelRow++) {
					for (int pixelCol = 0; pixelCol < squareSize; pixelCol++) {
						// Calculate the source pixel coordinates
						int sourceY = colIndex * squareSize + pixelCol;
						int sourceX = rowIndex * squareSize + pixelRow;
						cellPixels[pixelRow][pixelCol] =
								sourceImage.getPixel(sourceX, sourceY);
					}
				}
				// Create a new Image for the sub-image and assign it to the grid
				grid[rowIndex][colIndex] =
						new Image(cellPixels, squareSize, squareSize);
			}
		}

		return grid;
	}

	/**
	 * Computes the average brightness of the image.
	 *
	 * @param image the Image to compute brightness for
	 * @return the average brightness as a double value
	 */
	public static double computeAverageBrightness(Image image) {
		int width = image.getWidth();
		int height = image.getHeight();
		double totalGrayValue = 0.0;

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Color pixelColor = image.getPixel(col, row);
				totalGrayValue += (pixelColor.getRed() * RED +
						pixelColor.getGreen() * GREEN +
						pixelColor.getBlue() * BLUE);
			}
		}

		return totalGrayValue / (width * height * MAX_RGB);
	}

	/**
	 * Computes the next power of two greater than or equal to n.
	 *
	 * @param n positive integer
	 * @return smallest power of two >= n
	 */
	private static int nextPowerOfTwo(int n) {
		int p = INITIAL_POWER;
		while (p < n) {
			p *= POWER_OF_TWO_BASE;
		}
		return p;
	}

}



