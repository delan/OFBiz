package org.ofbiz.designer.util;

import java.io.*;
import java.util.*;

class LaunchHelp{
	public static void main(String[] args) throws Exception{
		if (args.length == 0){
			System.err.print("fully qualified class name ? : ");
			byte[] buf = new byte[1000];
			//DataInputStream ds = new DataInputStream(System.in);
			BufferedReader ds = new BufferedReader(new InputStreamReader(System.in));
			args = new String[1];
			args[0] = ds.readLine();
			System.err.println("args[0] is >" + args[0] + "<");
		}
		java.util.StringTokenizer stk = new java.util.StringTokenizer(args[0], ".", false);
		String cmd = "explorer ";
		
		String fileName = "";
		
		while (stk.hasMoreTokens()){
			fileName += "\\" + stk.nextToken();
		}
		fileName += ".html";
		
		String[] values = { 
			"c:\\jdk1.3\\docs\\api",
			"c:\\jdk1.3\\docs\\api\\java\\lang",
			"c:\\jdk1.3\\docs\\api\\java\\lang\\reflect",
			"d:\\dxml\\doc\\api\\com\\objectspace\\xml",
			"c:\\jdk1.3\\docs\\api\\javax\\swing",
			"c:\\jdk1.3\\docs\\api\\javax\\swing\\text",
			"c:\\jdk1.3\\docs\\api\\javax\\swing\\event",
			"c:\\jdk1.3\\docs\\api\\java\\io",
			"c:\\jdk1.3\\docs\\api\\java\\net",
			"c:\\jdk1.3\\docs\\api\\java\\lang\\org.ofbiz.designer.util",
			"c:\\jdk1.3\\docs\\api\\java\\awt",
			"c:\\jdk1.3\\docs\\api\\java\\awt\\event"
		};

		int i=0;
		while (i<values.length){
			String fileBranch = values[i++];
			if (new File(fileBranch + fileName).exists()){
				cmd += fileBranch + fileName;
				break;
			}
		}
		System.err.println("cmd is " + cmd);
		Runtime.getRuntime().exec(cmd);
	}
}

class LaunchCode{
	public static void main(String[] args) throws Exception{
		if (args.length == 0){
			System.err.print("fully qualified class name ? : ");
			byte[] buf = new byte[1000];
			//DataInputStream ds = new DataInputStream(System.in);
			BufferedReader ds = new BufferedReader(new InputStreamReader(System.in));			
			args = new String[1];
			args[0] = ds.readLine();
			System.err.println("args[0] is >" + args[0] + "<");
		}
		java.util.StringTokenizer stk = new java.util.StringTokenizer(args[0], ".", false);
		//String cmd = "explorer ";
		String cmd = "c:\\Program Files\\Microsoft Visual Studio\\Common\\IDE\\IDE98\\devenv.exe ";
		String fileName = "";
		
		while (stk.hasMoreTokens()){
			fileName += "\\" + stk.nextToken();
		}
		fileName += ".java";
		
		String[] values = { 
			"c:\\jdk1.3\\src",
			"c:\\jdk1.3\\src\\java\\lang",
			"c:\\jdk1.3\\src\\java\\lang\\reflect",
			"d:\\dxml\\src\\com\\objectspace\\xml",
			"c:\\jdk1.3\\src\\javax\\swing",
			"c:\\jdk1.3\\src\\javax\\swing\\text",
			"c:\\jdk1.3\\src\\javax\\swing\\event",
			"c:\\jdk1.3\\src\\java\\io",
			"c:\\jdk1.3\\src\\java\\net",
			"c:\\jdk1.3\\src\\java\\lang\\org.ofbiz.designer.util",
			"c:\\jdk1.3\\src\\java\\awt",
			"c:\\jdk1.3\\src\\java\\awt\\event",
			"c:\\workflow\\org.ofbiz.designer.util",
			"c:\\workflow\\designer"
		};

		int i=0;
		while (i<values.length){
			String fileBranch = values[i++];
			if (new File(fileBranch + fileName).exists()){
				cmd += fileBranch + fileName;
				break;
			}
		}
		System.err.println("cmd is " + cmd);
		Runtime.getRuntime().exec(cmd);
	}
}
