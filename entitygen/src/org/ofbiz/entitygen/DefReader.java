package org.ofbiz.entitygen;

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

import org.ofbiz.commonapp.common.UtilCache;
import org.ofbiz.commonapp.common.UtilTimer;

/**
 * <p><b>Title:</b> Entity Generator - Entity Definition Reader
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

public class DefReader
{
  public static UtilCache documentCache = new UtilCache("entitygen-document-cache");
  public static UtilCache entityCache = new UtilCache("entitygen-entity-cache");

  /** Creates an Entity object based on a definition from the specified XML Entity descriptor file.
   * @param defFileName The full path and file name of the XML Entity descriptor file.
   * @param ejbName The ejbName of the Entity definition to use.
   * @return An Entity object describing the specified entity of the specified descriptor file.
   */    
  public static Entity getEntity(String defFileName, String ejbName)
  {
    Entity entity = null;
    if(entityCache.containsKey(defFileName + "::" + ejbName))
    {
      entity = (Entity)entityCache.get(defFileName + "::" + ejbName);
    }
    else
    {
      Document document = getDocument(defFileName);
      if(document == null) return null;
      Hashtable docElementValues = null;
      if(documentCache.containsKey(defFileName + ":elementvalues"))
      {
        docElementValues = (Hashtable)documentCache.get(defFileName + ":elementvalues");
      }
      else
      {
        docElementValues = new Hashtable();
        documentCache.put(defFileName + ":elementvalues", docElementValues);
      }

      Element docElement = document.getDocumentElement();
      if(docElement == null) return null;
      docElement.normalize();

      entity = createEntity(findEntity(docElement, ejbName), docElement, null, docElementValues);
      if(entity != null) entityCache.put(defFileName + "::" + ejbName, entity);
      else System.out.println("-- -- ENTITYGEN ERROR:getEntity: Could not create entity for ejbName: " + ejbName);
    }
    return entity;
  }

  /** Creates a Iterator with the ejbName of each Entity defined in the specified XML Entity Descriptor file.
   * @param defFileName The full path and file name of the XML Entity descriptor file.
   * @return A Iterator of ejbName Strings
   */  
  public static Iterator getEjbNamesIterator(String defFileName)
  {
    Collection collection = getEjbNames(defFileName);
    if(collection != null) return collection.iterator();
    else return null;
  }

  /** Creates a Collection with the ejbName of each Entity defined in the specified XML Entity Descriptor file.
   * @param defFileName The full path and file name of the XML Entity descriptor file.
   * @return A Collection of ejbName Strings
   */  
  public static Collection getEjbNames(String defFileName)
  {
    Document document = getDocument(defFileName);
    if(document == null) return null;
    Vector ejbNames = new Vector();

    if(documentCache.containsKey(defFileName + ":ejbnames"))
    {
      ejbNames = (Vector)documentCache.get(defFileName + ":ejbnames");
    }
    else
    {
      Element docElement = document.getDocumentElement();
      if(docElement == null) return null;
      docElement.normalize();
      NodeList entityList = docElement.getElementsByTagName("entity");
      for(int i=0; i<entityList.getLength(); i++)
      {
        ejbNames.add(entityEjbName(entityList.item(i)));
      }
      
      documentCache.put(defFileName + ":ejbnames", ejbNames);
    }

    return ejbNames;
  }
  
  /** Creates a Collection with the ejbName of each Entity defined in the specified XML Entity Descriptor file
   *   and as it reaches each entity it creates and Entity object for that entity, which will be cached.
   * @param defFileName The full path and file name of the XML Entity descriptor file.
   * @return A Collection of ejbName Strings
   */  
  public static Collection loadAllEntities(String defFileName)
  {
    Vector ejbNames = new Vector();
    UtilTimer utilTimer = new UtilTimer();
    utilTimer.timerString("Before getDocument");
    Document document = getDocument(defFileName);
    if(document == null) return null;
    Hashtable docElementValues = null;
    if(documentCache.containsKey(defFileName + ":elementvalues"))
    {
      docElementValues = (Hashtable)documentCache.get(defFileName + ":elementvalues");
    }
    else
    {
      docElementValues = new Hashtable();
      documentCache.put(defFileName + ":elementvalues", docElementValues);
    }

    utilTimer.timerString("Before getDocumentElement");
    Element docElement = document.getDocumentElement();
    if(docElement == null) return null;
    docElement.normalize();
    utilTimer.timerString("Before getElementsByTagName(entity)");
    NodeList entityList = docElement.getElementsByTagName("entity");

    utilTimer.timerString("Before start of loop");
    int i;
    for(i=0; i<entityList.getLength(); i++)
    {
      utilTimer.timerString("Start loop -- " + i + " --");
      Element curEntity = (Element)entityList.item(i);
      String ejbName = entityEjbName(curEntity);
      //utilTimer.timerString("  After entityEjbName -- " + i + " --");
      ejbNames.add(ejbName);      
      //Entity entity = createEntity(curEntity, docElement, utilTimer, docElementValues);
      Entity entity = createEntity(curEntity, docElement, null, docElementValues);
      utilTimer.timerString("  After createEntity -- " + i + " --");
      if(entity != null) 
      {
        entityCache.put(defFileName + "::" + ejbName, entity);
        //utilTimer.timerString("  After entityCache.put -- " + i + " --");
        System.out.println("-- getEntity: Created entity for ejbName: " + ejbName);
      }
      else System.out.println("-- -- ENTITYGEN ERROR:getEntity: Could not create entity for ejbName: " + ejbName);
    }
    utilTimer.timerString("FINISHED - Total Entities: " + i + " FINISHED");
    return ejbNames;    
  }
  
  static Element findEntity(Element docElement, String ejbName)
  {
    if(docElement == null || ejbName == null) return null;
    NodeList entityList = docElement.getElementsByTagName("entity");
    for(int i=0; i<entityList.getLength(); i++)
    {
      if(ejbName.compareTo(entityEjbName(entityList.item(i))) == 0)
        return (Element)entityList.item(i);
    }
    return null;
  }
  
  static Entity createEntity(Element entityElement, Element docElement, UtilTimer utilTimer, Hashtable docElementValues)
  {
    if(entityElement == null) return null;
    org.ofbiz.entitygen.Entity entity = new Entity();
    
    if(utilTimer != null) utilTimer.timerString("  createEntity: before general");
    entity.ejbName = checkNull(entityEjbName(entityElement));
    entity.tableName = checkNull(childElementValue(entityElement, "table-name"));
    entity.packageName = checkNull(childElementValue(entityElement, "package-name"));
    entity.useCache = ("true".compareToIgnoreCase(checkNull(childElementValue(entityElement, "use-cache"))) == 0);
    entity.allOrderBy = checkNull(childElementValue(entityElement, "all-order-by"));

    if(utilTimer != null) utilTimer.timerString("  createEntity: before comments");
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

    if(utilTimer != null) utilTimer.timerString("  createEntity: before cmp-fields");
    NodeList fieldList = entityElement.getElementsByTagName("cmp-field");
    for(int i=0; i<fieldList.getLength(); i++)
    {
      org.ofbiz.entitygen.Field field = createField((Element)fieldList.item(i), docElement, docElementValues);
      if(field != null) entity.fields.add(field);
    }
    
    if(utilTimer != null) utilTimer.timerString("  createEntity: before prim-key-columns");
    NodeList pkList = entityElement.getElementsByTagName("prim-key-column");
    for(int i=0; i<pkList.getLength(); i++)
    {
      org.ofbiz.entitygen.Field field = findField(entity, elementValue((Element)pkList.item(i)));
      if(field != null) 
      {
        entity.pks.add(field);
        field.isPk = true;
      }
    }
    
    entity.primKeyClass = childElementValue(entityElement, "prim-key-class");
    if(entity.primKeyClass == null)
    {
      //figure out the primary key class if it isn't specified
      if(entity.pks.size() == 1)
      {
        Field pkField = (Field)entity.pks.elementAt(0);
        entity.primKeyClass = pkField.javaType;
        if(entity.primKeyClass.indexOf(".") < 0)
        {
          //just assume it is in java.lang
          entity.primKeyClass = "java.lang." + entity.primKeyClass;
        }
      }
      else
      {
        entity.primKeyClass = entity.packageName + "." + entity.ejbName + "PK";
      }
    }

    if(utilTimer != null) utilTimer.timerString("  createEntity: before finders");
    NodeList finderList = entityElement.getElementsByTagName("finder");
    for(int i=0; i<finderList.getLength(); i++)
    {
      Finder finder = createFinder(entity, (Element)finderList.item(i));
      if(finder != null) entity.finders.add(finder);
    }

    if(utilTimer != null) utilTimer.timerString("  createEntity: before relations");
    NodeList relationList = entityElement.getElementsByTagName("relation");
    for(int i=0; i<relationList.getLength(); i++)
    {
      Element relationElement = (Element)relationList.item(i);
      if(relationElement.getParentNode() == entityElement)
      {
        Relation relation = createRelation(entity, relationElement, null);
        if(relation != null) entity.relations.add(relation);
      }
    }

    return entity;
  }
  
  static Relation createRelation(Entity entity, Element relationElement, Relation parent)
  {
    Relation relation = new Relation(parent);
    relation.mainEntity = entity;

    relation.relationTitle = checkNull(childElementValue(relationElement, "relation-title"));
    relation.relationType = checkNull(childElementValue(relationElement, "relation-type"));
    relation.relatedTableName = checkNull(childElementValue(relationElement, "related-table-name"));
    relation.relatedEjbName = checkNull(childElementValue(relationElement, "related-ejb-name"),GenUtil.dbNameToClassName(checkNull(relation.relatedTableName)));

    NodeList keyMapList = relationElement.getElementsByTagName("key-map");
    for(int i=0; i<keyMapList.getLength(); i++)
    {
      Element keyMapElement = (Element)keyMapList.item(i);
      if(keyMapElement.getParentNode() == relationElement)
      {
        KeyMap keyMap = createKeyMap(keyMapElement);
        if(keyMap != null) relation.keyMaps.add(keyMap);
      }
    }

    //recursively add relations...
    NodeList relationList = relationElement.getElementsByTagName("relation");
    for(int i=0; i<relationList.getLength(); i++)
    {
      Element relationSubElement = (Element)relationList.item(i);
      if(relationSubElement.getParentNode() == relationElement)
      {
        Relation relationNested = createRelation(entity, relationSubElement, relation);
        if(relationNested != null) relation.relations.add(relationNested);
      }
    }
    return relation;
  }

  static KeyMap createKeyMap(Element keyMapElement)
  {
    KeyMap keyMap = new KeyMap();

    keyMap.columnName = checkNull(childElementValue(keyMapElement, "column-name"));
    keyMap.fieldName = checkNull(childElementValue(keyMapElement, "field-name"),GenUtil.dbNameToVarName(checkNull(keyMap.columnName)));
    //if no relatedColumnName is specified, use the columnName; this is convenient for when they are named the same, which is often the case
    keyMap.relatedColumnName = checkNull(childElementValue(keyMapElement, "related-column-name"),keyMap.columnName);
    keyMap.relatedFieldName = checkNull(childElementValue(keyMapElement, "related-field-name"),GenUtil.dbNameToVarName(checkNull(keyMap.relatedColumnName)));

    return keyMap;
  }

  static Finder createFinder(org.ofbiz.entitygen.Entity entity, Element finderElement)
  {
    Finder finder = new Finder();

    finder.orderBy = checkNull(childElementValue(finderElement, "order-by"));

    NodeList columnNameString = finderElement.getElementsByTagName("column-name");
    for(int i=0; i<columnNameString.getLength(); i++)
    {
      org.ofbiz.entitygen.Field field = findField(entity, elementValue((Element)columnNameString.item(i)));
      if(field != null) finder.fields.add(field);
    }
    return finder;
  }

  static org.ofbiz.entitygen.Field findField(org.ofbiz.entitygen.Entity entity, String columnName)
  {
    for(int i=0; i<entity.fields.size(); i++)
    {
      org.ofbiz.entitygen.Field field = (org.ofbiz.entitygen.Field)entity.fields.elementAt(i);
      if(field.columnName.compareTo(columnName) == 0) return field;
    }
    return null;
  }
  
  static org.ofbiz.entitygen.Field createField(Element fieldElement, Element docElement, Hashtable docElementValues)
  {
    if(fieldElement == null) return null;
    
    org.ofbiz.entitygen.Field field = new org.ofbiz.entitygen.Field();
    
    // first check to see if a field-type was specified, and if so load
    //  the sql-type, java-type, and validator elements from the field-type-def
    String fieldType = childElementValue(fieldElement, "field-type");
    if(fieldType != null && fieldType.length() > 0)
    {
      Element fieldTypeDef = null;
      if(docElementValues == null)
      {
        fieldTypeDef = findFieldTypeDef(fieldType, docElement);
      }
      else
      {
        if(docElementValues.containsKey("field-type:" + fieldType))
        {
          fieldTypeDef = (Element)docElementValues.get("field-type:" + fieldType);
        }
        else
        {
          fieldTypeDef = findFieldTypeDef(fieldType, docElement);
          docElementValues.put("field-type:" + fieldType, fieldTypeDef);
        }
      }
      
      if(fieldTypeDef != null)
      {
        field.javaType = checkNull(childElementValue(fieldTypeDef, "java-type"));
        field.sqlType = checkNull(childElementValue(fieldTypeDef, "sql-type"));

        NodeList validateList = fieldTypeDef.getElementsByTagName("validate");
        for(int i=0; i<validateList.getLength(); i++)
        {
          Element element = (Element)validateList.item(i);
          field.validators.add(checkNull(elementValue(element)));
        }        
      }
    }

    //load the cmp-field data last so it can override the field-type data
    field.fieldName = childElementValue(fieldElement, "field-name");
    if(field.fieldName == null) field.fieldName = GenUtil.dbNameToVarName(checkNull(childElementValue(fieldElement, "column-name")));
    field.javaType = checkNull(childElementValue(fieldElement, "java-type"), field.javaType);
    field.columnName = checkNull(childElementValue(fieldElement, "column-name"));
    field.sqlType = checkNull(childElementValue(fieldElement, "sql-type"), field.sqlType);
    field.isPk = false;    
    
    NodeList validateList = fieldElement.getElementsByTagName("validate");
    for(int i=0; i<validateList.getLength(); i++)
    {
      Element element = (Element)validateList.item(i);
      field.validators.add(checkNull(elementValue(element)));
    }
    
    return field;
  }
  
  static Element findFieldTypeDef(String fieldType, Element docElement)
  {
    if(fieldType == null) return null;
    NodeList nodeList = docElement.getElementsByTagName("field-type-def");
    for(int i=0; i<nodeList.getLength(); i++)
    {
      Element element = (Element)nodeList.item(i);
      if(fieldType.equals(checkNull(childElementValue(element, "field-type"))))
        return element;
    }
    return null;
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

  static String entityEjbName(Node entityNode)
  {
    Element entityElement = (Element)entityNode;

    String ejbName = childElementValue(entityElement, "ejb-name");
    if(ejbName != null) return ejbName;

    //if no ejb-name exists, make it from the table-name
    String tableName = childElementValue(entityElement, "table-name");
    if(tableName != null) return GenUtil.dbNameToClassName(tableName);

    return "[ejb-name not found]";
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
      if(documentCache.containsKey(filename + ":document"))
      {
        document = (Document)documentCache.get(filename + ":document");
      }
      else
      {
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File(filename));
        documentCache.put(filename + ":document", document);
      }
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
    catch(IOException ioe) 
    {
      // I/O error
      ioe.printStackTrace();
    }
    
    return document;
  }  
}
