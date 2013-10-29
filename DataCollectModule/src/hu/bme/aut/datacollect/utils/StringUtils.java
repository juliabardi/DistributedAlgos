package hu.bme.aut.datacollect.utils;

public class StringUtils {

	private static final String EMPTY = "";
	
	public static String trimToNull(String input){
				
		if (input == null || EMPTY.equals(input.trim())){
			return null;
		}
		return input.trim();
	}
	
	public static boolean isEmpty(String input){
		
		return (input == null || EMPTY.equals(input));
	}
	
	public static boolean equals(String left, String right){
		
		return (left == null && right == null) || (left != null && left.equals(right));
	}
}
