/*
 * $Id: SearchServices.java,v 1.3 2004/08/09 23:52:20 jonesde Exp $
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

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;



/**
 * SearchServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a> Hacked from Lucene demo file
 * @version $Revision: 1.3 $
 * @since 3.1
 * 
 *  
 */
public class SearchServices {

    public static final String module = SearchServices.class.getName();
	
    public static Map indexTree(DispatchContext dctx, Map context) {

        String siteId = (String)context.get("contentId");
        String path = (String)context.get("path");
        Map envContext = (Map)context.get("context");
        GenericDelegator delegator = dctx.getDelegator();
	  	if (Debug.infoOn()) Debug.logInfo("in indexTree, siteId:" + siteId, module);

        Map results = null;
        try {
            results = SearchWorker.indexTree(delegator, siteId, envContext, path);
        } catch (Exception e) {
            return ServiceUtil.returnError("Error indexing tree: " + e.toString());
        }
        return results;
    }
}
