/*
 * $Id: ShipmentScaleApplet.java,v 1.1 2003/09/05 22:53:12 ajzeneski Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.shipment;

import javax.comm.*;

import java.io.*;
import java.util.TooManyListenersException;

/**
 * ShipmentScaleApplet - Applet for reading weight from a scale and input into the browser
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ShipmentScaleApplet implements SerialPortEventListener, CommPortOwnershipListener {
    
    private CommPortIdentifier portId = null;
    private SerialPort serialPort = null;
    private boolean portOpen = false;
    
    private InputStream in = null;
    private OutputStream out = null;
    
    public ShipmentScaleApplet() throws UnsupportedCommOperationException, IOException {
        try {
            portId = CommPortIdentifier.getPortIdentifier("COM1");
        } catch (NoSuchPortException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            serialPort = (SerialPort) portId.open("SerialScale", 30000);
        } catch (PortInUseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
               
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);                       
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);        
               
        in = serialPort.getInputStream();        
        out = serialPort.getOutputStream();
        
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                        
        serialPort.enableReceiveTimeout(30);                
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnBreakInterrupt(true); 
        portId.addPortOwnershipListener(this);  
        this.portOpen = true;
    }

    /* (non-Javadoc)
     * @see javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent)
     */
    public void serialEvent(SerialPortEvent event) {
        // Create a StringBuffer and int to receive input data.
        StringBuffer inputBuffer = new StringBuffer();
        int newData = 0;

        // Determine type of event.
        switch (event.getEventType()) {

            // Read data until -1 is returned. If \r is received substitute
            // \n for correct newline handling.
            case SerialPortEvent.DATA_AVAILABLE:
                while (newData != -1) {
                    try {
                        newData = in.read();
                    if (newData == -1) {
                    break;
                    }
                    if (newData != 32 && newData != 3) {
                        if ('\r' == (char)newData) {
                            inputBuffer.append('|');
                        } else if ('\n' == (char)newData) {
                            inputBuffer.append("");
                        } else {                              
                            inputBuffer.append((char)newData);
                        }
                        //inputBuffer.append("(" + newData + ")");
                    }                        
                    
                    } catch (IOException ex) {
                        System.err.println(ex);
                        return;
                    }
                }

            // Append received data to messageAreaIn.
            System.out.println(inputBuffer.toString());
            break;

            // If break event append BREAK RECEIVED message.
            case SerialPortEvent.BI:
            System.out.println("\n--- BREAK RECEIVED ---\n");
        }                
    }

    /* (non-Javadoc)
     * @see javax.comm.CommPortOwnershipListener#ownershipChange(int)
     */
    public void ownershipChange(int arg0) {
        // TODO Auto-generated method stub
        
    }
    
    public void sendMessage() throws IOException {
        String message = "W\r";
        char[] msgChars = message.toCharArray();
        for (int i = 0; i < msgChars.length; i++) {
            out.write((int)msgChars[i]);
        }
        out.flush();
        serialPort.sendBreak(1000);        
    }
    
    public void close() throws IOException {
        out.close();
        in.close();
        serialPort.close();        
    }
    
    public static void main(String args[]) throws Exception {
        ShipmentScaleApplet applet = new ShipmentScaleApplet();
        applet.sendMessage();
        applet.close();   
    }
}
