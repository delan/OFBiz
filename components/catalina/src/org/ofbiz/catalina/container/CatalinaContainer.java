/*
 * $Id: CatalinaContainer.java,v 1.4 2004/05/25 20:29:26 ajzeneski Exp $
 *
 */
package org.ofbiz.catalina.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.net.URL;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ClassLoaderContainer;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.component.ComponentConfig;

import org.apache.catalina.startup.Embedded;
import org.apache.catalina.logger.LoggerBase;
import org.apache.catalina.logger.SystemOutLogger;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import org.apache.catalina.Connector;
import org.apache.catalina.Loader;
import org.apache.catalina.valves.AccessLogValve;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.core.StandardEngine;
import org.apache.coyote.tomcat5.CoyoteServerSocketFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.4 $
 * @since      May 21, 2004
 */
public class CatalinaContainer implements Container {

    public static final String CATALINA_HOSTS_HOME = System.getProperty("ofbiz.home") + "/components/catalina/hosts";
    public static final String module = CatalinaContainer.class.getName();
    protected static Map mimeTypes = new HashMap();

    protected Embedded embedded = null;
    protected Map engines = new HashMap();
    protected Map hosts = new HashMap();

    protected boolean enableDefaultMimeTypes = true;

    public boolean start(String configFileLocation) throws ContainerException {
        // set catalina_home
        System.setProperty("catalina.home", System.getProperty("ofbiz.home") + "/components/catalina");

        // get the container config
        ContainerConfig.Container cc = ContainerConfig.getContainer("catalina-container", configFileLocation);
        if (cc == null) {
            throw new ContainerException("No catalina-container configuration found in container config!");
        }

        // embedded properties
        boolean useNaming = false;
        ContainerConfig.Container.Property namingProp = cc.getProperty("useNaming");
        if (namingProp != null) {
            useNaming = "true".equalsIgnoreCase(namingProp.value);
        }

        int debug = 0;
        ContainerConfig.Container.Property debugProp = cc.getProperty("debug");
        if (debugProp != null) {
            try {
                debug = Integer.parseInt(debugProp.value);
            } catch (Exception e) {
                debug = 0;
            }
        }

        // create the instance of Embedded
        embedded = new Embedded();
        embedded.setDebug(debug);
        embedded.setUseNaming(useNaming);
        embedded.setLogger(new SystemOutLogger()); // TODO Make this configurable

        // create the engines
        List engineProps = cc.getPropertiesWithValue("engine");
        if (engineProps == null && engineProps.size() == 0) {
            throw new ContainerException("Cannot load CatalinaContainer; no engines defined!");
        }
        Iterator ei = engineProps.iterator();
        while (ei.hasNext()) {
            ContainerConfig.Container.Property engineProp = (ContainerConfig.Container.Property) ei.next();
            createEngine(engineProp);
        }

        // load the web applications
        loadComponents();

        // create the connectors
        List connectorProps = cc.getPropertiesWithValue("connector");
        if (connectorProps == null && connectorProps.size() == 0) {
            throw new ContainerException("Cannot load CatalinaContainer; no engines defined!");
        }
        Iterator ci = connectorProps.iterator();
        while (ci.hasNext()) {
            ContainerConfig.Container.Property connectorProp = (ContainerConfig.Container.Property) ci.next();
            createConnector(connectorProp);
        }


        // Start the embedded server
        try {
            embedded.start();
        } catch (LifecycleException e) {
            throw new ContainerException(e);
        }

        return true;
    }

