/*
 * $Id: ContentDocument.java,v 1.1 2004/05/14 20:31:13 byersa Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.search;

import org.apache.lucene.document.*;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * ContentDocument Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.1
 * 
 *  
 */

public class ContentDocument {
	static char dirSep = System.getProperty("file.separator").charAt(0);
    public static final String module = ContentDocument.class.getName();
	
	public static Document Document(String id, GenericDelegator delegator) throws InterruptedException  {
	  	
		Document doc = null;
		GenericValue content = null;
	  	try {
	  		content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId",id));
	  	} catch(GenericEntityException e) {
	  		Debug.logError(e, module);
	  		return doc;
	  	}
	  	
	  	doc = Document(content);
	  		  	return doc;
	}
	
	public static Document Document(GenericValue content) throws InterruptedException  {
	  	
		Document doc = null;
	  	// make a new, empty document
	  	doc = new Document();
	  	
	  	doc.add(Field.Keyword("contentId", content.getString("contentId")));
	    
	    // Add the last modified date of the file a field named "modified".  Use a
	    // Keyword field, so that it's searchable, but so that no attempt is made
	    // to tokenize the field into words.
	  	doc.add(Field.Keyword("modified", content.get("lastModifiedDate").toString()));
	  	
	  	doc.add(Field.Text("title", content.get("contentName").toString()));
	    
	  	doc.add(Field.Text("description", content.get("description").toString()));
	    

	  	return doc;
	}

}
