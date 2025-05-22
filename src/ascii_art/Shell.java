package ascii_art;

import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.RoundingMode;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Shell {


	private static final String EXIT_MSG = "exit";
	private static final String CHARS_MSG = "chars";
	private static final Character[] DEFAULT_CHAR_SET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final int DEFAULT_RES = 2;
	private static final String ADD_MSG = "add";
	private static final char SPACE_CHAR = ' ';
	private static final String ALL_MSG = "all";
	private static final int FIRST_INDEX = 0;
	private static final int FIRST_ASCII_INDEX = 32;
	private static final int AFTER_LAST_ASCII_INDEX = 126;
	private static final String SPACE_MSG = "space";
	private static final char HYPHEN_CHAR = '-';
	private static final String INCORRECT_ADD_MSG = "Did not add due to incorrect format.";
	private static final String REMOVE_MSG = "remove";
	private static final String INCORRECT_REMOVE_MSG = "Did not remove due to incorrect format.";
	private static final String ALL_ADD_MSG = "all";
	private static final String RES_MSG = "res";
	private static final String OUTPUT_MSG = "output";
	private static final String ASCII_MSG = "asciiArt";
	private static final String ROUND_MSG = "round";
	private static final int AFTER_RES_INDEX = RES_MSG.length();
	private static final String INCORRECT_RES_FORMAT_MSG
			= "Did not change resolution due to incorrect format.";
	private static final String INCORRECT_RES_BOUND_MSG
			= "Did not change resolution due to exceeding boundaries.";
	private static final String INCORRECT_OUTPUT_MSG = "Did not change output method due" +
			" to incorrect format.";
	private static final String INCORRECT_ASCII_MSG = "Did not execute. Charset is too small.";
	private static final String INCORRECT_OUTPUT_FORMAT_MSG = "Did not execute due to incorrect command.";
	private static final String INCORRECT_ROUNDING_MODE_MSG = "Did not change rounding" +
			" method due to incorrect format.";
	private static final String[] OUTPUT_OPTIONS = {"html", "console"};


	Set<Character> charset;
	int res;
	private Image image;
	private int maxCharsInRow;
	private int minCharsInRow;
	private String output = "console";
	private RoundingMode roundingMode;
	private SubImgCharMatcher matcher;
	private boolean matcherDirty = true;
	private AsciiArtAlgorithm asciiAlgo;
	private boolean asciiAlgoDirty = true;


	public Shell() {
		this.charset = new TreeSet<>(Arrays.asList(DEFAULT_CHAR_SET));
		this.res = DEFAULT_RES;
		this.roundingMode = RoundingMode.NEAREST;
		char[] arrChar = toCharArray(charset);
		this.matcher = new SubImgCharMatcher(arrChar);
	}


	//todo add exceptions
	public void run(String imageName) throws IOException {
		extractImg(imageName);

		while (true) {
			try {

				System.out.print(">>> ");
				String action = KeyboardInput.readLine();

				if (action.startsWith(EXIT_MSG)) break;
				else if (action.startsWith(CHARS_MSG)) {
					chars_cmd();
					matcherDirty = true;
				} else if (action.startsWith(ADD_MSG)) {
					add_cmd(action);
					matcherDirty = true;
				} else if (action.startsWith(REMOVE_MSG)) {
					remove_cmd(action);
					matcherDirty = true;
				} else if (action.startsWith(RES_MSG)) resCmd(action);
				else if (action.startsWith(OUTPUT_MSG)) outputCmd(action);
				else if (action.startsWith(ASCII_MSG)) asciiCmd();
				else if (action.startsWith(ROUND_MSG)) roundCmd(action);
				else {
					throw new IOException(INCORRECT_OUTPUT_FORMAT_MSG);
				}
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	private void extractImg(String imageName) throws IOException {
		this.image = new Image(imageName);
		asciiAlgoDirty = true;
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		this.maxCharsInRow = imgWidth;
		this.minCharsInRow = Math.max(1, imgWidth / imgHeight);
		this.res = DEFAULT_RES;
	}

	private void remove_cmd(String action) throws IOException {

		String[] parts = action.split(" ");
		if (parts.length < 2) {
			throw new IOException(INCORRECT_REMOVE_MSG);
		}
		String specificRemove = parts[1];

		switch (specificRemove) {
			case ALL_MSG:
				for (char i = FIRST_ASCII_INDEX; i < AFTER_LAST_ASCII_INDEX; i++) {
					this.charset.remove(i);
					matcher.removeChar(i);
				}
				return;
			case SPACE_MSG:
				this.charset.remove(SPACE_CHAR);
				matcher.removeChar(SPACE_CHAR);
				return;
		}

		if (specificRemove.length() == 1) {
			char charToRemove = specificRemove.charAt(0);
			if (charToRemove > FIRST_ASCII_INDEX && charToRemove < AFTER_LAST_ASCII_INDEX) {
				this.charset.remove(specificRemove.charAt(FIRST_INDEX));
				matcher.removeChar(charToRemove);
			}
			return;
		}

		if (specificRemove.charAt(1) == HYPHEN_CHAR) {
			char charARemove = specificRemove.charAt(0);
			char charBRemove = specificRemove.charAt(2);
			if (charARemove > FIRST_ASCII_INDEX &&
					charARemove < AFTER_LAST_ASCII_INDEX &&
					charBRemove > FIRST_ASCII_INDEX &&
					charBRemove < AFTER_LAST_ASCII_INDEX) {
				char charStart = (char) Math.min(charARemove, charBRemove), charEnd =
						(char) Math.max(charARemove, charBRemove);
				for (char i = charStart; i <= charEnd; i++) {
					this.charset.remove(i);
					matcher.removeChar(i);
				}
			}
		} else {
			throw new IOException(INCORRECT_REMOVE_MSG);
		}
	}

	private void add_cmd(String action) throws IOException {
		String[] parts = action.split(" ", 2);
		if (parts.length < 2) {
			throw new IOException(INCORRECT_ADD_MSG);
		}
		String specificAdd = parts[1];
		switch (specificAdd) {
			case ALL_MSG:
				for (char i = FIRST_ASCII_INDEX; i < AFTER_LAST_ASCII_INDEX; i++) {
					this.charset.remove(i);
					matcher.addChar(i);
				}
				return;
			case SPACE_MSG:
				this.charset.add(SPACE_CHAR);
				matcher.addChar(SPACE_CHAR);
				return;
		}

		if (specificAdd.length() == 1) {
			char charToAdd = specificAdd.charAt(0);
			if (charToAdd > FIRST_ASCII_INDEX && charToAdd < AFTER_LAST_ASCII_INDEX) {
				this.charset.add(specificAdd.charAt(FIRST_INDEX));
				matcher.addChar(charToAdd);
				return;
			}
		}

		if (specificAdd.charAt(1) == HYPHEN_CHAR) {
			char charAAdd = specificAdd.charAt(0);
			char charBAdd = specificAdd.charAt(2);
			if (charAAdd > FIRST_ASCII_INDEX &&
					charAAdd < AFTER_LAST_ASCII_INDEX &&
					charBAdd > FIRST_ASCII_INDEX &&
					charBAdd < AFTER_LAST_ASCII_INDEX) {
				char charStart = (char) Math.min(charAAdd, charBAdd), charEnd =
						(char) Math.max(charAAdd, charBAdd);
				for (char i = charStart; i <= charEnd; i++) {
					this.charset.add(i);
					matcher.addChar(i);
				}
			}
		} else {
			throw new IOException(INCORRECT_ADD_MSG);
		}
	}

	private void chars_cmd() {
		if (!charset.isEmpty()) {
			for (char c : this.charset) {
				System.out.print(c + " ");
			}
			System.out.println();
		}
	}

	private void resCmd(String action) throws IOException {
		String param = action.substring(AFTER_RES_INDEX).trim();

		if (param.isEmpty()) {
			System.out.println("Resolution set to " + res + ".");
			return;
		}

		int newRes;
		if (param.startsWith("up")) {
			newRes = res * 2;
		} else if (param.startsWith("down")) {
			newRes = res / 2;
		} else {
			throw new IOException(INCORRECT_REMOVE_MSG);
		}

		if (newRes < minCharsInRow || newRes > maxCharsInRow) {
			throw new IOException(INCORRECT_REMOVE_MSG);
		} else {
			res = newRes;
			asciiAlgoDirty = true;
			System.out.println("Resolution set to " + res + ".");
		}
	}

	private void roundCmd(String action) throws IOException {
		String param = action.substring(ROUND_MSG.length()).trim();
		switch (param) {
			case "up" -> {
				this.roundingMode = RoundingMode.UP;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			case "down" -> {
				this.roundingMode = RoundingMode.DOWN;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			case "abs" -> {
				this.roundingMode = RoundingMode.NEAREST;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			default -> {
				throw new IOException(INCORRECT_ROUNDING_MODE_MSG);
			}
		}

	}

	private void outputCmd(String action) throws IOException {
		String output = action.substring(OUTPUT_MSG.length()).trim();
		if (output.startsWith("html")) {
			this.output = OUTPUT_OPTIONS[0];
		} else if (output.startsWith("console")) {
			this.output = OUTPUT_OPTIONS[1];
		} else {
			throw new IOException(INCORRECT_OUTPUT_MSG);
		}
	}

	private void asciiCmd() throws IOException {
		if (charset.size() < 2) {
			throw (new IOException(INCORRECT_ASCII_MSG));
		}
		char[] arrChar = toCharArray(charset);
		if (matcherDirty) {
			this.matcher = new SubImgCharMatcher(arrChar);
			matcher.setRoundingMode(this.roundingMode);
			matcherDirty = false;
			asciiAlgoDirty = true;
		}
		if (asciiAlgoDirty) {
			this.asciiAlgo = new AsciiArtAlgorithm(this.image, res, matcher);
			asciiAlgoDirty = false;
		}
		char[][] asciiPhoto = asciiAlgo.run();
		if (Objects.equals(output, "html")) {
			HtmlAsciiOutput htmlAsciiOutput = new HtmlAsciiOutput("output.html",
					"Courier New");
			htmlAsciiOutput.out(asciiPhoto);
		} else {
			ConsoleAsciiOutput consoleAsciiOutput = new ConsoleAsciiOutput();
			consoleAsciiOutput.out(asciiPhoto);
		}
	}

	private char[] toCharArray(Set<Character> set) {
		char[] a = new char[set.size()];
		int i = 0;
		for (Character c : set) {
			a[i++] = c;
		}
		return a;
	}

	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();
		if (args.length == 0) {
			System.out.println("Please provide an image file name.");
			return;
		}
		String imageName = args[0];
		shell.run(imageName);
		System.out.println("Goodbye!");
		System.exit(0);

	}
}
