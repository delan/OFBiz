package org.ofbiz.entitygen;

import java.util.*;

/**
 * <p><b>Title:</b> Entity Generator - Relation model class
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
 *@created    May 31, 2001
 *@version    1.0
 */

public class Relation
{
  /** the relation-title, gives a name/description to the relation */
  public String relationTitle = "";
  /** the relation-type: either "one" or "many" */
  public String relationType = "";
  /** the name of the related table */
  public String relatedTableName = "";
  /** the name of the related EJB/entity */
  public String relatedEjbName = "";
  /** keyMaps defining how to lookup the relatedTable using columns from this table */
  public Vector keyMaps = new Vector();
  /** relations nested under this relation */
  public Vector relations = new Vector();
  /** the parent relation of this relation */
  public Relation parent = null;
  /** the main entity of this relation */
  public Entity mainEntity = null;
  
  /** Default Constructor */  
  public Relation()
  {
  }

  /** Alternate Constructor which sets the parent Relation */  
  public Relation(Relation parent)
  {
    this.parent = parent;
  }
  
  /** Find a KeyMap with the specified fieldName */  
  public KeyMap findKeyMap(String fieldName)
  {
    for(int i=0; i<keyMaps.size(); i++)
    {
      KeyMap keyMap = (KeyMap)keyMaps.elementAt(i);
      if(keyMap.fieldName.equals(fieldName)) return keyMap;
    }
    return null;
  }

  /** Find a KeyMap with the specified relatedFieldName */  
  public KeyMap findKeyMapByRelated(String relatedFieldName)
  {
    for(int i=0; i<keyMaps.size(); i++)
    {
      KeyMap keyMap = (KeyMap)keyMaps.elementAt(i);
      if(keyMap.relatedFieldName.equals(relatedFieldName)) return keyMap;
    }
    return null;
  }

  public String keyMapUpperString(String separator, String afterLast)
  {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++)
    {
      returnString = returnString + GenUtil.upperFirstChar(((KeyMap)keyMaps.elementAt(i)).fieldName) + separator;
    }
    returnString = returnString + GenUtil.upperFirstChar(((KeyMap)keyMaps.elementAt(i)).fieldName) + afterLast;
    return returnString;
  }

  public String keyMapRelatedUpperString(String separator, String afterLast)
  {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++)
    {
      returnString = returnString + GenUtil.upperFirstChar(((KeyMap)keyMaps.elementAt(i)).relatedFieldName) + separator;
    }
    returnString = returnString + GenUtil.upperFirstChar(((KeyMap)keyMaps.elementAt(i)).relatedFieldName) + afterLast;
    return returnString;
  }

  public String keyMapColumnString(String separator, String afterLast)
  {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++)
    {
      returnString = returnString + ((KeyMap)keyMaps.elementAt(i)).columnName + separator;
    }
    returnString = returnString + ((KeyMap)keyMaps.elementAt(i)).columnName + afterLast;
    return returnString;
  }

  public String keyMapRelatedColumnString(String separator, String afterLast)
  {
    String returnString = "";
    if(keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < keyMaps.size() - 1; i++)
    {
      returnString = returnString + ((KeyMap)keyMaps.elementAt(i)).relatedColumnName + separator;
    }
    returnString = returnString + ((KeyMap)keyMaps.elementAt(i)).relatedColumnName + afterLast;
    return returnString;
  }
}
