package org.ofbiz.designer.util;

import java.io.*;

public class ExecHelper { 
	String returnValue1 = "";
	String returnValue2 = "";

	private boolean processIsAlive = true;
	
	public String[] exec(String cmdLine) throws Exception{
		try{
			final Process proc = Runtime.getRuntime().exec(cmdLine);
			final DataInputStream in = new DataInputStream(proc.getInputStream());
			final DataInputStream err = new DataInputStream(proc.getErrorStream());
			Thread.sleep(1000); // give process some time to run
			
			// process destroyer thread
			new Thread(){
				public void run(){
					try{
						Thread.sleep(5000);
					} catch (Exception ee){	}
					System.err.print("destroying process ...");
					proc.destroy();
					System.err.println(" done");
					processIsAlive = false;
				}
			}.start();

			// process stdout reader thread
			new Thread(){
				public void run(){
					try{
						int size;
						byte[] buffer = new byte[1000];
						while ((size = in.read(buffer)) != 0)
							ExecHelper.this.returnValue1 += new String(buffer, 0, size);
					} catch (Exception e){}
				}
			}.start();
			
			// process stderr reader thread
			new Thread(){
				public void run(){
					try{
						int size;
						byte[] buffer = new byte[1000];
						while ((size = err.read(buffer)) != 0)
							ExecHelper.this.returnValue2 += new String(buffer, 0, size);
					} catch (Exception e){}
				}
			}.start();
			
		} catch (Exception e){
			return null;
		}
		
		while (processIsAlive){
			Thread.sleep(100);
		}
		
		String[] returnArr = {returnValue1, returnValue2};
		return returnArr;
	}
	
	public static void main(String[] args) throws Exception{
		//String cmdLine = "xcopy tmp tmp2\\  /S /Y /C";
		String cmdLine = "xcopy /y tmp tmp2\\  ";
		String values[] = new ExecHelper().exec(cmdLine);
		System.err.println("stdout is >" + values[0] + "<");
		System.err.println("stderr is >" + values[1] + "<");
		Thread.sleep(2000);
	}
}
