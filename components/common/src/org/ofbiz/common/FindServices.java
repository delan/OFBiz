/*
 * $Id: FindServices.java,v 1.5 2004/02/16 21:36:51 jonesde Exp $
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
package org.ofbiz.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * FindServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      2.2
 */
public class FindServices {

    public static final String module = FindServices.class.getName();

    public static HashMap entityOperators;

    static {
        entityOperators = new HashMap();
        entityOperators.put("and", EntityOperator.AND);
        entityOperators.put("between", EntityOperator.BETWEEN);
        entityOperators.put("equals", EntityOperator.EQUALS);
        entityOperators.put("greaterThan", EntityOperator.GREATER_THAN);
        entityOperators.put("greaterThanEqualTo", EntityOperator.GREATER_THAN_EQUAL_TO);
        entityOperators.put("in", EntityOperator.IN);
        entityOperators.put("lessThan", EntityOperator.LESS_THAN);
        entityOperators.put("lessThanEqualTo", EntityOperator.LESS_THAN_EQUAL_TO);
        entityOperators.put("like", EntityOperator.LIKE);
        entityOperators.put("not", EntityOperator.NOT);
        entityOperators.put("notEqual", EntityOperator.NOT_EQUAL);
        entityOperators.put("or", EntityOperator.OR);
    }

    public FindServices() {}

