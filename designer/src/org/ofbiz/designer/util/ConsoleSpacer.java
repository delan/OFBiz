package org.ofbiz.designer.util;

import java.io.*;

public class ConsoleSpacer{
	static Thread thread = null;
	
	public static void init(){
		if (thread != null)
			return;
		thread = new Thread(){
			public void run(){
				while (true)
					try{ System.in.read();} catch (IOException e){}
			}
		};
		thread.start();
	}
}
