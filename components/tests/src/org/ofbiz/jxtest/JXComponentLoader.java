/*
 * $Id: JXComponentLoader.java,v 1.2 2004/07/31 20:10:16 ajzeneski Exp $
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
package org.ofbiz.jxtest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.component.ComponentLoaderConfig;
import org.ofbiz.base.container.*;
import org.ofbiz.base.start.Classpath;
import org.ofbiz.base.start.StartupException;
import org.ofbiz.base.start.StartupLoader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;


/**
 * JXComponentLoader - Loads the OFBiz component libraries and definitions.
 * Follows examples from the ComponentContainers and Start classes
 *
 * @author     <a href="mailto:bgpalmer@computer.org">Brett G. Palmer</a> 
  *@version    $Revision: 1.2 $
 * @since      3.0
 */
public class JXComponentLoader {

	public static final String module = JXComponentLoader.class.getName();   
	
    public static final String CONFIG_FILE = "org/ofbiz/jxtest/test.properties";
	//Default file for configuration is config/test.properties
    //public static final String CONFIG_FILE = "test.properties";

    private Classpath classPath = new Classpath(System.getProperty("java.class.path"));     
 
    private List loaders = null;
    private Config config = null;
    
    private static JXComponentLoader singleton;
    
    private JXComponentLoader(String configFile) throws Exception {
    	
    	//System.out.println("BGP: Initializing JXComponentLoader");
		if ( (configFile == null) || ("default".equals(configFile) )) {
			configFile = CONFIG_FILE;
		}
                
		this.config = new Config(configFile);              
		this.loaders = new ArrayList();  
		        
		//System.out.println("BGP: Loading classpath");
		loadClasspath();
		
		//System.out.println("BGP: Loading components");
		//loadComponents();
		//1/6/2003 - bgp - changed to use OFBiz containerLoader instead of custom component loader
		loadContainers(this.config);
    }
    
    //@todo: Synchronize on TestLoader
    public static void loadComponents(String configFile) throws Exception {
    	//System.out.println("BGP - JXComponentLoader.loadComponents for configfile" + configFile);
        if(singleton == null){
        	synchronized (JXComponentLoader.class){
				singleton = new JXComponentLoader(configFile); 
        	}    
        } 
    }
    
