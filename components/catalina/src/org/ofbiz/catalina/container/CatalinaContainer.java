/*
 * $Id: CatalinaContainer.java,v 1.16 2004/07/06 17:07:18 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
import org.ofbiz.entity.GenericDelegator;

import org.apache.catalina.startup.Embedded;
import org.apache.catalina.logger.LoggerBase;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import org.apache.catalina.Connector;
import org.apache.catalina.Cluster;
import org.apache.catalina.Manager;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;
import org.apache.catalina.cluster.tcp.ReplicationValve;
import org.apache.catalina.cluster.tcp.ReplicationListener;
import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.cluster.mcast.McastService;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.RequestDumperValve;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.core.StandardEngine;
import org.apache.coyote.tomcat5.CoyoteServerSocketFactory;
import org.apache.coyote.tomcat5.CoyoteConnector;
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
 * @version    $Revision: 1.16 $
 * @since      3.1
 */
public class CatalinaContainer implements Container {

    public static final String CATALINA_HOSTS_HOME = System.getProperty("ofbiz.home") + "/components/catalina/hosts";
    public static final String J2EE_SERVER = "OFBiz Container 3.1";
    public static final String J2EE_APP = "OFBiz";
    public static final String module = CatalinaContainer.class.getName();
    protected static Map mimeTypes = new HashMap();

    protected GenericDelegator delegator = null;
    protected Embedded embedded = null;
    protected Map clusterConfig = new HashMap();
    protected Map engines = new HashMap();
    protected Map hosts = new HashMap();
    protected boolean usePersistentManager = false;
    protected boolean contextReloadable = false;
    protected boolean crossContext = false;
    protected boolean distribute = false;

