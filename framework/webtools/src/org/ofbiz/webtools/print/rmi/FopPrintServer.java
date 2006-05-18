/*
 * $Id$
 *
 * Copyright (c) 2001-2006 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.webtools.print.rmi;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UID;
import java.util.Locale;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.LinkedMap;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.widget.screen.ScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

/**
 * FopPrintServer
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.5
 */
public class FopPrintServer implements Container {

    public static final String module = FopPrintServer.class.getName();
    private static UtilCache settingsCache = new UtilCache("printer.applet.settings", 50, 50, 0, false, true);

    protected static HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static FopPrintServer instance = null;
    protected FopPrintRemote remote = null;
    protected String configFile = null;
    protected String name = null;

    // Container methods

    /**
     * @see org.ofbiz.base.container.Container#init(String[], String)
     */
    public void init(String[] args, String configFile) {
        this.configFile = configFile;
        instance = this;
    }

    public boolean start() throws ContainerException {
        // get the container config
        ContainerConfig.Container cfg = ContainerConfig.getContainer("rmi-print-server", configFile);
        ContainerConfig.Container.Property initialCtxProp = cfg.getProperty("use-initial-context");
        ContainerConfig.Container.Property lookupHostProp = cfg.getProperty("bound-host");
        ContainerConfig.Container.Property lookupPortProp = cfg.getProperty("bound-port");
        ContainerConfig.Container.Property lookupNameProp = cfg.getProperty("bound-name");
        ContainerConfig.Container.Property delegatorProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property clientProp = cfg.getProperty("client-factory");
        ContainerConfig.Container.Property serverProp = cfg.getProperty("server-factory");

        // check the required lookup-name property
        if (lookupNameProp == null || lookupNameProp.value == null || lookupNameProp.value.length() == 0) {
            throw new ContainerException("Invalid lookup-name defined in container configuration");
        } else {
            this.name = lookupNameProp.value;
        }

        // check the required delegator-name property
        if (delegatorProp == null || delegatorProp.value == null || delegatorProp.value.length() == 0) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        }

        String useCtx = initialCtxProp == null || initialCtxProp.value == null ? "false" : initialCtxProp.value;
        String host = lookupHostProp == null || lookupHostProp.value == null ? "localhost" : lookupHostProp.value;
        String port = lookupPortProp == null || lookupPortProp.value == null ? "1099" : lookupPortProp.value;
        boolean clientAuth = ContainerConfig.getPropertyValue(cfg, "ssl-client-auth", false);

        // setup the factories
        RMIClientSocketFactory csf = null;
        RMIServerSocketFactory ssf = null;

