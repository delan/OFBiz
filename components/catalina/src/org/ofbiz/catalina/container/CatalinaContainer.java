/*
 * $Id: CatalinaContainer.java,v 1.6 2004/05/26 00:22:20 ajzeneski Exp $
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
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ClassLoaderContainer;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.UtilValidate;
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
import org.apache.catalina.Cluster;
import org.apache.catalina.Valve;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;
import org.apache.catalina.cluster.tcp.ReplicationValve;
import org.apache.catalina.cluster.tcp.ReplicationListener;
import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.cluster.mcast.McastService;
import org.apache.catalina.valves.AccessLogValve;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.core.StandardEngine;
import org.apache.coyote.tomcat5.CoyoteServerSocketFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * --- Access Log Pattern Information - From Tomcat 5 AccessLogValve.java
 * <p>Patterns for the logged message may include constant text or any of the
 * following replacement strings, for which the corresponding information
 * from the specified Response is substituted:</p>
 * <ul>
 * <li><b>%a</b> - Remote IP address
 * <li><b>%A</b> - Local IP address
 * <li><b>%b</b> - Bytes sent, excluding HTTP headers, or '-' if no bytes
 *     were sent
 * <li><b>%B</b> - Bytes sent, excluding HTTP headers
 * <li><b>%h</b> - Remote host name
 * <li><b>%H</b> - Request protocol
 * <li><b>%l</b> - Remote logical username from identd (always returns '-')
 * <li><b>%m</b> - Request method
 * <li><b>%p</b> - Local port
 * <li><b>%q</b> - Query string (prepended with a '?' if it exists, otherwise
 *     an empty string
 * <li><b>%r</b> - First line of the request
 * <li><b>%s</b> - HTTP status code of the response
 * <li><b>%S</b> - User session ID
 * <li><b>%t</b> - Date and time, in Common Log Format format
 * <li><b>%u</b> - Remote user that was authenticated
 * <li><b>%U</b> - Requested URL path
 * <li><b>%v</b> - Local server name
 * <li><b>%D</b> - Time taken to process the request, in millis
 * <li><b>%T</b> - Time taken to process the request, in seconds
 * </ul>
 * <p>In addition, the caller can specify one of the following aliases for
 * commonly utilized patterns:</p>
 * <ul>
 * <li><b>common</b> - <code>%h %l %u %t "%r" %s %b</code>
 * <li><b>combined</b> -
 *   <code>%h %l %u %t "%r" %s %b "%{Referer}i" "%{User-Agent}i"</code>
 * </ul>
 *
 * <p>
 * There is also support to write information from the cookie, incoming
 * header, the Session or something else in the ServletRequest.<br>
 * It is modeled after the apache syntax:
 * <ul>
 * <li><code>%{xxx}i</code> for incoming headers
 * <li><code>%{xxx}c</code> for a specific cookie
 * <li><code>%{xxx}r</code> xxx is an attribute in the ServletRequest
 * <li><code>%{xxx}s</code> xxx is an attribute in the HttpSession
 * </ul>
 * </p>
 *
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.6 $
 * @since      May 21, 2004
 */
public class CatalinaContainer implements Container {

    public static final String CATALINA_HOSTS_HOME = System.getProperty("ofbiz.home") + "/components/catalina/hosts";
    public static final String module = CatalinaContainer.class.getName();
    protected static Map mimeTypes = new HashMap();

    protected Embedded embedded = null;
    protected Map engines = new HashMap();
    protected Map hosts = new HashMap();
    protected boolean crossContext = false;
    protected boolean distribute = false;

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
        boolean useNaming = getPropertyValue(cc, "use-naming", false);
        int debug = getPropertyValue(cc, "debug", 0);

        this.crossContext = getPropertyValue(cc, "apps-cross-context", true);
        this.distribute = getPropertyValue(cc, "apps-distributable", true);
                
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

        // set the JVM Route property (JK/JK2)
        String jvmRoute = getPropertyValue(engineConfig, "jvm-route", null);
        if (jvmRoute != null) {
            engine.setJvmRoute(jvmRoute);
        }

        // cache the engine
        engines.put(engine.getName(), engine);

        // create a default virtual host; others will be created as needed
        Host host = createHost(engine, hostName);
        hosts.put(engineName + "._DEFAULT", host);

        // configure the access log valve
        ContainerConfig.Container.Property alp1 = engineConfig.getProperty("access-log-dir");
        AccessLogValve al = null;
        if (alp1 != null) {
            al = new AccessLogValve();
            String logDir = alp1.value;
            if (!logDir.startsWith("/")) {
                logDir = System.getProperty("ofbiz.home") + "/" + alp1.value;
            }
            File logFile = new File(logDir);
            if (!logFile.isDirectory()) {
                throw new ContainerException("Log directory [" + logDir + "] is not available; make sure the directory is created");
            }
            al.setDirectory(logFile.getAbsolutePath());
        }
        ContainerConfig.Container.Property alp2 = engineConfig.getProperty("access-log-pattern");
        if (al != null && alp2 != null) {
            al.setPattern(alp2.value);
        }
        ContainerConfig.Container.Property alp3 = engineConfig.getProperty("access-log-prefix");
        if (al != null && alp3 != null) {
            al.setPrefix(alp3.value);
        }
        ContainerConfig.Container.Property alp4 = engineConfig.getProperty("access-log-resolve");
        if (al != null && alp4 != null) {
            al.setResolveHosts("true".equalsIgnoreCase(alp4.value));
        }
        ContainerConfig.Container.Property alp5 = engineConfig.getProperty("access-log-rotate");
        if (al != null && alp5 != null) {
            al.setRotatable("true".equalsIgnoreCase(alp5.value));
        }