    protected boolean enableDefaultMimeTypes = true;

    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[])
     */
    public void init(String[] args) {
    }
    
    public boolean start(String configFileLocation) throws ContainerException {
        // set catalina_home
        System.setProperty("catalina.home", System.getProperty("ofbiz.home") + "/components/catalina");

        // get the container config
        ContainerConfig.Container cc = ContainerConfig.getContainer("catalina-container", configFileLocation);
        if (cc == null) {
            throw new ContainerException("No catalina-container configuration found in container config!");
        }

        // embedded properties
        boolean useNaming = ContainerConfig.getPropertyValue(cc, "use-naming", false);
        int debug = ContainerConfig.getPropertyValue(cc, "debug", 0);

        // grab some global context settings
        this.delegator = GenericDelegator.getGenericDelegator(ContainerConfig.getPropertyValue(cc, "delegator-name", "default"));
        this.usePersistentManager = ContainerConfig.getPropertyValue(cc, "apps-db-persistent-mgr", false);
        this.contextReloadable = ContainerConfig.getPropertyValue(cc, "apps-context-reloadable", false);
        this.crossContext = ContainerConfig.getPropertyValue(cc, "apps-cross-context", true);
        this.distribute = ContainerConfig.getPropertyValue(cc, "apps-distributable", true);        

        // create the instance of Embedded
        embedded = new Embedded();
        embedded.setDebug(debug);
        embedded.setUseNaming(useNaming);
        embedded.setLogger(new DebugLogger());

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
            throw new ContainerException("Cannot load CatalinaContainer; no connectors defined!");
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
        String jvmRoute = ContainerConfig.getPropertyValue(engineConfig, "jvm-route", null);
        if (jvmRoute != null) {
            engine.setJvmRoute(jvmRoute);
        }

        // cache the engine
        engines.put(engine.getName(), engine);

        // create a default virtual host; others will be created as needed
        Host host = createHost(engine, hostName);
        hosts.put(engineName + "._DEFAULT", host);

        // configure clustering
        List clusterProps = engineConfig.getPropertiesWithValue("cluster");
        if (clusterProps != null && clusterProps.size() > 1) {
            throw new ContainerException("Only one cluster configuration allowed per engine");
        }

        if (clusterProps != null && clusterProps.size() > 0) {
            ContainerConfig.Container.Property clusterProp = (ContainerConfig.Container.Property) clusterProps.get(0);
            createCluster(clusterProp, host);
            clusterConfig.put(engineName, clusterProp);
        }

        // request dumper valve
        boolean enableRequestDump = ContainerConfig.getPropertyValue(engineConfig, "enable-request-dump", false);
        if (enableRequestDump) {
            RequestDumperValve rdv = new RequestDumperValve();
            engine.addValve(rdv);
        }

        // configure the access log valve
        String logDir = ContainerConfig.getPropertyValue(engineConfig, "access-log-dir", null);
        AccessLogValve al = null;
        if (logDir != null) {
            al = new AccessLogValve();
            if (!logDir.startsWith("/")) {
                logDir = System.getProperty("ofbiz.home") + "/" + logDir;
            }
            File logFile = new File(logDir);
            if (!logFile.isDirectory()) {
                throw new ContainerException("Log directory [" + logDir + "] is not available; make sure the directory is created");
            }
            al.setDirectory(logFile.getAbsolutePath());
        }

        String alp2 = ContainerConfig.getPropertyValue(engineConfig, "access-log-pattern", null);
        if (al != null && !UtilValidate.isEmpty(alp2)) {
            al.setPattern(alp2);
        }

        String alp3 = ContainerConfig.getPropertyValue(engineConfig, "access-log-prefix", null);
        if (al != null && !UtilValidate.isEmpty(alp3)) {
            al.setPrefix(alp3);
        }


        boolean alp4 = ContainerConfig.getPropertyValue(engineConfig, "access-log-resolve", true);
        if (al != null) {
            al.setResolveHosts(alp4);
        }

        boolean alp5 = ContainerConfig.getPropertyValue(engineConfig, "access-log-rotate", false);
        if (al != null) {
            al.setRotatable(alp5);
        }

        if (al != null) {
            engine.addValve(al);
        }

        embedded.addEngine(engine);
        return engine;
    }

    protected Host createHost(Engine engine, String hostName) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Host without Embedded instance!");
        }

        Host host = embedded.createHost(hostName, CATALINA_HOSTS_HOME);
        host.setDeployOnStartup(true);
        host.setAutoDeploy(true);
        engine.addChild(host);
        hosts.put(engine.getName() + hostName, host);

        return host;
    }

    protected Cluster createCluster(ContainerConfig.Container.Property clusterProps, Host host) throws ContainerException {
        String defaultValveFilter = ".*.gif;.*.js;.*.jpg;.*.htm;.*.html;.*.txt;";

        ReplicationValve clusterValve = new ReplicationValve();
        clusterValve.setFilter(ContainerConfig.getPropertyValue(clusterProps, "rep-valve-filter", defaultValveFilter));

        String mcb = ContainerConfig.getPropertyValue(clusterProps, "mcast-bind-addr", null);
        String mca = ContainerConfig.getPropertyValue(clusterProps, "mcast-addr", null);
        int mcp = ContainerConfig.getPropertyValue(clusterProps, "mcast-port", -1);
        int mcd = ContainerConfig.getPropertyValue(clusterProps, "mcast-freq", 500);
        int mcf = ContainerConfig.getPropertyValue(clusterProps, "mcast-drop-time", 3000);

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

        String tla = ContainerConfig.getPropertyValue(clusterProps, "tcp-listen-host", "auto");
        int tlp = ContainerConfig.getPropertyValue(clusterProps, "tcp-listen-port", 4001);
        int tlt = ContainerConfig.getPropertyValue(clusterProps, "tcp-sector-timeout", 100);
        int tlc = ContainerConfig.getPropertyValue(clusterProps, "tcp-thread-count", 6);
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
        trans.setReplicationMode(ContainerConfig.getPropertyValue(clusterProps, "replication-mode", "pooled"));

        String mgrClassName = ContainerConfig.getPropertyValue(clusterProps, "manager-class", "org.apache.catalina.cluster.session.DeltaManager");
        int debug = ContainerConfig.getPropertyValue(clusterProps, "debug", 0);
        boolean expireSession = ContainerConfig.getPropertyValue(clusterProps, "expire-session", false);
        boolean useDirty = ContainerConfig.getPropertyValue(clusterProps, "use-dirty", true);

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

        // set the cluster to the host
        host.setCluster(cluster);
        Debug.logInfo("Catalina Cluster [" + cluster.getClusterName() + "] configured for host - " + host.getName(), module);

        return cluster;
    }

    protected Connector createConnector(ContainerConfig.Container.Property connectorProp) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Connector without Embedded instance!");
        }

        String conType = ContainerConfig.getPropertyValue(connectorProp, "type", "http");
        String conAddr = ContainerConfig.getPropertyValue(connectorProp, "host", "0.0.0.0");
        int conPort = ContainerConfig.getPropertyValue(connectorProp, "port", 8080);
        int debug = ContainerConfig.getPropertyValue(connectorProp, "debug", 0);

        // create the connector
        CoyoteConnector connector = (CoyoteConnector) embedded.createConnector(conAddr, conPort, conType);
        connector.setDebug(debug);

        boolean enableLookups = ContainerConfig.getPropertyValue(connectorProp, "enable-lookups", true);
        connector.setEnableLookups(enableLookups);

        boolean enableKeepAlive = ContainerConfig.getPropertyValue(connectorProp, "enable-keep-alive", true);
        connector.setKeepAlive(enableKeepAlive);

        int redirect = ContainerConfig.getPropertyValue(connectorProp, "port", -1);
        if (redirect > 0) {
            connector.setRedirectPort(redirect);
        }

        String proxyName = ContainerConfig.getPropertyValue(connectorProp, "proxy-name", null);
        int proxyPort = ContainerConfig.getPropertyValue(connectorProp, "proxy-port", 0);
        if (proxyName != null && proxyPort > 0) {
            connector.setProxyName(proxyName);
            connector.setProxyPort(proxyPort);
        }

        String compression = ContainerConfig.getPropertyValue(connectorProp, "compression", "off");
        connector.setCompression(compression); // on, off, force

        boolean allowTrace = ContainerConfig.getPropertyValue(connectorProp, "allow-trace", false);
        connector.setAllowTrace(allowTrace);

        int minProc = ContainerConfig.getPropertyValue(connectorProp, "min-processors", 5);
        connector.setMinProcessors(minProc);

        int maxProc = ContainerConfig.getPropertyValue(connectorProp, "max-processors", 20);
        connector.setMaxProcessors(maxProc);

        int maxKeepAlive = ContainerConfig.getPropertyValue(connectorProp, "max-keep-alive", 100);
        connector.setMaxKeepAliveRequests(maxKeepAlive);

        int maxHeaderSize = ContainerConfig.getPropertyValue(connectorProp, "max-header-size", 4096);
        connector.setMaxHttpHeaderSize(maxHeaderSize);

        int maxPostSize = ContainerConfig.getPropertyValue(connectorProp, "max-post-size", 2097152);
        connector.setMaxPostSize(maxPostSize);

        int bufferSize = ContainerConfig.getPropertyValue(connectorProp, "buffer-size", 2048);
        connector.setBufferSize(bufferSize);

        int acceptCount = ContainerConfig.getPropertyValue(connectorProp, "accept-count", 10);
        connector.setAcceptCount(acceptCount);

        int conLinger = ContainerConfig.getPropertyValue(connectorProp, "connection-linger", -1);
        connector.setConnectionLinger(conLinger);

        int conTimeout = ContainerConfig.getPropertyValue(connectorProp, "connection-timeout", 60000);
        connector.setConnectionTimeout(conTimeout);

        int uploadTimeout = ContainerConfig.getPropertyValue(connectorProp, "upload-timeout", 300000);
        connector.setConnectionUploadTimeout(uploadTimeout);

        int socketTimeout = ContainerConfig.getPropertyValue(connectorProp, "socket-timeout", 0);
        connector.setServerSocketTimeout(socketTimeout);

        if ("https".equals(conType)) {
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

        String keystore = ContainerConfig.getPropertyValue(connectorProp, "keystore", null);
        String password = ContainerConfig.getPropertyValue(connectorProp, "password", null);
        String storeType = ContainerConfig.getPropertyValue(connectorProp, "keystore-type", null);
        String keyAlias = ContainerConfig.getPropertyValue(connectorProp, "key-alias", null);
        String protocol = ContainerConfig.getPropertyValue(connectorProp, "ssl-protocol", null);
        String ciphers = ContainerConfig.getPropertyValue(connectorProp, "ssl-ciphers", null);
        String algorithm = ContainerConfig.getPropertyValue(connectorProp, "ssl-algorithm", null);
        String clientAuth = ContainerConfig.getPropertyValue(connectorProp, "ssl-client-auth", null);

        // keystore is requrired for SSL
        if (keystore == null) {
            throw new ContainerException("No keystore setting found for SSL connector!");
        }
        sf.setKeystoreFile(keystore);

        // other 'optional' parameters
        if (password != null) {
            sf.setKeystorePass(password);
        }

        if (storeType != null) {
            sf.setKeystoreType(storeType);
        }

        if (keyAlias != null) {
            sf.setKeyAlias(keyAlias);
        }

        if (clientAuth != null) {
            sf.setClientAuth(clientAuth);
        }

        if (protocol != null) {
            sf.setProtocol(protocol);
        }

        if (ciphers != null) {
            sf.setCiphers(ciphers);
        }

        if (algorithm != null) {
            sf.setAlgorithm(algorithm);
        }



        connector.setFactory(sf);
    }

    protected Context createContext(ComponentConfig.WebappInfo appInfo) throws ContainerException {
        // webapp settings
        Map initParameters = appInfo.getInitParameters();
        List virtualHosts = appInfo.getVirtualHosts();
        Engine engine = (Engine) engines.get(appInfo.server);
        if (engine == null) {
            Debug.logWarning("Server with name [" + appInfo.server + "] not found; not mounting [" + appInfo.name + "]", module);
            return null;
        }

        // set the root location (make sure we set the paths correctly)
        String location = appInfo.componentConfig.getRootLocation() + appInfo.location;
        location = location.replace('\\', '/');
        if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        // get the mount point
        String mount = appInfo.mountPoint;
        if (mount.endsWith("/*")) {
            mount = mount.substring(0, mount.length() - 2);
        }

        // configure persistent sessions
        Manager sessionMgr = null;
        if (usePersistentManager) {
            sessionMgr = new PersistentManager();
            ((PersistentManager)sessionMgr).setStore(new OfbizStore(delegator));
        } else {
            sessionMgr = new StandardManager();
        }

        // create the web application context
        StandardContext context = (StandardContext) embedded.createContext(mount, location);
        context.setJ2EEApplication(J2EE_APP);
        context.setJ2EEServer(J2EE_SERVER);
        context.setLoader(embedded.createLoader(ClassLoaderContainer.getClassLoader()));

        context.setDisplayName(appInfo.name);
        context.setDocBase(location);

        context.setReloadable(contextReloadable);
        context.setDistributable(distribute);
        context.setCrossContext(crossContext);
        context.setManager(sessionMgr);
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

        // set the init parameters
        Iterator ip = initParameters.keySet().iterator();
        while (ip.hasNext()) {
            String paramName = (String) ip.next();
            context.addParameter(paramName, (String) initParameters.get(paramName));
        }

        if (virtualHosts == null || virtualHosts.size() == 0) {
            Host host = (Host) hosts.get(engine.getName() + "._DEFAULT");
            host.addChild(context);
            context.getMapper().setDefaultHostName(host.getName());
        } else {
            Iterator vhi = virtualHosts.iterator();
            boolean isFirst = true;
            while (vhi.hasNext()) {
                String hostName = (String) vhi.next();
                Host host = (Host) hosts.get(engine.getName() + "." + hostName);
                if (host == null) {
                    host = createHost(engine, hostName);
                    host.addChild(context);
                    if (isFirst) {
                        context.getMapper().setDefaultHostName(host.getName());
                        isFirst = false;
                    }
                    hosts.put(engine.getName() + "." + hostName, host);
                }
            }
        }

        return context;
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
                    createContext(appInfo);
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

    class DebugLogger extends LoggerBase {

        public void log(String message) {
            Debug.log(message, module);
        }
    }
}