    protected Engine createEngine(ContainerConfig.Container.Property engineConfig) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Engine without Embedded instance!");
        }

        ContainerConfig.Container.Property defaultHostProp = engineConfig.getProperty("default-host");
        if (defaultHostProp == null) {
            throw new ContainerException("default-host element of server property is required for catalina!");
        }

        String engineName = engineConfig.name;
        String hostName = defaultHostProp.value;

        StandardEngine engine = (StandardEngine) embedded.createEngine();
        engine.setName(engineName);
        engine.setDefaultHost(hostName);
        engines.put(engine.getName(), engine);

        // create a default virtual host; others will be created as needed
        Host host = createHost(engine, hostName);
        hosts.put(engineName + "._DEFAULT", host);

        AccessLogValve al = new AccessLogValve();
        engine.addValve(al);

        embedded.addEngine(engine);
        return engine;
    }

    protected Host createHost(Engine engine, String hostName) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Host without Embedded instance!");
        }

        Host host = embedded.createHost(hostName, CATALINA_HOSTS_HOME);
        engine.addChild(host);
        hosts.put(engine.getName() + hostName, host);

        return host;
    }

    protected Connector createConnector(ContainerConfig.Container.Property connectorProp) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Connector without Embedded instance!");
        }

        String host = "0.0.0.0";
        String type = "http";
        int port = 8080;
        int redirect = 0;
        boolean lookup = false;

        ContainerConfig.Container.Property hostProp = connectorProp.getProperty("host");
        if (hostProp != null) {
            host = hostProp.value;
        }

        ContainerConfig.Container.Property typeProp = connectorProp.getProperty("type");
        if (typeProp != null) {
            type = typeProp.value;
        }

        ContainerConfig.Container.Property portProp = connectorProp.getProperty("port");
        if (portProp != null) {
            try {
                port = Integer.parseInt(portProp.value);
            } catch (Exception e) {
                Debug.logWarning("Invalid port number " + portProp.value + " setting connector [" + connectorProp.name + "] to 8080.", module);
                port = 8080;
            }
        }

        ContainerConfig.Container.Property lookupProp = connectorProp.getProperty("enable-lookups");
        if (lookupProp != null) {
            lookup = "true".equalsIgnoreCase(lookupProp.value);
        }

        ContainerConfig.Container.Property rePortProp = connectorProp.getProperty("redirect-port");
        if (rePortProp != null) {
            try {
                redirect = Integer.parseInt(rePortProp.value);
            } catch (Exception e) {
                Debug.logWarning("Invalid redirect-port number " + portProp.value + " not setting connector [" + connectorProp.name + "].", module);
                redirect = 0;
            }
        }

        Connector connector = embedded.createConnector(host, port, type);
        connector.setEnableLookups(lookup);
        if (redirect > 0) {
            connector.setRedirectPort(redirect);
        }

        if ("https".equals(type)) {
            configureSsl(connectorProp, connector);
        }
        embedded.addConnector(connector);

        return connector;
    }

    protected void configureSsl(ContainerConfig.Container.Property connectorProp, Connector connector) throws ContainerException {
        if (connector == null || connectorProp == null || !connector.getSecure()) {
            throw new ContainerException("Not an SSL (secure) connector!");
        }

        CoyoteServerSocketFactory sf = (CoyoteServerSocketFactory) connector.getFactory();

        String keystore = null;
        String storeType = null;
        String password = null;
        String keyAlias = null;

        ContainerConfig.Container.Property keyStoreProp = connectorProp.getProperty("keystore");
        if (keyStoreProp == null) {
            throw new ContainerException("No keystore setting found for SSL connector!");
        }
        keystore = keyStoreProp.value;

        ContainerConfig.Container.Property keyStoreType = connectorProp.getProperty("keystore-type");
        if (keyStoreType != null) {
            storeType = keyStoreType.value;
        }

        ContainerConfig.Container.Property passwordProp = connectorProp.getProperty("password");
        if (passwordProp != null) {
            password = passwordProp.value;
        }

        ContainerConfig.Container.Property aliasProp = connectorProp.getProperty("key-alias");
        if (aliasProp != null) {
            keyAlias = aliasProp.value;
        }

        sf.setKeystoreFile(keystore);
        if (password != null) {
            sf.setKeystorePass(password);
        }
        if (storeType != null) {
            sf.setKeystoreType(storeType);
        }
        if (keyAlias != null) {
            sf.setKeyAlias(keyAlias);
        }
        connector.setFactory(sf);
    }

    protected void loadComponents() throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot load web applications without Embedded instance!");
        }

        // load the applications
        Collection componentConfigs = ComponentConfig.getAllComponents();
        if (componentConfigs != null) {
            Iterator components = componentConfigs.iterator();
            while (components.hasNext()) {
                ComponentConfig component = (ComponentConfig) components.next();
                Iterator appInfos = component.getWebappInfos().iterator();
                while (appInfos.hasNext()) {
                    ComponentConfig.WebappInfo appInfo = (ComponentConfig.WebappInfo) appInfos.next();
                    List virtualHosts = appInfo.getVirtualHosts();
                    Map initParameters = appInfo.getInitParameters();
                    Host host = (Host) hosts.get(appInfo.server + "._DEFAULT");
                    if (host == null) {
                        Debug.logWarning("Server with name [" + appInfo.server + "] not found; not mounting [" + appInfo.name + "]", module);
                    } else {
                        try {
                            // set the root location (make sure we set the paths correctly)
                            String location = component.getRootLocation() + appInfo.location;
                            location = location.replace('\\', '/');
                            if (location.endsWith("/")) {
                                location = location.substring(0, location.length()-1);
                            }

                            // get the mount point
                            String mount = appInfo.mountPoint;
                            if (mount.endsWith("/*")) {
                                mount = mount.substring(0, mount.length()-2);
                            }

                            // create the web application context
                            Context context = embedded.createContext(mount, location);
                            context.setLoader(embedded.createLoader(ClassLoaderContainer.getClassLoader()));

                            context.setDisplayName(appInfo.name);
                            context.setDocBase(location);

                            context.setDistributable(true);
                            context.setCrossContext(true);
                            context.getServletContext().setAttribute("_serverId", appInfo.server);

                            // create the Default Servlet instance to mount
                            StandardWrapper defaultServlet = new StandardWrapper();
                            defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
                            defaultServlet.setServletName("default");
                            defaultServlet.setLoadOnStartup(1);
                            defaultServlet.addInitParameter("debug", "0");
                            defaultServlet.addInitParameter("listing", "true");
                            defaultServlet.addMapping("/");
                            context.addChild(defaultServlet);
                            context.addServletMapping("/", "default");

                            // create the Jasper Servlet instance to mount
                            StandardWrapper jspServlet = new StandardWrapper();
                            jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
                            jspServlet.setServletName("jsp");
                            jspServlet.setLoadOnStartup(1);
                            jspServlet.addInitParameter("fork", "false");
                            jspServlet.addInitParameter("xpoweredBy", "false");
                            jspServlet.addMapping("*.jsp");
                            jspServlet.addMapping("*.jspx");
                            context.addChild(jspServlet);
                            context.addServletMapping("*.jsp", "jsp");

                            // default mime-type mappings
                            configureMimeTypes(context);

                            /* Not implemented yet for tomcat
                            // set the virtual hosts
                            Iterator vh = virtualHosts.iterator();
                            while (vh.hasNext()) {
                                ctx.addVirtualHost((String)vh.next());
                            }
                            */

                            // set the init parameters
                            Iterator ip = initParameters.keySet().iterator();
                            while (ip.hasNext()) {
                                String paramName = (String) ip.next();
                                context.addParameter(paramName, (String) initParameters.get(paramName));
                            }

                            host.addChild(context);
                            context.getMapper().setDefaultHostName(host.getName());
                        } catch (Exception e) {
                            Debug.logError(e, "Problem mounting application [" + appInfo.name + " / " + appInfo.location + "]", module);
                        }
                    }
                }
            }
        }

    }

    public void stop() throws ContainerException {
        try {
            embedded.stop();
        } catch (LifecycleException e) {
            // don't throw this; or it will kill the rest of the shutdown process
            Debug.logError(e, module);
        }
    }

    protected void configureMimeTypes(Context context) throws ContainerException {
        Map mimeTypes = CatalinaContainer.getMimeTypes();
        if (mimeTypes != null && mimeTypes.size() > 0) {
            Iterator i = mimeTypes.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                context.addMimeMapping((String)entry.getKey(), (String)entry.getValue());
            }
        }
    }

    protected static synchronized Map getMimeTypes() throws ContainerException {
        if (mimeTypes != null && mimeTypes.size() > 0) {
            return mimeTypes;
        }

        if (mimeTypes == null) mimeTypes = new HashMap();
        URL xmlUrl = UtilURL.fromResource("mime-type.xml");

        // read the document
        Document mimeTypeDoc = null;
        try {
            mimeTypeDoc = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new ContainerException("Error reading the mime-type.xml config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ContainerException("Error reading the mime-type.xml config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new ContainerException("Error reading the mime-type.xml config file: " + xmlUrl, e);
        }

        if (mimeTypeDoc == null) {
            Debug.logError("Null document returned for mime-type.xml", module);
            return null;
        }

        // root element
        Element root = mimeTypeDoc.getDocumentElement();

        // mapppings
        Iterator elementIter = UtilXml.childElementList(root, "mime-mapping").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            String extension = UtilXml.childElementValue(curElement, "extension");
            String type = UtilXml.childElementValue(curElement, "mime-type");
            mimeTypes.put(extension, type);
        }

        return mimeTypes;
    }
}
