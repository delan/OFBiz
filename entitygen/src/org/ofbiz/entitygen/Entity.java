package org.ofbiz.entitygen;

import java.util.*;

/**
 * <p><b>Title:</b> Entity Generator - Entity model class
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
 *@created    May 15, 2001
 *@version    1.0
 */

public class Entity
{
  /** The ejb-name of the Entity */    
  public String ejbName = "";
  /** The table-name of the Entity */  
  public String tableName = "";
  /** The package-name of the Entity */  
  public String packageName = "";
  /** The prim-key-class of the Entity */  
  public String primKeyClass = "";
  /** The order string for a findAll */  
  public String allOrderBy = "";
  /** Use the Value object cache in the Helper? */
  public boolean useCache = false;

  //Strings to go in the comment header.
  /** The title for the class JavaDoc comment */  
  public String title = "";
  /** The description for the class JavaDoc comment */  
  public String description = "";
  /** The copyright for the class JavaDoc comment */  
  public String copyright = "";
  /** The author for the class JavaDoc comment */  
  public String author = "";
  /** The version for the class JavaDoc comment */  
  public String version = "";

  /** A Vector of the Field objects for the Entity */  
  public Vector fields = new Vector();
  /** A Vector of the Field objects for the Entity, one for each Primary Key */  
  public Vector pks = new Vector();
  /** A Vector of the Finder objects for the Entity */  
  public Vector finders = new Vector();
  /** relations defining relationships between this entity and other entities */  
  public Vector relations = new Vector();

  /** Default Constructor */  
  public Entity()
  {
  }

  public void makePkArray()
  {
    for(int i = 0; i < fields.size(); i++)
    {
      if(((Field)fields.elementAt(i)).isPk)
      {
        pks.add(fields.elementAt(i));
      }
    }
  }

  public String nameString(Vector flds) { return nameString(flds, ", ", ""); }
  public String nameString(Vector flds, String separator, String afterLast)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((Field)flds.elementAt(i)).fieldName + separator;
    }
    returnString = returnString + ((Field)flds.elementAt(i)).fieldName + afterLast;
    return returnString;
  }

  public String typeNameString(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((Field)flds.elementAt(i)).javaType + " " + ((Field)flds.elementAt(i)).fieldName + ", ";
    }
    returnString = returnString + ((Field)flds.elementAt(i)).javaType + " " + ((Field)flds.elementAt(i)).fieldName;
    return returnString;
  }

  public String fieldNameString() { return fieldNameString(", ", ""); }
  public String fieldNameString(String separator, String afterLast) { return nameString(fields, separator, afterLast); }
  public String fieldTypeNameString() { return typeNameString(fields); }
  
  public String primKeyClassNameString() { return typeNameString(pks); }
  public String pkNameString() { return pkNameString(", ", ""); }
  public String pkNameString(String separator, String afterLast) { return nameString(pks, separator, afterLast); }

  public String nonPkNullList()
  {
    return fieldsStringList(fields, "null", ", ", false, true);
  }

  public String fieldsStringList(Vector flds, String eachString, String separator)
  {
    return fieldsStringList(flds, eachString, separator, false, false);
  }
  
  public String fieldsStringList(Vector flds, String eachString, String separator, boolean appendIndex)
  {
    return fieldsStringList(flds, eachString, separator, appendIndex, false);
  }
  
  public String fieldsStringList(Vector flds, String eachString, String separator, boolean appendIndex, boolean onlyNonPK)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size(); i++)
    {
      if(onlyNonPK && ((Field)flds.elementAt(i)).isPk) continue;
      returnString = returnString + eachString;
      if(appendIndex) returnString = returnString + (i+1);
      if(i < flds.size() - 1) returnString = returnString + separator;
    }
    return returnString;
  }

  public String colNameString(Vector flds) { return colNameString(flds, ", ", ""); }
  public String colNameString(Vector flds, String separator, String afterLast)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((Field)flds.elementAt(i)).columnName + separator;
    }
    returnString = returnString + ((Field)flds.elementAt(i)).columnName + afterLast;
    return returnString;
  }

  public String classNameString(Vector flds) { return classNameString(flds, ", ", ""); }
  public String classNameString(Vector flds, String separator, String afterLast)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + separator;
    }
    returnString = returnString + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + afterLast;
    return returnString;
  }

  public String finderQueryString(Vector flds)
  {    
    String returnString = "";
    if(flds.size() < 1) { return ""; }
    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((Field)flds.elementAt(i)).columnName + " like {" + i + "} AND ";
    }
    returnString = returnString + ((Field)flds.elementAt(i)).columnName + " like {" + i + "}";
    return returnString;
  }
  
  public String httpArgList(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }
    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + ((Field)flds.elementAt(i)).fieldName + " + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + ((Field)flds.elementAt(i)).fieldName;
    return returnString;
  }

  public String httpArgListFromClass(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(ejbName) + ".get" + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + "() + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(ejbName) + ".get" + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + "()";
    return returnString;
  }

  public String httpArgListFromClass(Vector flds, String ejbNameSuffix)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(ejbName) + ejbNameSuffix + ".get" + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + "() + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(ejbName) + ejbNameSuffix + ".get" + GenUtil.upperFirstChar(((Field)flds.elementAt(i)).fieldName) + "()";
    return returnString;
  }

  public String httpRelationArgList(Vector flds, Relation relation)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      KeyMap keyMap = relation.findKeyMapByRelated(((Field)flds.elementAt(i)).fieldName);
      if(keyMap != null) returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(relation.mainEntity.ejbName) + ".get" + GenUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
      else System.out.println("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for fieldName: " + ((Field)flds.elementAt(i)).fieldName + " related entity: " + relation.relatedEjbName + " main entity: " + relation.mainEntity.ejbName + " type: " + relation.relationType);
    }
    KeyMap keyMap = relation.findKeyMapByRelated(((Field)flds.elementAt(i)).fieldName);
    if(keyMap != null) returnString = returnString + "\"" + tableName + "_" + ((Field)flds.elementAt(i)).columnName + "=\" + " + GenUtil.lowerFirstChar(relation.mainEntity.ejbName) + ".get" + GenUtil.upperFirstChar(keyMap.fieldName) + "()";
    else System.out.println("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for fieldName: " + ((Field)flds.elementAt(i)).fieldName + " related entity: " + relation.relatedEjbName + " main entity: " + relation.mainEntity.ejbName + " type: " + relation.relationType);
    return returnString;
  }

  public String httpRelationArgList(Relation relation)
  {
    String returnString = "";
    if(relation.keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < relation.keyMaps.size() - 1; i++)
    {
      KeyMap keyMap = (KeyMap)relation.keyMaps.elementAt(i);
      if(keyMap != null)
        returnString = returnString + "\"" + tableName + "_" + keyMap.relatedColumnName + "=\" + " + GenUtil.lowerFirstChar(relation.mainEntity.ejbName) + ".get" + GenUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
    }
    KeyMap keyMap = (KeyMap)relation.keyMaps.elementAt(i);
    returnString = returnString + "\"" + tableName + "_" + keyMap.relatedColumnName + "=\" + " + GenUtil.lowerFirstChar(relation.mainEntity.ejbName) + ".get" + GenUtil.upperFirstChar(keyMap.fieldName) + "()";
    return returnString;
  }
}

