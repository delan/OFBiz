package org.ofbiz.designer.util;

//import workflow.*;
import java.util.*;

public class Debug{
	static String DEBUG = System.getProperty("DEBUG");
	static String PRINTONLY = System.getProperty("PRINTONLY");
	static String DONOTPRINT = System.getProperty("DONOTPRINT");
	
	// this method needs to be fed the top level org.ofbiz.designer.task
	// and it will print out the status of all tasks with alternative tasks specified

	public static void main(String[] args){
		String big = "hello, hi, there";
		StringTokenizer stk = new StringTokenizer(big, ",", false);
		while (stk.hasMoreTokens()){
			System.err.println(">" + stk.nextToken().trim() + "<");
		}
   }						   
	
	public static String print(String msg){
		if (	DEBUG == null && PRINTONLY==null && DONOTPRINT==null)
			return null;
		
		if  (PRINTONLY != null){
			HashSet printonly = new HashSet();
			StringTokenizer stk = new StringTokenizer(PRINTONLY);
			while (stk.hasMoreTokens())
				printonly.add(stk.nextToken().trim());

			Iterator it = printonly.iterator();
			boolean inPrintOnlyList = false;
			while (it.hasNext()){
				String next = (String)it.next();
				if (msg.indexOf(next) != -1){
					inPrintOnlyList = true;
					break;
				}
			}
			if (!inPrintOnlyList)
				return null;
		}
		
		if  (DONOTPRINT != null){
			HashSet donotprint = new HashSet();
			StringTokenizer stk = new StringTokenizer(DONOTPRINT);
			while (stk.hasMoreTokens())
				donotprint.add(stk.nextToken().trim());

			Iterator it = donotprint.iterator();
			while (it.hasNext()){
				String next = (String)it.next();
				if (msg.indexOf(next) != -1)
					return null;
			}
		}
		System.err.print(msg);		
		return msg;
	}

	public static  void println(String msg){
		if (print(msg)!= null)
			System.err.println();
	}

	public static  void print(int msg){
		print("" + msg);
	}

	public static  void println(int msg){
		println("" + msg);
	}

	public static  void print(long msg){
		print("" + msg);
	}

	public static  void println(long msg){
		println("" + msg);
	}

	public static  void print(Object msg){
		// TBD special handling for array classes
		print("" + msg);
	}

	public static  void println(Object msg){
		println("" + msg);
	}

	public static  void print(boolean msg){
		print("" + msg);
	}

	public static  void println(boolean msg){
		println("" + msg);
	}

	public static void println(){
		//println("");
	}
}
