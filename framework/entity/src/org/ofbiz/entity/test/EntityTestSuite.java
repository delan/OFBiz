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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:sichen@opensourcestrategies.com">Si Chen</a>
 * @author     <a href="mailto:m.meyer@wanadoo.fr">Manuel Meyer</a>
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

    final static private int _level1max = 3;   // number of TestingNode entities to create
    
    protected void setUp() throws Exception {
        this.delegator = GenericDelegator.getGenericDelegator(DELEGATOR_NAME);
    }

    /*
     * Tests storing values with the delegator's .create, .makeValue, and .storeAll methods
     */
    public void testMakeValue() throws Exception {
        try {
            // This method call directly stores a new value into the entity engine
            delegator.create("TestingType", UtilMisc.toMap("testingTypeId", "TEST-1", "description", "Testing Type #1"));
            
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
    
    /*
     * Tests updating entities by doing a GenericValue .put(key, value) and .store()
     */
    public void testUpdateValue() throws Exception {
        try {
            
            // retrieve a sample GenericValue, make sure it's correct 
            GenericValue testValue = delegator.findByPrimaryKey("TestingType", UtilMisc.toMap("testingTypeId", "TEST-1"));
            TestCase.assertEquals("Retrieved value has the correct description", testValue.getString("description"), "Testing Type #1");
            
            // now update and store it
            testValue.put("description", "New Testing Type #1");
            testValue.store();
            
            // now retrieve it again and make sure that the updated value is correct
            testValue = delegator.findByPrimaryKey("TestingType", UtilMisc.toMap("testingTypeId", "TEST-1"));
            TestCase.assertEquals("Retrieved value has the correct description", testValue.getString("description"), "New Testing Type #1");
            
        } catch (GenericEntityException ex) {
            TestCase.fail(ex.getMessage());
        }
    }
    
    
    /*
     * Tests storing data with the delegator's .create method.  Also tests .findCountByCondition and .getNextSeqId 
     */
    public void testCreateTree() throws Exception {
        try {
        // get how many child nodes did we have before creating the tree    
        EntityCondition isChild = new EntityExpr("primaryParentNodeId", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD);
        long alreadyStored = delegator.findCountByCondition("TestingNode", isChild, null);
        
        //
        // The tree has a root, the root has level1max children.
        //
        
        // create the root
        GenericValue root = delegator.create("TestingNode", 
                UtilMisc.toMap(
                        "testingNodeId", delegator.getNextSeqId("testingNodeId"), 
                        "primaryParentNodeId", GenericEntity.NULL_FIELD, 
                        "description", "root")
                );
        int level1;
        for(level1 = 0; level1 < _level1max; level1++) {
            String nextSeqId = delegator.getNextSeqId("testingNodeId");
            GenericValue v = 
                delegator.create("TestingNode", 
                    UtilMisc.toMap("testingNodeId", nextSeqId, 
                                    "primaryParentNodeId", (String)root.get("testingNodeId"), 
                                    "description", "node-level #1")
                                );
        }
    
        long created = level1;
        long newlyStored = delegator.findCountByCondition("TestingNode", isChild, null);

        // Normally, newlyStored = alreadyStored + created  
        TestCase.assertEquals("Created/Stored Nodes", newlyStored, created + alreadyStored);
        } catch(GenericEntityException e) {
            Debug.logInfo(e.getMessage(), module);
        }
    }
    
    /*
     * More tests of storing data with .storeAll.  Also prepares data for testing view-entities (see below.)
     */
    public void testAddMembersToTree() throws Exception {
        // get the level1 nodes
        EntityCondition isLevel1 = new EntityExpr("primaryParentNodeId", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD);
        List nodeLevel1 = delegator.findByCondition("TestingNode", isLevel1, null, null);
        
        List newValues = new LinkedList();
        Timestamp now = UtilDateTime.nowTimestamp();
        
        Iterator nodeIterator = nodeLevel1.iterator();
        while(nodeIterator.hasNext()) {
            GenericValue node = (GenericValue)nodeIterator.next();
            GenericValue testing = delegator.makeValue("Testing",
                    UtilMisc.toMap(
                            "testingId", delegator.getNextSeqId("testing"),
                            "testingTypeId", "TEST-1"
                            )
                    );
            testing.put("testingName", "leaf-#" + node.getString("testingNodeId"));
            testing.put("description", "level1 leaf");
            testing.put("comments", "No-comments"); 
            testing.put("testingSize", new Long(10)); 
            testing.put("testingDate", now);

            newValues.add(testing);
            GenericValue member = delegator.makeValue("TestingNodeMember",
                    UtilMisc.toMap(
                            "testingNodeId", node.get("testingNodeId"),
                            "testingId", testing.get("testingId")
                            )
                    );
            
            member.put("fromDate", now);
            member.put("thruDate", UtilDateTime.getNextDayStart(now));
            
            newValues.add(member);
        }
        int n = delegator.storeAll(newValues);
        TestCase.assertEquals("Created/Stored Nodes", n, newValues.size());
    }
    
    /*
     * Tests findByCondition and tests searching on a view-entity
     */
    public void testCountViews() throws Exception {
        EntityCondition isNodeWithMember = new EntityExpr("testingId", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD);
        List nodeWithMembers = delegator.findByCondition("TestingNodeAndMember", isNodeWithMember, null, null);
        
        Iterator it;
        it = nodeWithMembers.iterator();
        
        while(it.hasNext()) {
            GenericValue v = (GenericValue)it.next();
            Map fields = v.getAllFields();
            Debug.logInfo("--------------------------", module);
            //      For values of a map
            for(Iterator it1 = fields.keySet().iterator(); it1.hasNext(); ) {
                Object field = it1.next();
                Object value = fields.get(field);
                Debug.logInfo(field.toString() + " = " + ((value == null) ? "[null]" : value.toString()), module);
            }
        }
        long testingcount = delegator.findCountByCondition("Testing", null, null);
        TestCase.assertEquals("Number of views should equal number of created entities in the test.", nodeWithMembers.size(), testingcount);
    }

    /*
     * Tests findByCondition and a find by distinct
     */
    public void testFindDistinct() throws Exception {
        List exprList = UtilMisc.toList(
                new EntityExpr("testingSize", EntityOperator.EQUALS, new Long(10)),
                new EntityExpr("comments", EntityOperator.EQUALS, "No-comments") 
                );
        EntityConditionList condition = new EntityConditionList(exprList, EntityOperator.AND);
        
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);
        
        List testingSize10 = delegator.findByCondition("Testing", condition, null, UtilMisc.toList("testingSize", "comments"), null, findOptions);
        Debug.logInfo("testingSize10 is " + testingSize10.size(), module);
        
        TestCase.assertEquals("There should only be 1 result found by findDistinct()", testingSize10.size(), 1);
    }

    /*
     * Tests a findByCondition using not like
     */
    public void testNotLike() throws Exception {
        EntityCondition cond  = new EntityExpr("description", EntityOperator.NOT_LIKE, "root%");
        List nodes = delegator.findByCondition("TestingNode", cond, null, null);
        TestCase.assertTrue("Found nodes", nodes != null);

        Iterator i = nodes.iterator();
        while (i.hasNext()) {
            GenericValue product = (GenericValue) i.next();
            String nodeId = product.getString("description");
            Debug.logInfo("Testing name - " + nodeId, module);
            TestCase.assertTrue("No nodes starting w/ root", !nodeId.startsWith("root"));
        }
    }
    
    /*
     * Tests foreign key integrity by trying to remove an entity which has foreign-key dependencies.  Should cause an exception.
     */
    public void testForeignKeyCreate() throws Exception {
        try {
            delegator.create("Testing", UtilMisc.toMap("testingId", delegator.getNextSeqId("Testing"), "testingTypeId", "NO-SUCH-KEY"));
        } catch(GenericEntityException e) {
            return;
        }
        TestCase.fail("Foreign key referential integrity is not observed for create (INSERT)");
    }    
      
    /*
     * Tests foreign key integrity by trying to remove an entity which has foreign-key dependencies.  Should cause an exception.
     */
    public void testForeignKeyRemove() throws Exception {
        try {
            EntityCondition isLevel1 = new EntityExpr("description", EntityOperator.EQUALS, "node-level #1");
            delegator.removeByCondition("TestingNode", isLevel1);
        } catch(GenericEntityException e) {
            return;
        }
        TestCase.fail("Foreign key referential integrity is not observed for remove (DELETE)");
    }
    
    /*
     * Tests the .getRelatedOne method and removeAll for removing entities
     */
    public void testRemoveNodeMemberAndTesting() throws Exception {
            //
            // Find the testing entities tru the node member and build a list of them
            //
            List values = delegator.findAll("TestingNodeMember");
            Iterator i = values.iterator();
            
            ArrayList testings = new ArrayList();

            while(i.hasNext()) {
                GenericValue nodeMember = (GenericValue)i.next();
                testings.add(nodeMember.getRelatedOne("Testing"));
            }
            // and remove the nodeMember afterwards
            delegator.removeAll(values);
            values = delegator.findAll("TestingNodeMember");
            TestCase.assertTrue("No more Node Member entities", values.size() == 0);
            
            delegator.removeAll(testings);
            values = delegator.findAll("Testing");
            TestCase.assertTrue("No more Testing entities", values.size() == 0);
    }

    /*
     * Tests the .removeByCondition method for removing entities directly
     */
    public void testRemoveByCondition() throws Exception {
        //
        // remove all the level1 nodes by using a condition on the description field
        //
        EntityCondition isLevel1 = new EntityExpr("description", EntityOperator.EQUALS, "node-level #1");
        int n = delegator.removeByCondition("TestingNode", isLevel1);

        TestCase.assertTrue("Deleted nodes", n > 0);
    }
    
    /*
     * Test the .removeByPrimaryKey by using findByCondition and then retrieving the GenericPk from a GenericValue
     */
    public void testRemoveByPK() throws Exception {
        //
        // Find all the root nodes, 
        // delete them their primary key 
        //
        EntityCondition isRoot = new EntityExpr("primaryParentNodeId", EntityOperator.EQUALS, GenericEntity.NULL_FIELD);
        List rootValues = delegator.findByCondition("TestingNode", isRoot, UtilMisc.toList("testingNodeId"), null);
 
        Iterator it = rootValues.iterator();
        while(it.hasNext()) {
            GenericPK pk = ((GenericValue)it.next()).getPrimaryKey();
            int del = delegator.removeByPrimaryKey(pk);
            TestCase.assertEquals("Removing Root by primary key", del, 1);
        }
        
        // no more TestingNode should be in the data base anymore.
        
        List testingNodes = delegator.findAll("TestingNode");
        TestCase.assertEquals("No more TestingNode after removing the roots", testingNodes.size(), 0);    
    }
    
    /*
     * Tests the .removeAll method only.  
     */
    public void testRemoveType() throws Exception {
        List values = delegator.findAll("TestingType");
        delegator.removeAll(values);
        
        // now make sure there are no more of these
        values = delegator.findAll("TestingType");
        TestCase.assertEquals("No more TestingTypes after remove all", values.size(), 0);
    }
}
