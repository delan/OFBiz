/*
 * $Id$
 * $Log$
 * Revision 1.3  2002/08/20 15:52:17  dustinsc
 * fixed project issues
 *
 * Revision 1.2  2002/02/02 12:01:47  jonesde
 * Changed method of getting dispatcher to get from request instead of ServletContext, more control to control servlet and works with Weblogic
 *
 * Revision 1.1  2002/01/24 11:53:03  jonesde
 * Moved party specific worker here from party/contact
 *
 *
 */
package org.ofbiz.commonapp.party.party;


import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Worker methods for Party Information
 * <p><b>Description:</b> None
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created January 24, 2002
 */
public class PartyWorker {
    public static void getPartyOtherValues(PageContext pageContext, String partyId, String partyAttr, String personAttr, String partyGroupAttr) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");

        try {
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));

            if (party != null)
                pageContext.setAttribute(partyAttr, party);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        try {
            GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));

            if (person != null)
                pageContext.setAttribute(personAttr, person);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }

        try {
            GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

            if (partyGroup != null)
                pageContext.setAttribute(partyGroupAttr, partyGroup);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
    }
}
