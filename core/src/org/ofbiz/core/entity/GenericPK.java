package org.ofbiz.core.entity;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Primary Key Object
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
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericPK extends GenericEntity
{
  /** Creates new GenericPK */
  public GenericPK(String entityName) { super(entityName); }
  /** Creates new GenericPK from existing Map */
  public GenericPK(String entityName, Map fields) { super(entityName, fields); }
  /** Creates new GenericPK from existing GenericPK */
  public GenericPK(GenericPK value) { super(value); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericPK(String entityName, String name1, Object value1) { super(entityName, name1, value1); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericPK(String entityName, String name1, Object value1, String name2, Object value2) { super(entityName, name1, value1, name2, value2); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericPK(String entityName, String name1, Object value1, String name2, Object value2, String name3, Object value3) { super(entityName, name1, value1, name2, value2, name3, value3); }
  /** Creates new GenericPK from Map based on parameters */
  public GenericPK(String entityName, String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4) { super(entityName, name1, value1, name2, value2, name3, value3, name4, value4); }
}
