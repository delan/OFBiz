/*
 * $Id: PartyWorker.java,v 1.1 2003/08/17 17:57:35 ajzeneski Exp $
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
package org.ofbiz.party.party;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Worker methods for Party Information
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class PartyWorker {
    
    public static String module = PartyWorker.class.getName();
    
    public static Map getPartyOtherValues(ServletRequest request, String partyId, String partyAttr, String personAttr, String partyGroupAttr) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map result = new HashMap();
        try {
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));

            if (party != null)
                result.put(partyAttr, party);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Party entity", module);
        }

        try {
            GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));

            if (person != null)
                result.put(personAttr, person);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Person entity", module);
        }

        try {
            GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

            if (partyGroup != null)
                result.put(partyGroupAttr, partyGroup);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting PartyGroup entity", module);
        }
        return result;
    }              
    
    public static void getPartyOtherValues(PageContext pageContext, String partyId, String partyAttr, String personAttr, String partyGroupAttr) {
        Map partyMap = getPartyOtherValues(pageContext.getRequest(), partyId, partyAttr, personAttr, partyGroupAttr);
        Iterator i = partyMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            pageContext.setAttribute((String) e.getKey(), e.getValue());
            
        }      
    }
}
