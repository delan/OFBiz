package org.ofbiz.designer.util;

public class SafeThread extends Thread{
	public static void sleep(long duration){
		try{
			Thread.sleep(duration);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Thread(){
			public void run(){
				LOG.println("about to sleep");
				SafeThread.sleep(1000);
				LOG.println("done");
			}
		}.start();
	}
}
