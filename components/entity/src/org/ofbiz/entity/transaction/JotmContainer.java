package org.ofbiz.entity.transaction;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.objectweb.carol.util.configuration.RMIConfigurationException;
import org.objectweb.transaction.jta.TMService;
import org.objectweb.jotm.Jotm;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * User: jaz
 * Date: Dec 1, 2003
 * Time: 1:46:07 PM
 * To change this template use Options | File Templates.
 */
public class JotmContainer implements Container {

    public static final String module = JotmContainer.class.getName();

    protected InitialContext icx = null;
    protected TMService jotm = null;

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
            icx = new InitialContext();
            Object o = icx.lookup("java:comp/UserTransaction");
            if (o == null) {
                throw new NamingException("Object came back null");
            }
        } catch (NamingException e) {
            throw new ContainerException("Unable to lookup bound objects", e);
        }

        return true;
    }

    public void stop() throws ContainerException {
        MinervaConnectionFactory.closeAll();
        jotm.stop();
    }

}
