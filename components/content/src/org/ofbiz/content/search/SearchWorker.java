/*
 * $Id: SearchWorker.java,v 1.1 2004/05/14 20:31:14 byersa Exp $
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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileNotFoundException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;

import org.apache.lucene.search.Hits;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.Debug;



/**
 * SearchWorker Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a> Hacked from Lucene demo file
 * @version $Revision: 1.1 $
 * @since 3.1
 * 
 *  
 */
public class SearchWorker {
	public static final String module = SearchWorker.class.getName();
	
	public static void indexContentList(List idList, GenericDelegator delegator, Map context) throws Exception {
		String path = null;
		indexContentList(idList, path, delegator, context);
	}
	
	public static void indexContentList(List idList, String path, GenericDelegator delegator, Map context) throws Exception {
		String indexAllPath = getIndexPath(path);
		GenericValue content = null;
		
		// Delete existing documents
		IndexReader reader = IndexReader.open(indexAllPath);
		if (Debug.infoOn()) Debug.logInfo("in indexContent, reader:" + reader, module);
		List contentList = new ArrayList();
		Iterator iter = idList.iterator();
		while (iter.hasNext()) {
			String id = (String)iter.next();
		  	if (Debug.infoOn()) Debug.logInfo("in indexContent, id:" + id, module);
			try {
		  		content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId",id));
		  		contentList.add(content);
		  	} catch(GenericEntityException e) {
		  		Debug.logError(e, module);
		  		return;
		  	}
			deleteContent(id, path, reader);
		}
		reader.close();
		
		// Now create
	  	IndexWriter writer =  null;
	  	try {
	  	    writer =  new IndexWriter(indexAllPath, new StandardAnalyzer(), false);
	    } catch(FileNotFoundException e) {
		    writer = new IndexWriter(indexAllPath, new StandardAnalyzer(), true);
		}
	  	if (Debug.infoOn()) Debug.logInfo("in indexContent, writer:" + writer, module);
		
		iter = contentList.iterator();
		while (iter.hasNext()) {
			content = (GenericValue)iter.next();
			indexContent(content, path, delegator, context, writer);
		}
		writer.optimize();
		writer.close();
    }
	
	
	public static void deleteContent(String contentId, String path, IndexReader reader) throws Exception {
	    if (Debug.infoOn()) Debug.logInfo("in indexContent, path:" + path, module);
	    String indexAllPath = null;
	    if (reader == null) {
		    indexAllPath = getIndexPath(path);
	    }
		boolean validReader = true;
		if (reader == null) {
			reader = IndexReader.open(indexAllPath);
			validReader = false;
		}
		Term term = new Term("contentId", contentId);
	    if (Debug.infoOn()) Debug.logInfo("in indexContent, term:" + term, module);
		int qtyDeleted = reader.delete(term);
		if (Debug.infoOn()) Debug.logInfo("in indexContent, qtyDeleted:" + term, module);
		if (!validReader) {
        	reader.close();
        }

        return;
	}
	
	public static void indexContent(GenericValue content, String path, GenericDelegator delegator, Map context, IndexWriter writer) throws Exception {
	    if (Debug.infoOn()) Debug.logInfo("in indexContent, path:" + path, module);
		String indexAllPath = getIndexPath(path);
		boolean validWriter = true;
		if (writer == null) {
			try {
		    	writer = new IndexWriter(indexAllPath, new StandardAnalyzer(), false);
			} catch(FileNotFoundException e) {
		    	writer = new IndexWriter(indexAllPath, new StandardAnalyzer(), true);
			}
			validWriter = false;
		}
		String contentId = content.getString("contentId");
	    Document doc = ContentDocument.Document(content);
	    if (Debug.infoOn()) Debug.logInfo("in indexContent, content:" + content, module);
        writer.addDocument(doc);
        String dataResourceId = content.getString("dataResourceId");
        if (UtilValidate.isNotEmpty(dataResourceId)) {
            indexDataResource(dataResourceId, path, delegator, context, writer);
        }
        
        if (!validWriter) {
        	writer.optimize();
        	writer.close();
        }
        return;
	}
	
	public static void indexDataResource(String id, GenericDelegator delegator, Map context) throws Exception {
		indexDataResource(id, null, delegator, context, null);
	}
	
	public static void indexDataResource(String id, GenericDelegator delegator, Map context, IndexWriter writer) throws Exception {
		indexDataResource(id, null, delegator, context, writer);
	}
	
	public static void indexDataResource(String id, String path, GenericDelegator delegator, Map context, IndexWriter writer) throws Exception {
		String indexAllPath = getIndexPath(path);
		boolean validWriter = true;
	    if (writer == null) {
			try {
			    writer = new IndexWriter(indexAllPath, new StandardAnalyzer(), false);
			} catch(FileNotFoundException e) {
			    writer = new IndexWriter(indexAllPath, new StandardAnalyzer(), true);
			}	
			validWriter = false;
		}
	    Document doc = DataResourceDocument.Document(id, delegator, context);
	    writer.addDocument(doc);
	    if (!validWriter) {
		  writer.optimize();
	      writer.close();
	    }

	}
	
	public static String getIndexPath(String path) {
		String indexAllPath = path;
		if (UtilValidate.isEmpty(indexAllPath))
			indexAllPath = UtilProperties.getPropertyValue("search", "defaultIndex");
		if (UtilValidate.isEmpty(indexAllPath))
			indexAllPath = "index";
		return indexAllPath;

	}
}
