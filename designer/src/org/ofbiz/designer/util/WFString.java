package org.ofbiz.designer.util;

public class WFString
{
	private static final String LESS_THAN = "LESS_THAN";
	private static final String GREATER_THAN = "GREATER_THAN";
	private static final String AMPERSAND = "AMPERSAND";
	
	// The following methods remove and restore the "<" , ">" and  "&" symbols from strings with "safe" String equivalents.
	// This is so as not to confuse the xml parser library routines being used.
	
	public static String removeXMLSpecialCharacters(String test){
		while (true){
			int index1 = test.indexOf("<"); 
			if (index1 != -1)
				test = test.substring(0, index1) + LESS_THAN + test.substring(index1+1, test.length());
			
			int index2 = test.indexOf(">"); 
			if (index2 != -1)
				test = test.substring(0, index2) + GREATER_THAN + test.substring(index2+1, test.length());

			int index3 = test.indexOf("&"); 
			if (index3 != -1)
				test = test.substring(0, index3) + AMPERSAND + test.substring(index3+1, test.length());
			
			if (index1 == -1 && index2 == -1 && index3 == -1)
				break;
		}
		return test;
	}

	public static String restoreXMLSpecialCharacters(String test){
		while (true){
			int index1 = test.indexOf(LESS_THAN); 
			if (index1 != -1)
				test = test.substring(0, index1) + "<" + test.substring(index1+LESS_THAN.length(), test.length());
			
			int index2 = test.indexOf(GREATER_THAN);
			if (index2 != -1)
				test = test.substring(0, index2) + ">" + test.substring(index2+GREATER_THAN.length(), test.length());
			
			int index3 = test.indexOf(AMPERSAND);
			if (index3 != -1)
				test = test.substring(0, index3) + "&" + test.substring(index3+AMPERSAND.length(), test.length());
			
			if (index1 == -1 && index2 == -1 && index3 == -1)
				break;
		}
		return test;
	}
	
	private static final String SPACE = "_SPACE_";
	public static String space2underscore(Object xObj){
		String x = xObj.toString();
		while (x.indexOf(" ") != -1){
			int index = x.indexOf(" ");
			x = x.substring(0, index) + SPACE + x.substring(index+1, x.length());
		}
		return x;
	}

	public static String underscore2space(Object  xObj){
		String x = xObj.toString();
		while (x.indexOf(SPACE) != -1){
			int index = x.indexOf(SPACE);
			x = x.substring(0, index) + " " + x.substring(index+SPACE.length(), x.length());
		}
		return x;
	}

	public static void main(String[] args){
	}
}
