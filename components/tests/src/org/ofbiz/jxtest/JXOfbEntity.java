/*
 * Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ofbiz.jxtest;

import java.util.*;
import java.io.File;
import java.net.*;
import net.sourceforge.jxunit.JXProperties;
import net.sourceforge.jxunit.JXTestCase;
import net.sourceforge.jxunit.JXTestStep;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;


/**
 * JXOfbEntity is a JXUnit test step element to create funtional tests for
 * the OFBiz framework.
 *
 * @author     <a href="mailto:bgpalmer@computer.org">Brett G. Palmer</a>
 * @version    $Revision: 1.1 $
 * @since      1.0
 */
public class JXOfbEntity implements JXTestStep
{
	
	public String delegator="";
	public String action="";
	public String type="";
	public String input="";
	public String output="";
	public String dir="";
	public String message="";
	
	private Document inputdoc;
	private JXProperties props;
	private GenericDelegator genericDelegator;

	private static String module = JXOfbEntity.class.getName();

	public void eval(JXTestCase testCase) throws Throwable	{
		
		this.props=testCase.getProperties();
		
		JXComponentLoader.loadComponents(null);
		
		String pwd = (String) props.getString(JXConstants.JX_TEST_DIRECTORY);
		readInputFile(pwd + File.separatorChar + input);
		
		//Get the named entity delegator
		getDelegator();
		
		createEntities(testCase);
	}
	
	/**
	 * readInputFile reads the input XML file.
	 * 
	 * @param input
	 * @throws DocumentException
	 */
	private void readInputFile(String input) throws DocumentException {
		
		URL url = UtilURL.fromFilename(input);
		if(url == null) {
			throw new DocumentException("File: " + input + " not found.");
		}
		SAXReader reader = new SAXReader();
		inputdoc = reader.read(url);

	}
	
	
	/**
	 * @param testCase
	 */
	private void createEntities(JXTestCase testCase) throws GenericEntityException {
		
		List list = inputdoc.selectNodes("/entity-config/entity");
		
		Iterator iter = list.iterator();
		
		while(iter.hasNext()) {
			createEntity( (Element) iter.next());
		}
		
		
	
/*		XPath xpathSelector = DocumentHelper.createXPath("/people/person[@name='James']");
		List results = xpathSelector.selectNodes(doc);
		for ( Iterator iter = result.iterator(); iter.hasNext(); ) {
		  Element element = (Element) iter.next();
		  System.out.println(element.getName();
		}
*/	
	}
	
	private void createEntity(Element element) throws GenericEntityException {
		
		String entityName = element.attributeValue("entity-name");
		Debug.log("Entity name: " + entityName, module);
		
		Map map = buildEntityMap(entityName, element);
		
		this.genericDelegator.create(entityName, map);
		
		
	}
	
	private Map buildEntityMap(String entityName, Element element) {
		
		HashMap map = new HashMap();
		
		List fieldList = element.elements();
		
		Iterator iter = fieldList.iterator();
		
		String name = null;
		String mapName = null;
		String mapInput = null;
		
		while(iter.hasNext()) {
			Element field = (Element) iter.next();
			name = field.attributeValue(JXConstants.JX_ATTRIBUTE_NAME);
			mapInput = field.attributeValue(JXConstants.MAP_INPUT_NAME);
			
			if(mapInput != null ){
				//Auto set the value from the JXProperties environment.
				map.put(name, this.props.get(mapInput));
				//String value = (String) this.props.getString(mapInput);
				//field.setText(value);
			} else {
				map.put(name, getDataType(entityName, field));
			}
			 
			//If mapName is set then save element value in the JXProperties 
			//for other tests that may need the value.
			mapName = field.attributeValue(JXConstants.MAP_OUTPUT_NAME);
			if(mapName != null) {
				this.props.put(mapName, map.get(name));
			}
		}
		
		return map;
	}
	
	/**
	 * getDataType matches Java types to SQL data types.  The matching follows the
	 * OFBiz entity data type definitions as follows:
	 * <p> 
	 * <field-type-def type="date-time" sql-type="DATETIME" java-type="java.sql.Timestamp"></field-type-def>
	 *	<field-type-def type="date" sql-type="DATE" java-type="java.sql.Date"></field-type-def>
	 *	<field-type-def type="time" sql-type="TIME" java-type="java.sql.Time"></field-type-def>
     *
	 *	<field-type-def type="currency-amount" sql-type="DECIMAL(18,2)" java-type="Double"><validate name="isSignedDouble" /></field-type-def>
	 *	<field-type-def type="floating-point" sql-type="DECIMAL(18,6)" java-type="Double"><validate name="isSignedDouble" /></field-type-def>
	 *	<field-type-def type="numeric" sql-type="DECIMAL(18,0)" java-type="Long"><validate name="isSignedLong" /></field-type-def>
	 * </p>
	 * @param element
	 * @return
	 */
	private Object getDataType(String entityName, Element element){
		
		Object object = null;
		
		String type = element.attributeValue("type");
		String instruction = element.attributeValue("instruction");
		
		
		if(type.equals("date-time")) {
			if( (instruction != null) && (instruction.equals("currentdate"))) {
				object = UtilDateTime.nowTimestamp();
			} else {
				object = java.sql.Timestamp.valueOf((String) element.getTextTrim());
			}
		} else if(type.equals("date")) {
			if( (instruction != null) && (instruction.equals("currentdate"))) {
				object = UtilDateTime.nowDate();
			} else {
				object = java.sql.Date.valueOf((String) element.getTextTrim());
			}		
		} else if(type.equals("time")) {
			if( (instruction != null) && (instruction.equals("currentdate"))) {
				object = new java.sql.Time(System.currentTimeMillis());
			} else {
				object = java.sql.Time.valueOf((String) element.getTextTrim());
			}
		} else if(type.equals("numeric")) {
			if( (instruction != null) && (instruction.equals("auto"))) {
			   object = genericDelegator.getNextSeqId(entityName);
			} else {
				//Default to zero if no value set.
				String value = (String) element.getTextTrim();
				if("".equals(value)){
					value = "0";
				}
				//Debug.log("numeric value = " + value);
				object = Long.valueOf(value);
			}
		} else if(type.equals("currency-amount")) {
			object = Double.valueOf((String) element.getTextTrim());
		} else if(type.equals("floating-point")) {
			object = Double.valueOf((String) element.getTextTrim());
		} else if(type.equals("id-vlong-ne")) {
					object = (String) element.getTextTrim();	
		// Default is a String object
		} else {
			if( (instruction != null) && (instruction.equals("auto"))) {
				object = genericDelegator.getNextSeqId(entityName).toString();
			} else {
				object = element.getTextTrim();
			}
		}
		
		return object;

	}

	private void getDelegator(){
	
		//System.out.println("bgp: here is the classpath in entity:" + System.getProperty("java.class.path"));
		this.genericDelegator = GenericDelegator.getGenericDelegator(this.delegator);

	}
}