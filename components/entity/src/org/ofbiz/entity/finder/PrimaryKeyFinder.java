/*
 * $Id$
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a condition
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class PrimaryKeyFinder {
    
    public static final String module = PrimaryKeyFinder.class.getName();         
    
    protected FlexibleStringExpander entityNameExdr;
    protected FlexibleMapAccessor valueNameAcsr;
    protected FlexibleStringExpander useCacheExdr;
    protected FlexibleStringExpander autoFieldMapExdr;
    protected Map fieldMap;
    protected List selectFieldExpanderList;

    public PrimaryKeyFinder(Element entityOneElement) {
        this.entityNameExdr = new FlexibleStringExpander(entityOneElement.getAttribute("entity-name"));
        this.valueNameAcsr = new FlexibleMapAccessor(entityOneElement.getAttribute("value-name"));
        this.useCacheExdr = new FlexibleStringExpander(entityOneElement.getAttribute("use-cache"));
        this.autoFieldMapExdr = new FlexibleStringExpander(entityOneElement.getAttribute("auto-field-map"));

        // process field-map
        this.fieldMap = EntityFinderUtil.makeFieldMap(entityOneElement);

        // process select-field
        selectFieldExpanderList = EntityFinderUtil.makeSelectFieldExpanderList(entityOneElement);
    }

    public void runFind(Map context, GenericDelegator delegator) throws GeneralException {
        String entityName = this.entityNameExdr.expandString(context);
        ModelEntity modelEntity = delegator.getModelEntity(entityName);
        
        String useCacheString = this.useCacheExdr.expandString(context);
        // default to false
        boolean useCacheBool = "true".equals(useCacheString);

        String autoFieldMapString = this.autoFieldMapExdr.expandString(context);
        // default to true
        boolean autoFieldMapBool = !"false".equals(autoFieldMapString);

        // assemble the field map
        Map entityContext = new HashMap();
        if (autoFieldMapBool) {
            GenericValue tempVal = delegator.makeValue(entityName, null);
            // just get the primary keys, and hopefully will get all of them, if not they must be manually filled in below in the field-maps
            tempVal.setAllFields(context, true, null, Boolean.TRUE);
            // if we don't get a full PK, try a map called "parameters"
            Object parametersObj = context.get("parameters");
            if (!tempVal.containsPrimaryKey() && parametersObj != null && parametersObj instanceof Map) {
                tempVal.setAllFields((Map) parametersObj, true, null, Boolean.TRUE);
            }
            entityContext.putAll(tempVal);
        }
        EntityFinderUtil.expandFieldMapToContext(this.fieldMap, context, entityContext);
        // then convert the types...
        modelEntity.convertFieldMapInPlace(entityContext, delegator);
        
        // get the list of fieldsToSelect from selectFieldExpanderList
        Set fieldsToSelect = EntityFinderUtil.makeFieldsToSelect(selectFieldExpanderList, context);
        
        //if fieldsToSelect != null and useCacheBool is true, throw an error
        if (fieldsToSelect != null && useCacheBool) {
            throw new IllegalArgumentException("Error in entity-one definition, cannot specify select-field elements when use-cache is set to true");
        }
        
        try {
            if (useCacheBool) {
                this.valueNameAcsr.put(context, delegator.findByPrimaryKeyCache(entityName, entityContext));
            } else {
                if (fieldsToSelect != null) {
                    this.valueNameAcsr.put(context, delegator.findByPrimaryKeyPartial(delegator.makePK(entityName, entityContext), fieldsToSelect));
                } else {
                    this.valueNameAcsr.put(context, delegator.findByPrimaryKey(entityName, entityContext));
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error finding entity value by primary key with entity-one: " + e.toString();
            Debug.logError(e, errMsg, module);
            throw new IllegalArgumentException(errMsg);
        }
    }
}

