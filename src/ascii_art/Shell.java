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
	private static final String ALL_ADD_MSG = "all";
	private static final int FIRST_INDEX = 0;
	private static final int AFTER_ADD_INDEX = 4;
	private static final int FIRST_ASCII_INDEX = 32;
	private static final int AFTER_LAST_ASCII_INDEX = 126;
	private static final String SPACE_MSG = "space";
	private static final char HYPHEN_CHAR = '-';
	private static final String INCORRECT_ADD_MSG = "Did not add due to incorrect format.";
	private static final String REMOVE_MSG = "remove";
	private static final String INCORRECT_REMOVE_MSG = "Did not remove due to incorrect format.";

	Set<Character> charset;
	int res;
	int counter=0;

	public Shell(){
		this.charset = new TreeSet<>(Arrays.asList(DEFULT_CHAR_SET));

		this.res = DEFULT_RES;
		counter+=DEFULT_CHAR_SET.length;
	}


	//todo add exceptions
	public void run(String imageName) {
		System.out.print(">>> ");
		String action = KeyboardInput.readLine();
		if (action.startsWith(CHARS_MSG)) {
			if (!charset.isEmpty()) {
				chars_cmd();
			}
		}
		if (action.startsWith(ADD_MSG)){
			add_cmd(action);
		}
		if (action.startsWith(REMOVE_MSG)){
			remove_cmd(action);
		}

		while (!(action.startsWith(EXIT_MSG))) {
			System.out.print(">>> ");
			action = KeyboardInput.readLine();
			if (action.startsWith(CHARS_MSG)) {
				if (!charset.isEmpty()) {
					chars_cmd();
				}
			}
			if (action.startsWith(ADD_MSG)){
				add_cmd(action);
			}
			if (action.startsWith(REMOVE_MSG)){
				remove_cmd(action);
			}

		}

	}

	private void remove_cmd(String action) {
		String specificRemove = action.substring(AFTER_ADD_INDEX);

		if (specificRemove.charAt(1)==SPACE_CHAR) {
			if (specificRemove.charAt(0) > FIRST_ASCII_INDEX && specificRemove.charAt(0) < AFTER_LAST_ASCII_INDEX) {
				this.charset.remove(specificRemove.charAt(FIRST_INDEX));
			}
		}
		if (specificRemove.startsWith(ALL_ADD_MSG)){
			for (char i = FIRST_ASCII_INDEX;i<AFTER_LAST_ASCII_INDEX;i++){
				this.charset.remove(i);
			}
		}
		if (specificRemove.startsWith(SPACE_MSG)){
			this.charset.remove(SPACE_CHAR);
		}
		if (specificRemove.charAt(1)== HYPHEN_CHAR){
			if (specificRemove.charAt(0)>FIRST_ASCII_INDEX &&
					specificRemove.charAt(0)<AFTER_LAST_ASCII_INDEX &&
					specificRemove.charAt(2)>FIRST_ASCII_INDEX &&
					specificRemove.charAt(2)<AFTER_LAST_ASCII_INDEX) {
				if (specificRemove.charAt(0) > specificRemove.charAt(2)) {
					for (char i = specificRemove.charAt(2); i < specificRemove.charAt(0) + 1; i++) {
						this.charset.remove(i);
					}
				} else {
					for (char i = specificRemove.charAt(0); i < specificRemove.charAt(2) + 1; i++) {
						this.charset.remove(i);
					}
				}
			}
		}
		else{
			System.out.println(INCORRECT_REMOVE_MSG);
		}
	}

	private void add_cmd(String action) {
		String specificAdd = action.substring(AFTER_ADD_INDEX);
		if (specificAdd.charAt(1)==SPACE_CHAR) {
			if (specificAdd.charAt(0) > FIRST_ASCII_INDEX && specificAdd.charAt(0) < AFTER_LAST_ASCII_INDEX) {
				this.charset.add(specificAdd.charAt(FIRST_INDEX));
			}
		}
		if (specificAdd.startsWith(ALL_ADD_MSG)){
			for (char i = FIRST_ASCII_INDEX;i<AFTER_LAST_ASCII_INDEX;i++){
				this.charset.add(i);
			}
		}
		if (specificAdd.startsWith(SPACE_MSG)){
			this.charset.add(SPACE_CHAR);
		}
		if (specificAdd.charAt(1)== HYPHEN_CHAR){
			if (specificAdd.charAt(0)>FIRST_ASCII_INDEX && specificAdd.charAt(0)<AFTER_LAST_ASCII_INDEX && specificAdd.charAt(2)>FIRST_ASCII_INDEX && specificAdd.charAt(2)<AFTER_LAST_ASCII_INDEX) {
				if (specificAdd.charAt(0) > specificAdd.charAt(2)) {
					for (char i = specificAdd.charAt(2); i < specificAdd.charAt(0) + 1; i++) {
						this.charset.add(i);
					}
				} else {
					for (char i = specificAdd.charAt(0); i < specificAdd.charAt(2) + 1; i++) {
						this.charset.add(i);
					}
				}
			}
		}
		else{
			System.out.println(INCORRECT_ADD_MSG);
		}
	}

	private void chars_cmd() {
		for (char c : this.charset) {
			System.out.print(c + " ");
		}
		System.out.println();
	}


	public static void main(String[] args) {

	}
}
