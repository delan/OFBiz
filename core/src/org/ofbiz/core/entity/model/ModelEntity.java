package org.ofbiz.core.entity.model;

import java.util.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Entity - Entity model class
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

public class ModelEntity
{
  /** The ejb-name of the Entity */    
  public String entityName = "";
  /** The table-name of the Entity */  
  public String tableName = "";
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
  /** A Vector of the Field objects for the Entity, one for each NON Primary Key */  
  public Vector nopks = new Vector();
  /** relations defining relationships between this entity and other entities */  
  public Vector relations = new Vector();

  /** Default Constructor */  
  public ModelEntity() { }

  public ModelField getField(String fieldName)
  {
    if(fieldName == null) return null;
    for(int i=0; i<fields.size(); i++)
    {
      ModelField field = (ModelField)fields.get(i);
      if(field.name.equals(fieldName)) return field;
    }
    return null;
  }
  
  public ModelRelation getRelation(String relationName)
  {
    if(relationName == null) return null;
    for(int i=0; i<relations.size(); i++)
    {
      ModelRelation relation = (ModelRelation)relations.get(i);
      if(relationName.equals(relation.title + relation.relEntityName)) return relation;
    }
    return null;
  }
  
  public String nameString(Vector flds) { return nameString(flds, ", ", ""); }
  public String nameString(Vector flds, String separator, String afterLast)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((ModelField)flds.elementAt(i)).name + separator;
    }
    returnString = returnString + ((ModelField)flds.elementAt(i)).name + afterLast;
    return returnString;
  }

  public String typeNameString(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((ModelField)flds.elementAt(i)).type + " " + ((ModelField)flds.elementAt(i)).name + ", ";
    }
    returnString = returnString + ((ModelField)flds.elementAt(i)).type + " " + ((ModelField)flds.elementAt(i)).name;
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
      if(onlyNonPK && ((ModelField)flds.elementAt(i)).isPk) continue;
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
      returnString = returnString + ((ModelField)flds.elementAt(i)).colName + separator;
    }
    returnString = returnString + ((ModelField)flds.elementAt(i)).colName + afterLast;
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
      returnString = returnString + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + separator;
    }
    returnString = returnString + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + afterLast;
    return returnString;
  }

  public String finderQueryString(Vector flds)
  {    
    String returnString = "";
    if(flds.size() < 1) { return ""; }
    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + ((ModelField)flds.elementAt(i)).colName + " like {" + i + "} AND ";
    }
    returnString = returnString + ((ModelField)flds.elementAt(i)).colName + " like {" + i + "}";
    return returnString;
  }
  
  public String httpArgList(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }
    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ((ModelField)flds.elementAt(i)).name + " + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ((ModelField)flds.elementAt(i)).name;
    return returnString;
  }

  public String httpArgListFromClass(Vector flds)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + ".get" + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + "() + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + ".get" + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + "()";
    return returnString;
  }

  public String httpArgListFromClass(Vector flds, String entityNameSuffix)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + entityNameSuffix + ".get" + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + "() + \"&\" + ";
    }
    returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(entityName) + entityNameSuffix + ".get" + ModelUtil.upperFirstChar(((ModelField)flds.elementAt(i)).name) + "()";
    return returnString;
  }

  public String httpRelationArgList(Vector flds, ModelRelation relation)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name);
      if(keyMap != null) returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
      else Debug.logWarning("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for name: " + ((ModelField)flds.elementAt(i)).name + " related entity: " + relation.relEntityName + " main entity: " + relation.mainEntity.entityName + " type: " + relation.type);
    }
    ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name);
    if(keyMap != null) returnString = returnString + "\"" + tableName + "_" + ((ModelField)flds.elementAt(i)).colName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "()";
    else Debug.logWarning("-- -- ENTITYGEN ERROR:httpRelationArgList: Related Key in Key Map not found for name: " + ((ModelField)flds.elementAt(i)).name + " related entity: " + relation.relEntityName + " main entity: " + relation.mainEntity.entityName + " type: " + relation.type);
    return returnString;
  }

  public String httpRelationArgList(ModelRelation relation)
  {
    String returnString = "";
    if(relation.keyMaps.size() < 1) { return ""; }

    int i = 0;
    for(; i < relation.keyMaps.size() - 1; i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.elementAt(i);
      if(keyMap != null)
        returnString = returnString + "\"" + tableName + "_" + keyMap.relColName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "() + \"&\" + ";
    }
    ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.elementAt(i);
    returnString = returnString + "\"" + tableName + "_" + keyMap.relColName + "=\" + " + ModelUtil.lowerFirstChar(relation.mainEntity.entityName) + ".get" + ModelUtil.upperFirstChar(keyMap.fieldName) + "()";
    return returnString;
  }

  public String typeNameStringRelatedNoMapped(Vector flds, ModelRelation relation)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    if(relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name) == null)
      returnString = returnString + ((ModelField)flds.elementAt(i)).type + " " + ((ModelField)flds.elementAt(i)).name;
    i++;
    for(; i < flds.size(); i++)
    {
      if(relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name) == null)
      {
        if(returnString.length() > 0) returnString = returnString + ", ";
        returnString = returnString + ((ModelField)flds.elementAt(i)).type + " " + ((ModelField)flds.elementAt(i)).name;
      }
    }
    return returnString;
  }

  public String typeNameStringRelatedAndMain(Vector flds, ModelRelation relation)
  {
    String returnString = "";
    if(flds.size() < 1) { return ""; }

    int i = 0;
    for(; i < flds.size() - 1; i++)
    {
      ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name);
      if(keyMap != null)
        returnString = returnString + keyMap.fieldName + ", ";
      else
        returnString = returnString + ((ModelField)flds.elementAt(i)).name + ", ";
    }
    ModelKeyMap keyMap = relation.findKeyMapByRelated(((ModelField)flds.elementAt(i)).name);
    if(keyMap != null)
      returnString = returnString + keyMap.fieldName;
    else
      returnString = returnString + ((ModelField)flds.elementAt(i)).name;
    return returnString;
  }
}

