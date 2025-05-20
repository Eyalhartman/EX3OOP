package ascii_art;

import image.Image;
import image_char_matching.SubImgCharMatcher;
import image.ImageProcessor;

import java.io.IOException;

public class AsciiArtAlgorithm {
	private Image image;
	private final int res;
	private char[] charset;

	public AsciiArtAlgorithm(Image image, int res, char[] charset){
		this.image = image;
		this.res = res;
		this.charset = charset;
	}


	public char[][] run(){
		SubImgCharMatcher subImgCharMatcher = new SubImgCharMatcher(charset);
		image = ImageProcessor.padToPowerOfTwo(this.image);
		Image[][] images = ImageProcessor.splitImage(this.image, this.res);
		char[][] brightness = new char[images.length][images[0].length];
		for (int i=0; i<images.length;i++){
			for (int j =0; j<images[i].length; j++){
				brightness[i][j] =
						subImgCharMatcher.getCharByImageBrightness
								(ImageProcessor.computeAverageBrightness(images[i][j]));
			}
		}
		return brightness;
	}

	public static void main(String[] args) {
		char[] set = {'m', 'o'};
		int res = 2;
		try{
			Image image1 = new Image(args[0]);
			AsciiArtAlgorithm ascii = new AsciiArtAlgorithm(image1,res,set);
			char[][] charset = ascii.run();
			for (int i = 0; i<charset.length;i++){
				for (int j = 0;j<charset[0].length;j++){
					System.out.println(charset[i][j]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
