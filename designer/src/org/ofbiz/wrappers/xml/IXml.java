/*

 * Created by IntelliJ IDEA.

 * User: Oliver Wieland

 * Date: Jul 27, 2001

 * Time: 1:14:38 PM

 * To change template for new class use 

 * Code Style | Class Templates options (Tools | IDE Options).

 */

package org.ofbiz.wrappers.xml;



import java.net.URL;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Iterator;



import org.jdom.*; 

import org.jdom.input.*; 

import org.jdom.output.*;



public class IXml {

    private IIDRefBinding binding = new IIDRefBinding();

    private Document xmlDocument;

    private Object root = null;
    private Element rootElement = null;

    private void createDocument(File pFile) throws IOException {

        try {

            // Build w/ SAX and Xerces, no validation

            SAXBuilder b = new SAXBuilder();

            // Create the document
            System.out.println("Parsng file " + pFile.getName() + " ...");
            xmlDocument = b.build(pFile);

        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    public static IXml openDocument(URL pUrl) throws IOException {

        // Hack
        return openDocument ( new File (pUrl.getFile()) );
    }

    public static IXml openDocument(File pFile) throws IOException {

        IXml doc = new IXml();
        doc.createDocument(pFile);

        return doc;
    }

    public void saveDocument(File pFile) throws IOException {

        try {

            // Output as XML to screen

            XMLOutputter outputter = new XMLOutputter();

            outputter.output(xmlDocument, new FileWriter(pFile));

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static void saveDocument() throws IOException {
    }

    public static IXml newDocument(File pFile, String pName) throws IOException {

        return null;
    }

    public IIDRefBinding getIDRefBinding() {

        return binding;       
    }

    public Object getRoot() {

        if (root == null) {
            
            rootElement = xmlDocument.getRootElement();
            try {
                root = create( rootElement );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            build( xmlDocument.getRootElement() , root );
            
        }

        return root;        
    }

    private void build(Element pNode, Object pParent) {
        
        Object newObj = null;
        
        // handle root element 
        if (!pNode.getName().equalsIgnoreCase(rootElement.getName())) {
            try {
                newObj = create(pNode);        
            } catch(Throwable t) {
                
                // there are fields that doesn't required an element, 
                // like Name and SimpleType in the DataClass DTD
                
                if (pNode.getName().equalsIgnoreCase("Name")        || 
                    pNode.getName().equalsIgnoreCase("Package")     ||
                    pNode.getName().equalsIgnoreCase("IdAttribute") ||
                    pNode.getName().equalsIgnoreCase("Description") ||
                    pNode.getName().equalsIgnoreCase("DefaultValue")) {
                    try {
                            invokeAttrSetter(pParent, "set" +  pNode.getName(), 
                                             pNode.getContent().get(0) );                    
                        }
                        catch (Exception ex) {
                            //System.out.println("cannot set reate failed for '" + pNode.getName() + "' "+t);
                        }
                
                // there are fields, for wich exists only one supprt Class :
                // [SimpleType|Url]: SimpleTypeOrUrl
                } else if (pNode.getName().equalsIgnoreCase("SimpleType") ||
                           pNode.getName().equalsIgnoreCase("Url")) {
                   try {
                        // david weia .....                        
                        newObj = create("SimpleTypeOrUrl");
                        invokeAttrSetter(newObj, "set" +  pNode.getName(), 
                                         pNode.getContent().get(0) );                              
                        pNode.setName("SimpleTypeOrUrl");
                   }
                   catch(Exception e) {
                       System.out.println("cannot create SimpleTypeOrUrl Support Class");
                       e.printStackTrace();
                   }
                } else if (pNode.getName().equalsIgnoreCase("TransactionalInput") ||
                           pNode.getName().equalsIgnoreCase("TransactionalOutput")) {
                    try {
                        invokeAttrSetter(pParent, "add" +  pNode.getName(), 
                                         pNode.getContent().get(0) );
                   }
                   catch(Exception e) {
                       System.out.println("cannot create SimpleTypeOrUrl Support Class");
                       e.printStackTrace();
                   }
                } else if (pNode.getName().equalsIgnoreCase("Number1") ||
                           pNode.getName().equalsIgnoreCase("Number2") ||
                           pNode.getName().equalsIgnoreCase("Number3")) {
                    try {
                        invokeAttrSetter(pParent, "set" +  pNode.getName(), 
                                         pNode.getContent().get(0) );
                   }
                   catch(Exception e) {
                       System.out.println("cannot set Number[x] attribute");
                       e.printStackTrace();
                   }
                }                
                else {
                 System.out.println("create failed for '" + pNode.getName() + "' "+t);
               }
           }                
        }                    
        
        if (newObj != null) {

            binding.setIdRef( getId( pNode ), newObj);

            String name = pNode.getName();

            try {
                
                invokeSetter(pParent, "add" + name, newObj);
                
            } catch(Exception ex) {

                try {
                    
                    invokeSetter(pParent, "set" + name, newObj);
                    
                } catch (Exception e) {

                    System.out.println("set ref failed for '" + name + "': " + e);
                }
            }


            for(Iterator it = pNode.getAttributes().iterator(); it.hasNext();) {

                Attribute attr = (Attribute)it.next();

                try {

                    invokeAttrSetter(newObj, "set" + fu( attr.getName() + "Attribute" ), attr.getValue() );

                } catch (Exception e) {
                    System.out.println("set attribute failed for '" + attr + "': " + e);
                }
            }
        }

        for(Iterator it = pNode.getChildren().iterator(); it.hasNext();) {

            Element e = (Element)it.next();

            build ( e , newObj != null ? newObj : pParent ); // Rekusion über Kinderknoten
        }
    }

    /** Ersten Buchstaben groß */
    public static String fu(String text)  {

	return (text!=null && text.length()>0) 
        ? text.substring(0,1).toUpperCase()+text.substring(1,text.length()) 
        : text;
        
    }

    private Object create(Element pNode) throws Exception {

        return create(pNode.getName());
        
    }

    private Object create(String name) throws Exception {

        return Class.forName("org.ofbiz.designer." + 
               rootElement.getName().toLowerCase() + "." + name).newInstance();            
    }

    
    private String getId(Element pNode) {
        String id = pNode.getAttributeValue( "id" );
        
        //System.out.println("Node = "+pNode.getName() + " Id=" + id);
        
        return id;
    }

    private Object invokeSetter(Object pInstance, String pAttrName, Object pArg) throws Exception {

	Class c = pInstance.getClass();
	Object ret=null;

        String name = pArg.getClass().getName();

        String path = "";

        int pos = name.lastIndexOf(".");

        if (pos != -1) {

            path = name.substring( 0, pos );
            name = name.substring( pos + 1);
        }

	Method m = (Method)c.getMethod(pAttrName, new Class[] {Class.forName(path+".I" + name)});

	if (m!=null) {
            ret = m.invoke(pInstance, new Object[] {pArg});
	} else {
            System.out.println("Method not found: "+pAttrName);
	}

	return ret;
    }

    private Object invokeAttrSetter(Object pInstance, String pAttrName, Object pArg) throws Exception {

	Class c = pInstance.getClass();

        Object ret = null;

        //System.out.println("Invoke " +pInstance+ "."+ pAttrName + "( " + pArg + ")");

	Method m = (Method)c.getMethod(pAttrName, new Class[] { pArg.getClass() });

	if (m!=null) {
            ret = m.invoke(pInstance, new Object[] {pArg});
	} else {

            System.out.println("Method not found: "+pAttrName);

	}

	return ret;
    }
    
    public String getRootElementName() {
        
        return rootElement.getName();
    }
}