    public String toString(){
    	//return this.config.toString();
    	return this.classPath.toString();
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
                	Debug.log("Adding " + files[i] + " to classpath");
                    classPath.addComponent(files[i]);   
                }
            }            
        }
    }
    
    private void loadContainers(Config config ) throws Exception {
    	
    	
//		get the master container configuration file
			String configFileLocation = config.containerConfig;
			List loadedContainers = new LinkedList();  
        
			Collection containers = null;
			try {
				containers = ContainerConfig.getContainers(configFileLocation);
			} catch (ContainerException e) {            
				throw new StartupException(e);
			}

			if (containers != null) {
				Iterator i = containers.iterator();
				 while (i.hasNext()) {
					 ContainerConfig.Container containerCfg = (ContainerConfig.Container) i.next();                
					 loadedContainers.add(loadContainer(containerCfg.className, configFileLocation));
				 }
			 }                             	
    	
    }
    

	/**
	 * loadContainer was taken from the ContainerLoader example.
	 * @param classname
	 * @param configFileLocation
	 * @return
	 * @throws StartupException
	 */
	private Container loadContainer(String classname, String configFileLocation) throws StartupException {
		// load the component container class
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			Debug.logWarning("Unable to get context classloader; using system", module);
			loader = ClassLoader.getSystemClassLoader();
		}
		Class componentClass = null;
		try {
			componentClass = loader.loadClass(classname);
		} catch (ClassNotFoundException e) {
			throw new StartupException("Cannot locate container class", e);            
		}
		if (componentClass == null) {
			throw new StartupException("Component container class not loaded");
		}
        
		Container componentObj = null;
		try {
			componentObj = (Container) componentClass.newInstance();
		} catch (InstantiationException e) {
			throw new StartupException(e);            
		} catch (IllegalAccessException e) {
			throw new StartupException(e);            
		} catch (ClassCastException e) {
			throw new StartupException(e);
		}
        
		if (componentObj == null) {
			throw new StartupException("Unable to create instance of component container");
		}

        try {
            componentObj.init(null, configFileLocation);
        } catch (ContainerException e) {
            throw new StartupException(e);
        }
        
		try {
			componentObj.start();
		} catch (ContainerException e) {
			throw new StartupException(e);
		}  
        
		return componentObj;
	}   
    
    private void loadComponents() throws Exception {
    	            
			 // get the components to load
			 List components = null;
			 try {            
				 components = ComponentLoaderConfig.getComponentsToLoad(null);
			 } catch (ComponentException e) {
				 throw new ContainerException(e);            
			 }
                       
			 // load each component
			 if (components != null) {
				 Iterator ci = components.iterator();
				 while (ci.hasNext()) {
					 ComponentLoaderConfig.ComponentDef def = (ComponentLoaderConfig.ComponentDef) ci.next();                
					 if (def.type == ComponentLoaderConfig.SINGLE_COMPONENT) {
						 ComponentConfig config = null;
						 try {
							 config = ComponentConfig.getComponentConfig(def.name, def.location);
							 if (UtilValidate.isEmpty(def.name)) {
								 def.name = config.getGlobalName();
							 }
						 } catch (ComponentException e) {
							 Debug.logError("Cannot load component : " + def.name + " @ " + def.location + " : " + e.getMessage(), module);    
						 }
						 if (config == null) {
							 Debug.logError("Cannot load component : " + def.name + " @ " + def.location, module);   
						 } else {
							 loadComponent(config);
						 }                   
					 } else if (def.type == ComponentLoaderConfig.COMPONENT_DIRECTORY) {
						 loadComponentDirectory(def.location);    
					 }                                
				 }
			 }

			 // set the new classloader on the current thread
			 System.setProperty("java.class.path", classPath.toString());
			 ClassLoader cl = classPath.getClassLoader();
			 Thread.currentThread().setContextClassLoader(cl);        	
    	
    }
    

	private void loadClasspath() throws Exception {
		// load the lib directory
		loadLibs(config.baseLib);  
		loadLibs(config.jxtestLib);      
        
		// load the ofbiz-base.jar        
		classPath.addComponent(config.baseJar);
		
		//@todo: adding the ofbiz-entity.jar couldn't be found when running jxtest.bat
		//script.  I finally had to add the jar manually to the script file - bgp
		classPath.addComponent(config.entityJar);
        
		// load the config directory
		classPath.addComponent(config.baseConfig);
		
                
		// set the classpath/classloader
		//Debug.log("Setting Classpath: " + classPath.toString());
		System.setProperty("java.class.path", classPath.toString());        
		ClassLoader classloader = classPath.getClassLoader();
		Thread.currentThread().setContextClassLoader(classloader);
        
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
				loaders.add(loader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}                                   
	} 
	private void loadComponentDirectory(String directoryName) throws ContainerException {
		Debug.logInfo("Loading component directory [" + directoryName + "]", module);
		File parentPath = new File(directoryName);
		if (!parentPath.exists() || !parentPath.isDirectory()) {
			Debug.logError("Auto-Load Component directory not found : " + directoryName, module);
		} else {
			String subs[] = parentPath.list();
			for (int i = 0; i < subs.length; i++) {
				try {
					File componentPath = new File(parentPath.getCanonicalPath() + "/" + subs[i]);
					if (componentPath.isDirectory() && !subs[i].equals("CVS")) {                        
						// make sure we have a component configuraton file
						String componentLocation = componentPath.getCanonicalPath();
						File configFile = new File(componentLocation + "/ofbiz-component.xml");                        
						if (configFile.exists()) {
							ComponentConfig config = null;
							try {
								// pass null for the name, will default to the internal component name
								config = ComponentConfig.getComponentConfig(null, componentLocation);
							} catch (ComponentException e) {
								Debug.logError(e, "Cannot load component : " + componentPath.getName() + " @ " + componentLocation + " : " + e.getMessage(), module);    
							}
							if (config == null) {
								Debug.logError("Cannot load component : " + componentPath.getName() + " @ " + componentLocation, module);    
							} else {
								loadComponent(config);                                
							}                          
						}
					}
				} catch (IOException ioe) {
					Debug.logError(ioe, module);
				}
			}            
		}                           
	}
    
	private void loadComponent(ComponentConfig config) throws ContainerException {
		List classpathInfos = config.getClasspathInfos();
		String configRoot = config.getRootLocation();
		configRoot = configRoot.replace('\\', '/');
		// set the root to have a trailing slash
		if (!configRoot.endsWith("/")) {
			configRoot = configRoot + "/";
		}
		if (classpathInfos != null) {
			Iterator cpi = classpathInfos.iterator();
			while (cpi.hasNext()) {
				ComponentConfig.ClasspathInfo cp = (ComponentConfig.ClasspathInfo) cpi.next();
				String location = cp.location.replace('\\', '/');
				// set the location to not have a leading slash
				if (location.startsWith("/")) {
					location = location.substring(1);
				}
				if ("dir".equals(cp.type)) {                    
					classPath.addComponent(configRoot + location);
				} else if ("jar".equals(cp.type)) {
					String dirLoc = location;
					if (dirLoc.endsWith("/*")) {
						// strip off the slash splat                        
						dirLoc = location.substring(0, location.length() - 2);
					}                    
					File path = new File(configRoot + dirLoc);
					if (path.exists()) {
						if (path.isDirectory()) {
							// load all .jar and .zip files in this directory
							File files[] = path.listFiles();
							for (int i = 0; i < files.length; i++) {
								String file = files[i].getName();
								if (file.endsWith(".jar") || file.endsWith(".zip")) {                                    
									classPath.addComponent(files[i]);
								}
							}
						} else {
							// add a single file                                                       
							classPath.addComponent(configRoot + location);    
						}
					} else {                                               
						Debug.logWarning("Location '" + configRoot + dirLoc + "' does not exist", module);
					}
				} else {
					Debug.logError("Classpath type '" + cp.type + "' is not supported; '" + location + "' not loaded", module);                    
				}
			}
		}                
	}    
    

    public static void main(String[] args) throws Exception {
        JXComponentLoader loader = new JXComponentLoader(args.length == 2 ? args[1] : null);

        System.out.println("config equals = " + loader.toString());
                        
    }
    
    public static class Config {

		public String containerConfig;			 	 
        public String entityJar;
        public String ofbizHome;
        public String baseJar;
        public String baseLib;
        public String jxtestLib;
        public String baseConfig;
        public String logDir;
        public List loaders;
        
        public Config(String config) {
            try {
                init(config);
            } catch (IOException e) {                
                e.printStackTrace();
                System.exit(-1);
            }
        }
        public void init(String config) throws IOException {
            InputStream propsStream = getClass().getClassLoader().getResourceAsStream(config);
            if (propsStream == null) {
                throw new IOException("Cannot load configuration properties : " + config);
            }
            Properties props = new Properties();
            props.load(propsStream);
            
            // set the ofbiz.home            
            //check if the ofbiz.home was passed in via an -D jvm option
            ofbizHome = System.getProperty("ofbiz.home");
            if (ofbizHome == null) {        
            	//Else just use the current working directory or the value set in the test.properties file
                ofbizHome = props.getProperty("ofbiz.home", ".");
                // get a full path
                if (ofbizHome.equals(".")) {        
                    ofbizHome = System.getProperty("user.dir");
                    ofbizHome = ofbizHome.replace('\\', '/');
                }            
                System.out.println("ofbiz.home set to: " + ofbizHome);
                System.setProperty("ofbiz.home", ofbizHome);
            }
            
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
            
			jxtestLib = System.getProperty("ofbiz.jxtest.lib");            
			if (jxtestLib == null) {
				jxtestLib = ofbizHome + "/" + props.getProperty("ofbiz.jxtest.lib", "lib");                
			} 
            
            // base jar file
            baseJar = System.getProperty("ofbiz.base.jar");            
            if (baseJar == null) {
                baseJar = ofbizHome + "/" + props.getProperty("ofbiz.base.jar", "base/build/lib/ofbiz-base.jar");                
            }
            
			//	entity jar file
			entityJar = System.getProperty("ofbiz.entity.jar");            
			if (entityJar == null) {
				entityJar = ofbizHome + "/" + props.getProperty("ofbiz.entity.jar", "components/entity/build/lib/ofbiz-entity.jar");                
			}
            
            // log directory
            logDir = System.getProperty("ofbiz.log.dir");
            if (logDir == null) {
                logDir = ofbizHome + "/" + props.getProperty("ofbiz.log.dir", "logs");
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
            
//			container configuration
			containerConfig = System.getProperty("ofbiz.container.config");
			if (containerConfig == null) {
				containerConfig = ofbizHome + "/" + props.getProperty("ofbiz.container.config", "base/config/ofbiz-containers.xml");
			}    
        
              
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

	/* (non-Javadoc)
	 * @see org.ofbiz.base.start.StartupLoader#load(org.ofbiz.base.start.Start.Config)
	 */
	public void load(Config config) throws StartupException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ofbiz.base.start.StartupLoader#unload()
	 */
	public void unload() throws StartupException {
		// TODO Auto-generated method stub
		
	}


}

