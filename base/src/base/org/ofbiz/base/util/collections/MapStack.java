/*
 * $Id: MapStack.java,v 1.3 2004/07/08 04:42:31 jonesde Exp $
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
package org.ofbiz.base.util.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Map Stack
 * 
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class MapStack implements Map {

    List stackList = new LinkedList();
    
    public MapStack() {
        super();
        // initialize with a single entry
        push();
    }
    
    /** Does a shallow copy of the internal stack of the passed MapStack; enables simultaneous stacks that share common parent Maps */
    public MapStack(MapStack source) {
        super();
        this.stackList.addAll(source.stackList);
    }
    
    /** Puts a new Map on the top of the stack */
    public void push() {
        this.stackList.add(0, new HashMap());
    }
    
    /** Remove and returns the Map from the top of the stack; if there is only one Map on the stack it returns null and does not remove it */
    public Map pop() {
        // always leave at least one Map in the List, ie never pop off the last Map
        if (this.stackList.size() > 1) {
            return (Map) stackList.remove(0);
        } else {
            return null;
        }
    }
    
    /** 
     * Creates a MapStack object that has the same Map objects on its stack, 
     * but with a new Map pushed on the top; meant to be used to enable a 
     * situation where a parent and child context are operating simultaneously 
     * using two different MapStack objects, but sharing the Maps in common  
     */
    public MapStack standAloneChildStack() {
        MapStack standAloneChild = new MapStack(this);
        standAloneChild.push();
        return standAloneChild;
    }

    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {
        // a little bit tricky; to represent the apparent size we need to aggregate all keys and get a count of unique keys
        // this is a bit of a slow way, but gets the best number possible
        Set keys = this.keySet();
        return keys.size();
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        // walk the stackList and if any is not empty, return false; otherwise return true
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            if (!curMap.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        // walk the stackList and for the first place it is found return true; otherwise refurn false
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            if (curMap.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        // walk the stackList and the entries for each Map and if nothing is in for the current key, consider it an option, otherwise ignore
        Set resultKeySet = new HashSet();
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            Iterator curEntrySetIter = curMap.entrySet().iterator();
            while (curEntrySetIter.hasNext()) {
                Map.Entry curEntry = (Map.Entry) curEntrySetIter.next();
                if (!resultKeySet.contains(curEntry.getKey())) {
                    resultKeySet.add(curEntry.getKey());
                    if (value == null) {
                        if (curEntry.getValue() == null) {
                            return true;
                        }
                    } else {
                        if (value.equals(curEntry.getValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        // walk the stackList and for the first place it is found return true; otherwise refurn false
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            // only return if the curMap contains the key, rather than checking for null; this allows a null at a lower level to override a value at a higher level
            if (curMap.containsKey(key)) {
                return curMap.get(key);
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        // all write operations are local: only put in the Map on the top of the stack
        Map currentMap = (Map) this.stackList.get(0);
        return currentMap.put(key, value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        // all write operations are local: only remove from the Map on the top of the stack
        Map currentMap = (Map) this.stackList.get(0);
        return currentMap.remove(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map arg0) {
        // all write operations are local: only put in the Map on the top of the stack
        Map currentMap = (Map) this.stackList.get(0);
        currentMap.putAll(arg0);
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        // all write operations are local: only clear the Map on the top of the stack
        Map currentMap = (Map) this.stackList.get(0);
        currentMap.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        // walk the stackList and aggregate all keys
        Set resultSet = new HashSet();
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            resultSet.addAll(curMap.keySet());
        }
        return Collections.unmodifiableSet(resultSet);
    }

    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        // walk the stackList and the entries for each Map and if nothing is in for the current key, put it in
        Set resultKeySet = new HashSet();
        List resultValues = new LinkedList();
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            Iterator curEntrySetIter = curMap.entrySet().iterator();
            while (curEntrySetIter.hasNext()) {
                Map.Entry curEntry = (Map.Entry) curEntrySetIter.next();
                if (!resultKeySet.contains(curEntry.getKey())) {
                    resultKeySet.add(curEntry.getKey());
                    resultValues.add(curEntry.getValue());
                }
            }
        }
        return Collections.unmodifiableCollection(resultValues);
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        // walk the stackList and the entries for each Map and if nothing is in for the current key, put it in
        Set resultKeySet = new HashSet();
        Set resultEntrySet = new HashSet();
        Iterator stackIter = this.stackList.iterator();
        while (stackIter.hasNext()) {
            Map curMap = (Map) stackIter.next();
            Iterator curEntrySetIter = curMap.entrySet().iterator();
            while (curEntrySetIter.hasNext()) {
                Map.Entry curEntry = (Map.Entry) curEntrySetIter.next();
                if (!resultKeySet.contains(curEntry.getKey())) {
                    resultKeySet.add(curEntry.getKey());
                    resultEntrySet.add(curEntry);
                }
            }
        }
        return Collections.unmodifiableSet(resultEntrySet);
    }
}
