package org.ofbiz.core.entity;

import java.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Type Utilities Class
 * <p><b>Description:</b> Makes it easier to deal with entities that follow the 
 *    extensibility pattern and that can be of various types as identified in the database.
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
 *@author     Eric Pabst and David E. Jones
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class EntityTypeUtil
{
  public static boolean isType(Collection thisCollection, String typeRelation, GenericValue targetType) {
      Iterator iter = thisCollection.iterator();
      while (iter.hasNext()) {
          if (isType(((GenericValue) iter.next()).getRelatedOne(typeRelation), targetType)) {
              return true;
          }//else keep looking
      }
      return false;
  }

  /*public static boolean isType(Collection thisTypeCollection, GenericValue targetType) {
      Iterator iter = thisTypeCollection.iterator();
      while (iter.hasNext()) {
          if (isType((GenericValue) iter.next(), targetType)) {
              return true;
          }//else keep looking
      }
      return false;
  }*/
    
  
/*  private static Object getTypeID(GenericValue typeValue) {
      Collection keys = typeValue.getAllKeys();
      if (keys.size() == 1) {
          return keys.iterator().next();
      } else {
          throw new IllegalArgumentException("getTypeID expecting value with single key");
      }
  }*/
    
  private static GenericValue getParentType(GenericValue typeValue) {
      //assumes Parent relation is "Parent<entityName>" 
      return typeValue.getRelatedOneCache("Parent" + typeValue.getEntityName());
  }

  /**
   *  Description of the Method
   *
   *@param  catName                       Description of Parameter
   *@exception  java.rmi.RemoteException  Description of Exception
   */
  public static boolean isType(GenericValue thisType, GenericValue targetType)
  {
      if (thisType == null) {
          return false;
      } else if (targetType.equals(thisType)) {
          return true;
      } else {
          return isType(getParentType(thisType), targetType);
      }
  }
}
