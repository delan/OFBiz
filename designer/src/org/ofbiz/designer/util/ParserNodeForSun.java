package org.ofbiz.designer.util;

/*
package org.ofbiz.designer.util;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import com.sun.xml.parser.Resolver;
import com.sun.xml.tree.ElementEx;
import com.sun.xml.tree.XmlDocument;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


//
//  This class encapsulates the functionalities of the Sun XML parser library
//

public class ParserNode {
	static final String SLASH = System.getProperty("file.separator");
	private Node node;
	private XmlDocument doc = null;
	private String nodePath = null;

	// Initialize the class with the Node & xml document
	ParserNode(Node nodeIn, XmlDocument docIn){
		if (nodeIn == null) throw new RuntimeException("node is null");
		node = nodeIn;
		doc = docIn;
	}

	// Instantiate the class from a (XML) file
	public static ParserNode loadXMLRoot(File file) throws IOException{
//System.err.println("ParserNode.loadXMLRoot - Point 0");
//		String filePath = file.getPath();
//		String dtdPath = filePath;
//System.err.println("  file.getPath - " + dtdPath);
//		int pathIndex = filePath.indexOf(WFActionEvent.WORKFLOWINFO);
//		if( pathIndex == -1 )
//			return null;
//		pathIndex += WFActionEvent.WORKFLOWINFO.length() + 1;
//		dtdPath = filePath.substring(0, pathIndex) + "dtd" + SLASH;
//System.err.println("  dtdPath - " + dtdPath);
//		String publicId = null;
//		if( (pathIndex=filePath.indexOf(WFActionEvent.WORKFLOWINFO+SLASH+"tasks")) != -1 ) {
//			publicId = "NetworkTask";
//			dtdPath += "NetworkTask.dtd";
//		} else if( (pathIndex=filePath.indexOf(WFActionEvent.WORKFLOWINFO+SLASH+"data")) != -1 ) {
//			publicId = "DataClass";
//			dtdPath += "DataClass.dtd";
//		} else if( (pathIndex=filePath.indexOf(WFActionEvent.WORKFLOWINFO+SLASH+"domainEnvironments")) != -1 ) {
//			publicId = "DomainEnv";
//			dtdPath += "DomainEnv.dtd";
//		} else if( (pathIndex=filePath.indexOf(WFActionEvent.WORKFLOWINFO+SLASH+"RoleDomains")) != -1 ) {
//			publicId = "RoleDomain";
//			dtdPath += "RoleDomain.dtd";
//		} else {
//			return null;
//		}
//System.err.println("  dtdPath - " + dtdPath);
//System.err.println("ParserNode.loadXMLRoot - Point 1");
		InputSource input = Resolver.createInputSource(file);
//System.err.println("  input system id - "+input.getSystemId());
//System.err.println("  input public id - "+input.getPublicId());
//System.err.println("ParserNode.loadXMLRoot - Point 2");
		XmlDocument docTemp = null;
//System.err.println("ParserNode.loadXMLRoot - Point 3");
		try {
			//docTemp = XmlDocument.createXmlDocument(input, true);
			docTemp = XmlDocument.createXmlDocument(input, false);
		} catch( SAXException se) {
//System.err.println("ParserNode.loadXMLRoot - Point 4a");
			se.printStackTrace();
			return null;
		}
//System.err.println("ParserNode.loadXMLRoot - Point 4b");
		Node rootTemp = (Node)docTemp.getDocumentElement();
//System.err.println("ParserNode.loadXMLRoot - Point 5");
		return new ParserNode(rootTemp, docTemp);
	}

	// Generate the sub-Node corresponding to an id string
	public ParserNode getNodeById(String id) {
		ElementEx elem = doc.getElementExById(id);
		if( elem != null )
			return new ParserNode(elem, doc);
		return null;
	}

	// Return the number of *direct* child nodes for this node
	public int size(){
		if (node.getChildNodes() == null) return -1;
		else return node.getChildNodes().getLength();
	}

	// Return the number of attributes for this node
	public int attListSize(){
		return node.getAttributes().getLength();
	}

	// Return the index'th child node for this node
	public ParserNode elementAt(int index){
		if( node.getChildNodes().getLength() == 0 )
			throw new ArrayIndexOutOfBoundsException("The node "+node.getNodeName()+" in node "+node.getParentNode().getNodeName()+" index "+index+" out of bounds");
		return new ParserNode(node.getChildNodes().item(index), doc);
	}

	// Return the *direct* child node with the given tag
	public ParserNode elementByName(String name){
		int numChildren = node.getChildNodes().getLength();
		for( int i=0; i < numChildren; i++ ) {
			Node tempNode = node.getChildNodes().item(i);
			if( tempNode.getNodeName().equals(name) )
				return new ParserNode(tempNode, doc);
		}
		return null;
	}

	// Return the index'th child node with the given tag
	public ParserNode elementByName(String name, int index){
		int numChildren = node.getChildNodes().getLength();
		int nameCount = 1;
		for( int i=0; i < numChildren; i++ ) {
			Node tempNode = node.getChildNodes().item(i);
			if( tempNode.getNodeName().equals(name) ) {
				if( nameCount == index )
					return new ParserNode(tempNode, doc);
				nameCount++;
			}
		}
		return null;
	}

	// Return the attribute of this node corresponding to the given key
	public String getAttribute(String key){
		return node.getAttributes().getNamedItem(key).getNodeValue();
	}

	// Return the node text value
	public String getNodeValue(){
		return node.getNodeValue();
	}

	// Return the node tag name
	public String getNodeName(){
		return node.getNodeName();
	}

	// Generate the string value of the current text node
	public String getTextValueOf() {
		org.w3c.dom.NodeList nodeList = node.getChildNodes();
		for( int i=0; i<nodeList.getLength(); i++) {
			org.w3c.dom.Node tmpNode = nodeList.item(i);
			if( tmpNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE ) {
				return tmpNode.getNodeValue();
			}
		}
		return null;
	}

}
*/