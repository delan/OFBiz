package org.ofbiz.base.util.collections;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Stores the result of a subset of data items from a source (such
 * as EntityListIterator).
 */
public class PagedList<E> implements Iterable<E> {

    protected int startIndex;
    protected int endIndex;
    protected int size;
    protected int viewIndex;
    protected int viewSize;
    protected List<E> data;

    /**
     * Default constructor - populates all fields in this class
     * @param startIndex
     * @param endIndex
     * @param size
     * @param viewIndex
     * @param viewSize
     * @param data
     */
    public PagedList(int startIndex, int endIndex, int size, int viewIndex, int viewSize, List<E> data) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.size = size;
        this.viewIndex = viewIndex;
        this.viewSize = viewSize;
        this.data = data;
    }

    /**
     * @param viewIndex
     * @param viewSize
     * @return an empty PagedList object
     */
    public static <E> PagedList<E> empty(int viewIndex, int viewSize) {
        List<E> emptyList = Collections.emptyList();
        return new PagedList<E>(0, 0, 0, viewIndex, viewSize, emptyList);
    }

    /**
     * @return the start index (for paginator) or known as low index
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @return the end index (for paginator) or known as high index
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * @return the size of the full list, this can be the 
     * result of <code>EntityListIterator.getResultsSizeAfterPartialList()</code>
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the paged data. Eg - the result from <code>EntityListIterator.getPartialList()</code>
     */
    public List<E> getData() {
        return data;
    }

    /**
     * @return the view index supplied by client
     */
    public int getViewIndex() {
        return viewIndex;
    }

    /**
     * @return the view size supplied by client
     */
    public int getViewSize() {
        return viewSize;
    }

    /**
     * @return an interator object over the data returned in getData() method
     *         of this class
     */
    @Override
    public Iterator<E> iterator() {
        return this.data.iterator();
    }

}