        if (al != null) {
            engine.addValve(al);
        }

        // configure clustering
        ContainerConfig.Container.Property clusterProps = engineConfig.getProperty("cluster");
        if (clusterProps != null) {
            createCluster(clusterProps, engine);
        }

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

    protected Cluster createCluster(ContainerConfig.Container.Property clusterProps, Engine engine) throws ContainerException {
        String defaultValveFilter = ".*.gif;.*.js;.*.jpg;.*.htm;.*.html;.*.txt;";

        ReplicationValve clusterValve = new ReplicationValve();
        clusterValve.setFilter(getPropertyValue(clusterProps, "rep-valve-filter", defaultValveFilter));

        String mcb = getPropertyValue(clusterProps, "mcast-bind-addr", null);
        String mca = getPropertyValue(clusterProps, "mcast-addr", null);
        int mcp = getPropertyValue(clusterProps, "mcast-port", -1);
        int mcd = getPropertyValue(clusterProps, "mcast-freq", 500);
        int mcf = getPropertyValue(clusterProps, "mcast-drop-time", 3000);

        if (mca == null || mcp == -1) {
            throw new ContainerException("Cluster configuration requires mcast-addr and mcast-port properties");
        }

        McastService mcast = new McastService();
        if (mcb != null) {
            mcast.setMcastBindAddress(mcb);
        }

        mcast.setMcastAddr(mca);
        mcast.setMcastPort(mcp);
        mcast.setMcastDropTime(mcd);
        mcast.setMcastFrequency(mcf);

        String tla = getPropertyValue(clusterProps, "tcp-listen-host", "auto");
        int tlp = getPropertyValue(clusterProps, "tcp-listen-port", 4001);
        int tlt = getPropertyValue(clusterProps, "tcp-sector-timeout", 100);
        int tlc = getPropertyValue(clusterProps, "tcp-thread-count", 6);
        //String tls = getPropertyValue(clusterProps, "", "");

        if (tlp == -1) {
            throw new ContainerException("Cluster configuration requires tcp-listen-port property");
        }

        ReplicationListener listener = new ReplicationListener();
        listener.setTcpListenAddress(tla);
        listener.setTcpListenPort(tlp);
        listener.setTcpSelectorTimeout(tlt);
        listener.setTcpThreadCount(tlc);
        //listener.setIsSenderSynchronized(false);

        ReplicationTransmitter trans = new ReplicationTransmitter();
        trans.setReplicationMode(getPropertyValue(clusterProps, "replication-mode", "pooled"));

        String mgrClassName = getPropertyValue(clusterProps, "manager-class", "org.apache.catalina.cluster.session.DeltaManager");
        int debug = getPropertyValue(clusterProps, "debug", 0);
        boolean expireSession = getPropertyValue(clusterProps, "expire-session", false);
        boolean useDirty = getPropertyValue(clusterProps, "use-dirty", true);

        SimpleTcpCluster cluster = new SimpleTcpCluster();
        cluster.setClusterName(clusterProps.name);
        cluster.setManagerClassName(mgrClassName);
        cluster.setDebug(debug);
        cluster.setExpireSessionsOnShutdown(expireSession);
        cluster.setUseDirtyFlag(useDirty);

        cluster.setClusterReceiver(listener);
        cluster.setClusterSender(trans);
        cluster.setMembershipService(mcast);
        cluster.addValve(clusterValve);
        cluster.setPrintToScreen(true);
        engine.setCluster(cluster);

        return cluster;
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

                            context.setDistributable(distribute);
                            context.setCrossContext(crossContext);
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

    protected String getPropertyValue(ContainerConfig.Container parentProp, String name, String defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return prop.value;
        }
    }

    protected int getPropertyValue(ContainerConfig.Container parentProp, String name, int defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            int num = defaultValue;
            try {
                num = Integer.parseInt(prop.value);
            } catch (Exception e) {
                return defaultValue;
            }
            return num;
        }
    }

    protected boolean getPropertyValue(ContainerConfig.Container parentProp, String name, boolean defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(prop.value);
        }
    }

    protected String getPropertyValue(ContainerConfig.Container.Property parentProp, String name, String defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return prop.value;
        }
    }

    protected int getPropertyValue(ContainerConfig.Container.Property parentProp, String name, int defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            int num = defaultValue;
            try {
                num = Integer.parseInt(prop.value);
            } catch (Exception e) {
                return defaultValue;
            }
            return num;
        }
    }

    protected boolean getPropertyValue(ContainerConfig.Container.Property parentProp, String name, boolean defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(prop.value);
        }
    }
}
