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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;


/**
 * JXOfbServiceEngine is a JXUnit test driver to run OFBiz services. 
 *
 * @author     <a href="mailto:bgpalmer@computer.org">Brett G. Palmer</a>
 * @version    $Revision: 1.1 $
 * @since      1.0
 */
public class JXOfbServiceEngine implements JXTestStep
{
	
	public String delegator="";
	public String dispatcher="";
	public String input="";
	public String output="";
	public String dir="";
	public String message="";
	
	public static String SEQUENCE_NAME = "TestServiceSeq";
	
	private Document inputdoc;
	private JXProperties props;
	private GenericDelegator genericDelegator;
	private GenericDispatcher genericDispatcher;

	//Used to capture JXUnit outputs to service outputs
	private Map serviceOutputMap;
	
	public static final String module = JXOfbServiceEngine.class.getName();


	public void eval(JXTestCase testCase) throws Throwable	{
		
		this.props = testCase.getProperties();
		JXComponentLoader.loadComponents(null);
		
		this.serviceOutputMap = new HashMap();
		
		getDispatcher();
		
		if(input.length() > 0) {
			String pwd = (String) props.getString(JXConstants.JX_TEST_DIRECTORY);
			readInputFile(pwd + File.separatorChar + input);
		} else {
			Debug.log("Nothing to do since no input file given.", module);
		}
		
		runServices(testCase);
		
	}
	
	/**
	 * readInputFile reads the input XML file.
	 * 
	 * @param input
	 * @throws DocumentException
	 */
	private void readInputFile(String input) throws DocumentException {
		
		URL url = UtilURL.fromFilename(input);
		SAXReader reader = new SAXReader();
		if(url == null) {
		    Debug.logError("Couldn't read input: " + input, module);
		    throw new DocumentException("Couldn't read input: " + input);
		}
		inputdoc = reader.read(url);

	}
	
	
	/**
	 * @param testCase
	 */
	private void runServices(JXTestCase testCase) throws Exception, GenericServiceException {  
		
		List list = inputdoc.selectNodes("/service-config/service");
		
		Iterator iter = list.iterator();
		
		while(iter.hasNext()) {
			runService( (Element) iter.next());
		}
		
	}
	
	private void runService(Element element) throws Exception, GenericServiceException {
		
		String serviceName = element.attributeValue("service-name");
		String mode = element.attributeValue("mode");
		String user = element.attributeValue("user");
		
		if( user != null && 
		        user.startsWith("$")) {
		    Debug.log("Service name: " + serviceName + " and mode = " + mode + " and user parameter: " + user, module);
		    user = user.substring(1);
			user = (String) this.props.getString(user);
		}
		
		Debug.log("Service name: " + serviceName + " and mode = " + mode + " as user: " + user, module);


		Map serviceResults = null;		
		Map context = buildInputMap(element);
		
		//Add user login object to context for services authorization 
		GenericValue userLogin = getLoginObject(user);
		if(userLogin != null ) {
		    context.put("userLogin", userLogin);
		}
		
		if( (mode != null) && (mode.equals(JXConstants.SERVICE_MODE_ASYNC))) {
			this.genericDispatcher.runAsync(serviceName, context);
		} else {
			serviceResults = this.genericDispatcher.runSync(serviceName, context);

			//@todo Need to define JXUnit parameter to determine if fails
			//should be ignored			
			if (ModelService.RESPOND_ERROR.equals((String) serviceResults.get(ModelService.RESPONSE_MESSAGE))) {
				Debug.logError("Error occurred running service: " + serviceName, module);
			}	
		}
		
		buildOutputMap( serviceResults);
		
	}
	
	/**
	 * buildOutputMap puts all parameters from the service call to the JXUnit
	 * properties.  These will be accessible from the test.jxu test.
	 * @param element 
	 */
	private void buildOutputMap(Map serviceResults) {
		
		Set keySet = serviceResults.keySet();
		String key = null;
		Object value = null;
		for (Iterator i = keySet.iterator(); i.hasNext(); ) {
			key = (String) i.next();
			value = serviceResults.get(key);
			Debug.log("Mapping Service Results to JXOutput: key = " + key + " value = " + value, module);
			this.props.put(key, value);
			
			if(this.serviceOutputMap.containsKey(key)) {
			    String outputName = (String) this.serviceOutputMap.get(key);
			    Debug.log("Mapping service parameter = " + key + " to JXOutput name = " + outputName, module);
			    this.props.put(outputName, value);
			    
			} 
			    
		}
	}
	
