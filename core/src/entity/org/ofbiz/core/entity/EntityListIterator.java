/*
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.entity;

import java.sql.*;
import java.util.*;
import org.ofbiz.core.entity.jdbc.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.util.*;

/**
 * Generic Entity Cursor List Iterator for Handling Cursored DB Results
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    July 12, 2002
 *@version    1.0
 */
public class EntityListIterator implements ListIterator {

    protected SQLProcessor sqlp;
    protected ResultSet resultSet;
    protected ModelEntity modelEntity;
    protected List selectFields;
    protected ModelFieldTypeReader modelFieldTypeReader;
    protected boolean closed = false;
    
    public EntityListIterator(SQLProcessor sqlp, ModelEntity modelEntity, List selectFields, ModelFieldTypeReader modelFieldTypeReader) {
        this.sqlp = sqlp;
        this.resultSet = sqlp.getResultSet();
        this.modelEntity = modelEntity;
        this.selectFields = selectFields;
        this.modelFieldTypeReader = modelFieldTypeReader;
    }
    
    public void close() throws GenericEntityException {
        if (closed) throw new GenericResultSetClosedException("This EntityListIterator has been closed, this operation cannot be performed");
        
        sqlp.close();
        closed = true;
    }
    
    public GenericValue currentGenericValue() throws GenericEntityException {
        if (closed) throw new GenericResultSetClosedException("This EntityListIterator has been closed, this operation cannot be performed");
        
        GenericValue value = new GenericValue(modelEntity);

        for (int j = 0; j < selectFields.size(); j++) {
            ModelField curField = (ModelField) selectFields.get(j);

            SqlJdbcUtil.getValue(resultSet, j + 1, curField, value, modelFieldTypeReader);
        }

        value.modified = false;
        return value;
    }
    
    public int currentIndex() throws GenericEntityException {
        if (closed) throw new GenericResultSetClosedException("This EntityListIterator has been closed, this operation cannot be performed");
        
        try {
            return resultSet.getRow();
        } catch (SQLException e) {
            throw new GenericEntityException("Error getting the current index", e);
        }
    }
    
    public boolean hasNext() {
        try {
            if (resultSet.isLast()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new GeneralRuntimeException("Error while checking to see if this is the last result", e);
        }
    }
    
    public boolean hasPrevious() {
        try {
            if (resultSet.isFirst()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new GeneralRuntimeException("Error while checking to see if this is the first result", e);
        }
    }
    
    public Object next() {
        try {
            if (resultSet.next()) {
                return currentGenericValue();
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new GeneralRuntimeException("Error getting the next result", e);
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException("Error creating GenericValue", e);
        }
    }
    
    public int nextIndex() {
        try {
            return currentIndex() + 1;
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException(e.getNonNestedMessage(), e.getNested());
        }
    }
    
    public Object previous() {
        try {
            if (resultSet.previous()) {
                return currentGenericValue();
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new GeneralRuntimeException("Error getting the previous result", e);
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException("Error creating GenericValue", e);
        }
    }
    
    public int previousIndex() {
        try {
            return currentIndex() + 1;
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException("Error getting the current index", e);
        }
    }
    
    public void setFetchSize(int rows) throws GenericEntityException {
        try {
            resultSet.setFetchSize(rows);
        } catch (SQLException e) {
            throw new GenericEntityException("Error getting the next result", e);
        }
    }
    
    public Collection getCompleteCollection() throws GenericEntityException {
        try {
            Collection collection = new LinkedList();
            while (this.hasNext()) {
                collection.add(this.next());
            }
            return collection;
        } catch (GeneralRuntimeException e) {
            throw new GenericEntityException(e.getNonNestedMessage(), e.getNested());
        }
    }
    
    public void add(Object obj) {
        throw new GeneralRuntimeException("CursorListIterator currently only supports read-only access");
    }
    
    public void remove() {
        throw new GeneralRuntimeException("CursorListIterator currently only supports read-only access");
    }
    
    public void set(Object obj) {
        throw new GeneralRuntimeException("CursorListIterator currently only supports read-only access");
    }
    
    protected void finalize() throws Throwable {
        try {
            sqlp.close();
        } catch (Exception e) {
            Debug.logError(e, "Error closing the result, connection, etc in finalize EntityListIterator");
        }
        super.finalize();
    }
}
