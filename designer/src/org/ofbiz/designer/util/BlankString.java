package org.ofbiz.designer.util;

public class BlankString {
	private String myString = null;

	public BlankString(int size){
		String temp = new String("");
		for (int i=0; i<2*size; i++) temp += " ";
		myString = temp;
	}

	public String toString(){
		return myString;
	}

	public void tabOut(){
		try{
			myString = myString.substring(2, myString.length());
		}
		catch (StringIndexOutOfBoundsException e){
			WARNING.println("\nWARNING : " + e.getMessage() + "\n");			
		}
	}

	public void tabIn(){
		myString += "  ";
	}
}