    /**
     * performFind
     *
     * This is a generic method that expects entity data affixed with special suffixes
     * to indicate their purpose in formulating an SQL query statement.
     */
    public static Map performFind(DispatchContext dctx, Map context) {

        String entityName = (String) context.get("entityName");

        Map inputFields = (Map) context.get("inputFields"); // Input
        // parameters run thru UtilHttp.getParameterMap

        String fieldName = null;
        String fieldNameRaw = null; // The name as it appeas in the HTML form
        String fieldNameRoot = null; // The entity field name.
        // Everything to the left of the first "_" if
        // it exists, or the whole word, if not.
        String fieldPair = null; // "fld0" or "fld1" - begin/end of range
        // or just fld0 if no range.
        String fieldValue = null; // If it is a "value" field, it will be the value
        // to be used in the query.
        // If it is an "op" field, it will be
        // "equals", "greaterThan", etc.
        int iPos = -1;
        int iPos2 = -1;
        HashMap subMap = null;
        HashMap subMap2 = null;
        String fieldMode = null;
        EntityOperator fieldOp = null;

        // Strip the "_suffix" off of the parameter name and
        // build a three-level map of values keyed by fieldRoot name,
        //    fld0 or fld1,  and, then, "op" or "value"
        // ie. id
        //	- fld0
        //		- op:like
        // 		- value:abc
        //	- fld1 (if there is a range)
        //		- op:lessThan
        // 		- value:55 (note: these two "flds" wouldn't really go together)
        // Also note that op/fld can be in any order. (eg. id_fld1_equals or id_equals_fld1)
        // Note that "normalizedFields" will contain values other than those
        // Contained in the associated entity.
        // Those extra fields will be ignored in the second half of this method.
        HashMap normalizedFields = new HashMap();
        Iterator ifIter = inputFields.keySet().iterator();
        while (ifIter.hasNext()) {
            fieldNameRaw = (String) ifIter.next();
            fieldValue = (String) inputFields.get(fieldNameRaw);
            if (fieldValue == null || fieldValue.length() == 0)
                continue;

            iPos = fieldNameRaw.indexOf("_"); // Look for suffix

            // If no suffix, assume no range (default to fld0) and operations of equals
            // If no field op is present, it will assume "equals".
            if (iPos < 0) {
                fieldNameRoot = fieldNameRaw;
                fieldPair = "fld0";
                fieldMode = "value";
            } else { // Must have at least "fld0/1" or "equals, greaterThan, etc."
                // Some bogus fields will slip in, like "ENTITY_NAME", but they will be ignored

                fieldNameRoot = fieldNameRaw.substring(0, iPos);
                String suffix = fieldNameRaw.substring(iPos + 1);
                iPos2 = suffix.indexOf("_");
                if (iPos2 < 0) {
                    if (suffix.startsWith("fld")) {
                        // If only one token and it starts with "fld"
                        //  assume it is a value field, not an op
                        fieldPair = suffix;
                        fieldMode = "value";
                    } else {
                        // if it does not start with fld, assume it is an op
                        fieldPair = "fld0";
                        fieldMode = suffix;
                    }
                } else {
                    String tkn0 = suffix.substring(0, iPos2);
                    String tkn1 = suffix.substring(iPos2 + 1);
                    // If suffix has two parts, let them be in any order
                    // One will be "fld0/1" and the other will be the op (eg. equals, greaterThan_
                    if (tkn0.startsWith("fld")) {
                        fieldPair = tkn0;
                        fieldMode = tkn1;
                    } else {
                        fieldPair = tkn1;
                        fieldMode = tkn0;
                    }
                }
            }
            subMap = (HashMap) normalizedFields.get(fieldNameRoot);
            if (subMap == null) {
                subMap = new HashMap();
                normalizedFields.put(fieldNameRoot, subMap);
            }
            subMap2 = (HashMap) subMap.get(fieldPair);
            if (subMap2 == null) {
                subMap2 = new HashMap();
                subMap.put(fieldPair, subMap2);
            }
            subMap2.put(fieldMode, fieldValue);
        }

        // Now use only the values that correspond to entity fields to build
        //   an EntityConditionList
        GenericDelegator delegator = dctx.getDelegator();

        GenericValue entityValue = delegator.makeValue(entityName, new HashMap());

        ModelEntity modelEntity = entityValue.getModelEntity();
        List keys = modelEntity.getAllFieldNames();
        Iterator iter = keys.iterator();
        EntityOperator entOp = EntityOperator.AND;
        EntityExpr cond = null;
        ArrayList tmpList = new ArrayList();
        String opString = null;
        int count = 0;
        while (iter.hasNext()) {
            fieldName = (String) iter.next();
            subMap = (HashMap) normalizedFields.get(fieldName);
            if (subMap == null) {
                continue;
            }

            subMap2 = (HashMap) subMap.get("fld0");
            opString = (String) subMap2.get("op");

            if (opString != null) {
                if (opString.equals("contains")) {
                    fieldOp = (EntityOperator) entityOperators.get("like");

                } else if (opString.equals("empty")) {
                    fieldOp = (EntityOperator) entityOperators.get("equals");
                } else {
                    fieldOp = (EntityOperator) entityOperators.get(opString);
                }
            } else {
                fieldOp = (EntityOperator) entityOperators.get("equals");
            }

            fieldValue = (String) subMap2.get("value");
            if (fieldValue == null) {
                continue;
            }

            if (opString != null) {
                if (opString.equals("contains")) {
                    fieldOp = (EntityOperator) entityOperators.get("like");
                    fieldValue = "%" + fieldValue + "%";
                } else if (opString.equals("empty")) {
                    fieldOp = (EntityOperator) entityOperators.get("equals");
                    fieldValue = null;
                } else if (opString.equals("like")) {
                    fieldOp = (EntityOperator) entityOperators.get("like");
                    fieldValue += "%";
                } else if (opString.equals("greaterThanFromDayStart")) {
                    fieldValue = dayStart(fieldValue, 0);
                    fieldOp = (EntityOperator) entityOperators.get("greaterThan");
                } else if (opString.equals("sameDay")) {
                    String timeStampString = fieldValue;
                    fieldValue = dayStart(timeStampString, 0);
                    fieldOp = (EntityOperator) entityOperators.get("greaterThan");
    
                    // Set up so next part finds ending conditions for same day
                    subMap2 = (HashMap) subMap.get("fld1");
                    if (subMap2 == null) {
                        subMap2 = new HashMap();
                        subMap.put("fld1", subMap2);
                    }
                    String endOfDay = dayStart(timeStampString, 1);
                    subMap2.put("value", endOfDay);
                    subMap2.put("op", "lessThan");
                } else {
                    fieldOp = (EntityOperator) entityOperators.get(opString);
                }
            } else {
                fieldOp = (EntityOperator) entityOperators.get("equals");
            }

            cond = new EntityExpr(fieldName, (EntityComparisonOperator) fieldOp, fieldValue);
            tmpList.add(cond);
            count++;

            // Repeat above operations if there is a "range" - second value
            subMap2 = (HashMap) subMap.get("fld1");
            if (subMap2 == null) {
                continue;
            }
            opString = (String) subMap2.get("op");

            if (opString != null) {
                if (opString.equals("contains")) {
                    fieldOp = (EntityOperator) entityOperators.get("like");
                } else if (opString.equals("empty")) {
                    fieldOp = (EntityOperator) entityOperators.get("equals");
                } else {
                    fieldOp = (EntityOperator) entityOperators.get(opString);
                }
            } else {
                fieldOp = (EntityOperator) entityOperators.get("equals");
            }

            fieldValue = (String) subMap2.get("value");
            if (fieldValue == null) {
                continue;
            }
            if (opString.equals("like")) {
                fieldValue += "%";
            } else if (opString.equals("contains")) {
                fieldValue += "%" + fieldValue + "%";
            } else if (opString.equals("empty")) {
                fieldOp = (EntityOperator) entityOperators.get("equals");
                fieldValue = null;
            } else if (opString.equals("upToDay")) {
                fieldValue = dayStart(fieldValue, 0);
                fieldOp = (EntityOperator) entityOperators.get("lessThan");
            } else if (opString.equals("upThruDay")) {
                fieldValue = dayStart(fieldValue, 1);
                fieldOp = (EntityOperator) entityOperators.get("lessThan");
            }
            // String rhs = fieldValue.toString();
            cond = new EntityExpr(fieldName, (EntityComparisonOperator) fieldOp, fieldValue);
            tmpList.add(cond);
        }
        EntityConditionList exprList = new EntityConditionList(tmpList, (EntityJoinOperator) entOp);
        EntityListIterator listIt = null;

        if (count > 0) {
            /* Retrieve entities  - an iterator over all the values*/
            try {
                listIt = delegator.findListIteratorByCondition(entityName, exprList,
                        null, null, null, new EntityFindOptions(false, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Error finding iterator: " + e.getMessage());
            }
        } else {
            try {
                /*
                List pkList = delegator.getModelEntity(entityName).getPkFieldNames();
                String pkName = (String)pkList.get(0);
                EntityExpr pkExpr = new EntityExpr(pkName, EntityOperator.LIKE, "%");
                */
                listIt = delegator.findListIteratorByCondition(entityName, null,
                        null, null, null, new EntityFindOptions(false, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Error finding all: " + e.getMessage());
            }
        }

        Map results = ServiceUtil.returnSuccess();
        results.put("listIt", listIt);
        return results;
    }

    private static String dayStart(String timeStampString, int daysLater) {
        String retValue = null;
        Timestamp ts = null;
        Timestamp startTs = null;
        try {
            ts = Timestamp.valueOf(timeStampString);
        } catch (IllegalArgumentException e) {
            timeStampString += " 00:00:00.000";
            try {
                ts = Timestamp.valueOf(timeStampString);
            } catch (IllegalArgumentException e2) {
                return retValue;
            }
        }
        startTs = UtilDateTime.getDayStart(ts, daysLater);
        retValue = startTs.toString();
        return retValue;
    }
}
