/*
 * $Id: ByConditionFinder.java,v 1.1 2004/07/15 22:17:34 jonesde Exp $
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
package org.ofbiz.entity.finder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.finder.EntityFinderUtil.Condition;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionExpr;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
import org.ofbiz.entity.finder.EntityFinderUtil.LimitRange;
import org.ofbiz.entity.finder.EntityFinderUtil.LimitView;
import org.ofbiz.entity.finder.EntityFinderUtil.OutputHandler;
import org.ofbiz.entity.finder.EntityFinderUtil.UseIterator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a condition
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class ByConditionFinder {
    
    public static final String module = ByConditionFinder.class.getName();         
    
    protected FlexibleStringExpander entityNameExdr;
    protected FlexibleStringExpander useCacheStrExdr;
    protected FlexibleStringExpander filterByDateStrExdr;
    protected FlexibleStringExpander distinctStrExdr;
    protected FlexibleStringExpander delegatorNameExdr;
    protected FlexibleMapAccessor listAcsr;
    
    protected Condition whereCondition;
    protected Condition havingCondition;
    protected List selectFieldExpanderList;
    protected List orderByExpanderList;
    protected OutputHandler outputHandler;

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
        selectFieldExpanderList = EntityFinderUtil.makeSelectFieldExpanderList(element);
        
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

        if (useCache) {
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
        Set fieldsToSelect = EntityFinderUtil.makeFieldsToSelect(selectFieldExpanderList, context);

        //if fieldsToSelect != null and useCacheBool is true, throw an error
        if (fieldsToSelect != null && useCache) {
            throw new IllegalArgumentException("Error in entity query by condition definition, cannot specify select-field elements when use-cache is set to true");
        }
        
        // get the list of orderByFields from orderByExpanderList
        List orderByFields = EntityFinderUtil.makeOrderByFieldList(this.orderByExpanderList, context);
        
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
}

