/*
 * $Id: ByConditionFinder.java,v 1.3 2004/07/10 15:18:32 jonesde Exp $
 *
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a condition
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class ByConditionFinder {
    
    public static final String module = ByConditionFinder.class.getName();         
    FlexibleStringExpander entityNameExdr;
    FlexibleStringExpander useCacheStrExdr;
    FlexibleStringExpander filterByDateStrExdr;
    FlexibleStringExpander distinctStrExdr;
    FlexibleStringExpander delegatorNameExdr;
    FlexibleMapAccessor listAcsr;
    
    Condition whereCondition;
    Condition havingCondition;
    List selectFieldExpanderList;
    List orderByExpanderList;
    OutputHandler outputHandler;
    

    public ByConditionFinder(Element element) {
        this.entityNameExdr = new FlexibleStringExpander(element.getAttribute("entity-name"));
        this.useCacheStrExdr = new FlexibleStringExpander(element.getAttribute("use-cache"));
        this.filterByDateStrExdr = new FlexibleStringExpander(element.getAttribute("filter-by-date"));
        this.distinctStrExdr = new FlexibleStringExpander(element.getAttribute("distinct"));
        this.delegatorNameExdr = new FlexibleStringExpander(element.getAttribute("delegator-name"));
        this.listAcsr = new FlexibleMapAccessor(element.getAttribute("list-name"));
        
        // process condition-expr | condition-list
        Element conditionExprElement = UtilXml.firstChildElement(element, "condition-expr");
        Element conditionListElement = UtilXml.firstChildElement(element, "condition-list");
        if (conditionExprElement != null && conditionListElement != null) {
            throw new IllegalArgumentException("In entity find by condition element, cannot have condition-expr and condition-list sub-elements");
        }
        if (conditionExprElement != null) {
            this.whereCondition = new ConditionExpr(conditionExprElement);
        } else if (conditionListElement != null) {
            this.whereCondition = new ConditionList(conditionListElement);
        }
        
        // process having-condition-list
        Element havingConditionListElement = UtilXml.firstChildElement(element, "having-condition-list");
        if (havingConditionListElement != null) {
            this.havingCondition = new ConditionList(havingConditionListElement);
        }

        // process select-field
        List selectFieldElementList = UtilXml.childElementList(element, "select-field");
        if (selectFieldElementList.size() > 0) {
            selectFieldExpanderList = new LinkedList();
            Iterator selectFieldElementIter = selectFieldElementList.iterator();
            while (selectFieldElementIter.hasNext()) {
                Element selectFieldElement = (Element) selectFieldElementIter.next();
                selectFieldExpanderList.add(new FlexibleStringExpander(selectFieldElement.getAttribute("field-name")));
            }
        }
        
        // process order-by
        List orderByElementList = UtilXml.childElementList(element, "order-by");
        if (orderByElementList.size() > 0) {
            orderByExpanderList = new LinkedList();
            Iterator orderByElementIter = orderByElementList.iterator();
            while (orderByElementIter.hasNext()) {
                Element orderByElement = (Element) orderByElementIter.next();
                orderByExpanderList.add(new FlexibleStringExpander(orderByElement.getAttribute("field-name")));
            }
        }

        // process limit-range | limit-view | use-iterator
        Element limitRangeElement = UtilXml.firstChildElement(element, "limit-range");
        Element limitViewElement = UtilXml.firstChildElement(element, "limit-view");
        Element useIteratorElement = UtilXml.firstChildElement(element, "use-iterator");
        if ((limitRangeElement != null && limitViewElement != null) || (limitRangeElement != null && useIteratorElement != null) || (limitViewElement != null && useIteratorElement != null)) {
            throw new IllegalArgumentException("In entity find by condition element, cannot have more than one of the following: limit-range, limit-view, and use-iterator");
        }
        if (limitRangeElement != null) {
            outputHandler = new LimitRange(limitRangeElement);
        } else if (limitViewElement != null) {
            outputHandler = new LimitView(limitViewElement);
        } else if (useIteratorElement != null) {
            outputHandler = new UseIterator(useIteratorElement);
        }
    }

    public void runFind(Map context, GenericDelegator delegator) throws GeneralException {
        String entityName = this.entityNameExdr.expandString(context);
        String useCacheStr = this.useCacheStrExdr.expandString(context);
        String filterByDateStr = this.filterByDateStrExdr.expandString(context);
        String distinctStr = this.distinctStrExdr.expandString(context);
        String delegatorName = this.delegatorNameExdr.expandString(context);
        
        boolean useCache = "true".equals(useCacheStr);
        boolean filterByDate = "true".equals(filterByDateStr);
        boolean distinct = "true".equals(distinctStr);
        
        if (delegatorName != null && delegatorName.length() > 0) {
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }

        // create whereEntityCondition from whereCondition
        EntityCondition whereEntityCondition = this.whereCondition.createCondition(context, entityName, delegator);

        // create havingEntityCondition from havingCondition
        EntityCondition havingEntityCondition = this.havingCondition.createCondition(context, entityName, delegator);

        if (useCache == true) {
            // if useCache == true && outputHandler instanceof UseIterator, throw exception; not a valid combination
            if (outputHandler instanceof UseIterator) {
                throw new IllegalArgumentException("In find entity by condition cannot have use-cache set to true and select use-iterator for the output type.");
            }
            if (distinct) {
                throw new IllegalArgumentException("In find entity by condition cannot have use-cache set to true and set distinct to true.");
            }
            if (havingEntityCondition != null) {
                throw new IllegalArgumentException("In find entity by condition cannot have use-cache set to true and specify a having-condition-list (can only use a where condition with condition-expr or condition-list).");
            }
        }
        
        // get the list of fieldsToSelect from selectFieldExpanderList
        List fieldsToSelect = null;
        if (this.selectFieldExpanderList != null && this.selectFieldExpanderList.size() > 0) {
            fieldsToSelect = new LinkedList();
            Iterator selectFieldExpanderIter = selectFieldExpanderList.iterator();
            while (selectFieldExpanderIter.hasNext()) {
                FlexibleStringExpander selectFieldExpander = (FlexibleStringExpander) selectFieldExpanderIter.next();
                fieldsToSelect.add(selectFieldExpander.expandString(context));
            }
        }
        
        
        // get the list of orderByFields from orderByExpanderList
        List orderByFields = null;
        if (this.orderByExpanderList != null && this.orderByExpanderList.size() > 0) {
            orderByFields = new LinkedList();
            Iterator orderByExpanderIter = orderByExpanderList.iterator();
            while (orderByExpanderIter.hasNext()) {
                FlexibleStringExpander orderByExpander = (FlexibleStringExpander) orderByExpanderIter.next();
                orderByFields.add(orderByExpander.expandString(context));
            }
        }
        
        try {
            // TODO: if filterByDate, do a date filter on the results based on the now-timestamp
            if (filterByDate) {
                throw new IllegalArgumentException("The filer-by-date feature is not yet implemented");
            }
            
            if (useCache) {
                List results = delegator.findByConditionCache(entityName, whereEntityCondition, fieldsToSelect, orderByFields);
                this.outputHandler.handleOutput(results, context, listAcsr);
            } else {
                EntityFindOptions options = new EntityFindOptions();
                options.setDistinct(distinct);
                EntityListIterator eli = delegator.findListIteratorByCondition(entityName, whereEntityCondition, havingEntityCondition, fieldsToSelect, orderByFields, options);
                this.outputHandler.handleOutput(eli, context, listAcsr);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error doing find by condition: " + e.toString();
            Debug.logError(e, module);
            throw new GeneralException(errMsg, e);
        }
    }
    
    public static interface Condition {
        public EntityCondition createCondition(Map context, String entityName, GenericDelegator delegator);
    }
    public static class ConditionExpr implements Condition {
        FlexibleStringExpander fieldNameExdr;
        FlexibleStringExpander operatorExdr;
        FlexibleMapAccessor envNameAcsr;
        FlexibleStringExpander valueExdr;
        
        public ConditionExpr(Element conditionExprElement) {
            this.fieldNameExdr = new FlexibleStringExpander(conditionExprElement.getAttribute("field-name"));
            this.operatorExdr = new FlexibleStringExpander(conditionExprElement.getAttribute("operator"));
            this.envNameAcsr = new FlexibleMapAccessor(conditionExprElement.getAttribute("env-name"));
            this.valueExdr = new FlexibleStringExpander(conditionExprElement.getAttribute("value"));
        }
        
        public EntityCondition createCondition(Map context, String entityName, GenericDelegator delegator) {
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            String fieldName = fieldNameExdr.expandString(context);
            
            Object value = null;
            // start with the environment variable, will override if exists and a value is specified
            if (envNameAcsr != null) {
                value = envNameAcsr.get(context);
            }
            // no value so far, and a string value is specified, use that
            if (value == null && valueExdr != null) {
                value = valueExdr.expandString(context);
            }
            // now to a type conversion for the target fieldName
            value = modelEntity.convertFieldValue(fieldName, value, delegator);
            
            String operatorName = operatorExdr.expandString(context);
            EntityOperator operator = EntityOperator.lookup(operatorName);
            if (operator == null) {
                throw new IllegalArgumentException("Could not find an entity operator for the name: " + operatorName);
            }
            
            return new EntityExpr(fieldName, (EntityComparisonOperator) operator, value);
        }
    }
    public static class ConditionList implements Condition {
        List conditionList = new LinkedList();
        FlexibleStringExpander combineExdr;
        
        public ConditionList(Element conditionListElement) {
            this.combineExdr = new FlexibleStringExpander(conditionListElement.getAttribute("combine"));
            
            List subElements = UtilXml.childElementList(conditionListElement);
            Iterator subElementIter = subElements.iterator();
            while (subElementIter.hasNext()) {
                Element subElement = (Element) subElementIter.next();
                if ("condition-expr".equals(subElement.getNodeName())) {
                    conditionList.add(new ConditionExpr(subElement));
                } else if ("condition-list".equals(subElement.getNodeName())) {
                    conditionList.add(new ConditionList(subElement));
                } else {
                    throw new IllegalArgumentException("Invalid element with name [" + subElement.getNodeName() + "] found under a condition-list element.");
                }
            }
        }
        
        public EntityCondition createCondition(Map context, String entityName, GenericDelegator delegator) {
            if (this.conditionList.size() == 0) {
                return null;
            }
            if (this.conditionList.size() == 1) {
                Condition condition = (Condition) this.conditionList.get(0);
                return condition.createCondition(context, entityName, delegator);
            }
            
            List entityConditionList = new LinkedList();
            Iterator conditionIter = conditionList.iterator();
            while (conditionIter.hasNext()) {
                Condition curCondition = (Condition) conditionIter.next();
                entityConditionList.add(curCondition.createCondition(context, entityName, delegator));
            }
            
            String operatorName = combineExdr.expandString(context);
            EntityOperator operator = EntityOperator.lookup(operatorName);
            if (operator == null) {
                throw new IllegalArgumentException("Could not find an entity operator for the name: " + operatorName);
            }
            
            return new EntityConditionList(entityConditionList, (EntityJoinOperator) operator);
        }
    }
    
    public static interface OutputHandler {
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr);
        public void handleOutput(List results, Map context, FlexibleMapAccessor listAcsr);
    }
    public static class LimitRange implements OutputHandler {
        FlexibleStringExpander startExdr;
        FlexibleStringExpander sizeExdr;
        
        public LimitRange(Element limitRangeElement) {
            this.startExdr = new FlexibleStringExpander(limitRangeElement.getAttribute("start"));
            this.sizeExdr = new FlexibleStringExpander(limitRangeElement.getAttribute("size"));
        }
        
        int getStart(Map context) {
            String startStr = this.startExdr.expandString(context);
            try {
                return Integer.parseInt(startStr);
            } catch (NumberFormatException e) {
                String errMsg = "The limit-range start number \"" + startStr + "\" was not valid: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        
        int getSize(Map context) {
            String sizeStr = this.sizeExdr.expandString(context);
            try {
                return Integer.parseInt(sizeStr);
            } catch (NumberFormatException e) {
                String errMsg = "The limit-range size number \"" + sizeStr + "\" was not valid: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            int start = getStart(context);
            int size = getSize(context);
            try {
                listAcsr.put(context, eli.getPartialList(start, size));
            } catch (GenericEntityException e) {
                String errMsg = "Error getting partial list in limit-range with start=" + start + " and size=" + size + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }

        public void handleOutput(List results, Map context, FlexibleMapAccessor listAcsr) {
            int start = getStart(context);
            int size = getSize(context);
            listAcsr.put(context, results.subList(start, start + size));
        }
    }
    public static class LimitView implements OutputHandler {
        FlexibleStringExpander viewIndexExdr;
        FlexibleStringExpander viewSizeExdr;
        
        public LimitView(Element limitViewElement) {
            this.viewIndexExdr = new FlexibleStringExpander(limitViewElement.getAttribute("view-index"));
            this.viewSizeExdr = new FlexibleStringExpander(limitViewElement.getAttribute("view-size"));
        }
        
        int getIndex(Map context) {
            String viewIndexStr = this.viewIndexExdr.expandString(context);
            try {
                return Integer.parseInt(viewIndexStr);
            } catch (NumberFormatException e) {
                String errMsg = "The limit-view view-index number \"" + viewIndexStr + "\" was not valid: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        
        int getSize(Map context) {
            String viewSizeStr = this.viewSizeExdr.expandString(context);
            try {
                return Integer.parseInt(viewSizeStr);
            } catch (NumberFormatException e) {
                String errMsg = "The limit-view view-size number \"" + viewSizeStr + "\" was not valid: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            int index = this.getIndex(context);
            int size = this.getSize(context);
            
            try {
                listAcsr.put(context, eli.getPartialList(index * size, size));
            } catch (GenericEntityException e) {
                String errMsg = "Error getting partial list in limit-view with index=" + index + " and size=" + size + ": " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }

        public void handleOutput(List results, Map context, FlexibleMapAccessor listAcsr) {
            int index = this.getIndex(context);
            int size = this.getSize(context);
            listAcsr.put(context, results.subList(index * size, index * size + size));
        }
    }
    public static class UseIterator implements OutputHandler {
        public UseIterator(Element useIteratorElement) {
            // no parameters, nothing to do
        }
        
        public void handleOutput(EntityListIterator eli, Map context, FlexibleMapAccessor listAcsr) {
            listAcsr.put(context, eli);
        }

        public void handleOutput(List results, Map context, FlexibleMapAccessor listAcsr) {
            throw new IllegalArgumentException("Cannot handle output with use-iterator when the query is cached, or the result in general is not an EntityListIterator");
        }
    }
}

/*
<!ELEMENT find-by-condition ( ( condition-expr | condition-list ), having-condition-list?, select-field*, order-by*, ( limit-range | limit-view | use-iterator )? )>
<!ATTLIST find-by-condition
    entity-name CDATA #REQUIRED
    use-cache ( true | false ) "false"
    filter-by-date ( true | false | by-name ) "false"
    distinct ( true | false ) "false"
    delegator-name CDATA #IMPLIED
    list-name CDATA #REQUIRED
>
    <!ELEMENT condition-list ( ( condition-expr | condition-list )+ )>
    <!ATTLIST condition-list
        combine ( and | or ) "and"
    >
    <!ELEMENT having-condition-list ( ( condition-expr | condition-list )+ )>
    <!ATTLIST having-condition-list
        combine ( and | or ) "and"
    >
    <!ELEMENT condition-expr EMPTY>
    <!ATTLIST condition-expr
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
