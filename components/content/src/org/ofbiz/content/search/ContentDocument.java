/*
 * $Id: ContentDocument.java,v 1.5 2004/07/02 20:18:22 byersa Exp $
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
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.base.util.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Iterator;

/**
 * ContentDocument Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.5 $
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
	  	
                Map map = new HashMap();
	  	doc = Document(content, map);
                return doc;
	}
	
	public static Document Document(GenericValue content, Map context) throws InterruptedException  {
	  	
		Document doc = null;
	  	// make a new, empty document
	  	doc = new Document();
	  	
	  	String contentId = content.getString("contentId");
	  	doc.add(Field.Keyword("contentId", contentId));
	    
	    // Add the last modified date of the file a field named "modified".  Use a
	    // Keyword field, so that it's searchable, but so that no attempt is made
	    // to tokenize the field into words.
                Timestamp modDate = (Timestamp)content.get("lastModifiedDate");
                if (modDate == null) {
                    modDate = (Timestamp)content.get("createdDate");
                }

                if (modDate != null) {
	  	    doc.add(Field.Keyword("modified", modDate.toString()));
                }
	  	
                String contentName = content.getString("contentName");
                if (UtilValidate.isNotEmpty(contentName) )
	  	    doc.add(Field.Text("title", contentName));
	    
                String description = content.getString("description");
                if (UtilValidate.isNotEmpty(description) )
	  	    doc.add(Field.Text("description", description));
	    
                List ancestorList = new ArrayList();
                GenericDelegator delegator = content.getDelegator();
                ContentWorker.getContentAncestryAll(delegator, contentId, "WEB_SITE_PUB_PT", "TO", ancestorList);
                String ancestorString = StringUtil.join(ancestorList, " ");
	  	Debug.logInfo("in ContentDocument, ancestorString:" + ancestorString, module);
                if (UtilValidate.isNotEmpty(ancestorString) ) {
                    Field field = Field.UnStored("site", ancestorString);
	  	    Debug.logInfo("in ContentDocument, field:" + field.stringValue(), module);
	  	    doc.add(field);
                }

                indexDataResource(content, doc, context);

	  	return doc;
	}

	public static void indexDataResource(GenericValue content, Document doc, Map context) {
	  	
            GenericDelegator delegator = content.getDelegator();
            String dataResourceId = content.getString("dataResourceId");
            GenericValue dataResource = null;
	    try {
	  	dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
	    } catch(GenericEntityException e) {
	  	Debug.logError(e, module);
	  	return ;
	    }
	  	
	  	
	    String mimeTypeId = dataResource.getString("mimeTypeId");
	    if (UtilValidate.isEmpty(mimeTypeId)) {
                mimeTypeId = "text/html";
            }

	    Locale locale = Locale.getDefault();
            String currentLocaleString = dataResource.getString("localeString");
            if (UtilValidate.isNotEmpty(currentLocaleString)) {
                locale = UtilMisc.parseLocale(currentLocaleString);
            }
        
            StringWriter outWriter = new StringWriter();
	    try {
	  	    DataResourceWorker.writeDataResourceTextCache(dataResource, mimeTypeId, locale, context, delegator, outWriter);
	    } catch(GeneralException e) {
	  	Debug.logError(e, module);
                return;
	    } catch(IOException e) {
	  	Debug.logError(e, module);
                return;
	    }
	    String text = outWriter.toString();
	    //Debug.logInfo("in DataResourceDocument, text:" + text, module);
            if (UtilValidate.isNotEmpty(text)) {
              Field field = Field.UnStored("content", text);
	      //Debug.logInfo("in ContentDocument, field:" + field.stringValue(), module);
	      doc.add(field);
            }

            List featureDataResourceList = null;
            try {
                featureDataResourceList = content.getRelatedCache("ProductFeatureDataResource");
	    } catch(GenericEntityException e) {
	  	Debug.logError(e, module);
                return;
	    }
            List featureList = new ArrayList();
            Iterator iter = featureDataResourceList.iterator(); 
            while (iter.hasNext()) {
                GenericValue productFeatureDataResource = (GenericValue)iter.next();
                String feature = productFeatureDataResource.getString("productFeatureId");
                featureList.add(feature); 
            }
            String featureString = StringUtil.join(featureList, " ");
            Debug.logInfo("in ContentDocument, featureString:" + featureString, module);
            if (UtilValidate.isNotEmpty(featureString) ) {
                Field field = Field.UnStored("feature", featureString);
	  	doc.add(field);
            }
	    
	    return;
	}
}
