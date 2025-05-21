package image_char_matching;

import java.util.*;


public class SubImgCharMatcher {
	private static final int NUM_SIZE_BOOL_ARR = 16*16;
	private Map<Character, Double> brightnessMap = new HashMap<>() {};
	private List<Character> charset= new ArrayList<>();
	private RoundingMode roundingMode;
	private final double minBrightness;
	private final double maxBrightness;

	//בנאי המקבל כפרמטר מערך של תווים שיהוו את סט התווים לשימוש האלגוריתם.
	public SubImgCharMatcher(char[] charset) {
		for (char c: charset){
			this.charset.add(c);
			this.brightnessMap.put(c, calcBrightness(c));
		}
		normalizingBrightness();
		this.minBrightness = Collections.min(this.brightnessMap.values());
		this.maxBrightness = Collections.max(this.brightnessMap.values());
	}
	public void setRoundingMode(RoundingMode mode) {
		this.roundingMode = mode;
	}

	public RoundingMode getRoundingMode() {
		return roundingMode;
	}


	//בהינתן ערך בהירות (brightness) של תת תמונה, המתודה תחזיר את התו מתוך סט התווים עם הבהירות הכי קרובה
	// בערך מוחלט לבהירות הנתונה. חשוב!!: אם יש מספר תווים מתוך הסט בעלי בהירות זהה יוחזר התו עם הערך
	// הASCII הנמוך ביניהם
	public char getCharByImageBrightness(double brightness){
		// check if brightness is in range
		double normalized = (brightness - minBrightness) / (maxBrightness - minBrightness);
		normalized = Math.max(0, Math.min(1, normalized));
        		// scale to the range of charset size
		double scaled = normalized * (charset.size() - 1);
		// round to the nearest index

		int idx = roundingMode.apply(scaled);
		// ensure index is within bounds

		idx = Math.max(0, Math.min(idx, charset.size() - 1));
		// return the character at the calculated index
		return charset.get(idx);
	}


	//מתודה שמוסיפה את התו c לסט התווים.
	public void addChar(char c){
		this.charset.add(c);
		this.brightnessMap.put(c, calcBrightness(c));
		normalizingBrightness();
	}


	//מתודה שמסירה את התו c מסט התווים.
	public void removeChar(char c){
		this.charset.remove(c);
		this.brightnessMap.remove(c);
		normalizingBrightness();
	}

	private double calcBrightness(char c){
		boolean[][] charToBool = CharConverter.convertToBoolArray(c);
		int trueCounter = 0;
		for (int i=0;i<charToBool.length;i++){
			for (int j=0; j<charToBool[i].length;j++){
				if (charToBool[i][j]){
					trueCounter++;
				}
			}
		}
		return (double) trueCounter /NUM_SIZE_BOOL_ARR;
	}

	private void normalizingBrightness(){
		double minVal = Collections.min(this.brightnessMap.values());
		double maxVal = Collections.max(this.brightnessMap.values());
		double maxMinusMin = maxVal-minVal;

		TreeMap<Character, Double> normalizedMap = new TreeMap<>();

		for (Map.Entry<Character, Double> entry : brightnessMap.entrySet()) {
			double newVal = (entry.getValue() - minVal) / maxMinusMin;
			normalizedMap.put(entry.getKey(), newVal);
		}
		this.brightnessMap = normalizedMap;
	}
}
