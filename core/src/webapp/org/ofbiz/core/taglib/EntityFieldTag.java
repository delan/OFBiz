/*
 * EntityFieldTag.java
 *
 * Created on October 2, 2001, 12:15 PM
 */

package org.ofbiz.core.taglib;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;
import org.ofbiz.core.entity.model.ModelFieldType;

/**
 *
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 
 */
public class EntityFieldTag extends TagSupport {
    
    private String attribute;
    private String field;
    private String fieldObjectType;
    private String fieldString;

    public String getAttribute() {
        return attribute;
    }
    
    public String getField() {
        return field;
    }
    
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public int doStartTag() throws JspTagException {        
        fieldObjectType = null;
        fieldString = null;
        
        // Get the ValueObject from PageContext.
        GenericValue valueObject = (GenericValue) pageContext.findAttribute(attribute);
        if ( valueObject == null ) {
            return (SKIP_BODY);  
        }
        
        // Get the Delegator from the ValueObject.        
        GenericDelegator delegator = null;
        try {
            delegator = valueObject.getDelegator();
        }
        catch ( IllegalStateException e ) {
            throw new JspTagException("Delegator not found in ValueObject.");
        }
                
        // Get the Entity Model from the ValueObject
        ModelEntity entityModel = null;
        try {
            entityModel = valueObject.getModelEntity();
        }
        catch ( IllegalStateException e ) {
            throw new JspTagException("ModelEntity not found in ValueObject.");
        }
                
        // Get the Locale from the Request object.
        Locale userLocale = pageContext.getRequest().getLocale();
        if ( userLocale == null )
            userLocale = Locale.getDefault();
        
        // Get the field as an object.
        Object fieldObject = valueObject.get(field);
        
        // Get the Object Type.         
        if ( fieldObject != null ) {            
            ModelField fieldModel = entityModel.getField(field);            
            fieldObjectType = fieldModel.type;            
        }
        else {
            fieldObject = new String();
            fieldObjectType = "comment"; // Default for NULL objects.
        }
                
        // Try to get the Field Type
        String javaType = null;
        try {
            ModelFieldType fieldType = delegator.getEntityFieldType(entityModel,fieldObjectType);
            javaType = fieldType.javaType;
        }
        catch ( GenericEntityException e ) {
            throw new JspTagException("[EntityFieldTag] : Cannot get the ModelFieldType from the Delegator.");
        }
        
        // Format the Object based on its type.
        if ( javaType.equals("java.lang.Object") || javaType.equals("Object") ) {
            fieldString = fieldObject.toString();
        }
        else if ( javaType.equals("java.lang.String") || javaType.equals("String") ) {
            fieldString = (String) fieldObject;
        }
        else if ( javaType.equals("java.lang.Double") || javaType.equals("Double") ) {
            Double doubleValue = (Double) fieldObject;     
            NumberFormat nf = null;
            if ( fieldObjectType.equals("currency-amount") ) 
                nf = NumberFormat.getCurrencyInstance(userLocale);              
            else 
                nf = NumberFormat.getNumberInstance(userLocale); 
            fieldString = nf.format(doubleValue);                                    
        }
        else if ( javaType.equals("java.lang.Long") || javaType.equals("Long") ) {
            Long longValue = (Long) fieldObject;
            NumberFormat nf = NumberFormat.getNumberInstance(userLocale);
            fieldString = nf.format(longValue);            
        }            
        else if ( javaType.equals("java.lang.Boolean") || javaType.equals("Boolean") ) {
            Boolean booleanValue = (Boolean) fieldObject;
            if ( booleanValue.booleanValue() )
                fieldString = "Yes";
            else
                fieldString = "No";            
        }
        else if ( javaType.equals("java.sql.Timestamp") ) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.FULL,userLocale);
            fieldString = df.format(dateValue);                                    
        }
        else if ( javaType.equals("java.sql.Time") ) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL,userLocale);
            fieldString = df.format(dateValue);                                    
        }
        else if ( javaType.equals("java.sql.Date") ) {
            Date dateValue = (Date) fieldObject;
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,userLocale);
            fieldString = df.format(dateValue);                                    
        }
                        
        try {
            JspWriter out = pageContext.getOut();
            out.print(fieldString);
        } 
        catch ( IOException e ) {
            throw new JspTagException(e.getMessage());
        }

        return (SKIP_BODY);    
    }
}
