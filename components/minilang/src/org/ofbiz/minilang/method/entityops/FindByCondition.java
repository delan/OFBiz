/*
 * $Id: FindByCondition.java,v 1.1 2004/07/08 09:27:37 jonesde Exp $
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
package org.ofbiz.minilang.method.entityops;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.minilang.SimpleMethod;
import org.ofbiz.minilang.method.ContextAccessor;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by anding the map fields
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class FindByCondition extends MethodOperation {
    
    public static final String module = FindByAnd.class.getName();         
/*
<!ELEMENT find-by-condition ( ( condition | condition-list ), having-condition-list?, select-field*, order-by*, ( limit-range | limit-view | use-iterator )? )>
<!ATTLIST find-by-condition
    entity-name CDATA #REQUIRED
    use-cache ( true | false ) "false"
    filter-by-date ( true | false | by-name ) "false"
    distinct ( true | false ) "false"
    delegator-name CDATA #IMPLIED
    list-name CDATA #REQUIRED
>
    <!ELEMENT condition-list ( ( condition | condition-list )+ )>
    <!ATTLIST condition-list
        combine ( and | or ) "and"
    >
    <!ELEMENT having-condition-list ( ( condition | condition-list )+ )>
    <!ATTLIST having-condition-list
        combine ( and | or ) "and"
    >
    <!ELEMENT condition EMPTY>
    <!ATTLIST condition
        field-name CDATA #REQUIRED
        operator ( less | greater | less-equals | greater-equals | equals | not-equals | in | between | like ) #REQUIRED
        env-name CDATA #IMPLIED
        value CDATA #IMPLIED
    >
    <!ELEMENT select-field EMPTY>
    <!ATTLIST select-field
        field-name CDATA #REQUIRED
    >
    <!ELEMENT order-by EMPTY>
    <!ATTLIST order-by
        field-name CDATA #REQUIRED
    >
    <!ELEMENT limit-range EMPTY>
    <!ATTLIST limit-range
        start CDATA #REQUIRED
        size CDATA #REQUIRED
    >
    <!ELEMENT limit-view EMPTY>
    <!ATTLIST limit-view
        view-index CDATA #REQUIRED
        view-size CDATA #REQUIRED
    >
    <!ELEMENT use-iterator EMPTY>
 * 
 */    
    String entityName;
    String useCacheStr;
    String filterByDateStr;
    String distinctStr;
    String delegatorName;
    FlexibleMapAccessor listAcsr;
    
    Condition whereCondition;
    Condition havingCondition;
    List selectFieldExpanderList;
    List orderByExpanderList;
    OutputHandler outputHandler;
    

    public FindByCondition(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        entityName = element.getAttribute("entity-name");
        useCacheStr = element.getAttribute("use-cache");
        filterByDateStr = element.getAttribute("filter-by-date");
        distinctStr = element.getAttribute("distinct");
        delegatorName = element.getAttribute("delegator-name");
        listAcsr = new FlexibleMapAccessor(element.getAttribute("list-name"));
        
        // TODO: process condition | condition-list
        Element conditionElement = UtilXml.firstChildElement(element, "condition");
        Element conditionListElement = UtilXml.firstChildElement(element, "condition-list");
        if (conditionElement != null && conditionListElement != null) {
            throw new IllegalArgumentException("In entity find by condition element, cannot have condition and condition-list sub-elements");
        }
        if (conditionElement != null) {
        } else if (conditionListElement != null) {
        }
        
        // TODO: process having-condition-list
        // TODO: process select-field
        // TODO: process order-by
        // TODO: process limit-range | limit-view | use-iterator
        
    }

    public boolean exec(MethodContext methodContext) {
        String entityName = methodContext.expandString(this.entityName);
        String useCacheStr = methodContext.expandString(this.useCacheStr);
        String filterByDateStr = methodContext.expandString(this.filterByDateStr);
        String distinctStr = methodContext.expandString(this.distinctStr);
        String delegatorName = methodContext.expandString(this.delegatorName);
        
        boolean useCache = "true".equals(useCacheStr);
        boolean filterByDate = "true".equals(filterByDateStr);
        boolean distinct = "true".equals(distinctStr);
        
        GenericDelegator delegator = methodContext.getDelegator();
        if (delegatorName != null && delegatorName.length() > 0) {
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }

        // TODO: if useCache == true && outputHandler instanceof UseIterator, throw exception; not a valid combination
        
        // TODO: create whereEntityCondition from whereCondition

        // TODO: create havingEntityCondition from havingCondition

        // TODO: get the list of fieldsToSelect from selectFieldExpanderList
        
        // TODO: get the list of orderByFields from orderByExpanderList
        
        /*
        try {
            // TODO: if filterByDate, do a date filter on the results based on the now-timestamp
            
            if (useCache) {
                listAcsr.put(methodContext, delegator.findByAndCache(entityName, (Map) mapAcsr.get(methodContext), orderByNames));
            } else {
                listAcsr.put(methodContext, delegator.findByAnd(entityName, (Map) mapAcsr.get(methodContext), orderByNames));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [problem finding the " + entityName + " entity: " + e.getMessage() + "]";

            if (methodContext.getMethodType() == MethodContext.EVENT) {
                methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
            } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
            }
            return false;
        }
        */
        return true;
    }
    
    public static interface Condition {
        public EntityCondition createCondition(Map context);
    }
    public static class SingleCondition implements Condition {
        FlexibleStringExpander fieldNameExdr;
        FlexibleStringExpander operatorExdr;
        FlexibleMapAccessor envNameAcsr;
        FlexibleStringExpander valueExdr;
        
        public SingleCondition(Element conditionElement) {
            // TODO: implement this
        }
        
        public EntityCondition createCondition(Map context) {
            // TODO: implement this
            return null;
        }
    }
    public static class ConditionList implements Condition {
        List conditionList;
        FlexibleStringExpander combineExdr;
        
        public ConditionList(Element conditionListElement) {
            // TODO: implement this
        }
        
        public EntityCondition createCondition(Map context) {
            // TODO: implement this
            return null;
        }
    }
    
    public static interface OutputHandler {
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr);
    }
    public static class LimitRange implements OutputHandler {
        FlexibleStringExpander startExdr;
        FlexibleStringExpander sizeExdr;
        
        public LimitRange(Element limitRangeElement) {
            // TODO: implement this
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            // TODO: implement this
        }
    }
    public static class LimitView implements OutputHandler {
        FlexibleStringExpander viewIndexExdr;
        FlexibleStringExpander viewSizeExdr;
        
        public LimitView(Element limitViewElement) {
            // TODO: implement this
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            // TODO: implement this
        }
    }
    public static class UseIterator implements OutputHandler {
        public UseIterator(Element useIteratorElement) {
            // TODO: implement this
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            // TODO: implement this
        }
    }
}

