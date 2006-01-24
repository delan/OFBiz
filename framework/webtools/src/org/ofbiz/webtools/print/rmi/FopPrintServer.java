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
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Locale;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;

/**
 * FopPrintServer
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.5
 */
public class FopPrintServer implements Container {

    public static final String module = FopPrintServer.class.getName();

    protected FopPrintRemote remote = null;
    protected String configFile = null;
    protected String name = null;

    // Container methods

    /**
     * @see org.ofbiz.base.container.Container#init(String[], String)
     */
    public void init(String[] args, String configFile) {
        this.configFile = configFile;
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

    public static String readFopPrintServerCookies(HttpServletRequest req, HttpServletResponse resp) {
        Map screenPrinterMap = new LinkedMap();
        Cookie[] cookies = req.getCookies();

        String[] screens = req.getParameterValues("screen");
        for (int i = 0; i < screens.length; i++) {
            for (int x = 0; x < cookies.length; x++) {
                String screen = screens[i].indexOf("?") != -1 ?
                    screens[i].substring(0, screens[i].indexOf("?")) : screens[i];
                Debug.log("Checking for cookie for FOP report: " + screen, module);

                if (cookies[x].getName().equals("ofbiz.print." + screen)) {
                    String value = cookies[x].getValue();
                    if (value.startsWith("\"")) {
                        value = value.substring(1);
                    }
                    if (value.endsWith("\"")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    screenPrinterMap.put(screens[i], value);
                    Debug.log("Found matching cookie", module);
                    printCookie(cookies[x]);
                } else if (cookies[x].getName().startsWith("ofbiz.print")) {
                    printCookie(cookies[x]);
                }
            }
            if (!screenPrinterMap.containsKey(screens[i])) {
                Debug.log("No matching cookie found; setting printer to null");
                screenPrinterMap.put(screens[i], null);
            }
        }

        req.setAttribute("screenPrinterMap", screenPrinterMap);
        Debug.log("Screen Printer Map - " + screenPrinterMap, module);
        return "success";
    }

    public static String writeFopPrintServerCookies(HttpServletRequest req, HttpServletResponse resp) {
        // get the screens used
        for (int i = 1; i < 11; i++) {
            // get the screens/printers used
            String printer = req.getParameter("printer." + i);
            String screen = req.getParameter("screen." + i);
            if (screen != null) {
                screen = screen.indexOf("?") != -1 ?
                        screen.substring(0, screen.indexOf("?")) : screen;

                Cookie printCookie = new Cookie("ofbiz.print." + screen, printer);
                printCookie.setMaxAge(60 * 60 * 24 * 365);
                printCookie.setPath("/");
                resp.addCookie(printCookie);
                printCookie(printCookie);
            }
        }

        return "success";
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
