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
package org.ofbiz.core.view;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.ibm.bsf.*;

import org.jpublish.*;
import org.jpublish.action.*;
import org.jpublish.component.*;
import org.jpublish.page.*;
import org.jpublish.util.*;

import org.ofbiz.core.util.*;

/**
 * Handles JPublish type view rendering
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class JPublishViewHandler implements ViewHandler {

    public static final String module = JPublishViewHandler.class.getName();

    protected ServletContext servletContext = null;
    protected SiteContext siteContext = null;

    /**
     * @see org.ofbiz.core.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
        // find the WEB-INF root
        String rootDir = servletContext.getRealPath("/");
        File contextRoot = new File(rootDir);
        File webInfPath = new File(contextRoot, "WEB-INF");

        // configure the classpath for scripting support
        configureClasspath(webInfPath);

        // configure BSF
        configureBSF();

        // create the site context
        try {
            //siteContext = new SiteContext(contextRoot, servletConfig.getInitParameter("config"));
            siteContext = new SiteContext(contextRoot, "WEB-INF/jpublish.xml");
            siteContext.setWebInfPath(webInfPath);
        } catch (Exception e) {
            throw new ViewHandlerException("Cannot load SiteContext", e);
        }

        // execute startup actions
        try {
            ActionManager actionManager = siteContext.getActionManager();
            actionManager.executeStartupActions();
        } catch (Exception e) {
            throw new ViewHandlerException("Problems executing JPublish startup actions", e);
        }
    }

    protected void configureClasspath(File webInfPath) {
        File webLibPath = new File(webInfPath, "lib");
        File webClassPath = new File(webInfPath, "classes");

        // add WEB-INF/classes to the classpath
        StringBuffer classPath = new StringBuffer();
        classPath.append(System.getProperty("java.class.path"));

        if (webClassPath.exists()) {
            classPath.append(System.getProperty("path.separator"));
            classPath.append(webClassPath);
        }

        // add WEB-INF/lib files to the classpath
        File[] files = webLibPath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toLowerCase().endsWith(".jar")
                || files[i].getName().toLowerCase().endsWith(".zip")) {
                classPath.append(System.getProperty("path.separator"));
                classPath.append(files[i]);
            }
        }

        AccessController.doPrivileged(new SetClassPathAction(classPath.toString()));
    }

    protected void configureBSF() {
        String[] extensions = {"bsh"};
        BSFManager.registerScriptingEngine("beanshell", "bsh.OfbizBshBsfEngine", extensions);
        //BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", extensions);
        
        String[] jsExtensions = {"js"};
        BSFManager.registerScriptingEngine("javascript", "org.ofbiz.core.action.OfbizJsBsfEngine", jsExtensions);
        
    }

    protected boolean executeGlobalActions(HttpServletRequest request, HttpServletResponse response, JPublishContext context, String path) throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        return optionalRedirect(actionManager.executeGlobalActions(context), path, response);
    }

    protected boolean executePathActions(HttpServletRequest request, HttpServletResponse response, JPublishContext context, String path) throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        return optionalRedirect(actionManager.executePathActions(path, context), path, response);
    }

    protected boolean executeParameterActions(HttpServletRequest request, HttpServletResponse response, JPublishContext context, String path) throws Exception {
        if (!siteContext.isParameterActionsEnabled()) {
            return false;
        }

        ActionManager actionManager = siteContext.getActionManager();
        String[] actionNames = request.getParameterValues(siteContext.getActionIdentifier());
        if (actionNames != null) {
            for (int i = 0; i < actionNames.length; i++) {
                return optionalRedirect(actionManager.execute(actionNames[i], context), path, response);
            }
        }
        return false;
    }

    protected boolean executePreEvaluationActions(HttpServletRequest request, HttpServletResponse response, JPublishContext context, String path) throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        return actionManager.executePreEvaluationActions(path, context);
    }

    protected boolean executePostEvaluationActions(HttpServletRequest request, HttpServletResponse response, JPublishContext context, String path) throws Exception {
        ActionManager actionManager = siteContext.getActionManager();
        actionManager.executePostEvaluationActions(path, context);
        return false;
    }

    private boolean optionalRedirect(String redirect, String path, HttpServletResponse response) throws IOException {
        if (redirect == null) {
            return false;
        }

        if (redirect.endsWith("/")) {
            response.sendRedirect(redirect);
            return true;
        }

        if (redirect.lastIndexOf(".") == -1) {
            response.sendRedirect(redirect + path.substring(path.lastIndexOf(".")));
            return true;
        } else {
            response.sendRedirect(redirect);
            return true;
        }
    }

    /**
     * @see org.ofbiz.core.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(String name, String path, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        HttpSession session = request.getSession();
        ActionManager actionManager = siteContext.getActionManager();
        //String path = servletContext.getRealPath(pagePath);
        //Debug.logError("Path:" + path);

        // get the character encoding map
        CharacterEncodingMap characterEncodingMap = siteContext.getCharacterEncodingManager().getMap(path);

        // put standard servlet stuff into the context
        JPublishContext context = new JPublishContext(this);
        context.put("request", request);
        context.put("response", response);
        context.put("session", session);
        context.put("application", servletContext);

        // add the character encoding map to the context
        context.put("characterEncodingMap", characterEncodingMap);

        // add the URLUtilities to the context
        URLUtilities urlUtilities = new URLUtilities(request, response);
        context.put("urlUtilities", urlUtilities);

        // add the DateUtilities to the context
        context.put("dateUtilities", DateUtilities.getInstance());

        // add the NumberUtilities to the context
        context.put("numberUtilities", NumberUtilities.getInstance());

        // add the messages log to the context
        context.put("syslog", SiteContext.syslog);

        // expose the SiteContext
        context.put("site", siteContext);

        if (siteContext.isProtectReservedNames()) {
            context.enableCheckReservedNames(this);
        }

        // add the repositories to the context
        Iterator repositories = siteContext.getRepositories().iterator();
        while (repositories.hasNext()) {
            Repository repository = (Repository) repositories.next();
            context.put(repository.getName(), new RepositoryWrapper(repository, context));
            // add the fs_repository also as the name 'pages' so we can use existing logic in pages
            // note this is a hack and we should look at doing this a different way; but first need
            // to investigate how to get content from different repositories
            if (repository.getName().equals("fs_repository")) {
                context.put("pages", new RepositoryWrapper(repository, context));
            }
        }

        try {
            if (executePreEvaluationActions(request, response, context, path))
                return;

            // if the page is static
            StaticResourceManager staticResourceManager = siteContext.getStaticResourceManager();
            if (staticResourceManager.resourceExists(path)) {
                // execute the global actions
                if (executeGlobalActions(request, response, context, path))
                    return;

                // execute path actions
                if (executePathActions(request, response, context, path))
                    return;

                // execute parameter actions
                if (executeParameterActions(request, response, context, path))
                    return;

                // load and return the static resource                
                OutputStream out = response.getOutputStream();
                staticResourceManager.load(path, out);
                out.flush();
                return;
            }

            // load the page          
            PageInstance pageInstance = siteContext.getPageManager().getPage(path);
            Page page = new Page(pageInstance);

            context.disableCheckReservedNames(this);

            // expose the page in the context
            context.put("page", page);

            // expose components in the context
            context.put("components", new ComponentMap(context));

            if (siteContext.isProtectReservedNames()) {
                context.enableCheckReservedNames(this);
            }

            // execute the global actions
            if (executeGlobalActions(request, response, context, path))
                return;

            // execute path actions
            if (executePathActions(request, response, context, path))
                return;

            // execute parameter actions
            if (executeParameterActions(request, response, context, path))
                return;

            // execute the page actions           
            if (optionalRedirect(page.executeActions(context), path, response))
                return;

            // get the template
            Template template = siteContext.getTemplateManager().getTemplate(page.getFullTemplateName());

            // get the Servlet writer
            Writer out = response.getWriter();

            // merge the template           
            template.merge(context, page, out);

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            throw new ViewHandlerException("File not found", e);
        } catch (Exception e) {
            throw new ViewHandlerException("JPublish execution error", e);
        } finally {
            try {
                executePostEvaluationActions(request, response, context, path);
            } catch (Exception e) {
                throw new ViewHandlerException("Error executing JPublish post evaluation actions", e);
            }
        }
    }

    /**      
     * Privleged action for setting the class path.  This is used to get around
     * the Java security system to set the class path so scripts have full 
     * access to all loaded Java classes.
     *  
     * <p>Note: This functionality is untested.</p>
     *   
     *  @author Anthony Eden
     */
    class SetClassPathAction implements PrivilegedAction {
        private String classPath;

        /** 
         * Construct the action to set the class path.        
         *   @param classPath The new class path
         */
        public SetClassPathAction(String classPath) {
            this.classPath = classPath;
        }

        /** 
         * Set the "java.class.path" property.               
         * @return Returns null
         */
        public Object run() {
            System.setProperty("java.class.path", classPath);
            return null; // nothing to return
        }
    }

}
