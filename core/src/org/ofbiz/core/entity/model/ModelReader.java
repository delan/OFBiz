package org.ofbiz.core.entity.model;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Entity - Entity Definition Reader
 * <p><b>Description:</b> Describes an Entity and acts as the base for all entity description data used in the code templates.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author David E. Jones
 * @created May 15, 2001
 * @version 1.0
 */

public class ModelReader
{
  //public static UtilCache documentCache = new UtilCache("entity-document-cache");
  public static UtilCache fieldTypeCache = null;
  public static UtilCache entityCache = null;

  public static String fieldTypeFileName = "f:\\ofbiz\\work\\ofbiz\\commonapp\\entitydef\\fieldtypemysql.xml";
  public static String entityFileName = "f:\\ofbiz\\work\\ofbiz\\commonapp\\entitydef\\entitymodel.xml";
  
  public static UtilCache getFieldTypeCache()
  {
    if(fieldTypeCache == null) //don't want to block here
    {
      synchronized(ModelReader.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(fieldTypeCache == null) //now it's safe
        {
          fieldTypeCache = new UtilCache("generic-field-type-cache");

          UtilTimer utilTimer = new UtilTimer();
          utilTimer.timerString("Before getDocument");
          Document document = getDocument(fieldTypeFileName);
          if(document == null) { fieldTypeCache = null; return null; }
          
          utilTimer.timerString("Before getDocumentElement");
          Element docElement = document.getDocumentElement();
          if(docElement == null) { fieldTypeCache = null; return null; }
          docElement.normalize();
          utilTimer.timerString("Before getElementsByTagName(field-type-def)");
          NodeList fieldTypeList = docElement.getElementsByTagName("field-type-def");

          utilTimer.timerString("Before start of loop");
          int i;
          for(i=0; i<fieldTypeList.getLength(); i++)
          {
            utilTimer.timerString("Start loop -- " + i + " --");
            Element curFieldType = (Element)fieldTypeList.item(i);
            String fieldTypeName = checkNull(childElementValue(curFieldType, "type"), "[No type name]");
            //utilTimer.timerString("  After fieldTypeName -- " + i + " --");
            ModelFieldType fieldType = createModelFieldType(curFieldType, docElement, null);
            utilTimer.timerString("  After createModelFieldType -- " + i + " --");
            if(fieldType != null) 
            {
              fieldTypeCache.put(fieldTypeName, fieldType);
              //utilTimer.timerString("  After fieldTypeCache.put -- " + i + " --");
              Debug.logInfo("-- getModelFieldType: Created fieldType for fieldTypeName: " + fieldTypeName);
            }
            else Debug.logWarning("-- -- ENTITYGEN ERROR:getModelFieldType: Could not create fieldType for fieldTypeName: " + fieldTypeName);
          }
          utilTimer.timerString("FINISHED - Total Entities: " + i + " FINISHED");
        }
      }
    }
    return fieldTypeCache;
  }
  
  public static UtilCache getEntityCache()
  {
    if(entityCache == null) //don't want to block here
    {
      synchronized(ModelReader.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(entityCache == null) //now it's safe
        {
          entityCache = new UtilCache("generic-entity-cache");

          UtilTimer utilTimer = new UtilTimer();
          utilTimer.timerString("Before getDocument");
          Document document = getDocument(entityFileName);
          if(document == null) { entityCache = null; return null; }
          
          Hashtable docElementValues = null;
          docElementValues = new Hashtable();

          utilTimer.timerString("Before getDocumentElement");
          Element docElement = document.getDocumentElement();
          if(docElement == null) { entityCache = null; return null; }
          docElement.normalize();
          utilTimer.timerString("Before getElementsByTagName(entity)");
          NodeList entityList = docElement.getElementsByTagName("entity");

          utilTimer.timerString("Before start of loop");
          int i;
          for(i=0; i<entityList.getLength(); i++)
          {
            utilTimer.timerString("Start loop -- " + i + " --");
            Element curEntity = (Element)entityList.item(i);
            String entityName = entityEntityName(curEntity);
            //utilTimer.timerString("  After entityEntityName -- " + i + " --");
            //ModelEntity entity = createModelEntity(curEntity, docElement, utilTimer, docElementValues);
            ModelEntity entity = createModelEntity(curEntity, docElement, null, docElementValues);
            utilTimer.timerString("  After createModelEntity -- " + i + " --");
            if(entity != null) 
            {
              entityCache.put(entityName, entity);
              //utilTimer.timerString("  After entityCache.put -- " + i + " --");
              Debug.logInfo("-- getModelEntity: Created entity for entityName: " + entityName);
            }
            else Debug.logWarning("-- -- ENTITYGEN ERROR:getModelEntity: Could not create entity for entityName: " + entityName);
          }
          utilTimer.timerString("FINISHED - Total Entities: " + i + " FINISHED");
        }
      }
    }
    return entityCache;
  }
  
