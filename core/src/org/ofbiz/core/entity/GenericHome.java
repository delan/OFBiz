package org.ofbiz.core.entity;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;

/**
 * <p><b>Title:</b> Generic Entity EJB Home Interface
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones
 *@created    Sat Aug 11 2001
 *@version    1.0
 */
public interface GenericHome extends EJBHome
{
  public GenericRemote create(String entityName, Map fields) throws RemoteException, CreateException;
  public GenericRemote create(GenericValue value) throws RemoteException, CreateException;
  public GenericRemote create(GenericPK primaryKey) throws RemoteException, CreateException;

  public GenericRemote findByPrimaryKey(GenericPK primaryKey) throws RemoteException, FinderException;
  public Collection findAll(String entityName, List orderBy) throws RemoteException, FinderException;
  public Collection findByAnd(String entityName, Map fields, List orderBy) throws RemoteException, FinderException;
}
