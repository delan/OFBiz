/*
 * $Id: JotmContainer.java,v 1.2 2004/06/22 19:00:48 ajzeneski Exp $
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
package org.ofbiz.jotm.container;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;

import org.objectweb.carol.util.configuration.RMIConfigurationException;
import org.objectweb.transaction.jta.TMService;
import org.objectweb.jotm.Jotm;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JOTM Container
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.0
 */
public class JotmContainer implements Container {

    public static final String module = JotmContainer.class.getName();

    protected TMService jotm = null;

    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[])
     */
    public void init(String[] args) {
    }
    
    public boolean start(String configFileLocation) throws ContainerException {

        // initialize Carol
        try {
            org.objectweb.carol.util.configuration.CarolConfiguration.init();
        } catch (RMIConfigurationException e) {
            throw new ContainerException("Carol threw configuration exception", e);
        }

        // start JOTM
        try {
            jotm = new Jotm(true, false);
        } catch (NamingException e) {
            throw new ContainerException("Unable to load JOTM", e);
        }

        // bind UserTransaction and TransactionManager to JNDI
        try {
            InitialContext ic = new InitialContext();
            ic.rebind("java:comp/UserTransaction", jotm.getUserTransaction());
        } catch (NamingException e) {
            throw new ContainerException("Unable to bind UserTransaction/TransactionManager to JNDI", e);
        }

        // check JNDI
        try {
            InitialContext ic = new InitialContext();
            Object o = ic.lookup("java:comp/UserTransaction");
            if (o == null) {
                throw new NamingException("Object came back null");
            }
        } catch (NamingException e) {
            throw new ContainerException("Unable to lookup bound objects", e);
        }

        return true;
    }

    public void stop() throws ContainerException {        
        jotm.stop();
    }

}
