/*
 * $Id: Start.java,v 1.3 2003/08/17 03:11:29 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.base.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Start - OFBiz Container(s) Startup Class
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.3 $
 * @since      2.1
 */
public class Start implements Runnable {

    public static final String CONFIG_FILE = "org/ofbiz/base/start/start.properties";
    public static final String SHUTDOWN_COMMAND = "SHUTDOWN";
    public static final String STATUS_COMMAND = "STATUS";
    private static final Config config = new Config();
                        
    private Classpath classPath = new Classpath(System.getProperty("java.class.path"));     
    private ServerSocket serverSocket = null;
    private Thread serverThread = null;
    private boolean serverRunning = true;    
    private List loaders = null;
    
    public Start() throws IOException {        
        this.serverSocket = new ServerSocket(config.adminPort, 1, config.adminAddress);
        this.serverThread = new Thread(this, this.toString());
        this.serverThread.setDaemon(false);        
        this.loaders = new ArrayList();          
    }
         
    public void run() {        
        while(serverRunning) {
            try {            
            Socket clientSocket = serverSocket.accept(); 
            System.out.println("Received connection from - " + clientSocket.getInetAddress() + " : " + clientSocket.getPort());          
            processClientRequest(clientSocket);
            clientSocket.close();                      
            } catch (IOException e) {
                e.printStackTrace();                              
            }
        }
        shutdownServer(); 
        System.exit(0);                       
    }
    
