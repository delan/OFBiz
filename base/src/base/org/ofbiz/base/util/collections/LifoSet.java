/*
 * $Id: LifoSet.java,v 1.1 2004/07/11 03:09:34 jonesde Exp $
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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * LifoSet - Set interface wrapper around a LinkedList
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class LifoSet extends AbstractSet {

    // This set's back LinkedList
    private LinkedList backedList = new LinkedList();
    private int maxCapacity = 10;

    /**
     * Constructs a set containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     */
    public LifoSet() {}

    /**
     * Constructs a set containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this set.
     */

    public LifoSet(int capacity) {
        maxCapacity = capacity;
    }

    /**
     * @see java.util.Collection#size()
     */
    public int size() {
        return backedList.size();
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object obj) {
        int index = backedList.indexOf(obj);

        if (index == -1) {
            backedList.addFirst(obj);
            while (size() > maxCapacity)
                backedList.removeLast();
        } else {
            backedList.remove(index);
            backedList.addFirst(obj);
        }
        return true;
    }

    /**
     * @see java.util.Collection#iterator()
     */  
    public Iterator iterator() {
        return backedList.iterator();
    }
}

