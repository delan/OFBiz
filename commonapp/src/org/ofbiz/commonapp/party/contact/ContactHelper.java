/*
 * $Id$
 * $Log$
 * Revision 1.7  2001/09/28 21:51:21  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.6  2001/09/27 15:56:13  epabst
 * refactored code to use getRelatedByAnd, filterByDate
 *
 * Revision 1.5  2001/09/26 18:40:34  epabst
 * created getRelatedByAnd (filtering)
 * created helper methods for getting Related from a collection
 * renamed getActive to filterByDate()
 *
 * Revision 1.4  2001/09/25 20:15:54  epabst
 * added getContactMech() overload that is based on the ContactMechPurposeType.
 *
 * Revision 1.3  2001/09/19 08:35:19  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.2  2001/09/13 18:32:55  epabst
 * format credit card uniformly
 *
 * Revision 1.1  2001/09/12 17:14:33  epabst
 * added helpers
 *
 */
package org.ofbiz.commonapp.party.contact;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ContactHelper.java
 * <p><b>Description:</b> Accessors for Contact Mechanisms.
 * <p>Copyright (c) 2001 The Open For Business Project (www.ofbiz.org) and repected authors.
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
 *@author <a href="mailto:epabst@bigfoot.com">Eric Pabst</a>
 *@version 1.0
 *@created Sep 12, 2001
 */
public class ContactHelper {  
    public static Collection getContactMech(GenericValue party, boolean includeOld) {
        return getContactMech(party, null, null, includeOld);
    }

    public static Collection getContactMechByType(GenericValue party, String contactMechTypeId, boolean includeOld) {
        return getContactMech(party, null, contactMechTypeId, includeOld);
    }
    
    public static Collection getContactMechByPurpose(GenericValue party, String contactMechPurposeTypeId, boolean includeOld) {
        return getContactMech(party, contactMechPurposeTypeId, null, includeOld);
    }


    public static Collection getContactMech(GenericValue party, String contactMechPurposeTypeId, String contactMechTypeId, boolean includeOld) {
        if (party == null) return null;
        try {
            Collection partyContactMechList;
            if (contactMechPurposeTypeId == null) {
                partyContactMechList = party.getRelated("PartyContactMech");
            } else {
                Collection list;
                list = party.getRelatedByAnd("PartyContactMechPurpose", UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId));
                if (!includeOld) {
                    list = EntityUtil.filterByDate(list);
                }
                partyContactMechList = EntityUtil.getRelated("PartyContactMech", list);
            }
            if (!includeOld) {
                partyContactMechList = EntityUtil.filterByDate(partyContactMechList);
            }
            partyContactMechList = EntityUtil.orderBy(partyContactMechList, UtilMisc.toList("fromDate DESC"));
            if (contactMechTypeId == null) {
                return EntityUtil.getRelated("ContactMech", partyContactMechList);
            } else {
                return EntityUtil.getRelatedByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", contactMechTypeId), partyContactMechList);
            }
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee);
            return Collections.EMPTY_LIST;
        }
    }

    public static String formatCreditCard(GenericValue creditCardInfo) {
        StringBuffer result = new StringBuffer(16);
        result.append(creditCardInfo.getString("cardType"));
        String cardNumber = creditCardInfo.getString("cardNumber");
        if(cardNumber != null && cardNumber.length() > 4) {
            result.append(' ').append(cardNumber.substring(cardNumber.length()-4));
        }
        result.append(' ').append(creditCardInfo.getString("expireDate"));
        return result.toString();
    }
}
