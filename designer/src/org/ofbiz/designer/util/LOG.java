package org.ofbiz.designer.util;

import java.io.*;
import java.util.*;

//
//      IMPORTANT NOTE
//      This class currently works correctly only when JVM is invoked with "-nojit option"
//

/**
 This class provides some debugging and execution tracing related functions.

 public static String mMethod(); // returns the name of the calling method
 public static String mClass(); // returns the name of the calling class
 public static void print(); // calls print("")
 public static void print(String msg); // prefixes class and method info to msg and prints to stderr
 public static String msg(); // calls msg("")
 public static String msg(String msg); // returns msg with class and method info prefixed
 public static void println(); // calls println("")
 public static void println(String msg); // print followed by CR

 */

public class LOG {
	static boolean print = true;
	static boolean noinfo = false;
	static Vector printables = null;
	static {
		String noPrint = System.getProperty("NOPRINT");
		noinfo = (System.getProperty("NOINFO") != null);
		System.err.println("noinfo is " + noinfo);
		String printOnly = System.getProperty("PRINTONLY");
		if (printOnly != null){
			printables = new Vector();
			StringTokenizer stk = new StringTokenizer(printOnly);
			while (stk.hasMoreTokens())
				printables.addElement(stk.nextToken());
		}
		print = (noPrint == null);
	}
	
	private static final String LOG = "[LOG] ";
	private static final String UNKNOWN = " UNKNOWN ";
	private static final int BUFFERSIZE = 10000;
	private static String mMethod() {
		try{
			Throwable e = new Throwable();
			int buflength = BUFFERSIZE;
			byte[] buf = new byte[buflength];
			DataOutputStreamEx ds = new DataOutputStreamEx(buf, 0);
			PrintWriter pw = new PrintWriter(ds);
			try{
				e.printStackTrace(pw);
			} catch (Exception e2){
				// do nothing
			}
			pw.flush();
			//StreamTokenizer st = new StreamTokenizer(new ByteArrayInputStream(buf));
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf))));
			int tokenIndex = 0;
			while (true){
				int tokentype = st.nextToken();
				System.err.println("tokenval is " + st.sval);
				if (tokentype == st.TT_EOF) return UNKNOWN;
				if (tokentype == st.TT_WORD && st.lineno() == 5 && ++tokenIndex == 2) {
					String target = st.sval;
					StringTokenizer stk = new StringTokenizer(target, ".");
					String result = UNKNOWN;
					while (stk.hasMoreTokens()) result = stk.nextToken();
					return result;
				}
			}
		} catch (Exception e){
			return UNKNOWN;
		}
	}

	private static int stackDepth() {
		int depth = -4;
		try{
			Throwable e = new Throwable();
			int buflength = BUFFERSIZE;
			byte[] buf = new byte[buflength];
			DataOutputStreamEx ds = new DataOutputStreamEx(buf, 0);
			PrintWriter pw = new PrintWriter(ds);
			try{
				e.printStackTrace(pw);
			} catch (Exception e2){
				// do nothing
			}
			pw.flush();
			//DataInputStream di = new DataInputStream( new ByteArrayInputStream(buf));
			BufferedReader di = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
			int tokenIndex = 0;
			String stackLevel = null;
			while (true){
				stackLevel = di.readLine();
				if (stackLevel == null) return depth;
				depth++;
			}
		} catch (Exception e){
			return depth;
		}
	}

	private static String location() {
		Throwable e = null;
		try{
			e = new Throwable();
			int buflength = BUFFERSIZE;
			byte[] buf = new byte[buflength];
			DataOutputStreamEx ds = new DataOutputStreamEx(buf, 0);
			PrintWriter pw = new PrintWriter(ds);
			try{
				e.printStackTrace(pw);
			} catch (Exception e2){
				// do nothing
			}
			pw.flush();
			//DataInputStream di = new DataInputStream(new ByteArrayInputStream(buf));
			BufferedReader di = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
			String returnValue = null;
			for (int i=0; i<4; i++){
				returnValue = di.readLine();
				if (returnValue == null || returnValue.trim().length() == 0) {
					i--;
					continue;
				} else {
				}
			}
			return returnValue.trim().substring(3);

		} catch (Exception ee){
			ee.printStackTrace();
			return UNKNOWN;
		}
	}

	private static String mClass() {
		try{
			Throwable e = new Throwable();
			int buflength = BUFFERSIZE;
			byte[] buf = new byte[buflength];
			DataOutputStreamEx ds = new DataOutputStreamEx(buf, 0);
			PrintWriter pw = new PrintWriter(ds);
			try{
				e.printStackTrace(pw);
			} catch (Exception e2){
				// do nothing
			}
			pw.flush();
			//StreamTokenizer st = new StreamTokenizer(new ByteArrayInputStream(buf));
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf))));
			int tokenIndex = 0;
			boolean firstAt = false;
			while (true){
				int tokentype = st.nextToken();
				if (tokentype == st.TT_EOF) return UNKNOWN;
				
				if (tokentype == st.TT_WORD && st.lineno() == 4 && ++tokenIndex == 2){
					String target = st.sval;
					StringTokenizer stk = new StringTokenizer(target, ".");
					String result = UNKNOWN;
					String prev = UNKNOWN;
					while (stk.hasMoreTokens()){
						prev = result;
						result = stk.nextToken();
					}
					return prev;
				}
			}
		} catch (Exception e){
			return UNKNOWN;
		}
	}

	private static boolean isPrintable(String msg){
		if (printables == null) return true;
		for (int i=0; i<printables.size(); i++)
			if (msg.indexOf((String)printables.elementAt(i)) != -1) return true;
		return false;
	}
	
	public static synchronized void print(String msg){
		if (!print || !isPrintable(msg)) return;
		if (noinfo) System.err.print(msg);
		else System.err.print(msg + " - " + location() + "[" + stackDepth() + "]");
	}

	public static synchronized void println(){
		if (!print) return;
		if (noinfo) System.err.println();
		else System.err.print(location() + "[" + stackDepth() + "]\n");
	}

	public static synchronized void println(String msg){
		if (!print || !isPrintable(msg)) return;
		if (noinfo) System.err.println(msg);
		else System.err.print(msg + " - " + location() + "[" + stackDepth() + "]\n");
	}

	public static synchronized void println(int msgInt){
		if (!print || !isPrintable("" + msgInt)) return;
		if (noinfo) System.err.println(msgInt);
		else System.err.print("" + msgInt + " - " + location() + "[" + stackDepth() + "]\n");
	}
	
	public static synchronized String msg(){
		if (noinfo) return "";
		return location() + "[" + stackDepth() + "]";
	}

	public static synchronized String msg(String msg){
		if (noinfo) return msg;
		return msg + " - " + location() + "[" + stackDepth() + "]";
	}

	public static void main(String[] args){
		org.ofbiz.designer.util.LOG.println("hello");
	}
}


