/*
 * JXUserHttpSession.java
 *
 * @author	Brett G. Palmer
 * Created: 1/20/2004/
 */
package org.ofbiz.jxtest;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Assert;
import net.sourceforge.jxunit.JXGeneric;
import net.sourceforge.jxunit.JXProperties;
import net.sourceforge.jxunit.JXTestCase;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 *
 */
public class JXGenericWebRequest extends JXGeneric {

	public static final String module = JXGenericWebRequest.class.getName();
	private Document inputdoc;
	
	
	private HashMap sessionMap;
	
	private ArrayList requestList;
	

	private JXProperties props;
	
    /**
     * Constructor
     */
    public JXGenericWebRequest() {

		this.sessionMap = new HashMap();
		this.requestList = new ArrayList();
		
    }

    public void eval(JXTestCase testCase) throws Throwable	{
    	
		this.props = testCase.getProperties();
		
        String fullPath = null;
        
        if(dir == null) {
        	fullPath = 	testCase.getTestDirectory() + File.separator + parent.input;
        } else {
        	fullPath = 	dir + File.separator + parent.input;
        }

		Debug.log("Using the following input file: " + fullPath);
        readInputFile(fullPath);
        
        readWebConfig();
        
        executeTest();
        
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
		inputdoc = reader.read(url);
		
	}



    private void readWebConfig() throws Exception {
    	
    	
		//Element element = (Element) inputdoc.selectSingleNode("/jxuConfig/webRequest");
		List list = inputdoc.selectNodes("/jxuConfig/webRequest");
		
		Iterator iter = list.iterator();
		
		while(iter.hasNext()) {
			Element requestElem = (Element) iter.next();
			
			String webSession = requestElem.attributeValue("session");			

			//Test if session/conversation has already been created for request			
			if( this.sessionMap.get(webSession) == null ) {
				WebConversation conversation = new WebConversation();
				this.sessionMap.put(webSession, conversation);
			} 
				
			WebRequestHelper request = new WebRequestHelper(requestElem);
			this.requestList.add(request);
			
		}
		
    }

    private void executeTest() throws Exception {


		Iterator iter = this.requestList.iterator();

		while(iter.hasNext() ) {

			WebRequestHelper helper = (WebRequestHelper) iter.next();

			WebConversation conversation = (WebConversation) this.sessionMap.get(helper.getSessionName());

			WebRequest request = helper.buildHttpRequest();

			WebResponse resp = conversation.getResponse( request );
			
			//Debug.log("Response from server: " + resp.getText());
			int index = resp.getText().indexOf( helper.getExpectedResponse() );

			if(index != -1) {
				Debug.log("Web Request was successful");
			} else {
				Assert.fail("Failed to get response: " + helper.getExpectedResponse());
			}

		}
    

    }
    
    
    /* 
     * Helper class to capture a WebRequest test
     * 
     * @author bgpalmer
     *
     */
    class WebRequestHelper {
    
    	
/*		<webRequest session="login" host="localhost" port="8080" context="ecommerce" path="control/login">
			<httpParam name="user.name">ofbiz</httpParam>
			<httpParam name="user.password">admin</httpParam>
			<webResponse>Welcome</webResponse>
		</webRequest>
*/
		private ArrayList httpParamNames;
		private ArrayList httpParamValues;
		private String response;
		private String sessionName;

		//Server Request Configuration parameters
		private String host;
		private String port;
		private String context;
		private String path;
		
    	WebRequestHelper(Element webRequest) {
    		
    		this.httpParamNames = new ArrayList();
    		this.httpParamValues = new ArrayList();
 
			//Get server configuration settings
  			this.sessionName = webRequest.attributeValue("session");
  			this.host = webRequest.attributeValue("host");
  			this.port = webRequest.attributeValue("port");
  			this.context = webRequest.attributeValue("context");
  			this.path = webRequest.attributeValue("path");

			Element respElem = (Element) webRequest.selectSingleNode("webResponse");
			this.response = respElem.getText();
			
			List paramList = webRequest.selectNodes("httpParam");
			
			processNameValues(paramList);
			
    	}
    	
		private void processNameValues(List list) {
			
			Iterator iter = list.iterator();
			while(iter.hasNext()) {
				Element param = (Element) iter.next();
				//Getting the name/value pairs for web request
				httpParamNames.add( param.attributeValue("name"));
				httpParamValues.add( param.getText());
			}
			
		}

		public WebRequest buildHttpRequest() {
			String requestStr = "http://" + host + ":" + port + "/" + context + "/" + path;
			Debug.log("Building WebRequest: " + requestStr);
		
			WebRequest request = new GetMethodWebRequest( requestStr );

			Iterator iter = this.httpParamNames.iterator();
			for(int i=0; iter.hasNext(); iter.next(), i++ ) {
				String name = (String) this.httpParamNames.get(i);
				String value = (String) this.httpParamValues.get(i);
				Debug.log("Setting Http name/value: " + name  + "/" + value);
				request.setParameter( name, value);
			}

			return request;			
		}

		public String getSessionName() {
			return this.sessionName;
		}

		public String getExpectedResponse() {
			return this.response;
		}
    	
    }

}

