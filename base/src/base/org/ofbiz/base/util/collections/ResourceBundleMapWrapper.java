/*
 * $Id$
 *
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.base.util.collections;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Set;


/**
 * Generic ResourceBundle Map Wrapper, given ResourceBundle allows it to be used as a Map
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class ResourceBundleMapWrapper implements Map {

    protected ResourceBundle resourceBundle;
    protected Map topLevelMap;
    
    public ResourceBundleMapWrapper(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        topLevelMap = new HashMap();
        // NOTE: this should return all keys, including keys from parent ResourceBundles, if not then something else must be done here...
        if (resourceBundle != null) {
            Enumeration keyNum = resourceBundle.getKeys();
            while (keyNum.hasMoreElements()) {
                String key = (String) keyNum.nextElement();
                //resourceBundleMap.put(key, bundle.getObject(key));
                Object value = resourceBundle.getObject(key);
                topLevelMap.put(key, value);
            }
        }
        topLevelMap.put("_RESOURCE_BUNDLE_", resourceBundle);
    }
    
    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {
        // this is an approximate size, won't include elements from parent bundles
        return topLevelMap.size() - 1;
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return topLevelMap.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object arg0) {
        if (topLevelMap.containsKey(arg0)) {
            return true;
        } else {
            if (this.resourceBundle.getObject((String) arg0) != null) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object arg0) {
        throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object arg0) {
        Object value = this.topLevelMap.get(arg0);
        if (resourceBundle != null) {
            if (value == null) {
                try {
                    value = this.resourceBundle.getObject((String) arg0);
                } catch(MissingResourceException mre) {
                    // do nothing, this will be handled by recognition that the value is still null
                }
            }
            if (value == null) {
                try {
                    value = this.resourceBundle.getString((String) arg0);
                } catch(MissingResourceException mre) {
                    // do nothing, this will be handled by recognition that the value is still null
                }
            }
        }
        if (value == null) {
            value = arg0;
        }
        return value;
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object arg0, Object arg1) {
        throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
    }

    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object arg0) {
        throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map arg0) {
        throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return this.topLevelMap.keySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        return this.topLevelMap.values();
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return this.topLevelMap.entrySet();
    }
    
    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }
    
    /*public String toString() {
        return this.topLevelMap.toString();
    }*/
}
