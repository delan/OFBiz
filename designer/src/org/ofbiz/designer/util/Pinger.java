package org.ofbiz.designer.util;

import java.net.*;

// The Pinger object measures network latency by sending a packet
// to the UDP Echo Port (Port 7) and timing how long it takes.
// We use this port instead of ICMP to avoid native methods
public class Pinger implements Runnable { 
	static final int echoPort = 7;
	static final int maxPingTime = 3000;     // Milliseconds
	static final int pingPollInterval = 100; // Milliseconds

	DatagramSocket socket;
	InetAddress fromIP;
	long sendTime;
	long timeMeasured;
	Thread timeOutMonitor;
	Thread pingListenThread;
	byte packetNumber = 0;

	public Pinger(String pingee) throws UnknownHostException{ 		fromIP = InetAddress.getByName(pingee);
	}

	public long ping() {
		byte[] msg = new byte[1];
		msg[0] = ++packetNumber;
		timeMeasured = -1;

		if(socket == null) {
			try {
				socket = new DatagramSocket(); 
			} catch (Exception e) { 
				e.printStackTrace();				return -1;			}
		}
		if(pingListenThread == null) {
			pingListenThread = new Thread(this);
			pingListenThread.start();
		}

		DatagramPacket packet = new DatagramPacket(msg,msg.length,fromIP,echoPort);

		try {
			System.err.println ("pinging " + fromIP.getHostName());			sendTime = System.currentTimeMillis();
			long timeLimit = sendTime + maxPingTime;
			socket.send(packet);			while (System.currentTimeMillis() < timeLimit) {
				Thread.sleep(pingPollInterval);
				if(timeMeasured != -1) {					socket.close();							
					return timeMeasured;				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return timeMeasured;		}
		return timeMeasured; 
	}

	// Run method for the listener thread
	public void run() {
		byte[] repBuf = new byte[1];		DatagramPacket reply = new DatagramPacket(repBuf,repBuf.length);
		try {
			while (true) {				socket.receive(reply);				System.err.println("reply from " + fromIP.getHostName());
				if (repBuf[0] == packetNumber) {
					timeMeasured = System.currentTimeMillis() - sendTime;
					pingListenThread = null;
					return;
				}
			}
		} catch  	(Exception e) { 
			pingListenThread = null; 		}
	}
}