package org.ofbiz.core.entity;

import java.util.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Entity Utilities Class
 * <p><b>Description:</b> Helper methods when dealing with Entities.
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
 *
 *@author     Eric Pabst 
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class EntityUtil {
    public static GenericValue getFirst(Collection values) {
        if ((values != null) && (values.size() > 0)) {
            return (GenericValue) values.iterator().next();
        } else {
            return null;
        }
    }
    
    /**
     *returns the values that are currently active.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@return Collection of GenericValue's that are currently active
     */
    public static Collection filterByDate(Collection datedValues) {
        return filterByDate(datedValues, UtilDateTime.nowDate());
    }

    /**
     *returns the values that are active at the moment.
     *
     *@param datedValues GenericValue's that have "fromDate" and "thruDate" fields
     *@param moment the moment in question
     *@return Collection of GenericValue's that are active at the moment
     */
    public static Collection filterByDate(Collection datedValues, java.util.Date moment) {
        if (datedValues == null)  return null;
        
        Collection result = new ArrayList();
        Iterator iter = datedValues.iterator();
        while(iter.hasNext()) {
            GenericValue datedValue = (GenericValue) iter.next();
            if((datedValue.get("thruDate") == null || datedValue.getTimestamp("thruDate").after(moment)) 
                    && (datedValue.get("fromDate") == null || datedValue.getTimestamp("fromDate").before(moment))) {
                result.add(datedValue);
            }//else not active at moment
        }
        return result;   
    }
    
    /**
     *returns the values that match the values in fields
     *
     *@param values collection of GenericValues
     *@param fields the field-name/value pairs that must match
     *@return Collection of GenericValue's that match the values in fields
     */
    public static Collection filterByAnd(Collection values, Map fields) {
        if (values == null)  return null;
        
        Collection result = new ArrayList();
        Iterator iter = values.iterator();
        while(iter.hasNext()) {
            GenericValue value = (GenericValue) iter.next();
            if (value.matchesFields(fields)) {
                result.add(value);
            }//else did not match
        }
        return result;
    }
    
    /**
     *returns the values in the order specified
     *
     *@param values collection of GenericValues
     *@param order The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue's in the proper order
     */
    public static List orderBy(Collection values, List orderBy) {
        if (values == null)  return null;
        if (values.size() == 0) return UtilMisc.toList(values);

        List result = new ArrayList(values);
        Collections.sort(result, new OrderByComparator(orderBy));
        return result;
    }
    
    public static Collection getRelated(String relationName, Collection values) throws GenericEntityException {
        if (values == null) return null;
        
        Collection result = new ArrayList();
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelated(relationName));
        }
        return result;
    }

    public static Collection getRelatedCache(String relationName, Collection values) throws GenericEntityException {
        if (values == null) return null;
        
        Collection result = new ArrayList();
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelatedCache(relationName));
        }
        return result;
    }

    public static Collection getRelatedByAnd(String relationName, Map fields, Collection values) throws GenericEntityException {
        if (values == null) return null;
        
        Collection result = new ArrayList();
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            result.addAll(((GenericValue) iter.next()).getRelatedByAnd(relationName, fields));
        }
        return result;
    }

    static class OrderByComparator implements Comparator
    {
        private String field;
        private boolean descending;
        private Comparator next = null;
        
        OrderByComparator(List orderBy) {
            this(orderBy, 0);
        }
        
        private OrderByComparator(List orderBy, int startIndex) {
            if ((orderBy == null) || (startIndex >= orderBy.size())) throw new IllegalArgumentException("orderBy may not be empty");
            String fieldAndDirection = (String) orderBy.get(startIndex);
            String upper = fieldAndDirection.trim().toUpperCase();
            if (upper.endsWith(" DESC")) {
                this.descending = true;
                this.field = fieldAndDirection.substring(0, fieldAndDirection.length()-5);
            } else if (upper.endsWith(" ASC")) {
                this.descending = false;
                this.field = fieldAndDirection.substring(0, fieldAndDirection.length()-4);
            } else {
                this.descending = false;
                this.field = fieldAndDirection;
            }
            if (startIndex+1 < orderBy.size()) {
                this.next = new OrderByComparator(orderBy, startIndex+1);
            }//else keep null
        }

        public int compare(java.lang.Object obj, java.lang.Object obj1) {
            int result = compareAsc((GenericEntity) obj, (GenericEntity) obj1);
            if (descending) {
                result = -result;
            } 
            if ((result == 0) && (next != null)) {
                return next.compare(obj, obj1);
            } else {
                return result;
            }
        }

        private int compareAsc(GenericEntity obj, GenericEntity obj2) {
            Object value = obj.get(field);
            Object value2 = obj2.get(field);
            //null is defined as the smallest possible value
            if (value == null) return value2 == null ? 0 : -1;
            return ((Comparable)obj).compareTo(value2);
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
}