	/**
	 * @param element
	 * @return Map of name value pairs to pass into an OFBiz service
	 */
	private Map buildInputMap(Element element) throws Exception {
		
		HashMap map = new HashMap();
		
		List list = element.selectNodes("attribute");
		
		Iterator iter = list.iterator();
		
		String name = null;
		String ioMode = null;
		Object value = null;
		String instruction = null;
		String mapOutputName = null;
		String mapInput = null;
		
		while(iter.hasNext()) {
			Element field = (Element) iter.next();
			name = field.attributeValue("name");
			ioMode = field.attributeValue("mode");
			//instruction = field.attributeValue("instruction");
			//if( (instruction != null) && ("auto".equals(instruction))) {
			//	value = genericDelegator.getNextSeqId(SEQUENCE_NAME).toString();
			//} else {
				value = getDataType(field);
			//}
			//Check if value is set in input map
			mapInput = field.attributeValue(JXConstants.MAP_INPUT_NAME);
			if(mapInput != null ){
				//Auto set the value from the JXProperties environment.
				//value = (String) this.props.getString(mapInput);
				value = this.props.get(mapInput);
				//todo - not needed for service map parameter field.setText(value);
			}
			
			//Map input parameters to the service map
			if( (ioMode == null) || (ioMode.equalsIgnoreCase("INOUT")) ) {
			    Debug.log("service map: " + name + "=" + value, module);
			    map.put(name, value);
			    
				//If mapName is set then save element value in the JXProperties 
				//for other tests that may need the value.
				mapOutputName = field.attributeValue(JXConstants.MAP_OUTPUT_NAME);
				if(mapOutputName != null) {
					this.props.put(mapOutputName, map.get(name));
				}			    
				
			//Preparation for mapping the output of a service parameter to a JXProps map for use in other tests.
			} else  if( (ioMode != null) && (ioMode.equalsIgnoreCase("OUT")) ) {
			    //e.g. serviceParameter_accountId, "myCheckingAcct"
			    mapOutputName = field.attributeValue(JXConstants.MAP_OUTPUT_NAME);
			    if(mapOutputName != null) {
			        Debug.log("output parameter map: " + name + "=" + mapOutputName, module);
			        this.serviceOutputMap.put(name, mapOutputName );  
			    }
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
	private Object getDataType(Element element) throws Exception {
		
		Object object = null;
		
		String type = element.attributeValue("type");
		String instruction = element.attributeValue("instruction");
		
		
		if(type.equalsIgnoreCase("String")) {
			if( (instruction != null) && (instruction.equals("auto"))) {
				object = genericDelegator.getNextSeqId(SEQUENCE_NAME).toString();
			} else {
				object = element.getTextTrim();
			}
		} else if(type.equalsIgnoreCase("Timestamp")) {
			if( (instruction != null) && (instruction.equals("currentdate"))) {
				object = UtilDateTime.nowTimestamp();
			} else {
				object = java.sql.Timestamp.valueOf((String) element.getTextTrim());
			}
		} else if(type.equalsIgnoreCase("Date")) {
			if( (instruction != null) && (instruction.equals("currentdate"))) {
				object = UtilDateTime.nowDate();
			} else {
				object = java.sql.Date.valueOf((String) element.getTextTrim());
			}		
		} else if(type.equalsIgnoreCase("Numeric")) {
			if( (instruction != null) && (instruction.equals("auto"))) {
			   object = genericDelegator.getNextSeqId(SEQUENCE_NAME);
			} else {
				object = Long.valueOf((String) element.getTextTrim());
			}
		} else if(type.equalsIgnoreCase("currency-amount")) {
			object = Double.valueOf((String) element.getTextTrim());
		} else if(type.equalsIgnoreCase("floating-point")) {
			object = Double.valueOf((String) element.getTextTrim());
		// Default is a String object
		} else {
			
			object =  Class.forName(type).newInstance();
			
			/*
			if( (instruction != null) && (instruction.equals("auto"))) {
				object = genericDelegator.getNextSeqId(SEQUENCE_NAME).toString();
			} else {
				object = element.getTextTrim();
			}
			*/
			
		}
		
		return object;

	}

	
	private void getDispatcher(){
	
		this.genericDelegator = GenericDelegator.getGenericDelegator(this.delegator);
		
		this.genericDispatcher = new GenericDispatcher(this.dispatcher,this.genericDelegator);
			
	}
	
	private GenericValue getLoginObject(String userName) {
	 
	    GenericValue userLogin = null;
	    
	    if(userName == null) {
	        return userLogin; 
	    }
	    
	    try {
            List list = this.genericDelegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", userName));
            if(list != null) {
                userLogin = (GenericValue) list.get(0);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting LoginObject for user: " + userName, module);
        }
	    
	    return userLogin;
	}
}