/*
 * $Id: SearchServices.java,v 1.6 2004/08/12 18:05:13 byersa Exp $
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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.InterruptedException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;



/**
 * SearchServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a> Hacked from Lucene demo file
 * @version $Revision: 1.6 $
 * @since 3.1
 * 
 *  
 */
public class SearchServices {

    public static final String module = SearchServices.class.getName();
	
    public static Map indexTree(DispatchContext dctx, Map context) {

        String siteId = (String)context.get("contentId");
        String path = (String)context.get("path");
        Map envContext = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
  	if (Debug.infoOn()) Debug.logInfo("in indexTree, siteId:" + siteId, module);
            List badIndexList = new ArrayList();
            envContext.put("badIndexList", badIndexList);
            envContext.put("goodIndexCount", new Integer(0));

        Map results = null;
        try {
            results = SearchWorker.indexTree(delegator, siteId, envContext, path);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error indexing tree: " + e.toString());
        } catch (IOException e2) {
            Debug.logError(e2, module);
            return ServiceUtil.returnError("Error indexing tree: " + e2.toString());
        } catch (InterruptedException e3) {
            Debug.logError(e3, module);
            return ServiceUtil.returnError("Error indexing tree: " + e3.toString());
        }
	  	if (Debug.infoOn()) Debug.logInfo("in indexTree, results:" + results, module);
        return results;
    }
}
