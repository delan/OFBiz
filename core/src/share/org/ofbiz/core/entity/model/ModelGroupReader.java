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
 * <p><b>Title:</b> Generic Entity - Entity Group Definition Reader
 * <p><b>Description:</b>
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
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @created May 15, 2001
 * @version 1.0
 */

public class ModelGroupReader {
  public static Map readers = new Hashtable();
  
  private Map groupCache = null;
  private Set groupNames = null;
  
  public String modelName;
  public String entityGroupFileName;
  
  public static ModelGroupReader getModelGroupReader(String delegatorName) {
    String tempModelName = UtilProperties.getPropertyValue("servers", delegatorName + ".model.group.reader");
    ModelGroupReader reader = (ModelGroupReader)readers.get(tempModelName);
    if(reader == null) //don't want to block here
    {
      synchronized(ModelGroupReader.class) {
        //must check if null again as one of the blocked threads can still enter
        reader = (ModelGroupReader)readers.get(tempModelName);
        if(reader == null) {
          reader = new ModelGroupReader(tempModelName);
          readers.put(tempModelName, reader);
        }
      }
    }
    return reader;
  }
  
  public ModelGroupReader(String modelName) {
    this.modelName = modelName;
    entityGroupFileName = UtilProperties.getPropertyValue("servers", modelName + ".xml.group");
    
    //preload caches...
    getGroupCache();
  }
  
  public Map getGroupCache() {
    if(groupCache == null) //don't want to block here
    {
      synchronized(ModelReader.class) {
        //must check if null again as one of the blocked threads can still enter
        if(groupCache == null) //now it's safe
        {
          groupCache = new HashMap();
          groupNames = new TreeSet();
          
          UtilTimer utilTimer = new UtilTimer();
          utilTimer.timerString("[ModelGroupReader.getGroupCache] Before getDocument");
          Document document = getDocument(entityGroupFileName);
          if(document == null) { groupCache = null; return null; }
          
          Hashtable docElementValues = null;
          docElementValues = new Hashtable();
          
          utilTimer.timerString("[ModelGroupReader.getGroupCache] Before getDocumentElement");
          Element docElement = document.getDocumentElement();
          if(docElement == null) { groupCache = null; return null; }
          docElement.normalize();
          Node curChild = docElement.getFirstChild();
          
          int i=0;
          if(curChild != null) {
            utilTimer.timerString("[ModelGroupReader.getGroupCache] Before start of entity loop");
            do {
              if(curChild.getNodeType() == Node.ELEMENT_NODE && "entity-group".equals(curChild.getNodeName())) {
                Element curEntity = (Element)curChild;
                String entityName = checkNull(curEntity.getAttribute("entity"));
                String groupName = checkNull(curEntity.getAttribute("group"));
                if(groupName == null || entityName == null) continue;
                groupNames.add(groupName);
                groupCache.put(entityName, groupName);
                //utilTimer.timerString("  After entityEntityName -- " + i + " --");
                i++;
              }
            } while((curChild = curChild.getNextSibling()) != null);
          }
          else Debug.logWarning("[ModelGroupReader.getGroupCache] No child nodes found.");
          utilTimer.timerString("[ModelGroupReader.getGroupCache] FINISHED - Total Entity-Groups: " + i + " FINISHED");
        }
      }
    }
    return groupCache;
  }
  
  /** Gets a group name based on a definition from the specified XML Entity Group descriptor file.
   * @param entityName The entityName of the Entity Group definition to use.
   * @return A group name
   */
  public String getEntityGroupName(String entityName) {
    Map gc = getGroupCache();
    if(gc != null) return (String)gc.get(entityName);
    else return null;
  }
  
  /** Creates a Collection with all of the groupNames defined in the specified XML Entity Group Descriptor file.
   * @return A Collection of groupNames Strings
   */
  public Collection getGroupNames() {
    getGroupCache();
    if(groupNames == null) return null;
    return new ArrayList(groupNames);
  }
  
  /** Creates a Collection with names of all of the entities for a given group
   * @return A Collection of entityName Strings
   */
  public Collection getEntityNamesByGroup(String groupName) {
    Map gc = getGroupCache();
    Collection enames = new LinkedList();
    if(groupName == null || groupName.length() <= 0) return enames;
    if(gc == null || gc.size() < 0) return enames;
    Set gcEntries = gc.entrySet();
    Iterator gcIter = gcEntries.iterator();
    while(gcIter.hasNext()) {
      Map.Entry entry = (Map.Entry)gcIter.next();
      if(groupName.equals(entry.getValue())) enames.add(entry.getKey());
    }
    return enames;
  }
  
  String checkNull(String string) {
    if(string != null) return string;
    else return "";
  }
  
  Document getDocument(String filename) {
    if(filename == null || filename.length() <=0) return null;
    Document document = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    //factory.setNamespaceAware(true);
    try {
      //if(documentCache.containsKey(filename + ":document")) document = (Document)documentCache.get(filename + ":document");
      //else {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(new File(filename));
      //documentCache.put(filename + ":document", document);
      //}
    }
    catch (SAXException sxe) {
      // Error generated during parsing)
      Exception  x = sxe;
      if(sxe.getException() != null) x = sxe.getException();
      x.printStackTrace();
    }
    catch(ParserConfigurationException pce) {
      // Parser with specified options can't be built
      pce.printStackTrace();
    }
    catch(IOException ioe) { ioe.printStackTrace(); }
    
    return document;
  }
}
