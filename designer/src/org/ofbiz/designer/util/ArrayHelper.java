// This file has been round trip engineered using Modelistic (www.modelistic.com)
// Edit Banner.txt in the installation directory to customize this message
// 
package org.ofbiz.designer.util;

import java.util.*;

public class ArrayHelper
{
	public static void main(String[] args)	{
		String[] arr1 = {"hello", "hi"};
		String[] arr2 = {"hello", "there"};
		Object[] result = subtract(arr1, arr2);
		System.err.println("result.length is " + result.length);
	}
	
	public static Object[] subtract(Object[] arr1, Object[] arr2){
		if (arr1 == null) return null;
		if (arr2 == null) return arr1;
				Vector temp = new Vector();
iloop:
		for (int i=0; i<arr1.length; i++){
			for (int j=0; j<arr2.length; j++)
				if (arr1[i].equals(arr2[j])) continue iloop;
			temp.add(arr1[i]);
		}
		return temp.toArray();
	}
}