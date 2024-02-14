package fr.innog.utils;

public class StringUtils {

	
	public static String extractString(String str, char startChar, char endChar)
	{
		return extractString(str, startChar, endChar,0);
	}
	
	public static String extractString(String str, char startChar, char endChar, int startAfterIndex)
	{
		String string = "";
		boolean start = false;
		
		for(int i = startAfterIndex; i < str.length(); i++)
		{
			char character = str.charAt(i);
			
			if(character == startChar)
			{
				start = true;
			}
			
			if(start)
			{
				string += character;
				
				
				if(character == endChar)
				{
					return string;
				}
			}
		}
		return "";
	}
	
	public static String getLeftTimeDisplay(int leftTime)
	{
		
		if(leftTime < 0)
		{
			leftTime = 0;
		}
		
		int hours = (int) (leftTime / 60 / 60); 
		int minutes = (int) (leftTime / 60) % 60;
		int seconds = (int)(leftTime % 60);


		String hoursStr = hours + "";
		if(hours <= 9)
		{
			hoursStr = "0" + hours;
		}
		
		String minutesStr = minutes + "";

		if(minutes <= 9)
		{
			minutesStr = "0" + minutes;
		}

		String secondsStr = seconds + "";

		if(seconds <= 9)
		{
			secondsStr = "0" + seconds;
		}
		
		return hoursStr + ":" + minutesStr + ":" + secondsStr;

	}

	
}
