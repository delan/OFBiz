/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.security;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.util.*;
import org.ofbiz.core.config.SecurityConfigUtil;
import org.ofbiz.core.config.GenericConfigException;
import org.w3c.dom.Element;

/**
 * <code>SecurityFactory</code>
 *
 * This Factory class returns an instance of a security implementation.
 *
 * Setting the security implementation className is done in security.xml.
 * If no customiz security name is given, the default implementation will be used (OFBizSecurity)
 *
 * @author     <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class SecurityFactory {
    
    public static final String module = SecurityFactory.class.getName();
    public static final String DEFAULT_SECURITY = "org.ofbiz.core.security.OFBizSecurity";
    
    private static String securityName = null;
    private static Element rootElement = null;
    private static SecurityConfigUtil.SecurityInfo securityInfo = null;

    /**
     * Returns an instance of a Security implementation as defined in the security.xml by defined name
     * in security.properties.
     *
     * @param delegator the generic delegator
     * @return instance of security implementation (default: OFBizSecurity)
     */
    public static Security getInstance(GenericDelegator delegator) throws SecurityConfigurationException {
        Security security = null;

        // Make securityName a singleton
        if (securityName == null) {
            String _securityName = UtilProperties.getPropertyValue("security.properties", "security.context");
            securityName = _securityName;
        }

        if (Debug.verboseOn()) Debug.logVerbose("[SecurityFactory.getInstance] Security implementation context name from security.properties: " + securityName, module);

        synchronized (SecurityFactory.class) {
            try {
                Class c = Class.forName(getSecurityClass(securityName));
                security = (Security) c.newInstance();
                security.setDelegator(delegator);
            } catch (ClassNotFoundException cnf) {
                throw new SecurityConfigurationException("Cannot load security implementation class", cnf);
            } catch (InstantiationException ie) {
                throw new SecurityConfigurationException("Cannot get instance of the security implementation", ie);
            } catch (IllegalAccessException iae) {
                throw new SecurityConfigurationException(iae.getMessage(), iae);
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[SecurityFactory.getInstance] Security implementation successfully loaded!!!", module);

        return security;
    }

    /**
     * Returns the class name of  a custom Security implementation.
     * The default class name (org.ofbiz.core.security.OFBizSecurity) may be overridden by a customized implementation
     * class name in security.xml.
     *
     * @param securityName the security context name to be looked up
     * @return className the class name of the security implementatin
     * @throws SecurityConfigurationException
     */
    private static String getSecurityClass(String securityName) throws SecurityConfigurationException {
        String className = null;

        if (Debug.verboseOn())
            Debug.logVerbose("[SecurityFactory.getSecurityClass] Security implementation context name: " + securityName, module);

        // Only load rootElement again, if not yet loaded (singleton)
        if (rootElement == null) {
            try {
                SecurityConfigUtil.getXmlDocument();
                Element _rootElement = SecurityConfigUtil.getXmlRootElement();

                rootElement = _rootElement;
            } catch (GenericConfigException e) {
                Debug.logError(e, "Error getting Security Config XML root element", module);
                return null;
            }
        }

        if (securityInfo == null) {
            SecurityConfigUtil.SecurityInfo _securityInfo = SecurityConfigUtil.getSecurityInfo(securityName);

            // Make sure, that the security conetxt name is defined and present
            if (_securityInfo == null) {
                throw new SecurityConfigurationException("ERROR: no security definition was found with the name " + securityName + " in security.xml");
            }
            securityInfo = _securityInfo;
        }

        // This is the default implementation and uses org.ofbiz.core.security.OFBizSecurity
        if (UtilValidate.isEmpty(securityInfo.className)) {
            className = DEFAULT_SECURITY;
        } else {
            // Use a customized security
            className = securityInfo.className;
        }

        if (Debug.verboseOn()) Debug.logVerbose("[SecurityFactory.getSecurity] Security implementation " + className + " for security name " + securityName + " successfully loaded!!!", module);
        return className;
    }
}
