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
            if (isSubset(fields, value.getAllFields())) {
                result.add(value);
            }//else did not match
        }
        return result;
    }
    
    private static boolean isSubset(Map smallSet, Map largeSet) {
        Iterator keyIter = smallSet.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = (String) keyIter.next();
            if (!equals(smallSet.get(key), largeSet.get(key))) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
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
}
