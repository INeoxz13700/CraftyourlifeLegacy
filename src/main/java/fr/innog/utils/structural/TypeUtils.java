package fr.innog.utils.structural;

public class TypeUtils {

	public static boolean isBoolean(String value)
	{
		return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equals("1") || value.equals("0");
	}
	
}
