/*
 * $Id$
 *
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
 */

package org.ofbiz.core.entity;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.entity.jdbc.*;
import org.ofbiz.core.util.*;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Nov 13, 2001
 *@version    1.0
 */
public class EntityExprList extends EntityCondition {

    protected List exprList;
    protected EntityOperator operator;

    protected EntityExprList() { }
    
    public EntityExprList(List exprList, EntityOperator operator) {
        this.exprList = exprList;
        this.operator = operator;
    }
    
    public String makeWhereString(ModelEntity modelEntity, List entityConditionParams) {
        //if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName());
        StringBuffer whereStringBuffer = new StringBuffer();
        if (exprList != null && exprList.size() > 0) {
            for (int i = 0; i < exprList.size(); i++) {
                EntityExpr expr = (EntityExpr) exprList.get(i);
                whereStringBuffer.append(expr.makeWhereString(modelEntity, entityConditionParams));
                if (i < exprList.size() - 1) {
                    whereStringBuffer.append(' ');
                    whereStringBuffer.append(operator.getCode());
                    whereStringBuffer.append(' ');
                }
            }
        }
        return whereStringBuffer.toString();
    }

    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        //if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName());
        Iterator exprIter = exprList.iterator();
        while (exprIter.hasNext()) {
            EntityExpr entityExpr = (EntityExpr) exprIter.next();
            entityExpr.checkCondition(modelEntity);
        }
    }
    
    public String toString() {
        StringBuffer toStringBuffer = new StringBuffer();
        toStringBuffer.append("[ExprList::");
        if (exprList != null && exprList.size() > 0) {
            for (int i = 0; i < exprList.size(); i++) {
                EntityExpr expr = (EntityExpr) exprList.get(i);
                toStringBuffer.append(expr.toString());
                if (i > 0) toStringBuffer.append("::");
            }
        }
        toStringBuffer.append(']');
        return toStringBuffer.toString();
    }
}
