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

/**
 * <p><b>Title:</b> Entity Generator - Entity class
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
    /** Creates an Entity object based on a definition from the specified XML Entity descriptor file.
     * @param defFileName The full path and file name of the XML Entity descriptor file.
     * @param ejbName The ejbName of the Entity definition to use.
     * @return An Entity object describing the specified entity of the specified descriptor file.
     */    
  public static Entity getEntity(String defFileName, String ejbName)
  {
    Document document = getDocument(defFileName);
    if(document == null) return null;
    
    Element docElement = document.getDocumentElement();
    if(docElement == null) return null;
    docElement.normalize();
    return createEntity(findEntity(docElement, ejbName), docElement);
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
    Element docElement = document.getDocumentElement();
    if(docElement == null) return null;
    docElement.normalize();
    NodeList entityList = docElement.getElementsByTagName("entity");
    for(int i=0; i<entityList.getLength(); i++)
    {
      ejbNames.add(entityEjbName(entityList.item(i)));
    }
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
  
  static Entity createEntity(Element entityElement, Element docElement)
  {
    if(entityElement == null) return null;
    org.ofbiz.entitygen.Entity entity = new Entity();
    
    entity.ejbName = checkNull(entityEjbName(entityElement));
    entity.tableName = checkNull(childElementValue(entityElement, "table-name"));
    entity.packageName = checkNull(childElementValue(entityElement, "package-name"));
    entity.useCache = ("true".compareToIgnoreCase(checkNull(childElementValue(entityElement, "use-cache"))) == 0);

    entity.title = checkNull(childElementValue(entityElement, "title"),childElementValue(docElement, "title"),"None");
    entity.description = checkNull(childElementValue(entityElement, "description"),childElementValue(docElement, "description"),"None");
    entity.copyright = checkNull(childElementValue(entityElement, "copyright"),childElementValue(docElement, "copyright"),"Copyright (c) 2001 The Open For Business Project - www.ofbiz.org");
    entity.author = checkNull(childElementValue(entityElement, "author"),childElementValue(docElement, "author"),"None");
    entity.version = checkNull(childElementValue(entityElement, "version"),childElementValue(docElement, "version"),"1.0");

    NodeList fieldList = entityElement.getElementsByTagName("cmp-field");
    for(int i=0; i<fieldList.getLength(); i++)
    {
      org.ofbiz.entitygen.Field field = createField((Element)fieldList.item(i));
      if(field != null) entity.fields.add(field);
    }
    
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

    NodeList finderList = entityElement.getElementsByTagName("finder");
    for(int i=0; i<finderList.getLength(); i++)
    {
      Finder finder = createFinder(entity, (Element)finderList.item(i));
      if(finder != null) entity.finders.add(finder);
    }
    
    return entity;
  }
  
  static Finder createFinder(org.ofbiz.entitygen.Entity entity, Element finderElement)
  {
    Finder finder = new Finder();
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
  
  static org.ofbiz.entitygen.Field createField(Element fieldElement)
  {
    if(fieldElement == null) return null;
    
    org.ofbiz.entitygen.Field field = new org.ofbiz.entitygen.Field();
    
    field.fieldName = childElementValue(fieldElement, "field-name");
    if(field.fieldName == null) field.fieldName = GenUtil.dbNameToVarName(checkNull(childElementValue(fieldElement, "column-name")));
    field.javaType = checkNull(childElementValue(fieldElement, "java-type"));
    field.columnName = checkNull(childElementValue(fieldElement, "column-name"));
    field.sqlType = checkNull(childElementValue(fieldElement, "sql-type"));
    field.isPk = false;    
    
    return field;
  }
  
  static Document getDocument(String filename)
  {
    Document document = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //factory.setValidating(true);   
    //factory.setNamespaceAware(true);
    try 
    {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(new File(filename));
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
}
