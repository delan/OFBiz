/*
 * $Id: EntityUtil.java,v 1.11 2004/07/07 06:33:23 doogie Exp $
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.entity.util;

import java.sql.Timestamp;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityDateFilterCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.OrderByItem;
import org.ofbiz.entity.model.ModelField;

/**
 * Helper methods when dealing with Entities, especially ones that follow certain conventions
 *
 *@author     Eric Pabst
 *@version    $ Revision: $
 *@since      1.0
 */
public class EntityUtil {

    public static final String module = EntityUtil.class.getName();

    public static GenericValue getFirst(List values) {
        if ((values != null) && (values.size() > 0)) {
            return (GenericValue) values.iterator().next();
        } else {
            return null;
        }
    }

    public static GenericValue getOnly(List values) {
        if (values != null) {
            if (values.size() <= 0) {
                return null;
            }
            if (values.size() == 1) {
                return (GenericValue) values.iterator().next();
            } else {
                throw new IllegalArgumentException("Passed List had more than one value.");
            }
        } else {
            return null;
        }
    }

    public static EntityCondition getFilterByDateExpr() {
        return new EntityDateFilterCondition("fromDate", "thruDate");
    }

    public static EntityCondition getFilterByDateExpr(String fromDateName, String thruDateName) {
        return new EntityDateFilterCondition(fromDateName, thruDateName);
    }

    public static EntityCondition getFilterByDateExpr(java.util.Date moment) {
        return EntityDateFilterCondition.makeCondition(new java.sql.Timestamp(moment.getTime()), "fromDate", "thruDate");
    }

    public static EntityCondition getFilterByDateExpr(java.sql.Timestamp moment) {
        return EntityDateFilterCondition.makeCondition(moment, "fromDate", "thruDate");
    }

    public static EntityCondition getFilterByDateExpr(java.sql.Timestamp moment, String fromDateName, String thruDateName) {
        return EntityDateFilterCondition.makeCondition(moment, fromDateName, thruDateName);
    }

    /**
     *returns the values that are currently active.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@return List of GenericValue's that are currently active
     */
    public static List filterByDate(List datedValues) {
        return filterByDate(datedValues, UtilDateTime.nowTimestamp(), null, null, true);
    }

    /**
     *returns the values that are currently active.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@param allAreSame Specifies whether all values in the List are of the same entity; this can help speed things up a fair amount since we only have to see if the from and thru date fields are valid once
     *@return List of GenericValue's that are currently active
     */
    public static List filterByDate(List datedValues, boolean allAreSame) {
        return filterByDate(datedValues, UtilDateTime.nowTimestamp(), null, null, allAreSame);
    }

    /**
     *returns the values that are active at the moment.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@param moment the moment in question
     *@return List of GenericValue's that are active at the moment
     */
    public static List filterByDate(List datedValues, java.util.Date moment) {
        return filterByDate(datedValues, new java.sql.Timestamp(moment.getTime()), null, null, true);
    }

    /**
     *returns the values that are active at the moment.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@param moment the moment in question
     *@return List of GenericValue's that are active at the moment
     */
    public static List filterByDate(List datedValues, java.sql.Timestamp moment) {
        return filterByDate(datedValues, moment, null, null, true);
    }

    /**
     *returns the values that are active at the moment.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@param moment the moment in question
     *@param allAreSame Specifies whether all values in the List are of the same entity; this can help speed things up a fair amount since we only have to see if the from and thru date fields are valid once
     *@return List of GenericValue's that are active at the moment
     */
    public static List filterByDate(List datedValues, java.sql.Timestamp moment, String fromDateName, String thruDateName, boolean allAreSame) {
        if (datedValues == null) return null;
        if (moment == null) return datedValues;
        if (fromDateName == null) fromDateName = "fromDate";
        if (thruDateName == null) thruDateName = "thruDate";

        List result = new LinkedList();
        Iterator iter = datedValues.iterator();

        if (allAreSame) {
            ModelField fromDateField = null;
            ModelField thruDateField = null;

            if (iter.hasNext()) {
                GenericValue datedValue = (GenericValue) iter.next();

                fromDateField = datedValue.getModelEntity().getField(fromDateName);
                if (fromDateField == null) throw new IllegalArgumentException("\"" + fromDateName + "\" is not a field of " + datedValue.getEntityName());
                thruDateField = datedValue.getModelEntity().getField(thruDateName);
                if (thruDateField == null) throw new IllegalArgumentException("\"" + thruDateName + "\" is not a field of " + datedValue.getEntityName());

                java.sql.Timestamp fromDate = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(fromDateField);
                java.sql.Timestamp thruDate = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(thruDateField);

                if ((thruDate == null || thruDate.after(moment)) && (fromDate == null || fromDate.before(moment))) {
                    result.add(datedValue);
                }// else not active at moment
            }
            while (iter.hasNext()) {
                GenericValue datedValue = (GenericValue) iter.next();
                java.sql.Timestamp fromDate = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(fromDateField);
                java.sql.Timestamp thruDate = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(thruDateField);

                if ((thruDate == null || thruDate.after(moment)) && (fromDate == null || fromDate.before(moment))) {
                    result.add(datedValue);
                }// else not active at moment
            }
        } else {
            // if not all values are known to be of the same entity, must check each one...
            while (iter.hasNext()) {
                GenericValue datedValue = (GenericValue) iter.next();
                java.sql.Timestamp fromDate = datedValue.getTimestamp(fromDateName);
                java.sql.Timestamp thruDate = datedValue.getTimestamp(thruDateName);

                if ((thruDate == null || thruDate.after(moment)) && (fromDate == null || fromDate.before(moment))) {
                    result.add(datedValue);
                }// else not active at moment
            }
        }

        return result;
    }

