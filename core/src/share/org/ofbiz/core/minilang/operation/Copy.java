
package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * <p><b>Title:</b> Copies a field in the in-map to the out-map
 * <p><b>Description:</b> None
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class Copy extends SimpleMapOperation {
    boolean replace = true;
    boolean setIfNull = false;
    String toField;

    public Copy(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        toField = element.getAttribute("to-field");
        if (this.toField == null || this.toField.length() == 0) {
            this.toField = this.fieldName;
        }

        //if anything but false it will be true
        replace = !"false".equals(element.getAttribute("replace"));
        //if anything but true it will be false
        setIfNull = "true".equals(element.getAttribute("set-if-null"));
    }

    public void exec(Map inMap, Map results, List messages, Locale locale, ClassLoader loader) {
        Object fieldValue = inMap.get(fieldName);

        if (fieldValue == null && !setIfNull)
            return;

        if (replace) {
            results.put(toField, fieldValue);
            //Debug.logInfo("[SimpleMapProcessor.Copy.exec] Copied \"" + fieldValue + "\" to field \"" + toField + "\"");
        } else {
            if (results.containsKey(toField)) {
                //do nothing
            } else {
                results.put(toField, fieldValue);
                //Debug.logInfo("[SimpleMapProcessor.Copy.exec] Copied \"" + fieldValue + "\" to field \"" + toField + "\"");
            }
        }
    }
}
