package org.ofbiz.designer.util;

import java.util.*;
							
public class MyCollections {
	public static void sort(Vector vec, Comparator comparator){
		ArrayList arrayList = new ArrayList(vec);
		Collections.sort(arrayList, comparator);
		vec.removeAllElements();
		for (int i=0; i<arrayList.size(); i++)
			vec.addAll(arrayList);
	}
	
	public static void sort(Vector vec){
		ArrayList arrayList = new ArrayList(vec);
		Collections.sort(arrayList);
	}
	
	public static void removeRepetitions(Vector vec){
		for (int i=0; i<vec.size(); i++)
			for (int j=i+1; j<vec.size(); j++)
				if (vec.elementAt(i).equals(vec.elementAt(j)))
					vec.removeElementAt(j--);
	}
}
