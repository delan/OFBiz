/*
 * $Id: CatalinaContainer.java,v 1.1 2004/05/22 21:14:48 ajzeneski Exp $
 *
 */
package org.ofbiz.catalina.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
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

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      May 21, 2004
 */
public class CatalinaContainer implements Container {

    public static final String CATALINA_HOSTS_HOME = System.getProperty("ofbiz.home") + "/components/catalina/hosts";
    public static final String module = CatalinaContainer.class.getName();
    protected static Map mimeTypes = new HashMap();

    protected Embedded embedded = null;
    protected Map servers = new HashMap();
    protected Map hosts = new HashMap();

    protected boolean enableDefaultMimeTypes = true;

    public boolean start(String configFileLocation) throws ContainerException {
        // get the container config
        ContainerConfig.Container cc = ContainerConfig.getContainer("catalina-container", configFileLocation);
        String useNaming = cc.getProperty("useNaming").value;
        String debug = cc.getProperty("debug").value;

        // set catalina_home
        System.setProperty("catalina.home", System.getProperty("ofbiz.home") + "/components/catalina");

        // create the instance of Embedded
        embedded = new Embedded();
        embedded.setDebug(0);
        embedded.setUseNaming("true".equalsIgnoreCase(useNaming));
        embedded.setLogger(new SystemOutLogger());



        String engineName = "default-server";
        String hostName = "0.0.0.0";

        // Create an engine
        Engine engine = embedded.createEngine();
        engine.setName(engineName);
        engine.setDefaultHost(hostName);

        // create a default virtual host; others will be created as needed
        Host host = embedded.createHost(hostName, CATALINA_HOSTS_HOME);
        hosts.put(engineName + "._DEFAULT", host);
        engine.addChild(host);

        // Add in all webapps (components)
        loadComponents(embedded);

        // Install the assembled container hierarchy
        embedded.addEngine(engine);

        // Assemble and install a default HTTP connector
        Connector connector = embedded.createConnector("0.0.0.0", 8080, "http");
        embedded.addConnector(connector);

        // Start the embedded server
        try {
            embedded.start();
        } catch (LifecycleException e) {
            throw new ContainerException(e);
        }

        return true;
    }

    protected Engine createServer(ContainerConfig.Container.Property serverConfig) throws ContainerException {
        if (embedded == null) {
            throw new ContainerException("Cannot create Engine without Embedded instance!");
        }

        ContainerConfig.Container.Property defaultHostProp = serverConfig.getProperty("default-host");
        if (defaultHostProp == null) {
            throw new ContainerException("default-host element of server property is required for catalina!");
        }

        String engineName = serverConfig.name;
        String hostName = defaultHostProp.value;

        Engine engine = embedded.createEngine();
        engine.setName(engineName);
        engine.setDefaultHost(hostName);

        // add connectors

        return engine;
    }

    protected void loadComponents(Embedded embedded) {

        // load the applications
        Collection componentConfigs = ComponentConfig.getAllComponents();
        if (componentConfigs != null) {
            Iterator components = componentConfigs.iterator();
            while (components.hasNext()) {
                ComponentConfig component = (ComponentConfig) components.next();
                Iterator appInfos = component.getWebappInfos().iterator();
                while (appInfos.hasNext()) {
                    ComponentConfig.WebappInfo appInfo = (ComponentConfig.WebappInfo) appInfos.next();
                    //List virtualHosts = appInfo.getVirtualHosts();
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

                            context.setDistributable(true);
                            context.setCrossContext(true);
                            context.addJspMapping("*.jsp");
                            context.addWelcomeFile("index.jsp");
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

                            // create the Jasper Servlet instance to mount
                            StandardWrapper jspServlet = new StandardWrapper();
                            jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
                            jspServlet.setServletName("jsp");
                            jspServlet.setServletName("jspx");
                            jspServlet.setLoadOnStartup(1);
                            jspServlet.addInitParameter("fork", "false");
                            jspServlet.addInitParameter("xpoweredBy", "false");
                            jspServlet.addMapping("*.jsp");
                            context.addChild(jspServlet);

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
