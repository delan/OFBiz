/*
 * $Id$
 * $Log$
 * Revision 1.10  2001/09/28 21:51:21  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.9  2001/09/26 17:17:57  epabst
 * added formatPartyId method
 *
 * Revision 1.8  2001/09/12 17:26:32  epabst
 * updated
 *
 * Revision 1.7  2001/09/12 17:14:33  epabst
 * added helpers
 *
 */
package org.ofbiz.commonapp.party.party;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> PartyHelper.java
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
public class PartyHelper {  
    public static String formatPartyId(String partyId, GenericDelegator delegator) {
        if (UtilValidate.isEmpty(partyId)) return "(none)";
        GenericValue person = null;
        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException gee) { Debug.logWarning(gee); }
        if (person != null) {
            return getPersonName(person);
        } else {
            return partyId;
        }
    }
    
    public static String getPersonName(GenericValue person) {
        StringBuffer result = new StringBuffer(20);
        if(person!=null){
            result.append(UtilFormatOut.ifNotEmpty(person.getString("firstName"), "", " "));
            result.append(UtilFormatOut.ifNotEmpty(person.getString("middleName"), "", " "));
            result.append(UtilFormatOut.checkNull(person.getString("lastName")));
        }
        return result.toString().trim();
    }
}
