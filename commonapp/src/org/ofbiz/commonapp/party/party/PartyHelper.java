/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.commonapp.party.party;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * PartyHelper
 *
 * @author     <a href="mailto:epabst@bigfoot.com">Eric Pabst</a>
 * @version    $Revision$
 * @since      2.0
 */
public class PartyHelper {
    public static String formatPartyId(String partyId, GenericDelegator delegator) {
        if (UtilValidate.isEmpty(partyId)) return "(none)";
        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee);
        }
        if (person != null) {
            return getPersonName(person);
        } else {
            return partyId;
        }
    }

    public static String getPersonName(GenericValue person) {
        StringBuffer result = new StringBuffer(20);

        if (person != null) {
            result.append(UtilFormatOut.ifNotEmpty(person.getString("firstName"), "", " "));
            result.append(UtilFormatOut.ifNotEmpty(person.getString("middleName"), "", " "));
            result.append(UtilFormatOut.checkNull(person.getString("lastName")));
        }
        return result.toString().trim();
    }

    public static String getPartyName(GenericValue userLogin) {
        StringBuffer result = new StringBuffer(20);

        if (userLogin != null) {
            try {
                GenericValue person = userLogin.getDelegator().findByPrimaryKey("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")));

                if (person == null) {
                    GenericValue group = userLogin.getDelegator().findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", userLogin.getString("partyId")));

                    if (group != null)
                        result.append(group.getString("groupName"));
                } else {
                    result.append(UtilFormatOut.ifNotEmpty(person.getString("firstName"), "", " "));
                    result.append(UtilFormatOut.ifNotEmpty(person.getString("middleName"), "", " "));
                    result.append(UtilFormatOut.checkNull(person.getString("lastName")));
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        return result.toString().trim();
    }
}
