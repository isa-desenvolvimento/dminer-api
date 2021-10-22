package com.dminer.utils;

import java.util.regex.Pattern;

public class UtilNumbers {
    
    public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if (strNum == null) {
			return false; 
		}
		return pattern.matcher(strNum).matches();
	}
    
}