    public static boolean isValueActive(GenericValue datedValue, java.sql.Timestamp moment) {
        return isValueActive(datedValue, moment, "fromDate", "thruDate");
    }

    public static boolean isValueActive(GenericValue datedValue, java.sql.Timestamp moment, String fromDateName, String thruDateName) {
        java.sql.Timestamp fromDate = datedValue.getTimestamp(fromDateName);
        java.sql.Timestamp thruDate = datedValue.getTimestamp(thruDateName);

        if ((thruDate == null || thruDate.after(moment)) && (fromDate == null || fromDate.before(moment))) {
            return true;
        } else {
            // else not active at moment
            return false;
        }
    }

    /**
     *returns the values that match the values in fields
     *
     *@param values List of GenericValues
     *@param fields the field-name/value pairs that must match
     *@return List of GenericValue's that match the values in fields
     */
    public static List filterByAnd(List values, Map fields) {
        if (values == null) return null;

        List result = null;

        if (fields == null || fields.size() == 0) {
            result = new ArrayList(values);
        } else {
            result = new ArrayList(values.size());
            Iterator iter = values.iterator();

            while (iter.hasNext()) {
                GenericValue value = (GenericValue) iter.next();

                if (value.matchesFields(fields)) {
                    result.add(value);
                }// else did not match
            }
        }
        return result;
    }