  /** Gets an FieldType object based on a definition from the specified XML FieldType descriptor file.
   * @param fieldTypeName The fieldTypeName of the FieldType definition to use.
   * @return An FieldType object describing the specified fieldType of the specified descriptor file.
   */    
  public static ModelFieldType getModelFieldType(String fieldTypeName)
  {
    UtilCache ftc = getFieldTypeCache();
    if(ftc != null) return (ModelFieldType)ftc.get(fieldTypeName);
    else return null;
  }

  /** Gets an Entity object based on a definition from the specified XML Entity descriptor file.
   * @param entityName The entityName of the Entity definition to use.
   * @return An Entity object describing the specified entity of the specified descriptor file.
   */    
  public static ModelEntity getModelEntity(String entityName)
  {
    UtilCache ec = getEntityCache();
    if(ec != null) return (ModelEntity)ec.get(entityName);
    else return null;
  }

  /** Creates a Iterator with the entityName of each Entity defined in the specified XML Entity Descriptor file.
   * @return A Iterator of entityName Strings
   */  
  public static Iterator getEntityNamesIterator()
  {
    Collection collection = getEntityNames();
    if(collection != null) return collection.iterator();
    else return null;
  }

  /** Creates a Collection with the entityName of each Entity defined in the specified XML Entity Descriptor file.
   * @return A Collection of entityName Strings
   */  
  public static Collection getEntityNames()
  {
    UtilCache ec = getEntityCache();
    return ec.valueTable.keySet();
  }
  
  static ModelFieldType createModelFieldType(Element fieldTypeElement, Element docElement, UtilTimer utilTimer)
  {
    if(fieldTypeElement == null) return null;

    ModelFieldType field = new ModelFieldType();
    field.type = checkNull(childElementValue(fieldTypeElement, "type"));
    field.javaType = checkNull(childElementValue(fieldTypeElement, "java-type"));
    field.sqlType = checkNull(childElementValue(fieldTypeElement, "sql-type"));

    NodeList validateList = fieldTypeElement.getElementsByTagName("validate");
    for(int i=0; i<validateList.getLength(); i++)
    {
      Element element = (Element)validateList.item(i);
      field.validators.add(checkNull(elementValue(element)));
    }
    
    return field;
  }
  
