package ascii_art;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Shell {


	private static final String EXIT_MSG = "exit";
	private static final String CHARS_MSG = "chars ";
	private static final Character[] DEFULT_CHAR_SET = {'0','1','2','3','4','5','6','7','8','9'};
	private static final int DEFULT_RES = 2;
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
	private static final int MAX_SIZE_SPLIT_ARR = 2;

	Set<Character> charset;
	int res;

	public Shell(){
		this.charset = new TreeSet<>(Arrays.asList(DEFULT_CHAR_SET));
		this.res = DEFULT_RES;
	}


	//todo add exceptions
	public void run(String imageName) {

		while (true) {
			System.out.print(">>> ");
			String action = KeyboardInput.readLine();

			if (action.startsWith(EXIT_MSG))  break;

			if (action.startsWith(CHARS_MSG))  chars_cmd();
			if (action.startsWith(ADD_MSG)) add_cmd(action);
			if (action.startsWith(REMOVE_MSG)) remove_cmd(action);

		}

	}

	private void remove_cmd(String action) {

		String[] parts = action.split(" ");
		if (parts.length < 2) {
			System.out.println(INCORRECT_ADD_MSG);
			return;
		}
		String specificRemove = parts[1];

		switch (specificRemove) {
			case ALL_MSG :
				for (char i = FIRST_ASCII_INDEX; i < AFTER_LAST_ASCII_INDEX; i++)  this.charset.remove(i);
				return;
			case SPACE_MSG:
				this.charset.remove(SPACE_CHAR);
				return;
		}

		if (specificRemove.length()== 1) {
			char charToRemove = specificRemove.charAt(0);
			if ( charToRemove> FIRST_ASCII_INDEX && charToRemove < AFTER_LAST_ASCII_INDEX) {
				this.charset.remove(specificRemove.charAt(FIRST_INDEX));
			}
		}

		if (specificRemove.charAt(1)== HYPHEN_CHAR){
			char charARemove = specificRemove.charAt(0);
			char charBRemove = specificRemove.charAt(2);
			if (charARemove>FIRST_ASCII_INDEX &&
					charARemove<AFTER_LAST_ASCII_INDEX &&
					charBRemove>FIRST_ASCII_INDEX &&
					charBRemove<AFTER_LAST_ASCII_INDEX) {
				char charStart = (char)Math.min(charARemove, charBRemove), charEnd =
						(char)Math.max(charARemove, charBRemove);
				for (char i = charStart; i<=charEnd;i++) this.charset.remove(i);
			}
		}
		else{
			System.out.println(INCORRECT_REMOVE_MSG);
		}
	}

	private void add_cmd(String action) {
		String[] parts = action.split(" ", 2);
		if (parts.length < 2) {
			System.out.println(INCORRECT_ADD_MSG);
			return;
		}
		String specificAdd = parts[1];
		switch (specificAdd) {
			case ALL_MSG :
				for (char i = FIRST_ASCII_INDEX; i < AFTER_LAST_ASCII_INDEX; i++)  this.charset.remove(i);
				return;
			case SPACE_MSG:
				this.charset.remove(SPACE_CHAR);
				return;
		}

		if (specificAdd.length()== 1) {
			char charToRemove = specificAdd.charAt(0);
			if ( charToRemove> FIRST_ASCII_INDEX && charToRemove < AFTER_LAST_ASCII_INDEX) {
				this.charset.remove(specificAdd.charAt(FIRST_INDEX));
			}
		}

		if (specificAdd.charAt(1)== HYPHEN_CHAR){
			char charARemove = specificAdd.charAt(0);
			char charBRemove = specificAdd.charAt(2);
			if (charARemove>FIRST_ASCII_INDEX &&
					charARemove<AFTER_LAST_ASCII_INDEX &&
					charBRemove>FIRST_ASCII_INDEX &&
					charBRemove<AFTER_LAST_ASCII_INDEX) {
				char charStart = (char)Math.min(charARemove, charBRemove), charEnd =
						(char)Math.max(charARemove, charBRemove);
				for (char i = charStart; i<=charEnd;i++) this.charset.remove(i);
			}
		}
		else{
			System.out.println(INCORRECT_ADD_MSG);
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


	public static void main(String[] args) {

	}
}
