/*
 * OrderHelper.java
 *
 * Created on August 28, 2001, 11:25 AM
 */

package org.ofbiz.ecommerce.order;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *
 * @author  epabst
 * @version 
 */
public class OrderHelper {
    private static GenericValue getFirst(Collection values) {
        if ((values != null) && (values.size() > 0)) {
            return (GenericValue) values.iterator().next();
        } else {
            return null;
        }
    }
    
    public static Collection getContactMech(GenericValue party, String contactMechTypeId, boolean includeOld) {
        Collection result = new LinkedList();
        Date now = new java.util.Date();
        Iterator partyContactMechIter = party.getRelated("PartyContactMech").iterator();
        while (partyContactMechIter.hasNext()) {
            GenericValue partyContactMech = (GenericValue) partyContactMechIter.next();
            if (includeOld || partyContactMech.get("thruDate") == null || partyContactMech.getTimestamp("thruDate").after(now)) {
                GenericValue contactMech = partyContactMech.getRelatedOne("ContactMech");
                if (contactMechTypeId == null || contactMechTypeId.equals(contactMech.get("contactMechTypeId"))) {
                    result.add(contactMech);
                }//else wrong type
            }//else old and includeOld is false
        }
        return result;
    }
    
    public static String getPersonName(GenericValue person) {
        StringBuffer result = new StringBuffer(20);
        if(person!=null){
            result.append(appendSpace(person.getString("firstName")));
            result.append(appendSpace(person.getString("middleName")));
            result.append(appendSpace(person.getString("lastName")));
        }
        return result.toString().trim();
    }
    
    private static String appendSpace(String string) {
        if ((string != null) && (string.length() > 0)) {
            return string + " ";
        } else {
            return "";
        }
    }
}