    /**
     *returns the values that match all of the exprs in list
     *
     *@param values List of GenericValues
     *@param exprs the expressions that must validate to true
     *@return List of GenericValue's that match the values in fields
     */
    public static List filterByAnd(List values, List exprs) {
        if (values == null) return null;
        if (exprs == null || exprs.size() == 0) {
            // no constraints... oh well
            return values;
        }

        List result = new ArrayList();
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            Iterator exprIter = exprs.iterator();
            boolean include = true;

            while (exprIter.hasNext()) {
                EntityExpr expr = (EntityExpr) exprIter.next();
                Object lhs = value.get((String) expr.getLhs());
                Object rhs = expr.getRhs();

                int operatorId = expr.getOperator().getId();
                switch (operatorId) {
                    case EntityOperator.ID_EQUALS:
                        include = EntityComparisonOperator.compareEqual(lhs, rhs);
                        break;
                    case EntityOperator.ID_NOT_EQUAL:
                        include = EntityComparisonOperator.compareNotEqual(lhs, rhs);
                        break;
                    case EntityOperator.ID_GREATER_THAN:
                        include = EntityComparisonOperator.compareGreaterThanEqualTo(lhs, rhs);
                        break;
                    case EntityOperator.ID_GREATER_THAN_EQUAL_TO:
                        include = EntityComparisonOperator.compareGreaterThan(lhs, rhs);
                        break;
                    case EntityOperator.ID_LESS_THAN:
                        include = EntityComparisonOperator.compareLessThan(lhs, rhs);
                        break;
                    case EntityOperator.ID_LESS_THAN_EQUAL_TO:
                        include = EntityComparisonOperator.compareLessThanEqualTo(lhs, rhs);
                        break;
                    case EntityOperator.ID_LIKE:
                        include = EntityComparisonOperator.compareLike(lhs, rhs);
                        break;
                    default:
                        throw new IllegalArgumentException("The " + expr.getOperator().getCode() + " with id " + expr.getOperator().getId() + " operator is not yet supported by filterByAnd");
                }
                if (!include) break;
            }
            if (include) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     *returns the values that match any of the exprs in list
     *
     *@param values List of GenericValues
     *@param exprs the expressions that must validate to true
     *@return List of GenericValue's that match the values in fields
     */
    public static List filterByOr(List values, List exprs) {
        if (values == null) return null;
        if (exprs == null || exprs.size() == 0) {
            return values;
        }

        List result = new ArrayList();
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            Iterator exprIter = exprs.iterator();
            boolean include = false;

            while (exprIter.hasNext()) {
                EntityExpr expr = (EntityExpr) exprIter.next();
                Object lhs = value.get((String) expr.getLhs());
                Object rhs = expr.getRhs();

                int operatorId = expr.getOperator().getId();
                switch (operatorId) {
                    case EntityOperator.ID_EQUALS:
                        if (EntityComparisonOperator.compareEqual(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_NOT_EQUAL:
                        if (EntityComparisonOperator.compareNotEqual(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_GREATER_THAN:
                        if (EntityComparisonOperator.compareGreaterThanEqualTo(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_GREATER_THAN_EQUAL_TO:
                        if (EntityComparisonOperator.compareGreaterThan(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_LESS_THAN:
                        if (EntityComparisonOperator.compareLessThan(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_LESS_THAN_EQUAL_TO:
                        if (EntityComparisonOperator.compareLessThanEqualTo(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    case EntityOperator.ID_LIKE:
                        if (EntityComparisonOperator.compareLike(lhs, rhs)) {
                            include = true;
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("The " + expr.getOperator().getCode() + " with id " + expr.getOperator().getId() + " operator is not yet supported by filterByOr");
                }
                if (include) break;
            }
            if (include) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     *returns the values in the order specified
     *
     *@param values List of GenericValues
     *@param orderBy The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue's in the proper order
     */
    public static List orderBy(Collection values, List orderBy) {
        if (values == null) return null;
        if (values.size() == 0) return new ArrayList();
        if (orderBy == null || orderBy.size() == 0) {
            return new ArrayList(values);
        }

        List result = new ArrayList(values);
        if (Debug.verboseOn()) Debug.logVerbose("Sorting " + values.size() + " values, orderBy=" + orderBy.toString(), module);
        Collections.sort(result, new OrderByComparator(orderBy));
        return result;
    }

    public static List getRelated(String relationName, List values) throws GenericEntityException {
        if (values == null) return null;

        List result = new ArrayList();
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelated(relationName));
        }
        return result;
    }

    public static List getRelatedCache(String relationName, List values) throws GenericEntityException {
        if (values == null) return null;

        List result = new ArrayList();
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelatedCache(relationName));
        }
        return result;
    }

    public static List getRelatedByAnd(String relationName, Map fields, List values) throws GenericEntityException {
        if (values == null) return null;

        List result = new ArrayList();
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelatedByAnd(relationName, fields));
        }
        return result;
    }

    public static List filterByCondition(List values, EntityCondition condition) {
        if (values == null) return null;

        List result = new ArrayList(values.size());
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            if (condition.entityMatches(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public static List filterOutByCondition(List values, EntityCondition condition) {
        if (values == null) return null;

        List result = new ArrayList(values.size());
        Iterator iter = values.iterator();

        while (iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            if (!condition.entityMatches(value)) {
                result.add(value);
            }
        }
        return result;
    }

    static class OrderByComparator extends OrderByItem implements Comparator {

        private ModelField modelField = null;
        private Comparator next = null;

        OrderByComparator(List orderBy) {
            this(orderBy, 0);
        }

        private OrderByComparator(List orderBy, int startIndex) {
            super();
            if (orderBy == null) throw new IllegalArgumentException("orderBy may not be empty");
            if (startIndex >= orderBy.size()) throw new IllegalArgumentException("startIndex may not be greater than or equal to orderBy size");
            parse(((String) orderBy.get(startIndex)));
            if (startIndex + 1 < orderBy.size()) {
                this.next = new OrderByComparator(orderBy, startIndex + 1);
            }// else keep null
        }

        public int compare(java.lang.Object obj, java.lang.Object obj1) {
            int result = compareAsc((GenericEntity) obj, (GenericEntity) obj1);

            if (descending && result != 0) {
                result = -result;
            }
            if ((result == 0) && (next != null)) {
                return next.compare(obj, obj1);
            } else {
                return result;
            }
        }

        private int compareAsc(GenericEntity obj, GenericEntity obj2) {
            if (this.modelField == null) {
                this.modelField = obj.getModelEntity().getField(field);
                if (this.modelField == null) {
                    throw new IllegalArgumentException("The field " + field + " could not be found in the entity " + obj.getEntityName());
                }
            }
            Object value = obj.dangerousGetNoCheckButFast(this.modelField);
            Object value2 = obj2.dangerousGetNoCheckButFast(this.modelField);

            // null is defined as the largest possible value
            if (value == null) return value2 == null ? 0 : 1;
            if (value2 == null) return value == null ? 0 : -1;
            if (value instanceof String) {
                if (caseSensitivity == LOWER) {
                    value = ((String) value).toLowerCase();
                    value2 = ((String) value2).toLowerCase();
                } else if (caseSensitivity == UPPER) {
                    value = ((String) value).toUpperCase();
                    value2 = ((String) value2).toUpperCase();
                }
            }
            int result = ((Comparable) value).compareTo(value2);

            // if (Debug.infoOn()) Debug.logInfo("[OrderByComparator.compareAsc] Result is " + result + " for [" + value + "] and [" + value2 + "]", module);
            return result;
        }

        public boolean equals(java.lang.Object obj) {
            if ((obj != null) && (obj instanceof OrderByComparator)) {
                OrderByComparator that = (OrderByComparator) obj;

                return this.field.equals(that.field) && (this.descending == that.descending)
                    && UtilValidate.areEqual(this.next, that.next);
            } else {
                return false;
            }
        }
    }

    public static List findDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search) throws GenericEntityException {
        return findDatedInclusionEntity(delegator, entityName, search, UtilDateTime.nowTimestamp());
    }

    public static List findDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search, Timestamp now) throws GenericEntityException {
        EntityCondition searchCondition = new EntityConditionList(UtilMisc.toList(
            new EntityFieldMap(search, EntityOperator.AND),
            EntityUtil.getFilterByDateExpr(now)
        ), EntityOperator.AND);
        return delegator.findByCondition(entityName,searchCondition,null,UtilMisc.toList("-fromDate"));
    }

    public static GenericValue newDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search) throws GenericEntityException {
        return newDatedInclusionEntity(delegator, entityName, search, UtilDateTime.nowTimestamp());
    }

    public static GenericValue newDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search, Timestamp now) throws GenericEntityException {
        List entities = findDatedInclusionEntity(delegator, entityName, search, now);
        if (entities != null && entities.size() > 0) {
            search = null;
            for (int i = 0; i < entities.size(); i++) {
                GenericValue entity = (GenericValue)entities.get(i);
                if (now.equals(entity.get("fromDate"))) {
                    search = new HashMap(entity.getPrimaryKey());
                    entity.remove("thruDate");
                } else {
                    entity.set("thruDate",now);
                }
                entity.store();
            }
            if (search == null) search = new HashMap(EntityUtil.getFirst(entities));
        } else {
            search = new HashMap(search);
        }
        if (now.equals(search.get("fromDate"))) {
            return EntityUtil.getOnly(delegator.findByAnd(entityName, search));
        } else {
            search.put("fromDate",now);
            search.remove("thruDate");
            return delegator.makeValue(entityName, search);
        }
    }

    public static void delDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search) throws GenericEntityException {
        delDatedInclusionEntity(delegator, entityName, search, UtilDateTime.nowTimestamp());
    }

    public static void delDatedInclusionEntity(GenericDelegator delegator, String entityName, Map search, Timestamp now) throws GenericEntityException {
        List entities = findDatedInclusionEntity(delegator, entityName, search, now);
        for (int i = 0; entities != null && i < entities.size(); i++) {
            GenericValue entity = (GenericValue)entities.get(i);
            entity.set("thruDate",now);
            entity.store();
        }
    }
    
    public static List getFieldListFromEntityList(List genericValueList, String fieldName, boolean distinct) {
        if (genericValueList == null || fieldName == null) {
            return null;
        }
        List fieldList = new ArrayList(genericValueList.size());
        Set distinctSet = null;
        if (distinct) {
            distinctSet = new HashSet();
        }
        
        Iterator genericValueIter = genericValueList.iterator();
        while (genericValueIter.hasNext()) {
            GenericValue value = (GenericValue) genericValueIter.next();
            Object fieldValue = value.get(fieldName);
            if (distinct) {
                if (!distinctSet.contains(fieldValue)) {
                    fieldList.add(fieldValue);
                    distinctSet.add(fieldValue);
                }
            } else {
                fieldList.add(fieldValue);
            }
        }
        
        return fieldList;
    }
    
    public static List getFieldListFromEntityListIterator(EntityListIterator genericValueEli, String fieldName, boolean distinct) {
        if (genericValueEli == null || fieldName == null) {
            return null;
        }
        List fieldList = new LinkedList();
        Set distinctSet = null;
        if (distinct) {
            distinctSet = new HashSet();
        }
        
        GenericValue value = null;
        while ((value = (GenericValue) genericValueEli.next()) != null) {
            Object fieldValue = value.get(fieldName);
            if (distinct) {
                if (!distinctSet.contains(fieldValue)) {
                    fieldList.add(fieldValue);
                    distinctSet.add(fieldValue);
                }
            } else {
                fieldList.add(fieldValue);
            }
        }
        
        return fieldList;
    }
}
