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
package org.ofbiz.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Contains extra information about Messages
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class MessageString {
    
    public static final String module = MessageString.class.getName();
    
    protected String message;
    protected String fieldName;
    protected String toFieldName;
    protected Throwable sourceError;
    protected Locale locale;
    protected String propertyResource;
    protected String propertyName;
    protected boolean isError = true;
    
    public static List getMessagesForField(String fieldName, boolean convertToStrings, List messageStringList) {
        if (fieldName == null) {
            return Collections.EMPTY_LIST;
        }
        Set fieldSet = new TreeSet();
        fieldSet.add(fieldName);
        return getMessagesForField(fieldSet, convertToStrings, messageStringList);
    }
    public static List getMessagesForField(String fieldName1, String fieldName2, String fieldName3, String fieldName4, boolean convertToStrings, List messageStringList) {
        Set fieldSet = new TreeSet();
        if (fieldName1 != null && fieldName1.length() > 0) fieldSet.add(fieldName1);
        if (fieldName2 != null && fieldName2.length() > 0) fieldSet.add(fieldName2);
        if (fieldName3 != null && fieldName3.length() > 0) fieldSet.add(fieldName3);
        if (fieldName4 != null && fieldName4.length() > 0) fieldSet.add(fieldName4);
        return getMessagesForField(fieldSet, convertToStrings, messageStringList);
    }
    public static List getMessagesForField(Set fieldNameSet, boolean convertToStrings, List messageStringList) {
        if (messageStringList == null || fieldNameSet == null || fieldNameSet.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List outList = new ArrayList(messageStringList.size());
        Iterator messageStringIter = messageStringList.iterator();
        while (messageStringIter.hasNext()) {
            Object messageStringCur = messageStringIter.next();
            if (messageStringCur instanceof MessageString) {
                MessageString messageString = (MessageString) messageStringCur;
                if (messageString.isForField(fieldNameSet)) {
                    if (convertToStrings) {
                        outList.add(messageString.toString());
                    } else {
                        outList.add(messageString);
                    }
                }
            } else {
                // not a MessageString, don't know if it is for this field so skip it
                continue;
            }
        }
        return outList;
    }
    
    /**
     * @param message
     * @param fieldName
     * @param locale
     * @param propertyResource
     * @param propertyName
     */
    public MessageString(String message, String fieldName, String propertyResource, String propertyName, Locale locale, boolean isError) {
        this.message = message;
        this.fieldName = fieldName;
        this.locale = locale;
        this.propertyResource = propertyResource;
        this.propertyName = propertyName;
        this.isError = isError;
    }
    /**
     * @param message
     * @param fieldName
     */
    public MessageString(String message, String fieldName, boolean isError) {
        this.message = message;
        this.fieldName = fieldName;
        this.isError = isError;
    }
    /**
     * @param message
     * @param fieldName
     * @param toFieldName
     * @param sourceError
     */
    public MessageString(String message, String fieldName, String toFieldName, Throwable sourceError) {
        this.message = message;
        this.fieldName = fieldName;
        this.toFieldName = toFieldName;
        this.sourceError = sourceError;
        this.isError = true;
    }
    /**
     * @param message
     * @param sourceError
     */
    public MessageString(String message, Throwable sourceError) {
        this.message = message;
        this.sourceError = sourceError;
        this.isError = true;
    }
    
    /**
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return fieldName;
    }
    /**
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public boolean isForField(Set fieldNameSet) {
        if (fieldNameSet == null) {
            return true;
        }
        return fieldNameSet.contains(this.fieldName);
    }
    public boolean isForField(String fieldName) {
        if (this.fieldName == null) {
            if (fieldName == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return this.fieldName.equals(fieldName);
        }
    }
    
    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @return Returns the sourceError.
     */
    public Throwable getSourceError() {
        return sourceError;
    }
    /**
     * @param sourceError The sourceError to set.
     */
    public void setSourceError(Throwable sourceError) {
        this.sourceError = sourceError;
    }
    /**
     * @return Returns the toFieldName.
     */
    public String getToFieldName() {
        return toFieldName;
    }
    /**
     * @param toFieldName The toFieldName to set.
     */
    public void setToFieldName(String toFieldName) {
        this.toFieldName = toFieldName;
    }

    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * @param locale The locale to set.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }
    /**
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    /**
     * @return Returns the propertyResource.
     */
    public String getPropertyResource() {
        return propertyResource;
    }
    /**
     * @param propertyResource The propertyResource to set.
     */
    public void setPropertyResource(String propertyResource) {
        this.propertyResource = propertyResource;
    }
    
    /**
     * @return Returns the isError.
     */
    public boolean isError() {
        return isError;
    }
    /**
     * @param isError The isError to set.
     */
    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String toString() {
        return this.message;
    }
}
