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
package org.ofbiz.core.minilang.method.entityops;

import java.util.List;

import org.w3c.dom.*;

import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilValidate;

/**
 * Gets a sequenced ID from the delegator and puts it in the env
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class MakeNextSeqId extends MethodOperation {
    String seqFieldName;
    ContextAccessor valueAcsr;
    String numericPaddingStr;
    String incrementByStr;

    public MakeNextSeqId(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        seqFieldName = element.getAttribute("seq-field-name");
        valueAcsr = new ContextAccessor(element.getAttribute("value-name"));
        
        numericPaddingStr = element.getAttribute("numeric-padding");
        incrementByStr = element.getAttribute("increment-by");
    }

    public boolean exec(MethodContext methodContext) {
        String seqFieldName = methodContext.expandString(this.seqFieldName);
        String numericPaddingStr = methodContext.expandString(this.numericPaddingStr);
        String incrementByStr = methodContext.expandString(this.incrementByStr);
        int numericPadding = 5;
        int incrementBy = 1;
        try {
            if (UtilValidate.isNotEmpty(numericPaddingStr)) {
                numericPadding = Integer.parseInt(numericPaddingStr);
            }
        } catch (Exception e) {
            Debug.logError(e, "numeric-padding format invalid for [" + numericPaddingStr + "]");
        }
        try {
            if (UtilValidate.isNotEmpty(incrementByStr)) {
                incrementBy = Integer.parseInt(incrementByStr);
            }
        } catch (Exception e) {
            Debug.logError(e, "increment-by format invalid for [" + incrementByStr + "]");
        }

        GenericValue value = (GenericValue) valueAcsr.get(methodContext);
        if (UtilValidate.isEmpty(value.getString(seqFieldName))) {
            value.remove(seqFieldName);
            GenericValue lookupValue = methodContext.getDelegator().makeValue(value.getEntityName(), null);
            lookupValue.setPKFields(value);
            try {
                // get values in reverse order
                List allValues = methodContext.getDelegator().findByAnd(value.getEntityName(), lookupValue, UtilMisc.toList("-" + seqFieldName));
                //Debug.logInfo("Get existing values from entity " + value.getEntityName() + " with lookupValue: " + lookupValue + ", and the seqFieldName: " + seqFieldName + ", and the results are: " + allValues);
                GenericValue first = EntityUtil.getFirst(allValues);
                String newSeqId = null;
                if (first != null) {
                    String currentSeqId = first.getString(seqFieldName);
                    int seqVal = Integer.parseInt(currentSeqId);
                    seqVal += incrementBy;
                    newSeqId = Integer.toString(seqVal);
                } else {
                    newSeqId = "1";
                }
                StringBuffer outStrBfr = new StringBuffer(newSeqId); 
                while (numericPadding > outStrBfr.length()) {
                    outStrBfr.insert(0, '0');
                }
                newSeqId = outStrBfr.toString();
                    
                value.set(seqFieldName, newSeqId);
            } catch (Exception e) {
                Debug.logError(e, "Error making next seqId");
                return true;
           }
        }
        
        return true;
    }
}
