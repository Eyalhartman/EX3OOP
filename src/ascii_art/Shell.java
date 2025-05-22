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
/**
 * The {@code Shell} class provides an interactive command-line interface for users
 * to control an ASCII art generation application. It supports image loading, character
 * set management, resolution control, output selection (HTML or console), and
 * rounding mode configuration.
 *
 * <p>
 * Users interact by entering commands such as:
 * <ul>
 *   <li>{@code add [char|range|all|space]} - add characters to the active charset</li>
 *   <li>{@code remove [char|range|all|space]} - remove characters from the charset</li>
 *   <li>{@code chars} - display the current character set</li>
 *   <li>{@code res up/down} - increase or decrease image resolution</li>
 *   <li>{@code output html/console} - select the output method</li>
 *   <li>{@code asciiArt} - generate and display the ASCII art</li>
 *   <li>{@code round up/down/abs} - change rounding strategy</li>
 * </ul>
 *
 * The shell maintains internal consistency by marking its matcher and algorithm as "dirty"
 * when configuration changes, and reinitializes them when needed.
 * </p>
 *
 *
 * @author Eyal and Dana
 */
public class Shell {


	public static final String GOODBYE_MSG = "Goodbye!";
	private static final String EXIT_MSG = "exit";
	private static final String CHARS_MSG = "chars";
	private static final Character[] DEFAULT_CHAR_SET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final int DEFAULT_RES = 2;
	private static final String ADD_MSG = "add";
	private static final char SPACE_CHAR = ' ';
	private static final String ALL_MSG = "all";
	private static final int FIRST_ASCII_INDEX = 32;
	private static final int AFTER_LAST_ASCII_INDEX = 126;
	private static final String SPACE_MSG = "space";
	private static final char HYPHEN_CHAR = '-';
	private static final String INCORRECT_ADD_MSG = "Did not add due to incorrect format.";
	private static final String REMOVE_MSG = "remove";
	private static final String INCORRECT_REMOVE_MSG = "Did not remove due to incorrect format.";
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
	private static final int MIN_NUM_ONE = 1;
	private static final String SPLIT_STRING = " ";
	private static final int PARTS_LENGTH = 2;
	private static final int CHAR_LENGTH = 1;
	private static final int INDEX_FIRST_CHAR = 0;
	private static final int INDEX_SECOND_CHAR_RANGE = 2;
	private static final int INDEX_HYPHEN_CHAR = 1;
	private static final String UP_MSG = "up";
	private static final String DOWN_MSG = "down";
	public static final String ABS_MSG = "abs";
	public static final String HTML_MSG = "html";
	public static final String CONSOLE_MSG = "console";
	public static final String IMAGE_FILE_NAME_MSG = "Please provide an image file name.";
	public static final String OUTPUT_HTML_FILENAME = "output.html";
	public static final String FONT_NAME = "Courier New";
	public static final String CMD_MSG = ">>> ";
	public static final int NEW_RES_FACTOR = 2;


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

	/**
	 * Constructs a Shell instance with default character set and resolution.
	 */
	public Shell() {
		this.charset = new TreeSet<>(Arrays.asList(DEFAULT_CHAR_SET));
		this.res = DEFAULT_RES;
		this.roundingMode = RoundingMode.NEAREST;
		char[] arrChar = toCharArray(charset);
		this.matcher = new SubImgCharMatcher(arrChar);
	}


