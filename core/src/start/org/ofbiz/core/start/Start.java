/*
 * $Id$
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
package org.ofbiz.core.start;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Start - OFBiz/Jetty Startup Class
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision$
 * @since      2.1
 */
public class Start implements Runnable {

    public static final String CONFIG_FILE = "org/ofbiz/core/start/start.properties";
    public static final String SHUTDOWN_COMMAND = "SHUTDOWN";
    public static final String STATUS_COMMAND = "STATUS";
               
    private static Configuration conf = new Configuration(Start.CONFIG_FILE);   
    
    private Classpath classPath = new Classpath(System.getProperty("java.class.path"));     
    private ServerSocket serverSocket = null;
    private Thread serverThread = null;
    private boolean serverRunning = true;
    private Config config = null;
    private List loaders = null;
    
    public Start() throws IOException {
        serverSocket = new ServerSocket(conf.adminPort, 1, conf.adminAddr);
        serverThread = new Thread(this, this.toString());
        serverThread.setDaemon(false);        
        loaders = new ArrayList();
        config = new Config();  
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
            if (!key.equals(conf.adminKey)) {        
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
        
        // load the config directory
        classPath.addComponent(config.baseConfig);
        
        // remove this later
        loadLibs();
        
        // set the classpath/classloader
        System.setProperty("java.class.path", classPath.toString());
        ClassLoader classloader = classPath.getClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        
        // set the shutdown hook
        setShutdownHook();
        
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
    
    /** @deprecated */
    private void oldStartServer(String args[]) throws Exception {
        ArrayList xargs = new ArrayList();
        xargs.add(conf.configFile);
        for (int i = 0; i < args.length; i++) {
            xargs.add(args[i]);            
        }
        ClassLoader cl = classPath.getClassLoader();
        
        System.out.println("");       
        System.out.println("java.home......: " + conf.javaHome);
        System.out.println("java.version...: " + conf.javaVersion);
        System.out.println("ofbiz.home.....: " + conf.ofbizHome);        
        System.out.println("server.class...: " + conf.serverClass);
        System.out.println("config.file....: " + conf.configFile);                               
        
        System.setProperty("java.class.path", classPath.toString());
        Thread.currentThread().setContextClassLoader(cl);   
        
        // stat the log directory
        boolean createdDir = false;
        File logDir = new File(conf.logDir);
        if (!logDir.exists()) {
            logDir.mkdir();
            createdDir = true;
        }        
        System.out.println("log.dir........: " + conf.logDir + (createdDir ? " [Created]" : ""));
        System.out.println("");
        
        // start the admin thread
        serverThread.start();
        
        // invoke the main method on the defined class                            
        invokeServerMain(cl, conf.serverClass, (String[]) xargs.toArray(args));
    }
  
    /** @deprecated */
    private void loadLibs() throws IOException {  
        // load the OFB jars
        Iterator jarIt = conf.jarList.iterator();
        while (jarIt.hasNext()) {
            String pathStr = (String) jarIt.next(); 
            //System.out.println("Loading " + pathStr + "...");           
            if (pathStr.endsWith(".zip") || pathStr.endsWith(".jar")) {             
                classPath.addComponent(pathStr);
            } else {
                File path = new File(pathStr);
                loadJarsFromPath(path, null);
            }
        }
       
        // load the OFB dirs
        Iterator dirIt = conf.dirList.iterator();
        while (dirIt.hasNext()) {
            String pathStr = (String) dirIt.next();
            //System.out.println("Loading " + pathStr + "...");
            classPath.addComponent(pathStr);                        
        }
        
        // try to load tools.jar      
        classPath.addComponent(conf.javaHome + "/lib/tools.jar");      
        classPath.addComponent(conf.javaHome + "/../lib/tools.jar");  
        
        // now load the required jetty lib based on java.version
        List excludes = new ArrayList();
        classPath.addClasspath(conf.ofbizHome + "/lib/jetty/lib/javax.servlet.jar");
        if (conf.javaVersion.startsWith("1.4")) {            
            classPath.addComponent(conf.ofbizHome + "/lib/jetty/lib/org.mortbay.jetty.jar");
        } else {
            classPath.addComponent(conf.ofbizHome + "/lib/jetty/lib/org.mortbay.jetty-jdk1.2.jar");
        }  
        loadJars(conf.ofbizHome + "/lib/jetty/ext", excludes);                                                   
    }
    
    /** @deprecated */
    private void loadJars(String parent, List excludes) throws IOException {  
        // note this is not recursive
        File libDir = new File(parent);  
        loadJarsFromPath(libDir, excludes);      
        String paths[] = libDir.list();
        for (int i = 0; i < paths.length; i++) {            
            File file = new File(libDir.getCanonicalPath() + "/" + paths[i]);           
            if (file.isDirectory() && !paths[i].equals("CVS") && !paths[i].equals("compile")) {            
                loadJarsFromPath(file, excludes);
            }
        }
    }
    
    /** @deprecated */
    private void loadJarsFromPath(File path, List excludes) {        
        File files[] = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            String file = files[i].getName();
            if (file.endsWith(".jar") || file.endsWith(".zip")) {
                if (excludes != null && excludes.size() > 0 && excludes.contains(file)) {
                    // do not add these
                } else {
                    classPath.addComponent(files[i]);
                }
            }
        }        
    }
    
    /** @deprecated */
    private void invokeServerMain(ClassLoader classloader, String classname, String[] args) throws Exception {                
        Class serverClass = classloader.loadClass(classname);                
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = args.getClass();
        
        Method main = serverClass.getDeclaredMethod("main", parameterTypes);
        Object[] parameters = new Object[1];
        parameters[0] = args;
        
        main.invoke(null, parameters);
    }
    
    public static void start(String[] args) throws Exception {
        Start start = new Start();        
        start.startServer(args);
    }
    
    /** @deprecated */
    public static void oldStart(String[] args) throws Exception {
        Start start = new Start();
        start.loadLibs();
        start.oldStartServer(args);
    }

    public static String status() throws Exception {
        return sendSocketCommand(Start.STATUS_COMMAND);        
    }
        
    public static String shutdown() throws Exception {
        return sendSocketCommand(Start.SHUTDOWN_COMMAND);        
    }
    
    private static String sendSocketCommand(String command) throws IOException {
        Socket socket = new Socket(conf.adminAddr, conf.adminPort);        
                                                
        // send the command
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);                    
        writer.println(conf.adminKey + ":" + command);
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
        } else if (firstArg.equals("-start")) {
            Start.start(args);
        } else if (firstArg.equals("-status")) {
            System.out.println("Current Status : " + Start.status());                               
        } else if (firstArg.equals("-shutdown")) {
            System.out.println("Shutting down server : " + Start.shutdown());
        } else {
            Start.oldStart(args);
        }                
    }
    
    public static class Config {
        public String containerConfig;
        public InetAddress adminAddress;
        public int adminPort;
        public String adminKey;
        public String ofbizHome;
        public String baseLib;
        public String baseConfig;
        public List loaders;
        
        public Config() throws IOException {
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

class Configuration {
        
    protected InetAddress adminAddr;
    protected int adminPort;
    protected String adminKey;
    
    protected String serverClass;
    protected String configFile;
    protected String logDir;
    protected String ofbizHome;
    protected String jettyHome;
    protected String javaHome;
    protected String javaVersion;
    
    protected List jarList;
    protected List dirList;
    
    protected Configuration(String config) {
        try {
            setConfig(config);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    protected void setConfig(String config) throws IOException {   
        serverClass = System.getProperty("server.class");
        configFile = System.getProperty("config.file");
        logDir = System.getProperty("log.dir");             
        ofbizHome = System.getProperty("ofbiz.home");
        jettyHome = System.getProperty("jetty.home");
        javaHome = System.getProperty("java.home");
        javaVersion = System.getProperty("java.version");
        jarList = new ArrayList();
        dirList = new ArrayList();
                
        InputStream propsStream = getClass().getClassLoader().getResourceAsStream(config);
        Properties props = new Properties();
        props.load(propsStream); 
                
        // grab default home paths if not on command line            
        if (ofbizHome == null) {        
            ofbizHome = props.getProperty("ofbiz.home", ".");
            // get a full path
            if (ofbizHome.equals(".")) {        
                ofbizHome = System.getProperty("user.dir");
                ofbizHome = ofbizHome.replace('\\', '/');
            }            
        }
        if (logDir == null) {
            logDir = props.getProperty("log.dir", ofbizHome + "/logs");                
        }
        if (jettyHome == null) {        
            jettyHome = props.getProperty("jetty.home", ofbizHome);
        }
        if (javaHome == null) {        
            javaHome = props.getProperty("java.home");
        }
                
        // set the home fields
        System.setProperty("ofbiz.home", ofbizHome);
        System.setProperty("jetty.home", jettyHome);
        System.setProperty("java.home", javaHome);
        
        // grab server class if not on command line
        if (serverClass == null)        
            serverClass = props.getProperty("server.class", "org.mortbay.jetty.Server.class");
        
        // grab the config file if not on command line
        if (configFile == null)  
            configFile = props.getProperty("config.file", ofbizHome + "/setup/jetty/etc/ofbiz.xml");                
        
        // set the property to tell Log4J to use debug.properties
        String log4jConfig = System.getProperty("log4j.configuration");
        if (log4jConfig == null) {        
            log4jConfig = props.getProperty("log4j.configuration");
        }
        
        // build a default log4j configuration based on ofbizHome
        if (log4jConfig == null) {
            log4jConfig = ofbizHome + "/commonapp/etc/debug.properties";
        }
        
        // set the log4j configuration property so we don't pick up one inside jars by mistake
        System.setProperty("log4j.configuration", log4jConfig);
        
        // set the property to tell Jetty to use 2.4 SessionListeners
        System.setProperty("org.mortbay.jetty.servlet.AbstractSessionManager.24SessionDestroyed", "true");
                          
        // get the lib dir prefix names
        String prefixNames = props.getProperty("library.configs");
        StringTokenizer st = new StringTokenizer(prefixNames, ",");
        List configList = null;
        if (st != null && st.hasMoreTokens()) {
            configList = new ArrayList();

            while (st.hasMoreTokens())
                configList.add(st.nextToken());
        }
        
        // load the libs        
        if (configList != null) {
            Iterator configIter = configList.iterator();
            while (configIter.hasNext()) {
                int currentPosition;
                boolean looping;
                String configName = (String) configIter.next();
                String configPrefixEnv = props.getProperty(configName + ".prefix.env");
                
                // load the jars
                currentPosition = 1;
                looping = true;
                while (looping) {
                    StringBuffer path = new StringBuffer();                    
                    String suffix = props.getProperty(configName + ".loadjar" + currentPosition);                    
                    if (configPrefixEnv != null && configPrefixEnv.length() > 0) {
                        String prefixPath = System.getProperty(configPrefixEnv);
                        if (prefixPath != null && prefixPath.length() > 0) {
                            path.append(prefixPath);                            
                        }
                    }
                    if (suffix != null && suffix.length() > 0) {
                        path.append(suffix);
                        jarList.add(path.toString());
                        currentPosition++;
                    } else {
                        looping = false;
                    }                                  
                }
                
                // load the dirs
                currentPosition = 1;
                looping = true;
                while (looping) {
                    StringBuffer path = new StringBuffer();                    
                    String suffix = props.getProperty(configName + ".loaddir" + currentPosition);                    
                    if (configPrefixEnv != null && configPrefixEnv.length() > 0) {
                        String prefixPath = System.getProperty(configPrefixEnv);
                        if (prefixPath != null && prefixPath.length() > 0) {
                            path.append(prefixPath);                            
                        }
                    }
                    if (suffix != null && suffix.length() > 0) {
                        path.append(suffix);
                        dirList.add(path.toString());
                        currentPosition++;
                    } else {
                        looping = false;
                    }                                  
                }                
            }
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
        adminAddr = InetAddress.getByName(serverHost);
        
        // parse the port number
        try {        
            adminPort = Integer.parseInt(adminPortStr);
        } catch (Exception e) {
            adminPort = 10523;
        }
                             
        propsStream.close();            
    }               
}
