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

import java.util.Iterator;
import java.util.*;

import org.ofbiz.base.util.Debug;

import net.sourceforge.jxunit.JXProperties;
import net.sourceforge.jxunit.JXTestCase;
import net.sourceforge.jxunit.JXTestStep;

import bsh.*;



/**
 * JXOfbEntity is a JXUnit test step element to create funtional tests for
 * the OFBiz framework.
 *
 * @author     <a href="mailto:bgpalmer@computer.org">Brett G. Palmer</a>
 * @version    $Revision: 1.1 $
 * @since      1.0
 */
public class JXOfbBshEngine implements JXTestStep
{
	
	public String script="";
	public String input="";
	public String output="";
	public String dir="";
	public String message="";
	
	private JXProperties props;

	public static final String module = JXOfbBshEngine.class.getName();

	public void eval(JXTestCase testCase) throws Throwable	{
		
		this.props=testCase.getProperties();
		
		JXComponentLoader.loadComponents(null);
		
		//Copy parameter maps into bash script environment
		HashMap context = new HashMap();
		Iterator iterKeys = props.keySet().iterator();
		while(iterKeys.hasNext()) {
			Object obj = iterKeys.next();
			String value = props.getString((String) obj);
			Debug.logVerbose("property key=" + obj + " value=" + value, module);
			context.put(obj, value);
			
		}
		
		String pwd = (String) props.getString("testDirectory");


        Interpreter bsh = new Interpreter();

		//HashMap context = new HashMap();
		context.put("jxprops", this.props);
        bsh.set("context", context); // set the parameter context used for both IN and OUT
        
		Debug.log("Calling BSH script: " + script);
        bsh.source(pwd + "/" + script);
        
        Map bshResults = (Map) bsh.get("context");


		String results = (String) bshResults.get(JXConstants.TEST_RESULTS);
		
		if(JXConstants.TEST_SUCCESS.equals(results)) {
			Debug.log("Script: " + script + " completed successfully", module);        
		} else if(JXConstants.TEST_FAIL.equals(results)) {
			String errMsg = (String) bshResults.get(JXConstants.ERROR_MESSAGE);
			Debug.logError("Script: " + script + " failed", module);
			Debug.logError("Error msg: " + errMsg, module);
		} else {
			Debug.log("Script: " + script + " completed", module);
		}

	}
	

}