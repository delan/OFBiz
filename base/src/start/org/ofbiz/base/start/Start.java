/*
 * $Id: Start.java,v 1.15 2004/03/30 23:38:28 ajzeneski Exp $
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Start - OFBiz Container(s) Startup Class
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.15 $
 * @since      2.1
 */
public class Start implements Runnable {

    public static final String CONFIG_FILE = "org/ofbiz/base/start/start.properties";
    public static final String SHUTDOWN_COMMAND = "SHUTDOWN";
    public static final String STATUS_COMMAND = "STATUS";    
                        
    private Classpath classPath = new Classpath(System.getProperty("java.class.path"));     
    private ServerSocket serverSocket = null;
    private Thread serverThread = null;
    private boolean serverRunning = true;    
    private List loaders = null;
    private Config config = null;
    private String loaderArgs[] = null;

    public Start(String args[]) throws IOException {
        this.loaders = new ArrayList();
        this.config = new Config();

        // always read the default properties first
        config.readConfig(CONFIG_FILE);

        // parse the args for config file
        String configFile = null;
        if (args.length > 1) {
            configFile = args[1];
            if (args.length > 2) {
                this.loaderArgs = new String[args.length - 2];
                for (int i = 2; i < args.length; i++) {
                    this.loaderArgs[i-2] = args[i];
                }
            }
        }

        // if we specified a config file; replace the values
        if (configFile != null) {
            System.out.println("External startup configuration file - " + configFile);
            config.readConfig(configFile);
        }
    }
    
    public void startListenerThread() throws IOException {
        this.serverSocket = new ServerSocket(config.adminPort, 1, config.adminAddress);
        this.serverThread = new Thread(this, this.toString());
        this.serverThread.setDaemon(false);
        this.serverThread.start();
        System.out.println("Admin socket listening on - " + config.adminAddress + ":" + config.adminPort);
    }
         
    public void run() {       
        while (serverRunning) {
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
                    System.out.println("Shutdown initiated from: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                    serverRunning = false;        
                } else if (command.equals(Start.STATUS_COMMAND)) {
                    return serverRunning ? "Running" : "Stopped";
                }
                return "OK";
            }
        } else {
            return "FAIL";
        }
    }
   
    private void loadLibs(String path) throws Exception {
        File libDir = new File(path);
        if (libDir.exists()) {
            File files[] = libDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory() && !"CVS".equals(fileName)) {
                    loadLibs(files[i].getCanonicalPath());
                } else if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                    classPath.addComponent(files[i]);   
                }
            }            
        }
    }
    
    private void startServer() throws Exception {
        // load the lib directory
        loadLibs(config.baseLib);        
        
        // load the ofbiz-base.jar        
        classPath.addComponent(config.baseJar);
        
        // load the config directory
        classPath.addComponent(config.baseConfig);
                
        // set the classpath/classloader
        System.setProperty("java.class.path", classPath.toString());        
        ClassLoader classloader = classPath.getClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        
        // set the shutdown hook
        setShutdownHook();
        
        // start the listener thread
        startListenerThread();
        
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
                loader.load(config, loaderArgs);
                loaders.add(loader);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(99);
            }
        }                                   
    } 
    
    private void setShutdownHook() {
        try {
            Method shutdownHook = java.lang.Runtime.class.getMethod("addShutdownHook", new Class[] { java.lang.Thread.class });
            Thread hook = new Thread() {
                public void run() {                
                    setName("OFBiz_Shutdown_Hook");
                    shutdownServer();                    
                    // Try to avoid JVM crash
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            
            shutdownHook.invoke(Runtime.getRuntime(), new Object[] { hook });
        } catch (Exception e) {                                    
            // VM Does not support shutdown hook
            e.printStackTrace();
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
        serverRunning = false;             
    }
    

    public void start() throws Exception {                
        startServer();
    }
    
    public String status() throws Exception {
        String status = null;
        try {
            status = sendSocketCommand(Start.STATUS_COMMAND);            
        } catch (ConnectException e) {
            return "Not Running";
        } catch (IOException e) {
            throw e;
        }
        return status;                
    }
        
    public String shutdown() throws Exception {
        return sendSocketCommand(Start.SHUTDOWN_COMMAND);        
    }
    
    private String sendSocketCommand(String command) throws IOException, ConnectException {
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
        Start start = new Start(args);
        String firstArg = args.length > 0 ? args[0] : "";        
        if (firstArg.equals("-help") || firstArg.equals("-?")) {
            System.out.println("");
            System.out.println("Usage: java -jar ofbiz.jar [command] [config] [config arguments]");
            System.out.println("-help, -? ----> This screen");
            System.out.println("-start -------> Start the server");
            System.out.println("-status ------> Status of the server");
            System.out.println("-shutdown ----> Shutdown the server");            
            System.out.println("[no config] --> Use default config");
            System.out.println("[no command] -> Start the server w/ default config");       
        } else if (firstArg.equals("-status")) {
            System.out.println("Current Status : " + start.status());                               
        } else if (firstArg.equals("-shutdown")) {
            System.out.println("Shutting down server : " + start.shutdown());
        } else {
            start.start();
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
        public String awtHeadless;

        private Properties getPropertiesFile(String config) throws IOException {
            InputStream propsStream = null;
            Properties props = new Properties();
            try {
                // first try classpath
                propsStream = getClass().getClassLoader().getResourceAsStream(config);
                if (propsStream != null) {
                    props.load(propsStream);
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                // next try file location
                File propsFile = new File(config);
                if (propsFile != null) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(propsFile);
                        if (fis != null) {
                            props.load(fis);
                        } else {
                            throw new FileNotFoundException();
                        }
                    } catch (FileNotFoundException e2) {
                        // do nothing; we will see empty props below
                    } finally {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                }
            } finally {
                if (propsStream != null) {
                    propsStream.close();
                }
            }

            // check for empty properties
            if (props.isEmpty()) {
                throw new IOException("Cannot load configuration properties : " + config);
            }
            return props;
        }

        public void readConfig(String config) throws IOException {
            Properties props = this.getPropertiesFile(config);
            
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
                log4jConfig = ofbizHome + "/base/config/debug.properties";
            }
        
            // set the log4j configuration property so we don't pick up one inside jars by mistake
            System.setProperty("log4j.configuration", log4jConfig);
        
            awtHeadless = System.getProperty("java.awt.headless");
            if (awtHeadless == null) {
                awtHeadless = props.getProperty("java.awt.headless");
            }
            if (awtHeadless != null) {
                System.setProperty("java.awt.headless", awtHeadless);
            }
            
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
        }        
    }
}

