package org.ofbiz.designer.util;

/*
package org.ofbiz.designer.util;

import com.ibm.xml.xpointer.XPointer;
import com.ibm.xml.parser.Child;
import com.ibm.xml.xpointer.Pointed;
import com.ibm.xml.xpointer.Pointed.Item;
import com.ibm.xml.parser.TXDocument;
import com.ibm.xml.parser.Parser;
import com.ibm.xml.xpointer.XPointerParseException;
import com.ibm.xml.xpointer.XPointerParser;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//
//  This class encapsulates the functionalities of the IBM XML parser library
//

public class ParserNode {
	private org.w3c.dom.Node node;
	private TXDocument doc = null;
	private String nodePath = null;

	// Initialize the class with the Node & xml document
	ParserNode(org.w3c.dom.Node nodeIn, TXDocument docIn){
		if (nodeIn == null) throw new RuntimeException("node is null");
		node = nodeIn;
		doc = docIn;
		XPointer xp = ((Child)node).makeXPointer();
		nodePath = xp==null?"":xp.toString();
		//LOG.println("nodePath is " + nodePath);
		//nodePath = node.
	}

	// Instantiate the class from a (XML) file
	public static ParserNode loadXMLRoot(File file) throws IOException{
		//LOG.println("file name is: " + file.getName());
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException notFound) {
			throw new IOException(notFound.getMessage());
		}
		String filename = file.getName();
		Parser parser = new Parser(filename);
		TXDocument docTemp = parser.readStream(fs);
		fs.close();
		org.w3c.dom.Node rootTemp = (org.w3c.dom.Node)docTemp.getDocumentElement();
		ParserNode root = new ParserNode(rootTemp, docTemp);
		return root;
	}

	// Generate the sub-Node corresponding to an id string
	public ParserNode getNodeById(String id) {
		org.w3c.dom.Node temp = getW3CNodeOf("id("+id+")");
		if (temp != null)
			return new ParserNode(temp, doc);
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
		return new ParserNode(node.getChildNodes().item(index), doc);
	}

	// Return the *direct* child node with the given tag
	public ParserNode elementByName(String name){
		return getNodeOf("child(1," + name + ")");
		//return new ParserNode(node.getChildNodes().item(index), doc);
	}

	// Return the index'th child node with the given tag
	public ParserNode elementByName(String name, int index){
		return getNodeOf("child(" + index + "," + name + ")");
		//return new ParserNode(node.getChildNodes().item(index), doc);
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

	// Generate the sub-Node corresponding to an XPointer string
//	public ParserNode getNodeOf(String parseString){
	private ParserNode getNodeOf(String parseString){
		org.w3c.dom.Node temp = getW3CNodeOf(parseString);
		if (temp != null)
			return new ParserNode(temp, doc);
		else return null;
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

	// *******************************************************************************
	// Private methods
	// *******************************************************************************

	private org.w3c.dom.Node getW3CNodeOf(String parseString){
		XPointer xp = null;
		Item it = null;
		Pointed pointed = null;

		try{
			if (parseString.startsWith("id("))
				xp = new XPointerParser().parse(parseString);
			else
				xp = new XPointerParser().parse(nodePath + "." + parseString);
		} catch (XPointerParseException e){
			return null;
		}
		pointed = xp.point(doc);
		if (pointed.size() == 0)
			return null;
		it = (Item) pointed.elementAt(0);
		return it.node;
	}

	private org.w3c.dom.Node w3CElementAt(int index){
		return node.getChildNodes().item(index);
	}

}
*/