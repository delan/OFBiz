/*
 * $Id: BaseUnitTests.java,v 1.1 2004/05/04 15:36:41 ajzeneski Exp $
 *
 */
package org.ofbiz.base.test;

import junit.framework.TestCase;

import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      May 4, 2004
 */
public class BaseUnitTests extends TestCase {

    public BaseUnitTests(String name) {
        super(name);
    }

    public void testDebug() {
        Debug.set(Debug.VERBOSE, true);
        assertTrue(Debug.verboseOn());

        Debug.set(Debug.VERBOSE, false);
        assertTrue(!Debug.verboseOn());

        Debug.set(Debug.INFO, true);
        assertTrue(Debug.infoOn());
    }
}