        // get the classloader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // load the factories
        if (clientProp != null && clientProp.value != null && clientProp.value.length() > 0) {
            try {
                Class c = loader.loadClass(clientProp.value);
                csf = (RMIClientSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }
        if (serverProp != null && serverProp.value != null && serverProp.value.length() > 0) {
            try {
                Class c = loader.loadClass(serverProp.value);
                ssf = (RMIServerSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }

        // set the client auth flag on our custom SSL socket factory
        if (ssf != null && ssf instanceof org.ofbiz.service.rmi.socket.ssl.SSLServerSocketFactory) {
            ((org.ofbiz.service.rmi.socket.ssl.SSLServerSocketFactory) ssf).setNeedClientAuth(clientAuth);
        }

        // get the delegator for this container
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorProp.value);

        // create the LocalDispatcher
        LocalDispatcher dispatcher = new GenericDispatcher(name, delegator);

        // create the RemoteDispatcher
        try {
            remote = new FopPrintRemoteImpl(dispatcher.getDispatchContext(), Locale.getDefault(), csf, ssf);
        } catch (RemoteException e) {
            throw new ContainerException("Unable to start the RMI Print Server", e);
        }

        if (!useCtx.equalsIgnoreCase("true")) {
            // bind RMIDispatcher to RMI Naming (Must be JRMP protocol)
            try {
                Naming.rebind("//" + host + ":" + port + "/" + name, remote);
            } catch (RemoteException e) {
                throw new ContainerException("Unable to bind RMI Print Server", e);
            } catch (java.net.MalformedURLException e) {
                throw new ContainerException("Invalid URL for binding", e);
            }
        } else {
            // bind RMIDispatcher to InitialContext (must be RMI protocol not IIOP)
            try {
                InitialContext ic = new InitialContext();
                ic.rebind(name, remote);
            } catch (NamingException e) {
                throw new ContainerException("Unable to bind RMI Print Server to JNDI", e);
            }

            // check JNDI
            try {
                InitialContext ic = new InitialContext();
                Object o = ic.lookup(name);
                if (o == null) {
                    throw new NamingException("Object came back null");
                }
            } catch (NamingException e) {
                throw new ContainerException("Unable to lookup bound objects", e);
            }
        }

        return true;
    }

    public void stop() throws ContainerException {
    }

    public FopPrintRemote getRemote() {
        return this.remote;
    }

    public static String getXslFo(HttpServletRequest req, HttpServletResponse resp) {
        LocalDispatcher dispatcher = (LocalDispatcher) req.getAttribute("dispatcher");
        Map reqParams = UtilHttp.getParameterMap(req);
        reqParams.put("locale", UtilHttp.getLocale(req));

        String screenUri = (String) reqParams.remove("screenUri");
        if (screenUri != null && reqParams.size() > 0) {
            String base64String = null;
            try {
                byte[] bytes = FopPrintServer.getXslFo(dispatcher.getDispatchContext(), screenUri, reqParams);
                base64String = new String(Base64.encodeBase64(bytes));
            } catch (GeneralException e) {
                Debug.logError(e, module);
                try {
                    resp.sendError(500);
                } catch (IOException e1) {
                    Debug.logError(e1, module);
                }
            }
            if (base64String != null) {
                try {
                    Writer out = resp.getWriter();
                    out.write(base64String);
                } catch (IOException e) {
                    try {
                        resp.sendError(500);
                    } catch (IOException e1) {
                        Debug.logError(e1, module);
                    }
                }
            }
        }

        return null;
    }

    public static byte[] getXslFo(DispatchContext dctx, String screen, Map parameters) throws GeneralException {
        // run as the system user
        GenericValue system = null;
        try {
            system = dctx.getDelegator().findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
        } catch (GenericEntityException e) {
            throw new GeneralException(e.getMessage(), e);
        }
        parameters.put("userLogin", system);
        if (!parameters.containsKey("locale")) {
            parameters.put("locale", Locale.getDefault());
        }

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenRenderer screens = new ScreenRenderer(writer, null, htmlScreenRenderer);
            screens.populateContextForService(dctx, parameters);
            screens.render(screen);
        } catch (Throwable t) {
            throw new GeneralException("Problems rendering FOP XSL-FO", t);
        }
        return writer.toString().getBytes();
    }

    public static String readFopPrintServerSettings(HttpServletRequest req, HttpServletResponse resp) {
        Map screenPrinterMap = new LinkedMap();
        Cookie[] cookies = req.getCookies();
        String sessionId = null;

        for (int i = 0; i < cookies.length; i++) {
            if ("ofbiz.print.session".equals(cookies[i].getName())) {
                String value = cookies[i].getValue();
                if (value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                if (UtilValidate.isNotEmpty(value)) {
                    sessionId = value;
                }
            }
        }

        Map settingsMap = null;
        if (sessionId != null) {
            settingsMap = (Map) settingsCache.get(sessionId);
        } else {
            sessionId = new UID().toString();
            Debug.log("Created new session ID: " + sessionId, module);
        }

        if (settingsMap == null) {
            settingsMap = FastMap.newInstance();
            Debug.log("Empty settings map created for ID: " + sessionId, module);
        } else {
            Debug.log("Found settings [" + settingsMap.size() + "] for ID: " + sessionId, module);
        }

        String[] screens = req.getParameterValues("screen");
        for (int i = 0; i < screens.length; i++) {
            String screen = screens[i].indexOf("?") != -1 ?
                screens[i].substring(0, screens[i].indexOf("?")) : screens[i];
            Debug.log("Checking setting for FOP report: " + screen, module);

            String reportSetting = (String) settingsMap.get(screen);
            if (reportSetting != null) {
                screenPrinterMap.put(screens[i], reportSetting);
                Debug.log("Found matching setting", module);
            }

            if (!screenPrinterMap.containsKey(screens[i])) {
               Debug.log("No matching setting found; using null");
               screenPrinterMap.put(screens[i], null);
            }
        }

        req.setAttribute("screenPrinterMap", screenPrinterMap);
        req.setAttribute("sessionId", sessionId);
        //Debug.log("Screen Printer Map ID [" + sessionId + "] - " + screenPrinterMap, module);

        return "success";
    }

    public static String writeFopPrintServerSettings(HttpServletRequest req, HttpServletResponse resp) {
        String sessionId = req.getParameter("sessionId");
        if (sessionId != null) {
            Cookie printCookie = new Cookie("ofbiz.print.session", sessionId);
            printCookie.setMaxAge(60 * 60 * 24 * 365);
            printCookie.setPath("/");
            resp.addCookie(printCookie);
            printCookie(printCookie);
        } else {
            Debug.logError("No session ID returned from applet", module);
        }

        return "success";
    }

    public static String receiveUpdateAppletSettings(HttpServletRequest req, HttpServletResponse resp) {
        Map paramMap = UtilHttp.getParameterMap(req);
        String sessionId = (String) paramMap.remove("sessionId");
        String message = "OK";
        if (sessionId != null) {
            settingsCache.put(sessionId, paramMap);
            //Debug.log("Received Settings:", module);
            //Debug.log("" + paramMap, module);
            Debug.log("Stored settings for session: " + sessionId, module);
        } else {
            message = "FAIL";
        }

        Writer out = null;
        try {
            out = resp.getWriter();
            out.write(message);
        } catch (IOException e) {
            Debug.logError(e, module);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return null;
    }

    public static String return404(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.sendError(404);
        } catch (IOException e) {
            Debug.logError(e, module);
        }
        return null;
    }

    private static void printCookie(Cookie cookie) {
        Debug.log("Cookie - " + cookie.getName() + " = " + cookie.getValue() +
                " ; " + cookie.getMaxAge() + " @ " + cookie.getPath(), module);
    }
}
