package image_char_matching;

import java.util.TreeMap;

import static java.util.Collections.min;

public class SubImgCharMatcher {
	private TreeMap<Double, Character> treeMap = new TreeMap<>();
	private char[] charset;

	//בנאי המקבל כפרמטר מערך של תווים שיהוו את סט התווים לשימוש האלגוריתם.
	public SubImgCharMatcher(char[] charset) {
		this.charset = new char[charset.length];
		for (int i =0; i<charset.length;i++){
			this.charset[i] = charset[i];
		}
	}


	//בהינתן ערך בהירות (brightness) של תת תמונה, המתודה תחזיר את התו מתוך סט התווים עם הבהירות הכי קרובה
	// בערך מוחלט לבהירות הנתונה. חשוב!!: אם יש מספר תווים מתוך הסט בעלי בהירות זהה יוחזר התו עם הערך
	// הASCII הנמוך ביניהם
	public char getCharByImageBrightness(double brightness){
		double floorVal = treeMap.floorKey(brightness)- brightness;
		double ceilVal = brightness- treeMap.ceilingKey(brightness);

		if (floorVal > ceilVal){
			return treeMap.get(ceilVal);
		}
		if (floorVal < ceilVal){
			return treeMap.get(floorVal);
		}
		return treeMap.get(Math.min(ceilVal, floorVal));
	}


	//מתודה שמוסיפה את התו c לסט התווים.
	public void addChar(char c){

	}


	//מתודה שמסירה את התו c מסט התווים.
	public void removeChar(char c){

	}
}
