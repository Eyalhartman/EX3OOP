package image_char_matching;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static java.util.Collections.min;

public class SubImgCharMatcher {
	private TreeMap<Double, Character> treeMap = new TreeMap<>();
	private List<Character> charset= new ArrayList<>();

	//בנאי המקבל כפרמטר מערך של תווים שיהוו את סט התווים לשימוש האלגוריתם.
	public SubImgCharMatcher(char[] charset) {
		for (int i =0; i<charset.length;i++){
			this.charset.add(charset[i]);
		}
	}


	//בהינתן ערך בהירות (brightness) של תת תמונה, המתודה תחזיר את התו מתוך סט התווים עם הבהירות הכי קרובה
	// בערך מוחלט לבהירות הנתונה. חשוב!!: אם יש מספר תווים מתוך הסט בעלי בהירות זהה יוחזר התו עם הערך
	// הASCII הנמוך ביניהם
	public char getCharByImageBrightness(double brightness){
		Double floor = treeMap.floorKey(brightness);
		Double ceil = treeMap.ceilingKey(brightness);

		if (floor == null && ceil != null){
			return treeMap.get(ceil);
		}

		if (ceil == null && floor != null){
			return treeMap.get(floor);
		}

		if (Math.abs(brightness-floor) > Math.abs(ceil-brightness)){
			return treeMap.get(ceil);
		}
		if (Math.abs(brightness- floor)  < Math.abs(ceil-brightness)){
			return treeMap.get(floor);
		}
		return treeMap.get(Math.min(Math.abs(ceil), Math.abs(floor)));
	}


	//מתודה שמוסיפה את התו c לסט התווים.
	public void addChar(char c){
		this.charset.add(c);
	}


	//מתודה שמסירה את התו c מסט התווים.
	public void removeChar(char c){
		this.charset.remove(c);
	}
}