    private void processClientRequest(Socket client) throws IOException {         
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));      
        String request = reader.readLine();        
        
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        writer.println(processRequest(request, client));
        writer.flush();
        
        writer.close();
        reader.close();        
    }
    
    private String processRequest(String request, Socket client) {
        if (request != null) {        
            String key = request.substring(0, request.indexOf(':'));
            String command = request.substring(request.indexOf(':')+1);       
            if (!key.equals(config.adminKey)) {        
                return "FAIL";
            } else {
                if (command.equals(Start.SHUTDOWN_COMMAND)) {                             
                    serverRunning = false;
                    System.out.println("Shutdown initiated from: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());        
                } else if (command.equals(Start.STATUS_COMMAND)) {
                    return serverRunning ? "Running" : "Stopped";
                }
                return "OK";
            }
        } else {
            return "FAIL";
        }
    }
   
    private void startServer(String args[]) throws Exception {
        // load the lib directory
        File libDir = new File(config.baseLib);        
        if (libDir.isDirectory()) {
            File files[] = libDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                String file = files[i].getName();
                if (file.endsWith(".jar") || file.endsWith(".zip")) {
                    classPath.addComponent(files[i]);
                }
            }
        }
        
        // load the ofbiz-base.jar
        System.out.println(config.baseJar);
        classPath.addComponent(config.baseJar);
        
        // load the config directory
        classPath.addComponent(config.baseConfig);
                
        // set the classpath/classloader
        System.setProperty("java.class.path", classPath.toString());
        System.out.println(classPath.toString());
        ClassLoader classloader = classPath.getClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        
        // set the shutdown hook
        setShutdownHook();
        
        // stat the log directory
        boolean createdDir = false;
        File logDir = new File(config.logDir);
        if (!logDir.exists()) {
            logDir.mkdir();
            createdDir = true;
        }        
        
        // start the loaders
        Iterator li = config.loaders.iterator();
        while (li.hasNext()) {
            String loaderClassName = (String) li.next();
            try {
                Class loaderClass = classloader.loadClass(loaderClassName);
                StartupLoader loader = (StartupLoader) loaderClass.newInstance();
                loader.load(config);
                loaders.add(loader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }                                   
    } 
    
    private void setShutdownHook() {
        try {
            Method shutdownHook = java.lang.Runtime.class.getMethod("addShutdownHook",new Class[] {java.lang.Thread.class});
            Thread hook = new Thread() {
                public void run() {                
                    setName("OFBiz_Shutdown_Hook");
                    serverRunning = false;                 
                    
                    // Try to avoid JVM crash
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            
            shutdownHook.invoke(Runtime.getRuntime(), new Object[]{hook});
        } catch(Exception e) {                            
            // VM Does not support shutdown hook
        }        
    }
    
    private void shutdownServer() {
        if (loaders != null && loaders.size() > 0) {
            Iterator i = loaders.iterator();
            while (i.hasNext()) {
                StartupLoader loader = (StartupLoader) i.next();
                try {
                    loader.unload();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }        
    }
    

    public static void start(String[] args) throws Exception {
        Start start = new Start();        
        start.startServer(args);
    }
    
    public static String status() throws Exception {
        return sendSocketCommand(Start.STATUS_COMMAND);        
    }
        
    public static String shutdown() throws Exception {
        return sendSocketCommand(Start.SHUTDOWN_COMMAND);        
    }
    
    private static String sendSocketCommand(String command) throws IOException {
        Socket socket = new Socket(config.adminAddress, config.adminPort);        
                                                
        // send the command
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);                    
        writer.println(config.adminKey + ":" + command);
        writer.flush();        
            
        // read the reply
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = null;
        if (reader.ready()) {
            response = reader.readLine();               
        }
        
        reader.close();
        
        // close the socket
        writer.close();                        
        socket.close();        
        
        return response;
    }
    
    public static void main(String[] args) throws Exception {
        String firstArg = args.length > 0 ? args[0] : "";
        
        if (firstArg.equals("-help") || firstArg.equals("-?")) {
            System.out.println("");
            System.out.println("Usage: java -jar ofbiz.jar [options]");
            System.out.println("-help, -? ---> This screen");
            System.out.println("-status -----> Status of the server");
            System.out.println("-shutdown ---> Shutdown the server");
            System.out.println("[no option] -> Start the server");        
        } else if (firstArg.equals("-status")) {
            System.out.println("Current Status : " + Start.status());                               
        } else if (firstArg.equals("-shutdown")) {
            System.out.println("Shutting down server : " + Start.shutdown());
        } else {
            Start.start(args);
        }                
    }
    
    public static class Config {
        public String containerConfig;
        public InetAddress adminAddress;
        public int adminPort;
        public String adminKey;
        public String ofbizHome;
        public String baseJar;
        public String baseLib;
        public String baseConfig;
        public String logDir;
        public List loaders;
        
        public Config() {
            try {
                init();
            } catch (IOException e) {                
                e.printStackTrace();
                System.exit(-1);
            }
        }
        public void init() throws IOException {
            InputStream propsStream = getClass().getClassLoader().getResourceAsStream(Start.CONFIG_FILE);
            Properties props = new Properties();
            props.load(propsStream);
            
            // set the ofbiz.home            
            if (ofbizHome == null) {        
                ofbizHome = props.getProperty("ofbiz.home", ".");
                // get a full path
                if (ofbizHome.equals(".")) {        
                    ofbizHome = System.getProperty("user.dir");
                    ofbizHome = ofbizHome.replace('\\', '/');
                }            
            }
            System.setProperty("ofbiz.home", ofbizHome);
            
            // base config directory
            baseConfig = System.getProperty("ofbiz.base.config");
            if (baseConfig == null) {
                baseConfig = ofbizHome + "/" + props.getProperty("ofbiz.base.config", "config");
            }
                        
            // base lib directory
            baseLib = System.getProperty("ofbiz.base.lib");            
            if (baseLib == null) {
                baseLib = ofbizHome + "/" + props.getProperty("ofbiz.base.lib", "lib");                
            } 
            
            // base jar file
            baseJar = System.getProperty("ofbiz.base.jar");            
            if (baseJar == null) {
                baseJar = ofbizHome + "/" + props.getProperty("ofbiz.base.jar", "base/build/lib/ofbiz-base.jar");                
            }
            
            // log directory
            logDir = System.getProperty("ofbiz.log.dir");
            if (logDir == null) {
                logDir = ofbizHome + "/" + props.getProperty("ofbiz.log.dir", "logs");
            }
            
            // container configuration
            containerConfig = System.getProperty("ofbiz.container.config");
            if (containerConfig == null) {
                containerConfig = ofbizHome + "/" + props.getProperty("ofbiz.container.config", "base/config/ofbiz-containers.xml");
            }           
            
            // get the admin server info 
            String serverHost = System.getProperty("ofbiz.admin.host");
            if (serverHost == null) {      
                serverHost = props.getProperty("ofbiz.admin.host", "127.0.0.1");
            }
                        
            String adminPortStr = System.getProperty("ofbiz.admin.port");
            if (adminPortStr == null) {
                adminPortStr = props.getProperty("ofbiz.admin.port", "10523");
            }

            // set the admin key
            adminKey = System.getProperty("ofbiz.admin.key");
            if (adminKey == null) {
                adminKey = props.getProperty("ofbiz.admin.key", "NA");
            }
                     
            // create the host InetAddress   
            adminAddress = InetAddress.getByName(serverHost);
        
            // parse the port number
            try {        
                adminPort = Integer.parseInt(adminPortStr);
            } catch (Exception e) {
                adminPort = 10523;
            }
            
            // set the property to tell Log4J to use debug.properties
            String log4jConfig = System.getProperty("log4j.configuration");
            if (log4jConfig == null) {        
                log4jConfig = props.getProperty("log4j.configuration");
            }
        
            // build a default log4j configuration based on ofbizHome
            if (log4jConfig == null) {
                log4jConfig = ofbizHome + "/config/debug.properties";
            }
        
            // set the log4j configuration property so we don't pick up one inside jars by mistake
            System.setProperty("log4j.configuration", log4jConfig);
        
            // set the property to tell Jetty to use 2.4 SessionListeners
            System.setProperty("org.mortbay.jetty.servlet.AbstractSessionManager.24SessionDestroyed", "true");
            
            // loader classes
            loaders = new ArrayList();
            int currentPosition = 1;
            while(true) {
                String loaderClass = props.getProperty("ofbiz.start.loader" + currentPosition);
                if (loaderClass == null || loaderClass.length() == 0) {
                    break;
                } else {
                    loaders.add(loaderClass);
                    currentPosition++;
                }
            }
                 
            // close the stream                 
            propsStream.close();            
        }        
    }
}

