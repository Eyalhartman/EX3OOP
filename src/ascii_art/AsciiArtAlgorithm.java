package ascii_art;

import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
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
		char[] set = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		char[] set2 = new char[128];
		char[] printable = new char[126 - 32 + 1];
		for (int i = 32; i <= 126; i++) {
			printable[i - 32] = (char) i;
		}
		for (int i = 0; i < set2.length; i++) {
			set2[i] = (char) i;
		}
		int res = 256;
		try{

			Image image1 = new Image(args[0]);
			AsciiArtAlgorithm ascii = new AsciiArtAlgorithm(image1,res, printable);
			char[][] charset = ascii.run();
			HtmlAsciiOutput htmlAsciiOutput = new HtmlAsciiOutput("output.html",
					"Courier New");
			htmlAsciiOutput.out(charset);
//			ConsoleAsciiOutput consoleAsciiOutput = new ConsoleAsciiOutput();
//			consoleAsciiOutput.out(charset);
//			for (int i = 0; i<charset.length;i++){
//				for (int j = 0;j<charset[0].length;j++){
//					System.out.println(charset[i][j]);
//				}
//			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