  static ModelEntity createModelEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues)
  {
    if(entityElement == null) return null;
    ModelEntity entity = new ModelEntity();
    
    if(utilTimer != null) utilTimer.timerString("  createModelEntity: before general");
    entity.entityName = checkNull(entityEntityName(entityElement));
    entity.tableName = checkNull(childElementValue(entityElement, "table-name"));
    entity.useCache = ("true".compareToIgnoreCase(checkNull(childElementValue(entityElement, "use-cache"))) == 0);

    if(utilTimer != null) utilTimer.timerString("  createModelEntity: before comments");
    if(docElementValues == null)
    {
      entity.title = checkNull(childElementValue(entityElement, "title"),childElementValue(docElement, "title"),"None");
      entity.description = checkNull(childElementValue(entityElement, "description"),childElementValue(docElement, "description"),"None");
      entity.copyright = checkNull(childElementValue(entityElement, "copyright"),childElementValue(docElement, "copyright"),"Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
      entity.author = checkNull(childElementValue(entityElement, "author"),childElementValue(docElement, "author"),"None");
      entity.version = checkNull(childElementValue(entityElement, "version"),childElementValue(docElement, "version"),"1.0");
    }
    else
    {
      if(!docElementValues.containsKey("title")) docElementValues.put("title", childElementValue(docElement, "title"));
      if(!docElementValues.containsKey("description")) docElementValues.put("description", childElementValue(docElement, "description"));
      if(!docElementValues.containsKey("copyright")) docElementValues.put("copyright", childElementValue(docElement, "copyright"));
      if(!docElementValues.containsKey("author")) docElementValues.put("author", childElementValue(docElement, "author"));
      if(!docElementValues.containsKey("version")) docElementValues.put("version", childElementValue(docElement, "version"));
      entity.title = checkNull(childElementValue(entityElement, "title"),(String)docElementValues.get("title"),"None");
      entity.description = checkNull(childElementValue(entityElement, "description"),(String)docElementValues.get("description"),"None");
      entity.copyright = checkNull(childElementValue(entityElement, "copyright"),(String)docElementValues.get("copyright"),"Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
      entity.author = checkNull(childElementValue(entityElement, "author"),(String)docElementValues.get("author"),"None");
      entity.version = checkNull(childElementValue(entityElement, "version"),(String)docElementValues.get("version"),"1.0");
    }

    if(utilTimer != null) utilTimer.timerString("  createModelEntity: before fields");
    NodeList fieldList = entityElement.getElementsByTagName("field");
    for(int i=0; i<fieldList.getLength(); i++)
    {
      ModelField field = createModelField((Element)fieldList.item(i), docElement, docElementValues);
      if(field != null) entity.fields.add(field);
    }
    
    if(utilTimer != null) utilTimer.timerString("  createModelEntity: before prim-key-cols");
    NodeList pkList = entityElement.getElementsByTagName("prim-key-col");
    for(int i=0; i<pkList.getLength(); i++)
    {
      ModelField field = findModelField(entity, elementValue((Element)pkList.item(i)));
      if(field != null) 
      {
        entity.pks.add(field);
        field.isPk = true;
      }
    }
    
    //now that we have the pks and the fields, make the nopks vector
    entity.nopks = new Vector(entity.fields);
    for(int ind=0;ind<entity.pks.size();ind++)
      entity.nopks.removeElement(entity.pks.elementAt(ind)); 
    
    if(utilTimer != null) utilTimer.timerString("  createModelEntity: before relations");
    NodeList relationList = entityElement.getElementsByTagName("relation");
    for(int i=0; i<relationList.getLength(); i++)
    {
      Element relationElement = (Element)relationList.item(i);
      if(relationElement.getParentNode() == entityElement)
      {
        ModelRelation relation = createRelation(entity, relationElement);
        if(relation != null) entity.relations.add(relation);
      }
    }

    return entity;
  }
  
  static ModelRelation createRelation(ModelEntity entity, Element relationElement)
  {
    ModelRelation relation = new ModelRelation();
    relation.mainEntity = entity;

    relation.title = checkNull(childElementValue(relationElement, "title"));
    relation.type = checkNull(childElementValue(relationElement, "type"));
    relation.relTableName = checkNull(childElementValue(relationElement, "rel-table-name"));
    relation.relEntityName = checkNull(childElementValue(relationElement, "rel-entity-name"),ModelUtil.dbNameToClassName(checkNull(relation.relTableName)));

    NodeList keyMapList = relationElement.getElementsByTagName("key-map");
    for(int i=0; i<keyMapList.getLength(); i++)
    {
      Element keyMapElement = (Element)keyMapList.item(i);
      if(keyMapElement.getParentNode() == relationElement)
      {
        ModelKeyMap keyMap = createKeyMap(keyMapElement);
        if(keyMap != null) relation.keyMaps.add(keyMap);
      }
    }

    return relation;
  }

  static ModelKeyMap createKeyMap(Element keyMapElement)
  {
    ModelKeyMap keyMap = new ModelKeyMap();

    keyMap.colName = checkNull(childElementValue(keyMapElement, "col-name"));
    keyMap.fieldName = checkNull(childElementValue(keyMapElement, "field-name"),ModelUtil.dbNameToVarName(checkNull(keyMap.colName)));
    //if no relatedColumnName is specified, use the columnName; this is convenient for when they are named the same, which is often the case
    keyMap.relColName = checkNull(childElementValue(keyMapElement, "rel-col-name"),keyMap.colName);
    keyMap.relFieldName = checkNull(childElementValue(keyMapElement, "rel-field-name"),ModelUtil.dbNameToVarName(checkNull(keyMap.relColName)));

    return keyMap;
  }

  static ModelField findModelField(ModelEntity entity, String colName)
  {
    for(int i=0; i<entity.fields.size(); i++)
    {
      ModelField field = (ModelField)entity.fields.elementAt(i);
      if(field.colName.compareTo(colName) == 0) return field;
    }
    return null;
  }
  
  static ModelField createModelField(Element fieldElement, Element docElement, Hashtable docElementValues)
  {
    if(fieldElement == null) return null;
    
    ModelField field = new ModelField();
    
    field.type = checkNull(childElementValue(fieldElement, "type"));
    field.colName = checkNull(childElementValue(fieldElement, "col-name"));
    field.name = checkNull(childElementValue(fieldElement, "name"), ModelUtil.dbNameToVarName(checkNull(field.colName)));
    field.isPk = false; //is set elsewhere
    
    NodeList validateList = fieldElement.getElementsByTagName("validate");
    for(int i=0; i<validateList.getLength(); i++)
    {
      Element element = (Element)validateList.item(i);
      field.validators.add(checkNull(elementValue(element)));
    }
    
    return field;
  }
  
  static String childElementValue(Element element, String childElementName)
  {
    if(element == null || childElementName == null) return null;
    //get the value of the first element with the given name
    NodeList nodeList = element.getElementsByTagName(childElementName);
    if(nodeList.getLength() >= 1)
    {
      Element childElement = (Element)nodeList.item(0);
      return elementValue(childElement);
    }
    return null;
  }  

  static String elementValue(Element element)
  {
    Node textNode = element.getFirstChild();
    if(textNode == null) return null;
    //should be of type text
    return textNode.getNodeValue();
  }  

  static String entityEntityName(Node entityNode)
  {
    Element entityElement = (Element)entityNode;

    String entityName = childElementValue(entityElement, "entity-name");
    if(entityName != null) return entityName;

    //if no ejb-name exists, make it from the table-name
    String tableName = childElementValue(entityElement, "table-name");
    if(tableName != null) return ModelUtil.dbNameToClassName(tableName);

    return "[entity-name not found]";
  }
  
  static String checkNull(String string)
  {
    if(string != null) return string;
    else return "";
  }
  
  static String checkNull(String string1, String string2)
  {
    if(string1 != null) return string1;
    else if(string2 != null) return string2;
    else return "";
  }
  static String checkNull(String string1, String string2, String string3)
  {
    if(string1 != null) return string1;
    else if(string2 != null) return string2;
    else if(string3 != null) return string3;
    else return "";
  }
  
  static Document getDocument(String filename)
  {
    Document document = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //factory.setValidating(true);   
    //factory.setNamespaceAware(true);
    try 
    {
      //if(documentCache.containsKey(filename + ":document")) document = (Document)documentCache.get(filename + ":document");
      //else {
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File(filename));
        //documentCache.put(filename + ":document", document);
      //}
    } 
    catch (SAXException sxe) 
    {
      // Error generated during parsing)
      Exception  x = sxe;
      if(sxe.getException() != null) x = sxe.getException();
      x.printStackTrace();
    } 
    catch(ParserConfigurationException pce) 
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
    } 
    catch(IOException ioe) { ioe.printStackTrace(); }
    
    return document;
  }  
}
