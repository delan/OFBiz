/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project (www.ofbiz.org)
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

package org.ofbiz.core.util;

import java.util.*;

/**
 * OrderedSet - Set interface wrapper around a LinkedList
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    1.0
 * @created    April 5, 2002
 */
public class OrderedSet extends AbstractSet {

    // This set's back LinkedList
    private List backedList = new LinkedList();


    /**
     * Constructs a set containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this set.
     */
    public OrderedSet(Collection c) {
        Iterator i = c.iterator();
        while (i.hasNext())
            add(i.next());
    }

    /**
     * Returns an iterator over the elements in this set.
     *
     * @return an iterator over the elements in this set.
     */
    public Iterator iterator() {
        return backedList.iterator();
    }

    /**
     * Returns the number of elements in this set.
     *
     * @return the number of elements in this set.
     */
     public int size() {
         return backedList.size();
     }

    /**
     * Appends the specified element to the end of this set.

     * @param obj element to be appended to this set.
     * @return <tt>true</tt> if this set did not already
     * contain the specified element.
     */
    public boolean add(Object obj) {
        int index = backedList.indexOf(obj);
        if (index == -1)
            return backedList.add(obj);
        else {
            backedList.add(index, obj);
            return false;
        }
    }
}
