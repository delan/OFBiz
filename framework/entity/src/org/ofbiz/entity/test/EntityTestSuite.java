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
import java.util.LinkedList;
import java.util.Iterator;

import junit.framework.TestCase;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
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

    // TODO: create more find tests for all the testing entities
    public void testMakeValue() throws Exception {
        try {
            // This method call directly stores a new value into the entity engine
            delegator.create("TestingType", UtilMisc.toMap("testingTypeId", "TEST-1", "description", "Testing Type #4"));
            
            // This sequence creates the GenericValue entities first, puts them in a List, then calls the delegator to store them all
            List newValues = new LinkedList();
        
            newValues.add(delegator.makeValue("TestingType", UtilMisc.toMap("testingTypeId", "TEST-2", "description", "Testing Type #2")));
            newValues.add(delegator.makeValue("TestingType", UtilMisc.toMap("testingTypeId", "TEST-3", "description", "Testing Type #3")));
            newValues.add(delegator.makeValue("TestingType", UtilMisc.toMap("testingTypeId", "TEST-4", "description", "Testing Type #4")));
            delegator.storeAll(newValues);
            
            // finds a List of newly created values.  the second parameter specifies the fields to order results by.
            List newlyCreatedValues = delegator.findAll("TestingType", UtilMisc.toList("testingTypeId"));
            TestCase.assertEquals("4 TestingTypes found", newlyCreatedValues.size(), 4);
        } catch (GenericEntityException ex) {
            TestCase.fail(ex.getMessage());
        }
    }
    
    // TODO: use a different testing entity here, one that was created above
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
    
    // TODO: remove all testing entities created above
    public void testRemoveValue() throws Exception {
        try {
            // uses the delegator.removeAll method to remove all the entities.  Could have also used .removeValue, .remove____ methods 
            List values = delegator.findAll("TestingType");
            delegator.removeAll(values);
            
            // now make sure there are no more of these
            values = delegator.findAll("TestingType");
            TestCase.assertEquals("No more TestingTypes after remove all", values.size(), 0);
        } catch (GenericEntityException ex) {
            TestCase.fail(ex.getMessage());
        }    
    }
}
