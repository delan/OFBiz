/*
 * $Id$
 *
 */
package org.ofbiz.service.test;

import junit.framework.TestCase;

import java.util.Map;

import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.util.UtilMisc;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      May 4, 2004
 */
public class ServiceEngineTests extends TestCase {

    public static final String DELEGATOR_NAME = "test";
    public static final String DISPATCHER_NAME = "test-dispatcher";

    private LocalDispatcher dispatcher = null;

    public ServiceEngineTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(DELEGATOR_NAME);
        dispatcher = new GenericDispatcher(DISPATCHER_NAME, delegator);
    }

    protected void tearDown() throws Exception {
        dispatcher.deregister();
    }

    public void testBasicJavaInvocation() throws Exception {
        Map result = dispatcher.runSync("testScv", UtilMisc.toMap("message", "Unit Test"));
        assertEquals("Service result success", ModelService.RESPOND_SUCCESS, result.get(ModelService.RESPONSE_MESSAGE));
    }
}
