/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.test;

import java.util.List;
import java.util.Iterator;

import junit.framework.TestCase;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      Apr 16, 2005
 */
public class EntityTestSuite extends TestCase {

    public static final String module = EntityTestSuite.class.getName();
    public static final String DELEGATOR_NAME = "test";
    public GenericDelegator delegator = null;

    public EntityTestSuite(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        this.delegator = GenericDelegator.getGenericDelegator(DELEGATOR_NAME);
    }

    // TODO: these tests should not expect seed data to exist; fix these to insert first and then run the tests
    
    // test a simple find by and
    public void testFindByAnd() throws Exception {
        List values = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", "admin"));
        TestCase.assertEquals("Admin users is 5", values.size(), 5);
    }

    // test the entity operator NOT_LIKE
    public void testNotLike() throws Exception {
        EntityCondition cond  = new EntityExpr("productId", EntityOperator.NOT_LIKE, "GZ-%");
        List products = delegator.findByCondition("Product", cond, null, null);
        TestCase.assertTrue("Found products", products != null);

        Iterator i = products.iterator();
        while (i.hasNext()) {
            GenericValue product = (GenericValue) i.next();
            String productId = product.getString("productId");
            Debug.logInfo("Testing ProductID - " + productId, module);
            TestCase.assertTrue("No product starting w/ GZ-", !productId.startsWith("GZ-"));
        }
    }
}