	/**
	 * Launches the interactive shell and processes user commands until "exit" is received.
	 *
	 * @param imageName the path to the image to be processed
	 * @throws IOException if invalid commands are entered or if image loading fails
	 */
	public void run(String imageName) throws IOException {
		extractImg(imageName);

		while (true) {
			try {
				System.out.print(CMD_MSG);
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
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	/**
	 * Loads the image from the given file name and sets initial resolution bounds.
	 *
	 * @param imageName the name/path of the image file
	 * @throws IOException if the image cannot be loaded
	 */
	private void extractImg(String imageName) throws IOException {
		this.image = new Image(imageName);
		asciiAlgoDirty = true;
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		this.maxCharsInRow = imgWidth;
		this.minCharsInRow = Math.max(MIN_NUM_ONE, imgWidth / imgHeight);
		this.res = DEFAULT_RES;
	}

	/**
	 * Parses and executes a character removal command.
	 *
	 * @param action the full user input starting with "remove"
	 * @throws IOException if the input format is invalid
	 */
	private void remove_cmd(String action) throws IOException {

		String[] parts = action.split(SPLIT_STRING);
		if (parts.length < PARTS_LENGTH) {
			throw new IOException(INCORRECT_REMOVE_MSG);
		}
		String specificRemove = parts[CHAR_LENGTH];

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

		if (specificRemove.length() == CHAR_LENGTH) {
			char charToRemove = specificRemove.charAt(INDEX_FIRST_CHAR);
			if (charToRemove > FIRST_ASCII_INDEX && charToRemove < AFTER_LAST_ASCII_INDEX) {
				this.charset.remove(specificRemove.charAt(INDEX_FIRST_CHAR));
				matcher.removeChar(specificRemove.charAt(INDEX_FIRST_CHAR));
			}
			return;
		}

		if (specificRemove.charAt(INDEX_HYPHEN_CHAR) == HYPHEN_CHAR) {
			char charARemove = specificRemove.charAt(INDEX_FIRST_CHAR);
			char charBRemove = specificRemove.charAt(INDEX_SECOND_CHAR_RANGE);
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

	/**
	 * Parses and executes a character addition command.
	 *
	 * @param action the full user input starting with "add"
	 * @throws IOException if the input format is invalid
	 */
	private void add_cmd(String action) throws IOException {
		String[] parts = action.split(SPLIT_STRING);
		if (parts.length < PARTS_LENGTH) {
			throw new IOException(INCORRECT_ADD_MSG);
		}
		String specificAdd = parts[CHAR_LENGTH];
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

		if (specificAdd.length() == CHAR_LENGTH) {
			char charToAdd = specificAdd.charAt(INDEX_FIRST_CHAR);
			if (charToAdd > FIRST_ASCII_INDEX && charToAdd < AFTER_LAST_ASCII_INDEX) {
				this.charset.add(charToAdd);
				matcher.addChar(charToAdd);
				return;
			}
		}

		if (specificAdd.charAt(INDEX_HYPHEN_CHAR) == HYPHEN_CHAR) {
			char charAAdd = specificAdd.charAt(INDEX_FIRST_CHAR);
			char charBAdd = specificAdd.charAt(INDEX_SECOND_CHAR_RANGE);
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

	/**
	 * Prints the current character set in ASCII order.
	 */
	private void chars_cmd() {
		if (!charset.isEmpty()) {
			for (char c : this.charset) {
				System.out.print(c + SPLIT_STRING);
			}
			System.out.println();
		}
	}

	/**
	 * Parses and applies a resolution change command ("res up" or "res down").
	 *
	 * @param action the full command input
	 * @throws IOException if the format is invalid or bounds are exceeded
	 */
	private void resCmd(String action) throws IOException {
		String param = action.substring(AFTER_RES_INDEX).trim();

		if (param.isEmpty()) {
			System.out.println("Resolution set to " + res + ".");
			return;
		}

		int newRes;
		if (param.startsWith(UP_MSG)) {
			newRes = res * NEW_RES_FACTOR;
		} else if (param.startsWith(DOWN_MSG)) {
			newRes = res / NEW_RES_FACTOR;
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

	/**
	 * Sets the rounding mode used when mapping brightness to characters.
	 *
	 * @param action the full input string (e.g., "round up")
	 * @throws IOException if the rounding mode is unrecognized
	 */
	private void roundCmd(String action) throws IOException {
		String param = action.substring(ROUND_MSG.length()).trim();
		switch (param) {
			case UP_MSG -> {
				this.roundingMode = RoundingMode.UP;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			case DOWN_MSG -> {
				this.roundingMode = RoundingMode.DOWN;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			case ABS_MSG -> {
				this.roundingMode = RoundingMode.NEAREST;
				if (matcher != null) matcher.setRoundingMode(this.roundingMode);
			}
			default -> {
				throw new IOException(INCORRECT_ROUNDING_MODE_MSG);
			}
		}

	}

	/**
	 * Sets the output method (console or HTML) for ASCII rendering.
	 *
	 * @param action the full command input (e.g., "output html")
	 * @throws IOException if the input format is invalid
	 */
	private void outputCmd(String action) throws IOException {
		String output = action.substring(OUTPUT_MSG.length()).trim();
		if (output.startsWith(HTML_MSG)) {
			this.output = OUTPUT_OPTIONS[INDEX_FIRST_CHAR];
		} else if (output.startsWith(CONSOLE_MSG)) {
			this.output = OUTPUT_OPTIONS[1];
		} else {
			throw new IOException(INCORRECT_OUTPUT_MSG);
		}
	}

	/**
	 * Executes the ASCII art generation pipeline.
	 *
	 * @throws IOException if the character set is too small or setup is incomplete
	 */
	private void asciiCmd() throws IOException {
		if (charset.size() < PARTS_LENGTH) {
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
		if (Objects.equals(output, HTML_MSG)) {
			HtmlAsciiOutput htmlAsciiOutput = new HtmlAsciiOutput(OUTPUT_HTML_FILENAME,
					FONT_NAME);
			htmlAsciiOutput.out(asciiPhoto);
		} else {
			ConsoleAsciiOutput consoleAsciiOutput = new ConsoleAsciiOutput();
			consoleAsciiOutput.out(asciiPhoto);
		}
	}

	/**
	 * Converts a character set to a primitive char array.
	 *
	 * @param set the set of characters
	 * @return the equivalent char array
	 */
	private char[] toCharArray(Set<Character> set) {
		char[] a = new char[set.size()];
		int i = INDEX_FIRST_CHAR;
		for (Character c : set) {
			a[i++] = c;
		}
		return a;
	}

	/**
	 * Entry point for the ASCII art shell program.
	 * Expects the image file path as the first command-line argument.
	 *
	 * @param args command-line arguments
	 * @throws IOException if image loading or shell execution fails
	 */
	public static void main(String[] args) throws IOException {
		Shell shell = new Shell();
		if (args.length == 0) {
			System.out.println(IMAGE_FILE_NAME_MSG);
			return;
		}
		String imageName = args[INDEX_FIRST_CHAR];
		shell.run(imageName);
		System.out.println(GOODBYE_MSG);
		System.exit(0);


	}
}
