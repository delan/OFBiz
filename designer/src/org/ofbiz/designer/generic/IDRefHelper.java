package org.ofbiz.designer.generic;

import java.util.*;
import org.ofbiz.designer.util.*;

public class IDRefHelper { 
	public static String removeIDRef(String idrefList, String idref) {
		String returnString = "";
		for ( Enumeration e = new StringTokenizer( idrefList ); e.hasMoreElements(); ) {
			String temp = (String)e.nextElement();
			if (!temp.equals(idref))
				returnString += " " + temp;
		}
		if (returnString.trim().length() == 0)
			return null;
		else
			return returnString.trim();
	}

	public static String addIDRef(String idrefList, String idref) {
		for ( Enumeration e = new StringTokenizer( idrefList ); e.hasMoreElements(); ) {
			String temp = (String)e.nextElement();
			if (temp.equals(idref))
				return idrefList;
		}
		return idrefList.trim() + " " + idref.trim();
	}

	public static String[] getReferenceArray(String idrefList) {
		if (idrefList == null) return new String[0];
		int size = 0;
		String[] returnArray = null;
		if ( idrefList != null ) {
			for ( Enumeration e = new StringTokenizer( idrefList ); e.hasMoreElements(); ) {
				e.nextElement();
				size++;
			}

			returnArray = new String[size];
			int index = 0;
			for ( Enumeration e = new StringTokenizer( idrefList ); e.hasMoreElements(); )
				returnArray[index++] = (String)e.nextElement();
		}

		return returnArray;
	}

    public static boolean idrefsMatch(String idref1, String idref2) {
        if (idref1 == null && idref2 == null) return true;
        if (idref1 == null || idref2 == null) return false;
        String[] arr1 = getReferenceArray(idref1);
        String[] arr2 = getReferenceArray(idref2);
        if (arr1.length != arr2.length) return false;
        HashSet set1 = new HashSet();
        HashSet set2 = new HashSet();
        for (int i=0; i<arr1.length; i++) set1.add(arr1[i]);
        for (int i=0; i<arr2.length; i++) set2.add(arr2[i]);
        set1.removeAll(set2);
        if (set1.isEmpty()) return true;
        return false;
    }
}
